package org.example.gui.components;

import org.example.gui.config.appTheme;
import org.example.gui.resources.fonts;

import java.awt.*;

public class RoutePanel extends roundPanel {
    private String fromLocation;
    private String toLocation;
    private String duration;
    private String transportMode;
    private Color accentColor;

    public RoutePanel() {
        this("Toril", "Roxas", "45min", "Bus", appTheme.RED);
    }

    public RoutePanel(String fromLocation, String toLocation, String duration, String transportMode, Color accentColor) {
        super(appTheme.BORDER_RADIUS_SMALL);
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.duration = duration;
        this.transportMode = transportMode;
        this.accentColor = accentColor;

        setupPanel();
    }

    private void setupPanel() {
        setPreferredSize(new Dimension(300, 80));
        setBackground(appTheme.WHITE);
        setLayout(null); // Using absolute positioning for precise control
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        try {
            Font titleFont = fonts.loadCustomFont(fonts.DM_SANS_BOLD, appTheme.TEXT_MEDIUM);
            Font subtitleFont = fonts.loadCustomFont(fonts.DM_SANS_REGULAR, appTheme.TEXT_SMALL);
            Font durationFont = fonts.loadCustomFont(fonts.DM_SANS_BOLD, appTheme.TEXT_LARGE);

            drawContent(g2d, titleFont, subtitleFont, durationFont);
        } catch (Exception e) {
            Font titleFont = new Font("Arial", Font.BOLD, 14);
            Font subtitleFont = new Font("Arial", Font.PLAIN, 12);
            Font durationFont = new Font("Arial", Font.BOLD, 16);

            drawContent(g2d, titleFont, subtitleFont, durationFont);
        }

        g2d.dispose();
    }

    private void drawContent(Graphics2D g2d, Font titleFont, Font subtitleFont, Font durationFont) {
        g2d.setColor(accentColor);
        g2d.setStroke(new BasicStroke(4f));
        g2d.drawLine(8, 12, 8, getHeight() - 12);

        drawTransportIcon(g2d, 20, 15);

        g2d.setColor(appTheme.BLACK);
        g2d.setFont(titleFont);
        g2d.drawString(fromLocation + " → " + toLocation, 50, 25);

        g2d.setColor(appTheme.GRAY);
        g2d.setFont(subtitleFont);
        g2d.drawString(transportMode + " • 1 Transfer", 50, 45);

        g2d.setColor(accentColor);
        g2d.setFont(durationFont);
        FontMetrics fm = g2d.getFontMetrics();
        String durationText = "~" + duration;
        int textWidth = fm.stringWidth(durationText);
        g2d.drawString(durationText, getWidth() - textWidth - appTheme.SPACING_SMALL, 35);
    }

    private void drawTransportIcon(Graphics2D g2d, int x, int y) {
        // Simple circular icon with transport mode letter
        g2d.setColor(accentColor);
        g2d.fillOval(x, y, 20, 20);

        g2d.setColor(appTheme.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g2d.getFontMetrics();
        String letter = transportMode.substring(0, 1).toUpperCase();
        int letterX = x + (20 - fm.stringWidth(letter)) / 2;
        int letterY = y + (20 + fm.getAscent()) / 2 - 2;
        g2d.drawString(letter, letterX, letterY);
    }

    public void setRoute(String from, String to) {
        this.fromLocation = from;
        this.toLocation = to;
        repaint();
    }

    public void setDuration(String duration) {
        this.duration = duration;
        repaint();
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
        repaint();
    }

    public void setAccentColor(Color accentColor) {
        this.accentColor = accentColor;
        repaint();
    }

    public String getFromLocation() {
        return fromLocation;
    }
    public String getToLocation() {
        return toLocation;
    }
    public String getDuration() {
        return duration;
    }
    public String getTransportMode() {
        return transportMode;
    }
    public Color getAccentColor() {
        return accentColor;
    }

}