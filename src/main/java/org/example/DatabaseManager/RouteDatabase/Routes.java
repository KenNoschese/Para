package org.example.DatabaseManager.RouteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Routes implements RouteComponent {
    private final List<RouteComponent> segments = new ArrayList<>();
    private final String routeName; // e.g., "Mintal → NCCC Maa"

    public Routes(String routeName) {
        this.routeName = routeName;
    }

    public void addSegment(RouteComponent segment) {
        if (segment != null) segments.add(segment);
    }

    @Override
    public String getFromLocation() {
        return segments.isEmpty() ? "" : segments.get(0).getFromLocation();
    }

    @Override
    public String getDestination() {
        return segments.isEmpty() ? "" : segments.get(segments.size() - 1).getDestination();
    }

    @Override
    public String getRoute() {
        return routeName;
    }

    @Override
    public String getDetails() {
        return segments.stream()
                .map(RouteComponent::getDetails)
                .findFirst()
                .orElse("Unknown");
    }

    @Override
    public List<String> getRouteStops() {
        List<String> allStops = new ArrayList<>();
        for (int i = 0; i < segments.size(); i++) {
            List<String> legStops = segments.get(i).getRouteStops();
            if (i > 0) {
                // Avoid duplicating transfer stop
                allStops.remove(allStops.size() - 1);
            }
            allStops.addAll(legStops);
        }
        return allStops;
    }

    @Override
    public int getTransfers() {
        return Math.max(0, segments.size() - 1);
    }

    @Override
    public int getStops() {
        return (int) getRouteStops().stream().distinct().count();
    }

    @Override
    public int getEta() {
        return segments.stream().mapToInt(RouteComponent::getEta).sum();
    }

    @Override
    public double getDistance() {
        return segments.stream().mapToDouble(RouteComponent::getDistance).sum();
    }

    @Override
    public double getFare() {
        return segments.stream().mapToDouble(RouteComponent::getFare).sum();
    }

    public List<RouteComponent> getSegments() {
        return List.copyOf(segments);
    }

    @Override
    public String toString() {
        return "Routes{" +
                "name='" + routeName + '\'' +
                ", transfers=" + getTransfers() +
                ", fare=" + getFare() +
                ", eta=" + getEta() +
                ", stops=" + getStops() +
                '}';
    }
}