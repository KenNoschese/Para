package org.example.DatabaseManager.RouteDatabase;

import org.example.DatabaseManager.DatabaseInstance;
import org.example.gui.resources.RouteData;

import java.sql.*;
import java.util.ArrayList;

public class RouteManager {
    public static void main(String[] args) { //Main method is debug only, delete later
        try {
            RouteManager manager = new RouteManager();
            NavigationFacade navigator = new NavigationFacade();
            String from = "matina crossing";
            String to = "roxas avenue";
            String category = "student";
            String priority = "fare"; // or "distance" or "eta"
                                        //Logic behind priority is user may choose which priority they want to find the best possible route. Add buttons in UI for this.

            ArrayList<RouteData> routes = manager.findRoutes(from, to, category); //Prints out all possible routes
            RouteData bestRoute = navigator.findBestRoute(from, to, category, priority); //Facade Pattern calls here, prints out one best route

            // Best Route
            if (bestRoute == null) {
                System.out.println("No matching route found for " + from + " → " + to);
                return;
            }
            System.out.println("===================================");
            System.out.println("🚍 Best Route Found:");
            System.out.println("Route: " + bestRoute.getRoute());
            System.out.println("From: " + bestRoute.getFromLocation());
            System.out.println("To: " + bestRoute.getDestination());
            System.out.println("Stops: " + bestRoute.getStops());
            System.out.println("Distance: " + bestRoute.getDistance() + " km");
            System.out.println("ETA: " + bestRoute.getETA() + " mins");
            System.out.println("Fare: ₱" + String.format("%.2f", bestRoute.getFare()));
            System.out.println("Category: " + bestRoute.getDetails());
            System.out.println("Stops along the way: " + bestRoute.getRoute_stops());
            System.out.println("===================================");

            // All routes
            if (routes.isEmpty()) {
                System.out.println("No matching route found for " + from + " → " + to);
            } else {
                for (RouteData route : routes) {
                    System.out.println("===================================");
                    System.out.println("Route: " + route.getRoute());
                    System.out.println("From: " + route.getFromLocation());
                    System.out.println("To: " + route.getDestination());
                    System.out.println("Stops: " + route.getStops());
                    System.out.println("Distance: " + route.getDistance() + " km");
                    System.out.println("ETA: " + route.getETA() + " mins");
                    System.out.println("Fare: ₱" + String.format("%.2f", route.getFare()));
                    System.out.println("Category: " + route.getDetails());
                    System.out.println("Stops along the way: " + route.getRoute_stops());
                    System.out.println("===================================");
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    private final Connection con;

    public RouteManager() {
        this.con = DatabaseInstance.getInstance().getConnection();
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
                    int stops = rs.getInt("stops");

                    double baseFare = rs.getDouble("base_fare");
                    double baseDist = rs.getDouble("base_distance_km");
                    double perKmRate = rs.getDouble("per_km_rate");

                    // Fare calculation
                    double fare = baseFare;
                    if (distanceKm > baseDist) {
                        fare += (distanceKm - baseDist) * perKmRate;
                    }
                    if (fare < 12.0) fare = 12.0;

                    double discount = getDiscountRate(category);
                    double finalFare = fare * (1 - discount);

                    // ETA calculation (3 minutes per km)
                    int eta = (int) Math.ceil(distanceKm * 3);

                    ArrayList<String> stopNames = getAllStopsForRoute(routeId, from, to);

                    RouteData data = new RouteData();
                    data.setRoute(routeName);
                    data.setFromLocation(from);
                    data.setDestination(to);
                    data.setStops(stops);
                    data.setETA(eta); // ✅ set ETA
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
            System.out.println("Direction detection failed: " + e.getMessage());
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
