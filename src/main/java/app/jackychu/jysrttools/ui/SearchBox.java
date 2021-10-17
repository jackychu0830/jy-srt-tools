package app.jackychu.jysrttools.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.util.Objects;

public class SearchBox extends JTextField implements KeyListener, MouseListener, MouseMotionListener {
    private static final int ICON_SPACING = 4;
    private Shape shape;
    private Border mBorder;
    private ImageIcon searchIcon;
    private ImageIcon cleanIcon;
    private boolean showCleanIcon = false;

    public SearchBox(int size) {
        super(size);
        try {
            searchIcon = resizeIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("images/search.png")))));
            cleanIcon = resizeIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("images/close.png")))));

            cleanIcon.setDescription("hello");
        } catch (IOException e) {
            e.printStackTrace();
        }
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        setOpaque(false);
        resetBorder();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        if (searchIcon != null) {
            Insets iconInsets = mBorder.getBorderInsets(this);
            searchIcon.paintIcon(this, g, iconInsets.left, iconInsets.top);
        }
        if (cleanIcon != null && showCleanIcon) {
            Insets iconInsets = mBorder.getBorderInsets(this);
            cleanIcon.paintIcon(this, g, this.getWidth() - cleanIcon.getIconWidth() - ICON_SPACING, iconInsets.top);
        }
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(getForeground());
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
    }

    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        }
        return shape.contains(x, y);
    }

    @Override
    public void setBorder(Border border) {
        mBorder = border;

        if (searchIcon == null && cleanIcon == null) {
            super.setBorder(border);
        } else {
            Border margin;
            if (cleanIcon == null) { // searchIcon
                margin = BorderFactory.createEmptyBorder(0, searchIcon.getIconWidth() + ICON_SPACING, 0, 0);
            } else {
                if (showCleanIcon && !getText().equals("")) {
                    if (searchIcon == null){
                        margin = BorderFactory.createEmptyBorder(0, 0, 0, cleanIcon.getIconWidth() + ICON_SPACING);
                    } else {
                        margin = BorderFactory.createEmptyBorder(0, searchIcon.getIconWidth() + ICON_SPACING, 0, cleanIcon.getIconWidth() + ICON_SPACING);
                    }
                } else {
                    if (searchIcon == null){
                        margin = BorderFactory.createEmptyBorder(0, 0, 0, 0);
                    } else {
                        margin = BorderFactory.createEmptyBorder(0, searchIcon.getIconWidth() + ICON_SPACING, 0, 0);
                    }
                }
            }
            Border compound = BorderFactory.createCompoundBorder(border, margin);
            super.setBorder(compound);
        }
    }

    private ImageIcon resizeIcon(ImageIcon icon) {
        if (icon.getIconWidth() > 16) {
            return new ImageIcon(icon.getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT));
        } else {
            return icon;
        }
    }

    private void resetBorder() {
        setBorder(mBorder);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        showCleanIcon = !getText().equals("");
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            setText("");
            showCleanIcon = false;
        } else {
            showCleanIcon = !getText().equals("");
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (isOnCleanIcon(e.getLocationOnScreen())) {
            setText("");
            showCleanIcon = false;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (isOnCleanIcon(e.getLocationOnScreen())) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setToolTipText("清除搜尋 (也可按下 ESC 鍵)");
        } else {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private boolean isOnCleanIcon(Point p) {
        if (showCleanIcon) {
            Point sp = getLocationOnScreen();
            return p.x >= sp.x + getWidth() - cleanIcon.getIconWidth() - ICON_SPACING &&
                    p.x <= sp.x + getWidth() &&
                    p.y <= sp.y + getHeight() && p.y >= sp.y;
        } else {
            return false;
        }
    }
}
