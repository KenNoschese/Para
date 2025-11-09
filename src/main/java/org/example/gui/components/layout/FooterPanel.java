package org.example.gui.components.layout;

import org.example.gui.appManager.ThemeManager;
import org.example.gui.appManager.SizeManager;
import org.example.gui.resources.Fonts;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static org.example.gui.resources.Fonts.loadCustomFont;

public class FooterPanel {
    public static JPanel createFooterPanel() throws IOException, FontFormatException {
        JPanel footerPanel = new JPanel();
        footerPanel.setPreferredSize(SizeManager.getInstance().getFooterSize());
        footerPanel.setBackground(ThemeManager.getInstance().getBackgroundColor());
        footerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 10));

        JLabel copyright = new JLabel("© 2025 Para! - All Rights Reserved");
        copyright.setFont(loadCustomFont(Fonts.DM_SANS_REGULAR, 12f));

        footerPanel.add(copyright);
        return footerPanel;
    }
}
