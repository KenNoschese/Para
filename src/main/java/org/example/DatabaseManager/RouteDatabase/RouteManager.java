package org.example.DatabaseManager.RouteDatabase;

import org.example.DatabaseManager.DatabaseInstance;
import org.example.DatabaseManager.RouteDatabase.ObserversClasses.JeepneyObserver;
import org.example.DatabaseManager.RouteDatabase.ObserversClasses.JeepneySubject;
import org.example.gui.resources.RouteData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

/**
 * RouteManager serves as a high-level interface to:
 *  - Fetch routes between locations
 *  - Handle fare and ETA computation
 *  - Manage Jeepney observer updates for passenger boarding/leaving
 */
public class RouteManager {
    private final Connection con;
    private final JeepneySubject jeepneySubject;

    public RouteManager() throws SQLException {
        this.con = DatabaseInstance.getInstance().getConnection();
        this.jeepneySubject = new JeepneySubject();
    }

    public void addJeepneyObserver(JeepneyObserver observer) {
        if (observer != null) {
            jeepneySubject.registerObserver(observer);
        }
    }

    public void boardJeepney(String plateNumber) throws SQLException {
        jeepneySubject.boardJeepney(plateNumber);
    }

    public void leaveJeepney(String plateNumber) throws SQLException {
        jeepneySubject.leaveJeepney(plateNumber);
    }

    public void showAllJeepneys() throws SQLException {
        jeepneySubject.showAllJeepneys();
    }

    public void close() throws SQLException {
        if (con != null && !con.isClosed()) {
            con.close();
        }
    }

    public ArrayList<RouteData> findRoutes(String from, String to, String category) throws SQLException {
        ArrayList<RouteData> routes = new ArrayList<>();
        boolean isInbound = detectDirection(from, to);

        String sql = """
            SELECT 
                r.route_id,
                r.route_name,
                rs_from.stop_order AS from_order,
                rs_to.stop_order AS to_order,
                ABS(rs_to.stop_order - rs_from.stop_order) AS distance_km,
                r.base_fare,
                r.base_distance_km,
                r.per_km_rate,
                (
                    SELECT COUNT(*) 
                    FROM Route_Stops rs_mid
                    WHERE rs_mid.route_id = r.route_id
                      AND rs_mid.stop_order BETWEEN 
                          LEAST(rs_from.stop_order, rs_to.stop_order)
                          AND GREATEST(rs_from.stop_order, rs_to.stop_order)
                ) AS stops
            FROM Routes r
            JOIN Route_Stops rs_from ON r.route_id = rs_from.route_id
            JOIN Stops s_from ON rs_from.stop_id = s_from.stop_id
            JOIN Route_Stops rs_to ON r.route_id = rs_to.route_id
            JOIN Stops s_to ON rs_to.stop_id = s_to.stop_id
            WHERE s_from.stop_name = ?
              AND s_to.stop_name = ?
              AND (
                  ( ? = TRUE  AND rs_to.stop_order > rs_from.stop_order )
                  OR
                  ( ? = FALSE AND rs_to.stop_order < rs_from.stop_order )
              )
            GROUP BY r.route_id, r.route_name, rs_from.stop_order, rs_to.stop_order;
        """;

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, from);
            pst.setString(2, to);
            pst.setBoolean(3, isInbound);
            pst.setBoolean(4, isInbound);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int routeId = rs.getInt("route_id");
                    String routeName = rs.getString("route_name");
                    double distanceKm = rs.getDouble("distance_km");
                    int stops = rs.getInt("stops");

                    double baseFare = rs.getDouble("base_fare");
                    double baseDist = rs.getDouble("base_distance_km");
                    double perKmRate = rs.getDouble("per_km_rate");

                    double fare = baseFare;
                    if (distanceKm > baseDist) {
                        fare += (distanceKm - baseDist) * perKmRate;
                    }
                    if (fare < 12.0) fare = 12.0;

                    double discount = getDiscountRate(category);
                    double finalFare = fare * (1 - discount);

                    int eta = (int) Math.ceil(distanceKm * 3);

                    ArrayList<String> stopNames = getAllStopsForRoute(routeId, from, to);

                    RouteData data = new RouteData();
                    data.setRoute(routeName);
                    data.setFromLocation(from);
                    data.setDestination(to);
                    data.setStops(stops);
                    data.setETA(eta);
                    data.setDistance((int) distanceKm);
                    data.setFare(finalFare);
                    data.setDetails(category);
                    data.setRoute_stops(stopNames);

