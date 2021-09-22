package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JyUtils;

import javax.swing.*;
import java.awt.*;

public class AboutPanel extends JPanel {
    public AboutPanel() {
        setLayout(new GridLayout(3, 1));
        add(new JLabel("<html><span style='font-size:16px'>作業系統: " + System.getProperty("os.name") + "</span></html>"));
        add(new JLabel("<html><span style='font-size:16px'>工具箱版本: " + JyUtils.getVersion() + "</span></html>"));
        add(new JLabel("<html><span style='font-size:16px'>剪映版本: " + JyUtils.getJyVersion() + "</span></html>"));
    }
}
