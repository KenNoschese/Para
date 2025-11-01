package org.example.DatabaseManager.RouteDatabase.StrategyClasses;

import org.example.DatabaseManager.RouteDatabase.RouteData;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class ShortestTimeStrategy implements RouteStrategy {
    @Override
    public Optional<RouteData> findBestRoute(ArrayList<RouteData> routes) {
        return routes.stream().min(Comparator.comparingInt(RouteData::getEta));
    }

    @Override
    public Optional<ArrayList<RouteData>> findBestTransferRoute(ArrayList<ArrayList<RouteData>> transferRoutes) {
        return transferRoutes.stream()
                .min(Comparator.comparingDouble(routeList ->
                        routeList.stream().mapToDouble(RouteData::getEta).sum()));
    }
}