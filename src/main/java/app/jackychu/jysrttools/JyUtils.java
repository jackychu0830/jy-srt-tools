package app.jackychu.jysrttools;

import app.jackychu.jysrttools.exception.JySrtToolsException;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
     * Get jr-srt-tools version number
     *
     * @return version number
     */
    public static String getVersion() {
        String version = "N/A";
        try {
            InputStream input = JyUtils.class.getClassLoader().getResourceAsStream("config.properties");
            Properties prop = new Properties();
            prop.load(input);
            version = prop.getProperty("version");
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        return version;
    }

    /**
     * Get JY version number
     *
     * @return JY version number
     */
    public static String getJyVersion() {
        String jyVersion = "N/A";

        String os = System.getProperty("os.name");
        try {
            String[] cmd;
            if (os.toLowerCase(Locale.ROOT).contains("windows")) {
                InputStream input = JyUtils.class.getClassLoader().getResourceAsStream("config.properties");
                Properties prop = new Properties();
                prop.load(input);
                return prop.getProperty("jy_version");
            } else {
                cmd = new String[]{"/usr/bin/bash", "mdls -name kMDItemVersion /Applications/VideoFusion-macOS.app | awk -F'\"' '{print $2}'"};
            }
            Process process = new ProcessBuilder(cmd)
                    .redirectErrorStream(true)
                    .start();

            ArrayList<String> output = new ArrayList<>();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null)
                output.add(line);

            //There should really be a timeout here.
            if (0 != process.waitFor()) {
                jyVersion = "N/A";
            }

            jyVersion = output.toString();
        } catch (IOException | InterruptedException e) {
            try {
                InputStream input = JyUtils.class.getClassLoader().getResourceAsStream("config.properties");
                Properties prop = new Properties();
                prop.load(input);
                jyVersion = prop.getProperty("jy_version");
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }

        return jyVersion;
    }

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
            return home + "\\AppData\\Local\\JianyingPro\\User Data\\Resources\\Font";
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
            System.out.printf(findCmd + "%n", appName);
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
    public static List<JyDraft> getAllJyDrafts() throws JySrtToolsException {
        List<JyDraft> drafts = new ArrayList<>();
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
            jyDraft.setLastModifiedTime(Long.parseLong(draft.get("tm_draft_modified").toString()));

            drafts.add(jyDraft);
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
        if (!(new File(filename)).exists()) {
            throw new JySrtToolsException("開啟草稿檔案錯誤! " + System.lineSeparator() + "請新增剪映草稿後，再使用工具箱!");
        }

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
     * Get JianyingPro draft subtitles in SRT format
     *
     * @param draft JianyingPro draft object
     * @throws JySrtToolsException get SRT format fail
     */
    public static String getDraftSubtitlesSRT(JyDraft draft) throws JySrtToolsException {
        List<Subtitle> subtitles = draft.getDraftSubtitles();
        StringBuilder srt = new StringBuilder();

        for (Subtitle sub : subtitles) {
            // fix time overlap
            if (sub.getNum() > 1) {
                Subtitle preSub = subtitles.get(sub.getNum() - 2); // list index start from 0
                if (sub.getStartTime() < preSub.getEndTime()) {
                    sub.setStartTime(preSub.getEndTime());
                }
            }

            srt.append(sub.getNum())
                    .append("\n")
                    .append(Subtitle.msToTimeStr(sub.getStartTime()))
                    .append(" --> ")
                    .append(Subtitle.msToTimeStr(sub.getEndTime()))
                    .append("\n")
                    .append(sub.getText())
                    .append("\n\n");
        }

        return srt.toString();
    }

    /**
     * Get JianyingPro draft subtitles in txt format
     *
     * @param draft JianyingPro draft object
     * @throws JySrtToolsException get TXT format fail
     */
    public static String getDraftSubtitlesTxt(JyDraft draft) throws JySrtToolsException {
        StringBuilder sb = new StringBuilder();
        for (String id : draft.getDraftTextIds()) {
            sb.append(draft.getDraftTexts().get(id)).append(System.lineSeparator());
        }
        return sb.toString();
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
     * @param filename SRT filename
     * @throws JySrtToolsException Export SRT file fail.
     */
    public static void exportToFile(JyDraft draft, String filename, String type) throws JySrtToolsException {
        String data;
        if (type.equals("srt")) {
            data = getDraftSubtitlesSRT(draft);
        } else { // txt
            data = getDraftSubtitlesTxt(draft);
        }
        try (FileWriter file = new FileWriter(filename, StandardCharsets.UTF_8)) {
            file.write(data);
        } catch (IOException e) {
            throw new JySrtToolsException("匯出 " + type + " 檔案錯誤! " + draft.getName() + System.lineSeparator() + e.getMessage(), e);
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

    /**
     * Load subtitles from srt file to JY draft
     *
     * @param filename SRT file name
     * @return subtitle lise
     * @throws JySrtToolsException load srt file error
     */
    public static List<Subtitle> loadSrtFile(String filename) throws JySrtToolsException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader;
        try {
            reader = new FileReader(filename, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new JySrtToolsException("開啟 SRT 檔案錯誤! " + System.lineSeparator() + e.getMessage(), e);
        }

        Subtitle sub = null;
        int subLineCount = 1;
        List<Subtitle> subtitles = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(reader)) {
            for (String line; (line = br.readLine()) != null; ) {

                // some srt file with wrong encoding (UTF-8 BOM).
                // This is how to remove extra character which not belong to UTF-8
                line = line.replace("\uFEFF", "");
                int num = StringUtils.isNumeric(line) ? Integer.parseInt(line) : -1;
                if (num != -1) { //number
                    sub = new Subtitle();
                    subLineCount = 1;
                    sub.setNum(num);
                } else {
                    if (subLineCount == 1) { //time
                        subLineCount = 2;
                        String[] time = line.trim().split(" --> ");
                        sub.setStartTime(Subtitle.timeStrToMs(time[0]));
                        sub.setEndTime(Subtitle.timeStrToMs(time[1]));
                        sub.setDuration(sub.getEndTime() - sub.getStartTime());
                    } else if (subLineCount == 2) { //text
                        sub.setText(line.trim());
                        subtitles.add(sub);
                        subLineCount = 3;
                    } // else empty line
                }
            }

            return subtitles;
        } catch (IOException | NullPointerException e) {
            throw new JySrtToolsException("解析 SRT 檔案錯誤! " + System.lineSeparator() + e.getMessage(), e);
        }
    }

}