package org.example.ui;

import org.example.components.FooterPanel;
import org.example.components.RoutePanel;
import org.example.components.roundPanel;
import org.example.config.appTheme;
import org.example.resources.Images;
import org.example.resources.fonts;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class mainPage extends JFrame {
    public mainPage() throws IOException, FontFormatException {
        initializeFrame();
        JPanel mainPage = createMainPanel();
        add(mainPage, BorderLayout.CENTER);

        FooterPanel footerPanel = new FooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void initializeFrame() {
        setSize(appTheme.WINDOW_SIZE);
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
        header.setPreferredSize(appTheme.HEADER_SIZE);
        header.setOpaque(false);
        header.setLayout(new BorderLayout());

        ImageIcon logo = createScaledIcon(Images.PARA_LOGO, 175, 175);
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
        contentPane.add(Box.createVerticalStrut(appTheme.SPACING_LARGE));
        contentPane.add(createResultLabel());
        contentPane.add(Box.createVerticalStrut(appTheme.SPACING_LARGE));
        contentPane.add(createRouteContainer());

        center.add(contentPane);
        return center;
    }

    private JPanel createTextContainer() throws IOException, FontFormatException {
        roundPanel textContainer = new roundPanel(appTheme.BORDER_RADIUS_LARGE);
        textContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 150, 50));
        textContainer.setBackground(appTheme.GREEN);
        textContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        textContainer.add(createWelcomeContainer());
        textContainer.add(createInputContainer());
        return textContainer;
    }

    private JPanel createWelcomeContainer() throws IOException, FontFormatException {
        JPanel wcContainer = new JPanel();
        wcContainer.setLayout(new BoxLayout(wcContainer, BoxLayout.Y_AXIS));
        wcContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        wcContainer.setBackground(appTheme.GREEN);

        JLabel wcText = new JLabel("Welcome Ken!");
        wcText.setFont(loadCustomFont(fonts.DM_SANS_ITALIC, 20f));
        wcText.setForeground(appTheme.YELLOW);

        JLabel wcQuestion = new JLabel("<html>Where do you want<br>to go?</html>");
        wcQuestion.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 40f));

        wcContainer.add(wcText);
        wcContainer.add(Box.createVerticalStrut(appTheme.SPACING_MEDIUM));
        wcContainer.add(wcQuestion);
        return wcContainer;
    }

    private JPanel createInputContainer() {
        JPanel inputContainer = new JPanel();
        inputContainer.setLayout(new BoxLayout(inputContainer, BoxLayout.Y_AXIS));
        inputContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputContainer.setBackground(appTheme.GREEN);

        JTextField currentLocation = new JTextField();
        currentLocation.setPreferredSize(new Dimension(250, 40));

        JTextField destination = new JTextField();
        destination.setPreferredSize(new Dimension(250, 40));

        inputContainer.add(currentLocation);
        inputContainer.add(Box.createVerticalStrut(appTheme.SPACING_MEDIUM));
        inputContainer.add(destination);
        return inputContainer;
    }

    private JLabel createResultLabel() throws IOException, FontFormatException {
        JLabel resultLabel = new JLabel("We found 4 routes!");
        resultLabel.setFont(loadCustomFont(fonts.DM_SANS_ITALIC, 20f));
        resultLabel.setForeground(appTheme.RED);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return resultLabel;
    }

    private JPanel createRouteContainer() {
        JPanel routeContainer = new JPanel();
        routeContainer.setLayout(new GridLayout(2, 2, 25, 25));
        routeContainer.setPreferredSize(new Dimension(1200, 400));
        routeContainer.setAlignmentX(Component.CENTER_ALIGNMENT);


        RoutePanel panel1 = new RoutePanel();
        RoutePanel panel2 = new RoutePanel("Mintal", "Roxas", "30min", "Jeep", appTheme.BLUE);
        RoutePanel panel3 = new RoutePanel("Bangkal", "Lanang", "25min", "Jeep", appTheme.GREEN);

        routeContainer.add(panel1);
        routeContainer.add(panel2);
        routeContainer.add(panel3);

        return routeContainer;
    }

    private static Font loadCustomFont(String fontPath, float size) throws IOException, FontFormatException {
        Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        return font.deriveFont(size);
    }

    private ImageIcon createScaledIcon(String path, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(path);
        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    private JLabel createImageLabel(String path, int width, int height) {
        return new JLabel(createScaledIcon(path, width, height), SwingConstants.CENTER);
    }

    private Image createScaledImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        return icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
}


