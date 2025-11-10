package org.example.DatabaseManager.RouteDatabase.ObserversClasses;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JeepneySubject {
    private final List<JeepneyObserver> observers = new ArrayList<>();

    // Register observer
    public void registerObserver(JeepneyObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            System.out.println("✅ Observer registered: " + observer.getClass().getSimpleName());
        }
    }

    // Remove observer
    public void removeObserver(JeepneyObserver observer) {
        observers.remove(observer);
        System.out.println("❌ Observer removed: " + observer.getClass().getSimpleName());
    }

    // Notify all observers
    private void notifyObservers(String plateNumber, int currentPassengers, int capacity) {
        System.out.println("📢 Notifying " + observers.size() + " observers about " + plateNumber);
        for (JeepneyObserver observer : observers) {
            try {
                observer.update(plateNumber, currentPassengers, capacity);
            } catch (Exception e) {
                System.err.println("⚠️ Error notifying observer: " + e.getMessage());
            }
        }
    }

    // Board a jeepney (increment passengers)
    // ✅ FIXED: Use the connection passed as parameter, don't store it!
    public void boardJeepney(String plateNumber, Connection conn) throws SQLException {
        String selectSql = "SELECT current_passengers, capacity FROM Jeepneys WHERE plate_number = ?";
        String updateSql = "UPDATE Jeepneys SET current_passengers = current_passengers + 1 WHERE plate_number = ?";

        int currentPassengers = 0;
        int capacity = 0;

        // First, get current state
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, plateNumber);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    currentPassengers = rs.getInt("current_passengers");
                    capacity = rs.getInt("capacity");

                    if (currentPassengers >= capacity) {
                        throw new SQLException("Jeepney is full! Cannot board.");
                    }
                } else {
                    throw new SQLException("Jeepney not found: " + plateNumber);
                }
            }
        }

        // Then update
        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setString(1, plateNumber);
            int rowsAffected = updateStmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Boarded " + plateNumber +
                        " - Passengers: " + (currentPassengers + 1) + "/" + capacity);

                // Notify observers AFTER successful update
                notifyObservers(plateNumber, currentPassengers + 1, capacity);
            } else {
                throw new SQLException("Failed to board jeepney.");
            }
        }
    }

    // Leave a jeepney (decrement passengers)
    public void leaveJeepney(String plateNumber, Connection conn) throws SQLException {
        String selectSql = "SELECT current_passengers, capacity FROM Jeepneys WHERE plate_number = ?";
        String updateSql = "UPDATE Jeepneys SET current_passengers = GREATEST(current_passengers - 1, 0) WHERE plate_number = ?";

        int currentPassengers = 0;
        int capacity = 0;

        // First, get current state
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, plateNumber);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    currentPassengers = rs.getInt("current_passengers");
                    capacity = rs.getInt("capacity");

                    if (currentPassengers <= 0) {
                        throw new SQLException("No passengers to disembark!");
                    }
                } else {
                    throw new SQLException("Jeepney not found: " + plateNumber);
                }
            }
        }

        // Then update
        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setString(1, plateNumber);
            int rowsAffected = updateStmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Left " + plateNumber +
                        " - Passengers: " + (currentPassengers - 1) + "/" + capacity);

                // Notify observers AFTER successful update
                notifyObservers(plateNumber, currentPassengers - 1, capacity);
            } else {
                throw new SQLException("Failed to leave jeepney.");
            }
        }
    }

    // Show all jeepneys (for debugging)
    public void showAllJeepneys(Connection conn) throws SQLException {
        String sql = """
            SELECT j.plate_number, j.capacity, j.current_passengers, r.route_name
            FROM Jeepneys j
            JOIN Routes r ON j.route_id = r.route_id
            ORDER BY r.route_name, j.plate_number;
            """;

        System.out.println("\n=== ALL JEEPNEYS ===");
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String plate = rs.getString("plate_number");
                int capacity = rs.getInt("capacity");
                int current = rs.getInt("current_passengers");
                String route = rs.getString("route_name");

                String status = current >= capacity ? "[FULL]" :
                        current >= capacity * 0.8 ? "[ALMOST FULL]" : "[AVAILABLE]";

                System.out.printf("%-15s | Route: %-20s | Passengers: %2d/%-2d %s\n",
                        plate, route, current, capacity, status);
            }
        }
        System.out.println("===================\n");
    }
}