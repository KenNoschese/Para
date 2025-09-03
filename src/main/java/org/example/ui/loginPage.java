package org.example.ui;

import org.example.components.roundButton;
import org.example.components.roundTextField;
import org.example.config.appTheme;
import org.example.resources.Images;
import org.example.resources.fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class loginPage extends JFrame {

    public loginPage() throws IOException, FontFormatException {
        setTitle("Para!");
        setSize(appTheme.WINDOW_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(appTheme.WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(appTheme.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 100, 0));


        ImageIcon img = new ImageIcon(Images.PARA_LOGO);
        Image resized = img.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        ImageIcon logo = new ImageIcon(resized);
        JLabel logoLabel = new JLabel(logo, SwingConstants.CENTER);

        mainPanel.add(logoLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(appTheme.WHITE);

        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(appTheme.WHITE);


        JLabel titleLabel = new JLabel("Create an account", SwingConstants.CENTER);
        titleLabel.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 28f));
        titleLabel.setForeground(appTheme.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Enter a username and password", SwingConstants.CENTER);
        subtitleLabel.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        subtitleLabel.setForeground(appTheme.GRAY);
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
        formContent.add(Box.createVerticalStrut(appTheme.SPACING_LARGE));
        formContent.add(subtitleLabel);
        formContent.add(Box.createVerticalStrut(appTheme.SPACING_LARGE));
        formContent.add(usernameField);
        formContent.add(Box.createVerticalStrut(appTheme.SPACING_LARGE));
        formContent.add(passwordField);
        formContent.add(Box.createVerticalStrut(appTheme.SPACING_LARGE));
        formContent.add(signUpButton);

        formContent.add(orLabel);

        formContent.add(loginButton);

        formPanel.add(formContent, BorderLayout.CENTER);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }


    private JTextField createTextField() throws IOException, FontFormatException {
        roundTextField textField = new roundTextField(0);
        textField.setArc(30,30);
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
        button.setForeground(appTheme.WHITE);
        button.setBackground(appTheme.BLACK);
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
        button.setForeground(appTheme.WHITE);
        button.setBackground(appTheme.RED);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                landingPage landingFrame = null;
                try {
                    landingFrame = new landingPage();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (FontFormatException ex) {
                    throw new RuntimeException(ex);
                }
                landingFrame.setVisible(true);


            }
        });
        return button;
    }

    private static Font loadCustomFont(String fontPath, float size) throws IOException, FontFormatException {
        Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);
        return font.deriveFont(size);
    }

}
