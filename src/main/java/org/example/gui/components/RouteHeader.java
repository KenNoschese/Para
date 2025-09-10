package org.example.gui.components;

import org.example.gui.appManager.ThemeManager;
import org.example.gui.appManager.sizeManager;
import org.example.gui.resources.fonts;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class RouteHeader extends roundPanel {
    private final ThemeManager themeManager;

    public RouteHeader() {
        super(sizeManager.BORDER_RADIUS_SMALL);
        this.themeManager = ThemeManager.getInstance();
        setupPanel();
    }

    private void setupPanel() {
        setPreferredSize(new Dimension(Integer.MAX_VALUE, 60));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        setBackground(themeManager.getBackgroundColor());
        setForeground(themeManager.getForegroundColor());
        setLayout(null);

        setBorder(new LineBorder(Color.BLACK, 2));

        putClientProperty("themeColor", "panel");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        try {
            Font dataFont = fonts.loadCustomFont(fonts.DM_SANS_BOLD, sizeManager.TEXT_MEDIUM);
            drawHeader(g2d, dataFont);
        } catch (Exception e) {
            Font dataFont = new Font("Arial", Font.PLAIN, 12);
            drawHeader(g2d, dataFont);
        }

        g2d.dispose();
    }

    private void drawHeader(Graphics g2d, Font font) {
        int[] columnWidths = {150, 100, 100, 600, 100, 100};
        int[] columnX = new int[columnWidths.length];

        for (int i = 1; i < columnWidths.length; i++) {
            columnX[i] = columnX[i - 1] + columnWidths[i - 1];
        }

        g2d.setFont(font);
        g2d.setColor(themeManager.getBlack());

        int yPos = (getHeight() + g2d.getFontMetrics().getAscent()) / 2 - 4; // Center text

        g2d.drawString("Route", columnX[0] + 10, yPos);
        g2d.drawString("Transfers", columnX[1] + 10, yPos);
        g2d.drawString("Stops", columnX[2] + 10, yPos);
        g2d.drawString("Details", columnX[3] + 10, yPos);
        g2d.drawString("Fare", columnX[4] + 10, yPos);
        g2d.drawString("ETA", columnX[5] + 10, yPos);
    }
}
