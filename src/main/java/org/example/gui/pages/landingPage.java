package org.example.gui.pages;

import org.example.gui.appManager.darkModeToggle;
import org.example.gui.appManager.sizeManager;
import org.example.gui.components.FooterPanel;
import org.example.gui.components.roundButton;
import org.example.gui.components.roundPanel;
import org.example.gui.config.AnimationConfig;
import org.example.gui.appManager.ThemeManager;
import org.example.gui.resources.Images;
import org.example.gui.resources.fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class landingPage extends JPanel implements ThemeManager.ThemeChangeListener {
    private Consumer<String> cardChanger;
    private Image jeepney;
    private Timer timer;
    private AnimationConfig config = new AnimationConfig();
    private int x = 0;
    private boolean isPaused = false;
    private ThemeManager themeManager;

    private JPanel headerPanel;
    private JPanel centerPanel;
    private FooterPanel footerPanel;
    private roundPanel infoContainer;
    private JPanel infoTextPanel;
    private roundButton startButton;
    private roundPanel step1Panel, step2Panel, step3Panel;

    public landingPage(Consumer<String> cardChanger) throws IOException, FontFormatException {
        this.cardChanger = cardChanger;
        this.themeManager = ThemeManager.getInstance();
        this.themeManager.addThemeChangeListener(this);
        setupPanel();
    }

    private void setupPanel() throws IOException, FontFormatException {
        setLayout(new BorderLayout());
        setBackground(themeManager.getBackgroundColor());

        headerPanel = createHeaderPanel();
        centerPanel = createCenterPanel();
        footerPanel = new FooterPanel();

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private ImageIcon createScaledIcon(String path, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(path);
        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    private JLabel createImageLabel(String path, int width, int height) {
        return new JLabel(createScaledIcon(path, width, height), SwingConstants.CENTER);
    }

    private Image createScaledImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        return icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    private JPanel createHeaderPanel() throws IOException, FontFormatException {
        Image backgroundImage = createScaledImage(Images.CITY, 1920, 160);

        JPanel header = new JPanel() {
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
        header.setPreferredSize(new Dimension(sizeManager.HEADER_SIZE));
        header.setOpaque(false);
        header.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        header.setBackground(ThemeManager.getInstance().getWhite());

        jeepney = createScaledImage(Images.JEEP, config.jeepneyWidth, config.jeepneyHeight);
        setupJeepneyAnimation(header);

        header.add(createImageLabel(Images.PARA_LOGO, 175, 175));
        header.add(new darkModeToggle());
        return header;
    }

    private void setupJeepneyAnimation(JPanel header) {
        timer = new Timer(config.timerInterval, e -> {
            if (!isPaused) {
                x += config.speed;
                if (Math.abs(x - (header.getWidth() / 2 - jeepney.getWidth(null) / 2)) < config.centerThreshold) {
                    isPaused = true;
                    x = header.getWidth() / 2 - jeepney.getWidth(null) / 2;
                    Timer pauseTimer = new Timer(config.centerPauseDuration, evt -> {
                        isPaused = false;
                        ((Timer)evt.getSource()).stop();
                    });
                    pauseTimer.setRepeats(false);
                    pauseTimer.start();
                }
                if (x >= header.getWidth()) {
                    isPaused = true;
                    Timer restartTimer = new Timer(config.restartDelay, evt -> {
                        x = 0;
                        isPaused = false;
                    });
                    restartTimer.setRepeats(false);
                    restartTimer.start();
                }
            }
            header.repaint();
        });
        timer.start();
    }

    private JPanel createCenterPanel() throws IOException, FontFormatException {
        JPanel center = new JPanel();
        center.setBackground(themeManager.getBackgroundColor());
        JPanel contentPane = new JPanel();
        contentPane.setBackground(themeManager.getBackgroundColor());
        contentPane.setPreferredSize(new Dimension(sizeManager.CONTENTPANE_SIZE));
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        contentPane.add(createInfoContainer());
        contentPane.add(Box.createVerticalStrut(sizeManager.SPACING_SMALL));
        contentPane.add(createHowToUseLabel());
        contentPane.add(Box.createVerticalStrut(sizeManager.SPACING_SMALL));
        contentPane.add(createInstructionContainer());

        center.add(contentPane);
        return center;
    }

    private JPanel createInfoContainer() throws IOException, FontFormatException {
        infoContainer = new roundPanel(sizeManager.BORDER_RADIUS_LARGE);
        infoContainer.setPreferredSize(new Dimension(sizeManager.INFOCONTAINER_SIZE));
        infoContainer.setBackground(themeManager.getYellow());
        infoContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 100, -75));
        // Set theme color property for custom theming
        infoContainer.putClientProperty("themeColor", "yellow");

        infoContainer.add(createInfoTextPanel());
        infoContainer.add(createMockUpLabel());

        return infoContainer;
    }

    private JPanel createInfoTextPanel() throws IOException, FontFormatException {
        infoTextPanel = new JPanel();
        infoTextPanel.setBackground(themeManager.getYellow());
        infoTextPanel.setLayout(new BoxLayout(infoTextPanel, BoxLayout.Y_AXIS));
        infoTextPanel.putClientProperty("themeColor", "yellow");

        JLabel welcomeText = new JLabel("Welcome to Para!");
        welcomeText.setFont(loadCustomFont(fonts.DM_SANS_ITALIC, 16f));
        welcomeText.setForeground(themeManager.getGreen());

        JLabel heading = new JLabel("<html>Your smart companion for navigating Davao<br>City's Jeepney routes.</html>");
        heading.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 22f));
        heading.setForeground(themeManager.getForegroundColor());

        JLabel bullet = new JLabel("<html>✅ Complete jeepney route database<br><br>" +
                "✅ Step-by-step travel instructions.<br><br>" +
                "✅ Easy-to-use interface for all commuters</html>");
        bullet.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        bullet.setForeground(themeManager.getForegroundColor());

        startButton = createStartButton();

        infoTextPanel.add(welcomeText);
        infoTextPanel.add(Box.createVerticalStrut(sizeManager.SPACING_MEDIUM));
        infoTextPanel.add(heading);
        infoTextPanel.add(Box.createVerticalStrut(sizeManager.SPACING_SMALL));
        infoTextPanel.add(bullet);
        infoTextPanel.add(Box.createVerticalStrut(sizeManager.SPACING_MEDIUM));
        infoTextPanel.add(startButton);

        return infoTextPanel;
    }

    private roundButton createStartButton() throws IOException, FontFormatException {
        startButton = new roundButton("Start Now!");
        startButton.setArc(20, 20);
        startButton.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        startButton.setBackground(themeManager.getRed());
        startButton.setForeground(themeManager.getPink());
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        startButton.putClientProperty("themeColor", "red");

        startButton.addActionListener(e -> cardChanger.accept("MAIN"));

        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                startButton.setBackground(themeManager.getWhite());
                startButton.setForeground(themeManager.getBlack());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                startButton.setBackground(themeManager.getRed());
                startButton.setForeground(themeManager.getPink());
            }
        });
        return startButton;
    }

    private JLabel createMockUpLabel() throws IOException {
        return createImageLabel(Images.MOCKUP, 475, 475);
    }

    private JLabel createHowToUseLabel() throws IOException, FontFormatException {
        JLabel howText = new JLabel("How to use?");
        howText.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 18f));
        howText.setAlignmentX(Component.CENTER_ALIGNMENT);
        howText.setForeground(themeManager.getForegroundColor());
        return howText;
    }

    private JPanel createInstructionContainer() throws IOException, FontFormatException {
        JPanel instructContainer = new JPanel();
        instructContainer.setBackground(themeManager.getBackgroundColor());
        instructContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        instructContainer.setPreferredSize(new Dimension(1000, 350));
        instructContainer.add(createStepPanel1());
        instructContainer.add(createStepPanel2());
        instructContainer.add(createStepPanel3());
        return instructContainer;
    }

    private roundPanel createStepPanel1() throws IOException, FontFormatException {
        step1Panel = new roundPanel(sizeManager.BORDER_RADIUS_LARGE);
        step1Panel.setLayout(new BoxLayout(step1Panel, BoxLayout.Y_AXIS));
        step1Panel.setPreferredSize(new Dimension(375, 350));
        step1Panel.setBackground(themeManager.getBlue());
        step1Panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        step1Panel.putClientProperty("themeColor", "blue");

        JLabel heading = new JLabel("Step 1");
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 18f));
        heading.setForeground(themeManager.getForegroundColor());

        roundPanel panel1 = new roundPanel(sizeManager.BORDER_RADIUS_LARGE);
        panel1.setPreferredSize(new Dimension(30, 20));
        panel1.setMaximumSize(new Dimension(300, 50));
        panel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel1.setBackground(themeManager.getComponentsColor());
        JLabel label1 = new JLabel("Enter your current location.");
        label1.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        label1.setForeground(themeManager.getForegroundColor());
        panel1.add(label1);

        roundPanel panel2 = new roundPanel(sizeManager.BORDER_RADIUS_LARGE);
        panel2.setPreferredSize(new Dimension(30, 20));
        panel2.setMaximumSize(new Dimension(300, 50));
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel2.setBackground(themeManager.getComponentsColor());
        JLabel label2 = new JLabel("Enter your destination");
        label2.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        label2.setForeground(themeManager.getForegroundColor());
        panel2.add(label2);

        roundButton button = new roundButton("Then press search!");
        button.setArc(30, 30);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(30, 20));
        button.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        button.setBackground(themeManager.getRed());
        button.setForeground(themeManager.getPink());
        button.putClientProperty("themeColor", "red");

        step1Panel.add(Box.createVerticalStrut(sizeManager.SPACING_LARGE));
        step1Panel.add(heading);
        step1Panel.add(Box.createVerticalStrut(sizeManager.SPACING_MEDIUM));
        step1Panel.add(panel1);
        step1Panel.add(Box.createVerticalStrut(sizeManager.SPACING_MEDIUM));
        step1Panel.add(panel2);
        step1Panel.add(Box.createVerticalStrut(sizeManager.SPACING_MEDIUM));
        step1Panel.add(button);

        return step1Panel;
    }

    private roundPanel createStepPanel2() throws IOException, FontFormatException {
        step2Panel = new roundPanel(sizeManager.BORDER_RADIUS_LARGE);
        step2Panel.setLayout(new BoxLayout(step2Panel, BoxLayout.Y_AXIS));
        step2Panel.setPreferredSize(new Dimension(375, 350));
        step2Panel.setBackground(themeManager.getYellow());
        step2Panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        step2Panel.putClientProperty("themeColor", "yellow");

        JLabel heading = new JLabel("Step 2");
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 18f));
        heading.setForeground(themeManager.getForegroundColor());

        roundPanel panel = new roundPanel(sizeManager.BORDER_RADIUS_LARGE);
        panel.setPreferredSize(new Dimension(30, 20));
        panel.setMaximumSize(new Dimension(300, 50));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setBackground(themeManager.getComponentsColor());
        JLabel label = new JLabel("See the routes pop up!");
        label.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        label.setForeground(themeManager.getForegroundColor());
        panel.add(label);

        JLabel routeLabel = createImageLabel(Images.ROUTE_IMAGE, 400, 400);
        routeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        step2Panel.add(Box.createVerticalStrut(sizeManager.SPACING_LARGE));
        step2Panel.add(heading);
        step2Panel.add(Box.createVerticalStrut(sizeManager.SPACING_MEDIUM));
        step2Panel.add(panel);
        step2Panel.add(routeLabel);

        return step2Panel;
    }

    private roundPanel createStepPanel3() throws IOException, FontFormatException {
        step3Panel = new roundPanel(sizeManager.BORDER_RADIUS_LARGE);
        step3Panel.setLayout(new BoxLayout(step3Panel, BoxLayout.Y_AXIS));
        step3Panel.setPreferredSize(new Dimension(375, 350));
        step3Panel.setBackground(themeManager.getBlue());
        step3Panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        step3Panel.putClientProperty("themeColor", "blue");

        JLabel heading = new JLabel("Step 3");
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 18f));
        heading.setForeground(themeManager.getForegroundColor());

        roundPanel panel = new roundPanel(sizeManager.BORDER_RADIUS_LARGE);
        panel.setPreferredSize(new Dimension(30, 20));
        panel.setMaximumSize(new Dimension(300, 75));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setBackground(themeManager.getComponentsColor());
        JLabel label = new JLabel("<html>Click a specific route for a<br>detailed overview of the<br>stops, transfers and more.</html>");
        label.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        label.setForeground(themeManager.getForegroundColor());
        panel.add(label);

        JLabel routeLabel = createImageLabel(Images.ROUTE_OVERVIEW, 400, 400);
        routeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        step3Panel.add(Box.createVerticalStrut(sizeManager.SPACING_LARGE));
        step3Panel.add(heading);
        step3Panel.add(Box.createVerticalStrut(sizeManager.SPACING_MEDIUM));
        step3Panel.add(panel);
        step3Panel.add(Box.createVerticalStrut(sizeManager.SPACING_SMALL));
        step3Panel.add(routeLabel);

        return step3Panel;
    }

    private static Font loadCustomFont(String fontPath, float size) throws IOException, FontFormatException {
        Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);
        return font.deriveFont(size);
    }

    @Override
    public void onThemeChange(boolean isDarkMode) {
        setBackground(themeManager.getBackgroundColor());
        SwingUtilities.invokeLater(() -> {
            repaint();
        });
    }

    public void dispose() {
        if (themeManager != null) {
            themeManager.removeThemeChangeListener(this);
        }
        if (timer != null) {
            timer.stop();
        }
    }
}