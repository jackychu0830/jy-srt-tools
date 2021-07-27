package app.jackychu.jysrttools;

import app.jackychu.jysrttools.exception.JySrtToolsException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    // id: text
    private Map<String, String> texts;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private List<String> textIds;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private List<Subtitle> subtitles;

    /**
     * Get all texts in this draft
     *
     * @return Map of texts
     * @throws JySrtToolsException Parse draft info json error
     */
    public Map<String, String> getDraftTexts() throws JySrtToolsException {
        // Force clean exist texts
        this.texts = new HashMap<>();
        this.textIds = new ArrayList<>();

        if (this.info == null) {
            loadJyDraftInfo();
        }

        JSONArray txts = (JSONArray) ((JSONObject) this.info.get("materials")).get("texts");
        for (Object txt : txts.toArray()) {
            Object txtId = ((JSONObject) txt).get("id");
            Object txtObj = ((JSONObject) txt).get("content");
            Object txtType = ((JSONObject) txt).get("type");
            if (txtType.toString().equals("subtitle")) {
                this.texts.put(txtId.toString(), txtObj.toString());
                this.textIds.add(txtId.toString());
            }
        }

        return this.texts;
    }

    /**
     * Get all text ids inorder
     * @return The list of text ids
     * @throws JySrtToolsException Parse draft info json error
     */
    public List<String> getDraftTextIds() throws JySrtToolsException {
        this.getDraftTexts();
        return this.textIds;
    }

    /**
     * Get draft subtitles
     *
     * @return List of subtitle (in time order)
     * @throws JySrtToolsException Parse draft info json error
     */
    public List<Subtitle> getDraftSubtitles() throws JySrtToolsException {
        if (this.texts == null) {
            getDraftTexts();
        }


        List<Subtitle> subtitles = new ArrayList<>();
        JSONArray tracks = (JSONArray) this.info.get("tracks");
        for (Object track : tracks.toArray()) {
            JSONObject tk = (JSONObject) track;
            // only flag=2 and type=text is subtitle
            if (!((tk.get("flag").toString().equals("1") || tk.get("flag").toString().equals("2")) &&
                    tk.get("type").toString().equals("text"))) continue;
            JSONArray segments = (JSONArray) tk.get("segments");
            for (Object segment : segments.toArray()) {
                String materialId = ((JSONObject) segment).get("material_id").toString();
                if (this.texts.containsKey(materialId)) {
                    Subtitle sub = new Subtitle();
                    sub.setId(materialId);
                    sub.setText(this.texts.get(materialId));
                    JSONObject target = (JSONObject) ((JSONObject) segment).get("target_timerange");
                    sub.setDuration(Long.parseLong(target.get("duration").toString()));
                    sub.setStart(Long.parseLong(target.get("start").toString()));
                    subtitles.add(sub);
                }
            }
        }
        Collections.sort(subtitles);

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
     * Update draft text to info object
     *
     * @param texts New texts which want to update
     */
    public void updateDraftInfoTexts(Map<String, String> texts) {
        this.texts = texts;
        JSONArray originTexts;
        originTexts = (JSONArray) ((JSONObject) this.info.get("materials")).get("texts");
        for (Object originText : originTexts.toArray()) {
            String id = ((JSONObject) originText).get("id").toString();
            if (this.texts.containsKey(id)) {
                ((JSONObject) originText).put("content", texts.get(id));
            }
        }
    }

    /**
     * Delete draft subtitles
     *
     * @throws JySrtToolsException Load draft texts fail
     */
    public void deleteDraftSubtitles() throws JySrtToolsException {
        if (this.texts == null) {
            getDraftTexts();
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
                if (this.texts.containsKey(materialId)) {
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
    }
}
