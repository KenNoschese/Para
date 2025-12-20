// JeepneySubject.java — FINAL & CLEAN
package org.example.DatabaseManager.RouteDatabase.ObserversClasses;

import org.example.DatabaseManager.DatabaseInstance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// JeepneySubject.java — FINAL
public class JeepneySubject {
    private final List<JeepneyObserver> observers = new ArrayList<>();

    public JeepneySubject() {
        // DO NOT CACHE CONNECTION
    }

    public void boardJeepney(String plateNumber) throws SQLException {
        try (Connection con = DatabaseInstance.getInstance().getConnection()) {
            updatePassengerCount(con, plateNumber, true);
        }
    }

    public void leaveJeepney(String plateNumber) throws SQLException {
        try (Connection con = DatabaseInstance.getInstance().getConnection()) {
            updatePassengerCount(con, plateNumber, false);
        }
    }

    private void updatePassengerCount(Connection con, String plateNumber, boolean boarding) throws SQLException {
        String sql = boarding
                ? "UPDATE Jeepneys SET current_passengers = current_passengers + 1 WHERE plate_number = ?"
                : "UPDATE Jeepneys SET current_passengers = GREATEST(current_passengers - 1, 0) WHERE plate_number = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, plateNumber);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                notifyChange(con, plateNumber);
            }
        }
    }

    private void notifyChange(Connection con, String plateNumber) throws SQLException {
        String query = "SELECT current_passengers, capacity FROM Jeepneys WHERE plate_number = ?";
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

    private void notifyObservers(String plateNumber, int currentPassengers, int capacity) {
        for (JeepneyObserver observer : observers) {
            observer.update(plateNumber, currentPassengers, capacity);
        }
    }

    public void registerObserver(JeepneyObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(JeepneyObserver observer) {
        observers.remove(observer);
    }

    public void showAllJeepneys() throws SQLException {
        try (Connection con = DatabaseInstance.getInstance().getConnection()) {
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
    }
}