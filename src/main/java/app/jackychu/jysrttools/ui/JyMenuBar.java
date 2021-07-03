package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JySrtTools;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Objects;

public class JyMenuBar extends JMenuBar {
    private final JySrtTools jySrtTools;
    private final JMenuItem aboutMenuItem;

    public JyMenuBar(JySrtTools jySrtTools) {
        super();

        this.jySrtTools = jySrtTools;
        JMenu fileMenu = new JMenu("檔案");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        aboutMenuItem = new JMenuItem("關於");
        aboutMenuItem.setMnemonic(KeyEvent.VK_A);
        aboutMenuItem.setToolTipText("關於本程式");
        addAboutMenuItemActionListener();
        fileMenu.add(aboutMenuItem);

        JMenuItem exiMenuItem = new JMenuItem("結束");
        exiMenuItem.setMnemonic(KeyEvent.VK_E);
        exiMenuItem.setToolTipText("結束程式");
        exiMenuItem.addActionListener((event) -> System.exit(0));
        fileMenu.add(exiMenuItem);

        add(fileMenu);
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
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    icon,
                    options,
                    options[0]);
        });
    }
}
