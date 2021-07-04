package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JyFont;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;

public class JyFontTableModel extends AbstractTableModel {
    private final List<JyFont> fontList;

    private final String[] columnNames = new String[]{
            "剪映字型", "已替換字型"
    };

    private final Class<FontCell>[] columnClass = new Class[]{
            FontCell.class, FontCell.class
    };

    public JyFontTableModel(List<JyFont> fontList) {
        this.fontList = fontList;
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
        return fontList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        JyFont row = fontList.get(rowIndex);
        if (0 == columnIndex) {
            if (row.isReplaced()) {
                return new FontCell(row.getName(), row.getBackupFont());
            } else {
                return new FontCell(row.getName(), row.getFont());
            }
        } else if (1 == columnIndex) {
            return new FontCell(row.getReplacedName(), row.getFont());
        }
        return null;
    }
}
