package org.example.gui.components.Factories.RoutePanelFactory;

import org.example.DatabaseManager.RouteDatabase.RouteComponent;
import org.example.DatabaseManager.RouteDatabase.Routes;
import org.example.gui.components.panels.RoutePanel;
import org.example.gui.components.panels.TransferRoutePanel;
import java.util.List;
import java.util.function.Consumer;

// Concrete Factory for Transfer Routes
// Implements the factory method to create TransferRoutePanel
public class TransferRouteFactory extends RouteFactory {

    @Override
    public RoutePanel createRoutePanel(
            RouteComponent routeData,
            Consumer<?> onClick
    ) throws Exception {
        List<RouteComponent> segments;
        if (routeData instanceof Routes) {
            segments = ((Routes) routeData).getSegments();
        } else {
            segments = List.of(routeData);
        }

        @SuppressWarnings("unchecked")
        Consumer<List<RouteComponent>> typedOnClick =
                (Consumer<List<RouteComponent>>) onClick;

        return new TransferRoutePanel(segments, typedOnClick);
    }
}