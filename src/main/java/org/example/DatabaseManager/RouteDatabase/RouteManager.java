package org.example.DatabaseManager.RouteDatabase;

import org.example.DatabaseManager.databaseInstance;
import org.example.DatabaseManager.RouteDatabase.RouteComponent;
import org.example.DatabaseManager.RouteDatabase.Routes;
import org.example.DatabaseManager.RouteDatabase.Segments;
import org.example.gui.resources.RouteData;

import java.sql.*;
import java.util.ArrayList;

public class RouteManager {
    private final Connection con;

    public RouteManager() {
        this.con = databaseInstance.getInstance().getConnection();
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
                  ( ? = TRUE  AND rs_to.stop_order = (
                      SELECT MIN(rs_to2.stop_order)
                      FROM Route_Stops rs_to2
                      JOIN Stops s_to2 ON rs_to2.stop_id = s_to2.stop_id
                      WHERE rs_to2.route_id = r.route_id
                        AND s_to2.stop_name = s_to.stop_name
                        AND rs_to2.stop_order > rs_from.stop_order
                  ))
                  OR
                  ( ? = FALSE AND rs_to.stop_order = (
                      SELECT MAX(rs_to2.stop_order)
                      FROM Route_Stops rs_to2
                      JOIN Stops s_to2 ON rs_to2.stop_id = s_to2.stop_id
                      WHERE rs_to2.route_id = r.route_id
                        AND s_to2.stop_name = s_to.stop_name
                        AND rs_to2.stop_order < rs_from.stop_order
                  ))
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
//                  int eta = rs.getInt("total_eta_minutes");
                    int stops = rs.getInt("stops");

                    double baseFare = rs.getDouble("base_fare");
                    double baseDist = rs.getDouble("base_distance_km");
                    double perKmRate = rs.getDouble("per_km_rate");

                    // ✅ Fare calculation
                    double fare = baseFare;
                    if (distanceKm > baseDist) {
                        fare += (distanceKm - baseDist) * perKmRate;
                    }
                    if (fare < 12.0) fare = 12.0;

                    double discount = getDiscountRate(category);
                    double finalFare = fare * (1 - discount);

                    ArrayList<String> stopNames = getAllStopsForRoute(routeId, from, to);

                    Routes routeComposite = new Routes(routeName);
                    Segments segment = new Segments(from, to, distanceKm, finalFare);
                    routeComposite.addSegment(segment);

                    RouteData data = new RouteData();
                    data.setRoute(routeName);
                    data.setFromLocation(from);
                    data.setDestination(to);
                    data.setStops(stops);
//                  data.setETA(eta);
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
                int fromOrder = rs.getInt("from_order");
                int toOrder = rs.getInt("to_order");
                return fromOrder < toOrder;
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Direction detection failed: " + e.getMessage());
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
                   WHERE rsf.route_id = ? AND sf.stop_name = ?
                   LIMIT 1) 
              AND 
                  (SELECT rst.stop_order 
                   FROM Route_Stops rst 
                   JOIN Stops st ON rst.stop_id = st.stop_id 
                   WHERE rst.route_id = ? AND st.stop_name = ?
                   LIMIT 1)
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
}
