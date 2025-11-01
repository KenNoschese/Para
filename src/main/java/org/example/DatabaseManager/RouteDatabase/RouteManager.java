package org.example.DatabaseManager.RouteDatabase;

import org.example.DatabaseManager.DatabaseInstance;
import org.example.DatabaseManager.RouteDatabase.ObserversClasses.JeepneyObserver;
import org.example.DatabaseManager.RouteDatabase.ObserversClasses.JeepneySubject;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

// For fetching routes, fare & time computation and managing observer updates (boarding/leaving)
public class RouteManager {
    private final Connection con;
    private final JeepneySubject jeepneySubject;

    public RouteManager() throws SQLException {
        this.con = DatabaseInstance.getInstance().getConnection();
        this.jeepneySubject = new JeepneySubject();
    }

    public void addJeepneyObserver(JeepneyObserver observer) {
        if (observer != null) jeepneySubject.registerObserver(observer);
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
        if (con != null && !con.isClosed()) con.close();
    }

    // =======================================================
    // === FIND DIRECT ROUTES ===
    // =======================================================
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
                    if (distanceKm > baseDist) fare += (distanceKm - baseDist) * perKmRate;
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
                    data.setEta(eta);
                    data.setDistance((int) distanceKm);
                    data.setFare(finalFare);
                    data.setDetails(category);
                    data.setRouteStops(stopNames);

