package org.example.gui.components.panels;

import org.example.DatabaseManager.RouteDatabase.RouteComponent;
import javax.swing.JPanel;

//product interface - defines what all route panels must provide ( product of the factory method)

public interface RoutePanel {
    JPanel getPanel();
    RouteComponent getRouteData();
    void refresh();
}