package inklaf;

import java.awt.*;
import java.awt.geom.Path2D;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.View;

/** Index tabs sketched on the page; the selected page is a torn slip lying on the sheet. */
public class InkTabbedPaneUI extends BasicTabbedPaneUI {
    public static ComponentUI createUI(JComponent c) { return new InkTabbedPaneUI(); }

    /** Outline points of a tab in its own (u along the tab, v from outer edge to baseline) space. */
    private double[][] outline(int len, int thick) {
        double l = len, t = thick;
        return new double[][]{{0, t}, {0.6, 8}, {3.5, 2.5}, {8, 0.8}, {l - 8, 0.8}, {l - 3.5, 2.5}, {l - 0.6, 8}, {l, t}};
    }

    private double[][] map(int tp, int x, int y, int w, int h, double[][] uv) {
        double[] xs = new double[uv.length], ys = new double[uv.length];
        for (int i = 0; i < uv.length; i++) {
            double u = uv[i][0], v = uv[i][1];
            switch (tp) {
                case BOTTOM -> { xs[i] = x + u; ys[i] = y + h - v; }
                case LEFT -> { xs[i] = x + v; ys[i] = y + u; }
                case RIGHT -> { xs[i] = x + w - v; ys[i] = y + u; }
                default -> { xs[i] = x + u; ys[i] = y + v; }
            }
        }
        return new double[][]{xs, ys};
    }

    @Override protected void paintTabBackground(Graphics g0, int tp, int i, int x, int y, int w, int h, boolean sel) {
        Graphics2D g = Pen.aa(g0);
        boolean vert = tp == LEFT || tp == RIGHT;
        double[][] p = map(tp, x, y, w, h, outline(vert ? h : w, vert ? w : h));
        Path2D poly = new Path2D.Double();
        poly.moveTo(p[0][0], p[1][0]);
        for (int k = 1; k < p[0].length; k++) poly.lineTo(p[0][k], p[1][k]);
        poly.closePath();
        g.setColor(sel ? InkPaper.SLIP : new Color(0xe0c98f));
        g.fill(poly);
        g.setPaint(null);
        g.dispose();
    }

    @Override protected void paintTabBorder(Graphics g0, int tp, int i, int x, int y, int w, int h, boolean sel) {
        Graphics2D g = Pen.aa(g0);
        boolean vert = tp == LEFT || tp == RIGHT;
        double[][] p = map(tp, x, y, w, h, outline(vert ? h : w, vert ? w : h));
        Color ink = tabPane.isEnabledAt(i) ? Pen.INK : Pen.GREY;
        Pen.polyline(g, p[0], p[1], sel ? 2.0 : 1.4, x * 7L + y + i, sel ? ink : Pen.alpha(ink, 0.8));
        g.dispose();
    }

    @Override protected void paintContentBorder(Graphics g0, int tp, int sel) {
        Graphics2D g = Pen.aa(g0);
        Insets in = tabPane.getInsets();
        int x = in.left, y = in.top, w = tabPane.getWidth() - in.right - x, h = tabPane.getHeight() - in.bottom - y;
        switch (tp) {
            case LEFT -> { int a = calculateTabAreaWidth(tp, runCount, maxTabWidth); x += a; w -= a; }
            case RIGHT -> w -= calculateTabAreaWidth(tp, runCount, maxTabWidth);
            case BOTTOM -> h -= calculateTabAreaHeight(tp, runCount, maxTabHeight);
            default -> { int a = calculateTabAreaHeight(tp, runCount, maxTabHeight); y += a; h -= a; }
        }
        InkPaper.slip(g, tabPane, x + 2, y + 2, w - 9, h - 9, w * 31L + h);
        g.dispose();
    }

    @Override protected void paintText(Graphics g0, int tp, Font font, FontMetrics m, int i, String title, Rectangle tr, boolean sel) {
        Graphics2D g = Pen.aa(g0);
        g.setFont(font);
        View v = getTextViewForTab(i);
        if (v != null) { v.paint(g, tr); g.dispose(); return; }
        g.setColor(!tabPane.isEnabledAt(i) ? Pen.GREY : sel ? Pen.RUBRIC : Pen.INK);
        g.drawString(title, tr.x, tr.y + m.getAscent());
        g.dispose();
    }

    @Override protected void paintFocusIndicator(Graphics g, int tp, Rectangle[] rects, int i, Rectangle icon, Rectangle text, boolean sel) { }
}
