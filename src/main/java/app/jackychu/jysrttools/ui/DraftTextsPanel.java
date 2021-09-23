package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JyDraft;
import app.jackychu.jysrttools.JySrtTools;
import app.jackychu.jysrttools.JyUtils;
import app.jackychu.jysrttools.Subtitle;
import app.jackychu.jysrttools.exception.JySrtToolsException;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DraftTextsPanel extends JPanel {
    private final JySrtTools jySrtTools;
    @Getter
    private final JTable subtitleTable;

    public DraftTextsPanel(JySrtTools jySrtTools) {
        this.jySrtTools = jySrtTools;
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel label = new JLabel("<html><span style='font-size:20px'>步驟二: 檢視草稿字幕內容</span></html>", JLabel.LEFT);

        subtitleTable = new JTable(new DraftSubtitleTableModel(new ArrayList<>()));
        subtitleTable.setDefaultRenderer(Subtitle.class, new SubtitleCellRender());
        subtitleTable.setDefaultEditor(Subtitle.class, new SubtitleCellEditor());
        subtitleTable.setRowHeight(24);
        subtitleTable.getTableHeader().setReorderingAllowed(false);
        subtitleTable.getTableHeader().setFont(subtitleTable.getTableHeader().getFont().deriveFont(16f));
        subtitleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setTableColumnSize();

        subtitleTable.addPropertyChangeListener("tableCellEditor", e -> {
            if (!subtitleTable.isEditing()) {
                DraftSubtitleTableModel dm = (DraftSubtitleTableModel) subtitleTable.getModel();
                if (dm.isDirty()) {
                    Subtitle sub = dm.getSubtitles().get(subtitleTable.getSelectedRow());
                    saveSubtitleChanges(sub);
                    dm.setDirty(false);
                }
            }
        });

        setLayout(new BorderLayout());
        add(label, BorderLayout.NORTH);
        JScrollPane jsp = new JScrollPane(subtitleTable);
        jsp.setPreferredSize(new Dimension(500, 600));

        add(new JScrollPane(subtitleTable), BorderLayout.CENTER);
    }

    public void saveSubtitleChanges(Subtitle sub) {
        try {
            this.jySrtTools.getCurrentSelectedDraft().updateDraftText(sub.getId(), sub.getText());
            JyUtils.saveDraft(this.jySrtTools.getCurrentSelectedDraft());
        } catch (JySrtToolsException ex) {
            JOptionPane.showMessageDialog(this,
                    new ErrorMessagePanel(ex), "儲存修改失敗", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setSubtitles(JyDraft draft) throws JySrtToolsException {
        subtitleTable.setModel(new DraftSubtitleTableModel(new ArrayList<>()));
        setTableColumnSize();

        if (draft == null) return;
        if (draft.getDraftTextIds().size() == 0) {
            jySrtTools.getJyTextPanel().getActionPanel().enableButtons(false);
        } else {
            subtitleTable.setModel(new DraftSubtitleTableModel(draft.getDraftSubtitles()));
            jySrtTools.getJyTextPanel().getActionPanel().enableButtons(true);
        }
        setTableColumnSize();
    }

    private void setTableColumnSize() {
//        subtitleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        subtitleTable.getColumnModel().getColumn(0).setMaxWidth(40);
        subtitleTable.getColumnModel().getColumn(0).setResizable(false);
        subtitleTable.getColumnModel().getColumn(1).setMaxWidth(115);
        subtitleTable.getColumnModel().getColumn(1).setMinWidth(115);
        subtitleTable.getColumnModel().getColumn(1).setResizable(false);
        subtitleTable.getColumnModel().getColumn(2).setMaxWidth(115);
        subtitleTable.getColumnModel().getColumn(2).setMinWidth(115);
        subtitleTable.getColumnModel().getColumn(2).setResizable(false);
//        subtitleTable.getColumnModel().getColumn(3).setMinWidth(270);
    }
}
