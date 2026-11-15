package inklaf;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

/** A sketched trough that gets hatched in as it fills. */
public class InkProgressBarUI extends BasicProgressBarUI {
    public static ComponentUI createUI(JComponent c) { return new InkProgressBarUI(); }

    @Override public void update(Graphics g, JComponent c) { InkUIs.bg(g, c); paint(g, c); }

    private void trough(Graphics2D g, int x, int y, int w, int h) {
        Pen.rect(g, x + 3, y + 3, w - 6, h - 6, 1.8, w * 5L + h, Pen.INK);
    }

    private void fill(Graphics2D g, Rectangle r, boolean horiz) {
        if (r.width <= 0 || r.height <= 0) return;
        Rectangle2D inner = new Rectangle2D.Double(r.x, r.y, r.width, r.height);
        Pen.hatch(g, inner, 3.6, horiz ? 0.9 : 0.9, 1.3, r.width * 3L + r.height, Pen.alpha(Pen.RUBRIC, 0.85));
    }

    @Override protected void paintDeterminate(Graphics g0, JComponent c) {
        Graphics2D g = Pen.aa(g0);
        Insets b = progressBar.getInsets();
        int w = progressBar.getWidth() - (b.left + b.right), h = progressBar.getHeight() - (b.top + b.bottom);
        if (w <= 0 || h <= 0) { g.dispose(); return; }
        boolean horiz = progressBar.getOrientation() == SwingConstants.HORIZONTAL;
        int full = getAmountFull(b, w, h);
        trough(g, b.left, b.top, w, h);
        Rectangle f = horiz ? new Rectangle(b.left + 5, b.top + 6, Math.max(0, full - 8), h - 12)
                            : new Rectangle(b.left + 6, b.top + h - full + 3, w - 12, Math.max(0, full - 8));
        fill(g, f, horiz);
        if (progressBar.isStringPainted()) paintString(g, b.left, b.top, w, h, full, b);
        g.dispose();
    }

    @Override protected void paintIndeterminate(Graphics g0, JComponent c) {
        Graphics2D g = Pen.aa(g0);
        Insets b = progressBar.getInsets();
        int w = progressBar.getWidth() - (b.left + b.right), h = progressBar.getHeight() - (b.top + b.bottom);
        if (w <= 0 || h <= 0) { g.dispose(); return; }
        trough(g, b.left, b.top, w, h);
        Rectangle box = getBox(new Rectangle());
        if (box != null) {
            boolean horiz = progressBar.getOrientation() == SwingConstants.HORIZONTAL;
            fill(g, horiz ? new Rectangle(box.x + 2, box.y + 6, Math.max(0, box.width - 4), h - 12) : new Rectangle(box.x + 6, box.y + 2, w - 12, Math.max(0, box.height - 4)), horiz);
        }
        g.dispose();
    }
}
