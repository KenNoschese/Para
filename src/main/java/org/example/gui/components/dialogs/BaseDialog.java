package org.example.gui.components.dialogs;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import org.example.gui.appManager.ThemeManager;
import org.example.gui.components.base.RoundedPanel;
import org.example.gui.resources.Fonts;

/**
 * Base class for custom dialogs matching the application's UI design.
 */
public abstract class BaseDialog extends JDialog {
    protected final ThemeManager themeManager;
    protected JLabel messageLabel;

    public BaseDialog(Frame owner, String title, String message) throws Exception {
        super(owner, title, true);
        this.themeManager = ThemeManager.getInstance();
        setupDialog(message);
    }

    private void setupDialog(String message) throws Exception {
        setUndecorated(true);
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 0));

        // Main panel with rounded corners and border
        RoundedPanel mainPanel = new RoundedPanel(30);
        mainPanel.setBackground(themeManager.getWhite());
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBlack(), 2),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        mainPanel.setLayout(new BorderLayout(0, 20));

        // Header with icon and title
        JPanel header = createHeader();

        // Message

        messageLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setForeground(themeManager.getBlack().brighter());
        try {
            messageLabel.setFont(loadFont(Fonts.DM_SANS_REGULAR, 14f));
        } catch (Exception e) {
            messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        }

        // Button
        JButton okButton = createOkButton();
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(messageLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        add(mainPanel);

        pack();
        Dimension pref = getPreferredSize();
        int width = Math.max(pref.width, 420);
        int height = Math.max(pref.height, 200);
        setSize(width, height);

        setMinimumSize(new Dimension(420, 180));
        setMaximumSize(new Dimension(600, 400));
        setLocationRelativeTo(getOwner());
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        // Icon
        JLabel iconLabel = new JLabel(getIcon());
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        iconLabel.setForeground(getIconColor());
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel(getTitle());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(themeManager.getBlack());
        try {
            titleLabel.setFont(loadFont(Fonts.DM_SANS_BOLD, 18f));
        } catch (Exception e) {
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        }

        header.add(iconLabel);
        header.add(Box.createVerticalStrut(10));
        header.add(titleLabel);

        return header;
    }

    private JButton createOkButton() {
        JButton button = new JButton("OK") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Color base = getButtonColor();
                if (getModel().isPressed()) {
                    g2.setColor(base.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(base.brighter());
                } else {
                    g2.setColor(base);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.setColor(getForeground());

                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        button.setPreferredSize(new Dimension(120, 40));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ---- SAFE FONT LOADING ----
        try {
            button.setFont(loadFont(Fonts.DM_SANS_BOLD, 14f));
        } catch (Exception ignored) {
            button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        }

        button.addActionListener(e -> dispose());
        return button;
    }

    protected Font loadFont(String path, float size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(path));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font.deriveFont(size);
        } catch (Exception ex) {
            // Silent fallback – never let font loading crash the UI
            return new Font(Font.SANS_SERIF, Font.PLAIN, (int) size);
        }
    }

    protected static Frame getFrame(Component parent) {
        if (parent instanceof Frame) {
            return (Frame) parent;
        }
        return (Frame) SwingUtilities.getWindowAncestor(parent);
    }

    // Abstract methods to be implemented by subclasses
    protected abstract String getIcon();
    protected abstract Color getIconColor();
    protected abstract Color getButtonColor();
}