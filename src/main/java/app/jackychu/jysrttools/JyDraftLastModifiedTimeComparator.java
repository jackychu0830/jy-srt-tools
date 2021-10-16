package app.jackychu.jysrttools;

import java.util.Comparator;

public class JyDraftLastModifiedTimeComparator implements Comparator<JyDraft> {

    @Override
    public int compare(JyDraft o1, JyDraft o2) {
        return Long.compare(o2.getLastModifiedTime(), o1.getLastModifiedTime());
    }

    @Override
    public Comparator<JyDraft> reversed() {
        return Comparator.super.reversed();
    }
}
