package org.example.DatabaseManager.RouteDatabase;

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

    public String getRouteName() {
        return routeName;
    }

    public List<RouteComponent> getSegments() {
        return segments;
    }

    @Override
    public double getDistance() {
        return segments.stream().mapToDouble(RouteComponent::getDistance).sum();
    }

    @Override
    public double getFare() {
        return segments.stream().mapToDouble(RouteComponent::getFare).sum();
    }
}