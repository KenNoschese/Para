package org.example.DatabaseManager.RouteDatabase;

import org.example.DatabaseManager.RouteDatabase.StrategyClasses.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Facade for finding the best route using Strategy Pattern.
 * Works with {@link RouteComponent} (Composite Pattern).
 */
public class NavigationFacade {

    private RouteStrategy strategy;

    public void setRouteStrategy(RouteStrategy s) {
        this.strategy = s;
    }

    /**
     * Finds the best route (direct or with transfer) based on priority.
     *
     * @return List of segments (RouteComponent), or null if none
     */
    public ArrayList<RouteComponent> findBestRoute(
            String from, String to, String priority, Connection conn) throws SQLException {

        RouteManager rm = new RouteManager();
        ArrayList<RouteComponent> allRoutes = rm.findRoutesWithTransfers(from, to, conn);

        if (allRoutes.isEmpty()) return null;

        // FIXED: Now priority parameter is passed
        setRouteStrategy(switch (priority.toLowerCase()) {
            case "distance" -> new ShortestDistanceStrategy();
            case "eta", "time" -> new ShortestTimeStrategy();
            case "fare", "cheapest" -> new CheapestFareStrategy();
            case "transfers", "stops" -> new LeastTransferStrategy();
            default -> new ShortestTimeStrategy();
        });

        Optional<RouteComponent> best = strategy.findBestTransferRoute(allRoutes);
        return best.map(route -> {
            if (route instanceof Routes composite) {
                return new ArrayList<>(composite.getSegments());
            } else {
                return new ArrayList<>(List.of(route));
            }
        }).orElse(null);
    }

    /**
     * Returns all possible routes.
     */
    public ArrayList<RouteComponent> getAllRoutes(
            String from, String to, Connection conn) throws SQLException {

        return new RouteManager().findRoutesWithTransfers(from, to, conn);
    }
}