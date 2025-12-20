package org.example.DatabaseManager.RouteDatabase.StrategyClasses;

import org.example.DatabaseManager.RouteDatabase.RouteComponent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class ShortestDistanceStrategy implements RouteStrategy {

    @Override
    public Optional<RouteComponent> findBestRoute(ArrayList<RouteComponent> routes) {
        return routes.stream()
                .min(Comparator.comparingDouble(RouteComponent::getDistance));
    }

    @Override
    public Optional<RouteComponent> findBestTransferRoute(ArrayList<RouteComponent> transferRoutes) {
        return transferRoutes.stream()
                .min(Comparator.comparingDouble(RouteComponent::getDistance));
    }
}