package org.example.DatabaseManager.RouteDatabase.StrategyClasses;

import org.example.DatabaseManager.RouteDatabase.RouteComponent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class LeastTransferStrategy implements RouteStrategy {

    @Override
    public Optional<RouteComponent> findBestRoute(ArrayList<RouteComponent> routes) {
        return routes.stream()
                .min(Comparator.comparingInt(RouteComponent::getTransfers)
                        .thenComparingInt(RouteComponent::getStops));
    }

    @Override
    public Optional<RouteComponent> findBestTransferRoute(ArrayList<RouteComponent> transferRoutes) {
        return transferRoutes.stream()
                .min(Comparator.comparingInt(RouteComponent::getTransfers)
                        .thenComparingInt(RouteComponent::getStops));
    }
}