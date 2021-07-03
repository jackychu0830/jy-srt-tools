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
    private String type;
    private File file;
    private Font font;
    private boolean replaced = false;
    private String replacedName;

    public JyFont(File file) throws JySrtToolsException {
        this.file = file;
        String[] s = file.getName().split("\\.");
        this.name = s[0];
        this.type = s[s.length - 1];

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(file));
            GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
            genv.registerFont(font);
            font = font.deriveFont(20f);
        } catch (FontFormatException | IOException e) {
            throw new JySrtToolsException("讀取字型錯誤: " + name, e);
        }
    }
}
