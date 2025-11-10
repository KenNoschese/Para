// JeepneySubject.java — FINAL & CLEAN
package org.example.DatabaseManager.RouteDatabase.ObserversClasses;

import org.example.DatabaseManager.DatabaseInstance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JeepneySubject {
    private final List<JeepneyObserver> observers = new ArrayList<>();
    private final Connection con;  // ← Internal connection

    public JeepneySubject() {
        this.con = DatabaseInstance.getInstance().getConnection();  // ← Gets live connection
    }

    public void registerObserver(JeepneyObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(JeepneyObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(String plateNumber, int currentPassengers, int capacity) {
        for (JeepneyObserver observer : observers) {
            observer.update(plateNumber, currentPassengers, capacity);
        }
    }

    // ========================================
    // BOARD / LEAVE JEEPNEY — FIXED: NO CONN PARAM
    // ========================================
    public void boardJeepney(String plateNumber) throws SQLException {
        updatePassengerCount(plateNumber, true);
    }

    public void leaveJeepney(String plateNumber) throws SQLException {
        updatePassengerCount(plateNumber, false);
    }

    private void updatePassengerCount(String plateNumber, boolean boarding) throws SQLException {
        String sql = boarding
                ? "UPDATE Jeepneys SET current_passengers = current_passengers + 1 WHERE plate_number = ?"
                : "UPDATE Jeepneys SET current_passengers = GREATEST(current_passengers - 1, 0) WHERE plate_number = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, plateNumber);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                notifyChange(plateNumber);
            }
        }
    }

    // ========================================
    // NOTIFY CHANGE
    // ========================================
    private void notifyChange(String plateNumber) throws SQLException {
        String query = """
            SELECT current_passengers, capacity
            FROM Jeepneys
            WHERE plate_number = ?
            """;

        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, plateNumber);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int passengers = rs.getInt("current_passengers");
                    int capacity = rs.getInt("capacity");
                    notifyObservers(plateNumber, passengers, capacity);
                }
            }
        }
    }

    // ========================================
    // SHOW ALL JEEPNEYS — FIXED: NO CONN PARAM
    // ========================================
    public void showAllJeepneys() throws SQLException {
        String query = """
            SELECT j.plate_number, r.route_name, j.current_passengers, j.capacity
            FROM Jeepneys j
            JOIN Routes r ON j.route_id = r.route_id
            ORDER BY j.plate_number
            """;

        try (PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            System.out.println("Jeepney Status Overview:");
            System.out.println("-".repeat(60));
            while (rs.next()) {
                String plate = rs.getString("plate_number");
                String route = rs.getString("route_name");
                int curr = rs.getInt("current_passengers");
                int cap = rs.getInt("capacity");
                String status = curr >= cap ? "FULL" : curr + "/" + cap;
                System.out.printf("Plate: %-10s | Route: %-15s | %s%n", plate, route, status);
            }
            System.out.println("-".repeat(60));
        }
    }

    // ========================================
    // SUBSCRIBE CURRENT USER
    // ========================================
    public boolean subscribeCurrentUserToJeepney(String plateNumber) {
        String username = DatabaseInstance.getCurrentAppUser();
        if (username == null) {
            System.err.println("No user logged in.");
            return false;
        }

        String sql = """
            INSERT INTO Jeepney_Subscribers (jeepney_id, user_id)
            SELECT j.jeepney_id, ua.user_id
            FROM Jeepneys j
            JOIN UserAccounts ua ON ua.username = ?
            WHERE j.plate_number = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, plateNumber);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println(username + " subscribed to jeepney: " + plateNumber);
                return true;
            } else {
                System.out.println("Already subscribed or jeepney not found.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Subscribe failed: " + e.getMessage());
            return false;
        }
    }

    // ========================================
    // UNSUBSCRIBE CURRENT USER
    // ========================================
    public boolean unsubscribeCurrentUserFromJeepney(String plateNumber) {
        String username = DatabaseInstance.getCurrentAppUser();
        if (username == null) return false;

        String sql = """
            DELETE js FROM Jeepney_Subscribers js
            JOIN Jeepneys j ON js.jeepney_id = j.jeepney_id
            JOIN UserAccounts ua ON js.user_id = ua.user_id
            WHERE j.plate_number = ? AND ua.username = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, plateNumber);
            ps.setString(2, username);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println(username + " unsubscribed from: " + plateNumber);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Unsubscribe failed: " + e.getMessage());
            return false;
        }
    }

    // ========================================
    // GET SUBSCRIBERS
    // ========================================
    public ArrayList<String> getSubscribersForJeepney(String plateNumber) {
        ArrayList<String> subscribers = new ArrayList<>();
        String sql = """
            SELECT ua.username
            FROM Jeepney_Subscribers js
            JOIN Jeepneys j ON js.jeepney_id = j.jeepney_id
            JOIN UserAccounts ua ON js.user_id = ua.user_id
            WHERE j.plate_number = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, plateNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    subscribers.add(rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Fetch subscribers failed: " + e.getMessage());
        }
        return subscribers;
    }
}