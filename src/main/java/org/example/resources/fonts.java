package org.example.resources;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class fonts {
    public static final String DM_SANS_REGULAR = "ProjectFiles/DMSans.ttf";
    public static final String DM_SANS_BOLD = "ProjectFiles/DMSansBold.ttf";
    public static final String DM_SANS_ITALIC = "ProjectFiles/DMSansItalic.ttf";

    public static Font loadCustomFont(String fontPath, float size) throws IOException, FontFormatException {
        Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        return font.deriveFont(size);
    }
}