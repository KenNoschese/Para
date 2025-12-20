package org.example.DatabaseManager.RouteDatabase.ObserversClasses;

/**
 * Observer interface — any class implementing this will receive jeepney updates.
 */
public interface JeepneyObserver {
    void update(String plateNumber, int currentPassengers, int capacity);
}