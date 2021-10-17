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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class JySrtTools extends JFrame {
    private List<JyDraft> drafts;
    @Getter
    @Setter
    private JyDraft currentSelectedDraft = null;
    @Getter
    private TranslateProgressDialog progressDialog;
    @Getter
    private FindReplaceDialog findReplaceDialog;
    @Getter
    private JyTextPanel jyTextPanel;

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
        setMinimumSize(new Dimension(1385, 335));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setVisible(true);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);

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
            Image image = ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("images/icon.png")));
            setIconImage(new ImageIcon(image).getImage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        jyTextPanel = new JyTextPanel(this);
        add(jyTextPanel);
        setJMenuBar(new JyMenuBar(this));

        progressDialog = new TranslateProgressDialog(this, true);
        findReplaceDialog = new FindReplaceDialog(this, false);
    }

    public void loadDrafts() throws JySrtToolsException {
        drafts = JyUtils.getAllJyDrafts();
    }

    public List<JyDraft> getDrafts() {
        return getDrafts(new JyDraftLastModifiedTimeComparator(), false);
    }

    public List<JyDraft> getDrafts(Comparator<JyDraft> comp, boolean reverse) {
        if (reverse)
            drafts.sort(comp.reversed());
        else
            drafts.sort(comp);
        return drafts;
    }

    public List<JyDraft> filterDrafts(String text) {
        if (text.trim().equals("")) {
            for (JyDraft draft : drafts) {
                draft.setHidden(false);
            }
        } else {
            for (JyDraft draft : drafts) {
                draft.setHidden(!draft.getName().contains(text));
            }
        }

        return drafts;
    }
}
