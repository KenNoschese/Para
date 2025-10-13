package org.example.gui.config;

import com.mysql.cj.protocol.Resultset;
import org.example.gui.resources.RouteData;

import java.sql.*;
import java.util.ArrayList;

public class RouteManager {
    private static Connection con;
    private static Statement st;
    private static ResultSet rs;

    static String db = "route_schema"; // change if needed
    static String uname = "root";
    static String pswd = "Ken11514!";

    public RouteManager() {
        connectDB();
    }

    public static void connectDB() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/" + db + "?serverTimezone=UTC",
                    uname, pswd
            );
            st = con.createStatement();
            System.out.println("✅ Connected to database: " + db);
        } catch (Exception e) {
            System.out.println("❌ Failed to connect: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static ArrayList<RouteData> findRoutes(String from, String to) throws SQLException {
        ArrayList<RouteData> routes = new ArrayList<>();
        String sql = """
            SELECT 
                r.route_id,
                r.route_name,
                MIN(rs_to.total_eta_minutes) AS total_eta_minutes,
                (
                    SELECT COUNT(*) 
                    FROM Route_Stops rs_mid
                    WHERE rs_mid.route_id = r.route_id
                      AND rs_mid.stop_order BETWEEN rs_from.stop_order AND rs_to.stop_order
                ) AS stops
            FROM Routes r
            JOIN Jeepneys j ON r.route_id = j.route_id
            JOIN Route_Stops rs_from ON r.route_id = rs_from.route_id
            JOIN Stops s_from ON rs_from.stop_id = s_from.stop_id
            JOIN Route_Stops rs_to ON r.route_id = rs_to.route_id
            JOIN Stops s_to ON rs_to.stop_id = s_to.stop_id
            WHERE s_from.stop_name = ?
              AND s_to.stop_name = ?
              AND rs_from.stop_order < rs_to.stop_order
            GROUP BY r.route_id, r.route_name, rs_from.stop_order, rs_to.stop_order;
            """;

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, from);
            pst.setString(2, to);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    RouteData data = new RouteData();
                    int routeId = rs.getInt("route_id");

                    data.setRoute(rs.getString("route_name"));
                    data.setETA(rs.getInt("total_eta_minutes"));
                    data.setStops(rs.getInt("stops"));
                    data.setFromLocation(from);
                    data.setDestination(to);

                    ArrayList<String> stopNames = getAllStopsForRoute(routeId, from, to);
                    data.setRoute_stops(stopNames);

                    routes.add(data);
                }
            }
        } catch (SQLException sq) {
            sq.printStackTrace();
        }

        return routes;
    }
    private static ArrayList<String> getAllStopsForRoute(int routeId, String from, String to) throws SQLException {
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
               WHERE rsf.route_id = ? AND sf.stop_name = ?)
          AND 
              (SELECT rst.stop_order 
               FROM Route_Stops rst 
               JOIN Stops st ON rst.stop_id = st.stop_id 
               WHERE rst.route_id = ? AND st.stop_name = ?)
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


}
