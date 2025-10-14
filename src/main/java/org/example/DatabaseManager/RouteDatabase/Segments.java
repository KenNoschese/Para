package org.example.DatabaseManager.RouteDatabase;

public class Segments implements RouteComponent {
    private final String from;
    private final String to;
    private final double distanceKm;
    private final double fare;

    public Segments(String from, String to, double distanceKm, double fare) {
        this.from = from;
        this.to = to;
        this.distanceKm = distanceKm;
        this.fare = fare;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    @Override
    public double getDistance() {
        return distanceKm;
    }

    @Override
    public double getFare() {
        return fare;
    }
}
