package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JyDraft;
import app.jackychu.jysrttools.exception.JySrtToolsException;

import javax.swing.*;
import java.awt.*;

public class DraftTextsPanel extends JPanel {
    private JLabel label;
    private JTextArea textArea;

    public DraftTextsPanel() {
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        label = new JLabel("<html><span style='font-size:20px'>步驟二: 檢視草稿文字內容</span></html>", JLabel.LEFT);
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(textArea.getFont().deriveFont(16f));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        setLayout(new BorderLayout());
        add(label, BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

    }

    public void setTexts(JyDraft draft) throws JySrtToolsException {
        textArea.setText("");

        if (draft == null) return;
        StringBuilder sb = new StringBuilder();
        for (String text : draft.getDraftTexts().values()) {
            sb.append(text).append(System.lineSeparator());
        }
        textArea.setText(sb.toString());
    }
}
