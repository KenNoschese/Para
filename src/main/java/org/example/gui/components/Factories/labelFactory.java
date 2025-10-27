package org.example.gui.components.Factories;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;



public class labelFactory {
    public static JLabel create(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
}
