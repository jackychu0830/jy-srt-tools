package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JyDraft;
import app.jackychu.jysrttools.JySrtTools;
import app.jackychu.jysrttools.exception.JySrtToolsException;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Map;

public class DraftListPanel extends JPanel {
    private JySrtTools jySrtTools;
    private JLabel label;
    private JList<JyDraft> list;

    public DraftListPanel(JySrtTools jySrtTools) {
        this.jySrtTools = jySrtTools;
        setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 5));

        label = new JLabel("<html><span style='font-size:20px'>步驟一: 選擇影片草稿</span></html>", JLabel.LEFT);

        list = new JList<>(getListModel(jySrtTools.getDrafts()));
        list.setCellRenderer(new DraftListCellRender());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setListEventListener();
        JScrollPane jsp = new JScrollPane(list);
        jsp.setHorizontalScrollBar(null);

        setLayout(new BorderLayout());
        add(label, BorderLayout.NORTH);
        add(jsp, BorderLayout.CENTER);
    }

    public void setListEventListener() {
        list.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                JList<JyDraft> list = (JList<JyDraft>) event.getSource();
                 jySrtTools.setCurrentSelectedDraft(list.getSelectedValue());
                try {
                    jySrtTools.getTextsPanel().setTexts(jySrtTools.getCurrentSelectedDraft());
                    jySrtTools.getActionPanel().enableButtons(true);
                } catch (JySrtToolsException e) {
                    jySrtTools.getActionPanel().enableButtons(false);
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
