package app.jackychu.jysrttools.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class DraftActionPanel extends JPanel {
    private JLabel label;
    private final Map<String, JButton> buttons = new HashMap<>();

    public void init() {
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 20));
        setLayout(new BorderLayout());

        label = new JLabel("<html><span style='font-size:20px'>步驟三: 執行功能</span></html>", JLabel.LEFT);

        JPanel panel = new JPanel();
        JButton btnTranslate = new JButton("<html><span style='font-size:16px'>简体轉繁體</span></html>");
        JButton btnExport = new JButton("<html><span style='font-size:16px'>輸出 SRT 檔</span></html>");
        JButton btnRemove = new JButton("<html><span style='font-size:16px'>清除字幕</span></html>");
        btnTranslate.setEnabled(false);
        btnExport.setEnabled(false);
        btnRemove.setEnabled(false);
        buttons.put("translate", btnTranslate);
        buttons.put("export", btnExport);
        buttons.put("remove", btnRemove);
        panel.setLayout(new GridLayout(3, 1));
        panel.add(btnTranslate);
        panel.add(btnExport);
        panel.add(btnRemove);

        add(label, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }

    public void setButtonActionListener(String button, ActionListener listener) {
        buttons.get(button).addActionListener(listener);
    }

    public void enableButtons(boolean enable) {
        for (JButton btn : buttons.values()) {
            btn.setEnabled(enable);
        }
    }
}
