package org.example.gui.pages;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.*;

import org.example.DatabaseManager.RouteDatabase.ObserversClasses.JeepneyObserver;
import org.example.DatabaseManager.RouteDatabase.RouteManager;
import org.example.DatabaseManager.RouteDatabase.RouteComponent;
import org.example.DatabaseManager.RouteDatabase.Routes;
import org.example.gui.appManager.*;
import org.example.gui.components.dialogs.ConfirmDialog;
import org.example.gui.components.dialogs.SuccessDialog;
import org.example.gui.components.factories.*;
import org.example.gui.components.factories.RoutePanelFactory.DirectRouteFactory;
import org.example.gui.components.factories.RoutePanelFactory.RouteFactory;
import org.example.gui.components.factories.RoutePanelFactory.TransferRouteFactory;
import org.example.gui.components.base.RoundedButton;
import org.example.gui.components.base.RoundedPanel;
import org.example.gui.components.base.RoundedTextField;
import org.example.gui.components.elements.UserButton;
import org.example.gui.components.panels.RoutePanel;
import org.example.gui.pages.managers.StateManager;
import org.example.gui.pages.managers.MainPageManager;
import org.example.gui.resources.Images;
import org.example.gui.resources.Fonts;

public class MainPage extends JPanel implements ThemeManager.ThemeChangeListener {
    private Consumer<String> cardChanger;
    private JPanel container, routeContainer, welcomeContainer, inputContainer, infoPanel;
    private RoundedTextField currentLocation, destination;
    private RoundedPanel textContainer, locationPanel, savedPanel;
    private JLabel locationsLabel, savedLabel, wcQuestion;
    private RoundedButton submitButton, userButton;
    private MainPageManager pageManager;
    private ThemeManager themeManager;
    private ButtonGroup filterButtonGroup;
    private RouteManager routeManager;
    private ArrayList<RouteComponent> displayedRoute;
    private StateManager stateManager;
    private RoundedPanel activeTripPanel;
    private List<RoutePanel> routePanelList = new ArrayList<>();

    public MainPage(Consumer<String> cardChanger) throws IOException, FontFormatException, SQLException {
        this.cardChanger = cardChanger;
        this.themeManager = ThemeManager.getInstance();
        this.themeManager.addThemeChangeListener(this);
        setupPanel();
        refreshSavedRoutesPanel();
    }

    private void setupPanel() throws IOException, FontFormatException, SQLException {
        this.pageManager = new MainPageManager();
        this.routeManager = new RouteManager();
        this.stateManager = new StateManager();
        routeManager.addJeepneyObserver(new UIRefreshObserver());
        setLayout(new BorderLayout());
        setPreferredSize(SizeManager.getInstance().flexibleWidth(1920, 1080));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        setBackground(themeManager.getBackgroundColor());

        container = createContainer();
        add(container, BorderLayout.CENTER);
    }

    private JPanel createContainer() throws IOException, FontFormatException, SQLException {
        JPanel center = PanelFactory.create(themeManager.getBackgroundColor(), 0, 0, 0);
        center.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        JPanel contentPane = PanelFactory.create(
                themeManager.getBackgroundColor(),
                1920, 1080,
                SizeManager.getInstance().getBorderRadiusLarge()
        );
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
        contentPane.add(createLeftJPanel());
        contentPane.add(createCenterPanel());
        contentPane.add(createRightPanel());

        center.add(contentPane);
        return center;
    }

