package scrolllaf;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;

/** Groove with an ink-filled stretch up to the thumb, which is a wax seal. */
public class ScrollSliderUI extends BasicSliderUI {
    public ScrollSliderUI(JSlider s) { super(s); }

    public static ComponentUI createUI(JComponent c) { return new ScrollSliderUI((JSlider) c); }

    /** Current thumb rectangle (exposed for repaint tests). */
    public Rectangle thumbRect() { return thumbRect; }

    @Override protected Dimension getThumbSize() { return new Dimension(28, 28); }

    @Override public void paintTrack(Graphics g0) {
        Graphics2D g = ScrollArt.aa(g0);
        boolean horiz = slider.getOrientation() == SwingConstants.HORIZONTAL, on = slider.isEnabled();
        Rectangle r = trackRect;
        double t = 10;
        RoundRectangle2D groove = horiz ? new RoundRectangle2D.Double(r.x, r.y + (r.height - t) / 2, r.width, t, t, t)
                                        : new RoundRectangle2D.Double(r.x + (r.width - t) / 2, r.y, t, r.height, t, t);
        g.setPaint(horiz ? new GradientPaint(0, (float) groove.getY(), new Color(0xb89a60), 0, (float) groove.getMaxY(), new Color(0xe9d7a4))
                         : new GradientPaint((float) groove.getX(), 0, new Color(0xb89a60), (float) groove.getMaxX(), 0, new Color(0xe9d7a4)));
        g.fill(groove);
        Shape old = g.getClip();
        g.clip(groove);                                           // inked part
        g.setColor(on ? new Color(0x6b4423) : new Color(0x8a8578));
        if (horiz) g.fill(new Rectangle2D.Double(r.x, groove.getY(), thumbRect.x + thumbRect.width / 2.0 - r.x, t));
        else g.fill(new Rectangle2D.Double(groove.getX(), thumbRect.y + thumbRect.height / 2.0, t, r.y + r.height - thumbRect.y - thumbRect.height / 2.0));
        g.setClip(old);
        g.setColor(ScrollArt.ink(on));
        g.setStroke(new BasicStroke(1.2f));
        g.draw(groove);
        g.dispose();
    }

    @Override public void paintThumb(Graphics g0) {
        Graphics2D g = ScrollArt.aa(g0);
        g.clipRect(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);   // stay inside what Swing repaints
        ScrollArt.seal(g, thumbRect.x + thumbRect.width / 2.0 - 0.5, thumbRect.y + thumbRect.height / 2.0 - 1, 10, !slider.isEnabled());
        g.dispose();
    }
}
