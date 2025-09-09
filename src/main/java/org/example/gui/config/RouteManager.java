package org.example.gui.config;

import org.example.gui.resources.RouteData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RouteManager {
    private ArrayList<RouteData> routes;
    private String filePath;

    public RouteManager(String filePath) {
        this.filePath = filePath;
        this.routes = new ArrayList<>();
        try {
            loadRoutes();
        } catch (IOException e) {
            System.err.println("Error loading routes: " + e.getMessage());
        }
    }

    public RouteManager() {
        this("ProjectFiles/routeData");
    }

    private void loadRoutes() throws IOException {
        BufferedReader sc = new BufferedReader(new FileReader(this.filePath));
        String line;

        try {
            while((line = sc.readLine()) != null) {
                String[] parts = line.split(",");
                if(parts.length >= 8) { // Changed from while to if
                    String fromLocation = parts[0].trim();
                    String destination = parts[1].trim();
                    String route = parts[2].trim();
                    int transfers = Integer.parseInt(parts[3].trim());
                    int stops = Integer.parseInt(parts[4].trim());
                    String details = parts[5].trim();
                    double fare = Double.parseDouble(parts[6].trim());
                    String ETA = parts[7].trim();

                    routes.add(new RouteData(fromLocation, destination, route, details, transfers, stops, ETA, fare));
                }
            }
        } catch (Exception ex) {
            System.out.println("No File" + ex);
        }

    }
    public ArrayList<RouteData> findRoutes(String fromLocation, String toLocation) {
        ArrayList<RouteData> matchingRoutes = new ArrayList<>();

        for (RouteData route : routes) {
            if (route.getFromLocation().equalsIgnoreCase(fromLocation.trim()) &&
                    route.getDestination().equalsIgnoreCase(toLocation.trim())) {
                matchingRoutes.add(route);
            }
        }

        return matchingRoutes;
    }

    public void reloadRoutes() throws IOException {
        routes.clear();
        loadRoutes();
    }


}
