package org.example.gui.components.dialogs;

import java.awt.*;

/**
 * Error dialog with red X icon.
 */
public class ErrorDialog extends BaseDialog {

    public ErrorDialog(Frame owner, String title, String message) throws Exception {
        super(owner, title, message);
    }

    @Override
    protected String getIcon() {
        return "✕";
    }

    @Override
    protected Color getIconColor() {
        return new Color(244, 67, 54); // Red
    }

    @Override
    protected Color getButtonColor() {
        return new Color(244, 67, 54); // Red
    }

    /**
     * Show an error dialog
     */
    public static void show(Component parent, String message, String title) throws Exception {
        Frame owner = getFrame(parent);
        ErrorDialog dialog = new ErrorDialog(owner, title, message);
        dialog.setVisible(true);
    }

    /**
     * Show an error dialog with default title
     */
    public static void show(Component parent, String message) throws Exception {
        show(parent, message, "Error");
    }
}