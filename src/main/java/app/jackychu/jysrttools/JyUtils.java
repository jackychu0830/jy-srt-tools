package app.jackychu.jysrttools;

import app.jackychu.jysrttools.exception.JySrtToolsException;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

/**
 * Some utils for JY video
 */
public class JyUtils {
    final static String ROOT_DRAFT_META_INFO_FILENAME = "root_draft_meta_info.json";

    /**
     * Get JY video config path by OS (Mac, Windows)
     *
     * @return JY draft path
     */
    public static String getPath() {
        String os = System.getProperty("os.name");
        String home = System.getProperty("user.home");
        if (os.toLowerCase(Locale.ROOT).contains("windows"))
            return home + "\\AppData\\Local\\JianyingPro\\User Data\\Projects\\com.lveditor.draft";
        else
            return home + "/Library/Containers/com.lemon.lvpro/Data/Movies/JianyingPro/User Data/Projects/com.lveditor.draft";
    }

    /**
     * Get JianyingPro fonts path
     *
     * @return JianyingPro fonts path
     */
    public static String getJyFontsPath() {
        String os = System.getProperty("os.name");
        String home = System.getProperty("user.home");
        if (os.toLowerCase(Locale.ROOT).contains("windows")) {
            return home+ "\\AppData\\Local\\JianyingPro\\User Data\\Resources\\Font";
        } else {
            return "/Applications/VideoFusion-macOS.app/Contents/Resources/Font";
        }
    }

