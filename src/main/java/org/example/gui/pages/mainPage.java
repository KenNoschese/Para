package org.example.gui.pages;

import static org.example.gui.components.Factories.factoryPanel.createUserButton;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.example.DatabaseManager.DatabaseInstance;
import org.example.DatabaseManager.RouteDatabase.ObserversClasses.JeepneyObserver;
import org.example.DatabaseManager.RouteDatabase.RouteManager;
import org.example.DatabaseManager.RouteDatabase.RouteComponent;
import org.example.DatabaseManager.RouteDatabase.Routes;
import org.example.gui.appManager.*;
import org.example.gui.components.*;
import org.example.gui.components.Factories.*;
import org.example.gui.resources.Images;
import org.example.gui.resources.fonts;

public class mainPage extends JPanel implements ThemeManager.ThemeChangeListener {
    private Consumer<String> cardChanger;
    private JPanel routeContainer;
    private RoundingOfTextfields currentLocation;
    private RoundingOfTextfields destination;
    private ThemeManager themeManager;
    private JPanel container;
    private RoundingOfPanels textContainer;
    private JPanel welcomeContainer;
    private JPanel inputContainer;
    private RoundingOfButtons submitButton;
    private RoundingOfButtons userButton;
    private RoundingOfPanels locationPanel;
    private JPanel infoPanel;
    private JLabel locationsLabel;
    private RoundingOfPanels savedPanel;
    private JLabel savedLabel;
    private JLabel wcQuestion;
    private mainPageManager pageManager;
    private ButtonGroup filterButtonGroup;
    private RouteManager routeManager;
    private ArrayList<RouteComponent> displayedRoute; // ← FIXED: Now RouteComponent

    public mainPage(Consumer<String> cardChanger) throws IOException, FontFormatException, SQLException {
        this.cardChanger = cardChanger;
        this.themeManager = ThemeManager.getInstance();
        this.themeManager.addThemeChangeListener(this);
        setupPanel();
    }

    private void setupPanel() throws IOException, FontFormatException, SQLException {
        this.pageManager = new mainPageManager();
        this.routeManager = new RouteManager();
        routeManager.addJeepneyObserver(new UIRefreshObserver());
        setLayout(new BorderLayout());
        setPreferredSize(sizeManager.getInstance().flexibleWidth(1920, 1080));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        setBackground(themeManager.getBackgroundColor());

        container = createContainer();
        add(container, BorderLayout.CENTER);
    }

