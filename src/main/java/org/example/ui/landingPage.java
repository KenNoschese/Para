package org.example.ui;

import org.example.config.AnimationConfig;
import org.example.components.*;
import org.example.config.appTheme;
import org.example.resources.Images;
import org.example.resources.fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class landingPage extends JFrame {
    private Image jeepney;
    private Timer timer;
    AnimationConfig config = new AnimationConfig();
    private int x = 0;
    private boolean isPaused = false;

    public landingPage() throws IOException, FontFormatException {
        setTitle("Para!");
        setSize(appTheme.WINDOW_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel headerPanel = createHeaderPanel();
        JPanel centerPanel = createCenterPanel();
        FooterPanel footerPanel = new FooterPanel();

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
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
        header.setPreferredSize(new Dimension(appTheme.HEADER_SIZE));
        header.setOpaque(false);

        header.setLayout(new FlowLayout(FlowLayout.CENTER, 0,0));

        jeepney = createScaledImage(Images.JEEP, config.jeepneyWidth, config.jeepneyHeight);
        setupJeepneyAnimation(header);

        header.add(createImageLabel(Images.PARA_LOGO, 175, 175));

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
        JPanel contentPane = new JPanel();
        contentPane.setPreferredSize(new Dimension(appTheme.CONTENTPANE_SIZE));
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        contentPane.add(createInfoContainer());
        contentPane.add(Box.createVerticalStrut(appTheme.SPACING_SMALL));
        contentPane.add(createHowToUseLabel());
        contentPane.add(Box.createVerticalStrut(appTheme.SPACING_SMALL));
        contentPane.add(createInstructionContainer());

        center.add(contentPane);
        return center;
    }

    private JPanel createInfoContainer() throws IOException, FontFormatException {
        roundPanel infoContainer = new roundPanel(appTheme.BORDER_RADIUS_LARGE);
        infoContainer.setPreferredSize(new Dimension(appTheme.INFOCONTAINER_SIZE));
        infoContainer.setBackground(appTheme.YELLOW);
        infoContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 100, -75));

        infoContainer.add(createInfoTextPanel());
        infoContainer.add(createMockUpLabel());

        return infoContainer;
    }

    private JPanel createInfoTextPanel() throws IOException, FontFormatException {
        JPanel infoText = new JPanel();
        infoText.setBackground(appTheme.YELLOW);
        infoText.setLayout(new BoxLayout(infoText, BoxLayout.Y_AXIS));

        JLabel welcomeText = new JLabel("Welcome to Para!");
        welcomeText.setFont(loadCustomFont(fonts.DM_SANS_ITALIC, 16f));
        welcomeText.setForeground(appTheme.GREEN);

        JLabel heading = new JLabel("<html>Your smart companion for navigating Davao<br>City's Jeepney routes.</html>");
        heading.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 22f));

        JLabel bullet = new JLabel("<html>✅ Complete jeepney route database<br><br>" +
                "✅ Step-by-step travel instructions.<br><br>" +
                "✅ Easy-to-use interface for all commuters</html>");
        bullet.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 16f));

        roundButton startButton = createStartButton();
        infoText.add(welcomeText);
        infoText.add(Box.createVerticalStrut(appTheme.SPACING_MEDIUM));
        infoText.add(heading);
        infoText.add(Box.createVerticalStrut(appTheme.SPACING_SMALL));
        infoText.add(bullet);
        infoText.add(Box.createVerticalStrut(appTheme.SPACING_MEDIUM));
        infoText.add(startButton);

        return infoText;
    }

    private roundButton createStartButton() throws IOException, FontFormatException {
        roundButton start = new roundButton("Start Now!");
        start.setArc(20, 20);
        start.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        start.setBackground(appTheme.RED);
        start.setForeground(appTheme.PINK);
        start.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        start.addActionListener(e -> {
            try {
                new mainPage().setVisible(true);
                dispose();
            } catch (IOException | FontFormatException ex) {
                throw new RuntimeException(ex);
            }
        });
        start.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                start.setBackground(appTheme.WHITE);
                start.setForeground(appTheme.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                start.setBackground(appTheme.RED);
                start.setForeground(appTheme.PINK);
            }
        });
        return start;
    }

    private JLabel createMockUpLabel() throws IOException {
        return createImageLabel(Images.MOCKUP, 475, 475);
    }

    private JLabel createHowToUseLabel() throws IOException, FontFormatException {
        JLabel howText = new JLabel("How to use?");
        howText.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 18f));
        howText.setAlignmentX(Component.CENTER_ALIGNMENT);
        return howText;
    }

    private JPanel createInstructionContainer() throws IOException, FontFormatException {
        JPanel instructContainer = new JPanel();
        instructContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        instructContainer.setPreferredSize(new Dimension(1000, 350));
        instructContainer.add(createStepPanel1());
        instructContainer.add(createStepPanel2());
        instructContainer.add(createStepPanel3());
        return instructContainer;
    }

    private roundPanel createStepPanel1() throws IOException, FontFormatException {
        roundPanel step1 = new roundPanel(appTheme.BORDER_RADIUS_LARGE);
        step1.setLayout(new BoxLayout(step1, BoxLayout.Y_AXIS));
        step1.setPreferredSize(new Dimension(375, 350));
        step1.setBackground(appTheme.BLUE);
        step1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading = new JLabel("Step 1");
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 18f));

        roundPanel panel1 = new roundPanel(appTheme.BORDER_RADIUS_LARGE);
        panel1.setPreferredSize(new Dimension(30, 20));
        panel1.setMaximumSize(new Dimension(300, 50));
        panel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel label1 = new JLabel("Enter your current location.");
        label1.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        panel1.add(label1);

        roundPanel panel2 = new roundPanel(appTheme.BORDER_RADIUS_LARGE);
        panel2.setPreferredSize(new Dimension(30, 20));
        panel2.setMaximumSize(new Dimension(300, 50));
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel label2 = new JLabel("Enter your destination");
        label2.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        panel2.add(label2);

        roundButton button = new roundButton("Then press search!");
        button.setArc(30,30);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(30, 20));
        button.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        button.setBackground(appTheme.RED);
        button.setForeground(appTheme.PINK);

        step1.add(Box.createVerticalStrut(appTheme.SPACING_LARGE));
        step1.add(heading);
        step1.add(Box.createVerticalStrut(appTheme.SPACING_MEDIUM));
        step1.add(panel1);
        step1.add(Box.createVerticalStrut(appTheme.SPACING_MEDIUM));
        step1.add(panel2);
        step1.add(Box.createVerticalStrut(appTheme.SPACING_MEDIUM));
        step1.add(button);

        return step1;
    }

    private roundPanel createStepPanel2() throws IOException, FontFormatException {
        roundPanel step2 = new roundPanel(appTheme.BORDER_RADIUS_LARGE);
        step2.setLayout(new BoxLayout(step2, BoxLayout.Y_AXIS));
        step2.setPreferredSize(new Dimension(375, 350));
        step2.setBackground(appTheme.YELLOW);
        step2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading = new JLabel("Step 2");
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 18f));

        roundPanel panel = new roundPanel(appTheme.BORDER_RADIUS_LARGE);
        panel.setPreferredSize(new Dimension(30, 20));
        panel.setMaximumSize(new Dimension(300, 50));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel label = new JLabel("See the routes pop up!");
        label.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        panel.add(label);

        JLabel routeLabel = createImageLabel(Images.ROUTE_IMAGE, 400, 400);
        routeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        step2.add(Box.createVerticalStrut(appTheme.SPACING_LARGE));
        step2.add(heading);
        step2.add(Box.createVerticalStrut(appTheme.SPACING_MEDIUM));
        step2.add(panel);
        step2.add(routeLabel);

        return step2;
    }

    private roundPanel createStepPanel3() throws IOException, FontFormatException {
        roundPanel step3 = new roundPanel(appTheme.BORDER_RADIUS_LARGE);
        step3.setLayout(new BoxLayout(step3, BoxLayout.Y_AXIS));
        step3.setPreferredSize(new Dimension(375, 350));
        step3.setBackground(appTheme.BLUE);
        step3.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading = new JLabel("Step 3");
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 18f));

        roundPanel panel = new roundPanel(appTheme.BORDER_RADIUS_LARGE);
        panel.setPreferredSize(new Dimension(30, 20));
        panel.setMaximumSize(new Dimension(300, 75));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel label = new JLabel("<html>Click a specific route for a<br>detailed overview of the<br>stops, transfers and more.</html>");
        label.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 16f));
        panel.add(label);

        JLabel routeLabel = createImageLabel(Images.ROUTE_OVERVIEW, 400, 400);
        routeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        step3.add(Box.createVerticalStrut(appTheme.SPACING_LARGE));
        step3.add(heading);
        step3.add(Box.createVerticalStrut(appTheme.SPACING_MEDIUM));
        step3.add(panel);
        step3.add(Box.createVerticalStrut(appTheme.SPACING_SMALL));
        step3.add(routeLabel);

        return step3;
    }

    private static Font loadCustomFont(String fontPath, float size) throws IOException, FontFormatException {
        Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);
        return font.deriveFont(size);
    }
}