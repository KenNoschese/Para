package org.example.gui.pages.managers;

import org.example.DatabaseManager.RouteDatabase.RouteComponent;
import org.example.DatabaseManager.RouteDatabase.RouteManager.JeepneyInfo;

import java.time.LocalDateTime;

/**
 * Manages the user's current trip state
 * Tracks whether user is actively on a route or just browsing
 */
public class StateManager {
    private TripState currentState = TripState.BROWSING;
    private ActiveTrip activeTrip = null;

    public enum TripState {
        BROWSING,      // Normal mode - can search and view routes
        IN_TRANSIT,    // On a jeepney - limited interactions
        COMPLETED      // Trip finished - ready to return to browsing
    }

    public static class ActiveTrip {
        private final RouteComponent route;
        private final JeepneyInfo jeepney;
        private final LocalDateTime boardedAt;
        private final String fromLocation;
        private final String toLocation;

        public ActiveTrip(RouteComponent route, JeepneyInfo jeepney,
                          String from, String to) {
            this.route = route;
            this.jeepney = jeepney;
            this.boardedAt = LocalDateTime.now();
            this.fromLocation = from;
            this.toLocation = to;
        }

        public RouteComponent getRoute() { return route; }
        public JeepneyInfo getJeepney() { return jeepney; }
        public LocalDateTime getBoardedAt() { return boardedAt; }
        public String getFromLocation() { return fromLocation; }
        public String getToLocation() { return toLocation; }

        public int getMinutesInTransit() {
            return (int) java.time.Duration.between(boardedAt, LocalDateTime.now())
                    .toMinutes();
        }
    }

    // State transitions
    public void startTrip(RouteComponent route, JeepneyInfo jeepney,
                          String from, String to) {
        this.activeTrip = new ActiveTrip(route, jeepney, from, to);
        this.currentState = TripState.IN_TRANSIT;
    }

    public void completeTrip() {
        this.currentState = TripState.COMPLETED;
        // Keep activeTrip for history/receipt display
    }

    public void returnToBrowsing() {
        this.activeTrip = null;
        this.currentState = TripState.BROWSING;
    }

    // State queries
    public boolean isInTransit() {
        return currentState == TripState.IN_TRANSIT;
    }

    public boolean isBrowsing() {
        return currentState == TripState.BROWSING;
    }

    public boolean hasActiveTrip() {
        return activeTrip != null;
    }

    public ActiveTrip getActiveTrip() {
        return activeTrip;
    }

    public TripState getCurrentState() {
        return currentState;
    }
}