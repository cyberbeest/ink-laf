package scrolllaf;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

/** Paper groove filled with red-brown ink. */
public class ScrollProgressBarUI extends BasicProgressBarUI {
    public static ComponentUI createUI(JComponent c) { return new ScrollProgressBarUI(); }

    private Shape groove(int x, int y, int w, int h) {
        double arc = Math.min(w, h) * 0.6;
        return new RoundRectangle2D.Double(x + .5, y + .5, w - 1, h - 1, arc, arc);
    }

    private void drawGroove(Graphics2D g, Shape s) {
        Rectangle2D b = s.getBounds2D();
        g.setPaint(new GradientPaint(0, (float) b.getY(), new Color(0xcdb479), 0, (float) b.getMaxY(), new Color(0xf3e4b6)));
        g.fill(s);
    }

    private void drawFill(Graphics2D g, Shape groove, Rectangle fill, boolean horiz) {
        Shape old = g.getClip();
        g.clip(groove);
        g.setPaint(horiz ? new GradientPaint(0, fill.y, new Color(0xb8482f), 0, fill.y + fill.height, new Color(0x6e1a10))
                         : new GradientPaint(fill.x, 0, new Color(0xb8482f), fill.x + fill.width, 0, new Color(0x6e1a10)));
        g.fill(fill);
        g.setColor(new Color(255, 255, 255, 60));              // wet-ink sheen
        if (horiz) g.fillRect(fill.x, fill.y + 2, fill.width, Math.max(2, fill.height / 5));
        else g.fillRect(fill.x + 2, fill.y, Math.max(2, fill.width / 5), fill.height);
        g.setClip(old);
    }

    @Override protected void paintDeterminate(Graphics g0, JComponent c) {
        Graphics2D g = ScrollArt.aa(g0);
        Insets b = progressBar.getInsets();
        int w = progressBar.getWidth() - (b.left + b.right), h = progressBar.getHeight() - (b.top + b.bottom);
        if (w <= 0 || h <= 0) { g.dispose(); return; }
        boolean horiz = progressBar.getOrientation() == SwingConstants.HORIZONTAL;
        int full = getAmountFull(b, w, h);
        Shape gr = groove(b.left, b.top, w, h);
        drawGroove(g, gr);
        if (full > 0) drawFill(g, gr, horiz ? new Rectangle(b.left, b.top, full, h) : new Rectangle(b.left, b.top + h - full, w, full), horiz);
        g.setColor(ScrollArt.INK_SOFT);
        g.setStroke(new BasicStroke(1.2f));
        g.draw(gr);
        if (progressBar.isStringPainted()) paintString(g, b.left, b.top, w, h, full, b);
        g.dispose();
    }

    @Override protected void paintIndeterminate(Graphics g0, JComponent c) {
        Graphics2D g = ScrollArt.aa(g0);
        Insets b = progressBar.getInsets();
        int w = progressBar.getWidth() - (b.left + b.right), h = progressBar.getHeight() - (b.top + b.bottom);
        if (w <= 0 || h <= 0) { g.dispose(); return; }
        Shape gr = groove(b.left, b.top, w, h);
        drawGroove(g, gr);
        Rectangle box = getBox(new Rectangle());
        if (box != null) drawFill(g, gr, box, progressBar.getOrientation() == SwingConstants.HORIZONTAL);
        g.setColor(ScrollArt.INK_SOFT);
        g.setStroke(new BasicStroke(1.2f));
        g.draw(gr);
        if (progressBar.isStringPainted() && progressBar.getOrientation() == SwingConstants.HORIZONTAL) paintString(g, b.left, b.top, w, h, 0, b);
        g.dispose();
    }
}
