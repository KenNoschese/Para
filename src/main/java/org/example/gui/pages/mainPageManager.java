package org.example.gui.pages;

import org.example.DatabaseManager.RouteDatabase.NavigationFacade;
import org.example.DatabaseManager.RouteDatabase.RouteManager;
import org.example.DatabaseManager.RouteDatabase.RouteData;
import org.example.DatabaseManager.RouteDatabase.StrategyClasses.*;
import org.example.DatabaseManager.DatabaseInstance;

import java.sql.SQLException;
import java.util.*;

public class mainPageManager {
    private final RouteManager routeManager;
    private final NavigationFacade navigationFacade;
    private final ArrayList<RouteData> savedRoutes;
    private String currentFilter = "all";

    public mainPageManager() throws SQLException {
        this.routeManager = new RouteManager();
        this.navigationFacade = new NavigationFacade();
        this.savedRoutes = new ArrayList<>();
    }


}
