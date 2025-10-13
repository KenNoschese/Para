package org.example.gui.pages;

import org.example.gui.appManager.ThemeManager;
import org.example.gui.appManager.sizeManager;
import org.example.gui.components.*;
import org.example.gui.resources.RouteData;
import org.example.gui.config.RouteManager;
import org.example.gui.resources.Images;
import org.example.gui.resources.fonts;
import org.example.gui.components.Factories.factoryPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Consumer;

import static org.example.gui.config.RouteManager.findRoutes;

public class mainPage extends JPanel implements ThemeManager.ThemeChangeListener {
    private Consumer<String> cardChanger;
    private JPanel routeContainer;
    private RoundingOfTextfields currentLocation;
    private RoundingOfTextfields destination;
    private RouteManager routeManager;
    private ThemeManager themeManager;
    private JPanel centerPanel;
    private RoundingOfPanels textContainer;
    private JPanel welcomeContainer;
    private JPanel inputContainer;
    private RoundingOfButtons submitButton;
    private RoundingOfPanels infoPanel;
    private RoundingOfPanels savedPanel;
    private RoundingOfPanels recentPanel;
    private JLabel infoLabel, savedLabel, recentLabel;
    private JLabel wcText, wcQuestion;
    private final ArrayList<RouteData> savedRoutes = new ArrayList<>();


    public mainPage(Consumer<String> cardChanger) throws IOException, FontFormatException {
        this.cardChanger = cardChanger;
        this.themeManager = ThemeManager.getInstance();
        this.themeManager.addThemeChangeListener(this);
        setupPanel();
    }

    private void setupPanel() throws IOException, FontFormatException {
        this.routeManager = new RouteManager();
        setLayout(new BorderLayout());
        setPreferredSize(sizeManager.getInstance().flexibleWidth(1920, 1080));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        setBackground(themeManager.getBackgroundColor());

        // Use FactoryPanel for header and footer
        add(factoryPanel.createHeaderPanel(), BorderLayout.NORTH);
        centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        add(factoryPanel.createFooterPanel(), BorderLayout.SOUTH);
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
        rightPanel.setPreferredSize(new Dimension(550, 1080));
        rightPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoLabel = createInfoLabel();
        rightPanel.add(infoLabel);
        rightPanel.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
        rightPanel.add(createInfoPanel());

        rightPanel.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));

        savedLabel = createSavedLabel();
        rightPanel.add(savedLabel);
        rightPanel.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
        rightPanel.add(createSavedPanel());

        rightPanel.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));

//        recentLabel = createRecentLabel();
//        rightPanel.add(recentLabel);
        rightPanel.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
