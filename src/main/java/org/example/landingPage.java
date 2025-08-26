package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class landingPage extends JFrame {
    private Image jeepney;
    private Timer timer;
    private int xVelocity = 1;
    private int x = 0;
    private int y = 85;
    private boolean isPaused = false;
    private final int pauseDuration = 2000;

    public landingPage() throws IOException, FontFormatException {
        setTitle("Para!");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel headerPanel = createHeaderPanel();
        JPanel centerPanel = createCenterPanel();
        JPanel footerPanel = createFooterPanel();

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }


    private JPanel createHeaderPanel() throws IOException, FontFormatException {
        Image backgroundImage = loadAndScaleImage("ProjectFiles/City.png", 1920, 160);

        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2D = (Graphics2D) g;

                if (backgroundImage != null) {
                    g2D.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
                }

                if (jeepney != null) {
                    g2D.drawImage(jeepney, x, y, null);
                }
            }
        };
        header.setPreferredSize(new Dimension(1920, 160));
        header.setOpaque(false);

        header.setLayout(new FlowLayout(FlowLayout.CENTER, 0,0));

        jeepney = loadAndScaleImage("ProjectFiles/jeep.png", 100, 100);
        setupJeepneyAnimation(header);

        ImageIcon logoIcon = new ImageIcon(loadAndScaleImage("ProjectFiles/Para.png", 175, 175));
        header.add(new JLabel(logoIcon, SwingConstants.CENTER));

        return header;
    }

    private void setupJeepneyAnimation(JPanel header) {
        timer = new Timer(20, e -> {
            if (!isPaused) {
                x += xVelocity;
                if (Math.abs(x - (header.getWidth() / 2 - jeepney.getWidth(null) / 2)) < 5) {
                    isPaused = true;
                    x = header.getWidth() / 2 - jeepney.getWidth(null) / 2;
                    new Timer(pauseDuration, evt -> isPaused = false).start();
                }
                if (x >= header.getWidth()) {
                    x = 0;
                }
            }
            header.repaint();
        });
        timer.start();
    }

    private JPanel createCenterPanel() throws IOException, FontFormatException {
        JPanel center = new JPanel();
        JPanel contentPane = new JPanel();
        contentPane.setPreferredSize(new Dimension(1200, 800));
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        contentPane.add(createInfoContainer());
        contentPane.add(Box.createVerticalStrut(10));
        contentPane.add(createHowToUseLabel());
        contentPane.add(Box.createVerticalStrut(10));
        contentPane.add(createInstructionContainer());

        center.add(contentPane);
        return center;
    }

    private JPanel createInfoContainer() throws IOException, FontFormatException {
        roundPanel infoContainer = new roundPanel(30);
        infoContainer.setPreferredSize(new Dimension(1200, 300));
        infoContainer.setBackground(new Color(0xffe786));
        infoContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 100, -75));

        infoContainer.add(createInfoTextPanel());
        infoContainer.add(createMockUpLabel());

        return infoContainer;
    }

    private JPanel createInfoTextPanel() throws IOException, FontFormatException {
        JPanel infoText = new JPanel();
        infoText.setBackground(new Color(0xffe786));
        infoText.setLayout(new BoxLayout(infoText, BoxLayout.Y_AXIS));

        JLabel welcomeText = new JLabel("Welcome to Para!");
        welcomeText.setFont(loadCustomFont("ProjectFiles/DMSansItalic.ttf", 16f));
        welcomeText.setForeground(new Color(0x84b477));

        JLabel heading = new JLabel("<html>Your smart companion for navigating Davao<br>City's Jeepney routes.</html>");
        heading.setFont(loadCustomFont("ProjectFiles/DMSansBold.ttf", 22f));

        JLabel bullet = new JLabel("<html>✅ Complete jeepney route database<br><br>" +
                "✅ Step-by-step travel instructions.<br><br>" +
                "✅ Easy-to-use interface for all commuters</html>");
        bullet.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 16f));

        roundButton startButton = createStartButton();
        infoText.add(welcomeText);
        infoText.add(Box.createVerticalStrut(15));
        infoText.add(heading);
        infoText.add(Box.createVerticalStrut(10));
        infoText.add(bullet);
        infoText.add(Box.createVerticalStrut(20));
        infoText.add(startButton);

        return infoText;
    }

    private roundButton createStartButton() throws IOException, FontFormatException {
        roundButton start = new roundButton("Start Now!");
        start.setArc(20, 20);
        start.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 16f));
        start.setBackground(new Color(0xd85259));
        start.setForeground(new Color(0xe8ced6));
        start.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        start.addActionListener(e -> {
            try {
                new mainPage().setVisible(true);
                dispose(); // Close the landing page
            } catch (IOException | FontFormatException ex) {
                throw new RuntimeException(ex);
            }
        });
        start.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                start.setBackground(Color.WHITE);
                start.setForeground(Color.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                start.setBackground(new Color(0xd85259));
                start.setForeground(new Color(0xe8ced6));
            }
        });
        return start;
    }

    private JLabel createMockUpLabel() throws IOException {
        ImageIcon mockUpIcon = new ImageIcon(loadAndScaleImage("ProjectFiles/MockUp.png", 475, 475));
        return new JLabel(mockUpIcon, SwingConstants.CENTER);
    }

    private JLabel createHowToUseLabel() throws IOException, FontFormatException {
        JLabel howText = new JLabel("How to use?");
        howText.setFont(loadCustomFont("ProjectFiles/DMSansBold.ttf", 18f));
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
        roundPanel step1 = new roundPanel(30);
        step1.setLayout(new BoxLayout(step1, BoxLayout.Y_AXIS));
        step1.setPreferredSize(new Dimension(375, 350));
        step1.setBackground(new Color(0xb7caef));
        step1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading = new JLabel("Step 1");
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setFont(loadCustomFont("ProjectFiles/DMSansBold.ttf", 18f));

        roundPanel panel1 = new roundPanel(30);
        panel1.setPreferredSize(new Dimension(30, 20));
        panel1.setMaximumSize(new Dimension(300, 50));
        panel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel label1 = new JLabel("Enter your current location.");
        label1.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 16f));
        panel1.add(label1);

        roundPanel panel2 = new roundPanel(30);
        panel2.setPreferredSize(new Dimension(30, 20));
        panel2.setMaximumSize(new Dimension(300, 50));
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel label2 = new JLabel("Enter your destination");
        label2.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 16f));
        panel2.add(label2);

        JButton button = new JButton("Then press search!");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(30, 20));
        button.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 16f));
        button.setBackground(new Color(0xd85259));
        button.setForeground(new Color(0xe8ced6));

        step1.add(Box.createVerticalStrut(30));
        step1.add(heading);
        step1.add(Box.createVerticalStrut(20));
        step1.add(panel1);
        step1.add(Box.createVerticalStrut(15));
        step1.add(panel2);
        step1.add(Box.createVerticalStrut(20));
        step1.add(button);

        return step1;
    }

    private roundPanel createStepPanel2() throws IOException, FontFormatException {
        roundPanel step2 = new roundPanel(30);
        step2.setLayout(new BoxLayout(step2, BoxLayout.Y_AXIS));
        step2.setPreferredSize(new Dimension(375, 350));
        step2.setBackground(new Color(0xffe786));
        step2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading = new JLabel("Step 2");
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setFont(loadCustomFont("ProjectFiles/DMSansBold.ttf", 18f));

        roundPanel panel = new roundPanel(30);
        panel.setPreferredSize(new Dimension(30, 20));
        panel.setMaximumSize(new Dimension(300, 50));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel label = new JLabel("See the routes pop up!");
        label.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 16f));
        panel.add(label);

        ImageIcon routeIcon = new ImageIcon(loadAndScaleImage("ProjectFiles/RouteImg.png", 400, 400));
        JLabel routeLabel = new JLabel(routeIcon, SwingConstants.CENTER);
        routeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        step2.add(Box.createVerticalStrut(30));
        step2.add(heading);
        step2.add(Box.createVerticalStrut(20));
        step2.add(panel);
        step2.add(routeLabel);

        return step2;
    }

    private roundPanel createStepPanel3() throws IOException, FontFormatException {
        roundPanel step3 = new roundPanel(30);
        step3.setLayout(new BoxLayout(step3, BoxLayout.Y_AXIS));
        step3.setPreferredSize(new Dimension(375, 350));
        step3.setBackground(new Color(0xb7caef));
        step3.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading = new JLabel("Step 3");
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setFont(loadCustomFont("ProjectFiles/DMSansBold.ttf", 18f));

        roundPanel panel = new roundPanel(30);
        panel.setPreferredSize(new Dimension(30, 20));
        panel.setMaximumSize(new Dimension(300, 75));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel label = new JLabel("<html>Click a specific route for a<br>detailed overview of the<br>stops, transfers and more.</html>");
        label.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 16f));
        panel.add(label);

        step3.add(Box.createVerticalStrut(30));
        step3.add(heading);
        step3.add(Box.createVerticalStrut(20));
        step3.add(panel);

        return step3;
    }

    private JPanel createFooterPanel() throws IOException, FontFormatException {
        JPanel footer = new JPanel();
        footer.setPreferredSize(new Dimension(0, 50));
        footer.setBackground(new Color(0xd85259));
        footer.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 10));

        JButton about = new JButton("About Us");
        about.setPreferredSize(new Dimension(100, 40));
        about.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 14f));
        about.setBorder(BorderFactory.createEmptyBorder());
        about.setOpaque(false);
        about.setContentAreaFilled(false);
        about.setBorderPainted(false);
        about.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                about.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                about.setForeground(Color.BLACK);
            }
        });

        JButton faq = new JButton("FAQ");
        faq.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 14f));
        faq.setOpaque(false);
        faq.setContentAreaFilled(false);
        faq.setBorderPainted(false);
        faq.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                faq.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                faq.setForeground(Color.BLACK);
            }
        });

        JLabel copyright = new JLabel("© 2025 Para! - All Rights Reserved");
        copyright.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 12f));

        footer.add(copyright);
        footer.add(faq);
        footer.add(about);
        return footer;
    }

    private Image loadAndScaleImage(String path, int width, int height) throws IOException {
        ImageIcon icon = new ImageIcon(path);
        return icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    private static Font loadCustomFont(String fontPath, float size) throws IOException, FontFormatException {
        Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);
        return font.deriveFont(size);
    }
}