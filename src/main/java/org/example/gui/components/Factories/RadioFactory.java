package org.example.gui.components.Factories;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JRadioButton;

public class RadioFactory {
    public static JRadioButton create(String text, Font font, ActionListener listener) {
        JRadioButton radio = new JRadioButton(text);
        radio.setFont(font);
        radio.setAlignmentX(Component.LEFT_ALIGNMENT);
        radio.addActionListener(listener);
        return radio;
    }
}
