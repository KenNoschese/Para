package org.example.DatabaseManager.RouteDatabase.StrategyClasses;

import org.example.DatabaseManager.RouteDatabase.RouteData;
import java.util.*;

public interface RouteStrategy {
    Optional<RouteData> findBestRoute(ArrayList<RouteData> routes);
    Optional<ArrayList<RouteData>> findBestTransferRoute(ArrayList<ArrayList<RouteData>> transferRoutes);       
}