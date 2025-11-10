package org.example.gui.components.dialogs;

import java.awt.*;

/**
 * Success dialog with green checkmark icon.
 */
public class SuccessDialog extends BaseDialog {

    public SuccessDialog(Frame owner, String title, String message) throws Exception {
        super(owner, title, message);
    }

    @Override
    protected String getIcon() {
        return "✓";
    }

    @Override
    protected Color getIconColor() {
        return new Color(76, 175, 80); // Green
    }

    @Override
    protected Color getButtonColor() {
        return new Color(76, 175, 80); // Green
    }

    /**
     * Show a success dialog
     */
    public static void show(Component parent, String message, String title) throws Exception {
        Frame owner = getFrame(parent);
        SuccessDialog dialog = new SuccessDialog(owner, title, message);
        System.out.println("Dialog built – message: " + message);
        dialog.setVisible(true);
    }

    /**
     * Show a success dialog with default title
     */
    public static void show(Component parent, String message) throws Exception {
        show(parent, message, "Success");
    }
}