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

        // Name Field
        RoundingOfTextfields nameField = new RoundingOfTextfields(20);
        nameField.setMaximumSize(new Dimension(400, 45));
        nameField.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        nameField.setForeground(themeManager.getBlack());
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField.setBorderColor(themeManager.getGray());
        nameField.setPlaceholder("Name");

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

        // Category Label + Box
        JLabel categoryLabel = new JLabel("Select Category", SwingConstants.CENTER);
        categoryLabel.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        categoryLabel.setForeground(themeManager.getBlack());
        categoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] categories = {"Regular", "Student", "PWD", "Senior Citizen"};
        JComboBox<String> categoryBox = new JComboBox<>(categories);
        categoryBox.setMaximumSize(new Dimension(400, 45));
        categoryBox.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        categoryBox.setForeground(themeManager.getBlack());
        categoryBox.setBackground(themeManager.getWhite());
        categoryBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        categoryBox.setFocusable(false);

        // Create Account Button
        JButton createButton = getJButton(nameField, passField, confirmPassField, categoryBox);
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
        backButton.addActionListener(e -> {
            System.out.println("↩️ Returning to Login Page");
            cardChanger.accept("LOGIN");
        });

        // Add all components to panel
        formContent.add(titleLabel);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingLarge()));
        formContent.add(nameField);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingMedium()));
        formContent.add(passField);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingMedium()));
        formContent.add(confirmPassField);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingLarge()));
        formContent.add(categoryLabel);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
        formContent.add(categoryBox);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingLarge()));
        formContent.add(createButton);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingLarge()));
        formContent.add(backButton);

        formPanel.add(formContent, BorderLayout.CENTER);
        add(formPanel, BorderLayout.CENTER);
    }

    // ✅ Updated getJButton method
    private JButton getJButton(JTextField nameField, JTextField passField, JTextField confirmPassField, JComboBox<String> categoryBox) {
        ThemeManager themeManager = ThemeManager.getInstance();

        RoundingOfButtons createButton = new RoundingOfButtons("Create Account");
        createButton.setArc(30, 30);
        createButton.setMaximumSize(new Dimension(400, 45));
        try {
            createButton.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        } catch (Exception ignored) {}
        createButton.setForeground(themeManager.getWhite());
        createButton.setBackground(themeManager.getBlack());
        createButton.setBorder(BorderFactory.createEmptyBorder());

        createButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String password = passField.getText().trim();
            String confirmPassword = confirmPassField.getText().trim();
            String category = categoryBox.getSelectedItem().toString();

            if (name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this,
                        "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            System.out.println("📝 Signing up user: " + name + " | Category: " + category + " | Password: " + password);
            new UserManager().signUpUser(name, category, password);
        });
        return createButton;
    }
}