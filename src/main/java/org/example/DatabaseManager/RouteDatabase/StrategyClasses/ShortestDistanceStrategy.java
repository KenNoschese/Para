package org.example.DatabaseManager.RouteDatabase.StrategyClasses;

import org.example.DatabaseManager.RouteDatabase.RouteData;
import java.util.*;

public class ShortestDistanceStrategy implements RouteStrategy {
    @Override
    public Optional<RouteData> findBestRoute(ArrayList<RouteData> routes) {
        return routes.stream().min(Comparator.comparingDouble(RouteData::getDistance));
    }

    @Override
    public Optional<ArrayList<RouteData>> findBestTransferRoute(ArrayList<ArrayList<RouteData>> transferRoutes) {
        return transferRoutes.stream()
                .min(Comparator.comparingDouble(list ->
                        list.stream().mapToDouble(RouteData::getDistance).sum()));
    }
}