    /**
     * Find JianyingPro app location.
     * But it is too slow. The find command taks 30 seconds.
     *
     * @return JianyingPro app path
     * @throws JySrtToolsException Cannot find JianyingPro app
     */
    public static String findJyAppPath() throws JySrtToolsException {
        String appName = "VideoFusion-macOS.app";
        String findCmd = "find / -name %s";
        String appPath = null;

        String os = System.getProperty("os.name");
        if (os.toLowerCase(Locale.ROOT).contains("windows")) {
            appName = "";
            findCmd = "";
        }

        try {
            System.out.println(String.format(findCmd, appName));
            Process process = new ProcessBuilder("find", "/", "-name", appName).start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                appPath = line;
            }
            reader.close();

            if (appPath == null) {
                throw new JySrtToolsException("無法找到剪映主程式!");
            }

            return appPath;
        } catch (IOException e) {
            throw new JySrtToolsException("無法找到剪映主程式! " + System.lineSeparator() + e.getMessage(), e);
        }
    }

    /**
     * Get all draft projects
     *
     * @return All drafts name and its fold path
     * @throws JySrtToolsException Parse json file error
     */
    public static Map<String, JyDraft> getAllJyDrafts() throws JySrtToolsException {
        Map<String, JyDraft> drafts = new HashMap<>();
        JSONObject draftJson = loadJsonData(
                String.valueOf(Path.of(getPath(), ROOT_DRAFT_META_INFO_FILENAME).toAbsolutePath()));
        JSONArray allDraftStore = (JSONArray) draftJson.get("all_draft_store");
        for (Object obj : allDraftStore) {
            JSONObject draft = (JSONObject) obj;

            JyDraft jyDraft = new JyDraft();
            jyDraft.setName(draft.get("draft_name").toString());
            jyDraft.setId(draft.get("draft_id").toString());
            jyDraft.setFolderPath(draft.get("draft_fold_path").toString());
            jyDraft.setInfoFilename(draft.get("draft_json_file").toString());
            jyDraft.setCoverFilename(draft.get("draft_cover").toString());

            drafts.put(jyDraft.getName(), jyDraft);
        }

        return drafts;
    }

    /**
     * Load JianyingPro draft data fronm json file
     *
     * @param filename json filename
     * @return JSON object
     * @throws JySrtToolsException Open/parse json file error
     */
    public static JSONObject loadJsonData(String filename) throws JySrtToolsException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader;
        try {
            reader = new FileReader(filename, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new JySrtToolsException("開啟草稿檔案錯誤! " + System.lineSeparator() + e.getMessage(), e);
        }

        JSONObject json;
        try {
            json = (JSONObject) jsonParser.parse(reader);
        } catch (Exception e) {
            throw new JySrtToolsException("分析草稿內容錯誤! " + System.lineSeparator() + e.getMessage(), e);
        }

        return json;
    }

    /**
     * Convert time from ms to SRT time string format
     *
     * @param time time in ms
     * @return SRT time
     */
    private static String msToTimeStr(long time) {
        long ms = time / 1000 % 1000;
        long sec = time / 1000 / 1000 % 60;
        long min = time / 1000 / 1000 / 60 % 60;
        long hour = time / 1000 / 1000 / 60 / 60;
        return String.format("%02d:%02d:%02d,%s", hour, min, sec,
                StringUtils.rightPad(String.valueOf(ms), 3, "0"));

    }

    /**
     * Get JianyingPro draft subtitles in SRT format
     *
     * @param draft JianyingPro draft object
     * @throws JySrtToolsException get SRt format fail
     */
    public static String getDraftSubtitlesSRT(JyDraft draft) throws JySrtToolsException {
        List<Subtitle> subtitles = draft.getDraftSubtitles();
        StringBuilder srt = new StringBuilder();
        int count = 1;

        for (Subtitle sub : subtitles) {
            srt.append(count++)
                    .append("\n")
                    .append(msToTimeStr(sub.getStart()))
                    .append(" --> ")
                    .append(msToTimeStr(sub.getStart() + sub.getDuration()))
                    .append("\n")
                    .append(sub.getText())
                    .append("\n\n");
        }

        return srt.toString();
    }

    /**
     * Save JyDraft object back to JianyingPro draft info json file
     *
     * @param draft JyDraft object
     * @throws JySrtToolsException save draft info json file fail
     */
    public static void saveDraft(JyDraft draft) throws JySrtToolsException {
        try (FileWriter file = new FileWriter(draft.getInfoFilename(), StandardCharsets.UTF_8)) {
            file.write(draft.getInfo().toJSONString());
        } catch (IOException e) {
            throw new JySrtToolsException("儲存草稿資料錯誤! " + draft.getName() + System.lineSeparator() + e.getMessage(), e);
        }
    }

    /**
     * Export JianyingPro draft subtitles to SRT file
     *
     * @param draft    JyDraft object
     * @param filename SRt filename
     * @throws JySrtToolsException Export SRT file fail.
     */
    public static void exportToSrt(JyDraft draft, String filename) throws JySrtToolsException {
        String srt = getDraftSubtitlesSRT(draft);
        try (FileWriter file = new FileWriter(filename, StandardCharsets.UTF_8)) {
            file.write(srt);
        } catch (IOException e) {
            throw new JySrtToolsException("匯出 SRT 檔案錯誤s! " + draft.getName() + System.lineSeparator() + e.getMessage(), e);
        }
    }

    /**
     * Get all JianyingPro fonts
     *
     * @return JianyingPro font list
     * @throws JySrtToolsException Load font error
     */
    public static List<JyFont> getAllJyFonts() throws JySrtToolsException {
        File[] files = new File(getJyFontsPath()).listFiles((f, name) -> name.endsWith(".ttf") || name.endsWith(".otf"));
        Arrays.sort(Objects.requireNonNull(files), (a, b) -> -a.getName().compareTo(b.getName()));

        // The bak file name is original_fontname.replaced_fontname.bak
        String[] bak = new File(getJyFontsPath()).list((f, name) -> name.endsWith(".bak"));
        Map<String, String> bakFiles = new HashMap<>();
        for (String b : Objects.requireNonNull(bak)) {
            String fontName = b.split("\\.")[0];
            String replacedName = b.split("\\.")[1];
            bakFiles.put(fontName, replacedName);
        }
        Set<String> bakList = bakFiles.keySet();

        List<JyFont> fonts = new ArrayList<>();
        for (File f : Objects.requireNonNull(files)) {
            String name = f.getName().split("\\.")[0];
            if (bakList.contains(name)) {
                fonts.add(new JyFont(f, bakFiles.get(name)));
            } else {
                fonts.add(new JyFont(f));
            }
        }

        return fonts;
    }
}