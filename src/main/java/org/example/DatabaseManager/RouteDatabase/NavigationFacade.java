package org.example.DatabaseManager.RouteDatabase;

import org.example.gui.resources.RouteData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class NavigationFacade {

    private final RouteManager routeManager;

    public NavigationFacade() {
        this.routeManager = new RouteManager();
    }

    public RouteData findBestRoute(String from, String to, String category, String priority) throws SQLException {
        ArrayList<RouteData> possibleRoutes = routeManager.findRoutes(from, to, category);

        if (possibleRoutes.isEmpty()) {
            System.out.println("No matching route found for " + from + " → " + to);
            return null;
        }

        Comparator<RouteData> comparator = switch (priority.toLowerCase()) {
            case "distance" -> Comparator.comparingDouble(RouteData::getDistance);
            case "eta" -> Comparator.comparingInt(RouteData::getETA);
            case "fare" -> Comparator.comparingDouble(RouteData::getFare);
            default -> Comparator.comparingDouble(RouteData::getFare); // default to fare
        };

        Optional<RouteData> bestRoute = possibleRoutes.stream().min(comparator);

        return bestRoute.orElse(null);
    }
}

