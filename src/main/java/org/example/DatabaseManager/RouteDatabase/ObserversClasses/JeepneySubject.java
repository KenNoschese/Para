package org.example.DatabaseManager.RouteDatabase.ObserversClasses;

import org.example.DatabaseManager.DatabaseInstance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JeepneySubject {
    private final List<JeepneyObserver> observers = new ArrayList<>();
    private final Connection con;

    public JeepneySubject() {
        this.con = DatabaseInstance.getInstance().getConnection();
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

    public void boardJeepney(String plateNumber, Connection conn) throws SQLException {
        String updateSql = "UPDATE Jeepneys SET current_passengers = current_passengers + 1 WHERE plate_number = ?";
        try (PreparedStatement pst = con.prepareStatement(updateSql)) {
            pst.setString(1, plateNumber);
            pst.executeUpdate();
        }
        notifyChange(plateNumber);
    }

    public void leaveJeepney(String plateNumber, Connection conn) throws SQLException {
        String updateSql = "UPDATE Jeepneys SET current_passengers = GREATEST(current_passengers - 1, 0) WHERE plate_number = ?";
        try (PreparedStatement pst = con.prepareStatement(updateSql)) {
            pst.setString(1, plateNumber);
            pst.executeUpdate();
        }
        notifyChange(plateNumber);
    }

    private void notifyChange(String plateNumber) throws SQLException {
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

    public void showAllJeepneys(Connection conn) throws SQLException {
        String query = "SELECT plate_number, route_id, current_passengers, capacity FROM Jeepneys";
        try (PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            System.out.println("🚌 Jeepney Status Overview:");
            while (rs.next()) {
                System.out.printf("Plate: %s | Route ID: %d | %d/%d seats filled%n",
                        rs.getString("plate_number"),
                        rs.getInt("route_id"),
                        rs.getInt("current_passengers"),
                        rs.getInt("capacity"));
            }
        }
    }
}
