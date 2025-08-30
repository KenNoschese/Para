package org.example.ui;

import org.example.components.roundPanel;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class mainPage extends JFrame {
    public mainPage() throws IOException, FontFormatException {
        initializeFrame();
        JPanel mainPage = createMainPanel();
        add(mainPage, BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
        setVisible(true);
    }

    private void initializeFrame() {
        setSize(1920, 1080);
        setTitle("Para!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private JPanel createMainPanel() throws IOException, FontFormatException {
        JPanel mainPage = new JPanel();
        mainPage.setPreferredSize(new Dimension(1644, 960));
        mainPage.setLayout(new BorderLayout());

        mainPage.add(createHeader(), BorderLayout.NORTH);
        mainPage.add(createCenterPanel(), BorderLayout.CENTER);
        return mainPage;
    }

    private JPanel createHeader() throws IOException {
        JPanel header = new JPanel();
        header.setPreferredSize(new Dimension(1644, 160));
        header.setOpaque(false);
        header.setLayout(new BorderLayout());

        ImageIcon logo = loadScaledLogo("ProjectFiles/Para.png", 175, 175);
        JLabel logoLabel = new JLabel(logo, SwingConstants.CENTER);
        header.add(logoLabel, BorderLayout.CENTER);
        return header;
    }

    private JPanel createCenterPanel() throws IOException, FontFormatException {
        JPanel center = new JPanel();
        center.setLayout(new FlowLayout(FlowLayout.CENTER));

        JPanel contentPane = new JPanel();
        contentPane.setPreferredSize(new Dimension(1000, 800));
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        contentPane.add(createTextContainer());
        contentPane.add(Box.createVerticalStrut(30));
        contentPane.add(createResultLabel());
        contentPane.add(Box.createVerticalStrut(30));
        contentPane.add(createRouteContainer());

        center.add(contentPane);
        return center;
    }

    private JPanel createTextContainer() throws IOException, FontFormatException {
        roundPanel textContainer = new roundPanel(30);
        textContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 150, 50));
        textContainer.setBackground(new Color(0x84b477));
        textContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        textContainer.add(createWelcomeContainer());
        textContainer.add(createInputContainer());
        return textContainer;
    }

    private JPanel createWelcomeContainer() throws IOException, FontFormatException {
        JPanel wcContainer = new JPanel();
        wcContainer.setLayout(new BoxLayout(wcContainer, BoxLayout.Y_AXIS));
        wcContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        wcContainer.setBackground(new Color(0x84b477));

        JLabel wcText = new JLabel("Welcome Ken!");
        wcText.setFont(loadCustomFont("ProjectFiles/DMSansItalic.ttf", 20f));
        wcText.setForeground(new Color(0xffe786));

        JLabel wcQuestion = new JLabel("<html>Where do you want<br>to go?</html>");
        wcQuestion.setFont(loadCustomFont("ProjectFiles/DMSansBold.ttf", 40f));

        wcContainer.add(wcText);
        wcContainer.add(Box.createVerticalStrut(20));
        wcContainer.add(wcQuestion);
        return wcContainer;
    }

    private JPanel createInputContainer() {
        JPanel inputContainer = new JPanel();
        inputContainer.setLayout(new BoxLayout(inputContainer, BoxLayout.Y_AXIS));
        inputContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputContainer.setBackground(new Color(0x84b477));

        JTextField currentLocation = new JTextField();
        currentLocation.setPreferredSize(new Dimension(250, 40));

        JTextField destination = new JTextField();
        destination.setPreferredSize(new Dimension(250, 40));

        inputContainer.add(currentLocation);
        inputContainer.add(Box.createVerticalStrut(20));
        inputContainer.add(destination);
        return inputContainer;
    }

    private JLabel createResultLabel() throws IOException, FontFormatException {
        JLabel resultLabel = new JLabel("We found 4 routes!");
        resultLabel.setFont(loadCustomFont("ProjectFiles/DMSansItalic.ttf", 20f));
        resultLabel.setForeground(new Color(0xd85259));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return resultLabel;
    }

    private JPanel createRouteContainer() {
        JPanel routeContainer = new JPanel();
        routeContainer.setLayout(new GridLayout(2, 2, 25, 25));
        routeContainer.setPreferredSize(new Dimension(1200, 400));
        routeContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (int i = 0; i < 4; i++) {
            roundPanel route = new roundPanel(30);
            route.setPreferredSize(new Dimension(250, 100));
            route.setBackground(Color.GRAY);
            routeContainer.add(route);
        }
        return routeContainer;
    }

    private JPanel createFooter() throws IOException, FontFormatException {
        JPanel footer = new JPanel();
        footer.setPreferredSize(new Dimension(1644, 60));
        footer.setBackground(new Color(0xffe786));
        footer.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton startButton = new JButton("Back to Home");
        startButton.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 16f));
        startButton.setBackground(new Color(0xd85259));
        startButton.setForeground(new Color(0xe8ced6));
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        startButton.addActionListener(e -> {
            try {
                new landingPage().setVisible(true);
                dispose();
            } catch (IOException | FontFormatException ex) {
                throw new RuntimeException(ex);
            }
        });

        footer.add(startButton);
        return footer;
    }

    private ImageIcon loadScaledLogo(String path, int width, int height) throws IOException {
        ImageIcon img = new ImageIcon(path);
        Image resized = img.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resized);
    }

    private static Font loadCustomFont(String fontPath, float size) throws IOException, FontFormatException {
        Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        return font.deriveFont(size);
    }
}


