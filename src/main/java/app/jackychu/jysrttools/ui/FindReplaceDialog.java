package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JySrtTools;
import app.jackychu.jysrttools.Subtitle;
import app.jackychu.jysrttools.exception.JySrtToolsException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Objects;

public class FindReplaceDialog extends JDialog {
    private final JFrame parent;
    private final JTextField findTextField;
    private final JButton findButton;
    private final JButton clearButton;
    private final JButton replaceButton;
    private final JButton replaceAllButton;
    private final JPanel replacePanel;
    private JTextField replaceTextField;

    public FindReplaceDialog(JFrame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;

//        setSize(400, 150);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new GridLayout(2, 1));
        setTitle("尋找");

        JPanel findPanel = new JPanel();
        findPanel.setLayout(new BorderLayout());
        findPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
        findPanel.add(new JLabel("<html><span style='font-size:14px'> 尋找字串: </span></html>"), BorderLayout.WEST);
        findTextField = new JTextField();
        findTextField.setFont(findTextField.getFont().deriveFont(14f));
        findTextField.setMinimumSize(new Dimension(200, 36));
        findPanel.add(findTextField, BorderLayout.CENTER);
        findButton = new JButton("<html><span style='font-size:14px'>尋找</span></html>");
        clearButton = new JButton("<html><span style='font-size:14px'>清除尋找</span></html>");
        addButtonsListener(findButton);
        addButtonsListener(clearButton);
        JPanel buttonPanel1 = new JPanel();
        buttonPanel1.setLayout(new GridLayout(1, 2));
        buttonPanel1.add(findButton);
        buttonPanel1.add(clearButton);
        findPanel.add(buttonPanel1, BorderLayout.EAST);
        add(findPanel);

        replacePanel = new JPanel();
        replacePanel.setPreferredSize(new Dimension(600, 36));
        replacePanel.setLayout(new BorderLayout());
        replacePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
        replacePanel.add(new JLabel("<html><span style='font-size:14px'> 替換字串: </span></html>"), BorderLayout.WEST);
        replaceTextField = new JTextField();
        replaceTextField.setFont(replaceTextField.getFont().deriveFont(14f));
        replacePanel.add(replaceTextField, BorderLayout.CENTER);
        replaceButton = new JButton("<html><span style='font-size:14px'>替換</span></html>");
        replaceAllButton = new JButton("<html><span style='font-size:14px'>全部替換</span></html>");
        addButtonsListener(replaceButton);
        addButtonsListener(replaceAllButton);
        JPanel buttonPanel2 = new JPanel();
        buttonPanel2.setLayout(new GridLayout(1, 2));
        buttonPanel2.add(replaceButton);
        buttonPanel2.add(replaceAllButton);
        replacePanel.add(buttonPanel2, BorderLayout.EAST);
        add(replacePanel);
        replacePanel.setVisible(false);

        getRootPane().registerKeyboardAction(e -> this.setVisible(false),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

    }

    public void setReplaceMode(boolean replaceMode) {
        replacePanel.setVisible(replaceMode);
        if (replaceMode) {
            this.setTitle("尋找 & 替換");
        } else {
            this.setTitle("尋找");
        }
        this.pack();
    }

    private void addButtonsListener(JButton btn) {
        btn.addActionListener(e -> {
            if (e.getSource() == clearButton) {
                findTextField.setText("");
                replaceTextField.setText("");
            }

            String str = findTextField.getText();
            JySrtTools jySrtTools = (JySrtTools) parent;
            JTable tbl = jySrtTools.getJyTextPanel().getTextsPanel().getSubtitleTable();
            int index = tbl.getSelectedRow();

            List<Subtitle> subtitles;
            try {
                subtitles = jySrtTools.getCurrentSelectedDraft().getDraftSubtitles();
                int selectedIndex = tbl.getSelectedRow();
                if (e.getSource() == replaceButton) {
                    if (selectedIndex != -1 && !Objects.equals(str, "")) {
                        Subtitle sub = subtitles.get(selectedIndex);
                        sub.setText(sub.getText().replace(str, replaceTextField.getText()));
                        sub.setFound(false);
                        tbl.clearSelection();
                        jySrtTools.getJyTextPanel().getTextsPanel().saveSubtitleChanges(sub);
                    }
                } else if (e.getSource() == replaceAllButton && selectedIndex != -1) {
                    for (Subtitle sub : subtitles) {
                        sub.setText(sub.getText().replace(str, replaceTextField.getText()));
                        sub.setFound(false);
                        jySrtTools.getJyTextPanel().getTextsPanel().saveSubtitleChanges(sub);
                    }
                }

                for (Subtitle sub : subtitles) {
                    sub.setFindingText(str);
                    sub.setFound(sub.getText().contains(str));
                }
                index = selectNextFoundRow(index, subtitles);
                jySrtTools.getJyTextPanel().getTextsPanel().setSubtitles(jySrtTools.getCurrentSelectedDraft());
                if (index != -1 && !Objects.equals(str, "")) {
                    tbl.setRowSelectionInterval(index, index);
                    tbl.scrollRectToVisible(tbl.getCellRect(index, 0, true));
                }
            } catch (JySrtToolsException ex) {
                JOptionPane.showMessageDialog(jySrtTools,
                        new ErrorMessagePanel(ex), "無法尋找", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private int selectNextFoundRow(int index, List<Subtitle> subtitles) {
        for (int i = index + 1; i < subtitles.size(); i++) {
            Subtitle sub = subtitles.get(i);
            if (sub.isFound()) {
                return i;
            }
        }

        return -1;
    }
}
