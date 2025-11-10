package org.example.gui.components.dialogs;

import java.awt.*;

/**
 * Info dialog with blue information icon.
 */
public class InfoDialog extends BaseDialog {

    public InfoDialog(Frame owner, String title, String message) throws Exception {
        super(owner, title, message);
    }

    @Override
    protected String getIcon() {
        return "ℹ";
    }

    @Override
    protected Color getIconColor() {
        return new Color(33, 150, 243); // Blue
    }

    @Override
    protected Color getButtonColor() {
        return new Color(33, 150, 243); // Blue
    }

    /**
     * Show an info dialog
     */
    public static void show(Component parent, String message, String title) throws Exception {
        Frame owner = getFrame(parent);
        InfoDialog dialog = new InfoDialog(owner, title, message);
        System.out.println("Dialog built – message: " + message);
        dialog.setVisible(true);
    }

    /**
     * Show an info dialog with default title
     */
    public static void show(Component parent, String message) throws Exception {
        show(parent, message, "Information");
    }
}