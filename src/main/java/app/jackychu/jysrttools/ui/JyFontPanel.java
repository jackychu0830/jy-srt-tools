package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JyFont;
import app.jackychu.jysrttools.JySrtTools;
import app.jackychu.jysrttools.JyUtils;
import app.jackychu.jysrttools.exception.JySrtToolsException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class JyFontPanel extends JPanel {

    private final JySrtTools jySrtTools;
    private final JList<JyFont> fontList;
    private final JTextArea exampleTextArea;
    private final JTextArea originTextArea;
    private JyFont selectedFont;
    private final String originText;
    private final JButton btnReplace;
    private final JButton btnReset;

    public JyFontPanel(JySrtTools jySrtTools) {
        this.jySrtTools = jySrtTools;
        setLayout(new BorderLayout());

        JPanel fontListPanel = new JPanel();
        fontListPanel.setLayout(new BorderLayout());
        fontListPanel.add(new JLabel("<html><span style='font-size:20px'>剪映內建字型</span></html>", JLabel.LEFT), BorderLayout.NORTH);

        fontList = new JList<>(getListModel());
        fontList.setCellRenderer(new JyFontListCellRender());
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setListEventListener();
        JScrollPane jsp = new JScrollPane(fontList);
        jsp.setHorizontalScrollBar(null);
        fontListPanel.add(jsp, BorderLayout.CENTER);
        add(fontListPanel, BorderLayout.WEST);

        originText = "歡迎按讚、訂閱、留言加分享~" + System.lineSeparator() +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + System.lineSeparator() +
                "1234567890" + System.lineSeparator() +
                ",.;/':~!@#$%^&*()[]{}<>?~";

        JPanel textPanel = new JPanel();
        textPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 5, 20));
        textPanel.setLayout(new BorderLayout());
        textPanel.add(new JLabel("<html><span style='font-size:20px'>文字範例</span></html>", JLabel.LEFT), BorderLayout.NORTH);

        JPanel textExamplePanel = new JPanel();
        textExamplePanel.setLayout(new GridLayout(2, 1));

        JPanel originTextPanel = new JPanel();
        originTextPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        originTextPanel.setLayout(new BorderLayout());
        originTextPanel.add(new JLabel("<html><span style='font-size:16px'>系統預設字型</span></html>", JLabel.LEFT), BorderLayout.NORTH);
        originTextArea = new JTextArea();
        originTextArea.setText(originText);
        originTextArea.setFont(originTextArea.getFont().deriveFont(20f));
        originTextArea.setMargin(new Insets(10, 10, 10, 10));
        textAreaTextChange();
        originTextPanel.add(new JScrollPane(originTextArea), BorderLayout.CENTER);
        textExamplePanel.add(originTextPanel);

        JPanel exampleTextPanel = new JPanel();
        exampleTextPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        exampleTextPanel.setLayout(new BorderLayout());
        exampleTextPanel.add(new JLabel("<html><span style='font-size:16px'>套用剪映字型</span></html>", JLabel.LEFT), BorderLayout.NORTH);
        exampleTextArea = new JTextArea();
        exampleTextArea.setMargin(new Insets(10, 10, 10, 10));
        exampleTextArea.setText(originText);
        exampleTextArea.setEditable(false);
        exampleTextPanel.add(new JScrollPane(exampleTextArea), BorderLayout.CENTER);
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

        fontList.setSelectedIndex(0);
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

    private void setListEventListener() {
        fontList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                JList<JyFont> list = (JList<JyFont>) event.getSource();
                selectedFont = list.getSelectedValue();
                exampleTextArea.setFont(selectedFont.getTtfFont());
                btnReplace.setEnabled(true);

                if (selectedFont.isReplaced()) {
                    btnReset.setEnabled(true);
                } else {
                    btnReset.setEnabled(false);
                }
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
                exampleTextArea.setText(originTextArea.getText());
            }
        });
    }

    private void setReplaceButtonActionListener() {
        btnReplace.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("請選擇新字型 TTF 檔案");
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("TTF 字型檔", "ttf");
            fileChooser.addChoosableFileFilter(filter);
            int result = fileChooser.showOpenDialog(jySrtTools);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String path = selectedFile.getAbsolutePath();
                System.out.println(path);
            }
        });
    }

    private void setResetButtonActionListener() {
        btnReplace.addActionListener(e -> {
        });
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
}
