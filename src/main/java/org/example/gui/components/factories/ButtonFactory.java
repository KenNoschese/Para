package org.example.gui.components.factories;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import org.example.gui.components.base.RoundedButton;


public class ButtonFactory {
    public static RoundedButton create(String text, Font font, Color bg, Color fg, int width, int height, int arc) {
        RoundedButton button = new RoundedButton(text, arc);
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

