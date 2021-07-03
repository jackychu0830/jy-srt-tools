package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JyFont;

import javax.swing.*;
import java.awt.*;

public class JyFontListCellRender extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 20));

        JyFont font = (JyFont) value;
        setText(font.getName());
        setFont(font.getFont());

        if (font.isReplaced()) {
            setText(font.getName() + " (替換成: " + font.getReplacedName() + ")");
        }

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }
}
