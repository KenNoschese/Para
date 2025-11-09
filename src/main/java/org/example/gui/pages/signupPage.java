package org.example.gui.pages;

import org.example.DatabaseManager.UserDatabase.UserManager;
import org.example.gui.appManager.ThemeManager;
import org.example.gui.components.RoundingOfPasswordField;
import org.example.gui.resources.Images;
import org.example.gui.resources.fonts;
import org.example.gui.appManager.sizeManager;
import org.example.gui.components.RoundingOfButtons;
import org.example.gui.components.RoundingOfTextfields;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.function.Consumer;

public class signupPage extends JPanel {
    private final Consumer<String> cardChanger;

    public signupPage(Consumer<String> cardChanger) throws IOException, FontFormatException {
        this.cardChanger = cardChanger;
        setupPanel();
    }

    private void setupPanel() throws IOException, FontFormatException {
        ThemeManager themeManager = ThemeManager.getInstance();

        setLayout(new BorderLayout());
        setBackground(themeManager.getWhite());
        setBorder(BorderFactory.createEmptyBorder(50, 0, 100, 0));

        JLabel logo = Images.getInstance().getParaLogoLabel(250, 250);
        add(logo, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(themeManager.getWhite());

        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(themeManager.getWhite());

        JLabel titleLabel = new JLabel("Create Your Account", SwingConstants.CENTER);
        titleLabel.setFont(fonts.loadCustomFont(fonts.DM_SANS_BOLD, 18f));
        titleLabel.setForeground(themeManager.getBlack());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username Field
        RoundingOfTextfields usernameField = new RoundingOfTextfields(20);
        usernameField.setMaximumSize(new Dimension(400, 45));
        usernameField.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        usernameField.setForeground(themeManager.getBlack());
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setBorderColor(themeManager.getGray());
        usernameField.setPlaceholder("Username");

        // Password Field
        RoundingOfPasswordField passField = new RoundingOfPasswordField(20);
        passField.setMaximumSize(new Dimension(400, 45));
        passField.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        passField.setForeground(themeManager.getBlack());
        passField.setBackground(themeManager.getWhite());
        passField.setBorderColor(themeManager.getGray());
        passField.setPlaceholder("Password");
        passField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Confirm Password Field
        RoundingOfPasswordField confirmPassField = new RoundingOfPasswordField(20);
        confirmPassField.setMaximumSize(new Dimension(400, 45));
        confirmPassField.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        confirmPassField.setForeground(themeManager.getBlack());
        confirmPassField.setBackground(themeManager.getWhite());
        confirmPassField.setBorderColor(themeManager.getGray());
        confirmPassField.setPlaceholder("Confirm Password");
        confirmPassField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create Account Button
        JButton createButton = getCreateButton(usernameField, passField, confirmPassField);
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Back Button
        RoundingOfButtons backButton = new RoundingOfButtons("Back to Login");
        backButton.setArc(30, 30);
        backButton.setMaximumSize(new Dimension(400, 45));
        backButton.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        backButton.setForeground(themeManager.getWhite());
        backButton.setBackground(themeManager.getRed());
        backButton.setBorder(BorderFactory.createEmptyBorder());
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> cardChanger.accept("LOGIN"));

        // Add all
        formContent.add(titleLabel);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingLarge()));
        formContent.add(usernameField);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingMedium()));
        formContent.add(passField);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingMedium()));
        formContent.add(confirmPassField);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingLarge()));
        formContent.add(createButton);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingLarge()));
        formContent.add(backButton);

        formPanel.add(formContent, BorderLayout.CENTER);
        add(formPanel, BorderLayout.CENTER);
    }

    private JButton getCreateButton(JTextField usernameField, JTextField passField, JTextField confirmPassField) {
        ThemeManager themeManager = ThemeManager.getInstance();
        RoundingOfButtons button = new RoundingOfButtons("Create Account");
        button.setArc(30, 30);
        button.setMaximumSize(new Dimension(400, 45));
        try { button.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f)); } catch (Exception ignored) {}
        button.setForeground(themeManager.getWhite());
        button.setBackground(themeManager.getBlack());
        button.setBorder(BorderFactory.createEmptyBorder());

        button.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(((JPasswordField) passField).getPassword()).trim();
            String confirm = new String(((JPasswordField) confirmPassField).getPassword()).trim();

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = new UserManager().signUpUser(username, password);

            if (success) {
                JOptionPane.showMessageDialog(this, "Account created! Please log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                cardChanger.accept("LOGIN");
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return button;
    }
}