package app.jackychu.jysrttools.ui;


import org.apache.commons.text.WordUtils;

import javax.swing.*;
import java.awt.*;

public class ErrorMessagePanel extends JPanel {

    public ErrorMessagePanel(String message) {
        super();
        setLayout(new BorderLayout());
        String title = message.split("(?<=!)")[0];
        message = message.replaceFirst(title, "").trim();

        JTextArea textArea = new JTextArea(WordUtils.wrap(message, 50));

        JButton btnCopy = new JButton("複製錯誤訊息到剪貼簿");
        btnCopy.addActionListener(e -> {
            textArea.selectAll();
            textArea.copy();
        });

        add(new JLabel("<html><span color='red' style='font-size:16px'>" + title + "</span></html>"), BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(btnCopy, BorderLayout.SOUTH);
    }
}
