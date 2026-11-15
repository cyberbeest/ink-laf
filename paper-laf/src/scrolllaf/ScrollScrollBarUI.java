package scrolllaf;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

/** Track is a shaded groove in the page; the thumb is a little rolled scroll. */
public class ScrollScrollBarUI extends BasicScrollBarUI {
    public static ComponentUI createUI(JComponent c) { return new ScrollScrollBarUI(); }

    /** Current thumb rectangle (exposed for repaint tests). */
    public Rectangle thumbRect() { return getThumbBounds(); }

    @Override protected JButton createDecreaseButton(int o) { return new ScrollArrowButton(o); }
    @Override protected JButton createIncreaseButton(int o) { return new ScrollArrowButton(o); }

    @Override protected void paintTrack(Graphics g0, JComponent c, Rectangle r) {
        Graphics2D g = ScrollArt.aa(g0);
        boolean vert = scrollbar.getOrientation() == Adjustable.VERTICAL;
        Color dark = new Color(0xc2a56e), light = new Color(0xe3cf9b);
        g.setPaint(vert ? new GradientPaint(r.x, 0, dark, r.x + r.width, 0, light)
                        : new GradientPaint(0, r.y, dark, 0, r.y + r.height, light));
        g.fill(r);
        g.setColor(new Color(0x6b, 0x44, 0x23, 120));
        g.setStroke(new BasicStroke(1f));
        if (vert) { g.drawLine(r.x, r.y, r.x, r.y + r.height); g.drawLine(r.x + r.width - 1, r.y, r.x + r.width - 1, r.y + r.height); }
        else { g.drawLine(r.x, r.y, r.x + r.width, r.y); g.drawLine(r.x, r.y + r.height - 1, r.x + r.width, r.y + r.height - 1); }
        g.dispose();
    }

    @Override protected void paintThumb(Graphics g0, JComponent c, Rectangle r) {
        if (r.isEmpty() || !scrollbar.isEnabled()) return;
        Graphics2D g = ScrollArt.aa(g0);
        g.clipRect(r.x, r.y, r.width, r.height);    // never touch pixels outside the rect Swing repaints
        g.translate(r.x + 0.5, r.y + 0.5);          // keeps the 1px outline inside the rect
        int w = r.width - 1, h = r.height - 1;
        if (scrollbar.getOrientation() == Adjustable.VERTICAL) {
            ScrollPainter.paintRoll(g, 0, h, w, false, isDragging, true);
        } else {
            g.transform(new AffineTransform(0, 1, 1, 0, 0, 0));   // transpose: roll lies on its side
            ScrollPainter.paintRoll(g, 0, w, h, false, isDragging, true);
        }
        g.dispose();
    }
}