    // --------------------- LEFT PANEL ---------------------
    private JPanel createLeftJPanel() throws IOException, FontFormatException {
        JPanel leftPanel = PanelFactory.create(
                themeManager.getYellow(),
                350, Integer.MAX_VALUE,
                0
        );
        leftPanel.setLayout(new BorderLayout());

        DarkModeToggle darkMode = new DarkModeToggle();
        userButton = UserButton.createUserButton();
        userButton.setPreferredSize(new Dimension(200, 30));

        JPanel header = PanelFactory.create(null, 350, 50,
                SizeManager.getInstance().getBorderRadiusSmall());
        header.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));
        header.setOpaque(false);
        header.add(userButton);
        header.add(darkMode);

        JPanel contentPanel = PanelFactory.create(
                themeManager.getYellow(),
                0, 0,
                SizeManager.getInstance().getBorderRadiusSmall()
        );
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        contentPanel.add(createTextContainer());

        JPanel wrapper = PanelFactory.create(null, 0, 0,
                SizeManager.getInstance().getBorderRadiusSmall());
        wrapper.setLayout(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(contentPanel, BorderLayout.NORTH);

        leftPanel.add(header, BorderLayout.NORTH);
        leftPanel.add(wrapper, BorderLayout.CENTER);
        return leftPanel;
    }

    private JPanel createTextContainer() throws IOException, FontFormatException {
        textContainer = PanelFactory.create(
                themeManager.getYellow(),
                0, 0,
                SizeManager.getInstance().getBorderRadiusLarge()
        );
        textContainer.setLayout(new BoxLayout(textContainer, BoxLayout.Y_AXIS));
        textContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        activeTripPanel = createActiveTripPanel();
        activeTripPanel.setVisible(false);
        textContainer.add(activeTripPanel);
        textContainer.add(Box.createVerticalStrut(15));

        textContainer.add(createWelcomeContainer());
        return textContainer;
    }

    private RoundedPanel createActiveTripPanel() throws IOException, FontFormatException {
        RoundedPanel panel = PanelFactory.create(themeManager.getGreen(), 280, 50, SizeManager.getInstance().getBorderRadiusLarge());
        panel.setLayout(new BorderLayout(8, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (stateManager.isInTransit()) {
                    displayInTransitView();
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(themeManager.getGreen());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(themeManager.getGreen());
            }
        });

        return panel;
    }

    private void updateActiveTripPanel() {
        try {
            activeTripPanel.removeAll();

            if (!stateManager.isInTransit()) {
                activeTripPanel.setVisible(false);
                return;
            }

            StateManager.ActiveTrip trip = stateManager.getActiveTrip();
            if (trip == null) {
                activeTripPanel.setVisible(false);
                return;
            }

            JPanel content = PanelFactory.create(null, 0, 0, 0);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setOpaque(false);

            JLabel title = LabelFactory.create(
                    "Active Trip",
                    loadCustomFont(Fonts.DM_SANS_BOLD, 11f),
                    themeManager.getBlack()
            );
            title.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel route = LabelFactory.create(
                    trip.getRoute().getRoute(),
                    loadCustomFont(Fonts.DM_SANS_BOLD, 12f),
                    themeManager.getBlack()
            );
            route.setAlignmentX(Component.LEFT_ALIGNMENT);


            content.add(title);
            content.add(Box.createVerticalStrut(2));
            content.add(route);
            content.add(Box.createVerticalStrut(2));

            activeTripPanel.add(content, BorderLayout.CENTER);
            activeTripPanel.setVisible(true);
            activeTripPanel.revalidate();
            activeTripPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            activeTripPanel.setVisible(false);
        }
    }

    private JPanel createWelcomeContainer() throws IOException, FontFormatException {
        welcomeContainer = PanelFactory.create(
                themeManager.getYellow(),
                0, 0,
                SizeManager.getInstance().getBorderRadiusLarge()
        );
        welcomeContainer.setLayout(new BoxLayout(welcomeContainer, BoxLayout.Y_AXIS));
        welcomeContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        wcQuestion = LabelFactory.create(
                "<html>Where do you want<br>to go?</html>",
                loadCustomFont(Fonts.DM_SANS_BOLD, 22f),
                themeManager.getForegroundColor()
        );
        wcQuestion.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel inputContainerPanel = createInputContainer();
        inputContainerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        welcomeContainer.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingLarge()));
        welcomeContainer.add(wcQuestion);
        welcomeContainer.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingSmall()));
        welcomeContainer.add(inputContainerPanel);
        return welcomeContainer;
    }

    private JPanel createInputContainer() throws IOException, FontFormatException {
        inputContainer = PanelFactory.create(
                themeManager.getYellow(),
                0, 0,
                SizeManager.getInstance().getBorderRadiusLarge()
        );
        inputContainer.setLayout(new BoxLayout(inputContainer, BoxLayout.Y_AXIS));
        inputContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        currentLocation = TextfieldFactory.create(
                "Start",
                280, 40,
                26,
                loadCustomFont(Fonts.DM_SANS_ITALIC, SizeManager.getInstance().getTextSmall()),
                themeManager.getComponentsColor(),
                themeManager.getForegroundColor()
        );
        currentLocation.setAlignmentX(Component.LEFT_ALIGNMENT);

        destination = TextfieldFactory.create(
                "End",
                280, 40,
                26,
                loadCustomFont(Fonts.DM_SANS_ITALIC, SizeManager.getInstance().getTextSmall()),
                themeManager.getComponentsColor(),
                themeManager.getForegroundColor()
        );

        submitButton = ButtonFactory.create(
                "View Available Routes",
                loadCustomFont(Fonts.DM_SANS_BOLD, SizeManager.getInstance().getTextSmall()),
                themeManager.getGreen(),
                themeManager.getWhite(),
                250, 40,
                SizeManager.getInstance().getBorderRadiusLarge()
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

        inputContainer.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingSmall()));
        inputContainer.add(currentLocation);
        inputContainer.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingMedium()));
        inputContainer.add(destination);
        inputContainer.add(Box.createVerticalStrut(50));
        inputContainer.add(createFilterPanel());
        inputContainer.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingMedium()));
        inputContainer.add(submitButton);
        inputContainer.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingSmall()));
        return inputContainer;
    }

    private JPanel createFilterPanel() throws IOException, FontFormatException {
        JPanel filterContainer = PanelFactory.create(
                themeManager.getYellow(),
                0, 0,
                SizeManager.getInstance().getBorderRadiusLarge()
        );
        filterContainer.setLayout(new BoxLayout(filterContainer, BoxLayout.Y_AXIS));
        filterContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel filter = LabelFactory.create(
                "Filter for",
                loadCustomFont(Fonts.DM_SANS_BOLD, 15),
                themeManager.getForegroundColor()
        );
        filter.setAlignmentX(Component.LEFT_ALIGNMENT);

        JRadioButton all = RadioFactory.create(
                "All Routes",
                loadCustomFont(Fonts.DM_SANS_REGULAR, 13),
                e -> pageManager.setFilter("all")
        );
        all.setActionCommand("all");
        all.setSelected(true);
        all.setBackground(null);

        JRadioButton time = RadioFactory.create(
                "Shortest Time",
                loadCustomFont(Fonts.DM_SANS_REGULAR, 13),
                e -> pageManager.setFilter("time")
        );
        time.setActionCommand("time");
        time.setBackground(null);

        JRadioButton distance = RadioFactory.create(
                "Shortest Distance",
                loadCustomFont(Fonts.DM_SANS_REGULAR, 13),
                e -> pageManager.setFilter("distance")
        );
        distance.setActionCommand("distance");
        distance.setBackground(null);

        JRadioButton leastTransfer = RadioFactory.create(
                "Least Transfers",
                loadCustomFont(Fonts.DM_SANS_REGULAR, 13),
                e -> pageManager.setFilter("transfers")
        );
        leastTransfer.setActionCommand("transfers");
        leastTransfer.setBackground(null);

        JRadioButton cheapest = RadioFactory.create(
                "Cheapest Fare",
                loadCustomFont(Fonts.DM_SANS_REGULAR, 13),
                e -> pageManager.setFilter("fare")
        );
        cheapest.setActionCommand("fare");
        cheapest.setBackground(null);

        filterButtonGroup = new ButtonGroup();
        filterButtonGroup.add(all);
        filterButtonGroup.add(time);
        filterButtonGroup.add(distance);
        filterButtonGroup.add(cheapest);
        filterButtonGroup.add(leastTransfer);

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

    // --------------------- CENTER PANEL ---------------------
    private JPanel createCenterPanel() throws IOException, FontFormatException {
        JPanel center = PanelFactory.create(
                themeManager.getBackgroundColor(),
                1000, Integer.MAX_VALUE,
                SizeManager.getInstance().getBorderRadiusLarge()
        );
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(createRouteContainer());
        center.add(createInfoPanel());
        return center;
    }

    private JPanel createRouteContainer() {
        JPanel mainContainer = PanelFactory.create(
                themeManager.getBackgroundColor(),
                0, 0, 0
        );
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        routeContainer = PanelFactory.create(
                themeManager.getBackgroundColor(),
                0, 0, 0
        );
        routeContainer.setLayout(new BoxLayout(routeContainer, BoxLayout.Y_AXIS));
        routeContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel scrollWrapper = PanelFactory.create(
                themeManager.getBackgroundColor(),
                0, 0, 0
        );
        scrollWrapper.setLayout(new BorderLayout());
        scrollWrapper.add(routeContainer, BorderLayout.PAGE_START);

        JScrollPane scrollPane = new JScrollPane(scrollWrapper,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setBackground(themeManager.getBackgroundColor());
        scrollPane.getViewport().setBackground(themeManager.getBackgroundColor());
        scrollPane.setBorder(null);

        JPanel wrapper = PanelFactory.create(
                themeManager.getBackgroundColor(),
                Integer.MAX_VALUE, 500, 0
        );
        wrapper.setLayout(new BorderLayout());
        wrapper.add(scrollPane, BorderLayout.CENTER);

        mainContainer.add(Box.createVerticalStrut(5));
        mainContainer.add(wrapper);
        return mainContainer;
    }

    private JPanel createInfoPanel() {
        infoPanel = PanelFactory.create(
                themeManager.getWhite(),
                Integer.MAX_VALUE, 600, 0
        );
        setInfoMessage("No chosen route.");
        return infoPanel;
    }

    // --------------------- RIGHT PANEL ---------------------
    private JPanel createRightPanel() throws IOException, FontFormatException, SQLException {
        JPanel rightPanel = PanelFactory.create(
                themeManager.getBlue(),
                570, Integer.MAX_VALUE,
                0
        );
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        savedLabel = createSavedLabel();
        locationsLabel = createLocationsLabel();

        rightPanel.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingLarge()));
        rightPanel.add(locationsLabel);
        rightPanel.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingSmall()));
        rightPanel.add(createLocationsPanel());
        rightPanel.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingSmall()));
        rightPanel.add(savedLabel);
        rightPanel.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingSmall()));
        rightPanel.add(createSavedPanel());
        rightPanel.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingSmall()));
        rightPanel.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingSmall()));
        return rightPanel;
    }

    private JLabel createSavedLabel() throws IOException, FontFormatException {
        savedLabel = LabelFactory.create(
                "Saved Routes",
                loadCustomFont(Fonts.DM_SANS_BOLD, 14f),
                themeManager.getForegroundColor()
        );
        savedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return savedLabel;
    }

    private JLabel createLocationsLabel() throws IOException, FontFormatException {
        locationsLabel = LabelFactory.create(
                "Locations",
                loadCustomFont(Fonts.DM_SANS_BOLD, 14f),
                themeManager.getForegroundColor()
        );
        locationsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return locationsLabel;
    }

    private RoundedPanel createLocationsPanel() throws IOException, FontFormatException, SQLException {
        locationPanel = PanelFactory.create(
                themeManager.getBlue(),
                550, 500,
                SizeManager.getInstance().getBorderRadiusLarge()
        );
        locationPanel.setLayout(new GridLayout(1, 2, 15, 0));
        locationPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel fromPanel = createLocationTable("From (Current Location)", true);
        JPanel toPanel = createLocationTable("To (Destination)", false);

        locationPanel.add(fromPanel);
        locationPanel.add(toPanel);

        return locationPanel;
    }

    private JPanel createLocationTable(String title, boolean isFrom)
            throws IOException, FontFormatException, SQLException {

        JPanel panel = PanelFactory.create(null, 0, 0, 0);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JLabel titleLabel = LabelFactory.create(
                title,
                loadCustomFont(Fonts.DM_SANS_BOLD, 14f),
                themeManager.getBlack()
        );
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));

        BiConsumer<String, Boolean> onLocationSelected = (location, from) -> {
            if (from) {
                currentLocation.setText(location);
            } else {
                destination.setText(location);
            }
            if (!currentLocation.getText().trim().isEmpty() &&
                    !destination.getText().trim().isEmpty()) {
                SwingUtilities.invokeLater(this::searchRoutes);
            }
        };

        JTable table = pageManager.createLocationTable(onLocationSelected, isFrom);
        JScrollPane scrollPane = pageManager.createTableScrollPane(table, themeManager);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private RoundedPanel createSavedPanel() {
        savedPanel = createStatusPanel();
        savedPanel.setPreferredSize(new Dimension(550, 300));
        savedPanel.setMinimumSize(new Dimension(550, 300));
        savedPanel.setMaximumSize(new Dimension(550, 300));
        setPanelPlaceholder(savedPanel, "No saved routes.");
        return savedPanel;
    }

    private RoundedPanel createStatusPanel() {
        RoundedPanel panel = PanelFactory.create(
                themeManager.getBlue(),
                360, 170,
                SizeManager.getInstance().getBorderRadiusLarge()
        );
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private void setPanelPlaceholder(JPanel panel, String message) {
        JLabel placeHolder = Images.getInstance().getPlaceholderLabel(100, 100);
        placeHolder.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel placeholderText = new JLabel(message);
        try { placeholderText.setFont(loadCustomFont(Fonts.DM_SANS_ITALIC, 14)); }
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

    /* --------------------- SEARCH & DISPLAY --------------------- */
    private void searchRoutes() {
        try {
            String selectedFilter = filterButtonGroup.getSelection() != null
                    ? filterButtonGroup.getSelection().getActionCommand() : "all";
            pageManager.setFilter(selectedFilter);

            String from = currentLocation.getText().trim();
            String to = destination.getText().trim();

            MainPageManager.SearchResult result = pageManager.searchRoutes(from, to);

            if (!result.isSuccess()) {
                JOptionPane.showMessageDialog(this,
                        result.getMessage(),
                        result.getMessage().contains("enter") ? "Input Required" : "No Results",
                        JOptionPane.WARNING_MESSAGE);
                displayAllRoutes(result.getAllRoutes(), from, to);
                return;
            }

            displayAllRoutes(result.getAllRoutes(), from, to);

            if (result.isDirect()) {
                displayRouteInfo(result.getDefaultRoute().get(0));
            } else if (result.isTransferRoute()) {
                displayTransferRouteInfo(result.getDefaultRoute());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void clearRouteContainer() {
        routeContainer.removeAll();
        routePanelList.clear();
        routeContainer.revalidate();
        routeContainer.repaint();
    }

    private void refreshCurrentRouteInfo() {
        if (displayedRoute == null) return;
        List<RouteComponent> segments = pageManager.getSegments(displayedRoute.get(0));
        if (segments.size() == 1) {
            displayRouteInfo(segments.get(0));
        } else {
            displayTransferRouteInfo(segments);
        }
    }

    private void displayAllRoutes(ArrayList<RouteComponent> routes, String from, String to)
            throws Exception {
        clearRouteContainer();

        if (routes.isEmpty()) {
            JLabel noRoutes = LabelFactory.create(
                    "No routes found from " + from + " to " + to,
                    loadCustomFont(Fonts.DM_SANS_REGULAR, 14),
                    themeManager.getGray()
            );
            noRoutes.setAlignmentX(Component.CENTER_ALIGNMENT);
            routeContainer.add(Box.createVerticalStrut(50));
            routeContainer.add(noRoutes);
            routeContainer.add(Box.createVerticalStrut(50));
        } else {
            for (RouteComponent route : routes) {
                RouteFactory factory = getFactory(route);
                RoutePanel routePanel = factory.createRoutePanel(route, this::handleRouteClick);
                routePanelList.add(routePanel);
                JPanel panel = routePanel.getPanel();
                panel.setAlignmentX(Component.CENTER_ALIGNMENT);
                routeContainer.add(panel);
                routeContainer.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingSmall()));
            }
        }
        routeContainer.revalidate();
        routeContainer.repaint();
    }

    private void handleRouteClick(Object routeData) {
        if (routeData instanceof List) {
            @SuppressWarnings("unchecked")
            List<RouteComponent> segments = (List<RouteComponent>) routeData;

            // If it's a single-segment list, treat it as a direct route
            if (segments.size() == 1) {
                displayRouteInfo(segments.get(0));
            } else {
                displayTransferRouteInfo(segments);
            }
        } else if (routeData instanceof RouteComponent) {
            displayRouteInfo((RouteComponent) routeData);
        }
    }

    private RouteFactory getFactory(RouteComponent route) {
        return (route instanceof Routes) ? new TransferRouteFactory() : new DirectRouteFactory();
    }

    // --------------------- IMPROVED INFO PANEL UI ---------------------
    private void displayRouteInfo(RouteComponent route) {
        // If this is actually a Routes composite (transfer route), display it properly
        if (route instanceof Routes) {
            List<RouteComponent> segments = pageManager.getSegments(route);
            displayTransferRouteInfo(segments);
            return;
        }

        displayedRoute = new ArrayList<>(List.of(route));
        infoPanel.removeAll();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBackground(themeManager.getWhite());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        try {
            JPanel mainContent = PanelFactory.create(null, 0, 0, 0);
            mainContent.setLayout(new BorderLayout(0, 15));
            mainContent.setOpaque(false);

            // Top section
            JPanel topSection = PanelFactory.create(null, 0, 0, 0);
            topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
            topSection.setOpaque(false);

            JPanel header = createHeaderPanel(route);
            topSection.add(header);
            topSection.add(Box.createVerticalStrut(15));

            // Details and buttons in ONE horizontal panel
            JPanel detailsAndButtons = PanelFactory.create(null, 0, 0, 0);
            detailsAndButtons.setLayout(new BorderLayout(10, 0));
            detailsAndButtons.setOpaque(false);

            // Wrap details in a container that aligns to top
            JPanel detailsWrapper = PanelFactory.create(null, 0, 0, 0);
            detailsWrapper.setLayout(new BorderLayout());
            detailsWrapper.setOpaque(false);
            JPanel details = createInfoSection(route);
            detailsWrapper.add(details, BorderLayout.NORTH);

            JPanel buttons = createButtonSection(route);

            detailsAndButtons.add(detailsWrapper, BorderLayout.CENTER);
            detailsAndButtons.add(buttons, BorderLayout.EAST);

            topSection.add(detailsAndButtons);
            topSection.add(Box.createVerticalStrut(15));

            // Stops section - FIXED: Now properly sized
            JPanel stopsSection = createHorizontalStopsSection(route);
            topSection.add(stopsSection);

            mainContent.add(topSection, BorderLayout.NORTH);

            infoPanel.add(mainContent, BorderLayout.NORTH);
        } catch (Exception e) {
            setInfoMessage("Error displaying route.");
            e.printStackTrace();
        }
        infoPanel.revalidate();
        infoPanel.repaint();
    }

    // FIXED: createHorizontalStopsSection - compact with wrapping
    private JPanel createHorizontalStopsSection(RouteComponent route) throws IOException, FontFormatException {
        JPanel wrapper = PanelFactory.create(null, 0, 0, 0);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);

        JPanel sec = PanelFactory.create(themeManager.getWhite(), 0, 0, 0);
        sec.setLayout(new BorderLayout(0, 8));
        sec.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBlack().brighter().brighter(), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));
        sec.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel header = LabelFactory.create("Route Stops",
                loadCustomFont(Fonts.DM_SANS_BOLD, 15f), themeManager.getBlack());

        // Create wrapper with fixed width to force wrapping
        JPanel stopsWrapper = PanelFactory.create(null, 0, 0, 0);
        stopsWrapper.setOpaque(false);
        stopsWrapper.setLayout(new BorderLayout());
        stopsWrapper.setPreferredSize(new Dimension(850, 0)); // Fixed width forces wrapping

        // Create stops flow that wraps to new line
        JPanel stopsFlow = PanelFactory.create(null, 0, 0, 0);
        stopsFlow.setOpaque(false);
        stopsFlow.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        List<String> allStops = route.getRouteStops();
        for (int i = 0; i < allStops.size(); i++) {
            JLabel stopLabel = LabelFactory.create(
                    allStops.get(i),
                    loadCustomFont(Fonts.DM_SANS_REGULAR, 14f),
                    themeManager.getBlack().brighter());
            stopsFlow.add(stopLabel);

            if (i < allStops.size() - 1) {
                JLabel arrow = LabelFactory.create(" → ",
                        loadCustomFont(Fonts.DM_SANS_BOLD, 14f),
                        themeManager.getGreen());
                stopsFlow.add(arrow);
            }
        }

        stopsWrapper.add(stopsFlow, BorderLayout.CENTER);

        sec.add(header, BorderLayout.NORTH);
        sec.add(stopsWrapper, BorderLayout.CENTER);

        wrapper.add(sec);
        wrapper.add(Box.createVerticalGlue()); // Push everything to top
        return wrapper;
    }

    private JPanel createHeaderPanel(RouteComponent route) throws IOException, FontFormatException {
        JPanel p = PanelFactory.create(null, 0, 0, 0);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setOpaque(false);

        JLabel title = LabelFactory.create(route.getRoute(),
                loadCustomFont(Fonts.DM_SANS_BOLD, 20f), themeManager.getBlack());
        JLabel eta = LabelFactory.create(route.getEta() + " min",
                loadCustomFont(Fonts.DM_SANS_BOLD, 18f), themeManager.getGreen());

        p.add(title);
        p.add(Box.createHorizontalGlue());
        p.add(eta);
        return p;
    }

    private JPanel createInfoSection(RouteComponent route) throws IOException, FontFormatException {
        JPanel sec = PanelFactory.create(null, 0, 0, 0);
        sec.setLayout(new GridLayout(2, 2, 10, 8));
        sec.setOpaque(false);

        sec.add(createInfoItem("Transfers:", route.getTransfers() + ""));
        sec.add(createInfoItem("Stops:", String.valueOf(route.getStops())));
        sec.add(createInfoItem("Fare:", "Php " + String.format("%.2f", route.getFare())));
        sec.add(createInfoItem("Details:", route.getDetails()));

        return sec;
    }

    private JPanel createInfoItem(String label, String value) throws IOException, FontFormatException {
        JPanel p = PanelFactory.create(null, 0, 0, 0);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setOpaque(false);

        JLabel labelComp = LabelFactory.create(label + " ",
                loadCustomFont(Fonts.DM_SANS_BOLD, 14f), themeManager.getBlack());
        JLabel valueComp = LabelFactory.create(value,
                loadCustomFont(Fonts.DM_SANS_REGULAR, 14f), themeManager.getBlack().brighter());

        p.add(labelComp);
        p.add(valueComp);
        p.add(Box.createHorizontalGlue());
        return p;
    }

    private JPanel createButtonSection(RouteComponent route) throws IOException, FontFormatException {
        JPanel p = PanelFactory.create(null, 0, 0, 0);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        RoundedButton save = ButtonFactory.create(
                "Save Route",
                loadCustomFont(Fonts.DM_SANS_BOLD, 13f),
                themeManager.getYellow(),
                themeManager.getBlack(),
                160, 40,
                SizeManager.getInstance().getBorderRadiusLarge()
        );
        save.setAlignmentX(Component.RIGHT_ALIGNMENT);
        save.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                setSavedRoutes(route);
            }
            @Override public void mouseEntered(MouseEvent e) {
                save.setBackground(themeManager.getBlue());
                save.setForeground(themeManager.getWhite());
            }
            @Override public void mouseExited(MouseEvent e) {
                save.setBackground(themeManager.getYellow());
                save.setForeground(themeManager.getBlack());
            }
        });

        RoundedButton take = ButtonFactory.create(
                stateManager.isInTransit() ? "Complete Trip" : "Take Route",
                loadCustomFont(Fonts.DM_SANS_BOLD, 13f),
                themeManager.getGreen(),
                themeManager.getWhite(),
                160, 40,
                SizeManager.getInstance().getBorderRadiusLarge()
        );
        take.setAlignmentX(Component.RIGHT_ALIGNMENT);

        take.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (stateManager.isInTransit()) {
                    handleCompleteTrip();
                } else {
                    try {
                        handleTakeRoute(route);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }

            @Override public void mouseEntered(MouseEvent e) {
                take.setBackground(themeManager.getWhite());
                take.setForeground(themeManager.getGreen());
            }

            @Override public void mouseExited(MouseEvent e) {
                take.setBackground(themeManager.getGreen());
                take.setForeground(themeManager.getWhite());
            }
        });

        p.add(save);
        p.add(Box.createVerticalStrut(10));
        p.add(take);
        return p;
    }

    private void handleTakeRoute(RouteComponent route) throws Exception {
        MainPageManager.TakeRouteResult result = pageManager.takeRoute(route);

        if (!result.isSuccess()) {
            JOptionPane.showMessageDialog(this,
                    result.getErrorMessage(),
                    "Boarding Failed",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        stateManager.startTrip(route, result.getJeepney(), route.getFromLocation(), route.getDestination());
        displayInTransitView();
        updateActiveTripPanel();

        SuccessDialog.show(this,
                String.format("Successfully boarded jeepneys for all segments!<br>" +
                                "Primary Jeepney: %s<br>Passengers: %d/%d<br>Enjoy your ride!",
                        result.getJeepney().getPlateNumber(),
                        result.getJeepney().getCurrentPassengers() + 1,
                        result.getJeepney().getCapacity()),
                "Boarding Successful");
    }

    private void handleCompleteTrip() {
        StateManager.ActiveTrip trip = stateManager.getActiveTrip();
        if (trip == null) return;

        boolean confirmed = ConfirmDialog.show(this,
                "<html>Complete trip on <b>" + trip.getJeepney().getPlateNumber() +
                        "</b>?<br>You've been traveling for <b>" + trip.getMinutesInTransit() +
                        " minutes</b>.</html>",
                "Complete Trip");

        if (confirmed) {
            MainPageManager.CompleteRouteResult result = pageManager.completeRoute(trip);

            if (!result.isSuccess()) {
                JOptionPane.showMessageDialog(this,
                        result.getErrorMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            stateManager.completeTrip();
            displayTripSummary(trip);
            updateActiveTripPanel();
        }
    }

    private void displayInTransitView() {
        StateManager.ActiveTrip trip = stateManager.getActiveTrip();
        if (trip == null) return;

        infoPanel.removeAll();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBackground(themeManager.getWhite());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        try {
            JPanel content = PanelFactory.create(null, 0, 0, 0);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setOpaque(false);

            JLabel header = LabelFactory.create("Trip in Progress",
                    loadCustomFont(Fonts.DM_SANS_BOLD, 24f), themeManager.getBlack());
            header.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel routeInfo = PanelFactory.create(themeManager.getBlue(), 0, 0, SizeManager.getInstance().getBorderRadiusLarge());
            routeInfo.setLayout(new GridLayout(6, 1, 0, 10));
            routeInfo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            routeInfo.add(createInfoRow("Route:", trip.getRoute().getRoute()));
            routeInfo.add(createInfoRow("From:", trip.getFromLocation()));
            routeInfo.add(createInfoRow("To:", trip.getToLocation()));
            routeInfo.add(createInfoRow("Jeepney:", trip.getJeepney().getPlateNumber()));
            routeInfo.add(createInfoRow("Expected Time:", trip.getRoute().getEta() + " min"));
            routeInfo.add(createInfoRow("Fare:", "Php " + String.format("%.2f", trip.getRoute().getFare())));

            RoundedButton completeBtn = ButtonFactory.create("Complete Trip", loadCustomFont(Fonts.DM_SANS_BOLD, 14f),
                    themeManager.getBlack(), themeManager.getWhite(), 200, 45,
                    SizeManager.getInstance().getBorderRadiusLarge());
            completeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            completeBtn.addActionListener(e -> handleCompleteTrip());

            content.add(header);
            content.add(Box.createVerticalStrut(20));
            content.add(routeInfo);
            content.add(Box.createVerticalStrut(30));
            content.add(completeBtn);
            content.add(Box.createVerticalGlue());

            infoPanel.add(content, BorderLayout.NORTH);

        } catch (Exception e) {
            setInfoMessage("Error displaying trip info.");
            e.printStackTrace();
        }

        infoPanel.revalidate();
        infoPanel.repaint();
    }

    private JPanel createInfoRow(String label, String value) throws IOException, FontFormatException {
        JPanel row = PanelFactory.create(null, 0, 0, 0);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);

        JLabel labelComp = LabelFactory.create(label + " ", loadCustomFont(Fonts.DM_SANS_BOLD, 14f), themeManager.getBlack());
        JLabel valueComp = LabelFactory.create(value, loadCustomFont(Fonts.DM_SANS_REGULAR, 14f), themeManager.getBlack().brighter());

        row.add(labelComp);
        row.add(valueComp);
        row.add(Box.createHorizontalGlue());
        return row;
    }

    private void displayTripSummary(StateManager.ActiveTrip trip) {
        infoPanel.removeAll();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBackground(themeManager.getWhite());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        try {
            JPanel content = PanelFactory.create(null, 0, 0, 0);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setOpaque(false);

            JLabel header = LabelFactory.create("✅ Trip Completed!", loadCustomFont(Fonts.DM_SANS_BOLD, 24f), themeManager.getBlack());
            header.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel summary = PanelFactory.create(themeManager.getBlue(), 0, 0, SizeManager.getInstance().getBorderRadiusLarge());
            summary.setLayout(new GridLayout(3, 1, 0, 10));
            summary.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            summary.add(createInfoRow("Route:", trip.getRoute().getRoute()));
            summary.add(createInfoRow("Duration:", trip.getMinutesInTransit() + " minutes"));
            summary.add(createInfoRow("Fare Paid:", "Php " + String.format("%.2f", trip.getRoute().getFare())));

            RoundedButton newSearchBtn = ButtonFactory.create("Search New Route", loadCustomFont(Fonts.DM_SANS_BOLD, 14f),
                    themeManager.getGreen(), themeManager.getWhite(), 200, 45,
                    SizeManager.getInstance().getBorderRadiusLarge());
            newSearchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            newSearchBtn.addActionListener(e -> {
                stateManager.returnToBrowsing();
                updateActiveTripPanel();
                setInfoMessage("No chosen route.");
            });

            content.add(header);
            content.add(Box.createVerticalStrut(20));
            content.add(summary);
            content.add(Box.createVerticalStrut(30));
            content.add(newSearchBtn);
            content.add(Box.createVerticalGlue());

            infoPanel.add(content, BorderLayout.NORTH);

        } catch (Exception e) {
            setInfoMessage("Trip completed.");
            e.printStackTrace();
        }

        infoPanel.revalidate();
        infoPanel.repaint();
    }

    private void displayTransferRouteInfo(List<RouteComponent> segments) {
        displayedRoute = new ArrayList<>(segments);
        infoPanel.removeAll();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBackground(themeManager.getWhite());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        try {
            JPanel mainContent = PanelFactory.create(null, 0, 0, 0);
            mainContent.setLayout(new BorderLayout(0, 15));
            mainContent.setOpaque(false);

            RouteComponent firstSegment = segments.get(0);
            MainPageManager.RouteDetails details = pageManager.calculateTransferMetrics(firstSegment);

            // Top section
            JPanel topSection = PanelFactory.create(null, 0, 0, 0);
            topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
            topSection.setOpaque(false);

            // Header
            JPanel header = PanelFactory.create(null, 0, 0, 0);
            header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
            header.setOpaque(false);

            // Change title based on number of segments
            String titleText = segments.size() == 1 ? "Direct Route" : "Transfer Route (" + segments.size() + " legs)";
            JLabel title = new JLabel(titleText);
            title.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 20f));
            title.setForeground(themeManager.getBlack());

            JLabel eta = LabelFactory.create(details.getTotalETA() + " min",
                    loadCustomFont(Fonts.DM_SANS_BOLD, 18f), themeManager.getGreen());

            header.add(title);
            header.add(Box.createHorizontalGlue());
            header.add(eta);

            topSection.add(header);
            topSection.add(Box.createVerticalStrut(15));

            // Details and buttons in ONE horizontal panel (matching direct route layout)
            JPanel detailsAndButtons = PanelFactory.create(null, 0, 0, 0);
            detailsAndButtons.setLayout(new BorderLayout(10, 0));
            detailsAndButtons.setOpaque(false);

            // Wrap summary in a container that aligns to top
            JPanel summaryWrapper = PanelFactory.create(null, 0, 0, 0);
            summaryWrapper.setLayout(new BorderLayout());
            summaryWrapper.setOpaque(false);

            // Summary section
            JPanel summary = PanelFactory.create(null, 0, 0, 0);
            summary.setLayout(new GridLayout(2, 2, 15, 10));
            summary.setOpaque(false);

            summary.add(createInfoItem("Transfers:", String.valueOf(details.getTransfers())));
            summary.add(createInfoItem("Total Stops:", String.valueOf(details.getTotalStops())));
            summary.add(createInfoItem("Total Fare:", "Php " + String.format("%.2f", details.getTotalFare())));
            summary.add(createInfoItem("Segments:", String.valueOf(details.getSegments())));

            summaryWrapper.add(summary, BorderLayout.NORTH);

            // Buttons - ADDED TAKE ROUTE BUTTON
            JPanel buttons = createTransferButtonSection(firstSegment);

            detailsAndButtons.add(summaryWrapper, BorderLayout.CENTER);
            detailsAndButtons.add(buttons, BorderLayout.EAST);

            topSection.add(detailsAndButtons);

            // For single-segment routes, show compact stops section like direct routes
            if (segments.size() == 1) {
                topSection.add(Box.createVerticalStrut(15));
                JPanel stopsSection = createHorizontalStopsSection(firstSegment);
                topSection.add(stopsSection);

                mainContent.add(topSection, BorderLayout.NORTH);
                infoPanel.add(mainContent, BorderLayout.NORTH);
            } else {
                // For multi-segment routes, show segment panels with scrolling
                JPanel segsContainer = PanelFactory.create(themeManager.getWhite(), 0, 0, 0);
                segsContainer.setLayout(new BoxLayout(segsContainer, BoxLayout.Y_AXIS));

                for (int i = 0; i < segments.size(); i++) {
                    JPanel segPanel = createSegmentPanel(segments.get(i), i + 1);
                    segsContainer.add(segPanel);
                    if (i < segments.size() - 1) {
                        segsContainer.add(Box.createVerticalStrut(12));
                    }
                }

                JScrollPane segmentsScroll = new JScrollPane(segsContainer);
                segmentsScroll.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
                segmentsScroll.setOpaque(false);
                segmentsScroll.getViewport().setOpaque(false);
                segmentsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                segmentsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                segmentsScroll.getVerticalScrollBar().setUnitIncrement(16);

                mainContent.add(topSection, BorderLayout.NORTH);
                mainContent.add(segmentsScroll, BorderLayout.CENTER);

                infoPanel.add(mainContent, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            setInfoMessage("Error displaying transfer route.");
            e.printStackTrace();
        }
        infoPanel.revalidate();
        infoPanel.repaint();
    }

    private JPanel createTransferButtonSection(RouteComponent route) throws IOException, FontFormatException {
        JPanel p = PanelFactory.create(null, 0, 0, 0);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        RoundedButton save = ButtonFactory.create("Save Route",
                loadCustomFont(Fonts.DM_SANS_BOLD, 13f),
                themeManager.getYellow(),
                themeManager.getBlack(),
                160, 40,
                SizeManager.getInstance().getBorderRadiusLarge());
        save.setAlignmentX(Component.RIGHT_ALIGNMENT);
        save.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                setSavedRoutes(route);
            }
            @Override public void mouseEntered(MouseEvent e) {
                save.setBackground(themeManager.getBlue());
                save.setForeground(themeManager.getWhite());
            }
            @Override public void mouseExited(MouseEvent e) {
                save.setBackground(themeManager.getYellow());
                save.setForeground(themeManager.getBlack());
            }
        });

        // ADDED: Take Route button for transfer routes
        RoundedButton take = ButtonFactory.create(
                stateManager.isInTransit() ? "Complete Trip" : "Take Route",
                loadCustomFont(Fonts.DM_SANS_BOLD, 13f),
                themeManager.getGreen(),
                themeManager.getWhite(),
                160, 40,
                SizeManager.getInstance().getBorderRadiusLarge()
        );
        take.setAlignmentX(Component.RIGHT_ALIGNMENT);

        take.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (stateManager.isInTransit()) {
                    handleCompleteTrip();
                } else {
                    try {
                        handleTakeRoute(route);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }

            @Override public void mouseEntered(MouseEvent e) {
                take.setBackground(themeManager.getWhite());
                take.setForeground(themeManager.getGreen());
            }

            @Override public void mouseExited(MouseEvent e) {
                take.setBackground(themeManager.getGreen());
                take.setForeground(themeManager.getWhite());
            }
        });

        p.add(save);
        p.add(Box.createVerticalStrut(10));
        p.add(take);
        return p;
    }

    private JPanel createSegmentPanel(RouteComponent seg, int num) throws IOException, FontFormatException {
        JPanel p = PanelFactory.create(null, 0, 0, 0);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBlack().brighter().brighter(), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        // Segment header
        JLabel segTitle = LabelFactory.create("Segment " + num + ": " + seg.getRoute(),
                loadCustomFont(Fonts.DM_SANS_BOLD, 16f), themeManager.getBlack()); // Increased from 15f
        segTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(segTitle);
        p.add(Box.createVerticalStrut(8));

        // Route info
        JPanel routeInfo = PanelFactory.create(null, 0, 0, 0);
        routeInfo.setOpaque(false);
        routeInfo.setLayout(new BoxLayout(routeInfo, BoxLayout.Y_AXIS));

        JLabel fromTo = LabelFactory.create(seg.getFromLocation() + " → " + seg.getDestination(),
                loadCustomFont(Fonts.DM_SANS_BOLD, 14f), themeManager.getBlack()); // Increased from 13f
        fromTo.setAlignmentX(Component.LEFT_ALIGNMENT);
        routeInfo.add(fromTo);
        routeInfo.add(Box.createVerticalStrut(5));

        JLabel metrics = LabelFactory.create(
                String.format("Fare: Php %.2f | Stops: %d | ETA: %d min",
                        seg.getFare(), seg.getStops(), seg.getEta()),
                loadCustomFont(Fonts.DM_SANS_REGULAR, 13f), // Increased from 12f
                themeManager.getBlack().brighter());
        metrics.setAlignmentX(Component.LEFT_ALIGNMENT);
        routeInfo.add(metrics);

        p.add(routeInfo);
        p.add(Box.createVerticalStrut(8));

        // Route stops - HORIZONTAL with wrapping in scroll pane
        JLabel stopsHeader = LabelFactory.create("Stops:",
                loadCustomFont(Fonts.DM_SANS_BOLD, 14f), themeManager.getBlack());
        stopsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(stopsHeader);
        p.add(Box.createVerticalStrut(5));

        // Create stops flow panel
        JPanel stopsFlow = PanelFactory.create(null, 0, 0, 0);
        stopsFlow.setOpaque(false);
        stopsFlow.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        List<String> stops = seg.getRouteStops();
        for (int i = 0; i < stops.size(); i++) {
            JLabel stopLabel = LabelFactory.create(
                    stops.get(i),
                    loadCustomFont(Fonts.DM_SANS_REGULAR, 13f),
                    themeManager.getBlack().brighter());
            stopsFlow.add(stopLabel);

            if (i < stops.size() - 1) {
                JLabel arrow = LabelFactory.create(" → ",
                        loadCustomFont(Fonts.DM_SANS_BOLD, 13f),
                        themeManager.getGreen());
                stopsFlow.add(arrow);
            }
        }

        // Wrap in scroll pane with invisible scrollbar
        JScrollPane stopsScroll = new JScrollPane(stopsFlow);
        stopsScroll.setBorder(BorderFactory.createEmptyBorder());
        stopsScroll.setOpaque(false);
        stopsScroll.getViewport().setOpaque(false);
        stopsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        stopsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        stopsScroll.getVerticalScrollBar().setUnitIncrement(16);
        stopsScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150)); // Max height 150px
        stopsScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(stopsScroll);

        return p;
    }

    // --------------------- SAVED ROUTES ---------------------
    public void setSavedRoutes(RouteComponent route) {
        if (pageManager.addSavedRoute(route)) {
            refreshSavedRoutesPanel();
        } else {
            JOptionPane.showMessageDialog(this, "Route already saved!", "Duplicate", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void refreshSavedRoutesPanel() {
        savedPanel.removeAll();
        ArrayList<RouteComponent> routes = pageManager.getSavedRoutes();

        if (routes.isEmpty()) {
            setPanelPlaceholder(savedPanel, "No saved routes.");
        } else {
            savedPanel.setLayout(new BoxLayout(savedPanel, BoxLayout.Y_AXIS));
            savedPanel.add(Box.createVerticalStrut(20));
            for (RouteComponent r : routes) {
                List<RouteComponent> segs = pageManager.getSegments(r);
                RoundedPanel rp = PanelFactory.create(themeManager.getWhite(), 500, 40, 30);
                rp.setLayout(new BorderLayout());
                rp.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                String labelText = segs.size() == 1
                        ? "<html>" + segs.get(0).getFromLocation() + "<b> to </b>" + segs.get(0).getDestination() +
                        " <b><i>&nbsp;via&nbsp;</i></b>" + segs.get(0).getRoute() +
                        " &nbsp;&nbsp;(" + r.getEta() + " min)</html>"
                        : "<html>" + segs.get(0).getFromLocation() + " to " + segs.get(segs.size()-1).getDestination() +
                        " <b><i>&nbsp;via transfer&nbsp;</i></b>" + segs.get(0).getRoute() + " to " + segs.get(segs.size()-1).getRoute() +
                        " &nbsp;&nbsp;(" + r.getEta() + " min)</html>";

                JLabel lbl = new JLabel(labelText);
                try { lbl.setFont(loadCustomFont(Fonts.DM_SANS_REGULAR, 14f)); }
                catch (Exception e) { lbl.setFont(new Font("SansSerif", Font.PLAIN, 14)); }
                lbl.setForeground(themeManager.getBlack());
                lbl.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) {
                        if (segs.size() == 1) {
                            displayRouteInfo(r);
                        } else {
                            displayTransferRouteInfo(segs);
                        }
                    }
                    @Override public void mouseEntered(MouseEvent e) {
                        lbl.setForeground(themeManager.getGreen());
                        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }
                    @Override public void mouseExited(MouseEvent e) { lbl.setForeground(themeManager.getBlack()); }
                });

                JButton del = ButtonFactory.create("X", new Font("SansSerif", Font.PLAIN, 13),
                        themeManager.getWhite(), Color.RED, 30, 30, 0);
                del.setBorder(BorderFactory.createEmptyBorder());
                del.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                del.addActionListener(e -> {
                    pageManager.removeSavedRoute(r);
                    refreshSavedRoutesPanel();
                });

                rp.add(lbl, BorderLayout.CENTER);
                rp.add(del, BorderLayout.EAST);
                savedPanel.add(rp);
                savedPanel.add(Box.createVerticalStrut(10));
            }
        }
        savedPanel.revalidate();
        savedPanel.repaint();
    }

    public void setInfoMessage(String msg) {
        setPanelPlaceholder(infoPanel, msg);
    }

    @Override
    public void onThemeChange(boolean isDarkMode) {
        setBackground(themeManager.getBackgroundColor());
        if (container != null) container.setBackground(themeManager.getBackgroundColor());

        // Update text fields
        if (currentLocation != null) {
            currentLocation.setBackground(themeManager.getComponentsColor());
            currentLocation.setForeground(themeManager.getForegroundColor());
        }
        if (destination != null) {
            destination.setBackground(themeManager.getComponentsColor());
            destination.setForeground(themeManager.getForegroundColor());
        }

        // Update labels
        if (savedLabel != null) savedLabel.setForeground(themeManager.getForegroundColor());
        if (locationsLabel != null) locationsLabel.setForeground(themeManager.getForegroundColor());
        if (wcQuestion != null) wcQuestion.setForeground(themeManager.getForegroundColor());

        // Update buttons (reset to default state)
        if (submitButton != null) {
            submitButton.setBackground(themeManager.getGreen());
            submitButton.setForeground(themeManager.getWhite());
        }

        // ADDED: Update info panel background
        if (infoPanel != null) {
            infoPanel.setBackground(themeManager.getWhite());
        }

        // ADDED: Update route container background
        if (routeContainer != null) {
            routeContainer.setBackground(themeManager.getBackgroundColor());
        }

        SwingUtilities.invokeLater(() -> {
            themeManager.applyThemeToContainers(this);

            // ADDED: Refresh displayed route info to update its colors
            if (displayedRoute != null && !displayedRoute.isEmpty()) {
                if (stateManager.isBrowsing()) {
                    refreshCurrentRouteInfo();
                } else if (stateManager.isInTransit()) {
                    displayInTransitView();
                }
            }

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

    private class UIRefreshObserver implements JeepneyObserver {
        @Override
        public void update(String plateNumber, int currentPassengers, int capacity) {
            SwingUtilities.invokeLater(() -> {
                // Refresh all route panels
                for (RoutePanel panel : routePanelList) {
                    panel.refresh();
                }

                if (stateManager.isBrowsing()) {
                    refreshCurrentRouteInfo();
                }
                if (stateManager.isInTransit()) {
                    updateActiveTripPanel();
                    StateManager.ActiveTrip trip = stateManager.getActiveTrip();
                    if (trip != null && trip.getJeepney().getPlateNumber().equals(plateNumber)) {
                        displayInTransitView();
                    }
                }
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame f = new JFrame("mainPage Preview");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                Consumer<String> dummy = System.out::println;
                MainPage p = new MainPage(dummy);
                f.add(p);
                f.setSize(1920, 1080);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }
}