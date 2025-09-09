package org.example.gui.resources;

public class RouteData {
    private String fromLocation, destination, route, details, ETA;
    private int transfers, stops;
    private double fare;

    public RouteData(String fromLocation, String destination, String route, String details,
                     int transfers, int stops, String ETA, double fare) {
        this.fromLocation = fromLocation;
        this.destination = destination;
        this.route = route;
        this.details = details;
        this.transfers = transfers;
        this.stops = stops;
        this.ETA = ETA;
        this.fare = fare;
    }

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

    public int getTransfers() {
        return transfers;
    }

    public int getstops() {
        return stops;
    }

    public String getETA() {
        return ETA;
    }

    public double getFare() {
        return fare;
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

    public void setETA(String ETA) {
        this.ETA = ETA;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }
}