    private JPanel createContainer() throws IOException, FontFormatException, SQLException {
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

    // --------------------- LEFT PANEL ---------------------
    private JPanel createLeftJPanel() throws IOException, FontFormatException {
        JPanel leftPanel = panelFactory.create(
                themeManager.getYellow(),
                350, Integer.MAX_VALUE,
                0
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
                e -> pageManager.setFilter("all")
        );
        all.setActionCommand("all");
        all.setSelected(true);
        all.setBackground(null);

        JRadioButton time = radioFactory.create(
                "Shortest Time",
                loadCustomFont(fonts.DM_SANS_REGULAR, 13),
                e -> pageManager.setFilter("time")
        );
        time.setActionCommand("time");
        time.setBackground(null);

        JRadioButton distance = radioFactory.create(
                "Shortest Distance",
                loadCustomFont(fonts.DM_SANS_REGULAR, 13),
                e -> pageManager.setFilter("distance")
        );
        distance.setActionCommand("distance");
        distance.setBackground(null);

        JRadioButton leastTransfer = radioFactory.create(
                "Least Transfers",
                loadCustomFont(fonts.DM_SANS_REGULAR, 13),
                e -> pageManager.setFilter("transfers")
        );
        leastTransfer.setActionCommand("transfers");
        leastTransfer.setBackground(null);

        JRadioButton cheapest = radioFactory.create(
                "Cheapest Fare",
                loadCustomFont(fonts.DM_SANS_REGULAR, 13),
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
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
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

    private JPanel createInfoPanel() {
        infoPanel = panelFactory.create(
                themeManager.getWhite(),
                Integer.MAX_VALUE, 600, 0
        );
        setInfoMessage("No chosen route.");
        return infoPanel;
    }

    // --------------------- RIGHT PANEL ---------------------
    private JPanel createRightPanel() throws IOException, FontFormatException, SQLException {
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

    private RoundingOfPanels createLocationsPanel() throws IOException, FontFormatException, SQLException {
        locationPanel = panelFactory.create(
                themeManager.getBlue(),
                550, 500,
                sizeManager.getInstance().getBorderRadiusLarge()
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

        JPanel panel = panelFactory.create(null, 0, 0, 0);
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);

        JLabel titleLabel = labelFactory.create(
                title,
                loadCustomFont(fonts.DM_SANS_BOLD, 14f),
                themeManager.getWhite()
        );
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Location"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 12f));
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(loadCustomFont(fonts.DM_SANS_BOLD, 12f));
        table.getTableHeader().setBackground(themeManager.getBlue().darker());
        table.getTableHeader().setForeground(themeManager.getWhite());
        table.setSelectionBackground(themeManager.getYellow());
        table.setSelectionForeground(themeManager.getBlack());

        List<String> stopNames = pageManager.getAllStopNames();
        for (String stop : stopNames) {
            model.addRow(new Object[]{stop});
        }

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    String selected = (String) model.getValueAt(row, 0);
                    if (isFrom) {
                        currentLocation.setText(selected);
                    } else {
                        destination.setText(selected);
                    }
                    if (!currentLocation.getText().trim().isEmpty() &&
                            !destination.getText().trim().isEmpty()) {
                        SwingUtilities.invokeLater(() -> searchRoutes());
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(themeManager.getWhite(), 1));
        scrollPane.getViewport().setBackground(themeManager.getWhite());

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
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

    /* --------------------- SEARCH & DISPLAY --------------------- */
    private void searchRoutes() {
        try {
            String selectedFilter = filterButtonGroup.getSelection() != null
                    ? filterButtonGroup.getSelection().getActionCommand() : "all";
            pageManager.setFilter(selectedFilter);

            String from = currentLocation.getText().trim();
            String to = destination.getText().trim();

            mainPageManager.SearchResult result = pageManager.searchRoutes(from, to);

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
            throws IOException, FontFormatException, SQLException {
        clearRouteContainer();

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
            for (RouteComponent route : routes) {
                List<RouteComponent> segments = getSegments(route);
                JPanel panel;
                if (segments.size() == 1) {
                    panel = factoryPanel.createRoutePanel(
                            segments.get(0), this::displayRouteInfo, pageManager);
                } else {
                    panel = factoryPanel.createTransferRoutePanel(
                            segments, this::displayTransferRouteInfo);
                }
                panel.setAlignmentX(Component.CENTER_ALIGNMENT);
                routeContainer.add(panel);
                routeContainer.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
            }
        }
        routeContainer.revalidate();
        routeContainer.repaint();
    }

    // --------------------- SINGLE ROUTE INFO ---------------------
    private void displayRouteInfo(RouteComponent route) {
        displayedRoute = new ArrayList<>(List.of(route));
        infoPanel.removeAll();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBackground(themeManager.getWhite());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        try {
            JPanel content = panelFactory.create(null, 0, 0, 0);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setOpaque(false);

            JPanel details = panelFactory.create(null, 0, 0, 0);
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

    private JPanel createInfoSection(RouteComponent route) throws IOException, FontFormatException {
        JPanel sec = panelFactory.create(null, 0, 0, 0);
        sec.setLayout(new BoxLayout(sec, BoxLayout.Y_AXIS));
        sec.setOpaque(false);

        JPanel row1 = panelFactory.create(null, 0, 0, 0);
        row1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row1.add(createInfoItem("Transfers:", route.getTransfers() + "    "));
        row1.add(createInfoItem("Stops:", String.valueOf(route.getStops())));

        JPanel row2 = panelFactory.create(null, 0, 0, 0);
        row2.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row2.add(createInfoItem("Fare:", "Php " + String.format("%.2f", route.getFare()) + "    "));
        row2.add(createInfoItem("Details:", route.getDetails()));

        sec.add(row1); sec.add(row2);
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

    private JPanel createStopsSection(RouteComponent route) throws IOException, FontFormatException {
        JPanel sec = panelFactory.create(null, 0, 0, 0);
        sec.setLayout(new BoxLayout(sec, BoxLayout.Y_AXIS));
        sec.setOpaque(false);
        sec.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBlack().brighter().brighter(), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        String stops = String.join(" to ", route.getRouteStops());
        sec.add(labelFactory.create("Route Stops",
                loadCustomFont(fonts.DM_SANS_BOLD, 13f), themeManager.getBlack()));
        sec.add(Box.createVerticalStrut(5));
        sec.add(labelFactory.create("<html>" + stops + "</html>",
                loadCustomFont(fonts.DM_SANS_REGULAR, 13f), themeManager.getBlack().brighter()));
        return sec;
    }

    private JPanel createButtonSection(RouteComponent route) throws IOException, FontFormatException {
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
            @Override public void mouseClicked(MouseEvent e) {
                setSavedRoutes(route);
            }
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
        take.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                try (Connection conn = DatabaseInstance.getInstance().getConnection()) {
                    ArrayList<RouteManager.JeepneyInfo> jeepneys = routeManager.getJeepneysForRoute(route.getRoute(), conn);
                    if (jeepneys.isEmpty()) {
                        JOptionPane.showMessageDialog(mainPage.this, "No jeepneys on this route.", "No Jeepneys", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    RouteManager.JeepneyInfo available = jeepneys.stream().filter(j -> !j.isFull()).findFirst().orElse(null);
                    if (available == null) {
                        JOptionPane.showMessageDialog(mainPage.this, "All jeepneys full.", "Full", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    routeManager.boardJeepney(available.getPlateNumber(), conn);
                    JOptionPane.showMessageDialog(mainPage.this,
                            "Boarded " + available.getPlateNumber() + "!\nPassengers: " + (available.getCurrentPassengers() + 1) + "/" + available.getCapacity(),
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainPage.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        p.add(take);
        return p;
    }

    // --------------------- TRANSFER ROUTE INFO ---------------------
    private void displayTransferRouteInfo(List<RouteComponent> segments) {
        displayedRoute = new ArrayList<>(segments);
        infoPanel.removeAll();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBackground(themeManager.getWhite());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        try {
            JPanel content = panelFactory.create(null, 0, 0, 0);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setOpaque(false);

            mainPageManager.RouteDetails details = pageManager.calculateTransferMetrics(segments.get(0));

            JPanel header = panelFactory.create(null, 0, 0, 0);
            header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
            header.setOpaque(false);
            JLabel title = new JLabel("Transfer Route (" + segments.size() + " legs)");
            title.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 20f));
            title.setForeground(themeManager.getBlack());
            JLabel eta = labelFactory.create(details.getTotalETA() + " min",
                    loadCustomFont(fonts.DM_SANS_BOLD, 14f), themeManager.getBlack());
            header.add(title);
            header.add(Box.createHorizontalGlue());
            header.add(eta);

            JPanel summary = panelFactory.create(null, 0, 0, 0);
            summary.setLayout(new BoxLayout(summary, BoxLayout.X_AXIS));
            summary.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            summary.add(createTransferSummarySection(details));
            summary.add(createTransferSaveButton(segments.get(0)));

            JPanel segs = panelFactory.create(null, 0, 0, 0);
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

    private JPanel createTransferSummarySection(mainPageManager.RouteDetails details) throws IOException, FontFormatException {
        JPanel sec = panelFactory.create(null, 0, 0, 0);
        sec.setLayout(new BoxLayout(sec, BoxLayout.Y_AXIS));
        sec.setOpaque(false);

        JPanel r1 = panelFactory.create(null, 0, 0, 0);
        r1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        r1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        r1.add(createInfoItem("Transfers:", String.valueOf(details.getTransfers())));
        r1.add(createInfoItem("Total Stops:", String.valueOf(details.getTotalStops())));

        JPanel r2 = panelFactory.create(null, 0, 0, 0);
        r2.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        r2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        r2.add(createInfoItem("Total Fare:", "Php " + String.format("%.2f", details.getTotalFare())));
        r2.add(createInfoItem("Segments:", String.valueOf(details.getSegments())));

        sec.add(r1); sec.add(r2);
        return sec;
    }

    private JPanel createTransferSaveButton(RouteComponent route) throws IOException, FontFormatException {
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
        info.add(labelFactory.create(seg.getFromLocation() + " to " + seg.getDestination(),
                loadCustomFont(fonts.DM_SANS_BOLD, 13f), themeManager.getBlack().brighter()));
        info.add(createInfoItem("    Fare:", "Php " + String.format("%.2f", seg.getFare())));
        info.add(createInfoItem("Stops:", String.valueOf(seg.getStops())));
        info.add(createInfoItem("ETA:", seg.getEta() + " min"));
        p.add(info);

        String stops = String.join(" to ", seg.getRouteStops());
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
                RoundingOfPanels rp = panelFactory.create(
                        themeManager.getWhite(),
                        500, 40, 30
                );
                rp.setLayout(new BorderLayout());
                rp.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                String labelText;
                int totalEta = r.getEta();
                if (segs.size() == 1) {
                    RouteComponent s = segs.get(0);
                    labelText = "<html>" + s.getFromLocation() + " to " + s.getDestination() +
                            " <b><i>&nbsp;via&nbsp;</i></b>" + s.getRoute() +
                            " &nbsp;&nbsp;(" + totalEta + " min)</html>";
                } else {
                    labelText = "<html>" + segs.get(0).getFromLocation() + " to " + segs.get(segs.size()-1).getDestination() +
                            " <b><i>&nbsp;via transfer&nbsp;</i></b>" + segs.get(0).getRoute() + " to " + segs.get(1).getRoute() +
                            " &nbsp;&nbsp;(" + totalEta + " min)</html>";
                }

                JLabel lbl = new JLabel(labelText);
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
            SwingUtilities.invokeLater(mainPage.this::refreshCurrentRouteInfo);
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
                mainPage p = new mainPage(dummy);
                f.add(p);
                f.setSize(1920, 1080);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }
}