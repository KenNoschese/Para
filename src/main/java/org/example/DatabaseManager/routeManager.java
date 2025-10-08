package org.example.DatabaseManager;

import java.sql.*;

public class routeManager {
    private static Connection con;
    private static Statement st;
    private static ResultSet rs;

    static String db = "route_schema"; // change if needed
    static String uname = "root";
    static String pswd = "1234";

    public static void main(String[] args) {
        connectDB();

        // Example: find routes from Bangkal to Roxas
        findRoutes("Bangkal", "Roxas");

        closeConnection();
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

    public static void runQuery(String sql) {
        try {
            rs = st.executeQuery(sql);
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(meta.getColumnLabel(i) + ": " + rs.getString(i) + "  ");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("❌ Error running query: " + e.getMessage());
        }
    }

    public static void findRoutes(String from, String to) {
        String sql =
                "SELECT r.route_name, j.plate_number " +
                        "FROM Routes r " +
                        "JOIN Jeepneys j ON r.route_id = j.route_id " +
                        "JOIN Route_Stops rs_from ON r.route_id = rs_from.route_id " +
                        "JOIN Stops s_from ON rs_from.stop_id = s_from.stop_id " +
                        "JOIN Route_Stops rs_to ON r.route_id = rs_to.route_id " +
                        "JOIN Stops s_to ON rs_to.stop_id = s_to.stop_id " +
                        "WHERE s_from.stop_name = '" + from + "' " +
                        "AND s_to.stop_name = '" + to + "' " +
                        "AND rs_from.stop_order < rs_to.stop_order " +
                        "GROUP BY r.route_name, j.plate_number;";
        runQuery(sql);
    }

    public static void closeConnection() {
        try {
            if (rs != null) rs.close();
            if (st != null) st.close();
            if (con != null) con.close();
            System.out.println("🔒 Connection closed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
