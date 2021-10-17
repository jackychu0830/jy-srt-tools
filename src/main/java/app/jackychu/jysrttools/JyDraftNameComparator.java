package app.jackychu.jysrttools;

import java.util.Comparator;

public class JyDraftNameComparator implements Comparator<JyDraft> {

    @Override
    public int compare(JyDraft o1, JyDraft o2) {
        int res = String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
        if (res == 0) {
            res = o1.getName().compareTo(o2.getName());
        }
        return res;
    }

    @Override
    public Comparator<JyDraft> reversed() {
        return Comparator.super.reversed();
    }
}
