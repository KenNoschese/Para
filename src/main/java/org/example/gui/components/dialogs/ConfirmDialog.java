package org.example.gui.components.dialogs;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import org.example.gui.appManager.ThemeManager;
import org.example.gui.components.base.RoundedPanel;
import org.example.gui.resources.Fonts;

/**
 * Confirmation dialog with Yes/No buttons.
 */
public class ConfirmDialog extends JDialog {
    private final ThemeManager themeManager;
    private boolean confirmed = false;

    public ConfirmDialog(Frame owner, String title, String message) {
        super(owner, title, true);
        this.themeManager = ThemeManager.getInstance();
        setupDialog(message);
    }

    private void setupDialog(String message) {
        setUndecorated(true);
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 0));

        RoundedPanel mainPanel = new RoundedPanel(30);
        mainPanel.setBackground(themeManager.getWhite());
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeManager.getBlack(), 2),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        mainPanel.setLayout(new BorderLayout(0, 20));

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel iconLabel = new JLabel("?");
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        iconLabel.setForeground(new Color(255, 152, 0)); // Orange
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(getTitle());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(themeManager.getBlack());
        try {
            titleLabel.setFont(Font.createFont(Font.TRUETYPE_FONT,
                    new File(Fonts.DM_SANS_BOLD)).deriveFont(18f));
        } catch (Exception e) {
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        }

        header.add(iconLabel);
        header.add(Box.createVerticalStrut(10));
        header.add(titleLabel);

        // Message
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setForeground(themeManager.getBlack().brighter());
        try {
            messageLabel.setFont(Font.createFont(Font.TRUETYPE_FONT,
                    new File(Fonts.DM_SANS_REGULAR)).deriveFont(14f));
        } catch (Exception e) {
            messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        }

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        JButton yesButton = createButton("Yes", themeManager.getGreen(), true);
        JButton noButton = createButton("No", themeManager.getGray(), false);

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(messageLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setSize(500, 350);
        setLocationRelativeTo(getOwner());
    }

    private JButton createButton(String text, Color color, boolean isYes) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.setColor(getForeground());

                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), x, y);

                g2.dispose();
            }
        };

        button.setPreferredSize(new Dimension(100, 40));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        try {
            button.setFont(Font.createFont(Font.TRUETYPE_FONT,
                    new File(Fonts.DM_SANS_BOLD)).deriveFont(14f));
        } catch (Exception e) {
            button.setFont(new Font("SansSerif", Font.BOLD, 14));
        }

        button.addActionListener(e -> {
            confirmed = isYes;
            dispose();
        });

        return button;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Show a confirmation dialog
     */
    public static boolean show(Component parent, String message, String title) {
        Frame owner = BaseDialog.getFrame(parent);
        ConfirmDialog dialog = new ConfirmDialog(owner, title, message);
        System.out.println("Dialog built – message: " + message);
        dialog.setVisible(true);
        return dialog.isConfirmed();
    }

    /**
     * Show a confirmation dialog with default title
     */
    public static boolean show(Component parent, String message) {
        return show(parent, message, "Confirm");
    }
}