package org.example.DatabaseManager.RouteManager;

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

    @Override
    public double getDistance() {
        return distanceKm;
    }

    @Override
    public double getFare() {
        return fare;
    }

    @Override
    public void displayInfo() {
        System.out.printf("  🚏 Segment: %s → %s | %.2f km | ₱%.2f\n", from, to, distanceKm, fare);
    }
}
