package org.example.gui.appManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SizeManager {
    // Singleton instance
    private static final SizeManager instance = new SizeManager();

    // List to track components affected by size changes
    private final List<Component> registeredComponents = new ArrayList<>();

    // Window + Layout
    private static final Dimension WINDOW_SIZE = new Dimension(1920, 1080);
    private static final Dimension HEADER_SIZE = new Dimension(1920, 160);
    private static final Dimension CONTENTPANE_SIZE = new Dimension(1200, 800);
    private static final Dimension FOOTER_SIZE = new Dimension(0, 50);
    private static final Dimension INFOCONTAINER_SIZE = new Dimension(1200, 300);

    // Panels
    private static final int TEXTCONTAINER_HEIGHT = 195;
    private static final int STATUSPANEL_HEIGHT = 170;
    private static final int ROUTECONTAINER_HEIGHT = 450;
    private static final int INPUT_FIELD_HEIGHT = 40;
    private static final int BUTTON_HEIGHT = 40;

    // Typography
    private static final float HEADING_LARGE = 40f;
    private static final float HEADING_MEDIUM = 22f;
    private static final float HEADING_SMALL = 18f;
    private static final float TEXT_LARGE = 20f;
    private static final float TEXT_MEDIUM = 16f;
    private static final float TEXT_SMALL = 14f;
    private static final float TEXT_TINY = 12f;

    // Spacing + Radius
    private static final int SPACING_LARGE = 30;
    private static final int SPACING_MEDIUM = 20;
    private static final int SPACING_SMALL = 15;

    private static final int BORDER_RADIUS_LARGE = 30;
    private static final int BORDER_RADIUS_SMALL = 20;

    // Private constructor for singleton
    private SizeManager() {}

    // Get singleton instance
    public static SizeManager getInstance() {
        return instance;
    }

    // Getter methods for private constants
    public Dimension getWindowSize() {
        return new Dimension(WINDOW_SIZE);
    }

    public Dimension getHeaderSize() {
        return new Dimension(HEADER_SIZE);
    }

    public Dimension getContentPaneSize() {
        return new Dimension(CONTENTPANE_SIZE);
    }

    public Dimension getFooterSize() {
        return new Dimension(FOOTER_SIZE);
    }

    public Dimension getInfoContainerSize() {
        return new Dimension(INFOCONTAINER_SIZE);
    }

    public int getTextContainerHeight() {
        return TEXTCONTAINER_HEIGHT;
    }

    public int getStatusPanelHeight() {
        return STATUSPANEL_HEIGHT;
    }

    public int getRouteContainerHeight() {
        return ROUTECONTAINER_HEIGHT;
    }

    public int getInputFieldHeight() {
        return INPUT_FIELD_HEIGHT;
    }

    public int getButtonHeight() {
        return BUTTON_HEIGHT;
    }

    public float getHeadingLarge() {
        return HEADING_LARGE;
    }

    public float getHeadingMedium() {
        return HEADING_MEDIUM;
    }

    public float getHeadingSmall() {
        return HEADING_SMALL;
    }

    public float getTextLarge() {
        return TEXT_LARGE;
    }

    public float getTextMedium() {
        return TEXT_MEDIUM;
    }

    public float getTextSmall() {
        return TEXT_SMALL;
    }

    public float getTextTiny() {
        return TEXT_TINY;
    }

    public int getSpacingLarge() {
        return SPACING_LARGE;
    }

    public int getSpacingMedium() {
        return SPACING_MEDIUM;
    }

    public int getSpacingSmall() {
        return SPACING_SMALL;
    }

    public int getBorderRadiusLarge() {
        return BORDER_RADIUS_LARGE;
    }

    public int getBorderRadiusSmall() {
        return BORDER_RADIUS_SMALL;
    }

    // Register a component to be affected by size changes
    public void registerComponent(Component comp) {
        if (!registeredComponents.contains(comp)) {
            registeredComponents.add(comp);
        }
    }

    // Remove a component from size change tracking
    public void unregisterComponent(Component comp) {
        registeredComponents.remove(comp);
    }

    // Update preferred size of a JPanel and adjust maximum size of affected components
    public void updatePreferredSize(JPanel panel, Dimension newPreferredSize) {
        panel.setPreferredSize(newPreferredSize);

        // Adjust maximum size to prevent components from exceeding the new preferred size
        Dimension newMaxSize = new Dimension(
                Math.min(newPreferredSize.width, Integer.MAX_VALUE),
                Math.min(newPreferredSize.height, Integer.MAX_VALUE)
        );
        panel.setMaximumSize(newMaxSize);

        // Update all registered components within the panel
        updateAffectedComponents(panel, newPreferredSize);

        // Revalidate and repaint the panel to reflect changes
        panel.revalidate();
        panel.repaint();
    }

    // Update maximum size of all registered components within the panel
    private void updateAffectedComponents(JPanel panel, Dimension newPreferredSize) {
        for (Component comp : registeredComponents) {
            if (isDescendant(panel, comp)) {
                Dimension currentPrefSize = comp.getPreferredSize();
                Dimension newMaxSize = new Dimension(
                        Math.min(currentPrefSize.width, newPreferredSize.width),
                        Math.min(currentPrefSize.height, newPreferredSize.height)
                );
                comp.setMaximumSize(newMaxSize);
            }
        }
    }

    // Check if a component is a descendant of a given panel
    private boolean isDescendant(Container parent, Component comp) {
        if (comp == null || parent == null) {
            return false;
        }
        Container current = comp.getParent();
        while (current != null) {
            if (current == parent) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    // Utility: Flexible size (min 0, pref, max infinite)
    public Dimension flexibleWidth(int prefWidth, int height) {
        return new Dimension(prefWidth, height);
    }

    public void applyFlexibleSize(Component comp, int prefWidth, int height) {
        comp.setMinimumSize(new Dimension(0, height));
        comp.setPreferredSize(new Dimension(prefWidth, height));
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }
}