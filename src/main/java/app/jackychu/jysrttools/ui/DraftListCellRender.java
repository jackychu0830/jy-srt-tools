package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JyDraft;
import org.apache.commons.text.WordUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class DraftListCellRender extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 20));

        JyDraft draft = (JyDraft) value;
        setText("<html><span>" + WordUtils.wrap(draft.getName(), 13, "<br/>", true));

        String icon = draft.getCoverFilename();
        if (icon == null || icon.length() < 1) {
            try {
                Image image = ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("images/default_cover.png")));
                setIcon(new ImageIcon(image));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            setIcon(new ImageIcon(new ImageIcon(icon).getImage().getScaledInstance(320, 180, Image.SCALE_DEFAULT)));
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
