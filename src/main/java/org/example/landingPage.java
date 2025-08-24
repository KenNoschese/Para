package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;


public class landingPage extends JFrame {
    public landingPage() throws IOException, FontFormatException {
        setTitle("Para! Jeepney Route System");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setLayout(new BorderLayout());

        JPanel head = new JPanel();
        head.setPreferredSize(new Dimension(1181, 160));
        head.setOpaque(false);

        ImageIcon img = new ImageIcon("ProjectFiles/Para.png");
        Image resized = img.getImage().getScaledInstance(175, 175, Image.SCALE_SMOOTH);
        ImageIcon logo = new ImageIcon(resized);
        JLabel logoLabel = new JLabel(logo, SwingConstants.CENTER);

        head.add(logoLabel, BorderLayout.CENTER);

        JPanel center = new JPanel();

        JPanel contentPane = new JPanel();
        contentPane.setPreferredSize(new Dimension(1200, 800));
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        roundPanel infoContainer = new roundPanel(30);
        infoContainer.setPreferredSize(new Dimension(1200, 300));
        infoContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 100, -75));
        infoContainer.setBackground(new Color(0xffe786));

        JPanel infoText = new JPanel();
        infoText.setBackground(new Color(0xffe786));
        infoText.setLayout(new BoxLayout(infoText, BoxLayout.Y_AXIS));

        JLabel wcText = new JLabel("Welcome to Para!");
        wcText.setFont(loadCustomFont("ProjectFiles/DMSansItalic.ttf", 16f));
        wcText.setForeground(new Color(0x84b477));

        JLabel heading = new JLabel("<html>Your smart companion for navigating Davao<br>City's Jeepney routes.</html>");
        heading.setFont(loadCustomFont("ProjectFiles/DMSansBold.ttf", 22f));

        JLabel bullet = new JLabel();
        bullet.setText("<html>✅ Complete jeepney route database<br><br>" +
                "✅ Step-by-step travel instructions.<br><br>" +
                "✅ Easy-to-use interface for all commuters</html>");
        bullet.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 16f));

        JButton start = new JButton("Start Now!");
        start.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 16f));
        start.setBackground(new Color(0xd85259));
        start.setForeground(new Color(0xe8ced6));
        start.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainPage landingFrame = null;
                try {
                    landingFrame = new mainPage();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (FontFormatException ex) {
                    throw new RuntimeException(ex);
                }
                landingFrame.setVisible(true);
            }
        });

        ImageIcon image = new ImageIcon("ProjectFiles/MockUp.png");
        Image scaled = image.getImage().getScaledInstance(475, 475, Image.SCALE_SMOOTH);
        ImageIcon Img = new ImageIcon(scaled);
        JLabel mockUp = new JLabel(Img, SwingConstants.CENTER);

        infoText.add(wcText);
        infoText.add(Box.createVerticalStrut(15));
        infoText.add(heading);
        infoText.add(Box.createVerticalStrut(10));
        infoText.add(bullet);
        infoText.add(Box.createVerticalStrut(20));
        infoText.add(start);

        infoContainer.add(infoText);
        infoContainer.add(mockUp);

        JLabel howText = new JLabel("How to use?");
        howText.setFont(loadCustomFont("ProjectFiles/DMSansBold.ttf", 18f));
        howText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel instructContainer = new JPanel();
        instructContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        instructContainer.setPreferredSize(new Dimension(1000, 350));

        roundPanel step1 = new roundPanel(30);
        step1.setLayout(new BoxLayout(step1, BoxLayout.Y_AXIS));
        step1.setPreferredSize(new Dimension(375,350));
        step1.setBackground(new Color(0xb7caef));
        step1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading1 = new JLabel("Step 1");
        heading1.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading1.setFont(loadCustomFont("ProjectFiles/DMSansBold.ttf", 18f));

        roundPanel panel1 = new roundPanel(30);
        panel1.setPreferredSize(new Dimension(30, 20));
        panel1.setMaximumSize(new Dimension(300, 50));
        panel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel label1 = new JLabel("Enter your current location.");
        label1.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 16f));

        roundPanel panel11 = new roundPanel(30);
        panel11.setPreferredSize(new Dimension(30, 20));
        panel11.setMaximumSize(new Dimension(300, 50));
        panel11.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel label11 = new JLabel("Enter your destination");
        label11.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 16f));

        JButton button1 = new JButton("Then press search!");
        button1.setAlignmentX(Component.CENTER_ALIGNMENT);
        button1.setPreferredSize(new Dimension(30, 20));
        button1.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 16f));
        button1.setBackground(new Color(0xd85259));
        button1.setForeground(new Color(0xe8ced6));

        panel1.add(label1);
        panel11.add(label11);

        step1.add(Box.createVerticalStrut(30));
        step1.add(heading1);
        step1.add(Box.createVerticalStrut(20));
        step1.add(panel1);
        step1.add(Box.createVerticalStrut(15));
        step1.add(panel11);
        step1.add(Box.createVerticalStrut(20));
        step1.add(button1);

        roundPanel step2 = new roundPanel(30);
        step2.setLayout(new BoxLayout(step2, BoxLayout.Y_AXIS));
        step2.setPreferredSize(new Dimension(375,350));
        step2.setBackground(new Color(0xffe786));
        step2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading2 = new JLabel("Step 2");
        heading2.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading2.setFont(loadCustomFont("ProjectFiles/DMSansBold.ttf", 18f));

        roundPanel panel2 = new roundPanel(30);
        panel2.setPreferredSize(new Dimension(30, 20));
        panel2.setMaximumSize(new Dimension(300, 50));
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel label2 = new JLabel("See the routes pop up!");
        label2.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 16f));

        ImageIcon routeimg = new ImageIcon("ProjectFiles/RouteImg.png");
        Image sized = routeimg.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        ImageIcon rimg = new ImageIcon(sized);
        JLabel rImg = new JLabel(rimg, SwingConstants.CENTER);
        rImg.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel2.add(label2);
        step2.add(Box.createVerticalStrut(30));
        step2.add(heading2);
        step2.add(Box.createVerticalStrut(20));
        step2.add(panel2);
        step2.add(rImg);

        roundPanel step3 = new roundPanel(30);
        step3.setLayout(new BoxLayout(step3, BoxLayout.Y_AXIS));
        step3.setPreferredSize(new Dimension(375,350));
        step3.setBackground(new Color(0xb7caef));
        step1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading3 = new JLabel("Step 3");
        heading3.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading3.setFont(loadCustomFont("ProjectFiles/DMSansBold.ttf", 18f));

        roundPanel panel33 = new roundPanel(30);
        panel33.setPreferredSize(new Dimension(30, 20));
        panel33.setMaximumSize(new Dimension(300, 75));
        panel33.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel label33 = new JLabel();
        label33.setText("<html>Click a specific route for a<br>detailed overview of the<br>stops, transfers and more.</html>");
        label33.setFont(loadCustomFont("ProjectFiles/DMSans.ttf", 16f));

        panel33.add(label33);

        step3.add(Box.createVerticalStrut(30));
        step3.add(heading3);
        step3.add(Box.createVerticalStrut(20));
        step3.add(panel33);

        instructContainer.add(step1);
        instructContainer.add(step2);
        instructContainer.add(step3);

        contentPane.add(infoContainer);
        contentPane.add(Box.createVerticalStrut(30));
        contentPane.add(howText);
        contentPane.add(Box.createVerticalStrut(30));
        contentPane.add(instructContainer);

        center.add(contentPane);
        add(head, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        setVisible(true);
    }

    private static Font loadCustomFont(String fontPath, float size) throws IOException, FontFormatException {
        Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);
        return font.deriveFont(size);
    }


}
