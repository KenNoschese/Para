package org.example.gui.components.Factories.RoutePanelFactory;

import org.example.DatabaseManager.RouteDatabase.RouteComponent;
import org.example.gui.components.panels.RoutePanel;
import java.util.function.Consumer;

// Abstract Factory - Contains the Factory Method
// Subclasses implement createRoutePanel() to create specific panel types
public abstract class RouteFactory {

    // implemented by subclass
    public abstract RoutePanel createRoutePanel(
            RouteComponent routeData,
            Consumer<?> onClick
    ) throws Exception;
}