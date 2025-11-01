package org.example.gui.components.Factories;

import org.example.gui.appManager.ThemeManager;
import org.example.gui.appManager.sizeManager;
import org.example.gui.config.AnimationConfig;
import org.example.gui.resources.Images;
import org.example.DatabaseManager.RouteDatabase.RouteData;
import org.example.gui.resources.fonts;
import org.example.gui.appManager.darkModeToggle;
import org.example.gui.components.RoundingOfPanels;
import org.example.gui.components.RoundingOfButtons;
import org.example.gui.components.RoundingOfTextfields;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import static org.example.gui.resources.fonts.loadCustomFont;

public class factoryPanel {

    public static JPanel createFooterPanel() throws IOException, FontFormatException {
        JPanel footerPanel = new JPanel();
        footerPanel.setPreferredSize(sizeManager.getInstance().getFooterSize());
        footerPanel.setBackground(ThemeManager.getInstance().getBackgroundColor());
        footerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 10));

        JLabel copyright = new JLabel("© 2025 Para! - All Rights Reserved");
        copyright.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 12f));

        footerPanel.add(copyright);

        return footerPanel;
    }

    public static RoundingOfButtons createUserButton() throws IOException, FontFormatException {
        RoundingOfButtons userButton;
        ThemeManager themeManager = ThemeManager.getInstance();
        String username = "Guest";
        String category = "N/A";

        try {
            org.example.DatabaseManager.DatabaseInstance db = org.example.DatabaseManager.DatabaseInstance.getInstance();
            username = db.getActiveUsername();
            String pswd = db.getActivePassword();
            System.out.println("Active user: " + username + ", pass: " + pswd);

            if (pswd != null && !pswd.isEmpty()) {
                char firstDigit = pswd.charAt(0);
                if (firstDigit == '1') category = "Regular";
                else if (firstDigit == '2') category = "Student";
                else if (firstDigit == '3') category = "PWD";
                else if (firstDigit == '4') category = "Senior Citizen";
            }
        } catch (Exception ignored) {}

        userButton = new RoundingOfButtons(username + "  (" + category + ")");
        userButton.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 13));
        userButton.setFocusPainted(false);
        userButton.setBorderPainted(false);
        userButton.setBackground(new Color(255, 255, 255, 180));
        userButton.setForeground(themeManager.getBlack());
        userButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userButton.setBounds(15, 10, 220, 30);
        userButton.setHorizontalAlignment(SwingConstants.LEFT);

        userButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                userButton.setBackground(themeManager.getYellow());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                userButton.setBackground(new Color(255, 255, 255, 180));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                JPopupMenu menu = new JPopupMenu();
                JMenuItem profileItem = new JMenuItem("Profile");
                JMenuItem logoutItem = new JMenuItem("Logout");
                logoutItem.addActionListener(evt -> {

                    org.example.DatabaseManager.DatabaseInstance.getInstance().close();
                    System.exit(0);
                });
                menu.add(profileItem);
                menu.add(logoutItem);
                menu.show(userButton, 0, userButton.getHeight());
            }
        });

        return userButton;
    }

    public static JPanel createHeaderPanel() throws IOException {
        ThemeManager themeManager = ThemeManager.getInstance();
        AnimationConfig config = new AnimationConfig();

        JPanel headerPanel = new JPanel() {
            private Image jeepney;
            private Image backgroundImage;
            private Timer timer;
            private int x = 0;
            private boolean isPaused = false;
            private JButton userButton;

            {
                themeManager.addThemeChangeListener(isDarkMode -> {
                    setBackground(themeManager.getWhite());
                    repaint();
                });
                setupPanel();
                setupJeepneyAnimation();
            }

            private void setupPanel() throws IOException {
                setPreferredSize(sizeManager.getInstance().getHeaderSize());
                setOpaque(false);
                setLayout(null);
                setBackground(themeManager.getWhite());

                JLabel logo = Images.getInstance().getParaLogoLabel(175, 175);
                logo.setBounds((getWidth() / 2) - 87, 0, 175, 175); // center when resized
                addComponentListener(new java.awt.event.ComponentAdapter() {
                    @Override
                    public void componentResized(java.awt.event.ComponentEvent e) {
                        logo.setBounds((getWidth() / 2) - 87, 0, 175, 175);
                    }
                });
                add(logo);

                darkModeToggle toggle = new darkModeToggle();
                toggle.setBounds(getWidth() - 80, 20, 50, 30);
                add(toggle);

                addComponentListener(new java.awt.event.ComponentAdapter() {
                    @Override
                    public void componentResized(java.awt.event.ComponentEvent e) {
                        toggle.setBounds(getWidth() - 80, 20, 50, 30);
                    }
                });

                Images images = Images.getInstance();
                ImageIcon cityIcon = images.getCityIcon();
                ImageIcon jeepIcon = images.getJeepIcon();
                backgroundImage = cityIcon.getImage().getScaledInstance(1920, 160, Image.SCALE_SMOOTH);
                jeepney = jeepIcon.getImage().getScaledInstance(config.jeepneyWidth, config.jeepneyHeight, Image.SCALE_SMOOTH);
            }

            private void setupJeepneyAnimation() {
                timer = new Timer(config.timerInterval, e -> {
                    if (!isPaused) {
                        x += config.speed;
                        if (Math.abs(x - (getWidth() / 2 - jeepney.getWidth(null) / 2)) < config.centerThreshold) {
                            isPaused = true;
                            x = getWidth() / 2 - jeepney.getWidth(null) / 2;
                            Timer pauseTimer = new Timer(config.centerPauseDuration, evt -> {
                                isPaused = false;
                                ((Timer) evt.getSource()).stop();
                            });
                            pauseTimer.setRepeats(false);
                            pauseTimer.start();
                        }
                        if (x >= getWidth()) {
                            isPaused = true;
                            Timer restartTimer = new Timer(config.restartDelay, evt -> {
                                x = 0;
                                isPaused = false;
                            });
                            restartTimer.setRepeats(false);
                            restartTimer.start();
                        }
                    }
                    repaint();
                });
                timer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2D = (Graphics2D) g;
                if (backgroundImage != null) {
                    g2D.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
                }
                if (jeepney != null) {
                    g2D.drawImage(jeepney, x, config.yPosition, null);
                }
            }
        };

        return headerPanel;
    }

    public static JPanel createRoutePanel(RouteData routeData, java.util.function.Consumer<RouteData> onClick) throws IOException, FontFormatException {
        ThemeManager themeManager = ThemeManager.getInstance();
        Color defaultColor = themeManager.getPanelColor();

        RoundingOfPanels routePanel = new RoundingOfPanels(sizeManager.getInstance().getBorderRadiusLarge());
        routePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        routePanel.setBackground(defaultColor);
        routePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        routePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        routePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        routePanel.setOpaque(true);

        JLabel routeTitle = new JLabel(routeData.getRoute());
        routeTitle.setFont(fonts.loadCustomFont(fonts.DM_SANS_BOLD, 16));
        routeTitle.setForeground(themeManager.getBlack());
        routePanel.add(routeTitle);
        routePanel.add(Box.createHorizontalStrut(30));

        JLabel detailsLabel = new JLabel(String.format(
                "<html><b>Details:</b> %s</html>", routeData.getDetails()
        ));
        detailsLabel.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 14));
        detailsLabel.setForeground(themeManager.getBlack());
        routePanel.add(detailsLabel);

        JLabel infoLabel = new JLabel(String.format(
                "<html><b>Transfers:</b> %d <b>| Stops: </b>%d <b>| ETA:</b> %s</html>",
                routeData.getTransfers(),
                routeData.getStops(),
                routeData.getEta()
        ));
        infoLabel.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 13));
        infoLabel.setForeground(themeManager.getForegroundColor());
        routePanel.add(infoLabel);
        routePanel.add(Box.createHorizontalStrut(30));

        JLabel fareLabel = new JLabel(String.format("Php %.2f", routeData.getFare()));
        fareLabel.setFont(fonts.loadCustomFont(fonts.DM_SANS_BOLD, 14));
        fareLabel.setForeground(themeManager.getBlack());
        routePanel.add(fareLabel);

        routePanel.add(Box.createVerticalStrut(8));

        Color hoverColor = themeManager.getYellow().brighter();

        routePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                routePanel.setBackground(hoverColor);
                routePanel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                routePanel.setBackground(defaultColor);
                routePanel.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) {
                    onClick.accept(routeData);
                }
            }
        });

        themeManager.addThemeChangeListener(isDarkMode -> {
            routePanel.setBackground(themeManager.getPanelColor());
            routeTitle.setForeground(themeManager.getBlack());
            detailsLabel.setForeground(themeManager.getYellow());
            infoLabel.setForeground(themeManager.getForegroundColor());
            fareLabel.setForeground(themeManager.getYellow());
            routePanel.repaint();
        });

        return routePanel;
    }

    public static JPanel createTransferRoutePanel(ArrayList<RouteData> transferRoute, java.util.function.Consumer<ArrayList<RouteData>> onClick) throws IOException, FontFormatException {
        ThemeManager themeManager = ThemeManager.getInstance();
        Color defaultColor = themeManager.getPanelColor();

        RoundingOfPanels panel = new RoundingOfPanels(30);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(defaultColor);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        panel.setOpaque(true);

        JLabel title = new JLabel("Transfer Route (" + transferRoute.size() + " segments)");
        title.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 16));
        title.setForeground(themeManager.getBlack());
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        double totalFare = 0;
        int totalDistance = 0;

        for (int i = 0; i < transferRoute.size(); i++) {
            RouteData seg = transferRoute.get(i);

            JLabel segLabel = new JLabel(String.format(
                    "Segment %d: %s — Php %.2f, %d km (%s → %s)",
                    i + 1,
                    seg.getRoute(),
                    seg.getFare(),
                    seg.getDistance(),
                    seg.getFromLocation(),
                    seg.getDestination()
            ));
            segLabel.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 14));
            segLabel.setForeground(themeManager.getBlack());
            panel.add(segLabel);

            totalFare += seg.getFare();
            totalDistance += seg.getDistance();
        }

        panel.add(Box.createVerticalStrut(10));

        JLabel totalLabel = new JLabel(String.format(
                "Total Fare: Php %.2f | Total Distance: %d km",
                totalFare, totalDistance
        ));
        totalLabel.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 14));
        totalLabel.setForeground(themeManager.getForegroundColor());
        panel.add(totalLabel);

        Color hoverColor = themeManager.getYellow().brighter();
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(hoverColor);
                panel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(defaultColor);
                panel.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null && !transferRoute.isEmpty()) {
                    onClick.accept(transferRoute);
                }
            }
        });

        return panel;
    }

    public static RoundingOfPanels createStepPanel(
            int stepNumber,
            String[] textLines,
            JLabel image,
            String themeColor,
            boolean includeButton,
            java.util.function.Consumer<String> cardChanger
    ) throws IOException, FontFormatException {

        ThemeManager themeManager = ThemeManager.getInstance();

        RoundingOfPanels panel = new RoundingOfPanels(sizeManager.getInstance().getBorderRadiusLarge());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(375, 350));
        panel.setBackground(themeColor.equals("blue") ? themeManager.getBlue() : themeManager.getYellow());
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.putClientProperty("themeColor", themeColor);

        // Heading
        JLabel heading = new JLabel("Step " + stepNumber);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setFont(fonts.loadCustomFont(fonts.DM_SANS_BOLD, 18f));
        heading.setForeground(themeManager.getForegroundColor());

        // Text panel
        RoundingOfPanels textPanel = new RoundingOfPanels(sizeManager.getInstance().getBorderRadiusLarge());
        textPanel.setPreferredSize(new Dimension(30, 20));
        textPanel.setMaximumSize(new Dimension(300, textLines.length > 1 ? 75 : 50));
        textPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.setBackground(themeManager.getComponentsColor());

        String labelText = textLines.length > 1 ? "<html>" + String.join("<br>", textLines) + "</html>" : textLines[0];
        JLabel textLabel = new JLabel(labelText);
        textLabel.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        textLabel.setForeground(themeManager.getForegroundColor());
        textPanel.add(textLabel);

        panel.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingLarge()));
        panel.add(heading);
        panel.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingMedium()));
        panel.add(textPanel);

        if (image != null) {
            panel.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
            image.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(image);
        }

        if (includeButton) {
            RoundingOfButtons button = new RoundingOfButtons("Then press search!");
            button.setArc(30, 30);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setPreferredSize(new Dimension(30, 20));
            button.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
            button.setBackground(themeManager.getRed());
            button.setForeground(themeManager.getPink());

            if (cardChanger != null) {
                button.addActionListener(e -> cardChanger.accept("MAIN"));
            }

            panel.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingMedium()));
            panel.add(button);
        }

        return panel;
    }
}