package org.example.DatabaseManager.RouteDatabase;

import java.util.List;

public class Segments implements RouteComponent {
    private final String from, to, route, details;
    private final List<String> routeStops;
    private final int stops, eta, distance;
    private final double fare;

    public Segments(String from, String to, String route, String details,
                    List<String> routeStops, int stops, int eta, int distance, double fare) {
        this.from = from;
        this.to = to;
        this.route = route;
        this.details = details != null ? details : "";
        this.routeStops = routeStops != null ? List.copyOf(routeStops) : List.of();
        this.stops = stops;
        this.eta = eta;
        this.distance = distance;
        this.fare = fare;
    }

    @Override public String getFromLocation() { return from; }
    @Override public String getDestination() { return to; }
    @Override public String getRoute() { return route; }
    @Override public String getDetails() { return details; }
    @Override public List<String> getRouteStops() { return routeStops; }
    @Override public int getTransfers() { return 0; }
    @Override public int getStops() { return stops; }
    @Override public int getEta() { return eta; }
    @Override public double getDistance() { return distance; }
    @Override public double getFare() { return fare; }
}