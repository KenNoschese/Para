package org.example.gui.pages;

import org.example.DatabaseManager.RouteDatabase.NavigationFacade;
import org.example.DatabaseManager.RouteDatabase.RouteManager;
import org.example.DatabaseManager.RouteDatabase.StrategyClasses.*;
import org.example.gui.appManager.ThemeManager;
import org.example.gui.appManager.darkModeToggle;
import org.example.gui.appManager.sizeManager;
import org.example.gui.components.*;
import org.example.gui.resources.RouteData;
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
import java.util.Optional;
import java.util.function.Consumer;

import static org.example.gui.components.Factories.factoryPanel.createUserButton;

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
    private String currentFilter = "all"; //default


    public mainPage(Consumer<String> cardChanger) throws IOException, FontFormatException, SQLException {
        this.cardChanger = cardChanger;
        this.themeManager = ThemeManager.getInstance();
        this.themeManager.addThemeChangeListener(this);
        setupPanel();
    }

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
        JPanel center = new JPanel();
        center.setBackground(themeManager.getBackgroundColor());
        center.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        JPanel contentPane = new JPanel();
        contentPane.setBackground(themeManager.getBackgroundColor());
        contentPane.setPreferredSize(new Dimension(1920, 1080));
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        contentPane.add(createLeftJPanel());
        contentPane.add(createCenterPanel());
        contentPane.add(createRightPanel());

        center.add(contentPane);
        return center;
    }

    private JPanel createCenterPanel() throws IOException, FontFormatException {
        JPanel center = new JPanel();
        center.setBackground(themeManager.getBackgroundColor());
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setPreferredSize(new Dimension(1000, Integer.MAX_VALUE));

        center.add(createRouteContainer());
        center.add(createInfoPanel());

        return center;
    }

    private JPanel createRightPanel() throws IOException, FontFormatException {
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(themeManager.getBackgroundColor());
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(570, Integer.MAX_VALUE));
        rightPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        savedLabel = createSavedLabel();
        locationsLabel = createLocationsLabel();

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

    private JPanel createLeftJPanel() throws IOException, FontFormatException {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(350, Integer.MAX_VALUE));
        leftPanel.setBackground(themeManager.getYellow());

        darkModeToggle darkMode = new darkModeToggle();

        userButton = createUserButton();
        userButton.setPreferredSize(new Dimension(200, 30));

        JPanel header = new JPanel();
        header.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));
        header.setPreferredSize(new Dimension(350, 50));
        header.setOpaque(false);

        header.add(userButton);
        header.add(darkMode);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(themeManager.getYellow());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        contentPanel.add(createTextContainer());

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(contentPanel, BorderLayout.NORTH);

        leftPanel.add(header, BorderLayout.NORTH);
        leftPanel.add(wrapper, BorderLayout.CENTER);

        return leftPanel;
    }

    private JPanel createTextContainer() throws IOException, FontFormatException {
        textContainer = new RoundingOfPanels(sizeManager.getInstance().getBorderRadiusLarge());
        textContainer.setLayout(new BoxLayout(textContainer, BoxLayout.Y_AXIS));
        textContainer.setBackground(themeManager.getYellow());
        textContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        textContainer.add(createWelcomeContainer());

        return textContainer;
    }

    private JPanel createWelcomeContainer() throws IOException, FontFormatException {
        welcomeContainer = new JPanel();
        welcomeContainer.setLayout(new BoxLayout(welcomeContainer, BoxLayout.Y_AXIS));
        welcomeContainer.setBackground(themeManager.getYellow());
        welcomeContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        wcQuestion = new JLabel("<html>Where do you want<br>to go?</html>");
        wcQuestion.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 22f));
        wcQuestion.setForeground(themeManager.getForegroundColor());
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
        inputContainer = new JPanel();
        inputContainer.setLayout(new BoxLayout(inputContainer, BoxLayout.Y_AXIS));
        inputContainer.setBackground(themeManager.getYellow());
        inputContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        currentLocation = new RoundingOfTextfields(26);
        currentLocation.setPreferredSize(new Dimension(280, 40));
        currentLocation.setMaximumSize(new Dimension(280, 40));
        currentLocation.setBackground(themeManager.getComponentsColor());
        currentLocation.setForeground(themeManager.getForegroundColor());
        currentLocation.setFont(loadCustomFont(fonts.DM_SANS_ITALIC, sizeManager.getInstance().getTextSmall()));
        currentLocation.setPlaceholder("Start");
        currentLocation.setAlignmentX(Component.LEFT_ALIGNMENT);

        destination = new RoundingOfTextfields(26);
        destination.setPreferredSize(new Dimension(280, 40));
        destination.setMaximumSize(new Dimension(280, 40));
        destination.setBackground(themeManager.getComponentsColor());
        destination.setForeground(themeManager.getForegroundColor());
        destination.setFont(loadCustomFont(fonts.DM_SANS_ITALIC, sizeManager.getInstance().getTextSmall()));
        destination.setPlaceholder("End");
        destination.setAlignmentX(Component.LEFT_ALIGNMENT);

        submitButton = new RoundingOfButtons("View Available Routes");
        submitButton.setArc(70, 70);
        submitButton.setPreferredSize(new Dimension(250, 40));
        submitButton.setMaximumSize(new Dimension(250, 40));
        submitButton.setBackground(themeManager.getGreen());
        submitButton.setForeground(themeManager.getWhite());
        submitButton.setFont(loadCustomFont(fonts.DM_SANS_BOLD, sizeManager.getInstance().getTextSmall()));
        submitButton.setArc(sizeManager.getInstance().getBorderRadiusLarge(), sizeManager.getInstance().getBorderRadiusLarge());
        submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);

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
        inputContainer.add(Box.createVerticalStrut(50));
        inputContainer.add(createFilterPanel());
        inputContainer.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingMedium()));
        inputContainer.add(submitButton);
        inputContainer.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));

        return inputContainer;
    }

    private JPanel createFilterPanel() throws IOException, FontFormatException {
        JPanel filterContainer = new JPanel();
        filterContainer.setLayout(new BoxLayout(filterContainer, BoxLayout.Y_AXIS));
        filterContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterContainer.setBackground(themeManager.getYellow());

        JLabel filter = new JLabel("Filter for");
        filter.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 15));
        filter.setAlignmentX(Component.LEFT_ALIGNMENT);

        JRadioButton all = new JRadioButton("All Routes");
        all.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 13));

        all.addActionListener(e -> {
            currentFilter = "all";
            if (!currentLocation.getText().trim().isEmpty() && !destination.getText().trim().isEmpty()) {
                searchRoutes();
            }
            searchRoutes();
        });

        JRadioButton time = new JRadioButton("Shortest Time");
        time.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 13));

        time.addActionListener(e -> {
            currentFilter = "time";
            if (!currentLocation.getText().trim().isEmpty() && !destination.getText().trim().isEmpty()) {
                searchRoutes();
            }
            searchRoutes();
        });
        JRadioButton distance = new JRadioButton("Shortest Distance");
        distance.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 13));

        distance.addActionListener(e -> {
            currentFilter = "distance";
            if (!currentLocation.getText().trim().isEmpty() && !destination.getText().trim().isEmpty()) {
                searchRoutes();
            }
            searchRoutes();
        });
        JRadioButton leastTransfer = new JRadioButton("Least Transfers");
        leastTransfer.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 13));

        leastTransfer.addActionListener(e -> {
            currentFilter = "transfers";
            if (!currentLocation.getText().trim().isEmpty() && !destination.getText().trim().isEmpty()) {
                searchRoutes();
            }
            searchRoutes();
        });

        JRadioButton cheapest = new JRadioButton("Cheapest Fare");
        cheapest.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 13));

        cheapest.addActionListener(e -> {
            currentFilter = "fare";
            if (!currentLocation.getText().trim().isEmpty() && !destination.getText().trim().isEmpty()) {
                searchRoutes();
            }
            searchRoutes();
        });

        ButtonGroup group = new ButtonGroup();
        group.add(all);
        group.add(time);
        group.add(distance);
        group.add(cheapest);
        group.add(leastTransfer);

        time.setAlignmentX(Component.LEFT_ALIGNMENT);
        distance.setAlignmentX(Component.LEFT_ALIGNMENT);
        cheapest.setAlignmentX(Component.LEFT_ALIGNMENT);
        leastTransfer.setAlignmentX(Component.LEFT_ALIGNMENT);

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
        infoPanel = new RoundingOfPanels(30);
        infoPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 600));
        infoPanel.setMinimumSize(new Dimension(Integer.MAX_VALUE, 600));
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));
        infoPanel.setBackground(themeManager.getWhite());

        setInfoMessage("No chosen route.");
        return infoPanel;
    }

    private void displayRouteInfo(RouteData route) {
        infoPanel.removeAll();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBackground(themeManager.getWhite());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        try {
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setOpaque(false);

            JPanel headerPanel = createHeaderPanel(route);
            JPanel infoSection = createInfoSection(route);
            JPanel stopsSection = createStopsSection(route);
            JPanel buttonSection = createButtonSection(route);

            contentPanel.add(headerPanel);
            contentPanel.add(Box.createVerticalStrut(30));
            contentPanel.add(infoSection);
            contentPanel.add(Box.createVerticalStrut(20));
            contentPanel.add(stopsSection);
            contentPanel.add(Box.createVerticalGlue());
            contentPanel.add(Box.createVerticalStrut(20));
            contentPanel.add(buttonSection);

            infoPanel.add(contentPanel, BorderLayout.CENTER);

        } catch (Exception e) {
            e.printStackTrace();
            setInfoMessage("Error displaying route info.");
        }

        infoPanel.revalidate();
        infoPanel.repaint();
    }

    private JPanel createHeaderPanel(RouteData route) throws IOException, FontFormatException {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel(route.getRoute());
        title.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 20f));
        title.setForeground(themeManager.getBlack());

        JLabel eta = new JLabel(route.getEta() + " min");
        eta.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 14f));
        eta.setForeground(themeManager.getBlack());

        headerPanel.add(title);
        headerPanel.add(Box.createHorizontalGlue());
        headerPanel.add(eta);

        return headerPanel;
    }

    private JPanel createInfoSection(RouteData route) throws IOException, FontFormatException {
        JPanel infoSection = new JPanel();
        infoSection.setLayout(new BoxLayout(infoSection, BoxLayout.Y_AXIS));
        infoSection.setOpaque(false);
        infoSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row1.setOpaque(false);
        row1.setAlignmentX(Component.LEFT_ALIGNMENT);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JPanel transfersPanel = createInfoItem("Transfers:", String.valueOf(route.getTransfers()));
        transfersPanel.setPreferredSize(new Dimension(400, 20));
        row1.add(transfersPanel);

        JPanel stopsPanel = createInfoItem("Stops:", String.valueOf(route.getStops()));
        stopsPanel.setPreferredSize(new Dimension(400, 20));
        row1.add(stopsPanel);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row2.setOpaque(false);
        row2.setAlignmentX(Component.LEFT_ALIGNMENT);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JPanel farePanel = createInfoItem("Fare:", "Php " + String.format("%.2f", route.getFare()));
        farePanel.setPreferredSize(new Dimension(400, 20));
        row2.add(farePanel);

        JPanel detailsPanel = createInfoItem("Details:", route.getDetails());
        detailsPanel.setPreferredSize(new Dimension(400, 20));
        row2.add(detailsPanel);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row3.setOpaque(false);
        row3.setAlignmentX(Component.LEFT_ALIGNMENT);
        row3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JPanel capacityPanel = createInfoItem("Capacity:", "N/A");
        capacityPanel.setPreferredSize(new Dimension(400, 20));
        row3.add(capacityPanel);

        JPanel arrivalPanel = createInfoItem("Est. Arrival:", "N/A");
        arrivalPanel.setPreferredSize(new Dimension(400, 20));
        row3.add(arrivalPanel);

        infoSection.add(row1);

        infoSection.add(row2);

        infoSection.add(row3);

        return infoSection;
    }

    private JPanel createInfoItem(String labelText, String valueText) throws IOException, FontFormatException {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText + " ");
        label.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 13f));
        label.setForeground(themeManager.getBlack());

        JLabel value = new JLabel(valueText);
        value.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 13f));
        value.setForeground(themeManager.getBlack().brighter());

        panel.add(label);
        panel.add(value);
        panel.add(Box.createHorizontalGlue());

        return panel;
    }

    private JPanel createStopsSection(RouteData route) throws IOException, FontFormatException {
        JPanel stopsSection = new JPanel();
        stopsSection.setLayout(new BoxLayout(stopsSection, BoxLayout.Y_AXIS));
        stopsSection.setOpaque(false);
        stopsSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        stopsSection.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBlack().brighter().brighter(), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        String stopsDisplay = String.join(" ➡ ", route.getRouteStops());
        JLabel stopsLabel = new JLabel("Route Stops");
        stopsLabel.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 13f));
        stopsLabel.setForeground(themeManager.getBlack());
        stopsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel stopsValue = new JLabel("<html>" + stopsDisplay + "</html>");
        stopsValue.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 13f));
        stopsValue.setForeground(themeManager.getBlack().brighter());
        stopsValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        stopsSection.add(stopsLabel);
        stopsSection.add(Box.createVerticalStrut(5));
        stopsSection.add(stopsValue);

        return stopsSection;
    }

    private JPanel createButtonSection(RouteData route) throws IOException, FontFormatException {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundingOfButtons saveBtn = new RoundingOfButtons("Save Route");
        saveBtn.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 13f));
        saveBtn.setBackground(themeManager.getYellow());
        saveBtn.setForeground(themeManager.getBlack());
        saveBtn.setPreferredSize(new Dimension(160, 40));
        saveBtn.setArc(
                sizeManager.getInstance().getBorderRadiusLarge(),
                sizeManager.getInstance().getBorderRadiusLarge()
        );
        saveBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setSavedRoutes(route);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                saveBtn.setBackground(themeManager.getBlue());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                saveBtn.setBackground(themeManager.getYellow());
            }
        });

        RoundingOfButtons offBtn = new RoundingOfButtons("Take Route");
        offBtn.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 13f));
        offBtn.setBackground(themeManager.getGreen());
        offBtn.setForeground(themeManager.getWhite());
        offBtn.setPreferredSize(new Dimension(180, 40));
        offBtn.setArc(
                sizeManager.getInstance().getBorderRadiusLarge(),
                sizeManager.getInstance().getBorderRadiusLarge()
        );

        buttonPanel.add(saveBtn);
        buttonPanel.add(offBtn);

        return buttonPanel;
    }

    private JPanel createInfoLabel(String labelText, String valueText) throws IOException, FontFormatException {
        return createInfoItem(labelText, valueText);
    }

    public void setInfoMessage(String message) {
        setPanelPlaceholder(infoPanel, message);
    }

    private RoundingOfPanels createLocationsPanel() {
        locationPanel = createStatusPanel();
        locationPanel.setPreferredSize(new Dimension(550, 500));
        locationPanel.setMinimumSize(new Dimension(550, 500));
        locationPanel.setMaximumSize(new Dimension(550, 500));
        setPanelPlaceholder(locationPanel, "Wala pa ni.");
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
            for (RouteData savedRoute : savedRoutes) {
                RoundingOfPanels routePanel = new RoundingOfPanels(30);
                routePanel.setLayout(new BorderLayout());
                routePanel.setBackground(themeManager.getWhite());
                routePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                routePanel.setMaximumSize(new Dimension(500, 40));

                JLabel routeLabel = new JLabel("<html>" + savedRoute.getFromLocation() + " to " + savedRoute.getDestination() + " <b><i>&nbsp;via&nbsp;</i></b> " +
                        savedRoute.getRoute() + " &nbsp;&nbsp;(" + savedRoute.getEta() + " min)" + "</html>");
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

    private JLabel createSavedLabel() throws IOException, FontFormatException {
        savedLabel = new JLabel("Saved Routes");
        savedLabel.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 14f));
        savedLabel.setForeground(themeManager.getForegroundColor());
        savedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return savedLabel;
    }

    private JLabel createLocationsLabel() throws IOException, FontFormatException {
        locationsLabel = new JLabel("Locations");
        locationsLabel.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 14f));
        locationsLabel.setForeground(themeManager.getForegroundColor());
        locationsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return locationsLabel;
    }

    private JPanel createRouteContainer() {
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(themeManager.getBackgroundColor());
        mainContainer.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mainContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        routeContainer = new JPanel();
        routeContainer.setBackground(themeManager.getBackgroundColor());
        routeContainer.setLayout(new BoxLayout(routeContainer, BoxLayout.Y_AXIS));
        routeContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel scrollWrapper = new JPanel(new BorderLayout());
        scrollWrapper.setBackground(themeManager.getBackgroundColor());
        scrollWrapper.add(routeContainer, BorderLayout.PAGE_START);

        JScrollPane scrollPane = new JScrollPane(scrollWrapper,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.setBackground(themeManager.getBackgroundColor());
        scrollPane.getViewport().setBackground(themeManager.getBackgroundColor());
        scrollPane.setBorder(null);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(themeManager.getBackgroundColor());
        wrapper.setPreferredSize(new Dimension(Integer.MAX_VALUE, 500));
        wrapper.add(scrollPane, BorderLayout.CENTER);

        mainContainer.add(Box.createVerticalStrut(5));
        mainContainer.add(wrapper);

        return mainContainer;
    }

    private void searchRoutes() {
        try {
            if (navigationFacade == null) {
                System.err.println("NavigationFacade not initialized yet!");
                return;
            }

            String from = currentLocation.getText().trim();
            String to = destination.getText().trim();

            if (from.isEmpty() || to.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter both current location and destination.",
                        "Input Required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String category = "Student";
            try {
                org.example.DatabaseManager.DatabaseInstance db =
                        org.example.DatabaseManager.DatabaseInstance.getInstance();
                String pswd = db.getActivePassword();
                if (pswd != null && !pswd.isEmpty()) {
                    char firstDigit = pswd.charAt(0);
                    if (firstDigit == '1') category = "Regular";
                    else if (firstDigit == '2') category = "Student";
                    else if (firstDigit == '3') category = "PWD";
                    else if (firstDigit == '4') category = "Senior Citizen";
                }
            } catch (Exception ignored) {}

            if (currentFilter.equals("all")) {
                ArrayList<RouteData> allDirectRoutes = routeManager.findRoutes(from, to, category);
                ArrayList<ArrayList<RouteData>> allTransferRoutes =
                        routeManager.findRoutesWithTransfers(from, to, category);

                if (allDirectRoutes.isEmpty() && allTransferRoutes.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "No routes found from " + from + " to " + to,
                            "No Results",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    displayRoutes(allDirectRoutes, allTransferRoutes, from, to);

                    if (!allDirectRoutes.isEmpty()) {
                        displayRouteInfo(allDirectRoutes.get(0));
                    } else if (!allTransferRoutes.isEmpty()) {
                        displayTransferRouteInfo(allTransferRoutes.get(0));
                    }
                }
            } else {
                RouteData bestRoute = navigationFacade.findBestRoute(from, to, category, currentFilter);

                if (bestRoute != null) {
                    ArrayList<RouteData> singleRouteList = new ArrayList<>();
                    singleRouteList.add(bestRoute);
                    displayRoutes(singleRouteList, new ArrayList<>(), from, to);

                    displayRouteInfo(bestRoute);
                } else {
                    ArrayList<ArrayList<RouteData>> transferRoutes =
                            routeManager.findRoutesWithTransfers(from, to, category);

                    if (!transferRoutes.isEmpty()) {
                        RouteStrategy strategy = getStrategyForPriority(currentFilter);
                        navigationFacade.setRouteStrategy(strategy);
                        Optional<ArrayList<RouteData>> bestTransferRoute =
                                strategy.findBestTransferRoute(transferRoutes);

                        if (bestTransferRoute.isPresent()) {
                            ArrayList<ArrayList<RouteData>> singleTransferList = new ArrayList<>();
                            singleTransferList.add(bestTransferRoute.get());
                            displayRoutes(new ArrayList<>(), singleTransferList, from, to);
                            displayTransferRouteInfo(bestTransferRoute.get());
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "No routes found for the selected filter.",
                                    "No Results", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "No routes found from " + from + " to " + to,
                                "No Results",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }

        } catch (IOException | FontFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error searching routes: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private RouteStrategy getStrategyForPriority(String priority) {
        switch (priority.toLowerCase()) {
            case "distance":
                return new ShortestDistanceStrategy();
            case "time":
            case "eta":
                return new ShortestTimeStrategy();
            case "transfers":
            case "stops":
                return new LeastTransferStrategy();
            case "fare":
                return new CheapestFareStrategy();
            default:
                return new ShortestTimeStrategy();
        }
    }

    private void displayRoutes(ArrayList<RouteData> routes, ArrayList<ArrayList<RouteData>> allRoutes,
                               String from, String to) throws IOException, FontFormatException {
        routeContainer.removeAll();
        routeContainer.setBackground(themeManager.getBackgroundColor());

        if (allRoutes.isEmpty() && routes.isEmpty()) {
            JLabel noRoutesLabel = new JLabel("No routes found from " + from + " to " + to);
            noRoutesLabel.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 14));
            noRoutesLabel.setForeground(themeManager.getGray());
            noRoutesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            routeContainer.add(Box.createVerticalStrut(50));
            routeContainer.add(noRoutesLabel);
            routeContainer.add(Box.createVerticalStrut(50));
        } else if (!routes.isEmpty()){
            for (int i = 0; i < routes.size(); i++) {

                JPanel panel = factoryPanel.createRoutePanel(routes.get(i), this::displayRouteInfo);
                panel.setAlignmentX(Component.CENTER_ALIGNMENT);
                routeContainer.add(panel);

                if (i < routes.size() - 1) {
                    routeContainer.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
                }
            }
        } else {
            for (int i = 0; i < allRoutes.size(); i++) {
                ArrayList<RouteData> transferRoute = allRoutes.get(i);
                JPanel panel = factoryPanel.createTransferRoutePanel(transferRoute, this::displayTransferRouteInfo);
                panel.setAlignmentX(Component.CENTER_ALIGNMENT);
                routeContainer.add(panel);
                if (i < allRoutes.size() - 1) {
                    routeContainer.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
                }
            }
        }

        routeContainer.revalidate();
        routeContainer.repaint();
    }

    private void displayTransferRouteInfo(ArrayList<RouteData> transferRoute) {
        infoPanel.removeAll();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBackground(themeManager.getWhite());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        try {
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setOpaque(false);

            JPanel headerPanel = new JPanel();
            headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
            headerPanel.setOpaque(false);
            headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel title = new JLabel("Transfer Route (" + transferRoute.size() + " segments)");
            title.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 20f));
            title.setForeground(themeManager.getBlack());

            int totalETA = 0;
            for (RouteData route : transferRoute) {
                totalETA += route.getEta();
            }

            JLabel eta = new JLabel(totalETA + " min");
            eta.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 14f));
            eta.setForeground(themeManager.getBlack());

            headerPanel.add(title);
            headerPanel.add(Box.createHorizontalGlue());
            headerPanel.add(eta);

            JPanel summarySection = createTransferSummarySection(transferRoute);

            JPanel segmentsSection = new JPanel();
            segmentsSection.setLayout(new BoxLayout(segmentsSection, BoxLayout.Y_AXIS));
            segmentsSection.setOpaque(false);
            segmentsSection.setAlignmentX(Component.LEFT_ALIGNMENT);

            for (int i = 0; i < transferRoute.size(); i++) {
                RouteData segment = transferRoute.get(i);
                JPanel segmentPanel = createSegmentPanel(segment, i + 1);
                segmentsSection.add(segmentPanel);
                if (i < transferRoute.size() - 1) {
                    segmentsSection.add(Box.createVerticalStrut(10));
                }
            }

            JPanel buttonSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            buttonSection.setOpaque(false);
            buttonSection.setAlignmentX(Component.LEFT_ALIGNMENT);

            RoundingOfButtons saveBtn = new RoundingOfButtons("Save Route");
            saveBtn.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 13f));
            saveBtn.setBackground(themeManager.getYellow());
            saveBtn.setForeground(themeManager.getBlack());
            saveBtn.setPreferredSize(new Dimension(160, 40));
            saveBtn.setArc(
                    sizeManager.getInstance().getBorderRadiusLarge(),
                    sizeManager.getInstance().getBorderRadiusLarge()
            );
            saveBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setSavedRoutes(transferRoute.get(0));
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    saveBtn.setBackground(themeManager.getBlue());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    saveBtn.setBackground(themeManager.getYellow());
                }
            });

            buttonSection.add(saveBtn);

            contentPanel.add(headerPanel);
            contentPanel.add(Box.createVerticalStrut(20));
            contentPanel.add(summarySection);
            contentPanel.add(Box.createVerticalStrut(20));
            contentPanel.add(segmentsSection);
            contentPanel.add(Box.createVerticalGlue());
            contentPanel.add(Box.createVerticalStrut(5));
            contentPanel.add(buttonSection);

            infoPanel.add(contentPanel, BorderLayout.CENTER);

        } catch (Exception e) {
            e.printStackTrace();
            setInfoMessage("Error displaying transfer route info.");
        }

        infoPanel.revalidate();
        infoPanel.repaint();
    }

    private JPanel createTransferSummarySection(ArrayList<RouteData> transferRoute) throws IOException, FontFormatException {
        JPanel summarySection = new JPanel();
        summarySection.setLayout(new BoxLayout(summarySection, BoxLayout.Y_AXIS));
        summarySection.setOpaque(false);
        summarySection.setAlignmentX(Component.LEFT_ALIGNMENT);

        double totalFare = 0;
        int totalStops = 0;
        int totalTransfers = transferRoute.size() - 1;

        for (RouteData route : transferRoute) {
            totalFare += route.getFare();
            totalStops += route.getStops();
        }

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row1.setOpaque(false);
        row1.setAlignmentX(Component.LEFT_ALIGNMENT);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JPanel transfersPanel = createInfoItem("Transfers:", String.valueOf(totalTransfers));
        transfersPanel.setPreferredSize(new Dimension(400, 20));
        row1.add(transfersPanel);

        JPanel stopsPanel = createInfoItem("Total Stops:", String.valueOf(totalStops));
        stopsPanel.setPreferredSize(new Dimension(400, 20));
        row1.add(stopsPanel);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row2.setOpaque(false);
        row2.setAlignmentX(Component.LEFT_ALIGNMENT);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JPanel farePanel = createInfoItem("Total Fare:", "Php " + String.format("%.2f", totalFare));
        farePanel.setPreferredSize(new Dimension(400, 20));
        row2.add(farePanel);

        JPanel segmentsPanel = createInfoItem("Segments:", String.valueOf(transferRoute.size()));
        segmentsPanel.setPreferredSize(new Dimension(400, 20));
        row2.add(segmentsPanel);

        summarySection.add(row1);
        summarySection.add(row2);

        return summarySection;
    }

    private JPanel createSegmentPanel(RouteData segment, int segmentNumber) throws IOException, FontFormatException {
        JPanel segmentPanel = new JPanel();
        segmentPanel.setLayout(new BoxLayout(segmentPanel, BoxLayout.Y_AXIS));
        segmentPanel.setOpaque(false);
        segmentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        segmentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBlack().brighter().brighter(), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel segmentTitle = new JLabel("Segment " + segmentNumber + ": " + segment.getRoute());
        segmentTitle.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 15f));
        segmentTitle.setForeground(themeManager.getBlack());
        segmentTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel infoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        infoRow.setPreferredSize(new Dimension(Integer.MAX_VALUE, 20));
        infoRow.setOpaque(false);
        infoRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel routeLabel = new JLabel(segment.getFromLocation() + " → " + segment.getDestination());
        routeLabel.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 13f));
        routeLabel.setForeground(themeManager.getBlack().brighter());
        routeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoRow.add(routeLabel);

        JPanel farePanel = createInfoItem("    " + "Fare:", "Php " + String.format("%.2f", segment.getFare()));
        farePanel.setPreferredSize(new Dimension(250, 20));
        infoRow.add(farePanel);

        JPanel stopsPanel = createInfoItem("Stops:", String.valueOf(segment.getStops()));
        stopsPanel.setPreferredSize(new Dimension(150, 20));
        infoRow.add(stopsPanel);

        JPanel etaPanel = createInfoItem("ETA:", segment.getEta() + " min");
        etaPanel.setPreferredSize(new Dimension(150, 20));
        infoRow.add(etaPanel);

        JPanel stopsSection = new JPanel();
        stopsSection.setLayout(new BoxLayout(stopsSection, BoxLayout.Y_AXIS));
        stopsSection.setOpaque(false);
        stopsSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel stopsLabelTitle = new JLabel("Route Stops:");
        stopsLabelTitle.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 12f));
        stopsLabelTitle.setForeground(themeManager.getBlack());
        stopsLabelTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        String stopsDisplay = "";
        if (segment.getRouteStops() != null && !segment.getRouteStops().isEmpty()) {
            stopsDisplay = String.join(" ➡ ", segment.getRouteStops());
        } else {
            stopsDisplay = segment.getFromLocation() + " ➡ " + segment.getDestination();
        }

        JLabel stopsValue = new JLabel("<html>" + stopsDisplay + "</html>");
        stopsValue.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 12f));
        stopsValue.setForeground(themeManager.getBlack().brighter());
        stopsValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        stopsSection.add(stopsLabelTitle);
        stopsSection.add(Box.createVerticalStrut(3));
        stopsSection.add(stopsValue);

        segmentPanel.add(segmentTitle);
        segmentPanel.add(Box.createVerticalStrut(10));
        segmentPanel.add(infoRow);
        segmentPanel.add(stopsSection);

        return segmentPanel;
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
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
