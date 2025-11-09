package org.example.DatabaseManager.RouteDatabase.StrategyClasses;

import org.example.DatabaseManager.RouteDatabase.RouteComponent;
import java.util.ArrayList;
import java.util.Optional;

public interface RouteStrategy {
    Optional<RouteComponent> findBestRoute(ArrayList<RouteComponent> routes);
    Optional<RouteComponent> findBestTransferRoute(ArrayList<RouteComponent> transferRoutes);
}