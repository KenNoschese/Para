package org.example.gui.pages;

import org.example.DatabaseManager.DatabaseInstance;
import org.example.gui.components.base.RoundedButton;
import org.example.gui.components.base.RoundedTextField;
import org.example.gui.components.base.RoundedPasswordField;
import org.example.gui.resources.Fonts;
import org.example.gui.resources.Images;
import org.example.gui.appManager.SizeManager;
import org.example.gui.appManager.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.function.Consumer;

public class LoginPage extends JPanel {
    private final Consumer<String> cardChanger;

    public LoginPage(Consumer<String> cardChanger) throws IOException, FontFormatException {
        this.cardChanger = cardChanger;
        setupPanel();
    }

    private void setupPanel() throws IOException, FontFormatException {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel loginPanel = createLoginPanel(cardChanger);
        add(loginPanel, BorderLayout.CENTER);
    }

    public static JPanel createLoginPanel(Consumer<String> cardChanger)
            throws IOException, FontFormatException {

        ThemeManager themeManager = ThemeManager.getInstance();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(themeManager.getWhite());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 100, 0));

        JLabel logo = Images.getInstance().getParaLogoLabel(250, 250);
        mainPanel.add(logo, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(themeManager.getWhite());

        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(themeManager.getWhite());

        JLabel titleLabel = new JLabel("Enter your username and password", SwingConstants.CENTER);
        titleLabel.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_BOLD, 16f));
        titleLabel.setForeground(themeManager.getBlack());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Don't have an account?", SwingConstants.CENTER);
        subtitleLabel.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        subtitleLabel.setForeground(themeManager.getBlack());
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username field
        RoundedTextField usernameField = new RoundedTextField(20);
        usernameField.setMaximumSize(new Dimension(400, 45));
        usernameField.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        usernameField.setForeground(themeManager.getBlack());
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setBorderColor(themeManager.getGray());
        usernameField.setPlaceholder("Username");

        // Password field
        RoundedPasswordField passwordField = new RoundedPasswordField(20);
        passwordField.setMaximumSize(new Dimension(400, 45));
        passwordField.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        passwordField.setForeground(themeManager.getBlack());
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorderColor(themeManager.getGray());
        passwordField.setPlaceholder("Password");

        // Sign Up button
        RoundedButton signUpButton = new RoundedButton("Sign Up");
        signUpButton.setArc(30, 30);
        signUpButton.setMaximumSize(new Dimension(400, 45));
        signUpButton.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        signUpButton.setForeground(themeManager.getWhite());
        signUpButton.setBackground(themeManager.getBlack());
        signUpButton.setBorder(BorderFactory.createEmptyBorder());
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.addActionListener(e -> {
            System.out.println("➡️ Sign Up button clicked");
            cardChanger.accept("SIGNUP");
        });

        JLabel orLabel = new JLabel("---------- or ----------", SwingConstants.CENTER);
        orLabel.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 14f));
        orLabel.setForeground(themeManager.getGray());
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login button (actual login logic here)
        RoundedButton loginButton = new RoundedButton("Login");
        loginButton.setArc(30, 30);
        loginButton.setMaximumSize(new Dimension(400, 45));
        loginButton.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        loginButton.setForeground(themeManager.getWhite());
        loginButton.setBackground(themeManager.getRed());
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ✅ Updated login logic that switches DB connection
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel,
                        "Please enter both username and password.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                System.out.println("Attempting login for user: " + username);

                boolean success = DatabaseInstance.loginAsUser(username, password);

                if (success) {
                    JOptionPane.showMessageDialog(mainPanel,
                            "✅ Login successful!\nWelcome, " + username,
                            "Login Success", JOptionPane.INFORMATION_MESSAGE);

                    // Set logged-in user
                    DatabaseInstance.setLoggedInUser(username, null);

                    // Confirm connection user
                    System.out.println("Connected as: " + DatabaseInstance.getInstance().getActiveUsername());

                    // Move to landing page
                    cardChanger.accept("LANDING");
                } else {
                    JOptionPane.showMessageDialog(mainPanel,
                            "Invalid username or password.",
                            "Login Failed", JOptionPane.ERROR_MESSAGE);
                    System.out.println("❌ Login failed for user: " + username);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainPanel,
                        "Unexpected error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("Unexpected error during login: " + ex.getMessage());
            }
        });

        // Layout assembly
        formContent.add(titleLabel);
        formContent.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingLarge()));
        formContent.add(usernameField);
        formContent.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingLarge()));
        formContent.add(passwordField);
        formContent.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingLarge()));
        formContent.add(loginButton);
        formContent.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingLarge()));
        formContent.add(subtitleLabel);
        formContent.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingSmall()));
        formContent.add(signUpButton);

        formPanel.add(formContent, BorderLayout.CENTER);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        return mainPanel;
    }
}