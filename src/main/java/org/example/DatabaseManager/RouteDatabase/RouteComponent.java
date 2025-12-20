package org.example.DatabaseManager.RouteDatabase;

import java.util.List;

public interface RouteComponent {
    String getFromLocation();
    String getDestination();
    String getRoute();           // e.g., "Route 1", "Route 2"
    String getDetails();         // e.g., "Fastest", "Student"
    List<String> getRouteStops(); // all stops in order
    int getTransfers();          // number of transfers
    int getStops();              // total stops
    int getEta();                // total ETA in minutes
    double getDistance();        // total km
    double getFare();            // total fare

    // Helper
    default boolean isDirect() {
        return getTransfers() == 0;
    }
}