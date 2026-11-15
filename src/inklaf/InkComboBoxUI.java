package inklaf;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class InkComboBoxUI extends BasicComboBoxUI {
    public static ComponentUI createUI(JComponent c) { return new InkComboBoxUI(); }

    @Override protected JButton createArrowButton() {
        JButton b = new InkArrowButton(SwingConstants.SOUTH);
        b.setName("ComboBox.arrowButton");
        return b;
    }

    @Override public void update(Graphics g, JComponent c) { InkUIs.bg(g, c); paint(g, c); }

    /** The renderer would fill the whole field with the selection colour when focused; the swipe above replaces that. */
    @Override public void paintCurrentValue(Graphics g, Rectangle r, boolean hasFocus) { super.paintCurrentValue(g, r, false); }

    @Override public void paintCurrentValueBackground(Graphics g0, Rectangle r, boolean focus) {
        if (!focus || comboBox.isPopupVisible()) return;           // otherwise just the page
        Graphics2D g = Pen.aa(g0);
        Object item = comboBox.getSelectedItem();                  // a highlighter swipe under the text, not the whole field
        int text = item == null ? 0 : comboBox.getFontMetrics(comboBox.getFont()).stringWidth(item.toString());
        int w = Math.min(r.width, Math.max(30, text + 14));
        g.setColor(InkLookAndFeel.MARKER);
        g.fillRoundRect(r.x, r.y + r.height / 3, w, r.height * 2 / 3 - 2, 6, 6);
        g.dispose();
    }
}
