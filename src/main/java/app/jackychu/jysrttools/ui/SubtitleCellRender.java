package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.Subtitle;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class SubtitleCellRender extends JLabel implements TableCellRenderer {

    public SubtitleCellRender() {
        super.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Subtitle srt = (Subtitle) value;

        setFont(getFont().deriveFont(16f));

        switch (column) {
            case 0:
                setText(String.valueOf(srt.getNum()));
                break;
            case 1:
                setText(Subtitle.msToTimeStr(srt.getStartTime()));
                break;
            case 2:
                setText(Subtitle.msToTimeStr(srt.getEndTime()));
                break;
            case 3:
                setText(srt.getText());
        }

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            if (row % 2 == 0) {
                setBackground(table.getBackground());
            } else {
                setBackground(new Color(220, 250, 250));
            }
            setForeground(table.getForeground());
        }

        return this;
    }
}
