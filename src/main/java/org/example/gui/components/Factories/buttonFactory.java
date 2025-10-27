package org.example.gui.components.Factories;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import org.example.gui.components.RoundingOfButtons;


public class buttonFactory {

    public static RoundingOfButtons create(String text, int width, int height, int arc, Font font, Color bg, Color fg) {
        RoundingOfButtons button = new RoundingOfButtons(text, arc);
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setFont(font);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    public static RoundingOfButtons create(String text, int width, int height, int arcX, int arcY, Font font, Color bg, Color fg) {
        RoundingOfButtons button = new RoundingOfButtons(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setFont(font);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setArc(arcX, arcY);
        return button;
    }

    public static RoundingOfButtons create(String text, int width, int height, Color bg, Color fg) {
        RoundingOfButtons button = new RoundingOfButtons(text, 0);
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    public static RoundingOfButtons create(String text, Color bg, Color fg) {
        RoundingOfButtons button = new RoundingOfButtons(text, 0);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        return button;
    }

    public static RoundingOfButtons create(String text, Font font, Color bg, Color fg, int width, int height, int arc) {
        RoundingOfButtons button = new RoundingOfButtons(text, arc);
        button.setFont(font);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }
}

