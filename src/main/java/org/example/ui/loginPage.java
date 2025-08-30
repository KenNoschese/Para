package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class loginPage extends JFrame {

    public loginPage() throws IOException, FontFormatException {
        setTitle("Para!");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(245, 245, 245));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 100, 0));


        ImageIcon img = new ImageIcon("ProjectFiles/Para.png");
        Image resized = img.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        ImageIcon logo = new ImageIcon(resized);
        JLabel logoLabel = new JLabel(logo, SwingConstants.CENTER);

        mainPanel.add(logoLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(new Color(245, 245, 245));

        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(new Color(245, 245, 245));


        JLabel titleLabel = new JLabel("Create an account", SwingConstants.CENTER);
        titleLabel.setFont(loadCustomFont("ProjectFiles/DMSansBold.ttf", 28f));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Enter a username and password", SwingConstants.CENTER);
        subtitleLabel.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 16f));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField usernameField = createTextField();
        JTextField passwordField = createTextField();

        JButton signUpButton = createSignUpButton();
        JLabel orLabel = new JLabel("or", SwingConstants.CENTER);
        orLabel.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 14f));
        orLabel.setForeground(Color.GRAY);
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginButton = createLoginButton();

        formContent.add(titleLabel);
        formContent.add(Box.createVerticalStrut(30));
        formContent.add(subtitleLabel);
        formContent.add(Box.createVerticalStrut(30));
        formContent.add(usernameField);
        formContent.add(Box.createVerticalStrut(30));
        formContent.add(passwordField);
        formContent.add(Box.createVerticalStrut(30));
        formContent.add(signUpButton);

        formContent.add(orLabel);

        formContent.add(loginButton);

        formPanel.add(formContent, BorderLayout.CENTER);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }


    private JTextField createTextField() throws IOException, FontFormatException {
        JTextField textField = new JTextField();
        textField.setMaximumSize(new Dimension(400, 45));
        textField.setPreferredSize(new Dimension(400, 45));
        textField.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 16f));
        textField.setForeground(Color.GRAY);
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);


        return textField;
    }

    private JButton createSignUpButton() throws IOException, FontFormatException {
        JButton button = new JButton("Sign Up");
        button.setMaximumSize(new Dimension(400, 45));
        button.setPreferredSize(new Dimension(400, 45));
        button.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 16f));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        return button;
    }

    private JButton createLoginButton() {
        JButton button = new JButton("Login");
        button.setMaximumSize(new Dimension(400, 45));
        button.setPreferredSize(new Dimension(400, 45));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(220, 85, 85));
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
