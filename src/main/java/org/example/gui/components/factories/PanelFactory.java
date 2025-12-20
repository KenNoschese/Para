package org.example.gui.components.factories;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;

import org.example.gui.components.base.RoundedPanel;


public class PanelFactory {
    public static RoundedPanel create(Color bg, int width, int height, int arc) {
        RoundedPanel panel = new RoundedPanel(arc);
        if (width > 0 && height > 0) {
            panel.setPreferredSize(new Dimension(width, height));
            panel.setMaximumSize(new Dimension(width, height));
            panel.setMinimumSize(new Dimension(width, height));
        }
        panel.setBackground(bg != null ? bg : new Color(0, 0, 0, 0)); 
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); 
        return panel;
    }
}
