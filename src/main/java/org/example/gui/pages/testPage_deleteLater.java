package org.example.gui.pages;

import org.example.DatabaseManager.DatabaseInstance;
import org.example.DatabaseManager.RouteDatabase.ObserversClasses.JeepneyObserver;
import org.example.DatabaseManager.RouteDatabase.RouteManager;
import org.example.gui.appManager.ThemeManager;
import org.example.gui.components.base.RoundedButton;
import org.example.gui.components.base.RoundedPanel;
import org.example.gui.resources.Fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Temporary testing page for Jeepney observer + database updates.
 */
public class testPage_deleteLater extends JPanel implements JeepneyObserver {
    private ThemeManager themeManager;
    private JLabel statusLabel;
    private JTextField plateField, fromField, toField;
    private RouteManager routeManager;
    private boolean inJeep = false;

    public testPage_deleteLater() throws IOException, FontFormatException, SQLException {
        this.themeManager = ThemeManager.getInstance();
        this.routeManager = new RouteManager();
        this.routeManager.addJeepneyObserver(this); // Register observer
        setupUI();
    }

    private void setupUI() throws IOException, FontFormatException {
        setLayout(new BorderLayout());
        setBackground(themeManager.getBackgroundColor());

        // 🔹 Header
        JPanel headerWrapper = new JPanel(new BorderLayout());
        headerWrapper.setBackground(themeManager.getBackgroundColor());
        headerWrapper.setPreferredSize(new Dimension(1920, 80));

        JLabel title = new JLabel("🚐 Jeepney Behavior Debug");
        title.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 22f));
        title.setForeground(themeManager.getForegroundColor());
        title.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 0));
        headerWrapper.add(title, BorderLayout.WEST);

        add(headerWrapper, BorderLayout.NORTH);

        // 🔹 Input Panel
        RoundedPanel inputPanel = new RoundedPanel(25);
        inputPanel.setLayout(new GridLayout(4, 2, 10, 10));
        inputPanel.setBackground(themeManager.getBlue());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JLabel fromLabel = new JLabel("From:");
        JLabel toLabel = new JLabel("To:");
        JLabel plateLabel = new JLabel("Plate Number:");

        fromLabel.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 16f));
        toLabel.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 16f));
        plateLabel.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 16f));

        fromField = new JTextField();
        toField = new JTextField();
        plateField = new JTextField();

        inputPanel.add(fromLabel);
        inputPanel.add(fromField);
        inputPanel.add(toLabel);
        inputPanel.add(toField);
        inputPanel.add(plateLabel);
        inputPanel.add(plateField);

        // 🔹 Status Label
        statusLabel = new JLabel("Passengers: -- / --", SwingConstants.CENTER);
        statusLabel.setFont(loadCustomFont(Fonts.DM_SANS_REGULAR, 18f));
        statusLabel.setForeground(themeManager.getBlack());

        inputPanel.add(new JLabel());
        inputPanel.add(statusLabel);

        add(inputPanel, BorderLayout.CENTER);

        // 🔹 Board Button
        RoundedButton jeepStatusButton = new RoundedButton("🚐 Board Jeep");
        jeepStatusButton.setPreferredSize(new Dimension(180, 45));
        jeepStatusButton.setBackground(themeManager.getGreen());
        jeepStatusButton.setForeground(themeManager.getWhite());
        jeepStatusButton.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 16f));

        jeepStatusButton.addActionListener(e -> handleBoardButton(jeepStatusButton));

        jeepStatusButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                jeepStatusButton.setForeground(themeManager.getBlack());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                jeepStatusButton.setForeground(themeManager.getWhite());
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        bottomPanel.setBackground(themeManager.getBackgroundColor());
        bottomPanel.add(jeepStatusButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void handleBoardButton(RoundedButton button) {
        String plateNumber = plateField.getText().trim();
        Connection conn = DatabaseInstance.getInstance().getConnection();

        if (plateNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a plate number first.");
            return;
        }

        try {
            if (!inJeep) {
                routeManager.boardJeepney(plateNumber, conn);
                button.setText("🏁 Leave Jeep");
                button.setBackground(themeManager.getBlue());
                inJeep = true;
            } else {
                routeManager.leaveJeepney(plateNumber, conn);
                button.setText("🚐 Board Jeep");
                button.setBackground(themeManager.getGreen());
                inJeep = false;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating jeepney: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void update(String plateNumber, int currentPassengers, int capacity) {
        // ✅ Update the label whenever the Jeepney data changes
        SwingUtilities.invokeLater(() ->
                statusLabel.setText("Passengers: " + currentPassengers + " / " + capacity)
        );
        System.out.println("[Observer Update] Jeep " + plateNumber + ": " + currentPassengers + "/" + capacity);
    }

    private static Font loadCustomFont(String fontPath, float size) throws IOException, FontFormatException {
        Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        return font.deriveFont(size);
    }

    // ✅ Standalone debug
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("Jeepney Debug Page");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(900, 600);
                frame.setLocationRelativeTo(null);
                frame.add(new testPage_deleteLater());
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
