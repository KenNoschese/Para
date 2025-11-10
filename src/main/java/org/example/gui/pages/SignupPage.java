package org.example.gui.pages;

import org.example.DatabaseManager.UserDatabase.UserManager;
import org.example.gui.appManager.ThemeManager;
import org.example.gui.components.base.RoundedPasswordField;
import org.example.gui.resources.Images;
import org.example.gui.resources.Fonts;
import org.example.gui.appManager.SizeManager;
import org.example.gui.components.base.RoundedButton;
import org.example.gui.components.base.RoundedTextField;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.function.Consumer;

public class SignupPage extends JPanel {
    private final Consumer<String> cardChanger;

    public SignupPage(Consumer<String> cardChanger) throws IOException, FontFormatException {
        this.cardChanger = cardChanger;
        setupPanel();
    }

    private void setupPanel() throws IOException, FontFormatException {
        ThemeManager themeManager = ThemeManager.getInstance();

        setLayout(new BorderLayout());
        setBackground(themeManager.getWhite());

        // Main container with horizontal split
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(themeManager.getWhite());

        // Left side - Signup form panel
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(themeManager.getWhite());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 100, 50));

        JLabel logo = Images.getInstance().getParaLogoLabel(250, 250);
        leftPanel.add(logo, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(themeManager.getWhite());

        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(themeManager.getWhite());

        JLabel titleLabel = new JLabel("Create Your Account", SwingConstants.CENTER);
        titleLabel.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_BOLD, 18f));
        titleLabel.setForeground(themeManager.getBlack());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Name Field
        RoundedTextField nameField = new RoundedTextField(20);
        nameField.setMaximumSize(new Dimension(400, 45));
        nameField.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        nameField.setForeground(themeManager.getBlack());
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField.setBorderColor(themeManager.getGray());
        nameField.setPlaceholder("Name");

        // Password Field
        RoundedPasswordField passField = new RoundedPasswordField(20);
        passField.setMaximumSize(new Dimension(400, 45));
        passField.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        passField.setForeground(themeManager.getBlack());
        passField.setBackground(themeManager.getWhite());
        passField.setBorderColor(themeManager.getGray());
        passField.setPlaceholder("Password");
        passField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Confirm Password Field
        RoundedPasswordField confirmPassField = new RoundedPasswordField(20);
        confirmPassField.setMaximumSize(new Dimension(400, 45));
        confirmPassField.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        confirmPassField.setForeground(themeManager.getBlack());
        confirmPassField.setBackground(themeManager.getWhite());
        confirmPassField.setBorderColor(themeManager.getGray());
        confirmPassField.setPlaceholder("Confirm Password");
        confirmPassField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create Account Button
        JButton createButton = getJButton(nameField, passField, confirmPassField, themeManager);
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Back Button
        RoundedButton backButton = new RoundedButton("Back to Login");
        backButton.setArc(30, 30);
        backButton.setMaximumSize(new Dimension(400, 45));
        backButton.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        backButton.setForeground(themeManager.getWhite());
        backButton.setBackground(themeManager.getRed());
        backButton.setBorder(BorderFactory.createEmptyBorder());
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            System.out.println("↩️ Returning to Login Page");
            cardChanger.accept("LOGIN");
        });

        // Add all components to form content
        formContent.add(titleLabel);
        formContent.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingLarge()));
        formContent.add(nameField);
        formContent.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingMedium()));
        formContent.add(passField);
        formContent.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingMedium()));
        formContent.add(confirmPassField);
        formContent.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingLarge()));
        formContent.add(createButton);
        formContent.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingLarge()));
        formContent.add(backButton);

        formPanel.add(formContent, BorderLayout.CENTER);
        leftPanel.add(formPanel, BorderLayout.CENTER);

        // Right side - Graphic panel (960x1080)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(960, 1080));
        rightPanel.setBackground(themeManager.getWhite());

        try {
            // Load and display the city graphic night
            JLabel graphicLabel = Images.getInstance().getCityGraphicNight(960,1080);
            rightPanel.add(graphicLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            System.err.println("Could not load citygraphicnight.png: " + e.getMessage());
            // Fallback: show a colored panel if image not found
            rightPanel.setBackground(new Color(240, 240, 240));
        }

        // Combine left and right panels
        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JButton getJButton(JTextField nameField, JTextField passField, JTextField confirmPassField, ThemeManager themeManager) {
        RoundedButton createButton = new RoundedButton("Create Account");
        createButton.setArc(30, 30);
        createButton.setMaximumSize(new Dimension(400, 45));
        try { createButton.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f)); } catch (Exception ignored) {}
        createButton.setForeground(themeManager.getWhite());
        createButton.setBackground(themeManager.getBlack());
        createButton.setBorder(BorderFactory.createEmptyBorder());

        createButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String password = passField.getText().trim();
            String confirmPassword = confirmPassField.getText().trim();

            if (name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("📝 Signing up user: " + name + " | Password: " + password);

            new UserManager().signUpUser(name, password);

            JOptionPane.showMessageDialog(this, "Account created successfully! Please log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            cardChanger.accept("Login");
        });

        return createButton;
    }
}