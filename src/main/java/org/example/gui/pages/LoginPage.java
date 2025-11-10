package org.example.gui.pages;

import org.example.DatabaseManager.DatabaseInstance;
import org.example.gui.appManager.SizeManager;
import org.example.gui.appManager.ThemeManager;
import org.example.gui.components.Factories.*;
import org.example.gui.components.base.RoundedButton;
import org.example.gui.components.base.RoundedTextField;
import org.example.gui.components.base.RoundedPasswordField;
import org.example.gui.resources.Fonts;
import org.example.gui.resources.Images;

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

        // Main container with horizontal split
        JPanel mainPanel = PanelFactory.create(themeManager.getWhite(), 0, 0, 0);
        mainPanel.setLayout(new BorderLayout());

        // Left side - Graphic panel (960x1080)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(960, 1080));
        leftPanel.setBackground(themeManager.getWhite());

        try {
            // Load and display the city graphic
            JLabel graphicLabel = Images.getInstance().getCityGraphic(960, 1080);
            leftPanel.add(graphicLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            System.err.println("Could not load citygraphic.png: " + e.getMessage());
            // Fallback: show a colored panel if image not found
            leftPanel.setBackground(new Color(240, 240, 240));
        }

        // Right side - Login form panel
        JPanel rightPanel = PanelFactory.create(themeManager.getWhite(), 0, 0, 0);
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 100, 50));

        JLabel logo = Images.getInstance().getParaLogoLabel(250, 250);
        rightPanel.add(logo, BorderLayout.NORTH);

        JPanel formPanel = PanelFactory.create(themeManager.getWhite(), 0, 0, 0);
        formPanel.setLayout(new BorderLayout());

        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(themeManager.getWhite());

        JLabel titleLabel = LabelFactory.create("Enter your username and password",
                Fonts.loadCustomFont(Fonts.DM_SANS_BOLD, 16f),
                themeManager.getBlack());

        JLabel subtitleLabel = LabelFactory.create("Don't have an account?",
                Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f),
                themeManager.getBlack());

        // Username field
        RoundedTextField usernameField = TextfieldFactory.create("Username", 400, 45, 20,
                Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f),
                themeManager.getWhite(), themeManager.getBlack());
        usernameField.setBorderColor(themeManager.getGray());

        // Password field
        RoundedPasswordField passwordField = new RoundedPasswordField(20);
        passwordField.setMaximumSize(new Dimension(400, 45));
        passwordField.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        passwordField.setForeground(themeManager.getBlack());
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorderColor(themeManager.getGray());
        passwordField.setPlaceholder("Password");

        // Sign Up button
        RoundedButton signUpButton = ButtonFactory.create("Sign Up",
                Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f),
                themeManager.getBlack(), themeManager.getWhite(),
                400, 45, 30);
        signUpButton.setBorder(BorderFactory.createEmptyBorder());
        signUpButton.addActionListener(e -> {
            System.out.println("➡️ Sign Up button clicked");
            cardChanger.accept("SIGNUP");
        });

        // Login button
        RoundedButton loginButton = ButtonFactory.create("Login",
                Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f),
                themeManager.getRed(), themeManager.getWhite(),
                400, 45, 30);
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

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

                    DatabaseInstance.setLoggedInUser(username, null);
                    System.out.println("Connected as: " + DatabaseInstance.getInstance().getActiveUsername());

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

        // Layout assembly for right panel
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
        rightPanel.add(formPanel, BorderLayout.CENTER);

        // Combine left and right panels
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        return mainPanel;
    }
}