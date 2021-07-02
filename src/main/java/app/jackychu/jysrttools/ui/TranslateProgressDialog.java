package app.jackychu.jysrttools.ui;

import app.jackychu.api.simplegoogletranslate.Language;
import app.jackychu.api.simplegoogletranslate.SimpleGoogleTranslate;
import app.jackychu.jysrttools.JyDraft;
import app.jackychu.jysrttools.JyUtils;
import app.jackychu.jysrttools.exception.JySrtToolsException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collector;

public class TranslateProgressDialog extends JDialog {
    private final JFrame parent;
    private final JProgressBar progressBar;
    private final JLabel label;
    private final JButton btnOk;
    private final SimpleGoogleTranslate translate = new SimpleGoogleTranslate();

    public TranslateProgressDialog(JFrame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        setSize(300, 150);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        label = new JLabel("翻譯中… 0%", JLabel.CENTER);
        label.setFont(label.getFont().deriveFont(20f));
        add(label, BorderLayout.NORTH);

        progressBar = new JProgressBar(0, 100);
        progressBar.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        progressBar.setValue(0);
        add(progressBar, BorderLayout.CENTER);

        btnOk = new JButton("完成");
        btnOk.setEnabled(false);
        btnOk.addActionListener(e ->
                ((JButton) e.getSource()).getParent().getParent().getParent().getParent().setVisible(false));
        add(btnOk, BorderLayout.SOUTH);
    }

    public void doTranslate(JyDraft draft) {
        progressBar.setValue(0);
        label.setText("翻譯中… 0%");
        btnOk.setEnabled(false);

        SwingWorker<Void, Void> sw = new SwingWorker<>() {

            @Override
            protected Void doInBackground() {
                int i = 0;
                int limit = 100;
                int percentage;

                try {
                    Map<String, String> texts = draft.getDraftTexts();

                    // Split large texts and every 100 texts concat into one string with delimiter |||
                    List<String> str = new ArrayList<>();
                    StringBuilder sb = new StringBuilder();
                    int start = 0;
                    for (String text : texts.values()) {
                        if (start < limit - 1) {
                            sb.append(text).append("|||");
                            start++;
                        } else {
                            start = 0;
                            sb.append(text);
                            str.add(sb.toString());
                            sb = new StringBuilder();
                        }
                    }
                    str.add(sb.toString());

                    // Translate new text string and split it out back to normal text string
                    List<String> newStr = new ArrayList<>();
                    for (String s : str) {
                        String ns = translate.doTranslate(Language.zh_cn, Language.zh_tw, s);
                        // mysterious issue. Sometime the delimiter ||| will becomes | || after translated
                        ns = ns.replaceAll("\\s", "");
                        for (String ss : ns.split("\\|\\|\\|")) {
                            percentage = (int) ((++i * 1.0) / texts.size() * 100);
                            progressBar.setValue(percentage);
                            label.setText(String.format("翻譯中… %d%%", percentage));
                            newStr.add(ss);
                        }
                    }

                    // Mapping new string with origin text id
                    label.setText("翻譯完成，處理中...");
                    Map<String, String> newTexts = new HashMap<>();
                    i = 0;
                    for (String txtId : texts.keySet()) {
                        newTexts.put(txtId, newStr.get(i++));
                    }

                    // Save translated texts back to Jy draft
                    draft.updateDraftInfoTexts(newTexts);
                    JyUtils.saveDraft(draft);

                    label.setText("翻譯完畢");
                    progressBar.setValue(100);

                } catch (IOException | InterruptedException | JySrtToolsException e) {
                    JOptionPane.showMessageDialog(parent,
                            new ErrorMessagePanel(e), "翻譯失敗", JOptionPane.ERROR_MESSAGE);
                }

                btnOk.setEnabled(true);
                return null;
            }
        };
        sw.execute();
        setVisible(true);
    }
}
