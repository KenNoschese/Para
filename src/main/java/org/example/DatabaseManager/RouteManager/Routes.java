package org.example.DatabaseManager.RouteManager;

import java.util.ArrayList;
import java.util.List;

public class Routes implements RouteComponent {
    private final String routeName;
    private final List<RouteComponent> segments = new ArrayList<>();

    public Routes(String routeName) {
        this.routeName = routeName;
    }

    public void addSegment(RouteComponent segment) {
        segments.add(segment);
    }

    @Override
    public double getDistance() {
        return segments.stream().mapToDouble(RouteComponent::getDistance).sum();
    }

    @Override
    public double getFare() {
        return segments.stream().mapToDouble(RouteComponent::getFare).sum();
    }

    @Override
    public void displayInfo() {
        System.out.printf("\n🚌 Route: %s\n", routeName);
        for (RouteComponent s : segments) {
            s.displayInfo();
        }
        System.out.printf("➡️ Total Distance: %.2f km | Total Fare: ₱%.2f\n", getDistance(), getFare());
    }
}
