package org.example.gui.components;


import org.example.gui.appManager.ThemeManager;
import org.example.gui.appManager.sizeManager;
import org.example.gui.resources.fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import static org.example.gui.resources.fonts.loadCustomFont;

public class FooterPanel extends JPanel{
    public FooterPanel() throws IOException, FontFormatException {
        setPreferredSize(sizeManager.FOOTER_SIZE);
        setBackground(ThemeManager.getInstance().getRed());
        setLayout(new FlowLayout(FlowLayout.LEFT, 30, 10));


        JLabel copyright = new JLabel("© 2025 Para! - All Rights Reserved");
        copyright.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 12f));

        add(copyright);

    }
}
