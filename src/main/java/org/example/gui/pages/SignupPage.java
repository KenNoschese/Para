package org.example.gui.pages;

import org.example.DatabaseManager.UserDatabase.UserManager;
import org.example.gui.appManager.ThemeManager;
import org.example.gui.components.base.RoundedButton;
import org.example.gui.components.base.RoundedTextField;
import org.example.gui.components.base.RoundedPasswordField;
import org.example.gui.resources.Images;
import org.example.gui.resources.Fonts;
import org.example.gui.appManager.SizeManager;

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

        // === MAIN CONTAINER: SPLIT LEFT (FORM) + RIGHT (GRAPHIC) ===
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(themeManager.getWhite());

        // === LEFT: SIGNUP FORM ===
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(themeManager.getWhite());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 100, 50));

        // Logo
        JLabel logo = Images.getInstance().getParaLogoLabel(250, 250);
        leftPanel.add(logo, BorderLayout.NORTH);

        // Form container
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(themeManager.getWhite());

        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(themeManager.getWhite());

        // Title
        JLabel titleLabel = new JLabel("Create Your Account", SwingConstants.CENTER);
        titleLabel.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_BOLD, 18f));
        titleLabel.setForeground(themeManager.getBlack());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // === INPUT FIELDS ===
        RoundedTextField usernameField = new RoundedTextField(20);
        usernameField.setMaximumSize(new Dimension(400, 45));
        usernameField.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        usernameField.setForeground(themeManager.getBlack());
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setBorderColor(themeManager.getGray());
        usernameField.setPlaceholder("Username");

        RoundedPasswordField passwordField = new RoundedPasswordField(20);
        passwordField.setMaximumSize(new Dimension(400, 45));
        passwordField.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        passwordField.setForeground(themeManager.getBlack());
        passwordField.setBackground(themeManager.getWhite());
        passwordField.setBorderColor(themeManager.getGray());
        passwordField.setPlaceholder("Password");
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        RoundedPasswordField confirmPasswordField = new RoundedPasswordField(20);
        confirmPasswordField.setMaximumSize(new Dimension(400, 45));
        confirmPasswordField.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        confirmPasswordField.setForeground(themeManager.getBlack());
        confirmPasswordField.setBackground(themeManager.getWhite());
        confirmPasswordField.setBorderColor(themeManager.getGray());
        confirmPasswordField.setPlaceholder("Confirm Password");
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // === CREATE ACCOUNT BUTTON ===
        RoundedButton createButton = new RoundedButton("Create Account");
        createButton.setArc(30, 30);
        createButton.setMaximumSize(new Dimension(400, 45));
        createButton.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        createButton.setForeground(themeManager.getWhite());
        createButton.setBackground(themeManager.getBlack());
        createButton.setBorderPainted(false);
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // === BACK TO LOGIN BUTTON ===
        RoundedButton backButton = new RoundedButton("Back to Login");
        backButton.setArc(30, 30);
        backButton.setMaximumSize(new Dimension(400, 45));
        backButton.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        backButton.setForeground(themeManager.getWhite());
        backButton.setBackground(themeManager.getRed());
        backButton.setBorderPainted(false);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            System.out.println("Returning to Login Page");
            cardChanger.accept("LOGIN");
        });

        // === SIGNUP LOGIC (OLD FUNCTIONALITY PRESERVED) ===
        createButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String confirm = new String(confirmPasswordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("Signing up user: " + username + " | Password: " + password);

            boolean success = new UserManager().signUpUser(username, password);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Account created!\nYou can now log in as: " + username,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                cardChanger.accept("LOGIN");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Username already exists or DB error.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // === ASSEMBLE FORM ===
        formContent.add(titleLabel);
        formContent.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingLarge()));
        formContent.add(usernameField);
        formContent.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingMedium()));
        formContent.add(passwordField);
        formContent.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingMedium()));
        formContent.add(confirmPasswordField);
        formContent.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingLarge()));
        formContent.add(createButton);
        formContent.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingLarge()));
        formContent.add(backButton);

        formPanel.add(formContent, BorderLayout.CENTER);
        leftPanel.add(formPanel, BorderLayout.CENTER);

        // === RIGHT: NIGHT CITY GRAPHIC (960x1080) ===
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(960, 1080));
        rightPanel.setBackground(themeManager.getWhite());

        try {
            JLabel graphicLabel = Images.getInstance().getCityGraphicNight(960, 1080);
            rightPanel.add(graphicLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            System.err.println("Could not load citygraphicnight.png: " + e.getMessage());
            rightPanel.setBackground(new Color(30, 30, 50)); // dark fallback
        }

        // === FINAL LAYOUT ===
        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);
    }
}