package org.example.components;

import org.example.config.appTheme;
import org.example.resources.fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import static org.example.resources.fonts.loadCustomFont;

public class FooterPanel extends JPanel{
    public FooterPanel() throws IOException, FontFormatException {
        setPreferredSize(appTheme.FOOTER_SIZE);
        setBackground(appTheme.RED);
        setLayout(new FlowLayout(FlowLayout.LEFT, 30, 10));

        JButton about = new JButton("About Us");
        about.setPreferredSize(new Dimension(100, 40));
        about.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 14f));
        about.setBorder(BorderFactory.createEmptyBorder());
        about.setOpaque(false);
        about.setContentAreaFilled(false);
        about.setBorderPainted(false);
        about.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                about.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                about.setForeground(Color.BLACK);
            }
        });

        JButton faq = new JButton("FAQ");
        faq.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 14f));
        faq.setOpaque(false);
        faq.setContentAreaFilled(false);
        faq.setBorderPainted(false);
        faq.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                faq.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                faq.setForeground(Color.BLACK);
            }
        });

        JLabel copyright = new JLabel("© 2025 Para! - All Rights Reserved");
        copyright.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 12f));

        add(copyright);
        add(faq);
        add(about);
    }
}
