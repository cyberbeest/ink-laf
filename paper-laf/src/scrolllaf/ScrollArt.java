package scrolllaf;

import java.awt.*;
import java.awt.geom.*;

/** Shared colours and drawing helpers for the Scroll UI delegates. */
final class ScrollArt {
    private ScrollArt() {}

    static final Color INK = new Color(0x3b2412), INK_SOFT = new Color(0x6b4423), RUBRIC = new Color(0x8e1b12),
            PAPER = new Color(0xf8ecc8), TAN = new Color(0xc9a468), GREY_INK = new Color(0x8a8578);

    static Graphics2D aa(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g;
    }

    /** Parchment fill: light on top, darker below; inverted when pressed. */
    static void paper(Graphics2D g, Shape s, boolean enabled, boolean hot, boolean down) {
        Rectangle2D b = s.getBounds2D();
        Color l = !enabled ? new Color(0xd9d3c6) : down ? new Color(0xd9bf8a) : hot ? new Color(0xfbeec3) : new Color(0xf6e8bd);
        Color d = !enabled ? new Color(0xb5afa0) : down ? new Color(0xa57e45) : new Color(0xd6b676);
        g.setPaint(down ? new GradientPaint(0, (float) b.getY(), d, 0, (float) b.getMaxY(), l)
                        : new GradientPaint(0, (float) b.getY(), l, 0, (float) b.getMaxY(), d));
        g.fill(s);
    }

    static Color ink(boolean enabled) { return enabled ? INK_SOFT : GREY_INK; }

    /** Red wax seal with a ragged rim, centred on (cx, cy). */
    static void seal(Graphics2D g, double cx, double cy, double r, boolean dis) {
        seal(g, cx, cy, r, dis, new Color(0xb52a1c), new Color(0x5e0f08));
    }

    /** Wax seal in any wax colour: a = lit centre, b = dark rim. */
    static void seal(Graphics2D g, double cx, double cy, double r, boolean dis, Color wa, Color wb) {
        double k = r / 7.2;
        Color a = dis ? new Color(0x9a9487) : wa, b = dis ? new Color(0x6d6a62) : wb;
        Path2D seal = new Path2D.Double();
        for (int i = 0; i <= 24; i++) {
            double t = i * Math.PI / 12, rr = (7.2 + (i % 3 == 0 ? 0.9 : 0) + Math.sin(i * 1.7) * 0.5) * k;
            double px = cx + Math.cos(t) * rr, py = cy + Math.sin(t) * rr;
            if (i == 0) seal.moveTo(px, py); else seal.lineTo(px, py);
        }
        seal.closePath();
        g.setColor(new Color(0, 0, 0, 60));
        g.fill(AffineTransform.getTranslateInstance(1.5, 2).createTransformedShape(seal));
        g.setPaint(new RadialGradientPaint(new Point2D.Double(cx - 2 * k, cy - 2 * k), (float) (10 * k), new float[]{0f, 1f}, new Color[]{a, b}));
        g.fill(seal);
        g.setColor(new Color(0, 0, 0, 80));
        g.setStroke(new BasicStroke(0.8f));
        g.draw(new Ellipse2D.Double(cx - 4.5 * k, cy - 4.5 * k, 9 * k, 9 * k));
        g.setColor(new Color(255, 255, 255, dis ? 40 : 90));
        g.fill(new Ellipse2D.Double(cx - 4 * k, cy - 5 * k, 3.5 * k, 2.2 * k));
    }

    /** Small solid triangle pointing in a BasicArrowButton direction (SwingConstants.NORTH/SOUTH/EAST/WEST). */
    static void arrow(Graphics2D g, int dir, double cx, double cy, double s, Color c) {
        Path2D p = new Path2D.Double();
        switch (dir) {
            case javax.swing.SwingConstants.NORTH -> { p.moveTo(cx, cy - s); p.lineTo(cx + s, cy + s * .7); p.lineTo(cx - s, cy + s * .7); }
            case javax.swing.SwingConstants.SOUTH -> { p.moveTo(cx, cy + s); p.lineTo(cx + s, cy - s * .7); p.lineTo(cx - s, cy - s * .7); }
            case javax.swing.SwingConstants.EAST -> { p.moveTo(cx + s, cy); p.lineTo(cx - s * .7, cy + s); p.lineTo(cx - s * .7, cy - s); }
            default -> { p.moveTo(cx - s, cy); p.lineTo(cx + s * .7, cy + s); p.lineTo(cx + s * .7, cy - s); }
        }
        p.closePath();
        g.setColor(c);
        g.fill(p);
    }
}
