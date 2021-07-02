package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JySrtTools;
import app.jackychu.jysrttools.exception.JySrtToolsException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Objects;

public class JyMenuBar extends JMenuBar {
    private JySrtTools jySrtTools;
    private JyTextPanel jyTextPanel;
    private JMenu fileMenu;
    private JMenuItem reloadMenuItem;
    private JMenuItem aboutMenuItem;
    private JMenuItem exiMenuItem;

    public JyMenuBar(JySrtTools jySrtTools, JyTextPanel jyTextPanel) {
        super();

        this.jySrtTools = jySrtTools;
        this.jyTextPanel = jyTextPanel;
        fileMenu = new JMenu("檔案");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        reloadMenuItem = new JMenuItem("重新載入");
        reloadMenuItem.setMnemonic(KeyEvent.VK_R);
        reloadMenuItem.setToolTipText("重新載入剪映草稿");
        addReloadMenuItemActionListener();
        fileMenu.add(reloadMenuItem);

        aboutMenuItem = new JMenuItem("關於");
        aboutMenuItem.setMnemonic(KeyEvent.VK_A);
        aboutMenuItem.setToolTipText("關於本程式");
        addAboutMenuItemActionListener();
        fileMenu.add(aboutMenuItem);

        exiMenuItem = new JMenuItem("結束");
        exiMenuItem.setMnemonic(KeyEvent.VK_E);
        exiMenuItem.setToolTipText("結束程式");
        exiMenuItem.addActionListener((event) -> System.exit(0));
        fileMenu.add(exiMenuItem);

        add(fileMenu);
    }

    private void addReloadMenuItemActionListener() {
        reloadMenuItem.addActionListener(e -> {
            try {
                jySrtTools.loadDrafts();
                jyTextPanel.getListPanel().reloadList(jySrtTools.getDrafts());
                jyTextPanel.getTextsPanel().setTexts(null);
            } catch (JySrtToolsException jye) {
                jyTextPanel.getActionPanel().enableButtons(false);
                JOptionPane.showMessageDialog(jySrtTools,
                        new ErrorMessagePanel(jye), "重新載入草稿錯誤", JOptionPane.ERROR_MESSAGE);
            }
            jyTextPanel.getActionPanel().enableButtons(false);
        });
    }

    private void addAboutMenuItemActionListener() {
        aboutMenuItem.addActionListener(e -> {
            Object[] options = {"知道了"};
            ImageIcon icon = null;
            try {
                Image image = ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("icon.png")));
                icon = new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(96, 96, Image.SCALE_DEFAULT));

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            JOptionPane.showOptionDialog(jySrtTools,
                    new AboutPanel(),
                    "關於本程式",
                    JOptionPane.OK_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    icon,
                    options,
                    options[0]);
        });
    }
}
