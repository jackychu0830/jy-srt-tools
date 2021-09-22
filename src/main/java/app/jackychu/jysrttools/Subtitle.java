package app.jackychu.jysrttools;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class Subtitle implements Comparable<Subtitle> {
    private String id;
    private int num;
    private String text;
    private long startTime;
    private long endTime;
    private long duration;

    @Override
    public int compareTo(Subtitle sub) {
        return (int) (this.startTime - sub.startTime);
    }

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
        return String.format("%02d:%02d:%02d,%s", hour, min, sec,
                StringUtils.rightPad(String.valueOf(ms), 3, "0"));

    }
}
