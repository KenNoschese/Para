package org.example.gui;

import org.example.gui.appManager.SizeManager;
import org.example.gui.components.dialogs.ErrorDialog;
import org.example.gui.pages.LoginPage;
import org.example.gui.pages.LandingPage;
import org.example.gui.pages.MainPage;
import org.example.gui.pages.SignupPage;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MainPage currentMainPage;

    public MainFrame() {
        initializeFrame();
        setupUI();
    }

    private void initializeFrame() {
        setTitle("Para!");
        setSize(SizeManager.getInstance().flexibleWidth(1920, 1080));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void setupUI() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        try {
            mainPanel.add(new LoginPage(this::changeCard), "LOGIN");
            mainPanel.add(new SignupPage(this::changeCard), "SIGNUP");
            mainPanel.add(new LandingPage(this::changeCard), "LANDING");
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
                currentMainPage = new MainPage(this::changeCard);
                mainPanel.add(currentMainPage, "MAIN");

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    ErrorDialog.show(this,
                            "Error initializing main page: " + e.getMessage(),
                            "Error");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;
            }
        }

        cardLayout.show(mainPanel, cardName);
    }
}