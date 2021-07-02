package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JySrtTools;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class JyTextPanel extends JPanel {

    private JySrtTools jySrtTools;
    @Getter
    private DraftListPanel listPanel;
    @Getter
    private DraftTextsPanel textsPanel;
    @Getter
    private DraftActionPanel actionPanel;

    public JyTextPanel(JySrtTools jySrtTools) {
        setLayout(new BorderLayout());

        this.jySrtTools = jySrtTools;
        listPanel = new DraftListPanel(jySrtTools, this);
        add(listPanel, BorderLayout.WEST);

        textsPanel = new DraftTextsPanel(jySrtTools);
        add(textsPanel, BorderLayout.CENTER);

        actionPanel = new DraftActionPanel(jySrtTools, this);
        add(actionPanel, BorderLayout.EAST);
    }
}
