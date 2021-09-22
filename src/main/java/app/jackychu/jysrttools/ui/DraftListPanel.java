package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JyDraft;
import app.jackychu.jysrttools.JySrtTools;
import app.jackychu.jysrttools.exception.JySrtToolsException;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class DraftListPanel extends JPanel {
    private final JySrtTools jySrtTools;
    private final JyTextPanel parent;
    private final JList<JyDraft> list;

    public DraftListPanel(JySrtTools jySrtTools, JyTextPanel parent) {
        this.jySrtTools = jySrtTools;
        this.parent = parent;
        setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 5));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());

        JLabel label = new JLabel("<html><span style='font-size:20px'>步驟一: 選擇影片草稿</span></html>", JLabel.LEFT);
        titlePanel.add(label, BorderLayout.WEST);
        JButton btnReload = new JButton("<html><span style='font-size:16px'>重新載入</span></html>");
        setButtonActionListener(btnReload);
        titlePanel.add(btnReload, BorderLayout.EAST);

        list = new JList<>(getListModel(jySrtTools.getDrafts()));
        list.setCellRenderer(new DraftListCellRender());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setListEventListener();
        JScrollPane jsp = new JScrollPane(list);
        jsp.setHorizontalScrollBar(null);

        setLayout(new BorderLayout());
        add(titlePanel, BorderLayout.NORTH);
        add(jsp, BorderLayout.CENTER);
    }

    private void setButtonActionListener(JButton btn) {
        btn.addActionListener(event -> {
            try {
                jySrtTools.loadDrafts();
                jySrtTools.getJyTextPanel().getListPanel().reloadList(jySrtTools.getDrafts());
                jySrtTools.getJyTextPanel().getTextsPanel().setSubtitles(null);
            } catch (JySrtToolsException jye) {
                jySrtTools.getJyTextPanel().getActionPanel().enableButtons(false);
                JOptionPane.showMessageDialog(jySrtTools,
                        new ErrorMessagePanel(jye), "重新載入草稿錯誤", JOptionPane.ERROR_MESSAGE);
            }
            jySrtTools.getJyTextPanel().getActionPanel().enableButtons(false);
        });
    }

    private void setListEventListener() {
        list.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                JList<JyDraft> list = (JList<JyDraft>) event.getSource();
                jySrtTools.setCurrentSelectedDraft(list.getSelectedValue());
                parent.getActionPanel().enableButtons(false);
                try {
                    parent.getTextsPanel().setSubtitles(jySrtTools.getCurrentSelectedDraft());
                } catch (JySrtToolsException e) {
                    JOptionPane.showMessageDialog(jySrtTools,
                            new ErrorMessagePanel(e), "草稿資料讀取錯誤", JOptionPane.ERROR_MESSAGE);
                }
            }

        });
    }

    public void reloadList(Map<String, JyDraft> drafts) {
        list.removeAll();
        list.setModel(getListModel(drafts));
    }

    private DraftListModel getListModel(Map<String, JyDraft> drafts) {
        DraftListModel model = new DraftListModel();
        for (JyDraft draft : drafts.values()) {
            model.addElement(draft);
        }
        return model;
    }
}
