package org.example.gui.components;

import org.example.gui.resources.RouteData;
import org.example.gui.config.appTheme;
import org.example.gui.resources.fonts;

import java.awt.*;

public class RoutePanel extends roundPanel {
    private RouteData routeData;
    public RoutePanel() {
        this(new RouteData("Toril", "Roxas", "Bus Route 1", "d", 8, 3, "12min.", 2.11));
    }

    public RoutePanel(RouteData routeData) {
        super(appTheme.BORDER_RADIUS_LARGE);
        this.routeData = routeData;
        setupPanel();
    }

    private void setupPanel() {
        setPreferredSize(new Dimension(Integer.MAX_VALUE, 60));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        setBackground(appTheme.WHITE);
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        try {
            Font dataFont = fonts.loadCustomFont(fonts.DM_SANS_REGULAR, appTheme.TEXT_SMALL);
            drawTableContent(g2d, dataFont);
        } catch (Exception e) {
            Font dataFont = new Font("Arial", Font.PLAIN, 12);
            drawTableContent(g2d, dataFont);
        }

        g2d.dispose();
    }

    private void drawTableContent(Graphics2D g2d, Font dataFont) {
        int[] columnWidths = {150, 100, 100, 600, 100, 100};
        int[] columnX = new int[6];
        columnX[0] = 10;
        for (int i = 1; i < 6; i++) {
            columnX[i] = columnX[i-1] + columnWidths[i-1];
        }

        g2d.setFont(dataFont);
        g2d.setColor(appTheme.BLACK);

        int yPos = (getHeight() + g2d.getFontMetrics().getAscent()) / 2;

        g2d.drawString(routeData.getRoute(), columnX[0], yPos);
        g2d.drawString(String.valueOf(routeData.getTransfers()), columnX[1], yPos);
        g2d.drawString(String.valueOf(routeData.getstops()), columnX[2], yPos);
        g2d.drawString(routeData.getDetails(), columnX[3], yPos);
        g2d.drawString(String.format("Php%.2f", routeData.getFare()), columnX[4], yPos);
        g2d.drawString(String.valueOf(routeData.getETA()), columnX[5], yPos);
    }

    public void setRouteData(RouteData routeData) {
        this.routeData = routeData;
        repaint();
    }
}