//        rightPanel.add(createRecentPanel());

        return rightPanel;
    }

    private JPanel createLeftJPanel() throws IOException, FontFormatException {
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(themeManager.getBackgroundColor());
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(950, 1080));
        leftPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(createTextContainer());
        leftPanel.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
        leftPanel.add(createRouteContainer());

        return leftPanel;
    }

    private JPanel createTextContainer() throws IOException, FontFormatException {
        textContainer = new RoundingOfPanels(sizeManager.getInstance().getBorderRadiusLarge());
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

        wcText = new JLabel("Welcome!");
        wcText.setFont(loadCustomFont(fonts.DM_SANS_ITALIC, 22f));
        wcText.setForeground(themeManager.getBlack());

        wcQuestion = new JLabel("<html>Where do you want<br>to go?</html>");
        wcQuestion.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 34f));
        wcQuestion.setForeground(themeManager.getForegroundColor());

        welcomeContainer.add(wcText);
        welcomeContainer.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
        welcomeContainer.add(wcQuestion);

        return welcomeContainer;
    }

    private JPanel createInputContainer() throws IOException, FontFormatException {
        inputContainer = new JPanel();
        inputContainer.setLayout(new BoxLayout(inputContainer, BoxLayout.Y_AXIS));
        inputContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputContainer.setBackground(themeManager.getYellow());

        currentLocation = new RoundingOfTextfields(26);
        currentLocation.setPreferredSize(new Dimension(250, 40));
        currentLocation.setMaximumSize(new Dimension(250, 40));
        currentLocation.setBackground(themeManager.getComponentsColor());
        currentLocation.setForeground(themeManager.getForegroundColor());

        destination = new RoundingOfTextfields(26);
        destination.setPreferredSize(new Dimension(250, 40));
        destination.setMaximumSize(new Dimension(250, 40));
        destination.setBackground(themeManager.getComponentsColor());
        destination.setForeground(themeManager.getForegroundColor());

        submitButton = new RoundingOfButtons("GO");
        submitButton.setPreferredSize(new Dimension(100, 40));
        submitButton.setMaximumSize(new Dimension(100, 40));
        submitButton.setBackground(themeManager.getGreen());
        submitButton.setForeground(themeManager.getWhite());
        submitButton.setFont(loadCustomFont(fonts.DM_SANS_BOLD, sizeManager.getInstance().getTextSmall()));
        submitButton.setArc(sizeManager.getInstance().getBorderRadiusLarge(), sizeManager.getInstance().getBorderRadiusLarge());
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        submitButton.addMouseListener(new MouseAdapter() {
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

        inputContainer.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
        inputContainer.add(currentLocation);
        inputContainer.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingMedium()));
        inputContainer.add(destination);
        inputContainer.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingMedium()));
        inputContainer.add(submitButton);
        inputContainer.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));

        return inputContainer;
    }

    private RoundingOfPanels createStatusPanel() {
        RoundingOfPanels panel = new RoundingOfPanels(sizeManager.getInstance().getBorderRadiusLarge());
        panel.setPreferredSize(new Dimension(360, 170));
        panel.setBackground(themeManager.getBlue());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private void setPanelPlaceholder(JPanel panel, String message) {
        JLabel placeHolder = Images.getInstance().getPlaceholderLabel(100, 100);

        placeHolder.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel placeholderText = new JLabel(message);
        try {
            placeholderText.setFont(loadCustomFont(fonts.DM_SANS_ITALIC, 14));
        } catch (Exception ex) {
            // ignore
        }
        placeholderText.setForeground(themeManager.getForegroundColor());
        placeholderText.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(placeHolder);
        panel.add(Box.createVerticalStrut(10));
        panel.add(placeholderText);
        panel.add(Box.createVerticalGlue());

        panel.revalidate();
        panel.repaint();
    }

    private RoundingOfPanels createInfoPanel() {
        infoPanel = createStatusPanel();
        infoPanel.setPreferredSize(new Dimension(550, 400));
        infoPanel.setMinimumSize(new Dimension(550, 400));
        infoPanel.setMaximumSize(new Dimension(550, 400));

        setInfoMessage("No chosen route.");
        return infoPanel;
    }


    private void displayRouteInfo(RouteData route) {
        String stopsDisplay = String.join(" ➡ ", route.getRoute_stops());
        infoPanel.removeAll();

        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(themeManager.getBlue());
        infoPanel.setAlignmentX(CENTER_ALIGNMENT);

        try {
            JLabel title = new JLabel(route.getRoute() + "     " + route.getETA() + "min. ");
            title.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 16f));
            title.setForeground(themeManager.getBlack());
            title.setAlignmentX(Component.CENTER_ALIGNMENT);

            RoundingOfButtons save = new RoundingOfButtons("Save");
            save.setText("💾 Save Route");
            save.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 14f));
            save.setBackground(themeManager.getYellow());
            save.setForeground(themeManager.getBlack());
            save.setAlignmentX(Component.CENTER_ALIGNMENT);


            save.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setSavedRoutes(route);
                }
            });



            JLabel details = new JLabel("<html>" +
                    "Transfers: " + route.getTransfers() + "<br>" +
                    "Stops: " + route.getstops() + "<br>" +
                    "Details: " + route.getDetails() + "<br>" +
                    "Fare: Php " + String.format("%.2f", route.getFare()) + "<br>" +
                    stopsDisplay + "<br>" +
                    "</html>");
            details.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 14f));
            details.setForeground(themeManager.getBlack());
            details.setAlignmentX(Component.CENTER_ALIGNMENT);

            infoPanel.add(Box.createVerticalStrut(20));
            infoPanel.add(title);
            infoPanel.add(Box.createVerticalStrut(10));
            infoPanel.add(details);
            infoPanel.add(save);
            infoPanel.add(Box.createVerticalGlue());

        } catch (Exception e) {
            e.printStackTrace();
            setInfoMessage("Error displaying route info.");
        }

        infoPanel.revalidate();
        infoPanel.repaint();
    }

    public void setInfoMessage(String message) {
        setPanelPlaceholder(infoPanel, message);
    }

    private RoundingOfPanels createSavedPanel() {
        savedPanel = createStatusPanel();
        savedPanel.setPreferredSize(new Dimension(550, 300));
        savedPanel.setMinimumSize(new Dimension(550, 300));
        savedPanel.setMaximumSize(new Dimension(550, 300));
        setPanelPlaceholder(savedPanel, "No saved routes.");
        return savedPanel;
    }

    public void setSavedRoutes(RouteData route) {
        for (RouteData saved : savedRoutes) {
//            if (saved.getRoute().equalsIgnoreCase(route.getRoute())) {
//                JOptionPane.showMessageDialog(this,
//                        "This route is already saved!",
//                        "Notice",
//                        JOptionPane.INFORMATION_MESSAGE);
//                return;
//            }
        }

        // Add to saved list
        savedRoutes.add(route);

        // Update UI
        refreshSavedRoutesPanel();
    }

    private void refreshSavedRoutesPanel() {
        savedPanel.removeAll();

        if (savedRoutes.isEmpty()) {
            setPanelPlaceholder(savedPanel, "No saved routes.");
        } else {
            savedPanel.setLayout(new BoxLayout(savedPanel, BoxLayout.Y_AXIS));

            savedPanel.add(Box.createVerticalStrut(20));
            for (RouteData savedRoute : savedRoutes) {
                RoundingOfPanels routePanel = new RoundingOfPanels(30);
                routePanel.setLayout(new BorderLayout());
                routePanel.setBackground(themeManager.getWhite());
                routePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                routePanel.setMaximumSize(new Dimension(500, 40));

                JLabel routeLabel = new JLabel("<html>" + savedRoute.getFromLocation() + " to " + savedRoute.getDestination() + " <b><i>&nbsp;via&nbsp;</i></b> " +
                        savedRoute.getRoute() + " &nbsp;&nbsp;(" + savedRoute.getETA() + " min)" + "</html>");
                try {
                    routeLabel.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 14f));
                } catch (Exception e) {
                    routeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
                }
                routeLabel.setForeground(themeManager.getBlack());

                routeLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        displayRouteInfo(savedRoute);
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        routeLabel.setForeground(themeManager.getGreen());
                        routeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        routeLabel.setForeground(themeManager.getBlack());
                    }
                });

                // ❌ Remove button
                JButton removeButton = new JButton("✖");
                removeButton.setPreferredSize(new Dimension(30, 30));
                removeButton.setFocusPainted(false);
                removeButton.setBackground(themeManager.getWhite());
                removeButton.setForeground(Color.RED);
                removeButton.setBorder(BorderFactory.createEmptyBorder());
                removeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                removeButton.addActionListener(e -> {
                    savedRoutes.remove(savedRoute);
                    refreshSavedRoutesPanel();
                });

                savedPanel.add(Box.createVerticalStrut(10));
                routePanel.add(routeLabel, BorderLayout.CENTER);
                routePanel.add(removeButton, BorderLayout.EAST);

                savedPanel.add(routePanel);
                savedPanel.add(Box.createVerticalStrut(5));
            }
        }

        savedPanel.revalidate();
        savedPanel.repaint();
    }

