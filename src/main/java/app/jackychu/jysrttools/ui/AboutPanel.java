package app.jackychu.jysrttools.ui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AboutPanel extends JPanel {
    public AboutPanel() {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("config.properties");
        Properties prop = new Properties();

        String version = "N/A";
        String jy_version = "N/A";

        try {
            prop.load(input);
            version = prop.getProperty("version");
            jy_version = prop.getProperty("jy_version");
        } catch (IOException e) {
            e.printStackTrace();
        }

        setLayout(new GridLayout(3,1));
        add(new JLabel("<html><span style='font-size:16px'>作業系統: " + System.getProperty("os.name") + "</span></html>"));
        add(new JLabel("<html><span style='font-size:16px'>工具箱版本: " + version + "</span></html>"));
        add(new JLabel("<html><span style='font-size:16px'>剪映版本: " + jy_version + "</span></html>"));
    }
}
