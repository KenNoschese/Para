package org.example.DatabaseManager.RouteDatabase;

import org.example.DatabaseManager.DatabaseInstance;
import org.example.DatabaseManager.RouteDatabase.ObserversClasses.JeepneyObserver;
import org.example.DatabaseManager.RouteDatabase.ObserversClasses.JeepneySubject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    // === NORMALIZE INPUT (case + whitespace safe) ===
    // =======================================================
    private String normalize(String input) {
        return input == null ? "" : input.trim().toLowerCase();
    }

    // =======================================================
    // === FIND DIRECT ROUTES (DIRECTION-AWARE) ===
    // =======================================================
    public ArrayList<RouteData> findRoutes(String from, String to, String category) throws SQLException {
        from = normalize(from);
        to = normalize(to);
        ArrayList<RouteData> routes = new ArrayList<>();

        // Detect direction: true = outbound (from → to increasing), false = inbound
        boolean isOutbound = detectDirection(from, to);

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
        WHERE LOWER(s_from.stop_name) = LOWER(?)
          AND LOWER(s_to.stop_name) = LOWER(?)
          AND (
              (? = TRUE  AND rs_from.stop_order < rs_to.stop_order) OR
              (? = FALSE AND rs_from.stop_order > rs_to.stop_order)
          )
        GROUP BY r.route_id, r.route_name, rs_from.stop_order, rs_to.stop_order
        ORDER BY stops ASC, route_id ASC;  -- FIXED: Prefer shortest (few stops first)
        """;

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, from);
            pst.setString(2, to);
            pst.setBoolean(3, isOutbound);
            pst.setBoolean(4, isOutbound);

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

                    int eta = (int) Math.ceil(distanceKm * 3); // 3 min/km

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
    // === DIRECTION DETECTION (ROBUST) ===
    // =======================================================
    private boolean detectDirection(String from, String to) {
        String query = """
            SELECT (rs_from.stop_order < rs_to.stop_order) AS is_outbound
            FROM Route_Stops rs_from
            JOIN Stops sf ON rs_from.stop_id = sf.stop_id
            JOIN Route_Stops rs_to ON rs_from.route_id = rs_to.route_id
            JOIN Stops st ON rs_to.stop_id = st.stop_id
            WHERE LOWER(sf.stop_name) = LOWER(?) 
              AND LOWER(st.stop_name) = LOWER(?)
            LIMIT 1;
            """;
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, from);
            pst.setString(2, to);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_outbound");
                }
            }
        } catch (SQLException e) {
            System.out.println("[WARN] Direction detection failed: " + e.getMessage());
        }
        return true; // default to outbound
    }

    // =======================================================
    // === STOPS FETCHER (DIRECTION-AWARE) ===
    // =======================================================
    private ArrayList<String> getAllStopsForRoute(int routeId, String from, String to) throws SQLException {
        from = normalize(from);
        to = normalize(to);
        ArrayList<String> stops = new ArrayList<>();

        String orderQuery = """
            SELECT stop_order 
            FROM Route_Stops rs
            JOIN Stops s ON rs.stop_id = s.stop_id
            WHERE rs.route_id = ? AND LOWER(s.stop_name) = LOWER(?);
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
            System.err.println("Stops not found for route " + routeId + " (" + from + " to " + to + ")");
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
    // === FIND ROUTES WITH TRANSFERS (VALIDATED BY findRoutes()) ===
    // =======================================================
    public ArrayList<ArrayList<RouteData>> findRoutesWithTransfers(String from, String to, String category) throws SQLException {
        from = normalize(from);
        to = normalize(to);
        ArrayList<ArrayList<RouteData>> allRoutes = new ArrayList<>();

        // 1. Direct routes
        ArrayList<RouteData> directRoutes = findRoutes(from, to, category);
        for (RouteData d : directRoutes) {
            allRoutes.add(new ArrayList<>(List.of(d)));
        }

        // 2. Transfer routes — validated by findRoutes()
        String sql = """
            SELECT DISTINCT
                tp.from_route_id,
                tp.to_route_id,
                s.stop_name AS transfer_stop
            FROM Transfer_Points tp
            JOIN Stops s ON tp.stop_id = s.stop_id
            JOIN Route_Stops rs_from ON rs_from.route_id = tp.from_route_id
            JOIN Stops sf ON rs_from.stop_id = sf.stop_id AND LOWER(sf.stop_name) = LOWER(?)
            JOIN Route_Stops rs_transfer1 ON rs_transfer1.route_id = tp.from_route_id AND rs_transfer1.stop_id = s.stop_id
            JOIN Route_Stops rs_to ON rs_to.route_id = tp.to_route_id
            JOIN Stops st ON rs_to.stop_id = st.stop_id AND LOWER(st.stop_name) = LOWER(?)
            JOIN Route_Stops rs_transfer2 ON rs_transfer2.route_id = tp.to_route_id AND rs_transfer2.stop_id = s.stop_id
            WHERE tp.from_route_id != tp.to_route_id
              AND rs_from.stop_order < rs_transfer1.stop_order
              AND rs_transfer2.stop_order < rs_to.stop_order
            """;

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, from);
            pst.setString(2, to);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int fromRouteId = rs.getInt("from_route_id");
                    int toRouteId = rs.getInt("to_route_id");
                    String transferStop = rs.getString("transfer_stop");

                    System.out.println("[DEBUG] Testing transfer: " + transferStop
                            + " (Route " + fromRouteId + " → " + toRouteId + ")");

                    ArrayList<RouteData> leg1 = findRoutes(from, transferStop, category);
                    ArrayList<RouteData> leg2 = findRoutes(transferStop, to, category);

                    if (!leg1.isEmpty() && !leg2.isEmpty()) {
                        RouteData firstLeg = leg1.get(0);
                        RouteData secondLeg = leg2.get(0);

                        System.out.println("[VALID] " + firstLeg.getRoute() + " → " + transferStop
                                + " → " + secondLeg.getRoute());

                        ArrayList<RouteData> chain = new ArrayList<>();
                        chain.add(firstLeg);
                        chain.add(secondLeg);
                        allRoutes.add(chain);
                    } else {
                        System.out.println("[INVALID] No valid path: leg1=" + !leg1.isEmpty()
                                + ", leg2=" + !leg2.isEmpty());
                    }
                }
            }
        }
        return allRoutes;
    }

    // =======================================================
    // === MAIN TEST METHOD — FULL DEBUG OUTPUT ===
    // =======================================================
    public static void main(String[] args) {
        RouteManager routeManager = null;
        try {
            routeManager = new RouteManager();

            String from = "Mintal";
            String to = "NCCC Maa";
            String category = "Regular";

            System.out.println("=== ROUTE DEBUG: " + from + " to " + to + " ===");
            System.out.println("Passenger Category: " + category + "\n");

            System.out.println("[DEBUG] Normalized FROM: '" + routeManager.normalize(from) + "'");
            System.out.println("[DEBUG] Normalized TO:   '" + routeManager.normalize(to) + "'\n");

            ArrayList<ArrayList<RouteData>> allRoutes = routeManager.findRoutesWithTransfers(from, to, category);

            System.out.println("=== TOTAL ROUTES FOUND: " + allRoutes.size() + " ===\n");

            if (allRoutes.isEmpty()) {
                System.out.println("No routes found from " + from + " to " + to);
                return;
            }

            int routeCounter = 1;
            for (ArrayList<RouteData> route : allRoutes) {
                System.out.println("----------------------------------------------");
                System.out.println("Route Option " + routeCounter++ + ":");

                double totalFare = 0.0;
                int totalStops = 0;
                int totalEta = 0;

                if (route.size() == 1) {
                    RouteData r = route.get(0);
                    System.out.printf("  Direct: %s,s%n", r.getRoute());
                    System.out.printf("  Stops: %s%n", String.join(" -> ", r.getRouteStops()));
                    System.out.printf("  Fare: ₱%.2f | ETA: %d min | Stops: %d%n", r.getFare(), r.getEta(), r.getStops());
                    totalFare = r.getFare();
                    totalStops = r.getStops();
                    totalEta = r.getEta();
                } else {
                    System.out.println("  Transfer Route:");
                    for (int i = 0; i < route.size(); i++) {
                        RouteData seg = route.get(i);
                        System.out.printf("    %d. %s (%s to %s)%n",
                                i + 1, seg.getRoute(), seg.getFromLocation(), seg.getDestination());
                        System.out.printf("       Fare: ₱%.2f | ETA: %d min | Stops: %d%n", seg.getFare(), seg.getEta(), seg.getStops());
                        totalFare += seg.getFare();
                        totalStops += seg.getStops();
                        totalEta += seg.getEta();

                        if (i < route.size() - 1) {
                            System.out.printf("       TRANSFER AT: %s%n", seg.getDestination());
                        }
                    }
                }

                System.out.println("  -----------------------------");
                System.out.printf("  TOTAL: ₱%.2f | %d min | %d stops%n", totalFare, totalEta, totalStops);
                System.out.println();
            }

            System.out.println("=== DEBUG SUMMARY ===");
            System.out.println("Total route options: " + (routeCounter - 1));
            for (int i = 0; i < allRoutes.size(); i++) {
                var r = allRoutes.get(i);
                if (r.size() == 1) {
                    System.out.println("  [" + (i+1) + "] Direct: " + r.get(0).getRoute());
                } else {
                    System.out.println("  [" + (i+1) + "] Transfer at: " + r.get(0).getDestination() +
                            " → " + r.get(1).getRoute());
                }
            }

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