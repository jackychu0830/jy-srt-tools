package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JySrtTools;
import app.jackychu.jysrttools.Subtitle;
import app.jackychu.jysrttools.exception.JySrtToolsException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FindReplaceDialog extends JDialog {
    private final JFrame parent;
    private final JTextField findTextField;
    private final JButton findButton;
    private final JButton clearButton;
    private final JButton replaceButton;
    private final JButton replaceAllButton;
    private final JPanel replacePanel;
    private final JTextField replaceTextField;
    private boolean replaceMode = false;

    public FindReplaceDialog(JFrame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;

        setLocationRelativeTo(null);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
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

        String os = System.getProperty("os.name");
        int cmdKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(); //KeyEvent.VK_META;
        if (os.toLowerCase(Locale.ROOT).contains("windows")) {
            cmdKey = KeyEvent.CTRL_DOWN_MASK;
        }
        getRootPane().registerKeyboardAction(e -> this.setReplaceMode(false),
                KeyStroke.getKeyStroke(KeyEvent.VK_F, cmdKey),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> this.setReplaceMode(true),
                KeyStroke.getKeyStroke(KeyEvent.VK_R, cmdKey),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

    }

    public void setReplaceMode(boolean replaceMode) {
        replacePanel.setVisible(replaceMode);
        if (replaceMode) {
            this.replaceTextField.grabFocus();
            this.setTitle("尋找 & 替換");
            this.replaceMode = true;
        } else {
            this.findTextField.grabFocus();
            this.setTitle("尋找");
            this.replaceMode = false;
        }
        this.pack();
    }

    private void addButtonsListener(JButton btn) {
        btn.setFocusable(false);
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
            int foundCount = 0;
            try {
                subtitles = jySrtTools.getCurrentSelectedDraft().getSubtitles();
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

                List<Integer> foundRows = new ArrayList<>();
                for (Subtitle sub : subtitles) {
                    sub.setFindingText(str);
                    sub.setFound(sub.getText().contains(str));
                    if (sub.isFound()) {
                        foundCount++;
                        foundRows.add(sub.getNum() - 1);
                    }
                }

                index = selectNextFoundRow(index, subtitles);
                jySrtTools.getJyTextPanel().getTextsPanel().setSubtitles(jySrtTools.getCurrentSelectedDraft());
                selectedIndex = 0;
                if (index != -1 && !Objects.equals(str, "")) {
                    tbl.setRowSelectionInterval(index, index);
                    selectedIndex = foundRows.indexOf(index) + 1;
                    tbl.scrollRectToVisible(tbl.getCellRect(index, 0, true));
                }

                if (e.getSource() == clearButton) {
                    if (this.replaceMode) {
                        this.setTitle("尋找 & 替換");
                    } else {
                        this.setTitle("尋找");
                    }
                } else {
                    if (this.replaceMode) {
                        this.setTitle("尋找 & 替換 - " + selectedIndex + "/" + foundCount);
                    } else {
                        this.setTitle("尋找 - " + selectedIndex + "/" + foundCount);
                    }
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
