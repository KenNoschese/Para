package org.example.gui.pages;

import org.example.gui.components.Factories.factoryPanel;

import javax.swing.*;
import java.awt.*;
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
        setBackground(Color.WHITE);

        JPanel loginPanel = factoryPanel.createLoginPanel(cardChanger);

        add(loginPanel, BorderLayout.CENTER);
    }
}
