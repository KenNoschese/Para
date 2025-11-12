package org.example.gui.components.panels;

import org.example.DatabaseManager.DatabaseInstance;
import org.example.DatabaseManager.RouteDatabase.RouteComponent;
import org.example.DatabaseManager.RouteDatabase.RouteManager;
import org.example.gui.appManager.SizeManager;
import org.example.gui.appManager.ThemeManager;
import org.example.gui.components.base.RoundedPanel;
import org.example.gui.resources.Fonts;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.example.gui.resources.Fonts.loadCustomFont;

public class DirectRoutePanel implements RoutePanel, ThemeManager.ThemeChangeListener {
    private final RouteComponent routeData;
    private final Consumer<RouteComponent> onClick;
    private final RoundedPanel panel;
    private final ThemeManager themeManager;
    private final RouteManager routeManager;

    private JPanel jeepneyPanel;
    private boolean isHovered = false;

    public DirectRoutePanel(RouteComponent routeData, Consumer<RouteComponent> onClick)
            throws IOException, FontFormatException {
        this.routeData = routeData;
        this.onClick = onClick;
        this.themeManager = ThemeManager.getInstance();
        this.routeManager = new RouteManager();
        this.panel = createPanel();

        // Register as theme listener
        this.themeManager.addThemeChangeListener(this);
    }

    private RoundedPanel createPanel() throws IOException, FontFormatException {
        RoundedPanel routePanel = new RoundedPanel(SizeManager.getInstance().getBorderRadiusLarge());
        routePanel.setLayout(new BorderLayout(15, 0));
        routePanel.setBackground(themeManager.getWhite());
        routePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        routePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        routePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        routePanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 90));
        routePanel.setOpaque(true);

        routePanel.add(createRouteInfoPanel(), BorderLayout.CENTER);

        jeepneyPanel = createJeepneyPanel();
        routePanel.add(jeepneyPanel, BorderLayout.EAST);

        routePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                routePanel.setBackground(themeManager.getYellow());
                routePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                routePanel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                routePanel.setBackground(themeManager.getWhite());
                routePanel.setCursor(Cursor.getDefaultCursor());
                routePanel.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) onClick.accept(routeData);
            }
        });

        return routePanel;
    }

    private JPanel createRouteInfoPanel() throws IOException, FontFormatException {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        // Row 1: Route name
        JLabel routeTitle = new JLabel(routeData.getRoute());
        routeTitle.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 16));
        routeTitle.setForeground(themeManager.getBlack());
        routeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(routeTitle);
        mainPanel.add(Box.createVerticalStrut(5));

        // Row 2: From -> To on left, details on right
        JPanel row2 = new JPanel(new BorderLayout());
        row2.setOpaque(false);
        row2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel fromTo = new JLabel(routeData.getFromLocation() + " → " + routeData.getDestination());
        fromTo.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 13));
        fromTo.setForeground(themeManager.getBlack());

        JLabel details = new JLabel(String.format("%d stops • %d min • Php %.2f",
                routeData.getStops(), routeData.getEta(), routeData.getFare()));
        details.setFont(loadCustomFont(Fonts.DM_SANS_REGULAR, 12));
        details.setForeground(themeManager.getBlack());

        row2.add(fromTo, BorderLayout.WEST);
        row2.add(details, BorderLayout.EAST);

        mainPanel.add(row2);

        return mainPanel;
    }

    private JPanel createJeepneyPanel() throws IOException, FontFormatException {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentY(Component.CENTER_ALIGNMENT);

        panel.setMinimumSize(new Dimension(180, 60));
        panel.setMaximumSize(new Dimension(180, 60));
        panel.setPreferredSize(new Dimension(180, 60));

        try (Connection conn = DatabaseInstance.getInstance().getConnection()) {
            ArrayList<RouteManager.JeepneyInfo> jeepneys =
                    routeManager.getJeepneysForRoute(routeData.getRoute());

            if (!jeepneys.isEmpty()) {
                JLabel header = new JLabel("Available Jeepneys:");
                header.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 11));
                header.setForeground(themeManager.getBlack());
                header.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(header);
                panel.add(Box.createVerticalStrut(3));

                int count = Math.min(2, jeepneys.size());
                for (int i = 0; i < count; i++) {
                    JLabel jeepLabel = createJeepneyLabel(jeepneys.get(i));
                    jeepLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    panel.add(jeepLabel);
                    if (i < count - 1) {
                        panel.add(Box.createVerticalStrut(2));
                    }
                }

                if (jeepneys.size() > 2) {
                    JLabel more = new JLabel("+" + (jeepneys.size() - 2) + " more");
                    more.setFont(loadCustomFont(Fonts.DM_SANS_ITALIC, 9));
                    more.setForeground(themeManager.getGray());
                    more.setAlignmentX(Component.LEFT_ALIGNMENT);
                    panel.add(more);
                }
            } else {
                JLabel noJeepneys = new JLabel("No jeepneys available");
                noJeepneys.setFont(loadCustomFont(Fonts.DM_SANS_ITALIC, 11));
                noJeepneys.setForeground(themeManager.getGray());
                noJeepneys.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(noJeepneys);
            }
        } catch (Exception e) {
            JLabel error = new JLabel("Error loading jeepneys");
            error.setFont(loadCustomFont(Fonts.DM_SANS_ITALIC, 10));
            error.setForeground(Color.RED);
            error.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(error);
        }

        return panel;
    }

    private JLabel createJeepneyLabel(RouteManager.JeepneyInfo jeep)
            throws IOException, FontFormatException {
        String status = jeep.isFull() ? " [FULL]" :
                jeep.getAvailableSeats() <= 5 ? " [Low]" : "";
        Color color = jeep.isFull() ? ThemeManager.getInstance().getRed() :
                jeep.getAvailableSeats() <= 5 ? Color.ORANGE :
                        themeManager.getGreen();

        JLabel label = new JLabel(String.format(
                "<html>%s: <b>%d/%d</b>%s</html>",
                jeep.getPlateNumber(), jeep.getCurrentPassengers(),
                jeep.getCapacity(), status
        ));
        label.setFont(loadCustomFont(Fonts.DM_SANS_REGULAR, 10));
        label.setForeground(color);
        return label;
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public RouteComponent getRouteData() {
        return routeData;
    }

    @Override
    public void refresh() {
        try {
            panel.remove(jeepneyPanel);
            jeepneyPanel = createJeepneyPanel();
            panel.add(jeepneyPanel, BorderLayout.EAST);
            panel.revalidate();
            panel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onThemeChange(boolean isDarkMode) {
        // Update panel background based on hover state
        panel.setBackground(themeManager.getWhite());
        panel.setBackground(isHovered ? themeManager.getYellow() : themeManager.getWhite());

        // Update all child components
        themeManager.applyThemeToContainers(panel);

        panel.revalidate();
        panel.repaint();
    }

    // Clean up listener when panel is no longer needed
    public void dispose() {
        themeManager.removeThemeChangeListener(this);
    }
}