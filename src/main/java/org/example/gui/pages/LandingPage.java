package org.example.gui.pages;

import org.example.gui.appManager.ThemeManager;
import org.example.gui.appManager.SizeManager;
import org.example.gui.components.Factories.*;
import org.example.gui.components.base.RoundedButton;
import org.example.gui.components.base.RoundedPanel;
import org.example.gui.components.elements.StepPanel;
import org.example.gui.components.layout.FooterPanel;
import org.example.gui.components.layout.HeaderPanel;
import org.example.gui.resources.Images;
import org.example.gui.resources.Fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class LandingPage extends JPanel implements ThemeManager.ThemeChangeListener {
    private Consumer<String> cardChanger;
    private ThemeManager themeManager;
    private JPanel centerPanel;
    private RoundedPanel infoContainer;
    private JPanel infoTextPanel;
    private RoundedButton startButton;

    public LandingPage(Consumer<String> cardChanger) throws IOException, FontFormatException {
        this.cardChanger = cardChanger;
        this.themeManager = ThemeManager.getInstance();
        this.themeManager.addThemeChangeListener(this);
        setupPanel();
    }

    private void setupPanel() throws IOException, FontFormatException {
        setLayout(new BorderLayout());
        setBackground(themeManager.getBackgroundColor());

        // ⬇️ Use factoryPanel instead of HeaderPanel / FooterPanel
        JPanel headerPanel = HeaderPanel.createHeaderPanel();
        JPanel footerPanel = FooterPanel.createFooterPanel();

        centerPanel = createCenterPanel();

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createCenterPanel() throws IOException, FontFormatException {
        JPanel center = PanelFactory.create(themeManager.getBackgroundColor(), 0, 0, 0);
        JPanel contentPane = PanelFactory.create(themeManager.getBackgroundColor(), 0, 0, 0);
        contentPane.setPreferredSize(SizeManager.getInstance().getContentPaneSize());
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        contentPane.add(createInfoContainer());
        contentPane.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingSmall()));
        contentPane.add(createHowToUseLabel());
        contentPane.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingSmall()));
        contentPane.add(createInstructionContainer());

        center.add(contentPane);
        return center;
    }

    private JPanel createInfoContainer() throws IOException, FontFormatException {
        infoContainer = new RoundedPanel(SizeManager.getInstance().getBorderRadiusLarge());
        infoContainer.setPreferredSize(SizeManager.getInstance().getInfoContainerSize());
        infoContainer.setBackground(themeManager.getYellow());
        infoContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 100, -75));
        infoContainer.putClientProperty("themeColor", "yellow");

        JLabel image = Images.getInstance().getMockupLabel(475, 475);

        infoContainer.add(createInfoTextPanel());
        infoContainer.add(image);

        return infoContainer;
    }

    private JPanel createInfoTextPanel() throws IOException, FontFormatException {
        infoTextPanel = PanelFactory.create(themeManager.getYellow(), 0, 0, 0);
        infoTextPanel.setLayout(new BoxLayout(infoTextPanel, BoxLayout.Y_AXIS));
        infoTextPanel.putClientProperty("themeColor", "yellow");

        JLabel welcomeText = LabelFactory.create("Welcome to Para!",
                loadCustomFont(Fonts.DM_SANS_ITALIC, 16f),
                themeManager.getGreen());

        JLabel heading = new JLabel("<html>Your smart companion for navigating Davao<br>City's Jeepney routes.</html>");
        heading.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 22f));
        heading.setForeground(themeManager.getForegroundColor());
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel bullet = new JLabel("<html>✅ Complete jeepney route database<br><br>" +
                "✅ Step-by-step travel instructions.<br><br>" +
                "✅ Easy-to-use interface for all commuters</html>");
        bullet.setFont(loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        bullet.setForeground(themeManager.getForegroundColor());
        bullet.setAlignmentX(Component.CENTER_ALIGNMENT);

        startButton = createStartButton();

        infoTextPanel.add(welcomeText);
        infoTextPanel.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingMedium()));
        infoTextPanel.add(heading);
        infoTextPanel.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingSmall()));
        infoTextPanel.add(bullet);
        infoTextPanel.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingMedium()));
        infoTextPanel.add(startButton);

        return infoTextPanel;
    }

    private RoundedButton createStartButton() throws IOException, FontFormatException {
        startButton = ButtonFactory.create("Start Now!",
                loadCustomFont(Fonts.DM_SANS_REGULAR, 16f),
                themeManager.getRed(), themeManager.getPink(),
                200, 40, 20);
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

    private JLabel createHowToUseLabel() throws IOException, FontFormatException {
        JLabel howText = LabelFactory.create("How to use?",
                loadCustomFont(Fonts.DM_SANS_BOLD, 18f),
                themeManager.getForegroundColor());
        return howText;
    }

    private JPanel createInstructionContainer() throws IOException, FontFormatException {
        JPanel instructContainer = PanelFactory.create(themeManager.getBackgroundColor(), 0, 0, 0);
        instructContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        instructContainer.setPreferredSize(new Dimension(1000, 350));

        instructContainer.add(StepPanel.createStepPanel(
                1,
                new String[]{"Enter your current location.", "Enter your destination"},
                null,
                "blue",
                true,
                cardChanger
        ));

        instructContainer.add(StepPanel.createStepPanel(
                2,
                new String[]{"See the routes pop up!"},
                Images.getInstance().getRouteImageLabel(400, 400),
                "yellow",
                false,
                null
        ));

        instructContainer.add(StepPanel.createStepPanel(
                3,
                new String[]{"Click a specific route for a", "detailed overview of the", "stops, transfers and more."},
                Images.getInstance().getRouteOverviewLabel(400, 400),
                "blue",
                false,
                null
        ));

        return instructContainer;
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
        SwingUtilities.invokeLater(this::repaint);
    }

    public void dispose() {
        if (themeManager != null) {
            themeManager.removeThemeChangeListener(this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame f = new JFrame("landingPage Preview");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                Consumer<String> dummy = System.out::println;
                LandingPage p = new LandingPage(dummy);
                f.add(p);
                f.setSize(1920, 1080);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }
}
