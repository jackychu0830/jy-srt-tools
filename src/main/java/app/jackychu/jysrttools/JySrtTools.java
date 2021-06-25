package app.jackychu.jysrttools;

import app.jackychu.jysrttools.exception.JySrtToolsException;
import app.jackychu.jysrttools.ui.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class JySrtTools extends JFrame {
    private Map<String, JyDraft> drafts;
    private DraftListPanel listPanel;
    private DraftTextsPanel textsPanel;
    private DraftActionPanel actionPanel;
    private JyDraft currentSelectedDraft;
    private TranslateProgressDialog progressDialog;

    public JySrtTools() {
        loadDrafts();
        init();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JySrtTools tools = new JySrtTools();
            tools.setVisible(true);
        });
    }

    private void loadDrafts() {
        try {
            this.drafts = JyUtils.getAllJyDrafts();
        } catch (JySrtToolsException e) {
            JOptionPane.showMessageDialog(this,
                    new ErrorMessagePanel(e.getMessage()), "程式錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void init() {
        setLayout(new BorderLayout());

        setTitle("剪映字幕工具程式");
        setSize(1280, 768);
        setMinimumSize(new Dimension(1080, 285));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Dimension d = getSize();
                Dimension minD = getMinimumSize();
                if (d.width < minD.width)
                    d.width = minD.width;
                if (d.height < minD.height)
                    d.height = minD.height;
                setSize(d);
            }
        });

        try {
            Image image = ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("icon.png")));
            setIconImage(new ImageIcon(image).getImage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        createMenuBar();
        createListPanel();
        createTextsPanel();
        createActionPanel();

        progressDialog = new TranslateProgressDialog(this, true);
    }

    private void createMenuBar() {
        var menuBar = new JMenuBar();

        var fileMenu = new JMenu("檔案");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        var reloadMenuItem = new JMenuItem("重新載入");
        reloadMenuItem.setMnemonic(KeyEvent.VK_R);
        reloadMenuItem.setToolTipText("重新載入剪映草稿");
        fileMenu.add(reloadMenuItem);
        reloadMenuItem.addActionListener(e -> {
            loadDrafts();
            listPanel.reloadList(drafts);
            try {
                textsPanel.setTexts(null);
            } catch (JySrtToolsException jye) {
                actionPanel.enableButtons(false);
                JOptionPane.showMessageDialog(JySrtTools.this,
                        new ErrorMessagePanel(jye.getMessage()), "重新載入草稿錯誤", JOptionPane.ERROR_MESSAGE);
            }
            actionPanel.enableButtons(false);
        });

        var aboutMenuItem = new JMenuItem("關於");
        aboutMenuItem.setMnemonic(KeyEvent.VK_A);
        aboutMenuItem.setToolTipText("關於本程式");
        fileMenu.add(aboutMenuItem);

        var exiMenuItem = new JMenuItem("結束");
        exiMenuItem.setMnemonic(KeyEvent.VK_E);
        exiMenuItem.setToolTipText("結束程式");
        exiMenuItem.addActionListener((event) -> System.exit(0));
        fileMenu.add(exiMenuItem);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
    }

    private void createListPanel() {
        listPanel = new DraftListPanel();
        listPanel.init(drafts);
        listPanel.setListEventListener(event -> {
            if (!event.getValueIsAdjusting()) {
                JList<JyDraft> list = (JList<JyDraft>) event.getSource();
                currentSelectedDraft = list.getSelectedValue();
                try {
                    textsPanel.setTexts(currentSelectedDraft);
                    actionPanel.enableButtons(true);
                } catch (JySrtToolsException e) {
                    actionPanel.enableButtons(false);
                    JOptionPane.showMessageDialog(JySrtTools.this,
                            new ErrorMessagePanel(e.getMessage()), "草稿資料讀取錯誤", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(listPanel, BorderLayout.WEST);
    }

    private void createTextsPanel() {
        textsPanel = new DraftTextsPanel();
        textsPanel.init();
        add(textsPanel, BorderLayout.CENTER);
    }

    private void createActionPanel() {
        actionPanel = new DraftActionPanel();
        actionPanel.init();
        add(actionPanel, BorderLayout.EAST);

        actionPanel.setButtonActionListener("translate", e -> {
            progressDialog.doTranslate(currentSelectedDraft);
            try {
                textsPanel.setTexts(currentSelectedDraft);
            } catch (JySrtToolsException jye) {
                actionPanel.enableButtons(false);
                JOptionPane.showMessageDialog(JySrtTools.this,
                        new ErrorMessagePanel(jye.getMessage()), "草稿文字更新錯誤", JOptionPane.ERROR_MESSAGE);
            }
        });

        actionPanel.setButtonActionListener("remove", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"清除", "算了"};
                ImageIcon icon = null;
                try {
                    Image image = ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("remove.png")));
                    icon = new ImageIcon(image);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                int result = JOptionPane.showOptionDialog(JySrtTools.this, "<html><span style='font-size:16px'>確定清除草稿字幕？</span></html>", "草稿字幕清除",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        icon,
                        options,
                        options[0]);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        currentSelectedDraft.deleteDraftSubtitles();
                    } catch (JySrtToolsException jye) {
                        JOptionPane.showMessageDialog(JySrtTools.this,
                                new ErrorMessagePanel(jye.getMessage()), "草稿字幕清除失敗", JOptionPane.ERROR_MESSAGE);
                    }
                    try {
                        JyUtils.saveDraft(currentSelectedDraft);
                    } catch (JySrtToolsException jye) {
                        JOptionPane.showMessageDialog(JySrtTools.this,
                                new ErrorMessagePanel(jye.getMessage()), "草稿更新存檔失敗", JOptionPane.ERROR_MESSAGE);
                    }
                    try {
                        textsPanel.setTexts(currentSelectedDraft);
                    } catch (JySrtToolsException jye) {
                        JOptionPane.showMessageDialog(JySrtTools.this,
                                new ErrorMessagePanel(jye.getMessage()), "草稿重新戴入失敗", JOptionPane.ERROR_MESSAGE);
                    }

                }
            }
        });

        actionPanel.setButtonActionListener("export", e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("請選擇要匯出的檔案目錄和名稱");
            fileChooser.setSelectedFile(new File(currentSelectedDraft.getName() + ".srt"));
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            fileChooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("SRT 字幕檔", "srt");
            fileChooser.addChoosableFileFilter(filter);
            int result = fileChooser.showSaveDialog(JySrtTools.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String path = selectedFile.getAbsolutePath();
                if (!path.endsWith(".srt")) {
                    path += ".srt";
                }
                try {
                    JyUtils.exportToSrt(currentSelectedDraft, path);
                } catch (JySrtToolsException jye) {
                    JOptionPane.showMessageDialog(JySrtTools.this,
                            new ErrorMessagePanel(jye.getMessage()), "字幕匯出失敗", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
