package org.example.gui.components.base;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedPasswordField extends JPasswordField {
    private int arcWidth = 30;
    private int arcHeight = 30;
    private Color borderColor = Color.WHITE;
    private Color focusBorderColor = new Color(100, 150, 255);
    private boolean isFocused = false;
    private String placeholder = "";
    private Color placeholderColor = new Color(160, 160, 160);

    public RoundedPasswordField(int columns) {
        super(columns);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setEchoChar('•'); // bullet style

        addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // background fill
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight));
        g2.dispose();

        super.paintComponent(g);

        // placeholder logic
        if (!isFocused && getPassword().length == 0 && placeholder != null && !placeholder.isEmpty()) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(placeholderColor);

            Insets insets = getInsets();
            FontMetrics fm = g2d.getFontMetrics();
            int x = insets.left + 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

            g2d.drawString(placeholder, x, y);
            g2d.dispose();
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(isFocused ? focusBorderColor : borderColor);
        g2.setStroke(new BasicStroke(isFocused ? 2.0f : 1.0f));
        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight));
        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        int minWidth = getColumnWidth() * getColumns() + 24;
        return new Dimension(Math.max(size.width, minWidth), Math.max(size.height, 30));
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(100, 30);
    }

    // === Setters and Getters ===
    public void setArc(int width, int height) {
        this.arcWidth = Math.min(width, getHeight() - 2);
        this.arcHeight = Math.min(height, getHeight() - 2);
        repaint();
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }

    public void setFocusBorderColor(Color color) {
        this.focusBorderColor = color;
        repaint();
    }

    public void setPlaceholder(String text) {
        this.placeholder = text;
        repaint();
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholderColor(Color color) {
        this.placeholderColor = color;
        repaint();
    }
}