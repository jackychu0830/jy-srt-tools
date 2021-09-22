package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JySrtTools;
import app.jackychu.jysrttools.JyUtils;
import app.jackychu.jysrttools.exception.JySrtToolsException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DraftActionPanel extends JPanel {
    private final JySrtTools jySrtTools;
    private final JyTextPanel parent;
    private final Map<String, JButton> buttons = new HashMap<>();

    public DraftActionPanel(JySrtTools jySrtTools, JyTextPanel parent) {
        this.jySrtTools = jySrtTools;
        this.parent = parent;
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 20));
        setLayout(new BorderLayout());

        JLabel label = new JLabel("<html><span style='font-size:20px'>步驟三: 執行功能</span></html>", JLabel.LEFT);

        JPanel panel = new JPanel();
        JButton btnTcTranslate = new JButton("<html><span style='font-size:16px'>简体 轉換為 繁體</span></html>");
        JButton btnTwTranslate = new JButton("<html><span style='font-size:16px'>简体 翻譯為 台灣正體</span></html>");
        JButton btnSrtExport = new JButton("<html><span style='font-size:16px'>輸出 SRT 檔</span></html>");
        JButton btnTxtExport = new JButton("<html><span style='font-size:16px'>輸出 txt 文字檔</span></html>");
        JButton btnRemove = new JButton("<html><span style='font-size:16px'>清除字幕</span></html>");
        btnTcTranslate.setEnabled(false);
        btnTwTranslate.setEnabled(false);
        btnSrtExport.setEnabled(false);
        btnTxtExport.setEnabled(false);
        btnRemove.setEnabled(false);
        buttons.put("tcTranslate", btnTcTranslate);
        buttons.put("twTranslate", btnTwTranslate);
        buttons.put("srtExport", btnSrtExport);
        buttons.put("txtExport", btnTxtExport);
        buttons.put("remove", btnRemove);
        setButtonActionListener("tcTranslate");
        setButtonActionListener("twTranslate");
        setButtonActionListener("srtExport");
        setButtonActionListener("txtExport");
        setButtonActionListener("remove");
        panel.setLayout(new GridLayout(buttons.size(), 1));
        panel.add(btnTcTranslate);
        panel.add(btnTwTranslate);
        panel.add(btnSrtExport);
        panel.add(btnTxtExport);
        panel.add(btnRemove);

        add(label, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }

    private void setButtonActionListener(String button) {
        switch (button) {
            case "tcTranslate":
            case "twTranslate":
                buttons.get(button).addActionListener(e -> {
                    jySrtTools.getProgressDialog().doTranslate(button, jySrtTools.getCurrentSelectedDraft());
                    try {
                        parent.getTextsPanel().setSubtitles(jySrtTools.getCurrentSelectedDraft());
                    } catch (JySrtToolsException jye) {
                        parent.getActionPanel().enableButtons(false);
                        JOptionPane.showMessageDialog(jySrtTools,
                                new ErrorMessagePanel(jye), "草稿文字更新錯誤", JOptionPane.ERROR_MESSAGE);
                    }
                });
                break;
            case "txtExport":
            case "srtExport":
                buttons.get(button).addActionListener(e -> {
                    String type = button.equals("txtExport") ? "txt" : "srt";
                    String typeName = button.equals("txtExport") ? "TXT 文字檔" : "SRT 字幕檔";
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("請選擇要匯出的檔案目錄和名稱");
                    fileChooser.setSelectedFile(new File(jySrtTools.getCurrentSelectedDraft().getName() + "." + type));
                    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                    fileChooser.setAcceptAllFileFilterUsed(false);
                    FileNameExtensionFilter filter = new FileNameExtensionFilter(typeName, type);
                    fileChooser.addChoosableFileFilter(filter);
                    int result = fileChooser.showSaveDialog(jySrtTools);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        String path = selectedFile.getAbsolutePath();
                        if (!path.endsWith("." + type)) {
                            path += "." + type;
                        }
                        try {
                            JyUtils.exportToFile(jySrtTools.getCurrentSelectedDraft(), path, type);
                        } catch (JySrtToolsException jye) {
                            JOptionPane.showMessageDialog(jySrtTools,
                                    new ErrorMessagePanel(jye), "檔案匯出失敗", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                });
                break;
            case "remove":
                buttons.get(button).addActionListener(e -> {
                    Object[] options = {"清除", "算了"};
                    ImageIcon icon = null;
                    try {
                        Image image = ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("remove.png")));
                        icon = new ImageIcon(image);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }

                    int result = JOptionPane.showOptionDialog(jySrtTools, "<html><span style='font-size:16px'>確定清除草稿字幕？</span></html>", "草稿字幕清除",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            icon,
                            options,
                            options[0]);
                    if (result == JOptionPane.YES_OPTION) {
                        try {
                            jySrtTools.getCurrentSelectedDraft().deleteDraftSubtitles();
                        } catch (JySrtToolsException jye) {
                            JOptionPane.showMessageDialog(jySrtTools,
                                    new ErrorMessagePanel(jye), "草稿字幕清除失敗", JOptionPane.ERROR_MESSAGE);
                        }
                        try {
                            JyUtils.saveDraft(jySrtTools.getCurrentSelectedDraft());
                        } catch (JySrtToolsException jye) {
                            JOptionPane.showMessageDialog(jySrtTools,
                                    new ErrorMessagePanel(jye), "草稿更新存檔失敗", JOptionPane.ERROR_MESSAGE);
                        }
                        try {
                            parent.getTextsPanel().setSubtitles(jySrtTools.getCurrentSelectedDraft());
                        } catch (JySrtToolsException jye) {
                            JOptionPane.showMessageDialog(jySrtTools,
                                    new ErrorMessagePanel(jye), "草稿重新戴入失敗", JOptionPane.ERROR_MESSAGE);
                        }

                    }
                });
                break;
        }
    }

    public void enableButtons(boolean enable) {
        for (JButton btn : buttons.values()) {
            btn.setEnabled(enable);
        }
    }
}
