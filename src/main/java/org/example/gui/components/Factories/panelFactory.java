package org.example.gui.components.Factories;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;

import org.example.gui.components.RoundingOfPanels;


public class panelFactory {
    public static RoundingOfPanels create(Color bg, int width, int height, int arc) {
        RoundingOfPanels panel = new RoundingOfPanels(arc);
        if (width > 0 && height > 0) {
            panel.setPreferredSize(new Dimension(width, height));
        }
        panel.setBackground(bg != null ? bg : new Color(0, 0, 0, 0)); 
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); 
        return panel;
    }
}
