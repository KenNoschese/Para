package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.*;

//green#84b477, red#d85259, #e8ced6, blue#b7caef, yellow#ffe786

public class landingPage extends JFrame {
    public landingPage() throws IOException, FontFormatException {
        setSize(1920, 1080);
        setTitle("Para!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPage = new JPanel();
        mainPage.setPreferredSize(new Dimension(1644, 960));
        mainPage.setLayout(new BorderLayout());

        JPanel head = new JPanel();
        head.setPreferredSize(new Dimension(1181, 160));
        head.setOpaque(false);

        ImageIcon img = new ImageIcon("ProjectFiles/Para.png");
        Image resized = img.getImage().getScaledInstance(175, 175, Image.SCALE_SMOOTH);
        ImageIcon logo = new ImageIcon(resized);
        JLabel logoLabel = new JLabel(logo, SwingConstants.CENTER);

        head.add(logoLabel, BorderLayout.CENTER);

        JPanel center = new JPanel();

        JPanel contentPane = new JPanel();
        contentPane.setPreferredSize(new Dimension(1000, 800));
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        roundPanel textContainer = new roundPanel(30);
        textContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 150, 50));
        textContainer.setBackground(new Color(0x84b477));
        textContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel wcContainer = new JPanel();
        wcContainer.setLayout(new BoxLayout(wcContainer, BoxLayout.Y_AXIS));
        wcContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        wcContainer.setBackground(new Color(0x84b477));

        JLabel wcText = new JLabel("Welcome Ken!");
        wcText.setFont(loadCustomFont("ProjectFiles/DMSansItalic.ttf", 20f));
        wcText.setForeground(new Color(0xffe786));
        JLabel wcQuestion = new JLabel();
        wcQuestion.setText("<html>Where do you want<br>to go?</html>");
        wcQuestion.setFont(loadCustomFont("ProjectFiles/DMSansBold.ttf", 40f));

        wcContainer.add(wcText);
        wcContainer.add(Box.createVerticalStrut(20));
        wcContainer.add(wcQuestion);

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

        textContainer.add(wcContainer);
        textContainer.add(inputContainer);

        JLabel resultLabel = new JLabel("We found 4 routes!");
        resultLabel.setFont(loadCustomFont("ProjectFiles/DMSansItalic.ttf", 20f));
        resultLabel.setForeground(new Color(0xd85259));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel routeContainer = new JPanel();
        routeContainer.setLayout(new GridLayout(2, 2, 25, 25));
        routeContainer.setPreferredSize(new Dimension(1200, 400));
        routeContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        roundPanel route1 = new roundPanel(30);
        route1.setPreferredSize(new Dimension(250, 100));
        route1.setBackground(Color.GRAY);

        roundPanel route2 = new roundPanel(30);
        route2.setPreferredSize(new Dimension(250, 100));
        route2.setBackground(Color.GRAY);

        roundPanel route3 = new roundPanel(30);
        route3.setPreferredSize(new Dimension(250, 100));
        route3.setBackground(Color.GRAY);

        roundPanel route4 = new roundPanel(30);
        route4.setPreferredSize(new Dimension(250, 100));
        route4.setBackground(Color.GRAY);

        routeContainer.add(route1);
        routeContainer.add(route2);
        routeContainer.add(route3);
        routeContainer.add(route4);


        contentPane.add(textContainer);
        contentPane.add(Box.createVerticalStrut(30));
        contentPane.add(resultLabel);
        contentPane.add(Box.createVerticalStrut(30));
        contentPane.add(routeContainer);
        center.add(contentPane);
        mainPage.add(center, BorderLayout.CENTER);
        mainPage.add(head, BorderLayout.NORTH);
        add(mainPage, BorderLayout.CENTER);

        setVisible(true);
    }

    private static Font loadCustomFont(String fontPath, float size) throws IOException, FontFormatException {
        Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);
        return font.deriveFont(size);
    }

}
