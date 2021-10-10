package app.jackychu.jysrttools;

import app.jackychu.jysrttools.ui.ErrorMessagePanel;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;

public class DraftTemplates {
    public static final String MAC_DEFAULT_FONT_PATH = "/Applications/VideoFusion-macOS.app/Contents/Resources/Font/SystemFont/zh-hans.ttf";
    public static final String WIN_DEFAULT_FONT_PATH = "${user_home}/AppData/Local/JianyingPro/Apps/${jy_version}/Resources/Font/SystemFont/zh-hans.ttf";

    public static String getTemplate(String templateName) {
        String temp = null;
        try {
            String os = System.getProperty("os.name");
            if (os.toLowerCase(Locale.ROOT).contains("windows")) {
                InputStream in = DraftTemplates.class.getClassLoader().getResourceAsStream("draft_templates/" + templateName + ".json");
                byte[] data = in.readAllBytes();
                temp = new String(data);
            } else {
                temp = Files.readString(Paths.get(Objects.requireNonNull(DraftTemplates.class.getClassLoader().getResource("draft_templates/" + templateName + ".json").toURI())),
                        StandardCharsets.UTF_8);
            }
        } catch (IOException | URISyntaxException | RuntimeException e) {
            JOptionPane.showMessageDialog(null,
                    new ErrorMessagePanel(e), "讀取字幕模版失敗", JOptionPane.ERROR_MESSAGE);
        }

        return temp;
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
