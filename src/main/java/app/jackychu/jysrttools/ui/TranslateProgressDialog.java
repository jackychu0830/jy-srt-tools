package app.jackychu.jysrttools.ui;

import app.jackychu.api.simplegoogletranslate.Language;
import app.jackychu.api.simplegoogletranslate.SimpleGoogleTranslate;
import app.jackychu.jysrttools.JyDraft;
import app.jackychu.jysrttools.JyUtils;
import app.jackychu.jysrttools.exception.JySrtToolsException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TranslateProgressDialog extends JDialog {
    private final JProgressBar progressBar;
    private final JLabel label;
    private final JButton btnOk;
    private final SimpleGoogleTranslate translate = new SimpleGoogleTranslate();

    public TranslateProgressDialog(JFrame frame, boolean modal) {
        super(frame, modal);
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
                Map<String, String> newTexts = new HashMap<>();
                int i = 1;
                int percentage;

                try {
                    Map<String, String> texts = draft.getDraftTexts();
                    for (String txtId : texts.keySet()) {
                        String newText = translate.doTranslate(Language.zh_cn, Language.zh_tw, texts.get(txtId));
                        newTexts.put(txtId, newText);
                        percentage = (int) ((i++ * 1.0) / texts.size() * 100);
                        progressBar.setValue(percentage);
                        label.setText(String.format("翻譯中… %d%%", percentage));
                    }

                    draft.updateDraftInfoTexts(newTexts);
                    JyUtils.saveDraft(draft);

                    label.setText("翻譯完成");
                    progressBar.setValue(100);

                } catch (IOException | InterruptedException | JySrtToolsException e) {
                    JOptionPane.showMessageDialog(null,
                            new ErrorMessagePanel(e.getMessage()), "翻譯失敗", JOptionPane.ERROR_MESSAGE);
                }

                btnOk.setEnabled(true);
                return null;
            }
        };
        sw.execute();
        setVisible(true);
    }
}
