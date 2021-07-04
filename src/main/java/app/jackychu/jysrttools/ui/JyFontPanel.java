package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JyFont;
import app.jackychu.jysrttools.JySrtTools;
import app.jackychu.jysrttools.JyUtils;
import app.jackychu.jysrttools.exception.JySrtToolsException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class JyFontPanel extends JPanel {

    private final JySrtTools jySrtTools;
//    private final JList<JyFont> fontList;
    private final JTable fontTable;
    private final JTextArea exampleTextArea;
    private final JTextArea originTextArea;
    private final String originText;
    private final JButton btnReplace;
    private final JButton btnReset;
    private final JScrollPane jspExample;
    private final JScrollPane jspOrigin;
    private JyFont selectedFont;

    public JyFontPanel(JySrtTools jySrtTools) {
        this.jySrtTools = jySrtTools;
        setLayout(new BorderLayout());

        JPanel fontTablePanel = new JPanel();
        fontTablePanel.setLayout(new BorderLayout());
        fontTablePanel.add(new JLabel("<html><span style='font-size:20px'>剪映內建字型</span></html>", JLabel.LEFT), BorderLayout.NORTH);
        fontTable = new JTable(getTableModel());
        fontTable.setDefaultRenderer(FontCell.class, new FontCellRender());
        fontTable.getColumnModel().getColumn(0).setMaxWidth(120);
        fontTable.getColumnModel().getColumn(0).setResizable(false);
        fontTable.getColumnModel().getColumn(1).setMaxWidth(180);
        fontTable.getColumnModel().getColumn(1).setResizable(false);
        fontTable.setRowHeight(32);
        fontTable.getTableHeader().setReorderingAllowed(false);
        fontTable.getTableHeader().setFont(fontTable.getTableHeader().getFont().deriveFont(20f));
        fontTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane jsp = new JScrollPane(fontTable);
        jsp.setPreferredSize(new Dimension(300, 600));
        fontTablePanel.add(jsp, BorderLayout.CENTER);
        add(fontTablePanel, BorderLayout.WEST);

//        JPanel fontListPanel = new JPanel();
//        fontListPanel.setLayout(new BorderLayout());
//        fontListPanel.add(new JLabel("<html><span style='font-size:20px'>剪映內建字型</span></html>", JLabel.LEFT), BorderLayout.NORTH);
//
//        fontList = new JList<>(getListModel());
//        fontList.setCellRenderer(new JyFontListCellRender());
//        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        setListEventListener();
//        JScrollPane jsp = new JScrollPane(fontList);
//        jsp.setHorizontalScrollBar(null);
//        fontListPanel.add(jsp, BorderLayout.CENTER);
//        add(fontListPanel, BorderLayout.WEST);

        originText = "(繁體) 歡迎按讚、訂閱、留言加分享~" + System.lineSeparator() +
                "(简体) 欢迎按赞、订阅、留言加分享~" + System.lineSeparator() +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + System.lineSeparator() +
                "1234567890" + System.lineSeparator() +
                ",.;/':~!@#$%^&*()[]{}<>?~";

        JPanel textPanel = new JPanel();
        textPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 5, 20));
        textPanel.setLayout(new BorderLayout());
        textPanel.add(new JLabel("<html><span style='font-size:20px'>文字範例</span></html>", JLabel.LEFT), BorderLayout.NORTH);

        JPanel textExamplePanel = new JPanel();
        textExamplePanel.setLayout(new GridLayout(1, 2));

        JPanel originTextPanel = new JPanel();
        originTextPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        originTextPanel.setLayout(new BorderLayout());
        originTextPanel.add(new JLabel("<html><span style='font-size:16px'>系統預設字型</span></html>", JLabel.LEFT), BorderLayout.NORTH);
        originTextArea = new JTextArea();
        originTextArea.setText(originText);
        originTextArea.setFont(originTextArea.getFont().deriveFont(20f));
        originTextArea.setMargin(new Insets(10, 10, 10, 10));
        textAreaTextChange();
        textAreaCaretChange();
        jspOrigin = new JScrollPane(originTextArea);
        // setScrollPaneAdjustmentListener(jspOrigin);
        originTextPanel.add(jspOrigin, BorderLayout.CENTER);
        textExamplePanel.add(originTextPanel);

        JPanel exampleTextPanel = new JPanel();
        exampleTextPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        exampleTextPanel.setLayout(new BorderLayout());
        exampleTextPanel.add(new JLabel("<html><span style='font-size:16px'>套用剪映字型</span></html>", JLabel.LEFT), BorderLayout.NORTH);
        exampleTextArea = new JTextArea();
        exampleTextArea.setMargin(new Insets(10, 10, 10, 10));
        exampleTextArea.setText(originText);
        exampleTextArea.setEditable(false);
        jspExample = new JScrollPane(exampleTextArea);
        // setScrollPaneAdjustmentListener(jspExample);
        exampleTextPanel.add(jspExample, BorderLayout.CENTER);
        textExamplePanel.add(exampleTextPanel);

        textPanel.add(textExamplePanel, BorderLayout.CENTER);
        add(textPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BorderLayout());
        actionPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 5, 20));
        actionPanel.add(new JLabel("<html><span style='font-size:20px'>字型功能</span></html>", JLabel.LEFT), BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(2, 1));
        btnReplace = new JButton("<html><span style='font-size:16px'>字型替換</span></html>");
        btnReplace.setEnabled(false);
        setReplaceButtonActionListener();
        buttonsPanel.add(btnReplace);
        btnReset = new JButton("<html><span style='font-size:16px'>字型還原</span></html>");
        btnReset.setEnabled(false);
        setResetButtonActionListener();
        buttonsPanel.add(btnReset);

        actionPanel.add(buttonsPanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.EAST);

