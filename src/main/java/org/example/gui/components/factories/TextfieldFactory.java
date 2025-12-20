package org.example.gui.components.factories;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import org.example.gui.components.base.RoundedTextField;


public class TextfieldFactory {
    public static RoundedTextField create(String placeholder, int width, int height, int arc, Font font, Color bg, Color fg) {
        RoundedTextField field = new RoundedTextField(arc);
        field.setPreferredSize(new Dimension(width, height));
        field.setMaximumSize(new Dimension(width, height));
        field.setBackground(bg);
        field.setForeground(fg);
        field.setFont(font);
        field.setPlaceholder(placeholder);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }
}
