package org.example.DatabaseManager.RouteDatabase.StrategyClasses;

import org.example.gui.resources.RouteData;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

/**
 * Strategy to find route with the shortest estimated time (ETA).
 */
public class ShortestTimeStrategy implements RouteStrategy {
    @Override
    public Optional<RouteData> findBestRoute(ArrayList<RouteData> routes) {
        return routes.stream().min(Comparator.comparingInt(RouteData::getETA));
    }
}

