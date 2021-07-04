package app.jackychu.jysrttools.ui;


import javax.swing.*;
import java.awt.*;

public class ErrorMessagePanel extends JPanel {

    public ErrorMessagePanel(Throwable th) {
        super();
        String message = th.getMessage() == null ? th.toString() : th.getMessage();
        setLayout(new BorderLayout());
        String title = message.split("(?<=!)")[0];
        message += System.lineSeparator() + th.getClass();
        message += System.lineSeparator() + System.lineSeparator() + trimStackTrace(th.getStackTrace());
        JTextArea textArea = new JTextArea(message);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        JScrollPane jsp = new JScrollPane(textArea);
        jsp.setPreferredSize(new Dimension(600, 200));

        JButton btnCopy = new JButton("複製錯誤訊息到剪貼簿");
        btnCopy.addActionListener(e -> {
            textArea.selectAll();
            textArea.copy();
        });

        add(new JLabel("<html><span color='red' style='font-size:16px'>" + title + "</span></html>"), BorderLayout.NORTH);
        add(jsp, BorderLayout.CENTER);
        add(btnCopy, BorderLayout.SOUTH);
    }

    private String trimStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();

        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().startsWith("app.jackychu"))
                sb.append(element).append(System.lineSeparator());
        }

        return sb.toString();
    }
}