//        fontList.setSelectedIndex(0);
        fontTable.setRowSelectionInterval(0, 0);
    }

    private JyFontTableModel getTableModel() {
        try {
            return new JyFontTableModel(JyUtils.getAllJyFonts());
        } catch (JySrtToolsException e) {
            JOptionPane.showMessageDialog(jySrtTools,
                    new ErrorMessagePanel(e), "產生字型列表錯誤", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private JyFontListModel getListModel() {
        JyFontListModel model = new JyFontListModel();
        try {
            for (JyFont font : JyUtils.getAllJyFonts()) {
                model.addElement(font);
            }
        } catch (JySrtToolsException e) {
            JOptionPane.showMessageDialog(jySrtTools,
                    new ErrorMessagePanel(e), "產生字型列表錯誤", JOptionPane.ERROR_MESSAGE);
        }
        return model;
    }

    private void setScrollPaneAdjustmentListener(JScrollPane jsp) {
        jsp.getVerticalScrollBar().addAdjustmentListener(event -> {
            if (event.getValueIsAdjusting()) {
                return;
            }
            if (jsp.equals(jspExample)) {
                jspOrigin.getVerticalScrollBar().setValue(event.getValue());
            } else {
                jspExample.getVerticalScrollBar().setValue(event.getValue());
            }
        });
    }

//    private void setListEventListener() {
//        fontList.addListSelectionListener(event -> {
//            if (!event.getValueIsAdjusting()) {
//                JList<JyFont> list = (JList<JyFont>) event.getSource();
//                selectedFont = list.getSelectedValue();
//                if (selectedFont != null) {
//                    exampleTextArea.setFont(selectedFont.getFont());
//                    btnReplace.setEnabled(true);
//
//                    btnReset.setEnabled(selectedFont.isReplaced());
//                } else {
//                    btnReplace.setEnabled(false);
//                    btnReset.setEnabled(false);
//                }
//            }
//        });
//    }

    private void textAreaCaretChange() {
        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);

        originTextArea.addCaretListener(event -> {
            if (!originTextArea.getText().equals(exampleTextArea.getText())) {
                exampleTextArea.setText(originTextArea.getText());
            }
            originTextArea.getHighlighter().removeAllHighlights();
            exampleTextArea.getHighlighter().removeAllHighlights();
            try {
                int caretPosition = originTextArea.getCaretPosition();
                int lineNum = originTextArea.getLineOfOffset(caretPosition);
                int lineStart = originTextArea.getLineStartOffset(lineNum);
                int lineEnd = originTextArea.getLineEndOffset(lineNum);
                originTextArea.getHighlighter().addHighlight(lineStart, lineEnd, painter);
                exampleTextArea.getHighlighter().addHighlight(lineStart, lineEnd, painter);
                exampleTextArea.setCaretPosition(caretPosition);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    private void textAreaTextChange() {
        originTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            private void setText() {
                //exampleTextArea.setText(originTextArea.getText());
            }
        });
    }

    private void setReplaceButtonActionListener() {
        btnReplace.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("請選擇新字型 TTF/OTF 檔案");
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            fileChooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("TTF/OTF 字型檔", "ttf", "otf");
            fileChooser.addChoosableFileFilter(filter);
            int result = fileChooser.showOpenDialog(jySrtTools);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String newFilename = selectedFile.getName().split("\\.")[0];
                Path target = Paths.get(selectedFont.getFile().getAbsolutePath());
                Path source = Paths.get(selectedFile.getAbsolutePath());
                Path bakTarget = Paths.get(target.getParent().toString(),
                        selectedFont.getName() + "." + newFilename + ".bak");

                Object[] options = {"確定", "取消"};
                ImageIcon icon = null;
                try {
                    Image image = ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("replace.png")));
                    icon = new ImageIcon(image);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                result = JOptionPane.showOptionDialog(jySrtTools,
                        "<html><span style='font-size:16px'>確定將 <font color='blue'>" + selectedFont.getName() +
                                "</font> 替換成 <font color='green'>" + newFilename + "</font>？</span></html>", "字型替換",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        icon,
                        options,
                        options[0]);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        if (selectedFont.isReplaced()) {
                            resetFont();
                        }
                        Files.copy(target, bakTarget, StandardCopyOption.REPLACE_EXISTING);
                        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                        reloadFontList();
                    } catch (Exception ioe) {
                        JOptionPane.showMessageDialog(jySrtTools,
                                new ErrorMessagePanel(ioe), "字型替換失敗", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void setResetButtonActionListener() {
        btnReset.addActionListener(e -> {
            Object[] options = {"確定", "取消"};
            ImageIcon icon = null;
            try {
                Image image = ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("reset.png")));
                icon = new ImageIcon(image);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            int result = JOptionPane.showOptionDialog(jySrtTools,
                    "<html><span style='font-size:16px'>確定還原回剪映 " + selectedFont.getName() + " 字型？</span></html>", "字型還原",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    icon,
                    options,
                    options[0]);
            if (result == JOptionPane.YES_OPTION) {
                resetFont();
                reloadFontList();
            }
        });
    }

    private void resetFont() {
        Path target = Paths.get(selectedFont.getFile().getAbsolutePath());
        Path source = Paths.get(selectedFont.getBackupPath());
        try {
            File old = new File(source.toString());
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(source);
        } catch (Exception ioe) {
            JOptionPane.showMessageDialog(jySrtTools,
                    new ErrorMessagePanel(ioe), "字型還原失敗", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setText(String text) {
        if (text == null || text.equals("")) {
            originTextArea.setText(originText);
        } else {
            originTextArea.setText(text);
        }
        originTextArea.setCaretPosition(0);
        exampleTextArea.setCaretPosition(0);
    }

    public void reloadFontList() {
//        String name = selectedFont.getName();
//        fontList.removeAll();
//        fontList.setModel(getListModel());
//        fontList.setSelectedIndex(0);
//
//        ListModel<JyFont> lm = fontList.getModel();
//        for (int i = 0; i < lm.getSize(); i++) {
//            if (lm.getElementAt(i).getName().equals(name)) {
//                fontList.setSelectedValue(lm.getElementAt(i), true);
//                break;
//            }
//        }
    }
}
