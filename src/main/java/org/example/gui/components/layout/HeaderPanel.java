package org.example.gui.components.layout;

import org.example.gui.appManager.ThemeManager;
import org.example.gui.appManager.DarkModeToggle;
import org.example.gui.appManager.SizeManager;
import org.example.gui.config.AnimationConfig;
import org.example.gui.resources.Images;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

public class HeaderPanel {
    public static JPanel createHeaderPanel() throws IOException {
        ThemeManager themeManager = ThemeManager.getInstance();
        AnimationConfig config = new AnimationConfig();

        JPanel headerPanel = new JPanel() {
            private Image jeepney;
            private Image backgroundImage;
            private Timer timer;
            private int x = 0;
            private boolean isPaused = false;

            {
                themeManager.addThemeChangeListener(isDarkMode -> { setBackground(themeManager.getWhite()); repaint(); });
                setupPanel();
                setupJeepneyAnimation();
            }

            private void setupPanel() throws IOException {
                setPreferredSize(SizeManager.getInstance().getHeaderSize());
                setOpaque(false);
                setLayout(null);
                setBackground(themeManager.getWhite());

                JLabel logo = Images.getInstance().getParaLogoLabel(175, 175);
                logo.setBounds((getWidth() / 2) - 87, 0, 175, 175);
                addComponentListener(new ComponentAdapter() {
                    @Override public void componentResized(ComponentEvent e) {
                        logo.setBounds((getWidth() / 2) - 87, 0, 175, 175);
                    }
                });
                add(logo);

                DarkModeToggle toggle = new DarkModeToggle();
                toggle.setBounds(getWidth() - 80, 20, 50, 30);
                add(toggle);
                addComponentListener(new ComponentAdapter() {
                    @Override public void componentResized(ComponentEvent e) {
                        toggle.setBounds(getWidth() - 80, 20, 50, 30);
                    }
                });

                Images images = Images.getInstance();
                ImageIcon cityIcon = images.getCityIcon();
                ImageIcon jeepIcon = images.getJeepIcon();
                backgroundImage = cityIcon.getImage().getScaledInstance(1920, 160, Image.SCALE_SMOOTH);
                jeepney = jeepIcon.getImage().getScaledInstance(config.jeepneyWidth, config.jeepneyHeight, Image.SCALE_SMOOTH);
            }

            private void setupJeepneyAnimation() {
                timer = new Timer(config.timerInterval, e -> {
                    if (!isPaused) {
                        x += config.speed;
                        if (Math.abs(x - (getWidth() / 2 - jeepney.getWidth(null) / 2)) < config.centerThreshold) {
                            isPaused = true; x = getWidth() / 2 - jeepney.getWidth(null) / 2;
                            new Timer(config.centerPauseDuration, evt -> isPaused = false).start();
                        }
                        if (x >= getWidth()) {
                            isPaused = true;
                            new Timer(config.restartDelay, evt -> { x = 0; isPaused = false; }).start();
                        }
                    }
                    repaint();
                });
                timer.start();
            }

            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2D = (Graphics2D) g;
                if (backgroundImage != null) g2D.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
                if (jeepney != null) g2D.drawImage(jeepney, x, config.yPosition, null);
            }
        };
        return headerPanel;
    }
}
