package inklaf;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

/** A faint rule for the track; the thumb is a hatched-in box. */
public class InkScrollBarUI extends BasicScrollBarUI {
    public static ComponentUI createUI(JComponent c) { return new InkScrollBarUI(); }

    /** Current thumb rectangle (exposed for repaint tests). */
    public Rectangle thumbRect() { return getThumbBounds(); }

    @Override public void update(Graphics g, JComponent c) { InkUIs.bg(g, c); paint(g, c); }

    @Override protected JButton createDecreaseButton(int o) { return new InkArrowButton(o); }
    @Override protected JButton createIncreaseButton(int o) { return new InkArrowButton(o); }

    @Override protected void paintTrack(Graphics g0, JComponent c, Rectangle r) {
        Graphics2D g = Pen.aa(g0);
        g.clipRect(r.x, r.y, r.width, r.height);
        InkPaper.paint(g, c);
        boolean vert = scrollbar.getOrientation() == Adjustable.VERTICAL;
        Color line = Pen.alpha(Pen.INK, 0.35);
        if (vert) Pen.line(g, r.x + r.width / 2.0, r.y, r.x + r.width / 2.0, r.y + r.height, 1.1, r.height, line);
        else Pen.line(g, r.x, r.y + r.height / 2.0, r.x + r.width, r.y + r.height / 2.0, 1.1, r.width, line);
        g.dispose();
    }

    @Override protected void paintThumb(Graphics g0, JComponent c, Rectangle r) {
        if (r.isEmpty() || !scrollbar.isEnabled()) return;
        Graphics2D g = Pen.aa(g0);
        g.clipRect(r.x, r.y, r.width, r.height);          // strokes wobble; never leave the repainted rect
        g.translate(r.x, r.y);
        long seed = r.width * 7L + r.height;
        Shape inner = new RoundRectangle2D.Double(5, 5, r.width - 10, r.height - 10, 4, 4);
        Pen.hatch(g, inner, isDragging ? 3.5 : 5, 0.9, 1.1, seed, Pen.alpha(Pen.INK, isDragging ? 0.7 : 0.5));
        Pen.rect(g, 3.5, 3.5, r.width - 7, r.height - 7, 1.8, seed + 1, isDragging ? Pen.RUBRIC : Pen.INK);
        g.dispose();
    }
}
