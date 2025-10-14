package org.example.DatabaseManager.RouteManager;
import org.example.DatabaseManager.databaseInstance;
import java.sql.*;
public class Manager {
    public static void main(String[] args) {
        databaseInstance dbInstance = databaseInstance.getInstance();

        buildAndDisplayRoute("bangkal", "roxas avenue", "Student");
        buildAndDisplayRoute("roxas avenue", "bangkal", "Regular");

        dbInstance.close();
    }

    public static void buildAndDisplayRoute(String from, String to, String category) {
        databaseInstance dbInstance = databaseInstance.getInstance();
        Statement st = dbInstance.getStatement();

        try {
            // ✅ Detect if user direction is inbound or outbound
            String dirQuery = """
            SELECT rs_from.stop_order AS from_order, rs_to.stop_order AS to_order
            FROM Route_Stops rs_from
            JOIN Stops s_from ON rs_from.stop_id = s_from.stop_id
            JOIN Route_Stops rs_to ON rs_from.route_id = rs_to.route_id
            JOIN Stops s_to ON rs_to.stop_id = s_to.stop_id
            WHERE s_from.stop_name = '%s' AND s_to.stop_name = '%s'
            LIMIT 1;
        """.formatted(from, to);

            ResultSet dirRS = st.executeQuery(dirQuery);
            boolean isInbound = false;
            if (dirRS.next()) {
                int fromOrder = dirRS.getInt("from_order");
                int toOrder = dirRS.getInt("to_order");
                isInbound = fromOrder < toOrder;
            }
            dirRS.close();

            // ✅ Use inbound/outbound query logic
            String sql = """
            SELECT r.route_name, s_from.stop_name AS from_stop, s_to.stop_name AS to_stop,
                   ABS(rs_to.stop_order - rs_from.stop_order) AS distance_km,
                   r.base_fare, r.base_distance_km, r.per_km_rate
            FROM Routes r
            JOIN Route_Stops rs_from ON r.route_id = rs_from.route_id
            JOIN Stops s_from ON rs_from.stop_id = s_from.stop_id
            JOIN Route_Stops rs_to ON r.route_id = rs_to.route_id
            JOIN Stops s_to ON rs_to.stop_id = s_to.stop_id
            WHERE s_from.stop_name = '%s'
              AND s_to.stop_name = '%s'
              AND ((%s AND rs_from.stop_order < rs_to.stop_order)
                OR (NOT %s AND rs_from.stop_order > rs_to.stop_order))
            LIMIT 1;
        """.formatted(from, to, isInbound, isInbound);

            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                String routeName = rs.getString("route_name");
                String fromStop = rs.getString("from_stop");
                String toStop = rs.getString("to_stop");
                double distance = rs.getDouble("distance_km");
                double baseFare = rs.getDouble("base_fare");
                double baseDist = rs.getDouble("base_distance_km");
                double perKm = rs.getDouble("per_km_rate");

                // ✅ Compute minimum ₱12 base fare
                double fare = baseFare;
                if (distance > baseDist) {
                    fare += (distance - baseDist) * perKm;
                }
                if (fare < 12.0) fare = 12.0;

                // ✅ Apply 20% discount for certain categories
                double discount = 0.0;
                if (category.equalsIgnoreCase("Student") ||
                        category.equalsIgnoreCase("PWD") ||
                        category.equalsIgnoreCase("Senior Citizen")) {
                    discount = 0.20;
                }
                double finalFare = fare * (1 - discount);

                // ✅ Display
                System.out.printf("""
                    
                    🚌 Route: %s
                    🚏 Segment: %s → %s
                    Distance: %.2f km
                    Base Fare: ₱%.2f
                    Category: %s (%.0f%% off)
                    ➡️ Final Fare: ₱%.2f
                    """,
                        routeName, fromStop, toStop, distance, fare,
                        category, discount * 100, finalFare
                );
            } else {
                System.out.printf("\n⚠️ No matching route found for %s → %s.\n", from, to);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Error building composite route: " + e.getMessage());
        }
    }
}
