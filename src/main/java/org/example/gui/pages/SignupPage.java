package org.example.gui.pages;

import org.example.DatabaseManager.UserDatabase.UserManager;
import org.example.gui.appManager.ThemeManager;
import org.example.gui.appManager.SizeManager;
import org.example.gui.components.Factories.*;
import org.example.gui.components.base.RoundedPasswordField;
import org.example.gui.resources.Images;
import org.example.gui.resources.Fonts;
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
        JPanel mainPanel = PanelFactory.create(themeManager.getWhite(), 0, 0, 0);
        mainPanel.setLayout(new BorderLayout());

        // Left side - Signup form panel
        JPanel leftPanel = PanelFactory.create(themeManager.getWhite(), 0, 0, 0);
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 100, 50));

        JLabel logo = Images.getInstance().getParaLogoLabel(250, 250);
        leftPanel.add(logo, BorderLayout.NORTH);

        JPanel formPanel = PanelFactory.create(themeManager.getWhite(), 0, 0, 0);
        formPanel.setLayout(new BorderLayout());

        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(themeManager.getWhite());

        JLabel titleLabel = LabelFactory.create("Create Your Account",
                Fonts.loadCustomFont(Fonts.DM_SANS_BOLD, 18f),
                themeManager.getBlack());

        // Name Field
        RoundedTextField nameField = TextfieldFactory.create("Name", 400, 45, 20,
                Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f),
                themeManager.getWhite(), themeManager.getBlack());
        nameField.setBorderColor(themeManager.getGray());

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
        RoundedButton backButton = ButtonFactory.create("Back to Login",
                Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f),
                themeManager.getRed(), themeManager.getWhite(),
                400, 45, 30);
        backButton.setBorder(BorderFactory.createEmptyBorder());
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
        RoundedButton createButton;
        try {
            createButton = ButtonFactory.create("Create Account",
                    Fonts.loadCustomFont(Fonts.DM_SANS_REGULAR, 16f),
                    themeManager.getBlack(), themeManager.getWhite(),
                    400, 45, 30);
        } catch (Exception e) {
            createButton = ButtonFactory.create("Create Account",
                    new Font("Arial", Font.PLAIN, 16),
                    themeManager.getBlack(), themeManager.getWhite(),
                    400, 45, 30);
        }
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