package app.jackychu.jysrttools.ui;

import app.jackychu.jysrttools.Subtitle;

import javax.swing.*;
import java.awt.*;

public class SubtitleCellEditor extends DefaultCellEditor {
    JTextField textField;

    public SubtitleCellEditor() {
        super(new JTextField());
        this.textField = (JTextField) this.getComponent();
    }

    @Override
    public Object getCellEditorValue() {

        return this.textField.getText();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value, boolean isSelected, int row, int column) {
        this.textField.setText(((Subtitle)value).getText());
        return this.textField;
    }
}
