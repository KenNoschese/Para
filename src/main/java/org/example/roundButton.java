package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class roundButton extends JButton {

    private int arcWidth = 30;
    private int arcHeight = 30;

    public roundButton(String label) {
        super(label);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arcWidth, arcHeight));

        super.paintComponent(g2);
        g2.dispose();
    }


    public void setArc(int width, int height) {
        this.arcWidth = width;
        this.arcHeight = height;
        repaint();
    }
}