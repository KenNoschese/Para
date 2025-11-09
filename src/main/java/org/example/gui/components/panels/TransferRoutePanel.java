package org.example.gui.components.panels;

import org.example.DatabaseManager.RouteDatabase.RouteComponent;
import org.example.gui.appManager.ThemeManager;
import org.example.gui.components.base.RoundedPanel;
import org.example.gui.resources.Fonts;

import javax.swing.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontFormatException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static org.example.gui.resources.Fonts.loadCustomFont;

// concrete Product - Transfer Route Panel (multiple jeepney, no transfers)
public class TransferRoutePanel implements RoutePanel {
    private final java.util.List<RouteComponent> segments;
    private final Consumer<List<RouteComponent>> onClick;
    private final RoundedPanel panel;
    private final ThemeManager themeManager;

    public TransferRoutePanel(java.util.List<RouteComponent> segments, Consumer<java.util.List<RouteComponent>> onClick)
            throws IOException, FontFormatException {
        this.segments = segments;
        this.onClick = onClick;
        this.themeManager = ThemeManager.getInstance();
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
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        panel.setOpaque(true);

        JLabel title = new JLabel("Transfer Route (" + segments.size() + " segments)");
        title.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 16));
        title.setForeground(themeManager.getBlack());
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        double totalFare = 0.0;
        double totalDistance = 0.0;

        for (int i = 0; i < segments.size(); i++) {
            RouteComponent seg = segments.get(i);

            JLabel segLabel = new JLabel(String.format(
                    "Segment %d: %s — Php %.2f, %.0f km (%s to %s)",
                    i + 1, seg.getRoute(), seg.getFare(), seg.getDistance(),
                    seg.getFromLocation(), seg.getDestination()
            ));
            segLabel.setFont(loadCustomFont(Fonts.DM_SANS_REGULAR, 14));
            segLabel.setForeground(themeManager.getBlack());
            panel.add(segLabel);

            totalFare += seg.getFare();
            totalDistance += seg.getDistance();
        }

        panel.add(Box.createVerticalStrut(10));

        JLabel totalLabel = new JLabel(String.format(
                "Total Fare: Php %.2f | Total Distance: %.0f km",
                totalFare, totalDistance
        ));
        totalLabel.setFont(loadCustomFont(Fonts.DM_SANS_REGULAR, 14));
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

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public RouteComponent getRouteData() {
        // Return first segment as representative
        return segments.isEmpty() ? null : segments.get(0);
    }

    @Override
    public void refresh() {
        // Transfer routes don't need refresh (no real-time jeepney data)
    }
}