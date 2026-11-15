package inklaf;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;

/** A pen line with a hand-drawn ring as the knob. */
public class InkSliderUI extends BasicSliderUI {
    public InkSliderUI(JSlider s) { super(s); }

    public static ComponentUI createUI(JComponent c) { return new InkSliderUI((JSlider) c); }

    /** Current thumb rectangle (exposed for repaint tests). */
    public Rectangle thumbRect() { return thumbRect; }

    @Override protected Dimension getThumbSize() { return new Dimension(26, 26); }

    @Override public void update(Graphics g, JComponent c) { InkUIs.bg(g, c); paint(g, c); }

    @Override public void paintTrack(Graphics g0) {
        Graphics2D g = Pen.aa(g0);
        boolean horiz = slider.getOrientation() == SwingConstants.HORIZONTAL;
        Rectangle r = trackRect;
        Color ink = slider.isEnabled() ? Pen.INK : Pen.GREY;
        long seed = r.width * 3L + r.height;
        if (horiz) {
            double y = r.y + r.height / 2.0, tx = thumbRect.x + thumbRect.width / 2.0;
            Pen.line(g, r.x, y, r.x + r.width, y, 1.6, seed, Pen.alpha(ink, 0.8));
            Pen.line(g, r.x, y + 1.5, tx, y + 1.5, 2.6, seed + 1, ink);                   // inked up to the knob
        } else {
            double x = r.x + r.width / 2.0, ty = thumbRect.y + thumbRect.height / 2.0;
            Pen.line(g, x, r.y, x, r.y + r.height, 1.6, seed, Pen.alpha(ink, 0.8));
            Pen.line(g, x + 1.5, r.y + r.height, x + 1.5, ty, 2.6, seed + 1, ink);   // from the fixed end, so repaints agree
        }
        g.dispose();
    }

    @Override public void paintThumb(Graphics g0) {
        Graphics2D g = Pen.aa(g0);
        g.clipRect(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
        double cx = thumbRect.x + thumbRect.width / 2.0, cy = thumbRect.y + thumbRect.height / 2.0;
        boolean on = slider.isEnabled();
        long seed = thumbRect.width * 11L + 3;
        Color ink = on ? Pen.INK : Pen.GREY;
        Pen.hatch(g, new java.awt.geom.Ellipse2D.Double(cx - 7, cy - 7, 14, 14), 3.5, 0.9, 1, seed, Pen.alpha(on ? Pen.RUBRIC : Pen.GREY, 0.75));
        Pen.ellipse(g, cx, cy, 8.3, 8.3, 1.8, seed + 2, ink);
        g.dispose();
    }
}
