package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.Subtitle;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Objects;

public class SubtitleCellRender extends JLabel implements TableCellRenderer {

    public SubtitleCellRender() {
        super.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Subtitle sub = (Subtitle) value;

        setFont(getFont().deriveFont(16f));

        switch (column) {
            case 0:
                setText(String.valueOf(sub.getNum()));
                break;
            case 1:
                setText(Subtitle.msToTimeStr(sub.getStartTime()));
                break;
            case 2:
                setText(Subtitle.msToTimeStr(sub.getEndTime()));
                break;
            case 3:
                if (sub.getFindingText() != null && !Objects.equals(sub.getFindingText(), "")) {
                    String str = sub.getText().replace(sub.getFindingText(),
                            "<span style=\"background-color:#fff68f;\">" + sub.getFindingText() + "</span>");
                    setText("<html>" + str + "</html>");
                } else {
                    setText(sub.getText());
                }
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
