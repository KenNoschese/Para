package org.example.gui.pages;

import org.example.gui.appManager.darkModeToggle;
import org.example.gui.components.*;
import org.example.gui.resources.RouteData;
import org.example.gui.config.RouteManager;
import org.example.gui.appManager.ThemeManager;
import org.example.gui.appManager.sizeManager;
import org.example.gui.resources.Images;
import org.example.gui.resources.fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class mainPage extends JPanel implements ThemeManager.ThemeChangeListener {
    private Consumer<String> cardChanger;
    private JPanel routeContainer;
    private roundTextField currentLocation;
    private roundTextField destination;
    private RouteManager routeManager;
    private ThemeManager themeManager;

    private JPanel headerPanel;
    private JPanel centerPanel;
    private FooterPanel footerPanel;
    private roundPanel textContainer;
    private JPanel welcomeContainer;
    private JPanel inputContainer;
    private roundButton submitButton;
    private roundPanel infoPanel;
    private roundPanel savedPanel;
    private roundPanel recentPanel;
    private JLabel infoLabel, savedLabel, recentLabel;
    private JLabel wcText, wcQuestion; // Store welcome text labels

    public mainPage(Consumer<String> cardChanger) throws IOException, FontFormatException {
        this.cardChanger = cardChanger;
        this.themeManager = ThemeManager.getInstance();
        this.themeManager.addThemeChangeListener(this);
        setupPanel();
    }

    private void setupPanel() throws IOException, FontFormatException {
        this.routeManager = new RouteManager();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1920, 1080));
        setBackground(themeManager.getBackgroundColor());

        headerPanel = createHeader();
        centerPanel = createCenterPanel();
        footerPanel = new FooterPanel();

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeader() throws IOException {
        JPanel header = new JPanel();
        header.setPreferredSize(new Dimension(sizeManager.HEADER_SIZE));
        header.setBackground(themeManager.getBackgroundColor());
        header.setLayout(new BorderLayout());

        ImageIcon logo = createScaledIcon(Images.PARA_LOGO, 175, 175);
        JLabel logoLabel = new JLabel(logo, SwingConstants.CENTER);

        header.add(logoLabel, BorderLayout.CENTER);
        header.add(new darkModeToggle(), BorderLayout.EAST);

        return header;
    }

    private JPanel createCenterPanel() throws IOException, FontFormatException {
        JPanel center = new JPanel();
        center.setBackground(themeManager.getBackgroundColor());
        center.setLayout(new FlowLayout(FlowLayout.CENTER, 150, 0));

        JPanel contentPane = new JPanel();
        contentPane.setBackground(themeManager.getBackgroundColor());
        contentPane.setPreferredSize(new Dimension(1920, 800));
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        contentPane.add(Box.createHorizontalStrut(100));
        contentPane.add(createLeftJPanel());
        contentPane.add(Box.createHorizontalStrut(100));
        contentPane.add(createRightPanel());
        contentPane.add(Box.createHorizontalStrut(100));

        center.add(contentPane);
        return center;
    }

    private JPanel createRightPanel() throws IOException, FontFormatException {
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(themeManager.getBackgroundColor());
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(450, 1080));
        rightPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Info
        infoLabel = createInfoLabel();
        rightPanel.add(infoLabel);
        rightPanel.add(Box.createVerticalStrut(sizeManager.SPACING_SMALL));
        rightPanel.add(createInfoPanel());

        rightPanel.add(Box.createVerticalStrut(sizeManager.SPACING_SMALL));

        // Saved
        savedLabel = createSavedLabel();
        rightPanel.add(savedLabel);
        rightPanel.add(Box.createVerticalStrut(sizeManager.SPACING_SMALL));
        rightPanel.add(createSavedPanel());

        rightPanel.add(Box.createVerticalStrut(sizeManager.SPACING_SMALL));

        // Recent
        recentLabel = createRecentLabel();
        rightPanel.add(recentLabel);
        rightPanel.add(Box.createVerticalStrut(sizeManager.SPACING_SMALL));
        rightPanel.add(createRecentPanel());

        return rightPanel;
    }


    private JPanel createLeftJPanel() throws IOException, FontFormatException {
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(themeManager.getBackgroundColor());
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(1050, 1080));
        leftPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(createTextContainer());
        leftPanel.add(Box.createVerticalStrut(sizeManager.SPACING_SMALL));
        leftPanel.add(createRouteContainer());

        return leftPanel;
    }

    private JPanel createTextContainer() throws IOException, FontFormatException {
        textContainer = new roundPanel(sizeManager.BORDER_RADIUS_LARGE);
        textContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 150, 75));
        textContainer.setPreferredSize(new Dimension(650, 195));
        textContainer.setBackground(themeManager.getYellow());
        textContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        textContainer.add(createWelcomeContainer());
        textContainer.add(createInputContainer());

        return textContainer;
    }

    private JPanel createWelcomeContainer() throws IOException, FontFormatException {
        welcomeContainer = new JPanel();
        welcomeContainer.setLayout(new BoxLayout(welcomeContainer, BoxLayout.Y_AXIS));
        welcomeContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        welcomeContainer.setBackground(themeManager.getYellow());

        wcText = new JLabel("Welcome Ken!");
        wcText.setFont(loadCustomFont(fonts.DM_SANS_ITALIC, 22f));
        wcText.setForeground(themeManager.getGreen());

        wcQuestion = new JLabel("<html>Where do you want<br>to go?</html>");
        wcQuestion.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 34f));
        wcQuestion.setForeground(themeManager.getForegroundColor());

        welcomeContainer.add(wcText);
        welcomeContainer.add(Box.createVerticalStrut(sizeManager.SPACING_SMALL));
        welcomeContainer.add(wcQuestion);

        return welcomeContainer;
    }

    private JPanel createInputContainer() throws IOException, FontFormatException {
        inputContainer = new JPanel();
        inputContainer.setLayout(new BoxLayout(inputContainer, BoxLayout.Y_AXIS));
        inputContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputContainer.setBackground(themeManager.getYellow());

        currentLocation = new roundTextField(26);
        currentLocation.setPreferredSize(new Dimension(250, 40));
        currentLocation.setMaximumSize(new Dimension(250, 40));
        currentLocation.setBackground(themeManager.getComponentsColor());
        currentLocation.setForeground(themeManager.getForegroundColor());

        destination = new roundTextField(26);
        destination.setPreferredSize(new Dimension(250, 40));
        destination.setMaximumSize(new Dimension(250, 40));
        destination.setBackground(themeManager.getComponentsColor());
        destination.setForeground(themeManager.getForegroundColor());

        submitButton = new roundButton("GO");
        submitButton.setPreferredSize(new Dimension(100, 40));
        submitButton.setMaximumSize(new Dimension(100, 40));
        submitButton.setBackground(themeManager.getGreen());
        submitButton.setForeground(themeManager.getWhite());
        submitButton.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 14));
        submitButton.setArc(30, 30);
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        submitButton.addMouseListener(new MouseAdapter (){
            @Override
            public void mouseEntered(MouseEvent e) {
                submitButton.setBackground(themeManager.getWhite());
                submitButton.setForeground(themeManager.getBlack());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                submitButton.setBackground(themeManager.getGreen());
                submitButton.setForeground(themeManager.getWhite());
            }
        });

        submitButton.addActionListener(e -> searchRoutes());
        currentLocation.addActionListener(e -> searchRoutes());
        destination.addActionListener(e -> searchRoutes());

        inputContainer.add(Box.createVerticalStrut(sizeManager.SPACING_SMALL));
        inputContainer.add(currentLocation);
        inputContainer.add(Box.createVerticalStrut(sizeManager.SPACING_MEDIUM));
        inputContainer.add(destination);
        inputContainer.add(Box.createVerticalStrut(sizeManager.SPACING_MEDIUM));
        inputContainer.add(submitButton);
        inputContainer.add(Box.createVerticalStrut(sizeManager.SPACING_SMALL));

        return inputContainer;
    }

    private roundPanel createStatusPanel() {
        roundPanel panel = new roundPanel(sizeManager.BORDER_RADIUS_LARGE);
        panel.setPreferredSize(new Dimension(360, 170));
        panel.setBackground(themeManager.getBlue());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private void setPanelPlaceholder(JPanel panel, String message) {
        JLabel placeholderImg = createImageLabel(Images.PLACEHOLDER, 100, 100);
        placeholderImg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel placeholderText = new JLabel(message);
        try {
            placeholderText.setFont(loadCustomFont(fonts.DM_SANS_ITALIC, 14));
        } catch (Exception ex) {}
        placeholderText.setForeground(themeManager.getForegroundColor());
        placeholderText.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(placeholderImg);
        panel.add(Box.createVerticalStrut(10));
        panel.add(placeholderText);
        panel.add(Box.createVerticalGlue());

        panel.revalidate();
        panel.repaint();
    }

    private roundPanel createInfoPanel() {
        infoPanel = createStatusPanel();

        setInfoMessage("No chosen route."); // default
        return infoPanel;
    }

    public void setInfoMessage(String message) {
        setPanelPlaceholder(infoPanel, message);
    }

    private roundPanel createSavedPanel() {
        savedPanel = createStatusPanel();
        setSavedRoutes(new ArrayList<>()); // default empty
        return savedPanel;
    }

    public void setSavedRoutes(ArrayList<String> routes) {
        if (routes.isEmpty()) {
            setPanelPlaceholder(savedPanel, "No saved routes.");
        } else {
            for (String route : routes) {
                JPanel row = new JPanel(new BorderLayout());
                row.setOpaque(false);

                JLabel routeLabel = new JLabel(route);
                JButton removeButton = new JButton("Remove");

                removeButton.addActionListener(e -> {
                    routes.remove(route);
                    setSavedRoutes(routes);
                });

                row.add(routeLabel, BorderLayout.CENTER);
                row.add(removeButton, BorderLayout.EAST);
                savedPanel.add(row);
            }
        }
        savedPanel.revalidate();
        savedPanel.repaint();
    }

    private roundPanel createRecentPanel() {
        recentPanel = createStatusPanel();
        setRecentSearches(new ArrayList<>());
        return recentPanel;
    }

    public void setRecentSearches(ArrayList<String> searches) {
        recentPanel.removeAll();
        if (searches.isEmpty()) {
            setPanelPlaceholder(recentPanel, "No recent searches.");
        } else {
            for (String search : searches) {
                JLabel searchLabel = new JLabel(search);
                recentPanel.add(searchLabel);
            }
        }
        recentPanel.revalidate();
        recentPanel.repaint();
    }


    private JLabel createInfoLabel() throws IOException, FontFormatException {
        infoLabel = new JLabel("Route Info");
        infoLabel.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 14f));
        infoLabel.setForeground(themeManager.getForegroundColor());
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return infoLabel;
    }

    private JLabel createSavedLabel() throws IOException, FontFormatException {
        savedLabel = new JLabel("Saved Routes");
        savedLabel.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 14f));
        savedLabel.setForeground(themeManager.getForegroundColor());
        savedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return savedLabel;
    }
    private JLabel createRecentLabel() throws IOException, FontFormatException {
        recentLabel = new JLabel("Recent Searches");
        recentLabel.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 14f));
        recentLabel.setForeground(themeManager.getForegroundColor());
        recentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return recentLabel;
    }

    private JPanel createRouteContainer() {
        JPanel mainContainer = new JPanel();
        mainContainer.setBackground(themeManager.getBackgroundColor());
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        routeContainer = new JPanel();
        routeContainer.setBackground(themeManager.getBackgroundColor());
        routeContainer.setLayout(new BoxLayout(routeContainer, BoxLayout.Y_AXIS));
        routeContainer.setPreferredSize(new Dimension(870, 450));
        routeContainer.setBorder(BorderFactory.createEmptyBorder());

        routeContainer.add(new RouteHeader());
        routeContainer.add(Box.createVerticalStrut(10));

        mainContainer.add(Box.createVerticalStrut(5));
        mainContainer.add(routeContainer);

        return mainContainer;
    }

    private void searchRoutes() {
        try {
            String from = currentLocation.getText().trim();
            String to = destination.getText().trim();

            if (from.isEmpty() || to.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter both current location and destination.",
                        "Input Required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            ArrayList<RouteData> foundRoutes = routeManager.findRoutes(from, to);
            displayRoutes(foundRoutes, from, to);
        } catch (IOException | FontFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error searching routes: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayRoutes(ArrayList<RouteData> routes, String from, String to)
            throws IOException, FontFormatException {
        routeContainer.removeAll();
        routeContainer.add(new RouteHeader());
        routeContainer.add(Box.createVerticalStrut(10));

        if (routes.isEmpty()) {
            JLabel noRoutesLabel = new JLabel("No routes found from " + from + " to " + to);
            noRoutesLabel.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 14));
            noRoutesLabel.setForeground(themeManager.getGray());
            noRoutesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            routeContainer.add(Box.createVerticalStrut(50));
            routeContainer.add(noRoutesLabel);
            routeContainer.add(Box.createVerticalStrut(50));
        } else {
            for (int i = 0; i < routes.size(); i++) {
                RoutePanel panel = new RoutePanel(routes.get(i));
                panel.setAlignmentX(Component.CENTER_ALIGNMENT);
                routeContainer.add(panel);

                if (i < routes.size() - 1) {
                    routeContainer.add(Box.createVerticalStrut(sizeManager.SPACING_SMALL));
                }
            }
        }

        routeContainer.revalidate();
        routeContainer.repaint();
    }

    public void reloadRoutes() throws IOException {
        routeManager.reloadRoutes();
    }

    @Override
    public void onThemeChange(boolean isDarkMode) {
        // Just re-apply backgrounds and foregrounds
        setBackground(themeManager.getBackgroundColor());

        if (headerPanel != null) headerPanel.setBackground(themeManager.getBackgroundColor());
        if (centerPanel != null) centerPanel.setBackground(themeManager.getBackgroundColor());

        if (currentLocation != null) {
            currentLocation.setBackground(themeManager.getComponentsColor());
            currentLocation.setForeground(themeManager.getForegroundColor());
        }
        if (destination != null) {
            destination.setBackground(themeManager.getComponentsColor());
            destination.setForeground(themeManager.getForegroundColor());
        }

        if (infoLabel != null) infoLabel.setForeground(themeManager.getForegroundColor());
        if (savedLabel != null) savedLabel.setForeground(themeManager.getForegroundColor());
        if (recentLabel != null) recentLabel.setForeground(themeManager.getForegroundColor());

        if (wcText != null) wcText.setForeground(themeManager.getGreen());
        if (wcQuestion != null) wcQuestion.setForeground(themeManager.getForegroundColor());

        // Let ThemeManager refresh all components
        SwingUtilities.invokeLater(() -> {
            themeManager.applyThemeToContainers(this);
            repaint();
        });
    }

    public void dispose() {
        if (themeManager != null) {
            themeManager.removeThemeChangeListener(this);
        }
    }

    // Utility methods
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("mainPage Preview");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                Consumer<String> dummyCardChanger = System.out::println;
                mainPage panel = new mainPage(dummyCardChanger);

                frame.add(panel);
                frame.setSize(1920, 1080);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (IOException | FontFormatException e) {
                e.printStackTrace();
            }
        });
    }
}
