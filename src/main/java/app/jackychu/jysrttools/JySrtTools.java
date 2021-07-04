package app.jackychu.jysrttools;

import app.jackychu.jysrttools.exception.JySrtToolsException;
import app.jackychu.jysrttools.ui.*;
import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class JySrtTools extends JFrame {
    @Getter
    private Map<String, JyDraft> drafts;
    @Getter
    @Setter
    private JyDraft currentSelectedDraft = null;
    @Getter
    private TranslateProgressDialog progressDialog;

    @Getter
    private JyTextPanel jyTextPanel;
    @Getter
    private JyFontPanel jyFontPanel;

    public JySrtTools() {
        try {
            loadDrafts();
            init();
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(this,
                    new ErrorMessagePanel(e), "程式錯誤", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
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

    private void init() {
        setLayout(new BorderLayout());

        setTitle("剪映字幕工具箱");
        setSize(1280, 768);
        setMinimumSize(new Dimension(1080, 285));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 限制主視窗無法縮的比 minimum size 還要小
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

        jyTextPanel = new JyTextPanel(this);
        jyFontPanel = new JyFontPanel(this);
        JTabbedPane tabPanel = new JTabbedPane();
        tabPanel.setFont(tabPanel.getFont().deriveFont(16f));
        tabPanel.add("草稿文字處理", jyTextPanel);
        tabPanel.add("剪映字型管理", jyFontPanel);
        add(tabPanel);
        setJMenuBar(new JyMenuBar(this));

        progressDialog = new TranslateProgressDialog(this, true);
    }

    public void loadDrafts() throws JySrtToolsException {
        drafts = JyUtils.getAllJyDrafts();
    }
}
