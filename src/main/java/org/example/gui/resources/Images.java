package org.example.gui.resources;

import javax.swing.*;
import java.awt.*;

public class Images {
    private static Images instance;
    private static final String IMAGES_PATH = "ProjectFiles/";

    private static final ImageIcon cityIcon = new ImageIcon(IMAGES_PATH + "City.png");
    private static final ImageIcon jeepIcon = new ImageIcon(IMAGES_PATH + "jeep.png");
    private static final ImageIcon paraLogoIcon = new ImageIcon(IMAGES_PATH + "Para.png");
    private static final ImageIcon mockupIcon = new ImageIcon(IMAGES_PATH + "MockUp.png");
    private static final ImageIcon routeImageIcon = new ImageIcon(IMAGES_PATH + "RouteImg.png");
    private static final ImageIcon routeOverviewIcon = new ImageIcon(IMAGES_PATH + "routeov.png");
    private static final ImageIcon placeholderIcon = new ImageIcon(IMAGES_PATH + "nodata.png");

    public ImageIcon getCityIcon() {
        return cityIcon;
    }

    public ImageIcon getJeepIcon() {
        return jeepIcon;
    }

    public JLabel getParaLogoLabel(int width, int height) {
        return createImageLabel(paraLogoIcon, width, height);
    }

    public JLabel getMockupLabel(int width, int height) {
        return createImageLabel(mockupIcon, width, height);
    }

    public JLabel getRouteImageLabel(int width, int height) {
        return createImageLabel(routeImageIcon, width, height);
    }

    public JLabel getRouteOverviewLabel(int width, int height) {
        return createImageLabel(routeOverviewIcon, width, height);
    }

    public JLabel getPlaceholderLabel(int width, int height) {
        return createImageLabel(placeholderIcon, width, height);
    }

    private JLabel createImageLabel(ImageIcon icon, int width, int height) {
        JLabel label = new JLabel(getScaledIcon(icon, width, height), SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    public static ImageIcon getScaledIcon(ImageIcon icon, int width, int height) {
        if (icon == null) return null;
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public static Images getInstance() {
        if (instance == null) {
            instance = new Images();
        }
        return instance;
    }

    private Images() {
    }
}