package app.jackychu.jysrttools;

import app.jackychu.jysrttools.exception.JySrtToolsException;
import com.github.houbb.heaven.util.util.JsonUtil;
import lombok.*;
import org.apache.commons.text.StringSubstitutor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

/**
 * Jianying video object
 */
@Data
public class JyDraft {
    private String id;
    private String name;
    private JSONObject info;
    private String infoFilename;
    private String coverFilename;
    private String folderPath;
    private long lastModifiedTime;
    private boolean hidden = false;

    @Getter(AccessLevel.NONE)
    private List<Subtitle> subtitles;

    @SneakyThrows
    public List<Subtitle> getSubtitles() {
        if (this.subtitles == null) {
            this.subtitles = new ArrayList<>();
            loadDraftSubtitles();
        }
        return this.subtitles;
    }

    /**
     * Get draft subtitles
     *
     * @return List of subtitle (in time order)
     * @throws JySrtToolsException Parse draft info json error
     */
    public List<Subtitle> loadDraftSubtitles() throws JySrtToolsException {
        if (this.info == null) {
            loadJyDraftInfo();
        }

        Map<String, String> texts = new HashMap<>();
        Map<String, String> contentFormats = new HashMap<>();
        JSONArray txts = (JSONArray) ((JSONObject) this.info.get("materials")).get("texts");
        for (Object txt : txts.toArray()) {
            Object txtId = ((JSONObject) txt).get("id");
            Object txtObj = ((JSONObject) txt).get("content");
            Object txtType = ((JSONObject) txt).get("type");
            if (txtType.toString().equals("subtitle")) {
                try {
                    String originContent = txtObj.toString();
                    JSONObject content = (JSONObject) (new JSONParser()).parse(originContent);
                    texts.put(txtId.toString(), content.get("text").toString());
                    content.put("text", "${text}");
                    contentFormats.put(txtId.toString(), content.toString());
                } catch (ParseException e) {
                    texts.put(txtId.toString(), e.toString());
                }
            }
        }

        JSONArray tracks = (JSONArray) this.info.get("tracks");
        for (Object track : tracks.toArray()) {
            JSONObject tk = (JSONObject) track;
            // only flag=2 and type=text is subtitle
            if (!((tk.get("flag").toString().equals("1") || tk.get("flag").toString().equals("2")) &&
                    tk.get("type").toString().equals("text"))) continue;
            JSONArray segments = (JSONArray) tk.get("segments");
            for (Object segment : segments.toArray()) {
                String materialId = ((JSONObject) segment).get("material_id").toString();
                if (texts.containsKey(materialId)) {
                    Subtitle sub = new Subtitle();
                    sub.setId(materialId);
                    sub.setText(texts.get(materialId));
                    sub.setContentFormat(contentFormats.get(materialId));
                    JSONObject target = (JSONObject) ((JSONObject) segment).get("target_timerange");
                    sub.setDuration(Long.parseLong(target.get("duration").toString()));
                    sub.setStartTime(Long.parseLong(target.get("start").toString()));
                    sub.setEndTime(sub.getStartTime() + sub.getDuration());
                    this.subtitles.add(sub);
                }
            }

            Collections.sort(this.subtitles);
            int index = 1;
            for (Subtitle sub : this.subtitles) {
                sub.setNum(index++);
            }
        }

        return subtitles;
    }

    /**
     * Load draft info from json file
     *
     * @return JSON object of draft info
     * @throws JySrtToolsException Load draft info file error
     */
    public JSONObject loadJyDraftInfo() throws JySrtToolsException {
        try {
            this.info = JyUtils.loadJsonData(this.infoFilename);
        } catch (JySrtToolsException e) {
            throw new JySrtToolsException("讀取草稿內容錯誤! " + System.lineSeparator() + e.getMessage(), e);
        }

        return this.info;
    }

    /**
     * Update draft subtitle to info object
     *
     * @param subtitle New subtitles which want to update
     */
    public void updateDraftSubtitle(Subtitle subtitle) {
        JSONArray originTexts;
        originTexts = (JSONArray) ((JSONObject) this.info.get("materials")).get("texts");
        for (Object originText : originTexts.toArray()) {
            String id = ((JSONObject) originText).get("id").toString();
            int index = this.subtitles.indexOf(new Subtitle(id));
            if (index != -1){
                Subtitle sub = this.subtitles.get(index);
                ((JSONObject) originText).put("content", sub.getFormattedText());
            }
        }
    }

