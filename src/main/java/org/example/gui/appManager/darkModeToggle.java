package org.example.gui.appManager;

import javax.swing.*;
import java.awt.*;

public class darkModeToggle extends JPanel {
    private final JToggleButton togButt;

    public darkModeToggle() {
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        setOpaque(false);

        togButt = new JToggleButton();
        togButt.addActionListener(e -> {
            ThemeManager.getInstance().toggleDark();
            updateButtonAppearance();
        });
        add(togButt);
        togButt.setPreferredSize(new Dimension(30,30));
        togButt.setBackground(ThemeManager.getInstance().getRed());
        togButt.setBorder(BorderFactory.createEmptyBorder());

        updateButtonAppearance();
    }

    private void updateButtonAppearance() {
        boolean isDarkMode = ThemeManager.getInstance().isDarkMode();
        togButt.setText(isDarkMode ? "\u2600" : "\uD83C\uDF19");
        togButt.setFont(new Font("Noto Color Emoji", Font.PLAIN, 16));
    }


}