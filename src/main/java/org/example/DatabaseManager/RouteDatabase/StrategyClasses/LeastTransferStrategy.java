package org.example.DatabaseManager.RouteDatabase.StrategyClasses;

import org.example.gui.resources.RouteData;
import java.util.*;

public class LeastTransferStrategy implements RouteStrategy {
    @Override
    public Optional<RouteData> findBestRoute(ArrayList<RouteData> routes) {
        return routes.stream().min(Comparator.comparingInt(RouteData::getStops));
    }

    @Override
    public Optional<ArrayList<RouteData>> findBestTransferRoute(ArrayList<ArrayList<RouteData>> transferRoutes) {
        return transferRoutes.stream()
                .min(Comparator.comparingDouble(routeList ->
                        routeList.stream().mapToDouble(RouteData::getStops).sum()));
    }
}

