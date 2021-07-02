package app.jackychu.jysrttools;

import app.jackychu.jysrttools.exception.JySrtToolsException;
import lombok.Data;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Data
public class JyFont {
    private String name;
    private File ttfFile;
    private Font ttfFont;
    private boolean replaced = false;
    private String replacedName;

    public JyFont(File ttfFile) throws JySrtToolsException {
        this.ttfFile = ttfFile;
        this.name = ttfFile.getName().split("\\.")[0];

        try {
            ttfFont = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(ttfFile));
            GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
            genv.registerFont(ttfFont);
            ttfFont = ttfFont.deriveFont(20f);
        } catch (FontFormatException | IOException e) {
            throw new JySrtToolsException("讀取字型錯誤: " + name, e);
        }
    }
}
