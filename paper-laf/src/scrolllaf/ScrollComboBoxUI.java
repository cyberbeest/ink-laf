package scrolllaf;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class ScrollComboBoxUI extends BasicComboBoxUI {
    public static ComponentUI createUI(JComponent c) { return new ScrollComboBoxUI(); }

    @Override protected JButton createArrowButton() {
        JButton b = new ScrollArrowButton(SwingConstants.SOUTH);
        b.setName("ComboBox.arrowButton");
        return b;
    }

    @Override public void paintCurrentValueBackground(Graphics g0, Rectangle r, boolean focus) {
        Graphics2D g = ScrollArt.aa(g0);
        Color c = !comboBox.isEnabled() ? new Color(0xe3dccb) : focus && !comboBox.isPopupVisible() ? ScrollArt.TAN : ScrollArt.PAPER;
        g.setColor(c);
        g.fillRect(r.x, r.y, r.width, r.height);
        g.dispose();
    }
}
