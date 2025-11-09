package org.example.gui.components.elements;

import org.example.gui.appManager.ThemeManager;
import org.example.gui.appManager.SizeManager;
import org.example.gui.components.base.RoundedButton;
import org.example.gui.components.base.RoundedPanel;
import org.example.gui.resources.Fonts;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.function.Consumer;

import static org.example.gui.resources.Fonts.loadCustomFont;

public class StepPanel {
    public static RoundedPanel createStepPanel(
            int stepNumber,
            String[] textLines,
            JLabel image,
            String themeColor,
            boolean includeButton,
            Consumer<String> cardChanger
    ) throws IOException, FontFormatException {

        ThemeManager themeManager = ThemeManager.getInstance();
        RoundedPanel panel = new RoundedPanel(SizeManager.getInstance().getBorderRadiusLarge());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(375, 350));
        panel.setBackground(themeColor.equals("blue") ? themeManager.getBlue() : themeManager.getYellow());
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.putClientProperty("themeColor", themeColor);

        JLabel heading = new JLabel("Step " + stepNumber);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setFont(loadCustomFont(Fonts.DM_SANS_BOLD, 18f));
        heading.setForeground(themeManager.getForegroundColor());

        RoundedPanel textPanel = new RoundedPanel(SizeManager.getInstance().getBorderRadiusLarge());
        textPanel.setPreferredSize(new Dimension(30, 20));
        textPanel.setMaximumSize(new Dimension(300, textLines.length > 1 ? 75 : 50));
        textPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.setBackground(themeManager.getComponentsColor());

        String labelText = textLines.length > 1 ? "<html>" + String.join("<br>", textLines) + "</html>" : textLines[0];
        JLabel textLabel = new JLabel(labelText);
        textLabel.setFont(loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
        textLabel.setForeground(themeManager.getForegroundColor());
        textPanel.add(textLabel);

        panel.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingLarge()));
        panel.add(heading);
        panel.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingMedium()));
        panel.add(textPanel);

        if (image != null) {
            panel.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingSmall()));
            image.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(image);
        }

        if (includeButton) {
            RoundedButton button = new RoundedButton("Then press search!");
            button.setArc(30, 30);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setPreferredSize(new Dimension(30, 20));
            button.setFont(loadCustomFont(Fonts.DM_SANS_REGULAR, 16f));
            button.setBackground(themeManager.getRed());
            button.setForeground(themeManager.getPink());

            if (cardChanger != null) {
                button.addActionListener(e -> cardChanger.accept("MAIN"));
            }

            panel.add(Box.createVerticalStrut(SizeManager.getInstance().getSpacingMedium()));
            panel.add(button);
        }

        return panel;
    }
}
