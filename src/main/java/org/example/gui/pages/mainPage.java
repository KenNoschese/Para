package org.example.gui.pages;

import org.example.gui.components.*;
import org.example.gui.resources.RouteData;
import org.example.gui.config.RouteManager;
import org.example.gui.config.appTheme;
import org.example.gui.resources.Images;
import org.example.gui.resources.fonts;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class mainPage extends JPanel {
    private Consumer<String> cardChanger;
    private JPanel routeContainer;
    private roundTextField currentLocation;
    private roundTextField destination;
    private RouteManager routeManager;

    public mainPage(Consumer<String> cardChanger) throws IOException, FontFormatException {
        this.cardChanger = cardChanger;
        setupPanel();
    }

    private void setupPanel() throws IOException, FontFormatException {
        this.routeManager = new RouteManager();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1920, 1080));

        add(createHeader(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);

        FooterPanel footerPanel = new FooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeader() throws IOException {
        JPanel header = new JPanel();
        header.setPreferredSize(appTheme.HEADER_SIZE);
        header.setOpaque(false);
        header.setLayout(new BorderLayout());

        ImageIcon logo = createScaledIcon(Images.PARA_LOGO, 175, 175);
        JLabel logoLabel = new JLabel(logo, SwingConstants.CENTER);
        header.add(logoLabel, BorderLayout.CENTER);
        return header;
    }

    private JPanel createCenterPanel() throws IOException, FontFormatException {
        JPanel center = new JPanel();
        center.setLayout(new FlowLayout(FlowLayout.CENTER, 150, 0));

        JPanel contentPane = new JPanel();
        contentPane.setPreferredSize(new Dimension(1920, 800));
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS)); // Use BoxLayout X_AXIS

        contentPane.add(Box.createHorizontalStrut(100));
        contentPane.add(createLeftJPanel());
        contentPane.add(Box.createHorizontalStrut(100));
        contentPane.add(createRightPanel());
        contentPane.add(Box.createHorizontalStrut(100));

        center.add(contentPane);
        return center;
    }

    private JPanel createRightPanel() throws IOException, FontFormatException {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(450, 1080));
        rightPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightPanel.add(createInfoLabel());
        rightPanel.add(Box.createVerticalStrut(appTheme.SPACING_SMALL));
        rightPanel.add(createInfoPanel());
        rightPanel.add(Box.createVerticalStrut(appTheme.SPACING_SMALL));
        rightPanel.add(createSavedLabel());
        rightPanel.add(Box.createVerticalStrut(appTheme.SPACING_SMALL));
        rightPanel.add(createSavedPanel());
        rightPanel.add(Box.createVerticalStrut(appTheme.SPACING_SMALL));
        rightPanel.add(createRecentLabel());
        rightPanel.add(Box.createVerticalStrut(appTheme.SPACING_SMALL));
        rightPanel.add(createRecentPanel());
        return rightPanel;
    }

    private JPanel createLeftJPanel() throws IOException, FontFormatException {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(1050, 1080));
        leftPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(createTextContainer());
        leftPanel.add(Box.createVerticalStrut(appTheme.SPACING_SMALL));
        leftPanel.add(createRouteContainer());
        return leftPanel;
    }

    private JPanel createTextContainer() throws IOException, FontFormatException {
        roundPanel textContainer = new roundPanel(appTheme.BORDER_RADIUS_LARGE);
        textContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 150, 75));
        textContainer.setPreferredSize(new Dimension(650, 195));
        textContainer.setBackground(appTheme.YELLOW);
        textContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        textContainer.add(createWelcomeContainer());
        textContainer.add(createInputContainer());
        return textContainer;
    }

    private JPanel createWelcomeContainer() throws IOException, FontFormatException {
        JPanel wcContainer = new JPanel();
        wcContainer.setLayout(new BoxLayout(wcContainer, BoxLayout.Y_AXIS));
        wcContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        wcContainer.setBackground(appTheme.YELLOW);

        JLabel wcText = new JLabel("Welcome Ken!");
        wcText.setFont(loadCustomFont(fonts.DM_SANS_ITALIC, 22f));
        wcText.setForeground(appTheme.GREEN);

        JLabel wcQuestion = new JLabel("<html>Where do you want<br>to go?</html>");
        wcQuestion.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 34f));

        wcContainer.add(wcText);
        wcContainer.add(Box.createVerticalStrut(appTheme.SPACING_MEDIUM));
        wcContainer.add(wcQuestion);
        return wcContainer;
    }

    private JPanel createInputContainer() throws IOException, FontFormatException {
        JPanel inputContainer = new JPanel();
        inputContainer.setLayout(new BoxLayout(inputContainer, BoxLayout.Y_AXIS));
        inputContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputContainer.setBackground(appTheme.YELLOW);

        // Create input fields as instance variables so we can access them
        currentLocation = new roundTextField(26);
        currentLocation.setPreferredSize(new Dimension(250, 40));
        currentLocation.setMaximumSize(new Dimension(250, 40));

        destination = new roundTextField(26);
        destination.setPreferredSize(new Dimension(250, 40));
        destination.setMaximumSize(new Dimension(250, 40));

        roundButton submit = new roundButton("SEARCH ROUTES");
        submit.setPreferredSize(new Dimension(200, 40));
        submit.setMaximumSize(new Dimension(200, 40));
        submit.setBackground(appTheme.GREEN);
        submit.setForeground(appTheme.WHITE);
        submit.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 14));
        submit.setArc(30, 30);
        submit.setAlignmentX(Component.CENTER_ALIGNMENT);

        submit.addActionListener(e -> {
            try {
                searchRoutes();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (FontFormatException ex) {
                throw new RuntimeException(ex);
            }
        });

        currentLocation.addActionListener(e -> {
            try {
                searchRoutes();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (FontFormatException ex) {
                throw new RuntimeException(ex);
            }
        });
        destination.addActionListener(e -> {
            try {
                searchRoutes();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (FontFormatException ex) {
                throw new RuntimeException(ex);
            }
        });

        inputContainer.add(Box.createVerticalStrut(appTheme.SPACING_SMALL));
        inputContainer.add(currentLocation);
        inputContainer.add(Box.createVerticalStrut(appTheme.SPACING_MEDIUM));
        inputContainer.add(destination);
        inputContainer.add(Box.createVerticalStrut(appTheme.SPACING_MEDIUM));
        inputContainer.add(submit);
        inputContainer.add(Box.createVerticalStrut(appTheme.SPACING_SMALL));

        return inputContainer;
    }


    private JLabel createInfoLabel() throws IOException, FontFormatException {
        JLabel label = new JLabel("Route Info");
        label.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 14f));
        label.setForeground(appTheme.BLACK);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private roundPanel createInfoPanel() throws IOException, FontFormatException {
        roundPanel panel = new roundPanel(appTheme.BORDER_RADIUS_LARGE);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 60));
        panel.setPreferredSize(new Dimension(360, 170));
        panel.setBackground(appTheme.BLUE);

        JLabel placeholder = createImageLabel(Images.PLACEHOLDER, 100, 100);
        panel.add(placeholder);

        JLabel label = new JLabel("No chosen route.");
        label.setFont(loadCustomFont(fonts.DM_SANS_ITALIC, 14));
        panel.add(label);
        return panel;
    }

    private JLabel createSavedLabel() throws IOException, FontFormatException {
        JLabel tipLabel = new JLabel("Saved Routes");
        tipLabel.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 14f));
        tipLabel.setForeground(appTheme.BLACK);
        tipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return tipLabel;
    }

    private roundPanel createSavedPanel() throws IOException, FontFormatException {
        roundPanel panel = new roundPanel(appTheme.BORDER_RADIUS_LARGE);
        panel.setPreferredSize(new Dimension(360, 170));
        panel.setBackground(appTheme.BLUE);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 60));
        JLabel placeholder = createImageLabel(Images.PLACEHOLDER, 100, 100);
        panel.add(placeholder);

        JLabel label = new JLabel("No saved Routes.");
        label.setFont(loadCustomFont(fonts.DM_SANS_ITALIC, 14));
        panel.add(label);
        return panel;
    }

    private JLabel createRecentLabel() throws IOException, FontFormatException {
        JLabel recentSearches = new JLabel("Recent Searches");
        recentSearches.setFont(loadCustomFont(fonts.DM_SANS_BOLD, 14f));
        recentSearches.setForeground(appTheme.BLACK);
        recentSearches.setAlignmentX(Component.CENTER_ALIGNMENT);
        return recentSearches;
    }

    private roundPanel createRecentPanel() throws IOException, FontFormatException {
        roundPanel panel = new roundPanel(appTheme.BORDER_RADIUS_LARGE);
        panel.setPreferredSize(new Dimension(360, 170));
        panel.setBackground(appTheme.BLUE);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 60));
        JLabel placeholder = createImageLabel(Images.PLACEHOLDER, 100, 100);
        panel.add(placeholder);

        JLabel label = new JLabel("No recent searches.");
        label.setFont(loadCustomFont(fonts.DM_SANS_ITALIC, 14));
        panel.add(label);
        return panel;
    }

    private JPanel createRouteContainer() {
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        routeContainer = new JPanel();
        routeContainer.setLayout(new BoxLayout(routeContainer, BoxLayout.Y_AXIS));
        routeContainer.setPreferredSize(new Dimension(870, 450));
        routeContainer.setBorder(BorderFactory.createEmptyBorder());

        mainContainer.add(Box.createVerticalStrut(5));
        mainContainer.add(routeContainer);
        return mainContainer;
    }

    private void searchRoutes() throws IOException, FontFormatException {
        String from = currentLocation.getText().trim();
        String to = destination.getText().trim();

        if (from.isEmpty() || to.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both current location and destination.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ArrayList<RouteData> foundRoutes = routeManager.findRoutes(from, to);
        displayRoutes(foundRoutes, from, to);
    }

    private void displayRoutes(ArrayList<RouteData> routes, String from, String to) throws IOException, FontFormatException {
        routeContainer.removeAll();

        if (routes.isEmpty()) {
            JLabel noRoutesLabel = new JLabel("No routes found from " + from + " to " + to);
            noRoutesLabel.setFont(loadCustomFont(fonts.DM_SANS_REGULAR, 14));
            noRoutesLabel.setForeground(appTheme.GRAY);
            noRoutesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            routeContainer.add(Box.createVerticalStrut(50));
            routeContainer.add(noRoutesLabel);
            routeContainer.add(Box.createVerticalStrut(50));
        } else {
            for (int i = 0; i < routes.size(); i++) {
                RoutePanel panel = new RoutePanel(routes.get(i));
                panel.setAlignmentX(Component.CENTER_ALIGNMENT);

                routeContainer.add(panel);
                if (i < routes.size() - 1) {
                    routeContainer.add(Box.createVerticalStrut(appTheme.SPACING_SMALL));
                }
            }
        }

        routeContainer.revalidate();
        routeContainer.repaint();
    }

    public void reloadRoutes() throws IOException {
        routeManager.reloadRoutes();
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

    private Image createScaledImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        return icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("mainPage Preview");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                Consumer<String> dummyCardChanger = System.out::println;

                mainPage panel = new mainPage(dummyCardChanger);

                frame.add(panel);

                frame.setSize(1920, 1080);

                frame.setLocationRelativeTo(null); // Center the frame
                frame.setVisible(true);
            } catch (IOException | FontFormatException e) {
                e.printStackTrace();
            }
        });
    }

}




