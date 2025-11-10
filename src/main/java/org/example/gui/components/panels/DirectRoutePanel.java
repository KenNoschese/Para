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
import java.awt.FontFormatException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.function.Consumer;

import static org.example.gui.resources.Fonts.loadCustomFont;

// concrete Product - Direct Route Panel (single jeepney, no transfers)

public class DirectRoutePanel implements RoutePanel {
    private final RouteComponent routeData;
    private final Consumer<RouteComponent> onClick;
    private final RoundedPanel panel;
    private final ThemeManager themeManager;
    private final RouteManager routeManager;

    private JPanel jeepneyPanel;  // Store reference for refresh

    public DirectRoutePanel(RouteComponent routeData, Consumer<RouteComponent> onClick)
            throws IOException, FontFormatException {
        this.routeData = routeData;
        this.onClick = onClick;
        this.themeManager = ThemeManager.getInstance();
        this.routeManager = new RouteManager();
        this.panel = createPanel();
    }

    private RoundedPanel createPanel() throws IOException, FontFormatException {
        Color defaultColor = themeManager.getPanelColor();
        Color hoverColor = themeManager.getYellow().brighter();

        RoundedPanel routePanel = new RoundedPanel(SizeManager.getInstance().getBorderRadiusLarge());
        routePanel.setLayout(new BorderLayout(15, 10));
        routePanel.setBackground(defaultColor);
        routePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        routePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        routePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        routePanel.setOpaque(true);

        routePanel.add(createRouteInfoPanel(), BorderLayout.WEST);

        jeepneyPanel = createJeepneyPanel();
        routePanel.add(jeepneyPanel, BorderLayout.EAST);

        routePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                routePanel.setBackground(hoverColor);
                routePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                routePanel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                routePanel.setBackground(defaultColor);
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
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel routeTitle = new JLabel(routeData.getRoute());
        routeTitle.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 16));
        routeTitle.setForeground(themeManager.getBlack());
        routeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(routeTitle);
        infoPanel.add(Box.createVerticalStrut(5));

        JLabel detailsLabel = new JLabel(String.format(
                "<html><b>Details:</b> %s</html>", routeData.getDetails()
        ));
        detailsLabel.setFont(loadCustomFont(Fonts.DM_SANS_REGULAR, 14));
        detailsLabel.setForeground(themeManager.getBlack());
        detailsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(detailsLabel);
        infoPanel.add(Box.createVerticalStrut(5));

        JLabel infoLabel = new JLabel(String.format(
                "<html><b>Transfers:</b> %d <b>| Stops:</b> %d <b>| ETA:</b> %d min</html>",
                routeData.getTransfers(), routeData.getStops(), routeData.getEta()
        ));
        infoLabel.setFont(loadCustomFont(Fonts.DM_SANS_REGULAR, 13));
        infoLabel.setForeground(themeManager.getForegroundColor());
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(infoLabel);

        JLabel fareLabel = new JLabel(String.format("Php %.2f", routeData.getFare()));
        fareLabel.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 14));
        fareLabel.setForeground(themeManager.getBlack());
        fareLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(fareLabel);

        return infoPanel;
    }

    private JPanel createJeepneyPanel() throws IOException, FontFormatException {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentY(Component.TOP_ALIGNMENT);

        panel.setMinimumSize(new Dimension(200, 100));
        panel.setMaximumSize(new Dimension(200, 100));

        try (Connection conn = DatabaseInstance.getInstance().getConnection()) {
            ArrayList<RouteManager.JeepneyInfo> jeepneys =
                    routeManager.getJeepneysForRoute(routeData.getRoute(), conn);

            if (!jeepneys.isEmpty()) {
                System.out.println("📊 Jeepneys found for route " + routeData.getRoute() + ": " + jeepneys.size());
                JLabel header = new JLabel("Available Jeepneys:");
                header.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 12));
                header.setForeground(themeManager.getBlack());
                panel.add(header);
                panel.add(Box.createVerticalStrut(5));

                int count = Math.min(3, jeepneys.size());
                for (int i = 0; i < count; i++) {
                    panel.add(createJeepneyLabel(jeepneys.get(i)));
                }

                if (jeepneys.size() > 3) {
                    JLabel more = new JLabel("+" + (jeepneys.size() - 3) + " more");
                    more.setFont(loadCustomFont(Fonts.DM_SANS_ITALIC, 10));
                    more.setForeground(themeManager.getGray());
                    panel.add(more);
                }
            } else {
                System.out.println("no jeep");
                JLabel noJeepneys = new JLabel("No jeepneys available");
                noJeepneys.setFont(loadCustomFont(Fonts.DM_SANS_ITALIC, 11));
                noJeepneys.setForeground(themeManager.getGray());
                panel.add(noJeepneys);
            }
        } catch (Exception e) {
            JLabel error = new JLabel("Error loading jeepneys");
            error.setFont(loadCustomFont(Fonts.DM_SANS_ITALIC, 11));
            error.setForeground(Color.RED);
            panel.add(error);
        }

        return panel;
    }

    private JLabel createJeepneyLabel(RouteManager.JeepneyInfo jeep)
            throws IOException, FontFormatException {
        String status = jeep.isFull() ? " [FULL]" :
                jeep.getAvailableSeats() <= 5 ? " [Almost Full]" : "";
        Color color = jeep.isFull() ? Color.RED :
                jeep.getAvailableSeats() <= 5 ? Color.ORANGE :
                        themeManager.getGreen();

        JLabel label = new JLabel(String.format(
                "<html>%s: <b>%d/%d</b>%s</html>",
                jeep.getPlateNumber(), jeep.getCurrentPassengers(),
                jeep.getCapacity(), status
        ));
        label.setFont(loadCustomFont(Fonts.DM_SANS_REGULAR, 11));
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
}