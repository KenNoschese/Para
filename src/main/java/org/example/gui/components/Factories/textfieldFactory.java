package org.example.gui.components.Factories;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JTextField;

import org.example.gui.components.RoundingOfTextfields;


public class textfieldFactory {
    public static RoundingOfTextfields create(String placeholder, int width, int height, int arc, Font font, Color bg, Color fg) {
        RoundingOfTextfields field = new RoundingOfTextfields(arc);
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
