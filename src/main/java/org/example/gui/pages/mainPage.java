package org.example.gui.pages;

import static org.example.gui.components.Factories.factoryPanel.createUserButton;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.*;

import org.example.DatabaseManager.RouteDatabase.*;
import org.example.DatabaseManager.RouteDatabase.StrategyClasses.*;
import org.example.gui.appManager.*;
import org.example.gui.components.*;
import org.example.gui.components.Factories.*;
import org.example.gui.resources.Images;
import org.example.DatabaseManager.RouteDatabase.RouteData;
import org.example.gui.resources.fonts;

public class mainPage extends JPanel implements ThemeManager.ThemeChangeListener {
    private Consumer<String> cardChanger;
    private JPanel routeContainer;
    private RoundingOfTextfields currentLocation;
    private RoundingOfTextfields destination;
    private RouteManager routeManager;
    private NavigationFacade navigationFacade;
    private ThemeManager themeManager;
    private JPanel container;
    private RoundingOfPanels textContainer;
    private JPanel welcomeContainer;
    private JPanel inputContainer;
    private RoundingOfButtons submitButton;
    private RoundingOfButtons userButton;
    private RoundingOfPanels locationPanel;
    private RoundingOfPanels infoPanel;
    private JLabel locationsLabel;
    private RoundingOfPanels savedPanel;
    private JLabel savedLabel;
    private JLabel wcQuestion;
    private final ArrayList<RouteData> savedRoutes = new ArrayList<>();
    private String currentFilter = "all"; // default

    public mainPage(Consumer<String> cardChanger) throws IOException, FontFormatException, SQLException {
        this.cardChanger = cardChanger;
        this.themeManager = ThemeManager.getInstance();
        this.themeManager.addThemeChangeListener(this);
        setupPanel();
    }

    /* --------------------------------------------------------------- */
    /*                     UI INITIALISATION                           */
    /* --------------------------------------------------------------- */
    private void setupPanel() throws IOException, FontFormatException, SQLException {
        this.routeManager = new RouteManager();
        this.navigationFacade = new NavigationFacade();
        setLayout(new BorderLayout());
        setPreferredSize(sizeManager.getInstance().flexibleWidth(1920, 1080));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        setBackground(themeManager.getBackgroundColor());

        container = createContainer();
        add(container, BorderLayout.CENTER);
    }

    private JPanel createContainer() throws IOException, FontFormatException {
        JPanel center = panelFactory.create(themeManager.getBackgroundColor(), 0, 0, 0);
        center.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        JPanel contentPane = panelFactory.create(
                themeManager.getBackgroundColor(),
                1920, 1080,
                sizeManager.getInstance().getBorderRadiusLarge()
        );
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
        contentPane.add(createLeftJPanel());
        contentPane.add(createCenterPanel());
        contentPane.add(createRightPanel());

        center.add(contentPane);
        return center;
    }

