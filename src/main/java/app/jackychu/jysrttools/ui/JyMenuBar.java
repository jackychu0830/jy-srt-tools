package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JySrtTools;
import app.jackychu.jysrttools.exception.JySrtToolsException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

public class JyMenuBar extends JMenuBar {
    private final JySrtTools jySrtTools;
    private final JMenuItem aboutMenuItem;
    private final JMenuItem findMenuItem;
    private final JMenuItem replaceMenuItem;

    public JyMenuBar(JySrtTools jySrtTools) {
        super();

        String os = System.getProperty("os.name");
        int cmdKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(); //KeyEvent.VK_META;
        if (os.toLowerCase(Locale.ROOT).contains("windows")) {
            cmdKey = KeyEvent.CTRL_DOWN_MASK;
        }

        this.jySrtTools = jySrtTools;
        JMenu fileMenu = new JMenu("檔案");

        aboutMenuItem = new JMenuItem("關於");
        aboutMenuItem.setToolTipText("關於本程式");
        addAboutMenuItemActionListener();
        fileMenu.add(aboutMenuItem);

        JMenuItem exiMenuItem = new JMenuItem("結束");
        exiMenuItem.setToolTipText("結束程式");
        exiMenuItem.addActionListener((event) -> System.exit(0));
        fileMenu.add(exiMenuItem);

        add(fileMenu);

        JMenu editMenu = new JMenu("編輯");
        findMenuItem = new JMenuItem("尋找");
        findMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, cmdKey));
        findMenuItem.setToolTipText("尋找字串");
        editMenu.add(findMenuItem);

        replaceMenuItem = new JMenuItem("替換");
        replaceMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, cmdKey));
        replaceMenuItem.setToolTipText("尋找並替換字串");
        editMenu.add(replaceMenuItem);

        addMenuItemActionListener();

        add(editMenu);
    }

    private void addAboutMenuItemActionListener() {
        aboutMenuItem.addActionListener(e -> {
            Object[] options = {"知道了"};
            ImageIcon icon = null;
            try {
                Image image = ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("images/icon.png")));
                icon = new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(96, 96, Image.SCALE_DEFAULT));

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            JOptionPane.showOptionDialog(jySrtTools,
                    new AboutPanel(),
                    "關於本程式",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    icon,
                    options,
                    options[0]);
        });
    }

    private void addMenuItemActionListener() {
        ActionListener actionListener = e -> {
            if (jySrtTools.getCurrentSelectedDraft() != null) {
                try {
                    if (jySrtTools.getCurrentSelectedDraft().getDraftTextIds().size() > 0) {
//                            jySrtTools.getJyTextPanel().getTextsPanel().getSubtitleTable().putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
                        if (jySrtTools.getJyTextPanel().getTextsPanel().getSubtitleTable().isEditing()) {
                            jySrtTools.getJyTextPanel().getTextsPanel().getSubtitleTable().getCellEditor().cancelCellEditing();
                        }
                        jySrtTools.getFindReplaceDialog().setVisible(true);
                        jySrtTools.getFindReplaceDialog().setReplaceMode(e.getSource() == replaceMenuItem);
                    }
                } catch (JySrtToolsException ex) {
                    JOptionPane.showMessageDialog(jySrtTools,
                            new ErrorMessagePanel(ex), "請選選擇有字幕的草稿檔", JOptionPane.ERROR_MESSAGE);
                }
            }

        };
        findMenuItem.addActionListener(actionListener);
        replaceMenuItem.addActionListener(actionListener);
    }
}
