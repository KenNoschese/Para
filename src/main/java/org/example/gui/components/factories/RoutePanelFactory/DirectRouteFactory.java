package org.example.gui.components.factories.RoutePanelFactory;

import org.example.DatabaseManager.RouteDatabase.RouteComponent;
import org.example.gui.components.panels.DirectRoutePanel;
import org.example.gui.components.panels.RoutePanel;
import java.util.function.Consumer;


//Concrete Factory for Direct Routes
//Implements the factory method to create DirectRoutePanel

public class DirectRouteFactory extends RouteFactory {

    @Override
    public RoutePanel createRoutePanel(
            RouteComponent routeData,
            Consumer<?> onClick
    ) throws Exception {
        @SuppressWarnings("unchecked")
        Consumer<RouteComponent> typedOnClick = (Consumer<RouteComponent>) onClick;

        return new DirectRoutePanel(routeData, typedOnClick);
    }
}