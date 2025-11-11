package org.example.gui.components.panels;

import org.example.DatabaseManager.DatabaseInstance;
import org.example.DatabaseManager.RouteDatabase.RouteComponent;
import org.example.DatabaseManager.RouteDatabase.RouteManager;
import org.example.gui.appManager.SizeManager;
import org.example.gui.appManager.ThemeManager;
import org.example.gui.components.base.RoundedPanel;
import org.example.gui.resources.Fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.example.gui.resources.Fonts.loadCustomFont;

public class TransferRoutePanel implements RoutePanel {
    private final List<RouteComponent> segments;
    private final Consumer<List<RouteComponent>> onClick;
    private final RoundedPanel panel;
    private final ThemeManager themeManager;
    private final RouteManager routeManager;
    private final List<JPanel> jeepneyPanels; // Store references for refresh

    public TransferRoutePanel(List<RouteComponent> segments, Consumer<List<RouteComponent>> onClick)
            throws IOException, FontFormatException {
        this.segments = segments;
        this.onClick = onClick;
        this.themeManager = ThemeManager.getInstance();
        this.routeManager = new RouteManager();
        this.jeepneyPanels = new ArrayList<>();
        this.panel = createPanel();
    }

    private RoundedPanel createPanel() throws IOException, FontFormatException {
        Color defaultColor = themeManager.getPanelColor();
        Color hoverColor = themeManager.getYellow().brighter();

        RoundedPanel panel = new RoundedPanel(30);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(defaultColor);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        panel.setOpaque(true);

        JLabel title = new JLabel("Transfer Route (" + segments.size() + " segments)");
        title.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 16));
        title.setForeground(themeManager.getBlack());
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        double totalFare = 0.0;
        int totalEta = 0;

        // Display each segment with jeepney info
        for (int i = 0; i < segments.size(); i++) {
            RouteComponent seg = segments.get(i);

            JPanel segmentContainer = new JPanel();
            segmentContainer.setLayout(new BorderLayout(10, 0));
            segmentContainer.setOpaque(false);
            segmentContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            // Segment info (left side)
            JPanel segInfo = new JPanel();
            segInfo.setLayout(new BoxLayout(segInfo, BoxLayout.Y_AXIS));
            segInfo.setOpaque(false);

            JLabel segLabel = new JLabel(String.format(
                    "Segment %d: %s",
                    i + 1, seg.getRoute()
            ));
            segLabel.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 14));
            segLabel.setForeground(themeManager.getBlack());
            segLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel segRoute = new JLabel(String.format("%s → %s | Php %.2f | %d min",
                    seg.getFromLocation(), seg.getDestination(), seg.getFare(), seg.getEta()));
            segRoute.setFont(loadCustomFont(Fonts.DM_SANS_REGULAR, 12));
            segRoute.setForeground(themeManager.getBlack().brighter());
            segRoute.setAlignmentX(Component.LEFT_ALIGNMENT);

            segInfo.add(segLabel);
            segInfo.add(Box.createVerticalStrut(3));
            segInfo.add(segRoute);

            // Jeepney info (right side)
            JPanel jeepPanel = createJeepneyPanel(seg);
            jeepneyPanels.add(jeepPanel);

            segmentContainer.add(segInfo, BorderLayout.CENTER);
            segmentContainer.add(jeepPanel, BorderLayout.EAST);

            panel.add(segmentContainer);
            panel.add(Box.createVerticalStrut(8));

            totalFare += seg.getFare();
            totalEta += seg.getEta();
        }

        panel.add(Box.createVerticalStrut(5));

        JLabel totalLabel = new JLabel(String.format(
                "Total: Php %.2f | %d min | %d transfers",
                totalFare, totalEta, segments.size() - 1
        ));
        totalLabel.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 14));
        totalLabel.setForeground(themeManager.getForegroundColor());
        panel.add(totalLabel);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(hoverColor);
                panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                panel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(defaultColor);
                panel.setCursor(Cursor.getDefaultCursor());
                panel.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) onClick.accept(segments);
            }
        });

        return panel;
    }

    private JPanel createJeepneyPanel(RouteComponent segment) throws IOException, FontFormatException {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.setMinimumSize(new Dimension(180, 50));
        panel.setMaximumSize(new Dimension(180, 50));

        try (Connection conn = DatabaseInstance.getInstance().getConnection()) {
            ArrayList<RouteManager.JeepneyInfo> jeepneys =
                    routeManager.getJeepneysForRoute(segment.getRoute());

            if (!jeepneys.isEmpty()) {
                JLabel header = new JLabel("Available:");
                header.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 11));
                header.setForeground(themeManager.getBlack());
                panel.add(header);
                panel.add(Box.createVerticalStrut(3));

                int count = Math.min(2, jeepneys.size());
                for (int i = 0; i < count; i++) {
                    panel.add(createJeepneyLabel(jeepneys.get(i)));
                }

                if (jeepneys.size() > 2) {
                    JLabel more = new JLabel("+" + (jeepneys.size() - 2) + " more");
                    more.setFont(loadCustomFont(Fonts.DM_SANS_ITALIC, 10));
                    more.setForeground(themeManager.getGray());
                    panel.add(more);
                }
            } else {
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
                jeep.getAvailableSeats() <= 5 ? " [Low]" : "";
        Color color = jeep.isFull() ? Color.RED :
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
        return segments.isEmpty() ? null : segments.get(0);
    }

    @Override
    public void refresh() {
        try {
            // Refresh each jeepney panel for each segment
            for (int i = 0; i < segments.size() && i < jeepneyPanels.size(); i++) {
                JPanel oldPanel = jeepneyPanels.get(i);
                Container parent = oldPanel.getParent();

                if (parent != null) {
                    JPanel newPanel = createJeepneyPanel(segments.get(i));
                    jeepneyPanels.set(i, newPanel);

                    parent.remove(oldPanel);
                    parent.add(newPanel, BorderLayout.EAST);
                }
            }

            panel.revalidate();
            panel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}