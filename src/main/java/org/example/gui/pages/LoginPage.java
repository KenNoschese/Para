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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        add(createLoginPanel(cardChanger), BorderLayout.CENTER);
    }

    public static JPanel createLoginPanel(Consumer<String> cardChanger) throws IOException, FontFormatException {
        ThemeManager themeManager = ThemeManager.getInstance();

        // Main container: split into left (graphic) and right (form)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(themeManager.getWhite());

        // === LEFT: City Graphic (960x1080) ===
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(960, 1080));
        leftPanel.setBackground(themeManager.getWhite());

        try {
            JLabel graphicLabel = Images.getInstance().getCityGraphic(960, 1080);
            leftPanel.add(graphicLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            System.err.println("Could not load citygraphic.png: " + e.getMessage());
            leftPanel.setBackground(new Color(240, 240, 240));
        }

        // === RIGHT: Login Form ===
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(themeManager.getWhite());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 100, 50));

        // Logo
        JLabel logo = Images.getInstance().getParaLogoLabel(250, 250);
        rightPanel.add(logo, BorderLayout.NORTH);

        // Form content
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(themeManager.getWhite());

        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(themeManager.getWhite());

        // Title & Subtitle
        JLabel titleLabel = new JLabel("Enter your username and password", SwingConstants.CENTER);
        titleLabel.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_BOLD, 16f));
        titleLabel.setForeground(themeManager.getBlack());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Don't have an account?", SwingConstants.CENTER);
        subtitleLabel.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        subtitleLabel.setForeground(themeManager.getBlack());
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

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
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorderColor(themeManager.getGray());
        passwordField.setPlaceholder("Password");

        // === BUTTONS ===
        RoundedButton signUpButton = new RoundedButton("Sign Up");
        signUpButton.setArc(30, 30);
        signUpButton.setMaximumSize(new Dimension(400, 45));
        signUpButton.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        signUpButton.setForeground(themeManager.getWhite());
        signUpButton.setBackground(themeManager.getBlack());
        signUpButton.setBorderPainted(false);
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.addActionListener(e -> cardChanger.accept("SIGNUP"));

        RoundedButton loginButton = new RoundedButton("Login");
        loginButton.setArc(30, 30);
        loginButton.setMaximumSize(new Dimension(400, 45));
        loginButton.setFont(Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        loginButton.setForeground(themeManager.getWhite());
        loginButton.setBackground(themeManager.getRed());
        loginButton.setBorderPainted(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // === LOGIN LOGIC (WORKS WITH UserAccounts) ===
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel,
                        "Please enter both username and password.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("Attempting login for user: " + username);

            // VALIDATE USING UserAccounts
            int userId = validateLogin(username, password);
            if (userId == -1) {
                JOptionPane.showMessageDialog(mainPanel,
                        "Invalid username or password.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // CREATE ActiveSession
            if (!insertActiveSession(userId)) {
                JOptionPane.showMessageDialog(mainPanel,
                        "Login failed: Could not start session.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(mainPanel,
                    "Login successful!\nWelcome, " + username,
                    "Login Success", JOptionPane.INFORMATION_MESSAGE);

            DatabaseInstance.setLoggedInUser(username);
            System.out.println("Connected as: " + username + " (user_id: " + userId + ")");

            cardChanger.accept("LANDING");
        });

        // === ASSEMBLE FORM ===
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

        // === FINAL LAYOUT ===
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    // ──────────────────────────────────────────────────────────────
    // GET USER ID FROM DATABASE
    // ──────────────────────────────────────────────────────────────
    private static int getUserIdByUsername(String username) {
        String sql = "SELECT user_id FROM Users WHERE LOWER(username) = LOWER(?) LIMIT 1";
        try (Connection conn = DatabaseInstance.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get user_id: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    // ──────────────────────────────────────────────────────────────
    // INSERT INTO ActiveSession (BULLETPROOF)
    // ──────────────────────────────────────────────────────────────
    private static boolean insertActiveSession(int userId) {
        String sql = "INSERT INTO ActiveSession (user_id) VALUES (?) " +
                "ON DUPLICATE KEY UPDATE user_id = VALUES(user_id), login_time = CURRENT_TIMESTAMP";

        try (Connection conn = DatabaseInstance.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int rows = ps.executeUpdate();
            System.out.println("[SESSION] ActiveSession updated for user_id: " + userId);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to create ActiveSession: " + e.getMessage());
            return false;
        }
    }

    private static int validateLogin(String username, String password) {
        String sql = "SELECT user_id FROM UserAccounts WHERE LOWER(username) = LOWER(?) AND password = ? LIMIT 1";
        try (Connection conn = DatabaseInstance.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("user_id") : -1;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Login failed (UserAccounts): " + e.getMessage());
            return -1;
        }
    }
}