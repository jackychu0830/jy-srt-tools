package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.Subtitle;
import lombok.Getter;
import lombok.Setter;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Objects;

public class DraftSubtitleTableModel extends DefaultTableModel {
    @Getter
    private final List<Subtitle> subtitles;
    private final String[] columnNames = new String[]{
            "編號", "開始時間", "結束時間", "字幕"
    };
    private final Class<Subtitle>[] columnClass = new Class[]{
//            Integer.class, String.class, String.class, String.class
            Subtitle.class, Subtitle.class, Subtitle.class, Subtitle.class
    };
    @Getter
    @Setter
    private boolean dirty;

    public DraftSubtitleTableModel(List<Subtitle> subtitles) {
        this.subtitles = subtitles;
        this.dirty = false;
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
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return subtitles.get(rowIndex);
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Subtitle sub = subtitles.get(rowIndex);
        if (columnIndex == 3) {
            if (!Objects.equals(sub.getText(), value.toString())) {
                this.dirty = true;
                sub.setText(value.toString());
            }
        }
    }
}
