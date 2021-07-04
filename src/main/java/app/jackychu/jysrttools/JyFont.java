package app.jackychu.jysrttools;

import app.jackychu.jysrttools.exception.JySrtToolsException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

@Data
public class JyFont {
    private String name;
    private String type;
    private File file;
    private Font font;
    private boolean replaced = false;
    private String replacedName;
    @Setter(AccessLevel.NONE)
    private Font backupFont;

    public JyFont(File file) throws JySrtToolsException {
        this.file = file;
        String[] s = file.getName().split("\\.");
        this.name = s[0];
        this.type = s[s.length - 1];

        try {
            GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
            font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(file));
            genv.registerFont(font);
            font = font.deriveFont(20f);
        } catch (FontFormatException | IOException e) {
            throw new JySrtToolsException("讀取字型錯誤: " + name, e);
        }
    }

    public JyFont(File file, String replacedName) throws JySrtToolsException {
        this(file);
        replaced = true;
        this.replacedName = replacedName;
        setBackupFont();
    }

    public void setBackupFont() throws JySrtToolsException {
        try {
            GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
            backupFont = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(getBackupPath()));
            genv.registerFont(backupFont);
            backupFont = backupFont.deriveFont(20f);
        } catch (FontFormatException | IOException e) {
            throw new JySrtToolsException("讀取字型錯誤: " + getBackupPath(), e);
        }
    }

    public String getBackupPath() {
        if (replaced) {
            Path target = Paths.get(file.getAbsolutePath());
            Path backupPath = Paths.get(target.getParent().toString(), name + "." + replacedName + ".bak");
            return backupPath.toString();
        } else {
            return null;
        }
    }
}
