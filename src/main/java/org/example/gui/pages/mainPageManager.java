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
    private final ArrayList<ArrayList<RouteData>> savedRoutes;
    private String currentFilter = "all";

    public mainPageManager() throws SQLException {
        this.routeManager = new RouteManager();
        this.navigationFacade = new NavigationFacade();
        this.savedRoutes = new ArrayList<>();
    }

    public SearchResult searchRoutes(String from, String to) throws SQLException {
        if (from == null || from.trim().isEmpty() || to == null || to.trim().isEmpty()) {
            return SearchResult.empty("Please enter both current location and destination.");
        }

        String category = getUserCategory();

        if (currentFilter.equals("all")) {
            return searchAllRoutes(from, to, category);
        } else {
            return searchFilteredRoutes(from, to, category);
        }
    }

    private SearchResult searchAllRoutes(String from, String to, String category) throws SQLException {
        ArrayList<ArrayList<RouteData>> allPossible = routeManager.findRoutesWithTransfers(from, to, category);
        allPossible = removeDuplicateRouteOptions(allPossible);

        if (allPossible.isEmpty()) {
            return SearchResult.empty("No routes found from " + from + " to " + to);
        }

        ArrayList<RouteData> firstRoute = allPossible.get(0);
        return new SearchResult(allPossible, firstRoute, true);
    }

    private SearchResult searchFilteredRoutes(String from, String to, String category) throws SQLException {
        ArrayList<RouteData> bestFullRoute = navigationFacade.findBestRoute(from, to, category, currentFilter);

        if (bestFullRoute == null || bestFullRoute.isEmpty()) {
            return SearchResult.empty("No routes found for the selected filter.");
        }

        ArrayList<ArrayList<RouteData>> single = new ArrayList<>();
        single.add(bestFullRoute);
        return new SearchResult(single, bestFullRoute, true);
    }

    private String getUserCategory() {
        String category = "Student";
        try {
            DatabaseInstance db = DatabaseInstance.getInstance();
            String pswd = db.getActivePassword();
            if (pswd != null && !pswd.isEmpty()) {
                char first = pswd.charAt(0);
                if (first == '1') category = "Regular";
                else if (first == '2') category = "Student";
                else if (first == '3') category = "PWD";
                else if (first == '4') category = "Senior Citizen";
            }
        } catch (Exception ignored) {}
        return category;
    }

    public boolean addSavedRoute(ArrayList<RouteData> route) {
        if (route == null || route.isEmpty()) return false;

        for (ArrayList<RouteData> existing : savedRoutes) {
            if (isSameRoute(existing, route)) {
                return false;
            }
        }

        savedRoutes.add(route);
        return true;
    }

    public void removeSavedRoute(ArrayList<RouteData> route) {
        savedRoutes.remove(route);
    }

    public ArrayList<ArrayList<RouteData>> getSavedRoutes() {
        return new ArrayList<>(savedRoutes);
    }

    public void clearSavedRoutes() {
        savedRoutes.clear();
    }

    private boolean isSameRoute(ArrayList<RouteData> r1, ArrayList<RouteData> r2) {
        if (r1.size() != r2.size()) return false;
        for (int i = 0; i < r1.size(); i++) {
            RouteData seg1 = r1.get(i);
            RouteData seg2 = r2.get(i);
            if (!seg1.getRoute().equals(seg2.getRoute()) ||
                    !seg1.getFromLocation().equals(seg2.getFromLocation()) ||
                    !seg1.getDestination().equals(seg2.getDestination())) {
                return false;
            }
        }
        return true;
    }

    public void setFilter(String filter) {
        this.currentFilter = filter;
    }

    public String getCurrentFilter() {
        return currentFilter;
    }

    public RouteDetails calculateTransferMetrics(ArrayList<RouteData> transfer) {
        if (transfer == null || transfer.isEmpty()) {
            return new RouteDetails(0, 0, 0.0, 0);
        }

        int totalETA = 0;
        int totalStops = 0;
        double totalFare = 0.0;
        int transfers = transfer.size() - 1;

        for (RouteData r : transfer) {
            totalETA += r.getEta();
            totalStops += r.getStops();
            totalFare += r.getFare();
        }

        return new RouteDetails(totalETA, totalStops, totalFare, transfers);
    }

    public RouteStrategy getStrategyForPriority(String priority) {
        switch (priority.toLowerCase()) {
            case "distance":
                return new ShortestDistanceStrategy();
            case "time":
            case "eta":
                return new ShortestTimeStrategy();
            case "transfers":
            case "stops":
                return new LeastTransferStrategy();
            case "fare":
                return new CheapestFareStrategy();
            default:
                return new ShortestTimeStrategy();
        }
    }

    private ArrayList<ArrayList<RouteData>> removeDuplicateRouteOptions(ArrayList<ArrayList<RouteData>> list) {
        Set<String> seen = new HashSet<>();
        ArrayList<ArrayList<RouteData>> uniq = new ArrayList<>();

        for (ArrayList<RouteData> route : list) {
            StringBuilder key = new StringBuilder();
            for (RouteData seg : route) {
                key.append(seg.getRoute()).append("→")
                        .append(seg.getFromLocation()).append("→")
                        .append(seg.getDestination()).append(";");
            }
            if (seen.add(key.toString())) {
                uniq.add(route);
            }
        }
        return uniq;
    }

    public boolean validateInput(String from, String to) {
        return from != null && !from.trim().isEmpty() &&
                to != null && !to.trim().isEmpty();
    }

    public static class SearchResult {
        private final ArrayList<ArrayList<RouteData>> allRoutes;
        private final ArrayList<RouteData> defaultRoute;
        private final boolean success;
        private final String message;

        public SearchResult(ArrayList<ArrayList<RouteData>> allRoutes,
                            ArrayList<RouteData> defaultRoute,
                            boolean success) {
            this.allRoutes = allRoutes;
            this.defaultRoute = defaultRoute;
            this.success = success;
            this.message = "";
        }

        private SearchResult(String message) {
            this.allRoutes = new ArrayList<>();
            this.defaultRoute = null;
            this.success = false;
            this.message = message;
        }

        public static SearchResult empty(String message) {
            return new SearchResult(message);
        }

        public ArrayList<ArrayList<RouteData>> getAllRoutes() {
            return allRoutes;
        }

        public ArrayList<RouteData> getDefaultRoute() {
            return defaultRoute;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public boolean isTransferRoute() {
            return defaultRoute != null && defaultRoute.size() > 1;
        }

        public boolean isDirect() {
            return defaultRoute != null && defaultRoute.size() == 1;
        }
    }

    public static class RouteDetails {
        private final int totalETA;
        private final int totalStops;
        private final double totalFare;
        private final int transfers;

        public RouteDetails(int totalETA, int totalStops, double totalFare, int transfers) {
            this.totalETA = totalETA;
            this.totalStops = totalStops;
            this.totalFare = totalFare;
            this.transfers = transfers;
        }

        public int getTotalETA() {
            return totalETA;
        }

        public int getTotalStops() {
            return totalStops;
        }

        public double getTotalFare() {
            return totalFare;
        }

        public int getTransfers() {
            return transfers;
        }

        public int getSegments() {
            return transfers + 1;
        }
    }
}