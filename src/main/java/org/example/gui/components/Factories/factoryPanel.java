package org.example.gui.components.Factories;

import org.example.gui.appManager.ThemeManager;
import org.example.gui.appManager.sizeManager;
import org.example.gui.config.AnimationConfig;
import org.example.gui.resources.Images;
import org.example.gui.resources.RouteData;
import org.example.gui.resources.fonts;
import org.example.gui.appManager.darkModeToggle;
import org.example.gui.components.RoundingOfPanels;
import org.example.gui.components.RoundingOfButtons;
import org.example.gui.components.RoundingOfTextfields;
import org.example.gui.pages.mainPage;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import static org.example.gui.resources.fonts.loadCustomFont;

public class factoryPanel {

    /**
     * ==========================
     *  Footer Panel
     * ==========================
     */
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

    /**
     * ==========================
     *  Header Panel
     * ==========================
     */
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
                setupUserButton(); // ✅ added user info button setup
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

                add(new darkModeToggle() {{
                    setBounds(getWidth() - 80, 20, 50, 30);
                    addComponentListener(new java.awt.event.ComponentAdapter() {
                        @Override
                        public void componentResized(java.awt.event.ComponentEvent e) {
                            setBounds(getWidth() - 80, 20, 50, 30);
                        }
                    });
                }});

