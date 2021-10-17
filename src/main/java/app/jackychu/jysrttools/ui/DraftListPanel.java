package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JyDraft;
import app.jackychu.jysrttools.JyDraftLastModifiedTimeComparator;
import app.jackychu.jysrttools.JyDraftNameComparator;
import app.jackychu.jysrttools.JySrtTools;
import app.jackychu.jysrttools.exception.JySrtToolsException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DraftListPanel extends JPanel {
    private final JySrtTools jySrtTools;
    private final JyTextPanel parent;
    private final JList<JyDraft> list;
    private final SearchBox searchBox;
    private final JButton btnAzSort;
    private final JButton btnTimeSort;
    private ImageIcon azIcon;
    private ImageIcon zaIcon;
    private ImageIcon clockwiseIcon;
    private ImageIcon counterclockwiseIcon;

    public DraftListPanel(JySrtTools jySrtTools, JyTextPanel parent) {
        try {
            azIcon = new ImageIcon(ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("images/a-z.png"))));
            zaIcon = new ImageIcon(ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("images/z-a.png"))));
            clockwiseIcon = new ImageIcon(ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("images/clockwise.png"))));
            counterclockwiseIcon = new ImageIcon(ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("images/counterclockwise.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.jySrtTools = jySrtTools;
        this.parent = parent;
        setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 5));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        JLabel label = new JLabel("<html><span style='font-size:20px'>步驟一: 選擇影片草稿</span></html>", JLabel.LEFT);
        titlePanel.add(label, BorderLayout.WEST);
        JButton btnReload = new JButton("<html><span style='font-size:16px'>重新載入</span></html>");
        setButtonActionListener(btnReload);
        titlePanel.add(btnReload, BorderLayout.EAST);
        topPanel.add(titlePanel);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        searchBox = new SearchBox(50);
        addSearchBoxListener();
        searchPanel.add(searchBox);
        btnAzSort = new JButton();
        btnAzSort.setIcon(azIcon);
        btnAzSort.setToolTipText("照草稿名稱排序");
        addSortButtonListener(btnAzSort);
        btnTimeSort = new JButton();
        btnTimeSort.setIcon(clockwiseIcon);
        btnTimeSort.setToolTipText("照草稿最後編輯時間排序");
        addSortButtonListener(btnTimeSort);
        searchPanel.add(btnAzSort);
        searchPanel.add(btnTimeSort);
        topPanel.add(searchPanel);

        list = new JList<>(getListModel(jySrtTools.getDrafts()));
        list.setCellRenderer(new DraftListCellRender());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setListEventListener();
        JScrollPane jsp = new JScrollPane(list);
        jsp.setHorizontalScrollBar(null);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(jsp, BorderLayout.CENTER);
    }

    private void setButtonActionListener(JButton btn) {
        btn.addActionListener(event -> {
            try {
                jySrtTools.loadDrafts();
                jySrtTools.getJyTextPanel().getListPanel().reloadList(jySrtTools.getDrafts());
                jySrtTools.getJyTextPanel().getTextsPanel().setSubtitles(null);
            } catch (JySrtToolsException jye) {
                jySrtTools.getJyTextPanel().getActionPanel().enableButtons(false, true);
                JOptionPane.showMessageDialog(jySrtTools,
                        new ErrorMessagePanel(jye), "重新載入草稿錯誤", JOptionPane.ERROR_MESSAGE);
            }
            jySrtTools.getJyTextPanel().getActionPanel().enableButtons(false, true);
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

    public void reloadList(List<JyDraft> drafts) {
        list.removeAll();
        list.setModel(getListModel(drafts));
    }

    private DraftListModel getListModel(List<JyDraft> drafts) {
        List<JyDraft> newDraftList = new ArrayList<>();
        for (JyDraft draft : drafts) {
            if (!draft.isHidden()) {
                newDraftList.add(draft);
            }
        }
        DraftListModel model = new DraftListModel();
        model.addAll(newDraftList);
        return model;
    }

    private void addSearchBoxListener() {
        searchBox.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                reloadList(jySrtTools.filterDrafts(searchBox.getText()));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                reloadList(jySrtTools.filterDrafts(searchBox.getText()));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    private void addSortButtonListener(JButton btn) {
        if (btn == btnAzSort) {
            btnAzSort.addActionListener(e -> {
                Icon icon = btnAzSort.getIcon();
                List<JyDraft> drafts = null;
                if (icon == azIcon) {
                    drafts = jySrtTools.getDrafts((new JyDraftNameComparator()).reversed(), true);
                    btnAzSort.setIcon(zaIcon);
                    btnAzSort.setToolTipText("照草稿名稱排序 (反向)");
                } else if (icon == zaIcon) {
                    drafts = jySrtTools.getDrafts((new JyDraftNameComparator()).reversed(), false);
                    btnAzSort.setIcon(azIcon);
                    btnAzSort.setToolTipText("照草稿名稱排序");
                }
                reloadList(drafts);
            });
        } else if (btn == btnTimeSort) {
            btnTimeSort.addActionListener(e -> {
                Icon icon = btnTimeSort.getIcon();
                List<JyDraft> drafts;
                if (icon == clockwiseIcon) {
                    drafts = jySrtTools.getDrafts(new JyDraftLastModifiedTimeComparator(), true);
                    btnTimeSort.setIcon(counterclockwiseIcon);
                    btnTimeSort.setToolTipText("照草稿最後編輯時間排序 (反向)");
                } else {
                    drafts = jySrtTools.getDrafts(new JyDraftLastModifiedTimeComparator(), false);
                    btnTimeSort.setIcon(clockwiseIcon);
                    btnTimeSort.setToolTipText("照草稿最後編輯時間排序");
                }
                reloadList(drafts);
            });
        }
    }
}
