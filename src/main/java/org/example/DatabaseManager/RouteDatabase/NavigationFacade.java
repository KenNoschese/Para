package org.example.DatabaseManager.RouteDatabase;

import org.example.DatabaseManager.RouteDatabase.StrategyClasses.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class NavigationFacade {

    private final RouteManager routeManager;
    private RouteStrategy routeStrategy;

    public NavigationFacade() throws SQLException {
        this.routeManager = new RouteManager();
    }

    public void setRouteStrategy(RouteStrategy routeStrategy) {
        this.routeStrategy = routeStrategy;
    }

    // FIXED: Now considers DIRECT + TRANSFERS
    public ArrayList<RouteData> findBestRoute(String from, String to, String category, String priority) throws SQLException {
        ArrayList<ArrayList<RouteData>> allRoutes = routeManager.findRoutesWithTransfers(from, to, category);

        if (allRoutes.isEmpty()) return null;

        switch (priority.toLowerCase()) {
            case "distance" -> setRouteStrategy(new ShortestDistanceStrategy());
            case "eta", "time" -> setRouteStrategy(new ShortestTimeStrategy());
            case "fare", "cheapest" -> setRouteStrategy(new CheapestFareStrategy());
            case "transfers", "stops" -> setRouteStrategy(new LeastTransferStrategy());
            default -> setRouteStrategy(new ShortestDistanceStrategy());
        }

        Optional<ArrayList<RouteData>> bestFull = routeStrategy.findBestTransferRoute(allRoutes);
        return bestFull.orElse(null);
    }

    // Helper: to get full route later in mainPage
    public ArrayList<ArrayList<RouteData>> getAllRoutes(String from, String to, String category) throws SQLException {
        return routeManager.findRoutesWithTransfers(from, to, category);
    }
}