                    routes.add(data);
                }
            }
        }
        return routes;
    }

    // =======================================================
    // === DIRECTION DETECTION ===
    // =======================================================
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
            if (rs.next()) return rs.getInt("from_order") < rs.getInt("to_order");
        } catch (SQLException e) {
            System.out.println("[WARN] Direction detection failed: " + e.getMessage());
        }
        return true;
    }

    // =======================================================
    // === STOPS FETCHER ===
    // =======================================================
    private ArrayList<String> getAllStopsForRoute(int routeId, String from, String to) throws SQLException {
        ArrayList<String> stops = new ArrayList<>();

        String orderQuery = """
            SELECT stop_order 
            FROM Route_Stops rs
            JOIN Stops s ON rs.stop_id = s.stop_id
            WHERE rs.route_id = ? AND s.stop_name = ?;
        """;

        int fromOrder = -1, toOrder = -1;
        try (PreparedStatement pst = con.prepareStatement(orderQuery)) {
            pst.setInt(1, routeId);
            pst.setString(2, from);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) fromOrder = rs.getInt("stop_order");
            }
        }

        try (PreparedStatement pst = con.prepareStatement(orderQuery)) {
            pst.setInt(1, routeId);
            pst.setString(2, to);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) toOrder = rs.getInt("stop_order");
            }
        }

        if (fromOrder == -1 || toOrder == -1) {
            System.err.println("⚠️ Stops not found for route " + routeId + " (" + from + " → " + to + ")");
            return stops;
        }

        boolean ascending = fromOrder < toOrder;
        String stopQuery = """
            SELECT s.stop_name
            FROM Route_Stops rs
            JOIN Stops s ON rs.stop_id = s.stop_id
            WHERE rs.route_id = ?
              AND rs.stop_order BETWEEN ? AND ?
            ORDER BY rs.stop_order %s;
        """.formatted(ascending ? "" : "DESC");

        try (PreparedStatement pst = con.prepareStatement(stopQuery)) {
            pst.setInt(1, routeId);
            pst.setInt(2, Math.min(fromOrder, toOrder));
            pst.setInt(3, Math.max(fromOrder, toOrder));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) stops.add(rs.getString("stop_name"));
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

    // =======================================================
    // === FIND ROUTES WITH TRANSFERS ===
    // =======================================================
    public ArrayList<ArrayList<RouteData>> findRoutesWithTransfers(String from, String to, String category) throws SQLException {
        ArrayList<ArrayList<RouteData>> allRoutes = new ArrayList<>();

        // include direct routes
        ArrayList<RouteData> directRoutes = findRoutes(from, to, category);
        allRoutes.addAll(directRoutes.stream().map(r -> {
            ArrayList<RouteData> single = new ArrayList<>();
            single.add(r);
            return single;
        }).toList());

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
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String transferStop = rs.getString("transfer_stop");

                // --- Segment 1 ---
                int fromRouteId = rs.getInt("from_route_id");
                String fromRouteName = rs.getString("from_route_name");
                double fromDistanceKm = Math.abs(rs.getDouble("transfer_from_order") - rs.getDouble("from_order"));
                double fromFare = computeFare(rs.getDouble("from_base_fare"),
                        rs.getDouble("from_base_distance_km"),
                        rs.getDouble("from_per_km_rate"),
                        fromDistanceKm,
                        category);
                ArrayList<String> fromStopsList = getAllStopsForRoute(fromRouteId, from, transferStop);

                RouteData fromRoute = new RouteData();
                fromRoute.setRoute(fromRouteName);
                fromRoute.setFromLocation(from);
                fromRoute.setDestination(transferStop);
                fromRoute.setDistance((int) fromDistanceKm);
                fromRoute.setFare(fromFare);
                fromRoute.setDetails(category);
                fromRoute.setRouteStops(fromStopsList);
                fromRoute.setStops(fromStopsList.size());
                fromRoute.setEta((int) Math.ceil(fromDistanceKm * 3));

                // --- Segment 2 ---
                int toRouteId = rs.getInt("to_route_id");
                String toRouteName = rs.getString("to_route_name");
                double toDistanceKm = Math.abs(rs.getDouble("to_order") - rs.getDouble("transfer_to_order"));
                double toFare = computeFare(rs.getDouble("to_base_fare"),
                        rs.getDouble("to_base_distance_km"),
                        rs.getDouble("to_per_km_rate"),
                        toDistanceKm,
                        category);
                ArrayList<String> toStopsList = getAllStopsForRoute(toRouteId, transferStop, to);

                RouteData toRoute = new RouteData();
                toRoute.setRoute(toRouteName);
                toRoute.setFromLocation(transferStop);
                toRoute.setDestination(to);
                toRoute.setDistance((int) toDistanceKm);
                toRoute.setFare(toFare);
                toRoute.setDetails(category);
                toRoute.setRouteStops(toStopsList);
                toRoute.setStops(toStopsList.size());
                toRoute.setEta((int) Math.ceil(toDistanceKm * 3));

                ArrayList<RouteData> transferRoute = new ArrayList<>();
                transferRoute.add(fromRoute);
                transferRoute.add(toRoute);
                allRoutes.add(transferRoute);
            }
        }
        return allRoutes;
    }

    private double computeFare(double baseFare, double baseDist, double perKmRate, double distanceKm, String category) {
        double fare = baseFare;
        if (distanceKm > baseDist) fare += (distanceKm - baseDist) * perKmRate;
        if (fare < 12.0) fare = 12.0;
        return fare * (1 - getDiscountRate(category));
    }

    // =======================================================
    // === MAIN TEST METHOD ===
    // =======================================================
    public static void main(String[] args) {
        RouteManager routeManager = null;
        try {
            routeManager = new RouteManager();

            String from = "Toril";
            String to = "GMall Bajada";
            String category = "Regular";

            System.out.println("=== Route Debug: " + from + " → " + to + " (Least Transfers) ===");
            System.out.println("Passenger Category: " + category);

            ArrayList<ArrayList<RouteData>> allRoutes = routeManager.findRoutesWithTransfers(from, to, category);

            if (allRoutes.isEmpty()) {
                System.out.println("No routes found from " + from + " to " + to);
                return;
            }

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
            int totalStops = 0;
            int totalEta = 0;

            if (route.size() == 1) {
                RouteData r = route.get(0);
                System.out.printf("  Direct Route: %s%n", r.getRoute());
                System.out.printf("  Stops: %s%n", String.join(" -> ", r.getRouteStops()));
                System.out.printf("  Stops Count: %d%n", r.getStops());
                System.out.printf("  ETA: %d minutes%n", r.getEta());
                System.out.printf("  Fare: ₱%.2f%n", r.getFare());
                System.out.printf("  Distance: %d km%n", r.getDistance());
                totalFare = r.getFare();
                totalDistance = r.getDistance();
                totalStops = r.getStops();
                totalEta = r.getEta();
            } else {
                System.out.println("  Transfer Route:");
                for (int i = 0; i < route.size(); i++) {
                    RouteData seg = route.get(i);
                    System.out.printf("    Segment %d: %s (from %s to %s)%n",
                            i + 1, seg.getRoute(), seg.getFromLocation(), seg.getDestination());
                    System.out.printf("      Stops: %s%n", String.join(" -> ", seg.getRouteStops()));
                    System.out.printf("      Stops Count: %d%n", seg.getStops());
                    System.out.printf("      ETA: %d minutes%n", seg.getEta());
                    System.out.printf("      Fare: ₱%.2f%n", seg.getFare());
                    System.out.printf("      Distance: %d km%n", seg.getDistance());
                    totalFare += seg.getFare();
                    totalDistance += seg.getDistance();
                    totalStops += seg.getStops();
                    totalEta += seg.getEta();
                }
            }

            System.out.printf("  Total ETA: %d minutes%n", totalEta);
            System.out.printf("  Total Stops: %d%n", totalStops);
            System.out.printf("  Total Fare: ₱%.2f%n", totalFare);
            System.out.printf("  Total Distance: %.0f km%n", totalDistance);

        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to query routes: " + e.getMessage());
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