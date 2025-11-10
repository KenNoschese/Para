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

import org.example.DatabaseManager.DatabaseInstance;
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
    private JPanel routeContainer;
    private RoundedTextField currentLocation;
    private RoundedTextField destination;
    private ThemeManager themeManager;
    private JPanel container;
    private RoundedPanel textContainer;
    private JPanel welcomeContainer;
    private JPanel inputContainer;
    private RoundedButton submitButton;
    private RoundedButton userButton;
    private RoundedPanel locationPanel;
    private JPanel infoPanel;
    private JLabel locationsLabel;
    private RoundedPanel savedPanel;
    private JLabel savedLabel;
    private JLabel wcQuestion;
    private MainPageManager pageManager;
    private ButtonGroup filterButtonGroup;
    private RouteManager routeManager;
    private ArrayList<RouteComponent> displayedRoute;
    private StateManager stateManager;

    public MainPage(Consumer<String> cardChanger) throws IOException, FontFormatException, SQLException {
        this.cardChanger = cardChanger;
        this.themeManager = ThemeManager.getInstance();
        this.themeManager.addThemeChangeListener(this);
        setupPanel();
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
        textContainer.add(createWelcomeContainer());
        return textContainer;
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
                "View Available Routes", loadCustomFont(Fonts.DM_SANS_BOLD, SizeManager.getInstance().getTextSmall()),
                themeManager.getGreen(), themeManager.getWhite(), 250, 40,
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
                themeManager.getBackgroundColor(),
                570, Integer.MAX_VALUE,
                SizeManager.getInstance().getBorderRadiusLarge()
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

        // Title label
        JLabel titleLabel = LabelFactory.create(
                title,
                loadCustomFont(Fonts.DM_SANS_BOLD, 14f),
                themeManager.getBlack()
        );
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));

        // Location selection callback
        BiConsumer<String, Boolean> onLocationSelected = (location, from) -> {
            if (from) {
                currentLocation.setText(location);
            } else {
                destination.setText(location);
            }

            // Auto-search if both fields are filled
            if (!currentLocation.getText().trim().isEmpty() &&
                    !destination.getText().trim().isEmpty()) {
                SwingUtilities.invokeLater(() -> searchRoutes());
            }
        };

        // Create table using manager
        JTable table = pageManager.createLocationTable(
                onLocationSelected,
                isFrom
        );

        // Create styled scroll pane
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
        routeContainer.revalidate();
        routeContainer.repaint();
    }

    private void refreshCurrentRouteInfo() {
        if (displayedRoute == null) return;
        List<RouteComponent> segments = getSegments(displayedRoute.get(0));
        if (segments.size() == 1) {
            displayRouteInfo(segments.get(0));
        } else {
            displayTransferRouteInfo(segments);
        }
    }

    // --------------------- DISPLAY ALL ROUTES ---------------------
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
                // Get appropriate factory based on route type
                RouteFactory factory = getFactory(route);

                // Factory creates the appropriate panel (Factory Method)
                RoutePanel routePanel = factory.createRoutePanel(
                        route,
                        this::handleRouteClick
                );

                // Add panel to container
                JPanel panel = routePanel.getPanel();
                panel.setAlignmentX(Component.CENTER_ALIGNMENT);
                routeContainer.add(panel);
                routeContainer.add(Box.createVerticalStrut(
                        SizeManager.getInstance().getSpacingSmall()));
            }
        }
        routeContainer.revalidate();
        routeContainer.repaint();
    }

    private void handleRouteClick(Object routeData) {
        if (routeData instanceof List) {
            @SuppressWarnings("unchecked")
            List<RouteComponent> segments = (List<RouteComponent>) routeData;
            displayTransferRouteInfo(segments);
        } else if (routeData instanceof RouteComponent) {
            displayRouteInfo((RouteComponent) routeData);
        }
    }

    private RouteFactory getFactory(RouteComponent route) {
        if (route instanceof Routes) {
            return new TransferRouteFactory();
        } else {
            return new DirectRouteFactory();
        }
    }

    // --------------------- SINGLE ROUTE INFO ---------------------
    private void displayRouteInfo(RouteComponent route) {
        displayedRoute = new ArrayList<>(List.of(route));
        infoPanel.removeAll();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBackground(themeManager.getWhite());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        try {
            JPanel content = PanelFactory.create(null, 0, 0, 0);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setOpaque(false);

            JPanel details = PanelFactory.create(null, 0, 0, 0);
            details.setLayout(new BoxLayout(details, BoxLayout.X_AXIS));
            details.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            details.add(createInfoSection(route));
            details.add(createButtonSection(route));

            content.add(createHeaderPanel(route));
            content.add(Box.createVerticalStrut(30));
            content.add(details);
            content.add(Box.createVerticalStrut(20));
            content.add(createStopsSection(route));
            content.add(Box.createVerticalGlue());

            infoPanel.add(content, BorderLayout.CENTER);
        } catch (Exception e) {
            setInfoMessage("Error displaying route.");
        }
        infoPanel.revalidate();
        infoPanel.repaint();
    }

    private JPanel createHeaderPanel(RouteComponent route) throws IOException, FontFormatException {
        JPanel p = PanelFactory.create(null, 0, 0, 0);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setOpaque(false);

        JLabel title = LabelFactory.create(route.getRoute(),
                loadCustomFont(Fonts.DM_SANS_BOLD, 20f), themeManager.getBlack());
        JLabel eta = LabelFactory.create(route.getEta() + " min",
                loadCustomFont(Fonts.DM_SANS_BOLD, 20f), themeManager.getBlack());

        p.add(title);
        p.add(Box.createHorizontalGlue());
        p.add(eta);
        return p;
    }

    private JPanel createInfoSection(RouteComponent route) throws IOException, FontFormatException {
        JPanel sec = PanelFactory.create(null, 0, 0, 0);
        sec.setLayout(new BoxLayout(sec, BoxLayout.Y_AXIS));
        sec.setOpaque(false);

        JPanel row1 = PanelFactory.create(null, 0, 0, 0);
        row1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row1.add(createInfoItem("Transfers:", route.getTransfers() + "    "));
        row1.add(createInfoItem("Stops:", String.valueOf(route.getStops())));

        JPanel row2 = PanelFactory.create(null, 0, 0, 0);
        row2.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row2.add(createInfoItem("Fare:", "Php " + String.format("%.2f", route.getFare()) + "    "));
        row2.add(createInfoItem("Details:", route.getDetails()));

        sec.add(row1); sec.add(row2);
        return sec;
    }

    private JPanel createInfoItem(String label, String value) throws IOException, FontFormatException {
        JPanel p = PanelFactory.create(null, 0, 0, 0);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setOpaque(false);
        p.add(LabelFactory.create(label + " ", loadCustomFont(Fonts.DM_SANS_BOLD, 13f), themeManager.getBlack()));
        p.add(LabelFactory.create(value, loadCustomFont(Fonts.DM_SANS_REGULAR, 13f), themeManager.getBlack().brighter()));
        p.add(Box.createHorizontalGlue());
        return p;
    }

    private JPanel createStopsSection(RouteComponent route) throws IOException, FontFormatException {
        JPanel sec = PanelFactory.create(null, 0, 0, 0);
        sec.setLayout(new BoxLayout(sec, BoxLayout.Y_AXIS));
        sec.setOpaque(false);
        sec.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBlack().brighter().brighter(), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        String stops = String.join(" to ", route.getRouteStops());
        sec.add(LabelFactory.create("Route Stops",
                loadCustomFont(Fonts.DM_SANS_BOLD, 13f), themeManager.getBlack()));
        sec.add(Box.createVerticalStrut(5));
        sec.add(LabelFactory.create("<html>" + stops + "</html>",
                loadCustomFont(Fonts.DM_SANS_REGULAR, 13f), themeManager.getBlack().brighter()));
        return sec;
    }

    private JPanel createButtonSection(RouteComponent route) throws IOException, FontFormatException {
        JPanel p = PanelFactory.create(null, 0, 0, 0);
        p.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        p.setOpaque(false);

        RoundedButton save = ButtonFactory.create(
                "Save Route",
                loadCustomFont(Fonts.DM_SANS_BOLD, 13f),
                themeManager.getYellow(),
                themeManager.getBlack(),
                160, 40,
                SizeManager.getInstance().getBorderRadiusLarge()
        );
        save.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                setSavedRoutes(route);
            }
            @Override public void mouseEntered(MouseEvent e) {
                save.setBackground(themeManager.getBlue());
            }
            @Override public void mouseExited(MouseEvent e) {
                save.setBackground(themeManager.getYellow());
            }
        });

        RoundedButton take = ButtonFactory.create(
                stateManager.isInTransit() ? "Complete Trip" : "Take Route",
                loadCustomFont(Fonts.DM_SANS_BOLD, 13f),
                themeManager.getGreen(),
                themeManager.getWhite(),
                180, 40,
                SizeManager.getInstance().getBorderRadiusLarge()
        );

        take.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (stateManager.isInTransit()) {
                    handleCompleteTrip();
                } else {
                    handleTakeRoute(route);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                take.setBackground(themeManager.getWhite());
                take.setForeground(themeManager.getGreen());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                take.setBackground(themeManager.getGreen());
                take.setForeground(themeManager.getWhite());
            }
        });

        p.add(save);
        p.add(take);
        return p;
    }

    // NEW: Handle taking a route
    private void handleTakeRoute(RouteComponent route) {
        Connection conn = null;
        try {
            conn = DatabaseInstance.getInstance().getConnection();

            // Get available jeepneys
            ArrayList<RouteManager.JeepneyInfo> jeepneys =
                    routeManager.getJeepneysForRoute(route.getRoute());

            if (jeepneys.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No jeepneys available on this route.",
                        "No Jeepneys",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Find available jeepney
            RouteManager.JeepneyInfo available = jeepneys.stream()
                    .filter(j -> !j.isFull())
                    .findFirst()
                    .orElse(null);

            if (available == null) {
                JOptionPane.showMessageDialog(this,
                        "All jeepneys are full on this route.",
                        "Route Full",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Board the jeepney (this will trigger observer notifications)
            routeManager.boardJeepney(available.getPlateNumber());

            // Update trip state
            stateManager.startTrip(
                    route,
                    available,
                    route.getFromLocation(),
                    route.getDestination()
            );

            // Show in-transit UI
            displayInTransitView();

            SuccessDialog.show(this,
                    String.format("Successfully boarded %s!<br>Passengers: %d/%d<br>Enjoy your ride!",
                            available.getPlateNumber(),
                            available.getCurrentPassengers() + 1,
                            available.getCapacity()),
                    "Boarding Successful");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error boarding jeepney: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
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
            try (Connection conn = DatabaseInstance.getInstance().getConnection()) {
                // Leave jeepney
                routeManager.leaveJeepney(trip.getJeepney().getPlateNumber());

                // Mark trip as completed
                stateManager.completeTrip();

                // Show trip summary
                displayTripSummary(trip);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error completing trip: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void displayInTransitView() {
        StateManager.ActiveTrip trip = stateManager.getActiveTrip();
        if (trip == null) return;

        infoPanel.removeAll();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBackground(themeManager.getGreen().brighter());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        try {
            JPanel content = PanelFactory.create(null, 0, 0, 0);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setOpaque(false);

            // Header
            JLabel header = LabelFactory.create(
                    "🚍 Trip in Progress",
                    loadCustomFont(Fonts.DM_SANS_BOLD, 24f),
                    themeManager.getBlack()
            );
            header.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Route info
            JPanel routeInfo = PanelFactory.create(
                    themeManager.getWhite(),
                    0, 0,
                    SizeManager.getInstance().getBorderRadiusLarge()
            );
            routeInfo.setLayout(new BoxLayout(routeInfo, BoxLayout.Y_AXIS));
            routeInfo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            routeInfo.add(createInfoRow("Route:", trip.getRoute().getRoute()));
            routeInfo.add(Box.createVerticalStrut(10));
            routeInfo.add(createInfoRow("From:", trip.getFromLocation()));
            routeInfo.add(Box.createVerticalStrut(10));
            routeInfo.add(createInfoRow("To:", trip.getToLocation()));
            routeInfo.add(Box.createVerticalStrut(10));
            routeInfo.add(createInfoRow("Jeepney:", trip.getJeepney().getPlateNumber()));
            routeInfo.add(Box.createVerticalStrut(10));
            routeInfo.add(createInfoRow("Expected Time:", trip.getRoute().getEta() + " min"));
            routeInfo.add(Box.createVerticalStrut(10));
            routeInfo.add(createInfoRow("Fare:", "Php " + String.format("%.2f", trip.getRoute().getFare())));

            // Complete button
            RoundedButton completeBtn = ButtonFactory.create(
                    "Complete Trip",
                    loadCustomFont(Fonts.DM_SANS_BOLD, 14f),
                    themeManager.getBlack(),
                    themeManager.getWhite(),
                    200, 45,
                    SizeManager.getInstance().getBorderRadiusLarge()
            );
            completeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            completeBtn.addActionListener(e -> handleCompleteTrip());

            content.add(header);
            content.add(Box.createVerticalStrut(20));
            content.add(routeInfo);
            content.add(Box.createVerticalStrut(30));
            content.add(completeBtn);
            content.add(Box.createVerticalGlue());

            infoPanel.add(content, BorderLayout.CENTER);

        } catch (Exception e) {
            setInfoMessage("Error displaying trip info.");
        }

        infoPanel.revalidate();
        infoPanel.repaint();

        // Disable route searching while in transit
        currentLocation.setEnabled(false);
        destination.setEnabled(false);
        submitButton.setEnabled(false);
    }

    // NEW: Helper to create info rows
    private JPanel createInfoRow(String label, String value) throws IOException, FontFormatException {
        JPanel row = PanelFactory.create(null, 0, 0, 0);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);

        JLabel labelComp = LabelFactory.create(
                label + " ",
                loadCustomFont(Fonts.DM_SANS_BOLD, 14f),
                themeManager.getBlack()
        );

        JLabel valueComp = LabelFactory.create(
                value,
                loadCustomFont(Fonts.DM_SANS_REGULAR, 14f),
                themeManager.getBlack().brighter()
        );

        row.add(labelComp);
        row.add(valueComp);
        row.add(Box.createHorizontalGlue());

        return row;
    }

    // NEW: Display trip summary after completion
    private void displayTripSummary(StateManager.ActiveTrip trip) {
        infoPanel.removeAll();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBackground(themeManager.getBlue());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        try {
            JPanel content = PanelFactory.create(null, 0, 0, 0);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setOpaque(false);

            JLabel header = LabelFactory.create(
                    "✓ Trip Completed!",
                    loadCustomFont(Fonts.DM_SANS_BOLD, 24f),
                    themeManager.getBlack()
            );
            header.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel summary = PanelFactory.create(
                    themeManager.getWhite(),
                    0, 0,
                    SizeManager.getInstance().getBorderRadiusLarge()
            );
            summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
            summary.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            summary.add(createInfoRow("Route:", trip.getRoute().getRoute()));
            summary.add(Box.createVerticalStrut(10));
            summary.add(createInfoRow("Duration:", trip.getMinutesInTransit() + " minutes"));
            summary.add(Box.createVerticalStrut(10));
            summary.add(createInfoRow("Fare Paid:", "Php " + String.format("%.2f", trip.getRoute().getFare())));

            RoundedButton newSearchBtn = ButtonFactory.create(
                    "Search New Route",
                    loadCustomFont(Fonts.DM_SANS_BOLD, 14f),
                    themeManager.getGreen(),
                    themeManager.getWhite(),
                    200, 45,
                    SizeManager.getInstance().getBorderRadiusLarge()
            );
            newSearchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            newSearchBtn.addActionListener(e -> {
                stateManager.returnToBrowsing();
                currentLocation.setEnabled(true);
                destination.setEnabled(true);
                submitButton.setEnabled(true);
                setInfoMessage("No chosen route.");
            });

            content.add(header);
            content.add(Box.createVerticalStrut(20));
            content.add(summary);
            content.add(Box.createVerticalStrut(30));
            content.add(newSearchBtn);
            content.add(Box.createVerticalGlue());

            infoPanel.add(content, BorderLayout.CENTER);

        } catch (Exception e) {
            setInfoMessage("Trip completed.");
        }

        infoPanel.revalidate();
        infoPanel.repaint();
    }

    // --------------------- TRANSFER ROUTE INFO ---------------------
    private void displayTransferRouteInfo(List<RouteComponent> segments) {
        displayedRoute = new ArrayList<>(segments);
        infoPanel.removeAll();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBackground(themeManager.getWhite());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        try {
            JPanel content = PanelFactory.create(null, 0, 0, 0);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setOpaque(false);

            MainPageManager.RouteDetails details = pageManager.calculateTransferMetrics(segments.get(0));

            JPanel header = PanelFactory.create(null, 0, 0, 0);
            header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
            header.setOpaque(false);
            JLabel title = new JLabel("Transfer Route (" + segments.size() + " legs)");
            title.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 20f));
            title.setForeground(themeManager.getBlack());
            JLabel eta = LabelFactory.create(details.getTotalETA() + " min",
                    loadCustomFont(Fonts.DM_SANS_BOLD, 14f), themeManager.getBlack());
            header.add(title);
            header.add(Box.createHorizontalGlue());
            header.add(eta);

            JPanel summary = PanelFactory.create(null, 0, 0, 0);
            summary.setLayout(new BoxLayout(summary, BoxLayout.X_AXIS));
            summary.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            summary.add(createTransferSummarySection(details));
            summary.add(createTransferSaveButton(segments.get(0)));

            JPanel segs = PanelFactory.create(null, 0, 0, 0);
            segs.setLayout(new BoxLayout(segs, BoxLayout.Y_AXIS));
            segs.setOpaque(false);
            for (int i = 0; i < segments.size(); i++) {
                segs.add(createSegmentPanel(segments.get(i), i + 1));
                if (i < segments.size() - 1) segs.add(Box.createVerticalStrut(10));
            }

            content.add(header);
            content.add(Box.createVerticalStrut(20));
            content.add(summary);
            content.add(Box.createVerticalStrut(20));
            content.add(segs);
            content.add(Box.createVerticalGlue());

            infoPanel.add(content, BorderLayout.CENTER);
        } catch (Exception e) {
            setInfoMessage("Error displaying transfer route.");
        }
        infoPanel.revalidate();
        infoPanel.repaint();
    }

    private JPanel createTransferSummarySection(MainPageManager.RouteDetails details) throws IOException, FontFormatException {
        JPanel sec = PanelFactory.create(null, 0, 0, 0);
        sec.setLayout(new BoxLayout(sec, BoxLayout.Y_AXIS));
        sec.setOpaque(false);

        JPanel r1 = PanelFactory.create(null, 0, 0, 0);
        r1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        r1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        r1.add(createInfoItem("Transfers:", String.valueOf(details.getTransfers())));
        r1.add(createInfoItem("Total Stops:", String.valueOf(details.getTotalStops())));

        JPanel r2 = PanelFactory.create(null, 0, 0, 0);
        r2.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        r2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        r2.add(createInfoItem("Total Fare:", "Php " + String.format("%.2f", details.getTotalFare())));
        r2.add(createInfoItem("Segments:", String.valueOf(details.getSegments())));

        sec.add(r1); sec.add(r2);
        return sec;
    }

    private JPanel createTransferSaveButton(RouteComponent route) throws IOException, FontFormatException {
        JPanel p = PanelFactory.create(null, 0, 0, 0);
        p.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        p.setOpaque(false);

        RoundedButton save = ButtonFactory.create(
                "Save Route",
                loadCustomFont(Fonts.DM_SANS_BOLD, 13f),
                themeManager.getYellow(),
                themeManager.getBlack(),
                160, 40,
                SizeManager.getInstance().getBorderRadiusLarge()
        );
        save.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                setSavedRoutes(route);
            }
            @Override public void mouseEntered(MouseEvent e) { save.setBackground(themeManager.getBlue()); }
            @Override public void mouseExited(MouseEvent e) { save.setBackground(themeManager.getYellow()); }
        });
        p.add(save);
        return p;
    }

    private JPanel createSegmentPanel(RouteComponent seg, int num) throws IOException, FontFormatException {
        JPanel p = PanelFactory.create(null, 0, 0, 0);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBlack().brighter().brighter(), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        p.add(LabelFactory.create("Segment " + num + ": " + seg.getRoute(),
                loadCustomFont(Fonts.DM_SANS_BOLD, 15f), themeManager.getBlack()));
        p.add(Box.createVerticalStrut(10));

        JPanel info = PanelFactory.create(null, 0, 0, 0);
        info.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        info.add(LabelFactory.create(seg.getFromLocation() + " to " + seg.getDestination(),
                loadCustomFont(Fonts.DM_SANS_BOLD, 13f), themeManager.getBlack().brighter()));
        info.add(createInfoItem("    Fare:", "Php " + String.format("%.2f", seg.getFare())));
        info.add(createInfoItem("Stops:", String.valueOf(seg.getStops())));
        info.add(createInfoItem("ETA:", seg.getEta() + " min"));
        p.add(info);

        String stops = String.join(" to ", seg.getRouteStops());
        JPanel stopsSec = PanelFactory.create(null, 0, 0, 0);
        stopsSec.setLayout(new BoxLayout(stopsSec, BoxLayout.Y_AXIS));
        stopsSec.setOpaque(false);
        stopsSec.add(LabelFactory.create("Route Stops:",
                loadCustomFont(Fonts.DM_SANS_BOLD, 12f), themeManager.getBlack()));
        stopsSec.add(Box.createVerticalStrut(3));
        stopsSec.add(LabelFactory.create("<html>" + stops + "</html>",
                loadCustomFont(Fonts.DM_SANS_REGULAR, 12f), themeManager.getBlack().brighter()));
        p.add(stopsSec);
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

    private void refreshSavedRoutesPanel() {
        savedPanel.removeAll();
        ArrayList<RouteComponent> routes = pageManager.getSavedRoutes();

        if (routes.isEmpty()) {
            setPanelPlaceholder(savedPanel, "No saved routes.");
        } else {
            savedPanel.setLayout(new BoxLayout(savedPanel, BoxLayout.Y_AXIS));
            savedPanel.add(Box.createVerticalStrut(20));
            for (RouteComponent r : routes) {
                List<RouteComponent> segs = getSegments(r);
                RoundedPanel rp = PanelFactory.create(
                        themeManager.getWhite(),
                        500, 40, 30
                );
                rp.setLayout(new BorderLayout());
                rp.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                String labelText;
                int totalEta = r.getEta();
                if (segs.size() == 1) {
                    RouteComponent s = segs.get(0);
                    labelText = "<html>" + s.getFromLocation() + "<b> -> </b>" + s.getDestination() +
                            " <b><i>&nbsp;via&nbsp;</i></b>" + s.getRoute() +
                            " &nbsp;&nbsp;(" + totalEta + " min)</html>";
                } else {
                    labelText = "<html>" + segs.get(0).getFromLocation() + " to " + segs.get(segs.size()-1).getDestination() +
                            " <b><i>&nbsp;via transfer&nbsp;</i></b>" + segs.get(0).getRoute() + " to " + segs.get(1).getRoute() +
                            " &nbsp;&nbsp;(" + totalEta + " min)</html>";
                }

                JLabel lbl = new JLabel(labelText);
                try { lbl.setFont(loadCustomFont(Fonts.DM_SANS_REGULAR, 14f)); }
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

                JButton del = ButtonFactory.create("X",
                        new Font("SansSerif", Font.PLAIN, 13),
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

    private class UIRefreshObserver implements JeepneyObserver {
        @Override
        public void update(String plateNumber, int currentPassengers, int capacity) {
            SwingUtilities.invokeLater(() -> {
                // Refresh route panels if browsing
                if (stateManager.isBrowsing()) {
                    refreshCurrentRouteInfo();
                }

                // Update in-transit view if the user is on this jeepney
                if (stateManager.isInTransit() &&
                        stateManager.getActiveTrip().getJeepney().getPlateNumber().equals(plateNumber)) {
                    // Optionally refresh the in-transit display
                    displayInTransitView();
                }
            });
        }
    }

    // Helper: Extract segments
    private List<RouteComponent> getSegments(RouteComponent route) {
        if (route instanceof Routes composite) {
            return composite.getSegments();
        } else {
            return List.of(route);
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