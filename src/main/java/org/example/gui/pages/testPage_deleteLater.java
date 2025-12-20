package org.example.gui.pages;

import org.example.DatabaseManager.RouteDatabase.ObserversClasses.JeepneyObserver;
import org.example.DatabaseManager.RouteDatabase.ObserversClasses.JeepneySubject;
import org.example.gui.appManager.ThemeManager;
import org.example.gui.components.base.RoundedButton;
import org.example.gui.components.base.RoundedPanel;
import org.example.gui.components.dialogs.ErrorDialog;
import org.example.gui.resources.Fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Temporary testing page for Jeepney observer + database updates.
 *
 * Features:
 * - Enter plate number (e.g., ABC-123)
 * - Click "Board Jeep" → +1 passenger
 * - Click "Leave Jeep" → -1 passenger
 * - Live update: "Passengers: 5 / 16"
 * - Red text when full
 * - Real-time via Observer Pattern
 *
 * Requires:
 * - JeepneySubject with boardJeepney(String) and leaveJeepney(String)
 * - Jeepney in DB: INSERT INTO Jeepneys (plate_number, route_id, capacity) VALUES ('ABC-123', 1, 16);
 */
public class testPage_deleteLater extends JPanel implements JeepneyObserver {

    private final ThemeManager themeManager;
    private final JeepneySubject jeepneySubject;
    private JLabel statusLabel;
    private JTextField plateField;
    private boolean inJeep = false;

    public testPage_deleteLater() throws IOException, FontFormatException, SQLException {
        this.themeManager = ThemeManager.getInstance();
        this.jeepneySubject = new JeepneySubject();
        this.jeepneySubject.registerObserver(this);
        setupUI();
    }

    private void setupUI() throws IOException, FontFormatException {
        setLayout(new BorderLayout());
        setBackground(themeManager.getBackgroundColor());

        // ========================================
        // HEADER
        // ========================================
        JPanel headerWrapper = new JPanel(new BorderLayout());
        headerWrapper.setBackground(themeManager.getBackgroundColor());
        headerWrapper.setPreferredSize(new Dimension(1920, 80));

        JLabel title = new JLabel("Jeepney Behavior Debug");
        title.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 22f));
        title.setForeground(themeManager.getForegroundColor());
        title.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 0));
        headerWrapper.add(title, BorderLayout.WEST);
        add(headerWrapper, BorderLayout.NORTH);

        // ========================================
        // INPUT PANEL
        // ========================================
        RoundedPanel inputPanel = new RoundedPanel(25);
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10));
        inputPanel.setBackground(themeManager.getBlue());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JLabel plateLabel = new JLabel("Plate Number:");
        plateLabel.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 16f));
        plateLabel.setForeground(themeManager.getWhite());

        plateField = new JTextField();
        plateField.setFont(loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));

        inputPanel.add(plateLabel);
        inputPanel.add(plateField);

        // Status Label
        statusLabel = new JLabel("Passengers: -- / --", SwingConstants.CENTER);
        statusLabel.setFont(loadCustomFont(Fonts.DM_SANS_REGULAR, 18f));
        statusLabel.setForeground(themeManager.getWhite());

        inputPanel.add(new JLabel());
        inputPanel.add(statusLabel);

        add(inputPanel, BorderLayout.CENTER);

        // ========================================
        // BOARD / LEAVE BUTTON
        // ========================================
        RoundedButton boardButton = new RoundedButton("Board Jeep");
        boardButton.setPreferredSize(new Dimension(180, 45));
        boardButton.setBackground(themeManager.getGreen());
        boardButton.setForeground(themeManager.getWhite());
        boardButton.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 16f));
        boardButton.addActionListener(e -> handleBoardButton(boardButton));

        boardButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boardButton.setForeground(themeManager.getBlack());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boardButton.setForeground(themeManager.getWhite());
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        bottomPanel.setBackground(themeManager.getBackgroundColor());
        bottomPanel.add(boardButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Handle Board / Leave button click
     */
    private void handleBoardButton(RoundedButton button) {
        String plateNumber = plateField.getText().trim();

        if (plateNumber.isEmpty()) {
            try {
                ErrorDialog.show(this, "Please enter a plate number first.", "Input Error");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
        }

        try {
            if (!inJeep) {
                // BOARD: +1 passenger
                jeepneySubject.boardJeepney(plateNumber);
                button.setText("Leave Jeep");
                button.setBackground(themeManager.getRed());
                inJeep = true;
            } else {
                // LEAVE: -1 passenger
                jeepneySubject.leaveJeepney(plateNumber);
                button.setText("Board Jeep");
                button.setBackground(themeManager.getGreen());
                inJeep = false;
            }
        } catch (SQLException ex) {
            try {
                ErrorDialog.show(this, "Database Error: " + ex.getMessage(), "Error");
            } catch (Exception e) {
                e.printStackTrace();
            }
            ex.printStackTrace();
        }
    }

    /**
     * Observer update — called when passenger count changes
     */
    @Override
    public void update(String plateNumber, int currentPassengers, int capacity) {
        String displayPlate = plateField.getText().trim();
        if (displayPlate.equals(plateNumber)) {
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Passengers: " + currentPassengers + " / " + capacity);
                if (currentPassengers >= capacity) {
                    statusLabel.setForeground(themeManager.getRed());
                } else {
                    statusLabel.setForeground(themeManager.getWhite());
                }
            });
        }
        System.out.println("[GUI Observer] Jeep " + plateNumber + ": " + currentPassengers + "/" + capacity);
    }

    /**
     * Load custom font from resources
     */
    private static Font loadCustomFont(String fontPath, float size) throws IOException, FontFormatException {
        Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        return font.deriveFont(size);
    }

    // ========================================
    // STANDALONE TEST
    // ========================================
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