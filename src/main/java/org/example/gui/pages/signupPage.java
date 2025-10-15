package org.example.gui.pages;

import org.example.DatabaseManager.UserDatabase.UserManager;

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
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel("Full Name:");
        JTextField nameField = new JTextField(20);

        JLabel categoryLabel = new JLabel("Category:");
        String[] categories = {"Regular", "Student", "PWD", "Senior Citizen"};
        JComboBox<String> categoryBox = new JComboBox<>(categories);

        JButton createButton = getJButton(nameField, categoryBox);

        JButton backButton = new JButton("Back to Login");
        backButton.addActionListener(e -> {
            System.out.println("Returning to Login Page");
            cardChanger.accept("LOGIN");
        });

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryBox);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(createButton);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(backButton);

        add(formPanel, BorderLayout.CENTER);
    }

    private JButton getJButton(JTextField nameField, JComboBox<String> categoryBox) {
        JButton createButton = new JButton("Create Account");
        createButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String category = categoryBox.getSelectedItem().toString();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("Signing up user: " + name + " | Category: " + category);

            new UserManager().signUpUser(name, category);
        });
        return createButton;
    }
}