                Images images = Images.getInstance();
                ImageIcon cityIcon = images.getCityIcon();
                ImageIcon jeepIcon = images.getJeepIcon();
                backgroundImage = cityIcon.getImage().getScaledInstance(1920, 160, Image.SCALE_SMOOTH);
                jeepney = jeepIcon.getImage().getScaledInstance(config.jeepneyWidth, config.jeepneyHeight, Image.SCALE_SMOOTH);
            }

            private void setupUserButton() {
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

                userButton = new JButton(username + "  (" + category + ")");
                userButton.setFont(new Font("Arial", Font.BOLD, 14));
                userButton.setFocusPainted(false);
                userButton.setBorderPainted(false);
                userButton.setBackground(new Color(255, 255, 255, 180));
                userButton.setForeground(themeManager.getBlack());
                userButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                userButton.setBounds(15, 10, 220, 30);
                userButton.setHorizontalAlignment(SwingConstants.LEFT);

                // Simple hover + click menu simulation
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


                add(userButton);
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

    /**
     * ==========================
     *  Route Panel
     * ==========================
     */
    public static JPanel createRoutePanel(RouteData routeData, java.util.function.Consumer<RouteData> onClick) {
        ThemeManager themeManager = ThemeManager.getInstance();

        RoundingOfPanels routePanel = new RoundingOfPanels(sizeManager.getInstance().getBorderRadiusLarge()) {
            private Color defaultColor = themeManager.getPanelColor();

            {
                this.setPreferredSize(new Dimension(Integer.MAX_VALUE, 60));
                this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                setBackground(defaultColor);
                setForeground(themeManager.getForegroundColor());
                setLayout(null);
                putClientProperty("themeColor", "panel");

                setupHoverEffect();
                themeManager.addThemeChangeListener(isDarkMode -> {
                    defaultColor = themeManager.getPanelColor();
                    setBackground(defaultColor);
                    repaint();
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                try {
                    Font dataFont = fonts.loadCustomFont(fonts.DM_SANS_REGULAR, sizeManager.getInstance().getTextSmall());
                    drawTableContent(g2d, dataFont);
                } catch (Exception e) {
                    Font dataFont = new Font("Arial", Font.PLAIN, 12);
                    drawTableContent(g2d, dataFont);
                }

                g2d.dispose();
            }

            private void drawTableContent(Graphics2D g2d, Font dataFont) {
                int[] columnWidths = {300, 150, 150, 300, 100, 100};
                int[] columnX = new int[6];
                columnX[0] = 10;
                for (int i = 1; i < 6; i++) {
                    columnX[i] = columnX[i - 1] + columnWidths[i - 1];
                }

                g2d.setFont(dataFont);
                g2d.setColor(themeManager.getBlack());

                int yPos = (getHeight() + g2d.getFontMetrics().getAscent()) / 2;

                g2d.drawString(routeData.getRoute(), columnX[0], yPos);
                g2d.drawString(String.valueOf(routeData.getTransfers()), columnX[1], yPos);
                g2d.drawString(String.valueOf(routeData.getstops()), columnX[2], yPos);
                g2d.drawString(routeData.getDetails(), columnX[3], yPos);
                g2d.drawString(String.format("Php%.2f", routeData.getFare()), columnX[4], yPos);
                g2d.drawString(String.valueOf(routeData.getETA()), columnX[5], yPos);
            }

            private void setupHoverEffect() {
                Color hoverColor = themeManager.getYellow().brighter();

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        setBackground(hoverColor);
                        repaint();
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (onClick != null) {
                            onClick.accept(routeData);
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        setBackground(defaultColor);
                        repaint();
                    }
                });
            }
        };

        return routePanel;
    }

    /**
     * ==========================
     *  Route Header
     * ==========================
     */
    public static JPanel createRouteHeader() {
        ThemeManager themeManager = ThemeManager.getInstance();

        RoundingOfPanels routeHeader = new RoundingOfPanels(sizeManager.getInstance().getBorderRadiusSmall()) {
            {
                setPreferredSize(new Dimension(Integer.MAX_VALUE, 60));
                setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                setBackground(themeManager.getBackgroundColor());
                setForeground(themeManager.getForegroundColor());
                setLayout(null);
                setBorder(new LineBorder(themeManager.getForegroundColor(), 2));
                putClientProperty("themeColor", "panel");

                themeManager.addThemeChangeListener(isDarkMode -> {
                    setBackground(themeManager.getBackgroundColor());
                    setForeground(themeManager.getForegroundColor());
                    setBorder(new LineBorder(themeManager.getForegroundColor(), 2));
                    repaint();
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                try {
                    Font dataFont = fonts.loadCustomFont(fonts.DM_SANS_BOLD, sizeManager.getInstance().getTextMedium());
                    drawHeader(g2d, dataFont);
                } catch (Exception e) {
                    Font dataFont = new Font("Arial", Font.BOLD, 12);
                    drawHeader(g2d, dataFont);
                }

                g2d.dispose();
            }

            private void drawHeader(Graphics2D g2d, Font font) {
                int[] columnWidths = {300, 150, 150, 300, 100, 100};
                int[] columnX = new int[columnWidths.length];

                columnX[0] = 10;
                for (int i = 1; i < columnWidths.length; i++) {
                    columnX[i] = columnX[i - 1] + columnWidths[i - 1];
                }

                g2d.setFont(font);
                g2d.setColor(themeManager.getForegroundColor());

                int yPos = (getHeight() + g2d.getFontMetrics().getAscent()) / 2 - 4;

                g2d.drawString("Route", columnX[0], yPos);
                g2d.drawString("Transfers", columnX[1], yPos);
                g2d.drawString("Stops", columnX[2], yPos);
                g2d.drawString("Details", columnX[3], yPos);
                g2d.drawString("Fare", columnX[4], yPos);
                g2d.drawString("ETA", columnX[5], yPos);
            }
        };

        return routeHeader;
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

        // Assemble components
        panel.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingLarge()));
        panel.add(heading);
        panel.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingMedium()));
        panel.add(textPanel);

        // Optional image
        if (image != null) {
            panel.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
            image.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(image);
        }

        // Optional button
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

    public static JPanel createLoginPanel(java.util.function.Consumer<String> cardChanger)
            throws IOException, FontFormatException {

        ThemeManager themeManager = ThemeManager.getInstance();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(themeManager.getWhite());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 100, 0));

        JLabel logo = Images.getInstance().getParaLogoLabel(250, 250);
        mainPanel.add(logo, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(themeManager.getWhite());

        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(themeManager.getWhite());

        JLabel titleLabel = new JLabel("Enter your username and password", SwingConstants.CENTER);
        titleLabel.setFont(fonts.loadCustomFont(fonts.DM_SANS_BOLD, 16f));
        titleLabel.setForeground(themeManager.getBlack());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Don't have an account?", SwingConstants.CENTER);
        subtitleLabel.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        subtitleLabel.setForeground(themeManager.getBlack());
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        RoundingOfTextfields usernameField = new RoundingOfTextfields(20);
        usernameField.setMaximumSize(new Dimension(400, 45));
        usernameField.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        usernameField.setForeground(themeManager.getBlack());
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setBorderColor(themeManager.getGray());
        usernameField.setPlaceholder("Username");

        RoundingOfTextfields passwordField = new RoundingOfTextfields(20);
        passwordField.setMaximumSize(new Dimension(400, 45));
        passwordField.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        passwordField.setForeground(themeManager.getBlack());
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorderColor(themeManager.getGray());
        passwordField.setPlaceholder("Password");

        RoundingOfButtons signUpButton = new RoundingOfButtons("Sign Up");
        signUpButton.setArc(30, 30);
        signUpButton.setMaximumSize(new Dimension(400, 45));
        signUpButton.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        signUpButton.setForeground(themeManager.getWhite());
        signUpButton.setBackground(themeManager.getBlack());
        signUpButton.setBorder(BorderFactory.createEmptyBorder());
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        signUpButton.addActionListener(e -> {
            System.out.println("➡️ Sign Up button clicked");
            cardChanger.accept("SIGNUP");
        });

        JLabel orLabel = new JLabel("---------- or ----------", SwingConstants.CENTER);
        orLabel.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 14f));
        orLabel.setForeground(themeManager.getGray());
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        RoundingOfButtons loginButton = new RoundingOfButtons("Login");
        loginButton.setArc(30, 30);
        loginButton.setMaximumSize(new Dimension(400, 45));
        loginButton.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        loginButton.setForeground(themeManager.getWhite());
        loginButton.setBackground(themeManager.getRed());
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ✅ Updated Login Button functionality using DatabaseInstance.loginAsUser()

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Please enter both username and password.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            char firstDigit = password.charAt(0);
            String category;
            if (firstDigit == '1') category = "Regular";
            else if (firstDigit == '2') category = "Student";
            else if (firstDigit == '3') category = "PWD";
            else if (firstDigit == '4') category = "Senior Citizen";
            else category = "Unknown";

            if (category.equals("Unknown")) {
                JOptionPane.showMessageDialog(mainPanel, "Invalid password format. Cannot identify category.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                System.out.println("Attempting login for user: " + username + " (" + category + ")");

                boolean success = org.example.DatabaseManager.DatabaseInstance.loginAsUser(username, password);

                if (success) {
                    JOptionPane.showMessageDialog(mainPanel,
                            "✅ Login successful!\nWelcome, " + username + " (" + category + ")",
                            "Login Success", JOptionPane.INFORMATION_MESSAGE);

                    System.out.println("✅ Logged in as: " + username + " (" + category + ")");

                    cardChanger.accept("LANDING");

                } else {
                    JOptionPane.showMessageDialog(mainPanel,
                            "Invalid username or password.",
                            "Login Failed", JOptionPane.ERROR_MESSAGE);
                    System.out.println("Login failed for user: " + username);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainPanel,
                        "Unexpected error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("Unexpected error: " + ex.getMessage());
            }
        });

        formContent.add(titleLabel);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingLarge()));
        formContent.add(usernameField);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingLarge()));
        formContent.add(passwordField);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingLarge()));
        formContent.add(loginButton);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingLarge()));
        formContent.add(subtitleLabel);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingSmall()));
        formContent.add(signUpButton);


        formPanel.add(formContent, BorderLayout.CENTER);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        return mainPanel;
    }
}