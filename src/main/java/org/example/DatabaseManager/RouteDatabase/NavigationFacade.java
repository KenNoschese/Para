package org.example.DatabaseManager.RouteDatabase;

import org.example.DatabaseManager.RouteDatabase.StrategyClasses.LeastTransferStrategy;
import org.example.DatabaseManager.RouteDatabase.StrategyClasses.RouteStrategy;
import org.example.DatabaseManager.RouteDatabase.StrategyClasses.ShortestDistanceStrategy;
import org.example.DatabaseManager.RouteDatabase.StrategyClasses.ShortestTimeStrategy;
import org.example.gui.resources.RouteData;
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

    public RouteData findBestRoute(String from, String to, String category, String priority) throws SQLException {
        ArrayList<RouteData> possibleRoutes = routeManager.findRoutes(from, to, category);

        if (possibleRoutes.isEmpty()) {
            System.out.println("No matching route found for " + from + " → " + to);
            return null;
        }

        switch (priority.toLowerCase()) {
            case "distance" -> setRouteStrategy(new ShortestDistanceStrategy());
            case "eta", "time" -> setRouteStrategy(new ShortestTimeStrategy());
            case "transfers", "stops" -> setRouteStrategy(new LeastTransferStrategy());
            default -> setRouteStrategy(new ShortestDistanceStrategy());
        }

        Optional<RouteData> bestRoute = routeStrategy.findBestRoute(possibleRoutes);

        return bestRoute.orElse(null);
    }
}
