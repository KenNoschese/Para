package org.example.DatabaseManager.RouteDatabase;

import org.example.DatabaseManager.DatabaseInstance;
import org.example.DatabaseManager.RouteDatabase.ObserversClasses.JeepneyObserver;
import org.example.DatabaseManager.RouteDatabase.ObserversClasses.JeepneySubject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages route queries using the Composite Pattern.
 * No category needed — simplified design.
 */
public class RouteManager {

    private final JeepneySubject jeepneySubject = new JeepneySubject();

    // ──────────────────────────────────────────────────────────────
    // SAFE DB READERS
    // ──────────────────────────────────────────────────────────────
    private static double getDoubleSafe(ResultSet rs, String column) throws SQLException {
        Object obj = rs.getObject(column);
        return obj == null ? 0.0 : ((Number) obj).doubleValue();
    }

    private static int getIntSafe(ResultSet rs, String column) throws SQLException {
        Object obj = rs.getObject(column);
        return obj == null ? 0 : ((Number) obj).intValue();
    }

    /* ====================== OBSERVER MANAGEMENT ====================== */
    public void addJeepneyObserver(JeepneyObserver observer) {
        if (observer != null) jeepneySubject.registerObserver(observer);
    }

    public void boardJeepney(String plateNumber, Connection conn) throws SQLException {
        jeepneySubject.boardJeepney(plateNumber, conn);
    }

    public void leaveJeepney(String plateNumber, Connection conn) throws SQLException {
        jeepneySubject.leaveJeepney(plateNumber, conn);
    }

    public void showAllJeepneys(Connection conn) throws SQLException {
        jeepneySubject.showAllJeepneys(conn);
    }

    /* ====================== UTILITIES ====================== */
    private String normalize(String input) {
        return input == null ? "" : input.trim().toLowerCase();
    }

