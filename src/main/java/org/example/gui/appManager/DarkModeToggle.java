package org.example.gui.appManager;

import org.example.gui.components.base.RoundedButton;

import javax.swing.*;
import java.awt.*;

public class DarkModeToggle extends JPanel {
    private final RoundedButton togButt;

    public DarkModeToggle() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        togButt = new RoundedButton("☾");
        togButt.setArc(20, 20);
        togButt.setPreferredSize(new Dimension(30, 30));
        togButt.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        togButt.setFocusable(false);
        togButt.setBorder(BorderFactory.createEmptyBorder());

        updateButtonAppearance();

        togButt.addActionListener(e -> {
            ThemeManager.getInstance().toggleDark();
            updateButtonAppearance();
        });

        add(togButt);
    }

    private void updateButtonAppearance() {
        boolean isDarkMode = ThemeManager.getInstance().isDarkMode();

        togButt.setText(isDarkMode ? "☀" : "☾");
        togButt.setForeground(isDarkMode ? ThemeManager.getInstance().getWhite(): ThemeManager.getInstance().getBlack());
        togButt.setBackground(isDarkMode ? ThemeManager.getInstance().getBlack() : ThemeManager.getInstance().getWhite()); // yellow or navy
    }
}