//    private RoundingOfPanels createRecentPanel() {
//        recentPanel = createStatusPanel();
//        setRecentSearches(new ArrayList<>());
//        return recentPanel;
//    }

//    public void setRecentSearches(ArrayList<String> searches) {
//        recentPanel.removeAll();
//        if (searches.isEmpty()) {
//            setPanelPlaceholder(recentPanel, "No recent searches.");
//        } else {
//            for (String search : searches) {
//                JLabel searchLabel = new JLabel(search);
//                recentPanel.add(searchLabel);
//            }
//        }
//        recentPanel.revalidate();
//        recentPanel.repaint();
//    }

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

//    private JLabel createRecentLabel() throws IOException, FontFormatException {
//        recentLabel = new JLabel("Recent Searches");
//        recentLabel.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 14f));
//        recentLabel.setForeground(themeManager.getForegroundColor());
//        recentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//        return recentLabel;
//    }

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

        // Use FactoryPanel instead of RouteHeader
        routeContainer.add(factoryPanel.createRouteHeader());
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

            ArrayList<RouteData> foundRoutes = RouteManager.findRoutes(from, to);
            displayRoutes(foundRoutes, from, to);
        } catch (IOException | FontFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error searching routes: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void displayRoutes(ArrayList<RouteData> routes, String from, String to)
            throws IOException, FontFormatException {
        routeContainer.removeAll();
        routeContainer.add(factoryPanel.createRouteHeader());
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
                JPanel panel = factoryPanel.createRoutePanel(routes.get(i), this::displayRouteInfo);
                panel.setAlignmentX(Component.CENTER_ALIGNMENT);
                routeContainer.add(panel);

                if (i < routes.size() - 1) {
                    routeContainer.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
                }
            }
        }

        routeContainer.revalidate();
        routeContainer.repaint();
    }

    @Override
    public void onThemeChange(boolean isDarkMode) {
        setBackground(themeManager.getBackgroundColor());

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
