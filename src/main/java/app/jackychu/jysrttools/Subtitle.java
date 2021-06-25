package app.jackychu.jysrttools;

import lombok.Data;

@Data
public class Subtitle implements Comparable<Subtitle> {
    private String id;
    private String text;
    private long start;
    private long duration;

    @Override
    public int compareTo(Subtitle sub) {
        return (int) (this.start - sub.start);
    }
}
