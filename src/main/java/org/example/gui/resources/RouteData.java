package org.example.gui.resources;

import java.util.ArrayList;

public class RouteData {
    private ArrayList<String> routeStops = new ArrayList<>();
    private String fromLocation, destination, route, details = "Fastest";
    private int transfers, eta, distance, stops;
    private double fare;

    public String getFromLocation() {
        return fromLocation;
    }

    public String getDestination() {
        return destination;
    }

    public String getRoute() {
        return route;
    }

    public String getDetails() {
        return details;
    }

    public ArrayList<String> getRouteStops() {
        return routeStops;
    }

    public int getTransfers() {
        return transfers;
    }

    public int getStops() {
        return stops;
    }

    public int getEta() {
        return eta;
    }

    public int getDistance() {
        return distance;
    }

    public double getFare() {
        return fare;
    }

    public void setRouteStops(ArrayList<String> routeStops) {
        this.routeStops = routeStops;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setTransfers(int transfers) {
        this.transfers = transfers;
    }

    public void setStops(int stops) {
        this.stops = stops;
    }

    public void setEta(int eta) {
        this.eta = eta;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    @Override
    public String toString() {
        return "RouteData{" +
                "route='" + route + '\'' +
                ", fromLocation='" + fromLocation + '\'' +
                ", destination='" + destination + '\'' +
                ", eta=" + eta +
                ", stops=" + stops +
                ", distance=" + distance +
                ", fare=" + fare +
                ", details='" + details + '\'' +
                ", routeStops=" + routeStops +
                '}';
    }
}