package org.example.DatabaseManager.RouteDatabase.ObserversClasses;

//a concrete observer that prints jeepney updates to the console

public class JeepneyDisplayObserver implements JeepneyObserver {
    private final String observerName;

    public JeepneyDisplayObserver(String name) {
        this.observerName = name;
    }

    @Override
    public void update(String plateNumber, int currentPassengers, int capacity) {
        System.out.printf("[%s] Update → Jeep %s now has %d/%d passengers.%n",
                observerName, plateNumber, currentPassengers, capacity);
    }
}