    /* --------------------- LEFT PANEL (INPUT) --------------------- */
    private JPanel createLeftJPanel() throws IOException, FontFormatException {
        JPanel leftPanel = panelFactory.create(
                themeManager.getYellow(),
                350, Integer.MAX_VALUE,
                sizeManager.getInstance().getBorderRadiusLarge()
        );
        leftPanel.setLayout(new BorderLayout());

        darkModeToggle darkMode = new darkModeToggle();
        userButton = createUserButton();
        userButton.setPreferredSize(new Dimension(200, 30));

        JPanel header = panelFactory.create(null, 350, 50,
                sizeManager.getInstance().getBorderRadiusSmall());
        header.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));
        header.setOpaque(false);
        header.add(userButton);
        header.add(darkMode);

        JPanel contentPanel = panelFactory.create(
                themeManager.getYellow(),
                0, 0,
                sizeManager.getInstance().getBorderRadiusSmall()
        );
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        contentPanel.add(createTextContainer());

        JPanel wrapper = panelFactory.create(null, 0, 0,
                sizeManager.getInstance().getBorderRadiusSmall());
        wrapper.setLayout(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(contentPanel, BorderLayout.NORTH);

        leftPanel.add(header, BorderLayout.NORTH);
        leftPanel.add(wrapper, BorderLayout.CENTER);
        return leftPanel;
    }

    private JPanel createTextContainer() throws IOException, FontFormatException {
        textContainer = panelFactory.create(
                themeManager.getYellow(),
                0, 0,
                sizeManager.getInstance().getBorderRadiusLarge()
        );
        textContainer.setLayout(new BoxLayout(textContainer, BoxLayout.Y_AXIS));
        textContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        textContainer.add(createWelcomeContainer());
        return textContainer;
    }

    private JPanel createWelcomeContainer() throws IOException, FontFormatException {
        welcomeContainer = panelFactory.create(
                themeManager.getYellow(),
                0, 0,
                sizeManager.getInstance().getBorderRadiusLarge()
        );
        welcomeContainer.setLayout(new BoxLayout(welcomeContainer, BoxLayout.Y_AXIS));
        welcomeContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        wcQuestion = labelFactory.create(
                "<html>Where do you want<br>to go?</html>",
                loadCustomFont(fonts.DM_SANS_BOLD, 22f),
                themeManager.getForegroundColor()
        );
        wcQuestion.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel inputContainerPanel = createInputContainer();
        inputContainerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        welcomeContainer.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingLarge()));
        welcomeContainer.add(wcQuestion);
        welcomeContainer.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
        welcomeContainer.add(inputContainerPanel);
        return welcomeContainer;
    }

    private JPanel createInputContainer() throws IOException, FontFormatException {
        inputContainer = panelFactory.create(
                themeManager.getYellow(),
                0, 0,
                sizeManager.getInstance().getBorderRadiusLarge()
        );
        inputContainer.setLayout(new BoxLayout(inputContainer, BoxLayout.Y_AXIS));
        inputContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        currentLocation = textfieldFactory.create(
                "Start",
                280, 40,
                26,
                loadCustomFont(fonts.DM_SANS_ITALIC, sizeManager.getInstance().getTextSmall()),
                themeManager.getComponentsColor(),
                themeManager.getForegroundColor()
        );
        currentLocation.setAlignmentX(Component.LEFT_ALIGNMENT);

        destination = textfieldFactory.create(
                "End",
                280, 40,
                26,
                loadCustomFont(fonts.DM_SANS_ITALIC, sizeManager.getInstance().getTextSmall()),
                themeManager.getComponentsColor(),
                themeManager.getForegroundColor()
        );

        submitButton = buttonFactory.create(
                "View Available Routes",
                250, 40,
                sizeManager.getInstance().getBorderRadiusLarge(),
                loadCustomFont(fonts.DM_SANS_BOLD, sizeManager.getInstance().getTextSmall()),
                themeManager.getGreen(),
                themeManager.getWhite()
        );

        submitButton.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                submitButton.setBackground(themeManager.getWhite());
                submitButton.setForeground(themeManager.getBlack());
            }
            @Override public void mouseExited(MouseEvent e) {
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
        inputContainer.add(Box.createVerticalStrut(50));
        inputContainer.add(createFilterPanel());
        inputContainer.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingMedium()));
        inputContainer.add(submitButton);
        inputContainer.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
        return inputContainer;
    }

    private JPanel createFilterPanel() throws IOException, FontFormatException {
        JPanel filterContainer = panelFactory.create(
                themeManager.getYellow(),
                0, 0,
                sizeManager.getInstance().getBorderRadiusLarge()
        );
        filterContainer.setLayout(new BoxLayout(filterContainer, BoxLayout.Y_AXIS));
        filterContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel filter = labelFactory.create(
                "Filter for",
                loadCustomFont(fonts.DM_SANS_BOLD, 15),
                themeManager.getForegroundColor()
        );
        filter.setAlignmentX(Component.LEFT_ALIGNMENT);

        JRadioButton all = radioFactory.create(
                "All Routes",
                loadCustomFont(fonts.DM_SANS_REGULAR, 13),
                e -> { currentFilter = "all"; if (bothFieldsFilled()) searchRoutes(); }
        );
        all.setBackground(null);
        JRadioButton time = radioFactory.create(
                "Shortest Time",
                loadCustomFont(fonts.DM_SANS_REGULAR, 13),
                e -> { currentFilter = "time"; if (bothFieldsFilled()) searchRoutes(); }
        );
        time.setBackground(null);
        JRadioButton distance = radioFactory.create(
                "Shortest Distance",
                loadCustomFont(fonts.DM_SANS_REGULAR, 13),
                e -> { currentFilter = "distance"; if (bothFieldsFilled()) searchRoutes(); }
        );
        distance.setBackground(null);
        JRadioButton leastTransfer = radioFactory.create(
                "Least Transfers",
                loadCustomFont(fonts.DM_SANS_REGULAR, 13),
                e -> { currentFilter = "transfers"; if (bothFieldsFilled()) searchRoutes(); }
        );
        leastTransfer.setBackground(null);
        JRadioButton cheapest = radioFactory.create(
                "Cheapest Fare",
                loadCustomFont(fonts.DM_SANS_REGULAR, 13),
                e -> { currentFilter = "fare"; if (bothFieldsFilled()) searchRoutes(); }
        );
        cheapest.setBackground(null);

        ButtonGroup group = new ButtonGroup();
        group.add(all); group.add(time); group.add(distance);
        group.add(cheapest); group.add(leastTransfer);

        filterContainer.add(filter);
        filterContainer.add(Box.createVerticalStrut(10));
        filterContainer.add(all);
        filterContainer.add(Box.createVerticalStrut(5));
        filterContainer.add(time);
        filterContainer.add(Box.createVerticalStrut(5));
        filterContainer.add(distance);
        filterContainer.add(Box.createVerticalStrut(5));
        filterContainer.add(cheapest);
        filterContainer.add(Box.createVerticalStrut(5));
        filterContainer.add(leastTransfer);
        return filterContainer;
    }

    /* --------------------- CENTER PANEL (ROUTES + INFO) --------------------- */
    private JPanel createCenterPanel() throws IOException, FontFormatException {
        JPanel center = panelFactory.create(
                themeManager.getBackgroundColor(),
                1000, Integer.MAX_VALUE,
                sizeManager.getInstance().getBorderRadiusLarge()
        );
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(createRouteContainer());
        center.add(createInfoPanel());
        return center;
    }

    private JPanel createRouteContainer() {
        JPanel mainContainer = panelFactory.create(
                themeManager.getBackgroundColor(),
                0, 0, 0
        );
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        routeContainer = panelFactory.create(
                themeManager.getBackgroundColor(),
                0, 0, 0
        );
        routeContainer.setLayout(new BoxLayout(routeContainer, BoxLayout.Y_AXIS));
        routeContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel scrollWrapper = panelFactory.create(
                themeManager.getBackgroundColor(),
                0, 0, 0
        );
        scrollWrapper.setLayout(new BorderLayout());
        scrollWrapper.add(routeContainer, BorderLayout.PAGE_START);

        JScrollPane scrollPane = new JScrollPane(scrollWrapper,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.setBackground(themeManager.getBackgroundColor());
        scrollPane.getViewport().setBackground(themeManager.getBackgroundColor());
        scrollPane.setBorder(null);

        JPanel wrapper = panelFactory.create(
                themeManager.getBackgroundColor(),
                Integer.MAX_VALUE, 500, 0
        );
        wrapper.setLayout(new BorderLayout());
        wrapper.add(scrollPane, BorderLayout.CENTER);

        mainContainer.add(Box.createVerticalStrut(5));
        mainContainer.add(wrapper);
        return mainContainer;
    }

    private RoundingOfPanels createInfoPanel() {
        infoPanel = panelFactory.create(
                themeManager.getWhite(),
                Integer.MAX_VALUE, 600, 30
        );
        setInfoMessage("No chosen route.");
        return infoPanel;
    }

    /* --------------------- RIGHT PANEL (SAVED / LOCATIONS) --------------------- */
    private JPanel createRightPanel() throws IOException, FontFormatException {
        JPanel rightPanel = panelFactory.create(
                themeManager.getBackgroundColor(),
                570, Integer.MAX_VALUE,
                sizeManager.getInstance().getBorderRadiusLarge()
        );
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        savedLabel = createSavedLabel();
        locationsLabel = createLocationsLabel();

        rightPanel.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingLarge()));
        rightPanel.add(locationsLabel);
        rightPanel.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
        rightPanel.add(createLocationsPanel());
        rightPanel.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
        rightPanel.add(savedLabel);
        rightPanel.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
        rightPanel.add(createSavedPanel());
        rightPanel.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
        rightPanel.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
        return rightPanel;
    }

    private JLabel createSavedLabel() throws IOException, FontFormatException {
        savedLabel = labelFactory.create(
                "Saved Routes",
                loadCustomFont(fonts.DM_SANS_BOLD, 14f),
                themeManager.getForegroundColor()
        );
        savedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return savedLabel;
    }

    private JLabel createLocationsLabel() throws IOException, FontFormatException {
        locationsLabel = labelFactory.create(
                "Locations",
                loadCustomFont(fonts.DM_SANS_BOLD, 14f),
                themeManager.getForegroundColor()
        );
        locationsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return locationsLabel;
    }

    private RoundingOfPanels createLocationsPanel() {
        locationPanel = createStatusPanel();
        locationPanel.setPreferredSize(new Dimension(550, 500));
        locationPanel.setMinimumSize(new Dimension(550, 500));
        locationPanel.setMaximumSize(new Dimension(550, 500));
        setPanelPlaceholder(locationPanel, "Work in progress");
        return locationPanel;
    }

    private RoundingOfPanels createSavedPanel() {
        savedPanel = createStatusPanel();
        savedPanel.setPreferredSize(new Dimension(550, 300));
        savedPanel.setMinimumSize(new Dimension(550, 300));
        savedPanel.setMaximumSize(new Dimension(550, 300));
        setPanelPlaceholder(savedPanel, "No saved routes.");
        return savedPanel;
    }

    private RoundingOfPanels createStatusPanel() {
        RoundingOfPanels panel = panelFactory.create(
                themeManager.getBlue(),
                360, 170,
                sizeManager.getInstance().getBorderRadiusLarge()
        );
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private void setPanelPlaceholder(JPanel panel, String message) {
        JLabel placeHolder = Images.getInstance().getPlaceholderLabel(100, 100);
        placeHolder.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel placeholderText = new JLabel(message);
        try { placeholderText.setFont(loadCustomFont(fonts.DM_SANS_ITALIC, 14)); }
        catch (Exception ignored) {}
        placeholderText.setForeground(themeManager.getForegroundColor());
        placeholderText.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.removeAll();
        panel.add(Box.createVerticalGlue());
        panel.add(placeHolder);
        panel.add(Box.createVerticalStrut(10));
        panel.add(placeholderText);
        panel.add(Box.createVerticalGlue());
        panel.revalidate();
        panel.repaint();
    }

    /* --------------------------------------------------------------- */
    /*                     ROUTE SEARCH & DISPLAY                      */
    /* --------------------------------------------------------------- */
    private void searchRoutes() {
        try {
            String from = currentLocation.getText().trim();
            String to = destination.getText().trim();

            if (from.isEmpty() || to.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter both current location and destination.",
                        "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String category = getUserCategory();

            if (currentFilter.equals("all")) {
                // ONE source of truth – includes direct & transfer routes
                ArrayList<ArrayList<RouteData>> allPossible = routeManager.findRoutesWithTransfers(from, to, category);
                allPossible = removeDuplicateRouteOptions(allPossible);   // optional

                if (allPossible.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "No routes found from " + from + " to " + to,
                            "No Results", JOptionPane.INFORMATION_MESSAGE);
                    displayAllRoutes(allPossible, from, to);
                } else {
                    displayAllRoutes(allPossible, from, to);
                    // show first route in info panel
                    ArrayList<RouteData> first = allPossible.get(0);
                    if (first.size() == 1) displayRouteInfo(first.get(0));
                    else displayTransferRouteInfo(first);
                }
            } else {
                // ----- FILTERED SEARCH (best according to priority) -----
                RouteData bestDirect = navigationFacade.findBestRoute(from, to, category, currentFilter);
                if (bestDirect != null) {
                    ArrayList<ArrayList<RouteData>> single = new ArrayList<>();
                    displayAllRoutes(single, from, to);
                    displayRouteInfo(bestDirect);
                    return;
                }

                // still no direct → look for best transfer
                ArrayList<ArrayList<RouteData>> transfers = routeManager.findRoutesWithTransfers(from, to, category);
                if (!transfers.isEmpty()) {
                    RouteStrategy strategy = getStrategyForPriority(currentFilter);
                    navigationFacade.setRouteStrategy(strategy);
                    Optional<ArrayList<RouteData>> bestTransfer = strategy.findBestTransferRoute(transfers);
                    if (bestTransfer.isPresent()) {
                        ArrayList<ArrayList<RouteData>> single = new ArrayList<>();
                        single.add(bestTransfer.get());
                        displayAllRoutes(single, from, to);
                        displayTransferRouteInfo(bestTransfer.get());
                        return;
                    }
                }
                JOptionPane.showMessageDialog(this,
                        "No routes found for the selected filter.",
                        "No Results", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getUserCategory() {
        String category = "Student";
        try {
            org.example.DatabaseManager.DatabaseInstance db = org.example.DatabaseManager.DatabaseInstance.getInstance();
            String pswd = db.getActivePassword();
            if (pswd != null && !pswd.isEmpty()) {
                char first = pswd.charAt(0);
                if (first == '1') category = "Regular";
                else if (first == '2') category = "Student";
                else if (first == '3') category = "PWD";
                else if (first == '4') category = "Senior Citizen";
            }
        } catch (Exception ignored) {}
        return category;
    }

    private boolean bothFieldsFilled() {
        return !currentLocation.getText().trim().isEmpty() && !destination.getText().trim().isEmpty();
    }

    /* --------------------- DISPLAY ALL ROUTES (direct + transfer) --------------------- */
    private void displayAllRoutes(ArrayList<ArrayList<RouteData>> routes, String from, String to)
            throws IOException, FontFormatException {
        routeContainer.removeAll();

        if (routes.isEmpty()) {
            JLabel noRoutes = labelFactory.create(
                    "No routes found from " + from + " to " + to,
                    loadCustomFont(fonts.DM_SANS_REGULAR, 14),
                    themeManager.getGray()
            );
            noRoutes.setAlignmentX(Component.CENTER_ALIGNMENT);
            routeContainer.add(Box.createVerticalStrut(50));
            routeContainer.add(noRoutes);
            routeContainer.add(Box.createVerticalStrut(50));
        } else {
            for (int i = 0; i < routes.size(); i++) {
                ArrayList<RouteData> option = routes.get(i);
                JPanel panel;
                if (option.size() == 1) {
                    // ----- DIRECT -----
                    panel = factoryPanel.createRoutePanel(option.get(0), this::displayRouteInfo);
                } else {
                    // ----- TRANSFER -----
                    panel = factoryPanel.createTransferRoutePanel(option, this::displayTransferRouteInfo);
                }
                // optional badge
                addTypeBadge(panel, option);
                panel.setAlignmentX(Component.CENTER_ALIGNMENT);
                routeContainer.add(panel);
                if (i < routes.size() - 1)
                    routeContainer.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
            }
        }
        routeContainer.revalidate();
        routeContainer.repaint();
    }

    /** optional visual cue – “Direct” or “2 Transfer” */
    private void addTypeBadge(JPanel panel, ArrayList<RouteData> option) throws IOException, FontFormatException {
        if (!(panel.getLayout() instanceof BorderLayout)) return;
        String txt = (option.size() == 1) ? "Direct" : (option.size() - 1) + " Transfer";
        Color col = (option.size() == 1) ? new Color(0, 150, 0) : new Color(200, 100, 0);
        JLabel badge = labelFactory.create(txt,
                loadCustomFont(fonts.DM_SANS_BOLD, 11f), col);
        badge.setOpaque(true);
        badge.setBackground(themeManager.getWhite());
        badge.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        ((BorderLayout) panel.getLayout()).addLayoutComponent(badge, BorderLayout.NORTH);
        panel.add(badge, BorderLayout.NORTH);
    }

    /* --------------------- INFO PANEL (single route) --------------------- */
    private void displayRouteInfo(RouteData route) {
        infoPanel.removeAll();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBackground(themeManager.getWhite());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        try {
            JPanel content = panelFactory.create(null, 0, 0, 0);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setOpaque(false);

            content.add(createHeaderPanel(route));
            content.add(Box.createVerticalStrut(30));
            content.add(createInfoSection(route));
            content.add(Box.createVerticalStrut(20));
            content.add(createStopsSection(route));
            content.add(Box.createVerticalGlue());
            content.add(Box.createVerticalStrut(20));
            content.add(createButtonSection(route));

            infoPanel.add(content, BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
            setInfoMessage("Error displaying route info.");
        }
        infoPanel.revalidate();
        infoPanel.repaint();
    }

    private JPanel createHeaderPanel(RouteData route) throws IOException, FontFormatException {
        JPanel p = panelFactory.create(null, 0, 0, 0);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setOpaque(false);

        JLabel title = labelFactory.create(route.getRoute(),
                loadCustomFont(fonts.DM_SANS_BOLD, 20f), themeManager.getBlack());
        JLabel eta = labelFactory.create(route.getEta() + " min",
                loadCustomFont(fonts.DM_SANS_BOLD, 20f), themeManager.getBlack());

        p.add(title);
        p.add(Box.createHorizontalGlue());
        p.add(eta);
        return p;
    }

    private JPanel createInfoSection(RouteData route) throws IOException, FontFormatException {
        JPanel sec = panelFactory.create(null, 0, 0, 0);
        sec.setLayout(new BoxLayout(sec, BoxLayout.Y_AXIS));
        sec.setOpaque(false);

        JPanel row1 = panelFactory.create(null, 0, 0, 0);
        row1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row1.add(createInfoItem("Transfers:", String.valueOf(route.getTransfers())));
        row1.add(createInfoItem("Stops:", String.valueOf(route.getStops())));

        JPanel row2 = panelFactory.create(null, 0, 0, 0);
        row2.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row2.add(createInfoItem("Fare:", "Php " + String.format("%.2f", route.getFare())));
        row2.add(createInfoItem("Details:", route.getDetails()));

        JPanel row3 = panelFactory.create(null, 0, 0, 0);
        row3.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row3.add(createInfoItem("Capacity:", "N/A"));
        row3.add(createInfoItem("Est. Arrival:", "N/A"));

        sec.add(row1); sec.add(row2); sec.add(row3);
        return sec;
    }

    private JPanel createInfoItem(String label, String value) throws IOException, FontFormatException {
        JPanel p = panelFactory.create(null, 0, 0, 0);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setOpaque(false);
        p.add(labelFactory.create(label + " ", loadCustomFont(fonts.DM_SANS_BOLD, 13f), themeManager.getBlack()));
        p.add(labelFactory.create(value, loadCustomFont(fonts.DM_SANS_REGULAR, 13f), themeManager.getBlack().brighter()));
        p.add(Box.createHorizontalGlue());
        return p;
    }

    private JPanel createStopsSection(RouteData route) throws IOException, FontFormatException {
        JPanel sec = panelFactory.create(null, 0, 0, 0);
        sec.setLayout(new BoxLayout(sec, BoxLayout.Y_AXIS));
        sec.setOpaque(false);
        sec.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBlack().brighter().brighter(), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        String stops = String.join(" -> ", route.getRouteStops());
        sec.add(labelFactory.create("Route Stops",
                loadCustomFont(fonts.DM_SANS_BOLD, 13f), themeManager.getBlack()));
        sec.add(Box.createVerticalStrut(5));
        sec.add(labelFactory.create("<html>" + stops + "</html>",
                loadCustomFont(fonts.DM_SANS_REGULAR, 13f), themeManager.getBlack().brighter()));
        return sec;
    }

    private JPanel createButtonSection(RouteData route) throws IOException, FontFormatException {
        JPanel p = panelFactory.create(null, 0, 0, 0);
        p.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        p.setOpaque(false);

        RoundingOfButtons save = buttonFactory.create(
                "Save Route",
                loadCustomFont(fonts.DM_SANS_BOLD, 13f),
                themeManager.getYellow(),
                themeManager.getBlack(),
                160, 40,
                sizeManager.getInstance().getBorderRadiusLarge()
        );
        save.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { setSavedRoutes(route); }
            @Override public void mouseEntered(MouseEvent e) { save.setBackground(themeManager.getBlue()); }
            @Override public void mouseExited(MouseEvent e) { save.setBackground(themeManager.getYellow()); }
        });

        RoundingOfButtons take = buttonFactory.create(
                "Take Route",
                loadCustomFont(fonts.DM_SANS_BOLD, 13f),
                themeManager.getGreen(),
                themeManager.getWhite(),
                180, 40,
                sizeManager.getInstance().getBorderRadiusLarge()
        );

        p.add(save);
        p.add(take);
        return p;
    }

    /* --------------------- INFO PANEL (transfer route) --------------------- */
    private void displayTransferRouteInfo(ArrayList<RouteData> transfer) {
        infoPanel.removeAll();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBackground(themeManager.getWhite());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        try {
            JPanel content = panelFactory.create(null, 0, 0, 0);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setOpaque(false);

            // header
            JPanel header = panelFactory.create(null, 0, 0, 0);
            header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
            header.setOpaque(false);
            JLabel title = new JLabel("Transfer Route (" + transfer.size() + " segments)");
            title.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 20f));
            title.setForeground(themeManager.getBlack());

            int totalETA = transfer.stream().mapToInt(RouteData::getEta).sum();
            JLabel eta = labelFactory.create(totalETA + " min",
                    loadCustomFont(fonts.DM_SANS_BOLD, 14f), themeManager.getBlack());

            header.add(title);
            header.add(Box.createHorizontalGlue());
            header.add(eta);

            content.add(header);
            content.add(Box.createVerticalStrut(20));
            content.add(createTransferSummarySection(transfer));
            content.add(Box.createVerticalStrut(20));

            // segments
            JPanel segments = panelFactory.create(null, 0, 0, 0);
            segments.setLayout(new BoxLayout(segments, BoxLayout.Y_AXIS));
            segments.setOpaque(false);
            for (int i = 0; i < transfer.size(); i++) {
                segments.add(createSegmentPanel(transfer.get(i), i + 1));
                if (i < transfer.size() - 1) segments.add(Box.createVerticalStrut(10));
            }
            content.add(segments);
            content.add(Box.createVerticalGlue());
            content.add(Box.createVerticalStrut(5));

            // save button (saves first segment only)
            JPanel btnSec = panelFactory.create(null, 0, 0, 0);
            btnSec.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            btnSec.setOpaque(false);
            RoundingOfButtons save = buttonFactory.create(
                    "Save Route",
                    loadCustomFont(fonts.DM_SANS_BOLD, 13f),
                    themeManager.getYellow(),
                    themeManager.getBlack(),
                    160, 40,
                    sizeManager.getInstance().getBorderRadiusLarge()
            );
            save.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { setSavedRoutes(transfer.get(0)); }
                @Override public void mouseEntered(MouseEvent e) { save.setBackground(themeManager.getBlue()); }
                @Override public void mouseExited(MouseEvent e) { save.setBackground(themeManager.getYellow()); }
            });
            btnSec.add(save);
            content.add(btnSec);

            infoPanel.add(content, BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
            setInfoMessage("Error displaying transfer route info.");
        }
        infoPanel.revalidate();
        infoPanel.repaint();
    }

    private JPanel createTransferSummarySection(ArrayList<RouteData> transfer) throws IOException, FontFormatException {
        JPanel sec = panelFactory.create(null, 0, 0, 0);
        sec.setLayout(new BoxLayout(sec, BoxLayout.Y_AXIS));
        sec.setOpaque(false);

        double fare = 0;
        int stops = 0;
        for (RouteData r : transfer) { fare += r.getFare(); stops += r.getStops(); }
        int transfers = transfer.size() - 1;

        JPanel r1 = panelFactory.create(null, 0, 0, 0);
        r1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        r1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        r1.add(createInfoItem("Transfers:", String.valueOf(transfers)));
        r1.add(createInfoItem("Total Stops:", String.valueOf(stops)));

        JPanel r2 = panelFactory.create(null, 0, 0, 0);
        r2.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        r2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        r2.add(createInfoItem("Total Fare:", "Php " + String.format("%.2f", fare)));
        r2.add(createInfoItem("Segments:", String.valueOf(transfer.size())));

        sec.add(r1); sec.add(r2);
        return sec;
    }

    private JPanel createSegmentPanel(RouteData seg, int num) throws IOException, FontFormatException {
        JPanel p = panelFactory.create(null, 0, 0, 0);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBlack().brighter().brighter(), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        p.add(labelFactory.create("Segment " + num + ": " + seg.getRoute(),
                loadCustomFont(fonts.DM_SANS_BOLD, 15f), themeManager.getBlack()));
        p.add(Box.createVerticalStrut(10));

        JPanel info = panelFactory.create(null, 0, 0, 0);
        info.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        info.add(labelFactory.create(seg.getFromLocation() + " -> " + seg.getDestination(),
                loadCustomFont(fonts.DM_SANS_BOLD, 13f), themeManager.getBlack().brighter()));
        info.add(createInfoItem("    Fare:", "Php " + String.format("%.2f", seg.getFare())));
        info.add(createInfoItem("Stops:", String.valueOf(seg.getStops())));
        info.add(createInfoItem("ETA:", seg.getEta() + " min"));
        p.add(info);

        String stops = seg.getRouteStops() != null && !seg.getRouteStops().isEmpty()
                ? String.join(" -> ", seg.getRouteStops())
                : seg.getFromLocation() + " -> " + seg.getDestination();

        JPanel stopsSec = panelFactory.create(null, 0, 0, 0);
        stopsSec.setLayout(new BoxLayout(stopsSec, BoxLayout.Y_AXIS));
        stopsSec.setOpaque(false);
        stopsSec.add(labelFactory.create("Route Stops:",
                loadCustomFont(fonts.DM_SANS_BOLD, 12f), themeManager.getBlack()));
        stopsSec.add(Box.createVerticalStrut(3));
        stopsSec.add(labelFactory.create("<html>" + stops + "</html>",
                loadCustomFont(fonts.DM_SANS_REGULAR, 12f), themeManager.getBlack().brighter()));
        p.add(stopsSec);
        return p;
    }

    /* --------------------------------------------------------------- */
    /*                     SAVED ROUTES                               */
    /* --------------------------------------------------------------- */
    public void setSavedRoutes(RouteData route) {
        // (you can add duplicate-check here if you want)
        savedRoutes.add(route);
        refreshSavedRoutesPanel();
    }

    private void refreshSavedRoutesPanel() {
        savedPanel.removeAll();
        if (savedRoutes.isEmpty()) {
            setPanelPlaceholder(savedPanel, "No saved routes.");
        } else {
            savedPanel.setLayout(new BoxLayout(savedPanel, BoxLayout.Y_AXIS));
            savedPanel.add(Box.createVerticalStrut(20));
            for (RouteData r : savedRoutes) {
                RoundingOfPanels rp = panelFactory.create(
                        themeManager.getWhite(),
                        500, 40, 30
                );
                rp.setLayout(new BorderLayout());
                rp.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                JLabel lbl = new JLabel("<html>" + r.getFromLocation() + " to " + r.getDestination() +
                        " <b><i>&nbsp;via&nbsp;</i></b>" + r.getRoute() +
                        " &nbsp;&nbsp;(" + r.getEta() + " min)</html>");
                try { lbl.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 14f)); }
                catch (Exception e) { lbl.setFont(new Font("SansSerif", Font.PLAIN, 14)); }
                lbl.setForeground(themeManager.getBlack());
                lbl.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) { displayRouteInfo(r); }
                    @Override public void mouseEntered(MouseEvent e) {
                        lbl.setForeground(themeManager.getGreen());
                        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }
                    @Override public void mouseExited(MouseEvent e) { lbl.setForeground(themeManager.getBlack()); }
                });

                JButton del = buttonFactory.create("X",
                        new Font("SansSerif", Font.PLAIN, 13),
                        themeManager.getWhite(), Color.RED, 30, 30, 0);
                del.setBorder(BorderFactory.createEmptyBorder());
                del.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                del.addActionListener(e -> {
                    savedRoutes.remove(r);
                    refreshSavedRoutesPanel();
                });

                rp.add(lbl, BorderLayout.CENTER);
                rp.add(del, BorderLayout.EAST);
                savedPanel.add(rp);
                savedPanel.add(Box.createVerticalStrut(5));
            }
        }
        savedPanel.revalidate();
        savedPanel.repaint();
    }

    /* --------------------------------------------------------------- */
    /*                     HELPER METHODS                              */
    /* --------------------------------------------------------------- */
    private RouteStrategy getStrategyForPriority(String priority) {
        switch (priority.toLowerCase()) {
            case "distance": return new ShortestDistanceStrategy();
            case "time": case "eta": return new ShortestTimeStrategy();
            case "transfers": case "stops": return new LeastTransferStrategy();
            case "fare": return new CheapestFareStrategy();
            default: return new ShortestTimeStrategy();
        }
    }

    /** optional – removes identical route sequences */
    private ArrayList<ArrayList<RouteData>> removeDuplicateRouteOptions(ArrayList<ArrayList<RouteData>> list) {
        Set<String> seen = new HashSet<>();
        ArrayList<ArrayList<RouteData>> uniq = new ArrayList<>();
        for (ArrayList<RouteData> route : list) {
            StringBuilder key = new StringBuilder();
            for (RouteData seg : route) {
                key.append(seg.getRoute()).append("→")
                        .append(seg.getFromLocation()).append("→")
                        .append(seg.getDestination()).append(";");
            }
            if (seen.add(key.toString())) uniq.add(route);
        }
        return uniq;
    }

    public void setInfoMessage(String msg) { setPanelPlaceholder(infoPanel, msg); }

    @Override
    public void onThemeChange(boolean isDarkMode) {
        setBackground(themeManager.getBackgroundColor());
        if (container != null) container.setBackground(themeManager.getBackgroundColor());
        if (currentLocation != null) {
            currentLocation.setBackground(themeManager.getComponentsColor());
            currentLocation.setForeground(themeManager.getForegroundColor());
        }
        if (destination != null) {
            destination.setBackground(themeManager.getComponentsColor());
            destination.setForeground(themeManager.getForegroundColor());
        }
        if (savedLabel != null) savedLabel.setForeground(themeManager.getForegroundColor());
        if (wcQuestion != null) wcQuestion.setForeground(themeManager.getForegroundColor());

        SwingUtilities.invokeLater(() -> {
            themeManager.applyThemeToContainers(this);
            repaint();
        });
    }

    public void dispose() {
        if (themeManager != null) themeManager.removeThemeChangeListener(this);
    }

    private static Font loadCustomFont(String path, float size) throws IOException, FontFormatException {
        Font f = Font.createFont(Font.TRUETYPE_FONT, new File(path));
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
        return f.deriveFont(size);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame f = new JFrame("mainPage Preview");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                Consumer<String> dummy = System.out::println;
                mainPage p = new mainPage(dummy);
                f.add(p);
                f.setSize(1920, 1080);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }
}