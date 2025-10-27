package org.example.gui;

import org.example.gui.appManager.sizeManager;
import org.example.gui.pages.loginPage;
import org.example.gui.pages.landingPage;
import org.example.gui.pages.mainPage;
import org.example.gui.pages.signupPage;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private mainPage currentMainPage;

    public MainFrame() {
        initializeFrame();
        setupUI();
    }

    private void initializeFrame() {
        setTitle("Para!");
        setSize(sizeManager.getInstance().flexibleWidth(1920, 1080));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void setupUI() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        try {
            mainPanel.add(new loginPage(this::changeCard), "LOGIN");
            mainPanel.add(new signupPage(this::changeCard), "SIGNUP");
            mainPanel.add(new landingPage(this::changeCard), "LANDING");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentPane(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
        setVisible(true);
    }

    public void changeCard(String cardName) {
        if (cardName.equals("MAIN")) {
            try {
                // old mainPage will be disposed
                if (currentMainPage != null) {
                    currentMainPage.dispose();
                    mainPanel.remove(currentMainPage);
                }

                // new mainPage with fresh connection so program works
                currentMainPage = new mainPage(this::changeCard);
                mainPanel.add(currentMainPage, "MAIN");

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error initializing main page: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        cardLayout.show(mainPanel, cardName);
    }
}