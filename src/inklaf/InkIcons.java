package inklaf;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.plaf.UIResource;

/** Checkbox and radio button marks, drawn fresh by hand for each instance. */
final class InkIcons {
    private InkIcons() {}

    private abstract static class Base implements Icon, UIResource {
        @Override public int getIconWidth() { return 28; }
        @Override public int getIconHeight() { return 28; }

        @Override public void paintIcon(Component c, Graphics g0, int x, int y) {
            AbstractButton b = (AbstractButton) c;
            ButtonModel m = b.getModel();
            Graphics2D g = Pen.aa(g0);
            g.translate(x, y);
            boolean on = b.isEnabled();
            draw(g, Pen.seedFor(c), m.isSelected(), on, m.isArmed() && m.isPressed(), on && m.isRollover());
            g.dispose();
        }

        abstract void draw(Graphics2D g, long seed, boolean sel, boolean on, boolean down, boolean hot);
    }

    static Icon checkBox() {
        return new Base() {
            @Override void draw(Graphics2D g, long seed, boolean sel, boolean on, boolean down, boolean hot) {
                Color ink = on ? Pen.INK : Pen.GREY;
                if (down) Pen.hatch(g, new RoundRectangle2D.Double(6, 7, 14, 14, 3, 3), 4, 0.9, 1, seed, Pen.alpha(ink, 0.5));
                Pen.rect(g, 4, 6, 18, 18, 1.9, seed, ink);
                if (hot) Pen.rect(g, 5, 7, 16, 16, 1.0, seed + 9, Pen.alpha(Pen.RUBRIC, 0.8));
                if (sel) Pen.polyline(g, new double[]{6, 10.5, 24}, new double[]{15, 21, 2}, 2.7, seed + 3, on ? Pen.RUBRIC : Pen.GREY);
            }
        };
    }

    static Icon radioButton() {
        return new Base() {
            @Override void draw(Graphics2D g, long seed, boolean sel, boolean on, boolean down, boolean hot) {
                Color ink = on ? Pen.INK : Pen.GREY;
                Pen.ellipse(g, 13, 15, 9, 9, 1.9, seed, ink);
                if (hot) Pen.ellipse(g, 13, 15, 8, 8, 1.0, seed + 9, Pen.alpha(Pen.RUBRIC, 0.8));
                if (sel || down) {                                   // quick spiral scribble fills the ring
                    int n = 30;
                    double[] xs = new double[n], ys = new double[n];
                    for (int i = 0; i < n; i++) {
                        double t = (double) i / (n - 1), a = t * 6.28 * 2.6, r = 5.2 * (1 - t) + 0.5;
                        xs[i] = 13 + Math.cos(a) * r;
                        ys[i] = 15 + Math.sin(a) * r;
                    }
                    Pen.polyline(g, xs, ys, 1.9, seed + 4, on ? Pen.RUBRIC : Pen.GREY);
                }
            }
        };
    }
}