                    routes.add(data);
                }
            }
        }

        return routes;
    }

    private boolean detectDirection(String from, String to) {
        String dirQuery = """
            SELECT rs_from.stop_order AS from_order, rs_to.stop_order AS to_order
            FROM Route_Stops rs_from
            JOIN Stops s_from ON rs_from.stop_id = s_from.stop_id
            JOIN Route_Stops rs_to ON rs_from.route_id = rs_to.route_id
            JOIN Stops s_to ON rs_to.stop_id = s_to.stop_id
            WHERE s_from.stop_name = ? AND s_to.stop_name = ?
            LIMIT 1;
        """;

        try (PreparedStatement pst = con.prepareStatement(dirQuery)) {
            pst.setString(1, from);
            pst.setString(2, to);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("from_order") < rs.getInt("to_order");
            }
        } catch (SQLException e) {
            System.out.println("[WARN] Direction detection failed: " + e.getMessage());
        }
        return true;
    }

    private ArrayList<String> getAllStopsForRoute(int routeId, String from, String to) throws SQLException {
        ArrayList<String> stops = new ArrayList<>();

        String stopQuery = """
            SELECT s.stop_name
            FROM Route_Stops rs
            JOIN Stops s ON rs.stop_id = s.stop_id
            WHERE rs.route_id = ?
              AND rs.stop_order BETWEEN 
                  (SELECT rsf.stop_order 
                   FROM Route_Stops rsf 
                   JOIN Stops sf ON rsf.stop_id = sf.stop_id 
                   WHERE rsf.route_id = ? AND sf.stop_name = ? LIMIT 1)
              AND 
                  (SELECT rst.stop_order 
                   FROM Route_Stops rst 
                   JOIN Stops st ON rst.stop_id = st.stop_id 
                   WHERE rst.route_id = ? AND st.stop_name = ? LIMIT 1)
            ORDER BY rs.stop_order;
        """;

        try (PreparedStatement pst = con.prepareStatement(stopQuery)) {
            pst.setInt(1, routeId);
            pst.setInt(2, routeId);
            pst.setString(3, from);
            pst.setInt(4, routeId);
            pst.setString(5, to);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    stops.add(rs.getString("stop_name"));
                }
            }
        }
        return stops;
    }

    private double getDiscountRate(String category) {
        if (category == null) return 0.0;
        return switch (category.toLowerCase()) {
            case "student", "pwd", "senior citizen" -> 0.20;
            default -> 0.0;
        };
    }

    public ArrayList<ArrayList<RouteData>> findRoutesWithTransfers(String from, String to, String category) throws SQLException {
        ArrayList<ArrayList<RouteData>> allRoutes = new ArrayList<>();

        // 1. Find direct routes
        ArrayList<RouteData> directRoutes = findRoutes(from, to, category);
        allRoutes.addAll(directRoutes.stream().map(route -> {
            ArrayList<RouteData> singleRoute = new ArrayList<>();
            singleRoute.add(route);
            return singleRoute;
        }).toList());

        // 2. Find routes with one transfer
        String sql = """
            SELECT DISTINCT
                r1.route_id AS from_route_id,
                r1.route_name AS from_route_name,
                r2.route_id AS to_route_id,
                r2.route_name AS to_route_name,
                s_transfer.stop_name AS transfer_stop,
                rs1_from.stop_order AS from_order,
                rs1_transfer.stop_order AS transfer_from_order,
                rs2_transfer.stop_order AS transfer_to_order,
                rs2_to.stop_order AS to_order,
                r1.base_fare AS from_base_fare,
                r1.base_distance_km AS from_base_distance_km,
                r1.per_km_rate AS from_per_km_rate,
                r2.base_fare AS to_base_fare,
                r2.base_distance_km AS to_base_distance_km,
                r2.per_km_rate AS to_per_km_rate
            FROM Transfer_Points tp
            JOIN Stops s_transfer ON tp.stop_id = s_transfer.stop_id
            JOIN Routes r1 ON tp.from_route_id = r1.route_id
            JOIN Routes r2 ON tp.to_route_id = r2.route_id
            JOIN Route_Stops rs1_from ON rs1_from.route_id = r1.route_id
            JOIN Stops s_from ON rs1_from.stop_id = s_from.stop_id
            JOIN Route_Stops rs1_transfer ON rs1_transfer.route_id = r1.route_id AND rs1_transfer.stop_id = s_transfer.stop_id
            JOIN Route_Stops rs2_transfer ON rs2_transfer.route_id = r2.route_id AND rs2_transfer.stop_id = s_transfer.stop_id
            JOIN Route_Stops rs2_to ON rs2_to.route_id = r2.route_id
            JOIN Stops s_to ON rs2_to.stop_id = s_to.stop_id
            WHERE s_from.stop_name = ? AND s_to.stop_name = ?
            ORDER BY r1.route_name, r2.route_name;
        """;

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, from);
            pst.setString(2, to);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    // First segment: from -> transfer_stop
                    String fromRouteName = rs.getString("from_route_name");
                    String transferStop = rs.getString("transfer_stop");
                    int fromRouteId = rs.getInt("from_route_id");
                    double fromDistanceKm = Math.abs(rs.getDouble("transfer_from_order") - rs.getDouble("from_order"));
                    double fromBaseFare = rs.getDouble("from_base_fare");
                    double fromBaseDist = rs.getDouble("from_base_distance_km");
                    double fromPerKmRate = rs.getDouble("from_per_km_rate");

                    double fromFare = fromBaseFare;
                    if (fromDistanceKm > fromBaseDist) {
                        fromFare += (fromDistanceKm - fromBaseDist) * fromPerKmRate;
                    }
                    if (fromFare < 12.0) fromFare = 12.0;
                    double discount = getDiscountRate(category);
                    fromFare *= (1 - discount);

                    RouteData fromRoute = new RouteData();
                    fromRoute.setRoute(fromRouteName);
                    fromRoute.setFromLocation(from);
                    fromRoute.setDestination(transferStop);
                    fromRoute.setDistance((int) fromDistanceKm);
                    fromRoute.setFare(fromFare);
                    fromRoute.setDetails(category);
                    fromRoute.setRoute_stops(getAllStopsForRoute(fromRouteId, from, transferStop));

                    // Second segment: transfer_stop -> to
                    String toRouteName = rs.getString("to_route_name");
                    int toRouteId = rs.getInt("to_route_id");
                    double toDistanceKm = Math.abs(rs.getDouble("to_order") - rs.getDouble("transfer_to_order"));
                    double toBaseFare = rs.getDouble("to_base_fare");
                    double toBaseDist = rs.getDouble("to_base_distance_km");
                    double toPerKmRate = rs.getDouble("to_per_km_rate");

                    double toFare = toBaseFare;
                    if (toDistanceKm > toBaseDist) {
                        toFare += (toDistanceKm - toBaseDist) * toPerKmRate;
                    }
                    if (toFare < 12.0) toFare = 12.0;
                    toFare *= (1 - discount);

                    RouteData toRoute = new RouteData();
                    toRoute.setRoute(toRouteName);
                    toRoute.setFromLocation(transferStop);
                    toRoute.setDestination(to);
                    toRoute.setDistance((int) toDistanceKm);
                    toRoute.setFare(toFare);
                    toRoute.setDetails(category);
                    toRoute.setRoute_stops(getAllStopsForRoute(toRouteId, transferStop, to));

                    // Combine into a single route with transfer
                    ArrayList<RouteData> transferRoute = new ArrayList<>();
                    transferRoute.add(fromRoute);
                    transferRoute.add(toRoute);
                    allRoutes.add(transferRoute);
                }
            }
        }

        return allRoutes;
    }

    // Updated main method to print only the route with the least transfers. DEBUG ONLY.
    public static void main(String[] args) {
        RouteManager routeManager = null;
        try {
            // Initialize RouteManager
            routeManager = new RouteManager();

            String from = "bangkal";
            String to = "gmall bajada";
            String category = "Regular"; // No discount for simplicity

            System.out.println("=== Route Debug: Bangkal to GMall Bajada (Least Transfers) ===");
            System.out.println("Passenger Category: " + category);

            // Find all routes (direct and with transfers)
            ArrayList<ArrayList<RouteData>> allRoutes = routeManager.findRoutesWithTransfers(from, to, category);

            if (allRoutes.isEmpty()) {
                System.out.println("No routes found from " + from + " to " + to);
                return;
            }

            // Select the route with the least transfers
            Optional<ArrayList<RouteData>> bestRoute = allRoutes.stream()
                    .min((r1, r2) -> Integer.compare(r1.size(), r2.size()));

            if (bestRoute.isEmpty()) {
                System.out.println("No valid route found.");
                return;
            }

            ArrayList<RouteData> route = bestRoute.get();
            System.out.println("\nBest Route (Least Transfers):");
            double totalFare = 0.0;
            double totalDistance = 0.0;

            if (route.size() == 1) {
                // Direct route
                RouteData directRoute = route.get(0);
                System.out.printf("  Direct Route: %s%n", directRoute.getRoute());
                System.out.printf("  Stops: %s%n", String.join(" -> ", directRoute.getRoute_stops()));
                System.out.printf("  Fare: ₱%.2f%n", directRoute.getFare());
                System.out.printf("  Distance: %d km%n", directRoute.getDistance());
                totalFare = directRoute.getFare();
                totalDistance = directRoute.getDistance();
            } else {
                // Transfer route
                System.out.println("  Transfer Route:");
                for (int i = 0; i < route.size(); i++) {
                    RouteData segment = route.get(i);
                    System.out.printf("    Segment %d: %s (from %s to %s)%n",
                            i + 1, segment.getRoute(), segment.getFromLocation(), segment.getDestination());
                    System.out.printf("      Stops: %s%n", String.join(" -> ", segment.getRoute_stops()));
                    System.out.printf("      Fare: ₱%.2f%n", segment.getFare());
                    System.out.printf("      Distance: %d km%n", segment.getDistance());
                    totalFare += segment.getFare();
                    totalDistance += segment.getDistance();
                }
            }

            System.out.printf("  Total Fare: ₱%.2f%n", totalFare);
            System.out.printf("  Total Distance: %.0f km%n", totalDistance);

        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to query routes: " + e.getMessage());
            e.printStackTrace();
        } finally {

            if (routeManager != null) {
                try {
                    routeManager.close();
                    System.out.println("\nDatabase connection closed.");
                } catch (SQLException e) {
                    System.err.println("[ERROR] Failed to close connection: " + e.getMessage());
                }
            }
        }
    }
}