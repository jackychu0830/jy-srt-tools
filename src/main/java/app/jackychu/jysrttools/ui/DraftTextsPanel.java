package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JyDraft;
import app.jackychu.jysrttools.JySrtTools;
import app.jackychu.jysrttools.exception.JySrtToolsException;

import javax.swing.*;
import java.awt.*;

public class DraftTextsPanel extends JPanel {
    private final JySrtTools jySrtTools;
    private final JTextArea textArea;

    public DraftTextsPanel(JySrtTools jySrtTools) {
        this.jySrtTools = jySrtTools;
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel label = new JLabel("<html><span style='font-size:20px'>步驟二: 檢視草稿字幕內容</span></html>", JLabel.LEFT);
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(textArea.getFont().deriveFont(20f));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        setLayout(new BorderLayout());
        add(label, BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

    }

    public void setTexts(JyDraft draft) throws JySrtToolsException {
        textArea.setText("");
        // jySrtTools.getJyFontPanel().setText(null);

        if (draft == null) return;
        StringBuilder sb = new StringBuilder();
        if (draft.getDraftTextIds().size() == 0) {
            sb.append("此草稿沒有字幕!")
                    .append(System.lineSeparator())
                    .append("請先在剪映裡執行")
                    .append(System.lineSeparator())
                    .append("\"文本 -> 智能字幕 -> 识别字幕\" 功能");
            jySrtTools.getJyTextPanel().getActionPanel().enableButtons(false);
        } else {
            for (String id : draft.getDraftTextIds()) {
                sb.append(draft.getDraftTexts().get(id)).append(System.lineSeparator());
            }
            jySrtTools.getJyTextPanel().getActionPanel().enableButtons(true);
        }
        textArea.setText(sb.toString());
        textArea.setCaretPosition(0);
        // jySrtTools.getJyFontPanel().setText(sb.toString());
    }
}
