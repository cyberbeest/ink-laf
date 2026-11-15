package inklaf;

import java.awt.*;
import java.awt.geom.*;

/** Hand-drawn strokes: wobbling path, broad-nib width variation, tapered ends. Deterministic per seed. */
final class Pen {
    static final Color INK = new Color(0x2b1b10), RUBRIC = new Color(0x8e1b12), GREY = new Color(0x2b, 0x1b, 0x10, 105);
    static final double NIB = 0.6;   // nib angle in radians: strokes along it are thin, across it thick

    private Pen() {}

    /** Small xorshift RNG so every drawing is repeatable from a seed. */
    static final class Rnd {
        private long s;
        Rnd(long seed) {
            s = seed * 0x9E3779B97F4A7C15L + 0x2545F4914F6CDD1DL;
            if (s == 0) s = 1;
            for (int i = 0; i < 4; i++) next();
        }
        double next() { s ^= s << 13; s ^= s >>> 7; s ^= s << 17; return (s >>> 11) * (1.0 / (1L << 53)); }
        double range(double a, double b) { return a + (b - a) * next(); }
        double sym(double a) { return range(-a, a); }
    }

    static Graphics2D aa(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        return g;
    }

    /** Per-control variation that survives re-runs and resizes: from the control's slot in its parent and its label. */
    static long seedFor(Component c) {
        long s = c.getClass().getSimpleName().hashCode();
        Container p = c.getParent();
        if (p != null) s += p.getComponentZOrder(c) * 7919L;
        if (c instanceof javax.swing.AbstractButton b && b.getText() != null) s += b.getText().hashCode() * 31L;
        return s;
    }