    /**
     * Delete draft subtitles
     *
     * @throws JySrtToolsException Load draft texts fail
     */
    public void deleteDraftSubtitles() throws JySrtToolsException {
        if (this.subtitles == null || this.subtitles.isEmpty()) {
            loadDraftSubtitles();
        }

        // Remove texts content
        JSONArray originTexts;
        originTexts = (JSONArray) ((JSONObject) this.info.get("materials")).get("texts");
        for (Object originText : originTexts.toArray()) {
            if (((JSONObject) originText).get("type").toString().equals("subtitle")) {
                originTexts.remove(originText);
            }
        }

        // Remove tracks
        List<String> extraMaterialIds = new ArrayList<>();
        JSONArray tracks = (JSONArray) this.info.get("tracks");
        for (Object track : tracks.toArray()) {
            JSONObject tk = (JSONObject) track;
            // only flag=2 and type=text is subtitle
            if (!((tk.get("flag").toString().equals("1") || tk.get("flag").toString().equals("2")) &&
                    tk.get("type").toString().equals("text"))) continue;
            JSONArray segments = (JSONArray) tk.get("segments");
            for (Object segment : segments.toArray()) {
                String materialId = ((JSONObject) segment).get("material_id").toString();
                if (this.subtitles.contains(new Subtitle(materialId))) {
                    String extraMaterialId = null;
                    if (((JSONObject) segment).containsKey("extra_material_refs")) {
                        extraMaterialId = ((JSONArray) ((JSONObject) segment).get("extra_material_refs")).get(0).toString();
                    } else if (((JSONObject) segment).containsKey("extra_material_ids")) {
                        extraMaterialId = ((JSONArray) ((JSONObject) segment).get("extra_material_ids")).get(0).toString();
                    }
                    if (!Objects.isNull(extraMaterialId))
                        extraMaterialIds.add(extraMaterialId);
                    segments.remove(segment);
                }
            }
        }

        // Remove extra materials
        JSONArray extraMaterials = (JSONArray) ((JSONObject) this.info.get("materials")).get("material_animations");
        for (Object extra : extraMaterials.toArray()) {
            JSONObject ex = (JSONObject) extra;
            if (extraMaterialIds.contains(ex.get("id").toString())) {
                extraMaterials.remove(ex);
            }
        }

        this.subtitles = null;
    }

    /**
     * Replace/Import new subtitle instead exist one
     *
     * @param newSubtitles new subtitle list
     * @throws JySrtToolsException load subtitle error
     */
    public void replaceDraftSubtitles(List<Subtitle> newSubtitles) throws JySrtToolsException {
        deleteDraftSubtitles();

        JSONParser parser = new JSONParser();

        JSONArray originTexts = (JSONArray) ((JSONObject) this.info.get("materials")).get("texts");
        JSONArray extraMaterials = (JSONArray) ((JSONObject) this.info.get("materials")).get("material_animations");

        String trackTemp = DraftTemplates.getTemplate("draft_track");
        Map<String, String> values = new HashMap<>();
        String trackId = UUID.randomUUID().toString().toUpperCase();
        values.put("id", trackId);
        JSONObject trackObj;
        try {
            trackObj = (JSONObject) parser.parse(StringSubstitutor.replace(trackTemp, values, "${", "}"));
        } catch (ParseException e) {
            throw new JySrtToolsException("新增 SRT 字幕失敗 (Track) " + System.lineSeparator() + e.getMessage(), e);
        }
        JSONArray segments = (JSONArray) trackObj.get("segments");

        for (Subtitle sub : newSubtitles) {
            String extraTemp = DraftTemplates.getTemplate("draft_extra_material");
            values = new HashMap<>();
            String extraId = UUID.randomUUID().toString().toUpperCase();
            values.put("id", extraId);
            String extraStr = StringSubstitutor.replace(extraTemp, values, "${", "}");
            try {
                JSONObject obj = (JSONObject) parser.parse(extraStr);
                extraMaterials.add(obj);
            } catch (ParseException e) {
                throw new JySrtToolsException("新增 SRT 字幕失敗 (Extra Material) " + System.lineSeparator() + e.getMessage(), e);
            }

            String textTemp = DraftTemplates.getTemplate("draft_text");
            values = new HashMap<>();
            values.put("font_path", DraftTemplates.getDefaultFontPath());
            String textId = UUID.randomUUID().toString().toUpperCase();
            values.put("id", textId);
            String textStr = StringSubstitutor.replace(textTemp, values, "${", "}");
            try {
                JSONObject textObj = (JSONObject) parser.parse(textStr);
                textObj.put("content", sub.getFormattedText().replace("${font_path}", DraftTemplates.getDefaultFontPath()));
                originTexts.add(textObj);
            } catch (ParseException e) {
                throw new JySrtToolsException("新增 SRT 字幕失敗 (Text) " + System.lineSeparator() + e.getMessage(), e);
            }

            String segmentTemp = DraftTemplates.getTemplate("draft_segment");
            values = new HashMap<>();
            String segmentId = UUID.randomUUID().toString().toUpperCase();
            values.put("id", segmentId);
            values.put("extra_material_refs", extraId);
            values.put("material_id", textId);
            values.put("duration", String.valueOf(sub.getDuration()));
            values.put("start", String.valueOf(sub.getStartTime()));
            String segmentStr = StringSubstitutor.replace(segmentTemp, values, "${", "}");
            JSONObject segmentObj;
            try {
                segmentObj = (JSONObject) parser.parse(segmentStr);
                segments.add(segmentObj);
            } catch (ParseException e) {
                throw new JySrtToolsException("新增 SRT 字幕失敗 (Segment) " + System.lineSeparator() + e.getMessage(), e);
            }
        }
        JSONArray tracks = (JSONArray) this.info.get("tracks");
        tracks.add(trackObj);

        this.subtitles = null;
    }

    /**
     * Clean current subtitles after translate or import
     */
    public void cleanSubtitles() {
        this.subtitles = null;
    }
}
