package org.example.gui.components.elements;

import org.example.DatabaseManager.DatabaseInstance;
import org.example.gui.appManager.ThemeManager;
import org.example.gui.components.base.RoundedButton;
import org.example.gui.resources.Fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import static org.example.gui.resources.Fonts.loadCustomFont;

public class UserButton {
    public static RoundedButton createUserButton() throws IOException, FontFormatException {
        ThemeManager themeManager = ThemeManager.getInstance();
        String username = DatabaseInstance.getCurrentAppUser();
        if (username == null || username.isEmpty()) username = "Guest";
        final String displayName = username;

        RoundedButton userButton = new RoundedButton(displayName);
        userButton.setFont(loadCustomFont(Fonts.DM_SANS_REGULAR, 13));
        userButton.setFocusPainted(false);
        userButton.setBorderPainted(false);
        userButton.setBackground(new Color(255, 255, 255, 180));
        userButton.setForeground(themeManager.getBlack());
        userButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userButton.setBounds(15, 10, 220, 30);
        userButton.setHorizontalAlignment(SwingConstants.LEFT);

        userButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                userButton.setBackground(themeManager.getYellow());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                userButton.setBackground(new Color(255, 255, 255, 180));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                JPopupMenu menu = new JPopupMenu();
                JMenuItem profileItem = new JMenuItem("Profile");
                profileItem.addActionListener(evt -> JOptionPane.showMessageDialog(null,
                        "Username: " + displayName + "\n(Profile feature coming soon)",
                        "User Profile", JOptionPane.INFORMATION_MESSAGE));
                JMenuItem logoutItem = new JMenuItem("Logout");
                logoutItem.addActionListener(evt -> {
                    JOptionPane.showMessageDialog(null, "You have been logged out.", "Logout", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                });
                menu.add(profileItem);
                menu.addSeparator();
                menu.add(logoutItem);
                menu.show(userButton, 0, userButton.getHeight());
            }
        });
        return userButton;
    }
}
