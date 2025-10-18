package org.example.DatabaseManager.RouteDatabase.StrategyClasses;

import org.example.gui.resources.RouteData;
import java.util.*;

/**
 * Strategy interface for different route-finding algorithms.
 * Implementations: ShortestDistanceStrategy, ShortestTimeStrategy, LeastTransfersStrategy.
 */
public interface RouteStrategy {
    Optional<RouteData> findBestRoute(ArrayList<RouteData> routes);
}

