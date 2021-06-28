package app.jackychu.jysrttools;

import app.jackychu.jysrttools.exception.JySrtToolsException;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        } catch (IOException e) {
            throw new JySrtToolsException("開啟草稿檔案錯誤! " + System.lineSeparator() + e.getMessage(), e);
        }

        JSONObject json;
        try {
            json = (JSONObject) jsonParser.parse(reader);
        } catch (IOException | ParseException e) {
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

    /*
     * Find files
     *
     * @param filename        The file's name which you want to find
     * @param searchDirectory Search directory
     * @return List of matched files
     */
//    public static javaxt.io.File[] findFile(String filename, String searchDirectory) {
//        String[] filter = new String[]{filename};
//        Directory directory = new Directory(searchDirectory);
//        List files = directory.getChildren(true, filter, false);
//        Object obj;
//        while (true) {
//            synchronized (files) {
//                while (files.isEmpty()) {
//                    try {
//                        files.wait();
//                    } catch (InterruptedException e) {
//                        break;
//                    }
//                }
//                obj = files.remove(0);
//                files.notifyAll();
//            }
//
//            if (obj == null) {
//                break;
//            } else {
//                if (obj instanceof javaxt.io.File) {
//                    System.out.println(obj);
//                    javaxt.io.File file = (javaxt.io.File) obj;
//                } else if (obj instanceof javaxt.io.Directory) {
//                    javaxt.io.Directory dir = (javaxt.io.Directory) obj;
//                }
//            }
//        }
//        return null;
//        //        javaxt.io.File[] files = directory.getFiles(filter, true);
////        return files;
//    }

    /*
     * Get JianyingPro draft config file real path
     *
     * @return config file path
     * @throws JySrtToolsException Finding draft config file error!
     */
//    public static String getDraftConfigFilePath() throws JySrtToolsException {
//        String filePath = null;
////        String os = System.getProperty("os.name");
////        String dir = os.toLowerCase(Locale.ROOT).contains("windows") ? "c:\\" : "/";
//
////        for (javaxt.io.Directory drive : javaxt.io.Directory.getRootDirectories()){
////            javaxt.io.File[] files = findFile(ROOT_DRAFT_META_INFO_FILENAME, drive.getPath());
//        javaxt.io.File[] files = findFile(ROOT_DRAFT_META_INFO_FILENAME, System.getProperty("user.home"));
//        // Get a real one (not redundant one)
//        // For Mac, the real one will under .../Library/....
//        for (javaxt.io.File file : files) {
//            System.out.println(file.toString());
//            if (file.toString().toLowerCase(Locale.ROOT).contains("library")) {
//                filePath = file.toString();
//            }
//        }
////        }
//
//
//        if (filePath == null) {
//            throw new JySrtToolsException("Cannot find draft config file.");
//        }
//        return filePath;
//    }
}