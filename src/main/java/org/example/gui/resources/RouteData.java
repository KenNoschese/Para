package org.example.gui.resources;

import java.util.ArrayList;

public class RouteData {
    private ArrayList<String> route_stops = new ArrayList<>();
    private String fromLocation, destination, route, details = "Fastest";
    private int transfers, ETA, distance, stops;
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

    public ArrayList<String> getRoute_stops() {
        return route_stops;
    }

    public int getTransfers() {
        return transfers;
    }

    public int getstops() {
        return stops;
    }

    public int getETA() {
        return ETA;
    }

    public int getDistance() {
        return distance;
    }

    public double getFare() {
        return fare;
    }

    public void setRoute_stops(ArrayList<String> route_stops) {
        this.route_stops = route_stops;
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

    public void setETA(int ETA) {
        this.ETA = ETA;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public int getStops() {
        return getstops();
    }

    public ArrayList<String> getRouteStops() {
        return getRoute_stops();
    }

    public void setRouteStops(ArrayList<String> stops) {
        setRoute_stops(stops);
    }

    public int getEta() {
        return getETA();
    }

    public void setEta(int eta) {
        setETA(eta);
    }

    public void setDistanceKm(int distanceKm) {
        setDistance(distanceKm);
    }

    public int getDistanceKm() {
        return getDistance();
    }

    @Override
    public String toString() {
        return "RouteData{" +
                "route='" + route + '\'' +
                ", fromLocation='" + fromLocation + '\'' +
                ", destination='" + destination + '\'' +
                ", ETA=" + ETA +
                ", stops=" + stops +
                ", distance=" + distance +
                ", fare=" + fare +
                ", details='" + details + '\'' +
                ", route_stops=" + route_stops +
                '}';
    }
}
