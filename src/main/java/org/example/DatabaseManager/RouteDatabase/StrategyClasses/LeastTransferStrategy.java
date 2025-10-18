package org.example.DatabaseManager.RouteDatabase.StrategyClasses;

import org.example.gui.resources.RouteData;
import java.util.*;

/**
 * Strategy to find route with the least number of stops/transfers.
 */
public class LeastTransferStrategy implements RouteStrategy {
    @Override
    public Optional<RouteData> findBestRoute(ArrayList<RouteData> routes) {
        return routes.stream().min(Comparator.comparingInt(RouteData::getStops));
    }
}

