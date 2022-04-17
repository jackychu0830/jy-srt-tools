package app.jackychu.jysrttools;

import app.jackychu.jysrttools.ui.ErrorMessagePanel;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class DraftTemplates {
    public static final String MAC_DEFAULT_FONT_PATH = "/Applications/VideoFusion-macOS.app/Contents/Resources/Font/SystemFont/zh-hans.ttf";
    public static final String WIN_DEFAULT_FONT_PATH = "${user_home}/AppData/Local/JianyingPro/Apps/${jy_version}/Resources/Font/SystemFont/zh-hans.ttf";

    public static String getTemplate(String templateName) {
        String temp = null;
        try {
            InputStream in = DraftTemplates.class.getClassLoader().getResourceAsStream("draft_templates/" + templateName + ".json");
            byte[] data = in.readAllBytes();
            temp = new String(data);
        } catch (IOException | RuntimeException e) {
            JOptionPane.showMessageDialog(null,
                    new ErrorMessagePanel(e), "讀取字幕模版失敗", JOptionPane.ERROR_MESSAGE);
        }

        return temp;
    }

    public static String getDefaultSubtitleFormat() {
        String os = System.getProperty("os.name");
        String fontPath = null;
        if (os.toLowerCase(Locale.ROOT).contains("windows")) {
            fontPath = WIN_DEFAULT_FONT_PATH;
        } else {
            fontPath = MAC_DEFAULT_FONT_PATH;
        }

        String format = "${text}";
        try {
            InputStream in = DraftTemplates.class.getClassLoader().getResourceAsStream("draft_templates/subtitle_format.txt");
            byte[] data = in.readAllBytes();
            format = new String(data);
            format = format.replace("${font_path}", fontPath);
        } catch (IOException | RuntimeException e) {
            JOptionPane.showMessageDialog(null,
                    new ErrorMessagePanel(e), "讀取預設字幕格式失敗", JOptionPane.ERROR_MESSAGE);
        }

        return format;
    }

    public static String getDefaultFontPath() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase(Locale.ROOT).contains("windows")) {
            return WIN_DEFAULT_FONT_PATH;
        } else {
            return MAC_DEFAULT_FONT_PATH;
        }
    }
}
