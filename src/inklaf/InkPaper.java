package inklaf;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * The parchment itself. Everything is painted in window (root pane) coordinates, so partial repaints
 * of any component line up exactly and the whole window reads as one sheet. Slips are lighter pieces on top.
 */
final class InkPaper {
    static final Color BASE = new Color(0xefdfb0), EDGE = new Color(0xc9a566), SLIP = new Color(0xf7edcb);

    private static TexturePaint tex, texSlip;

    private InkPaper() {}

    private static TexturePaint texture(boolean slip) {
        if (tex == null) {
            tex = new TexturePaint(makeTile(1), new Rectangle(0, 0, 256, 256));
            texSlip = new TexturePaint(makeTile(2), new Rectangle(37, 91, 256, 256));
        }
        return slip ? texSlip : tex;
    }

    /** Seamless fibre/speckle tile: everything is drawn with wrap-around. */
    private static BufferedImage makeTile(long seed) {
        int n = 256;
        BufferedImage img = new BufferedImage(n, n, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Pen.Rnd r = new Pen.Rnd(seed);
        for (int i = 0; i < 2600; i++) {
            double x = r.range(0, n), y = r.range(0, n), s = r.range(0.7, 2.2);
            boolean light = r.next() < 0.3;
            g.setColor(light ? new Color(255, 248, 225, (int) r.range(14, 36)) : new Color(0x5a, 0x3a, 0x1a, (int) r.range(6, 22)));
            for (int dx = -n; dx <= n; dx += n) for (int dy = -n; dy <= n; dy += n) g.fill(new Ellipse2D.Double(x + dx, y + dy, s, s * r.range(0.6, 1)));
        }
        g.setStroke(new BasicStroke(0.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 90; i++) {   // fibres
            double x = r.range(0, n), y = r.range(0, n), a = r.range(0, 6.28), len = r.range(8, 30), bend = r.sym(0.6);
            g.setColor(new Color(0x6b, 0x4a, 0x24, (int) r.range(5, 13)));
            for (int dx = -n; dx <= n; dx += n) for (int dy = -n; dy <= n; dy += n)
                g.draw(new QuadCurve2D.Double(x + dx, y + dy, x + dx + Math.cos(a + bend) * len / 2, y + dy + Math.sin(a + bend) * len / 2, x + dx + Math.cos(a) * len, y + dy + Math.sin(a) * len));
        }
        g.dispose();
        return img;
    }

    static boolean isSlipRoot(Component p) {
        if (p instanceof JScrollPane || p instanceof JPopupMenu || p instanceof JToolTip || p instanceof JTabbedPane) return true;
        if (p instanceof JComponent jc) {
            Border b = jc.getBorder();
            if (b instanceof TitledBorder tb) b = tb.getBorder();
            return b instanceof InkBorders.Slip;
        }
        return false;
    }

    private static boolean onSlip(Component c) {
        for (Component p = c.getParent(); p != null; p = p.getParent()) {
            if (isSlipRoot(p)) return true;
            if (p instanceof JRootPane) break;
        }
        return false;
    }

    private static Component rootOf(Component c) {
        JRootPane r = SwingUtilities.getRootPane(c);
        if (r != null) return r;
        Component p = c;
        while (p.getParent() != null) p = p.getParent();
        return p;
    }

    /** Paints this component's area as paper: the main sheet, or slip paper when it sits on a slip. */
    static void paint(Graphics g0, Component c) {
        Graphics2D g = (Graphics2D) g0.create();
        Component root = rootOf(c);
        Point o = SwingUtilities.convertPoint(c, 0, 0, root);
        g.translate(-o.x, -o.y);
        Rectangle clip = g.getClipBounds();
        if (clip == null) clip = new Rectangle(o.x, o.y, c.getWidth(), c.getHeight());
        int W = Math.max(1, root.getWidth()), H = Math.max(1, root.getHeight());
        boolean slip = onSlip(c);
        g.setColor(slip ? SLIP : BASE);
        g.fill(clip);
        if (!slip) {
            AffineTransform t = new AffineTransform();
            t.translate(W / 2.0, H / 2.0);
            t.scale(W * 0.72, H * 0.72);
            g.setPaint(new RadialGradientPaint(new Point2D.Double(0, 0), 1f, new Point2D.Double(0, 0), new float[]{0f, 0.55f, 1f},
                    new Color[]{new Color(0xc9, 0xa5, 0x66, 0), new Color(0xc9, 0xa5, 0x66, 18), new Color(0xb5, 0x8a, 0x48, 120)},
                    MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, t));
            g.fill(clip);
            Pen.Rnd r = new Pen.Rnd(W * 31L + H);                    // water rings / stains
            for (int i = 0; i < 7; i++) {
                double cx = r.range(0.05, 0.95) * W, cy = r.range(0.05, 0.95) * H, rad = r.range(40, 120), a = r.range(0.04, 0.09);
                if (!clip.intersects(cx - rad, cy - rad, rad * 2, rad * 2)) continue;
                g.setPaint(new RadialGradientPaint(new Point2D.Double(cx, cy), (float) rad, new float[]{0f, 0.72f, 0.92f, 1f},
                        new Color[]{new Color(0x8a, 0x5a, 0x20, 0), new Color(0x8a, 0x5a, 0x20, (int) (255 * a * 0.4)), new Color(0x8a, 0x5a, 0x20, (int) (255 * a)), new Color(0x8a, 0x5a, 0x20, 0)}));
                g.fill(new Ellipse2D.Double(cx - rad, cy - rad, rad * 2, rad * 2));
            }
        }
        g.setPaint(texture(slip));
        g.fill(clip);
        g.dispose();
    }

    /** Jagged torn outline around (x, y, w, h); the same seed always tears the same way. */
    static Path2D torn(double x, double y, double w, double h, long seed, double amp) {
        Pen.Rnd r = new Pen.Rnd(seed);
        Path2D.Double p = new Path2D.Double();
        double[][] corners = {{x, y}, {x + w, y}, {x + w, y + h}, {x, y + h}};
        boolean first = true;
        for (int side = 0; side < 4; side++) {
            double[] a = corners[side], b = corners[(side + 1) % 4];
            double len = Math.hypot(b[0] - a[0], b[1] - a[1]), ux = (b[0] - a[0]) / len, uy = (b[1] - a[1]) / len, nx = uy, ny = -ux;
            for (double s = 0; s < len; s += r.range(3.5, 8)) {
                double o = r.sym(amp);
                double px = a[0] + ux * s + nx * o, py = a[1] + uy * s + ny * o;
                if (first) { p.moveTo(px, py); first = false; } else p.lineTo(px, py);
            }
        }
        p.closePath();
        return p;
    }

    /** A paper slip: soft shadow, slip paper, torn edge with a pale fringe. */
    static void slip(Graphics2D g0, Component c, double x, double y, double w, double h, long seed) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Path2D s = torn(x, y, w, h, seed, 2.2);
        for (int k = 3; k >= 1; k--) {
            g.setColor(new Color(0x2b, 0x1b, 0x10, 22));
            g.fill(AffineTransform.getTranslateInstance(k * 1.0 + 0.4, k * 1.0 + 0.6).createTransformedShape(s));
        }
        g.setColor(SLIP);
        g.fill(s);
        g.setPaint(texture(true));
        g.fill(s);
        Shape old = g.getClip();
        g.clip(s);
        g.setColor(new Color(255, 250, 232, 150));
        g.setStroke(new BasicStroke(3.4f));
        g.draw(s);                                          // pale torn-fibre rim
        g.setClip(old);
        g.setColor(new Color(0x6b, 0x44, 0x23, 95));
        g.setStroke(new BasicStroke(0.8f));
        g.draw(s);
        g.dispose();
    }
}