    /* ====================== JEEPNEY INFO ====================== */
    public ArrayList<JeepneyInfo> getJeepneysForRoute(String routeName, Connection conn) throws SQLException {
        ArrayList<JeepneyInfo> jeepneys = new ArrayList<>();
        String sql = """
            SELECT j.jeepney_id, j.plate_number, j.capacity, j.current_passengers,
                   r.route_id, r.route_name
            FROM Jeepneys j
            JOIN Routes r ON j.route_id = r.route_id
            WHERE LOWER(r.route_name) = ?
            ORDER BY j.current_passengers ASC;
            """;
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, normalize(routeName));
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    jeepneys.add(new JeepneyInfo(
                            getIntSafe(rs, "jeepney_id"),
                            rs.getString("plate_number"),
                            getIntSafe(rs, "capacity"),
                            getIntSafe(rs, "current_passengers"),
                            getIntSafe(rs, "route_id"),
                            rs.getString("route_name")
                    ));
                }
            }
        }
        return jeepneys;
    }

    public int getJeepneyCountForRoute(String routeName, Connection conn) throws SQLException {
        String sql = """
            SELECT COUNT(*) AS cnt
            FROM Jeepneys j
            JOIN Routes r ON j.route_id = r.route_id
            WHERE LOWER(r.route_name) = ?
            """;
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, normalize(routeName));
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? getIntSafe(rs, "cnt") : 0;
            }
        }
    }

    public static class JeepneyInfo {
        private final int jeepneyId, capacity, currentPassengers, routeId;
        private final String plateNumber, routeName;

        public JeepneyInfo(int jeepneyId, String plateNumber, int capacity,
                           int currentPassengers, int routeId, String routeName) {
            this.jeepneyId = jeepneyId;
            this.plateNumber = plateNumber;
            this.capacity = capacity;
            this.currentPassengers = currentPassengers;
            this.routeId = routeId;
            this.routeName = routeName;
        }

        public int getJeepneyId() { return jeepneyId; }
        public String getPlateNumber() { return plateNumber; }
        public int getCapacity() { return capacity; }
        public int getCurrentPassengers() { return currentPassengers; }
        public int getRouteId() { return routeId; }
        public String getRouteName() { return routeName; }
        public int getAvailableSeats() { return capacity - currentPassengers; }
        public boolean isFull() { return currentPassengers >= capacity; }
        public double getOccupancyPercentage() {
            return capacity > 0 ? (currentPassengers * 100.0) / capacity : 0.0;
        }
    }

    /* ====================== COMPOSITE: DIRECT LEG ====================== */
    private Segments createSegment(String from, String to, Connection conn) throws SQLException {
        from = normalize(from);
        to   = normalize(to);

        boolean outbound = detectDirection(from, to, conn);
        String sql = """
            SELECT r.route_id, r.route_name,
                   rs_from.stop_order AS from_order,
                   rs_to.stop_order   AS to_order,
                   ABS(rs_to.stop_order - rs_from.stop_order) AS distance_km,
                   r.base_fare, r.base_distance_km, r.per_km_rate,
                   (SELECT COUNT(*)
                      FROM Route_Stops rs_mid
                      WHERE rs_mid.route_id = r.route_id
                        AND rs_mid.stop_order BETWEEN
                            LEAST(rs_from.stop_order, rs_to.stop_order)
                            AND GREATEST(rs_from.stop_order, rs_to.stop_order)
                   ) AS stops
            FROM Routes r
            JOIN Route_Stops rs_from ON r.route_id = rs_from.route_id
            JOIN Stops s_from ON rs_from.stop_id = s_from.stop_id
            JOIN Route_Stops rs_to   ON r.route_id = rs_to.route_id
            JOIN Stops s_to   ON rs_to.stop_id = s_to.stop_id
            WHERE LOWER(s_from.stop_name) = LOWER(?)
              AND LOWER(s_to.stop_name)   = LOWER(?)
              AND ((? = TRUE  AND rs_from.stop_order < rs_to.stop_order) OR
                   (? = FALSE AND rs_from.stop_order > rs_to.stop_order))
            GROUP BY r.route_id, r.route_name, rs_from.stop_order, rs_to.stop_order
            ORDER BY stops ASC, r.route_id ASC
            LIMIT 1;
            """;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, from);
            pst.setString(2, to);
            pst.setBoolean(3, outbound);
            pst.setBoolean(4, outbound);

            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) return null;

                int routeId      = getIntSafe(rs, "route_id");
                String routeName = rs.getString("route_name");
                double distKm    = getDoubleSafe(rs, "distance_km");
                int stops        = getIntSafe(rs, "stops");
                double baseFare  = getDoubleSafe(rs, "base_fare");
                double baseDist  = getDoubleSafe(rs, "base_distance_km");
                double perKmRate = getDoubleSafe(rs, "per_km_rate");

                double fare = baseFare;
                if (distKm > baseDist) fare += (distKm - baseDist) * perKmRate;
                if (fare < 12.0) fare = 12.0;

                int eta = (int) Math.ceil(distKm * 3);  // 3 min per km

                ArrayList<String> stopNames = getAllStopsForRoute(routeId, from, to, conn);

                return new Segments(from, to, routeName, "Regular", stopNames, stops, eta, (int) distKm, fare);
            }
        }
    }

    private boolean detectDirection(String from, String to, Connection conn) {
        String sql = """
            SELECT (rs_from.stop_order < rs_to.stop_order) AS is_outbound
            FROM Route_Stops rs_from
            JOIN Stops sf ON rs_from.stop_id = sf.stop_id
            JOIN Route_Stops rs_to ON rs_from.route_id = rs_to.route_id
            JOIN Stops st ON rs_to.stop_id = st.stop_id
            WHERE LOWER(sf.stop_name) = LOWER(?)
              AND LOWER(st.stop_name) = LOWER(?)
            LIMIT 1;
            """;
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, from);
            pst.setString(2, to);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() && rs.getBoolean("is_outbound");
            }
        } catch (SQLException e) {
            System.out.println("[WARN] Direction detection failed: " + e.getMessage());
        }
        return true;
    }

    private ArrayList<String> getAllStopsForRoute(int routeId, String from, String to, Connection conn) throws SQLException {
        from = normalize(from);
        to   = normalize(to);
        ArrayList<String> stops = new ArrayList<>();

        String orderSql = """
            SELECT stop_order
            FROM Route_Stops rs
            JOIN Stops s ON rs.stop_id = s.stop_id
            WHERE rs.route_id = ? AND LOWER(s.stop_name) = LOWER(?);
            """;

        int fromOrder = -1, toOrder = -1;
        try (PreparedStatement pst = conn.prepareStatement(orderSql)) {
            pst.setInt(1, routeId);
            pst.setString(2, from);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) fromOrder = getIntSafe(rs, "stop_order");
            }
        }
        try (PreparedStatement pst = conn.prepareStatement(orderSql)) {
            pst.setInt(1, routeId);
            pst.setString(2, to);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) toOrder = getIntSafe(rs, "stop_order");
            }
        }

        if (fromOrder == -1 || toOrder == -1) return stops;

        boolean asc = fromOrder < toOrder;
        String stopSql = """
            SELECT s.stop_name
            FROM Route_Stops rs
            JOIN Stops s ON rs.stop_id = s.stop_id
            WHERE rs.route_id = ?
              AND rs.stop_order BETWEEN ? AND ?
            ORDER BY rs.stop_order %s;
            """.formatted(asc ? "" : "DESC");

        try (PreparedStatement pst = conn.prepareStatement(stopSql)) {
            pst.setInt(1, routeId);
            pst.setInt(2, Math.min(fromOrder, toOrder));
            pst.setInt(3, Math.max(fromOrder, toOrder));
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) stops.add(rs.getString("stop_name"));
            }
        }
        return stops;
    }

    /* ====================== COMPOSITE: FULL ROUTES ====================== */
    public ArrayList<RouteComponent> findRoutesWithTransfers(
            String from, String to, Connection conn) throws SQLException {

        from = normalize(from);
        to   = normalize(to);
        ArrayList<RouteComponent> allRoutes = new ArrayList<>();

        // === Direct Route ===
        Segments direct = createSegment(from, to, conn);
        if (direct != null) {
            Routes route = new Routes(direct.getRoute() + " (Direct)");
            route.addSegment(direct);
            allRoutes.add(route);
        }

        // === Transfer Routes ===
        String sql = """
            SELECT DISTINCT tp.from_route_id, tp.to_route_id, s.stop_name AS transfer_stop
            FROM Transfer_Points tp
            JOIN Stops s ON tp.stop_id = s.stop_id
            JOIN Route_Stops rs_from ON rs_from.route_id = tp.from_route_id
            JOIN Stops sf ON rs_from.stop_id = sf.stop_id AND LOWER(sf.stop_name) = LOWER(?)
            JOIN Route_Stops rs_t1   ON rs_t1.route_id   = tp.from_route_id AND rs_t1.stop_id   = s.stop_id
            JOIN Route_Stops rs_to   ON rs_to.route_id   = tp.to_route_id
            JOIN Stops st ON rs_to.stop_id = st.stop_id AND LOWER(st.stop_name) = LOWER(?)
            JOIN Route_Stops rs_t2   ON rs_t2.route_id   = tp.to_route_id   AND rs_t2.stop_id   = s.stop_id
            WHERE tp.from_route_id != tp.to_route_id
              AND rs_from.stop_order < rs_t1.stop_order
              AND rs_t2.stop_order   < rs_to.stop_order;
            """;

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, from);
            pst.setString(2, to);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String transfer = rs.getString("transfer_stop");

                    Segments leg1 = createSegment(from, transfer, conn);
                    Segments leg2 = createSegment(transfer, to, conn);

                    if (leg1 != null && leg2 != null) {
                        Routes route = new Routes(from + " to " + to + " via " + transfer);
                        route.addSegment(leg1);
                        route.addSegment(leg2);
                        allRoutes.add(route);
                    }
                }
            }
        }
        return allRoutes;
    }
}