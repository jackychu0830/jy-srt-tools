package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JyDraft;
import app.jackychu.jysrttools.JyUtils;
import app.jackychu.jysrttools.Subtitle;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import com.github.houbb.opencc4j.util.ZhTwConverterUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TranslateProgressDialog extends JDialog {
    private final JFrame parent;
    private final JProgressBar progressBar;
    private final JLabel label;
    private final JButton btnOk;

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

    public void doTranslate(String type, JyDraft draft) {
        progressBar.setValue(0);
        label.setText("翻譯中… 0%");
        btnOk.setEnabled(false);

        SwingWorker<Void, Void> sw = new SwingWorker<>() {

            @Override
            protected Void doInBackground() {
                int i = 0;
                int percentage;

                try {
                    java.util.List<Subtitle> newSubs = new ArrayList<>();

                    int size = draft.getSubtitles().size();
                    for (Subtitle sub : draft.getSubtitles()) {
                        percentage = (int) ((++i * 1.0) / size * 100);
                        progressBar.setValue(percentage);
                        label.setText(String.format("翻譯中… %d%%", percentage));

                        String newStr = sub.getText();
                        if (ZhConverterUtil.isTraditional(newStr)) continue;
                        if (type.equals("tcTranslate")) {
                            newStr = ZhConverterUtil.toTraditional(newStr);
                        } else { // twTranslate
                            newStr = ZhTwConverterUtil.toTraditional(newStr);
                        }

                        sub.setText(newStr);
                        draft.updateDraftSubtitle(sub);
                    }

                    // Save translated texts back to Jy draft
                    JyUtils.saveDraft(draft);

                    label.setText("翻譯完畢");
                    progressBar.setValue(100);

                } catch (Throwable e) {
                    setVisible(false);
                    JOptionPane.showMessageDialog(parent,
                            new ErrorMessagePanel(e), "翻譯失敗", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnOk.setEnabled(true);
                }
                return null;
            }
        };

        sw.execute();
        setVisible(true);
    }
}
