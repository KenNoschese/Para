package org.example.gui.pages;

import org.example.gui.appManager.ThemeManager;
import org.example.gui.appManager.sizeManager;
import org.example.gui.components.roundButton;
import org.example.gui.components.roundTextField;
import org.example.gui.resources.Images;
import org.example.gui.resources.fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class loginPage extends JPanel {
    private Consumer<String> cardChanger;

    public loginPage(Consumer<String> cardChanger) throws IOException, FontFormatException {
        this.cardChanger = cardChanger;
        setupPanel();
    }

    private void setupPanel() throws IOException, FontFormatException {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getInstance().getWhite());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeManager.getInstance().getWhite());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 100, 0));

        ImageIcon img = new ImageIcon(Images.PARA_LOGO);
        Image resized = img.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        ImageIcon logo = new ImageIcon(resized);
        JLabel logoLabel = new JLabel(logo, SwingConstants.CENTER);

        mainPanel.add(logoLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(ThemeManager.getInstance().getWhite());

        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(ThemeManager.getInstance().getWhite());

        JLabel titleLabel = new JLabel("Create an account", SwingConstants.CENTER);
        titleLabel.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 28f));
        titleLabel.setForeground(ThemeManager.getInstance().getBlack());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Enter a username and password", SwingConstants.CENTER);
        subtitleLabel.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        subtitleLabel.setForeground(ThemeManager.getInstance().getGray());
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField usernameField = createTextField();
        JTextField passwordField = createTextField();
        JButton signUpButton = createSignUpButton();

        JLabel orLabel = new JLabel("or", SwingConstants.CENTER);
        orLabel.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 14f));
        orLabel.setForeground(Color.GRAY);
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginButton = createLoginButton();

        formContent.add(titleLabel);
        formContent.add(Box.createVerticalStrut(sizeManager.SPACING_LARGE));
        formContent.add(subtitleLabel);
        formContent.add(Box.createVerticalStrut(sizeManager.SPACING_LARGE));
        formContent.add(usernameField);
        formContent.add(Box.createVerticalStrut(sizeManager.SPACING_LARGE));
        formContent.add(passwordField);
        formContent.add(Box.createVerticalStrut(sizeManager.SPACING_LARGE));
        formContent.add(signUpButton);
        formContent.add(orLabel);
        formContent.add(loginButton);

        formPanel.add(formContent, BorderLayout.CENTER);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JTextField createTextField() throws IOException, FontFormatException {
        roundTextField textField = new roundTextField(0);
        textField.setArc(30, 30);
        textField.setMaximumSize(new Dimension(400, 45));
        textField.setPreferredSize(new Dimension(400, 45));
        textField.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        textField.setForeground(Color.GRAY);
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        return textField;
    }

    private JButton createSignUpButton() throws IOException, FontFormatException {
        roundButton button = new roundButton("Sign Up");
        button.setArc(30, 30);
        button.setMaximumSize(new Dimension(400, 45));
        button.setPreferredSize(new Dimension(400, 45));
        button.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        button.setForeground(ThemeManager.getInstance().getWhite());
        button.setBackground(ThemeManager.getInstance().getBlack());
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private JButton createLoginButton() throws IOException, FontFormatException {
        roundButton button = new roundButton("Login");
        button.setArc(30, 30);
        button.setMaximumSize(new Dimension(400, 45));
        button.setPreferredSize(new Dimension(400, 45));
        button.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 16));
        button.setForeground(ThemeManager.getInstance().getWhite());
        button.setBackground(ThemeManager.getInstance().getRed());
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addActionListener(e -> cardChanger.accept("LANDING"));

        return button;
    }

    private static Font loadCustomFont(String fontPath, float size) throws IOException, FontFormatException {
        Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);
        return font.deriveFont(size);
    }
}
