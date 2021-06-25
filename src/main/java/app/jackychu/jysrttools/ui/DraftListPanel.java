package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JyDraft;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Map;

public class DraftListPanel extends JPanel {
    private JLabel label;
    private JList<JyDraft> list;

    public void init(Map<String, JyDraft> drafts) {
        setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 5));

        label = new JLabel("<html><span style='font-size:20px'>步驟一: 選擇影片草稿</span></html>", JLabel.LEFT);

        list = new JList<>(getListModel(drafts));
        list.setCellRenderer(new DraftListCellRender());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane jsp = new JScrollPane(list);
        jsp.setHorizontalScrollBar(null);

        setLayout(new BorderLayout());
        add(label, BorderLayout.NORTH);
        add(jsp, BorderLayout.CENTER);
    }

    public void setListEventListener(ListSelectionListener listener) {
        list.addListSelectionListener(listener);
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