    static Color alpha(Color c, double a) { return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) Math.round(255 * a * c.getAlpha() / 255.0)); }

    /** One stroke along a polyline. */
    static void stroke(Graphics2D g, double[] xs, double[] ys, double width, long seed, Color color) {
        stroke(g, xs, ys, width, 0.7, seed, color);
    }

    static void stroke(Graphics2D g, double[] xs, double[] ys, double width, double wobble, long seed, Color color) {
        int n = xs.length;
        double total = 0;
        for (int i = 0; i < n - 1; i++) total += Math.hypot(xs[i + 1] - xs[i], ys[i + 1] - ys[i]);
        g.setColor(color);
        if (total < 0.01) { g.fill(new Ellipse2D.Double(xs[0] - width / 2, ys[0] - width / 2, width, width)); return; }
        int m = (int) Math.ceil(total / 1.5) + 1;   // fixed step, so a longer stroke is the shorter one extended (stable repaints)
        double[] px = new double[m], py = new double[m], ps = new double[m];
        int seg = 0;
        double segStart = 0, segLen = Math.hypot(xs[1] - xs[0], ys[1] - ys[0]);
        for (int i = 0; i < m; i++) {
            double s = Math.min(total, i * 1.5);
            while (seg < n - 2 && s > segStart + segLen) { segStart += segLen; seg++; segLen = Math.hypot(xs[seg + 1] - xs[seg], ys[seg + 1] - ys[seg]); }
            double t = segLen == 0 ? 0 : Math.min(1, (s - segStart) / segLen);
            px[i] = xs[seg] + (xs[seg + 1] - xs[seg]) * t;
            py[i] = ys[seg] + (ys[seg + 1] - ys[seg]) * t;
            ps[i] = s;
        }
        Rnd r = new Rnd(seed);
        double p1 = r.range(0, 6.28), p2 = r.range(0, 6.28), p3 = r.range(0, 6.28);
        double f1 = r.range(0.05, 0.09), f2 = r.range(0.13, 0.21), f3 = r.range(0.08, 0.14);
        double[] lx = new double[m], ly = new double[m], rx = new double[m], ry = new double[m], ww = new double[m];
        for (int i = 0; i < m; i++) {
            double tx = px[Math.min(i + 1, m - 1)] - px[Math.max(i - 1, 0)], ty = py[Math.min(i + 1, m - 1)] - py[Math.max(i - 1, 0)];
            double tl = Math.hypot(tx, ty);
            tx /= tl; ty /= tl;
            double nx = -ty, ny = tx;
            double amp = wobble * Math.min(1, Math.min(ps[i] / 15, (total - ps[i]) / 15));   // eases in/out near the ends only
            double off = amp * (Math.sin(ps[i] * f1 + p1) + 0.5 * Math.sin(ps[i] * f2 + p2));
            double nib = 0.6 + 0.4 * Math.abs(Math.sin(Math.atan2(ty, tx) - NIB));
            double taper = 0.5 + 0.5 * Math.min(1, Math.min(ps[i] / 6, (total - ps[i]) / 6));
            double w = width * nib * taper * (0.88 + 0.12 * Math.sin(ps[i] * f3 + p3));
            double cx = px[i] + nx * off, cy = py[i] + ny * off;
            lx[i] = cx + nx * w / 2; ly[i] = cy + ny * w / 2;
            rx[i] = cx - nx * w / 2; ry[i] = cy - ny * w / 2;
            ww[i] = w;
        }
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(lx[0], ly[0]);
        for (int i = 1; i < m; i++) poly.lineTo(lx[i], ly[i]);
        for (int i = m - 1; i >= 0; i--) poly.lineTo(rx[i], ry[i]);
        poly.closePath();
        g.fill(poly);
        for (int i : new int[]{0, m - 1}) {   // round ends
            double cx = (lx[i] + rx[i]) / 2, cy = (ly[i] + ry[i]) / 2;
            g.fill(new Ellipse2D.Double(cx - ww[i] / 2, cy - ww[i] / 2, ww[i], ww[i]));
        }
    }

    static void line(Graphics2D g, double x1, double y1, double x2, double y2, double w, long seed, Color c) {
        stroke(g, new double[]{x1, x2}, new double[]{y1, y2}, w, seed, c);
    }

    /** Four loosely joined sides whose corners overshoot and miss, as drawn freehand. */
    static void rect(Graphics2D g, double x, double y, double w, double h, double lw, long seed, Color c) {
        Rnd r = new Rnd(seed);
        double j = Math.min(1.3, Math.min(w, h) / 10);
        double[] cx = {x + r.sym(j), x + w + r.sym(j), x + w + r.sym(j), x + r.sym(j)};
        double[] cy = {y + r.sym(j), y + r.sym(j), y + h + r.sym(j), y + h + r.sym(j)};
        for (int i = 0; i < 4; i++) {
            int k = (i + 1) % 4;
            double dx = cx[k] - cx[i], dy = cy[k] - cy[i], len = Math.hypot(dx, dy);
            double ux = dx / len, uy = dy / len, o1 = r.range(0.4, 2.2), o2 = r.range(0.4, 2.2);
            stroke(g, new double[]{cx[i] - ux * o1, cx[k] + ux * o2}, new double[]{cy[i] - uy * o1, cy[k] + uy * o2}, lw, seed * 7 + i, c);
        }
    }

    /** A loop that doesn't quite close, like a quickly drawn circle. */
    static void ellipse(Graphics2D g, double cx, double cy, double rx, double ry, double lw, long seed, Color c) {
        Rnd r = new Rnd(seed);
        int n = Math.max(18, (int) (Math.PI * (rx + ry) / 2));
        double start = r.range(0, 6.28), sweep = 6.28 + r.range(0.15, 0.5), drift = r.sym(0.12), ph = r.range(0, 6.28);
        double[] xs = new double[n], ys = new double[n];
        for (int i = 0; i < n; i++) {
            double t = (double) i / (n - 1), a = start + sweep * t, k = 1 + drift * t + 0.025 * Math.sin(3 * a + ph);
            xs[i] = cx + Math.cos(a) * rx * k;
            ys[i] = cy + Math.sin(a) * ry * k;
        }
        stroke(g, xs, ys, lw, 0.4, seed + 5, c);
    }

    static void polyline(Graphics2D g, double[] xs, double[] ys, double lw, long seed, Color c) { stroke(g, xs, ys, lw, seed, c); }

    /** Diagonal pen hatching inside an area. */
    static void hatch(Graphics2D g0, Shape area, double spacing, double angle, double lw, long seed, Color c) {
        Graphics2D g = (Graphics2D) g0.create();
        g.clip(area);
        Rectangle2D b = area.getBounds2D();
        double cx = b.getCenterX(), cy = b.getCenterY(), d = Math.hypot(b.getWidth(), b.getHeight()) / 2 + 2;
        double dx = Math.cos(angle), dy = Math.sin(angle), nx = -dy, ny = dx;
        int i = 0;
        for (double t = -d; t <= d; t += spacing, i++)
            stroke(g, new double[]{cx + nx * t - dx * d, cx + nx * t + dx * d}, new double[]{cy + ny * t - dy * d, cy + ny * t + dy * d}, lw, 0.5, seed + i, c);
        g.dispose();
    }

    /** Wavy underline / divider. */
    static void wave(Graphics2D g, double x1, double x2, double y, double amp, double wavelength, double lw, long seed, Color c) {
        int n = Math.max(4, (int) ((x2 - x1) / 2));
        double[] xs = new double[n], ys = new double[n];
        for (int i = 0; i < n; i++) {
            xs[i] = x1 + (x2 - x1) * i / (n - 1);
            ys[i] = y + Math.sin((xs[i] - x1) / wavelength * 6.283) * amp;
        }
        stroke(g, xs, ys, lw, 0.3, seed, c);
    }
}
