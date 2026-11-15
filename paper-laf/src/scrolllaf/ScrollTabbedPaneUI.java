package scrolllaf;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.View;

/** Tabs are pieces of parchment; the selected one merges into the page, which has a ruled frame. */
public class ScrollTabbedPaneUI extends BasicTabbedPaneUI {
    public static ComponentUI createUI(JComponent c) { return new ScrollTabbedPaneUI(); }

    private Shape tabShape(int tp, int x, int y, int w, int h) {
        double r = 9, l = x + .5, rt = x + w - .5, t = y + .5, b = y + h - .5;
        Path2D p = new Path2D.Double();
        switch (tp) {
            case BOTTOM -> { p.moveTo(l, t); p.lineTo(l, b - r); p.quadTo(l, b, l + r, b); p.lineTo(rt - r, b); p.quadTo(rt, b, rt, b - r); p.lineTo(rt, t); }
            case LEFT -> { p.moveTo(rt, t); p.lineTo(l + r, t); p.quadTo(l, t, l, t + r); p.lineTo(l, b - r); p.quadTo(l, b, l + r, b); p.lineTo(rt, b); }
            case RIGHT -> { p.moveTo(l, t); p.lineTo(rt - r, t); p.quadTo(rt, t, rt, t + r); p.lineTo(rt, b - r); p.quadTo(rt, b, rt - r, b); p.lineTo(l, b); }
            default -> { p.moveTo(l, b); p.lineTo(l, t + r); p.quadTo(l, t, l + r, t); p.lineTo(rt - r, t); p.quadTo(rt, t, rt, t + r); p.lineTo(rt, b); }
        }
        return p;
    }

    @Override protected void paintTabBackground(Graphics g0, int tp, int i, int x, int y, int w, int h, boolean sel) {
        Graphics2D g = ScrollArt.aa(g0);
        Shape s = tabShape(tp, x, y, w, h);
        Shape closed = new Area(s);
        if (sel) { g.setColor(tabPane.getBackground()); g.fill(closed); }
        else {
            boolean on = tabPane.isEnabledAt(i);
            g.setPaint(new GradientPaint(0, y, on ? new Color(0xd8b97b) : new Color(0xccc6b6), 0, y + h, on ? new Color(0xb78d52) : new Color(0xa9a392)));
            g.fill(closed);
            g.setColor(new Color(0, 0, 0, 40));
            g.fillRect(x, y + h - 3, w, 3);
        }
        g.dispose();
    }

    @Override protected void paintTabBorder(Graphics g0, int tp, int i, int x, int y, int w, int h, boolean sel) {
        Graphics2D g = ScrollArt.aa(g0);
        g.setColor(ScrollArt.ink(tabPane.isEnabledAt(i)));
        g.setStroke(new BasicStroke(sel ? 1.6f : 1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(tabShape(tp, x, y, w, h));
        g.dispose();
    }

    @Override protected void paintContentBorder(Graphics g0, int tp, int sel) {
        Graphics2D g = ScrollArt.aa(g0);
        Insets in = tabPane.getInsets();
        int x = in.left, y = in.top, w = tabPane.getWidth() - in.right - x, h = tabPane.getHeight() - in.bottom - y;
        switch (tp) {
            case LEFT -> { int a = calculateTabAreaWidth(tp, runCount, maxTabWidth); x += a; w -= a; }
            case RIGHT -> w -= calculateTabAreaWidth(tp, runCount, maxTabWidth);
            case BOTTOM -> h -= calculateTabAreaHeight(tp, runCount, maxTabHeight);
            default -> { int a = calculateTabAreaHeight(tp, runCount, maxTabHeight); y += a; h -= a; }
        }
        g.setColor(ScrollArt.INK_SOFT);
        g.setStroke(new BasicStroke(1.6f));
        g.draw(new Rectangle2D.Double(x + .8, y + .8, w - 1.6, h - 1.6));
        g.setStroke(new BasicStroke(.7f));
        g.draw(new Rectangle2D.Double(x + 3.5, y + 3.5, w - 7, h - 7));
        if (sel >= 0 && sel < rects.length) {                        // open the frame under the selected tab
            Rectangle t = rects[sel];
            g.setColor(tabPane.getBackground());
            switch (tp) {
                case LEFT -> g.fillRect(x - 1, t.y + 1, 6, t.height - 2);
                case RIGHT -> g.fillRect(x + w - 5, t.y + 1, 6, t.height - 2);
                case BOTTOM -> g.fillRect(t.x + 1, y + h - 5, t.width - 2, 6);
                default -> g.fillRect(t.x + 1, y - 1, t.width - 2, 6);
            }
        }
        g.dispose();
    }

    @Override protected void paintText(Graphics g0, int tp, Font font, FontMetrics m, int i, String title, Rectangle tr, boolean sel) {
        Graphics2D g = ScrollArt.aa(g0);
        g.setFont(font);
        View v = getTextViewForTab(i);
        if (v != null) { v.paint(g, tr); g.dispose(); return; }
        g.setColor(!tabPane.isEnabledAt(i) ? ScrollArt.GREY_INK : sel ? ScrollArt.RUBRIC : ScrollArt.INK);
        g.drawString(title, tr.x, tr.y + m.getAscent());
        g.dispose();
    }

    @Override protected void paintFocusIndicator(Graphics g0, int tp, Rectangle[] rects, int i, Rectangle icon, Rectangle text, boolean sel) {
        if (!tabPane.hasFocus() || !sel) return;
        Graphics2D g = ScrollArt.aa(g0);
        g.setColor(ScrollArt.RUBRIC);
        g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{1f, 3f}, 0f));
        g.draw(new RoundRectangle2D.Double(text.x - 4, text.y - 2, text.width + 8, text.height + 4, 5, 5));
        g.dispose();
    }
}
