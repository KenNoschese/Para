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
                setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
                setBackground(themeManager.getWhite());

                JLabel logo = Images.getInstance().getParaLogoLabel(175, 175);
                add(logo);
                add(new darkModeToggle());

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

    /**
     * ==========================
     *  Route Panel
     * ==========================
     */
    public static JPanel createRoutePanel(RouteData routeData) {
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
                int[] columnWidths = {150, 100, 100, 600, 100, 100};
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
                int[] columnWidths = {150, 100, 100, 600, 100, 100};
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

        JLabel titleLabel = new JLabel("Create an account", SwingConstants.CENTER);
        titleLabel.setFont(fonts.loadCustomFont(fonts.DM_SANS_BOLD, 28f));
        titleLabel.setForeground(themeManager.getBlack());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Enter a username and password", SwingConstants.CENTER);
        subtitleLabel.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        subtitleLabel.setForeground(themeManager.getGray());
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField usernameField = new RoundingOfTextfields(0);
        usernameField.setMaximumSize(new Dimension(400, 45));
        usernameField.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        usernameField.setForeground(Color.GRAY);
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField passwordField = new RoundingOfTextfields(0);
        passwordField.setMaximumSize(new Dimension(400, 45));
        passwordField.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        passwordField.setForeground(Color.GRAY);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        RoundingOfButtons signUpButton = new RoundingOfButtons("Sign Up");
        signUpButton.setArc(30, 30);
        signUpButton.setMaximumSize(new Dimension(400, 45));
        signUpButton.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        signUpButton.setForeground(themeManager.getWhite());
        signUpButton.setBackground(themeManager.getBlack());
        signUpButton.setBorder(BorderFactory.createEmptyBorder());
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel orLabel = new JLabel("or", SwingConstants.CENTER);
        orLabel.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 14f));
        orLabel.setForeground(Color.GRAY);
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        RoundingOfButtons loginButton = new RoundingOfButtons("Login");
        loginButton.setArc(30, 30);
        loginButton.setMaximumSize(new Dimension(400, 45));
        loginButton.setFont(fonts.loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        loginButton.setForeground(themeManager.getWhite());
        loginButton.setBackground(themeManager.getRed());
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> cardChanger.accept("LANDING"));

        formContent.add(titleLabel);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingLarge()));
        formContent.add(subtitleLabel);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingLarge()));
        formContent.add(usernameField);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingLarge()));
        formContent.add(passwordField);
        formContent.add(Box.createVerticalStrut(sizeManager.getInstance().getSpacingLarge()));
        formContent.add(signUpButton);
        formContent.add(orLabel);
        formContent.add(loginButton);

        formPanel.add(formContent, BorderLayout.CENTER);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        return mainPanel;
    }

}
