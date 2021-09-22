package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.Subtitle;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class DraftSubtitleTableModel extends DefaultTableModel {
    private final List<Subtitle> subtitles;

    private final String[] columnNames = new String[]{
            "編號", "開始時間", "結束時間", "字幕"
    };

    private final Class<Subtitle>[] columnClass = new Class[]{
            Subtitle.class, Subtitle.class, Subtitle.class, Subtitle.class
    };

    public DraftSubtitleTableModel(List<Subtitle> subtitles) {
        this.subtitles = subtitles;
    }

    public List<Subtitle> getSrtList() {
        return subtitles;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClass[columnIndex];
    }

    @Override
    public int getRowCount() {
        if (subtitles == null) return 0;
        return subtitles.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return subtitles.get(rowIndex);
    }
}
