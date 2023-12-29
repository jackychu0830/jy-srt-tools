package app.jackychu.jysrttools;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class Subtitle implements Comparable<Subtitle> {
    private String id;
    private int num;
    private long startTime;
    private long endTime;
    private long duration;
    private String findingText;
    private boolean found;
    private String text;
    private String contentFormat;

    /**
     * Convert time from ms to SRT time string format
     *
     * @param time time in ms
     * @return SRT time
     */
    public static String msToTimeStr(long time) {
        long ms = time / 1000 % 1000;
        long sec = time / 1000 / 1000 % 60;
        long min = time / 1000 / 1000 / 60 % 60;
        long hour = time / 1000 / 1000 / 60 / 60;

        return String.format("%02d:%02d:%02d,%03d", hour, min, sec, ms);
    }

    /**
     * Convert time from STR time string format to ms
     *
     * @param str SRT time
     * @return time in ms
     */
    public static long timeStrToMs(String str) {
        long time = 0;
        String[] t1 = str.split(",");
        String[] t2 = t1[0].split(":");

        time += Long.parseLong(t2[0]) * 60 * 60 * 1000 * 1000;
        time += Long.parseLong(t2[1]) * 60 * 1000 * 1000;
        time += Long.parseLong(t2[2]) * 1000 * 1000;
        time += Long.parseLong(t1[1]) * 1000;

        return time;
    }

    public Subtitle() {

    }

    public Subtitle(String id) {
        this.id = id;
    }
    @Override
    public int compareTo(Subtitle sub) {
        return (int) (this.startTime - sub.startTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtitle subtitle = (Subtitle) o;
        return subtitle.id.equals(this.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, num, text, startTime, endTime, duration);
    }

    public String getFormattedText() {
        try {
            JSONObject content = (JSONObject) (new JSONParser()).parse(this.contentFormat);
            content.put("text", this.text);
            JSONArray range = new JSONArray();
            range.add(0);
            range.add(this.text.length());
            JSONArray styles = (JSONArray) content.get("styles");
            JSONObject style = (JSONObject) styles.get(0);
            style.put("range", range);
            styles.set(0, style);
            content.put("styles", styles);
            return content.toString();
        } catch (ParseException e) {
            e.printStackTrace();
            return this.contentFormat.replace("${text}", this.text);
        }


    }
}
