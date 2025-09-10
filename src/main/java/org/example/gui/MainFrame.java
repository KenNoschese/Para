package org.example.gui;

import org.example.gui.appManager.sizeManager;
import org.example.gui.pages.loginPage;
import org.example.gui.pages.landingPage;
import org.example.gui.pages.mainPage;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        initializeFrame();
        setupUI();
    }

    private void initializeFrame() {
        setTitle("Para!");
        setSize(sizeManager.WINDOW_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void setupUI() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        try {
            mainPanel.add(new loginPage(this::changeCard), "LOGIN");
            mainPanel.add(new landingPage(this::changeCard), "LANDING");
            mainPanel.add(new mainPage(this::changeCard), "MAIN");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentPane(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
        setVisible(true);
    }

    public void changeCard(String cardName) {
        cardLayout.show(mainPanel, cardName);
    }
}