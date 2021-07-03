package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.JySrtTools;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class JyTextPanel extends JPanel {

    @Getter
    private final DraftListPanel listPanel;
    @Getter
    private final DraftTextsPanel textsPanel;
    @Getter
    private final DraftActionPanel actionPanel;

    public JyTextPanel(JySrtTools jySrtTools) {
        setLayout(new BorderLayout());

        listPanel = new DraftListPanel(jySrtTools, this);
        add(listPanel, BorderLayout.WEST);

        textsPanel = new DraftTextsPanel(jySrtTools);
        add(textsPanel, BorderLayout.CENTER);

        actionPanel = new DraftActionPanel(jySrtTools, this);
        add(actionPanel, BorderLayout.EAST);
    }
}
