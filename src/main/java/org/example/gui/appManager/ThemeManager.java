package org.example.gui.appManager;

import java.awt.*;
import java.util.*;
import javax.swing.*;

public class ThemeManager {
    private static ThemeManager instance;
    private boolean isDark = false;

    private final ArrayList<ThemeChangeListener> listeners = new ArrayList<>();

    private Color lightBackground = new Color(0xf0eeee);
    private Color lightForeground = Color.BLACK;
    private Color lightComponents = Color.WHITE;
    private Color lightYellow = new Color(0xffe786);
    private Color lightGreen = new Color(0x84b477);
    private Color lightBlue = new Color(0xb7caef);
    private Color lightPink = new Color(0xe8ced6);
    private Color lightRed = new Color(0xd85259);
    private Color lightGray = new Color(0xd0d0d0);

    private Color darkBackground = new Color(45, 45, 45);
    private Color darkForeground = Color.WHITE;
    private Color darkComponents = new Color(60, 60, 60);
    private Color darkYellow = new Color(0x8b6f47);
    private Color darkGreen = new Color(0x4a7043);
    private Color darkBlue = new Color(0x3b5a9a);
    private Color darkPink = new Color(0x8a6a76);
    private Color darkRed = new Color(0x7a2f34);
    private Color darkGray = new Color(0x4a4a4a);
    private Color darkPanelColor = new Color(35, 35, 35);

    private ThemeManager() {}

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public void toggleDark() {
        isDark = !isDark;
        applyThemeToAllComponents();
        notifyListeners();
    }

    public void setDarkMode(boolean darkMode) {
        this.isDark = darkMode;
        applyThemeToAllComponents();
        notifyListeners();
    }

    public void addThemeChangeListener(ThemeChangeListener listener) {
        listeners.add(listener);
    }

    public void removeThemeChangeListener(ThemeChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (ThemeChangeListener listener : listeners) {
            listener.onThemeChange(isDark);
        }
    }

    public void applyThemeToAllComponents() {
        for (Window window : Window.getWindows()) {
            if (window.isDisplayable()) {
                applyThemeToContainers(window);
                window.repaint();
            }
        }
    }

    public void applyThemeToContainers(Container container) {
        applyThemeToComponents(container);

        for (Component comp : container.getComponents()) {
            if (comp instanceof Container) {
                applyThemeToContainers((Container) comp);
            } else {
                applyThemeToComponents(comp);
            }
        }
    }

    private void applyThemeToComponents(Component comp) {
        comp.setForeground(getForegroundColor());

        if (comp instanceof JComponent jcomp) {
            if (jcomp instanceof JButton || jcomp instanceof JTextField ||
                    jcomp instanceof JComboBox || jcomp instanceof JList) {
                jcomp.setBackground(getComponentsColor());
                jcomp.setBorder(BorderFactory.createLineBorder(getGray()));
            }
            else if (jcomp instanceof JLabel) {
                jcomp.setForeground(getForegroundColor());
            }
            else if (jcomp instanceof JPanel) {
                Color bg = jcomp.getBackground();

                if (bg.equals(lightYellow) || bg.equals(darkYellow)) {
                    jcomp.setBackground(getYellow());
                }
                else if (bg.equals(lightBlue) || bg.equals(darkBlue)) {
                    jcomp.setBackground(getBlue());
                }
                else if (bg.equals(lightGreen) || bg.equals(darkGreen)) {
                    jcomp.setBackground(getGreen());
                }
                else if (bg.equals(lightPink) || bg.equals(darkPink)) {
                    jcomp.setBackground(getPink());
                }
                else if (bg.equals(lightRed) || bg.equals(darkRed)) {
                    jcomp.setBackground(getRed());
                }
                else if (bg.equals(lightGray) || bg.equals(darkGray)) {
                    jcomp.setBackground(getGray());
                }
                else if (bg.equals(lightBackground) || bg.equals(darkBackground) ||
                        bg.equals(lightComponents) || bg.equals(darkComponents)) {
                    jcomp.setBackground(getBackgroundColor());
                }
            }
        }
    }

    // Getters
    public boolean isDarkMode() { return isDark; }

    public Color getBackgroundColor() {
        return isDark ? darkBackground : lightBackground;
    }
    public Color getForegroundColor() {
        return isDark ? darkForeground : lightForeground;
    }
    public Color getComponentsColor() {
        return isDark ? darkComponents : lightComponents;
    }
    public Color getWhite() {
        return isDark ? darkForeground : lightComponents;
    }
    public Color getBlack() {
        return isDark ? darkForeground : lightForeground;
    }
    public Color getGray() {
        return isDark ? darkGray : lightGray;
    }
    public Color getYellow() {
        return isDark ? darkYellow : lightYellow;
    }
    public Color getGreen() {
        return isDark ? darkGreen : lightGreen;
    }
    public Color getBlue() {
        return isDark ? darkBlue : lightBlue;
    }
    public Color getPink() {
        return isDark ? darkPink : lightPink;
    }
    public Color getRed() {
        return isDark ? darkRed : lightRed;
    }
    public Color getPanelColor() {
        return isDark ? darkPanelColor : lightComponents;
    }

    public interface ThemeChangeListener {
        void onThemeChange(boolean isDarkMode);
    }
}
