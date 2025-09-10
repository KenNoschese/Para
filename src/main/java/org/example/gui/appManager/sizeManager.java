package org.example.gui.appManager;

import java.awt.*;

public class sizeManager {
    // Window + Layout
    public static final Dimension WINDOW_SIZE = new Dimension(1920, 1080);
    public static final Dimension HEADER_SIZE = new Dimension(1920, 160);
    public static final Dimension CONTENTPANE_SIZE = new Dimension(1200, 800);
    public static final Dimension FOOTER_SIZE = new Dimension(0, 50);
    public static final Dimension INFOCONTAINER_SIZE = new Dimension(1200, 300);

    // Panels (flexible widths, fixed heights)
    public static final int TEXTCONTAINER_HEIGHT = 195;
    public static final int STATUSPANEL_HEIGHT = 170;
    public static final int ROUTECONTAINER_HEIGHT = 450;
    public static final int INPUT_FIELD_HEIGHT = 40;
    public static final int BUTTON_HEIGHT = 40;

    // Typography
    public static final float HEADING_LARGE = 40f;
    public static final float HEADING_MEDIUM = 22f;
    public static final float HEADING_SMALL = 18f;
    public static final float TEXT_LARGE = 20f;
    public static final float TEXT_MEDIUM = 16f;
    public static final float TEXT_SMALL = 14f;
    public static final float TEXT_TINY = 12f;

    // Spacing + Radius
    public static final int SPACING_LARGE = 30;
    public static final int SPACING_MEDIUM = 20;
    public static final int SPACING_SMALL = 15;

    public static final int BORDER_RADIUS_LARGE = 30;
    public static final int BORDER_RADIUS_SMALL = 20;

    // Utility: Flexible size (min 0, pref, max infinite)
    public static Dimension flexibleWidth(int prefWidth, int height) {
        return new Dimension(prefWidth, height);
    }

    public static void applyFlexibleSize(Component comp, int prefWidth, int height) {
        comp.setMinimumSize(new Dimension(0, height));
        comp.setPreferredSize(new Dimension(prefWidth, height));
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }
}
