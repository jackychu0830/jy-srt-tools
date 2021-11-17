package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.Subtitle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Objects;

public class SubtitleCellEditor extends DefaultCellEditor {
    JTextField textField;

    public SubtitleCellEditor() {
        super(new JTextField());
        this.textField = (JTextField) this.getComponent();
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) e;
            return me.getClickCount() >= 2;
        } else if (e instanceof KeyEvent) {
            KeyEvent ke = (KeyEvent) e;
            return ke.getKeyCode() == KeyEvent.VK_ENTER;
        } else if (e instanceof ActionEvent) {
            return Objects.equals(((ActionEvent) e).getActionCommand(), "\n");
        }
        return false;
    }

    @Override
    public Object getCellEditorValue() {

        return this.textField.getText();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value, boolean isSelected, int row, int column) {
        this.textField.setText(((Subtitle) value).getText());
        return this.textField;
    }

    public void focus() {
        this.textField.grabFocus();
    }
}
