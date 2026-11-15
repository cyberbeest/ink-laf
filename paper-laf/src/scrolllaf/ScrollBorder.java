package scrolllaf;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

/** Double ink rule with small corner dots, like a ruled frame on a manuscript page. */
public class ScrollBorder extends AbstractBorder implements UIResource {
    private final int pad;

    public ScrollBorder(int pad) { this.pad = pad; }

    @Override public Insets getBorderInsets(Component c, Insets i) {
        int n = 6 + pad;
        i.set(n, n + 2, n, n + 2);
        return i;
    }

    @Override public Insets getBorderInsets(Component c) { return getBorderInsets(c, new Insets(0, 0, 0, 0)); }

    @Override public void paintBorder(Component c, Graphics g0, int x, int y, int w, int h) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        Color ink = c.isEnabled() ? new Color(0x6b4423) : new Color(0x8a8578);
        g.setColor(new Color(0x3b, 0x24, 0x12, 40));          // paper sunk in a little at the top
        g.fillRect(x + 1, y + 1, w - 2, 3);
        g.setColor(ink);
        g.setStroke(new BasicStroke(1.5f));
        g.draw(new Rectangle2D.Double(x + 0.75, y + 0.75, w - 1.5, h - 1.5));
        g.setStroke(new BasicStroke(0.7f));
        g.draw(new Rectangle2D.Double(x + 3.5, y + 3.5, w - 7, h - 7));
        for (int cx : new int[]{x + 2, x + w - 3})            // corner dots
            for (int cy : new int[]{y + 2, y + h - 3})
                g.fillOval(cx - 1, cy - 1, 3, 3);
        g.dispose();
    }
}
