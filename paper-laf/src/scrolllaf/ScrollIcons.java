package scrolllaf;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.plaf.UIResource;

/** Checkbox (ink box, quill tick) and radio button (wax seal) icons. */
public final class ScrollIcons {
    private ScrollIcons() {}

    private static final int SIZE = 26;
    private static final Color INK = new Color(0x6b4423), RUBRIC = new Color(0x8e1b12);

    private abstract static class Base implements Icon, UIResource {
        @Override public int getIconWidth() { return SIZE; }
        @Override public int getIconHeight() { return SIZE; }

        @Override public void paintIcon(Component c, Graphics g0, int x, int y) {
            AbstractButton b = (AbstractButton) c;
            ButtonModel m = b.getModel();
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.translate(x, y);
            boolean dis = !b.isEnabled(), down = m.isArmed() && m.isPressed(), hot = m.isRollover();
            Color light = dis ? new Color(0xd9d3c6) : down ? new Color(0xd9bf8a) : hot ? new Color(0xfbeec3) : new Color(0xf6e8bd);
            Color dark = dis ? new Color(0xb5afa0) : down ? new Color(0xa57e45) : new Color(0xd6b676);
            paint(g, m.isSelected(), dis, light, dark, dis ? new Color(0x8a8578) : INK);
            g.dispose();
        }

        abstract void paint(Graphics2D g, boolean on, boolean dis, Color light, Color dark, Color ink);
    }

    public static Icon checkBox() {
        return new Base() {
            @Override void paint(Graphics2D g, boolean on, boolean dis, Color light, Color dark, Color ink) {
                Rectangle2D box = new Rectangle2D.Double(2, 3, 21, 21);
                g.setColor(new Color(0, 0, 0, 55));                       // soft shadow
                g.fill(new Rectangle2D.Double(3.5, 4.5, 21, 21));
                g.setPaint(new GradientPaint(0, 3, dark, 0, 24, light));
                g.fill(box);
                g.setColor(ink);
                g.setStroke(new BasicStroke(1.5f));
                g.draw(box);
                g.setStroke(new BasicStroke(0.7f));
                g.draw(new Rectangle2D.Double(4.5, 5.5, 16, 16));
                if (on) {                                                  // quill tick: thin upstroke, heavy downstroke
                    Color tick = dis ? new Color(0x8f897c) : RUBRIC;
                    g.setColor(tick);
                    g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.draw(new QuadCurve2D.Double(6, 14, 8, 15, 10.5, 19.5));
                    g.setStroke(new BasicStroke(3.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.draw(new CubicCurve2D.Double(10.5, 19.5, 13, 13, 17, 7, 24, 0.5));
                }
            }
        };
    }

    public static Icon radioButton() {
        return new Base() {
            @Override void paint(Graphics2D g, boolean on, boolean dis, Color light, Color dark, Color ink) {
                g.setColor(new Color(0, 0, 0, 55));
                g.fill(new Ellipse2D.Double(3.5, 4.5, 21, 21));
                g.setPaint(new GradientPaint(0, 3, dark, 0, 24, light));
                g.fill(new Ellipse2D.Double(2, 3, 21, 21));
                g.setColor(ink);
                g.setStroke(new BasicStroke(1.5f));
                g.draw(new Ellipse2D.Double(2, 3, 21, 21));
                g.setStroke(new BasicStroke(0.7f));
                g.draw(new Ellipse2D.Double(4.5, 5.5, 16, 16));
                if (on) {                                                  // wax seal
                    Color a = dis ? new Color(0x9a9487) : new Color(0xb52a1c), b = dis ? new Color(0x6d6a62) : new Color(0x5e0f08);
                    Path2D seal = new Path2D.Double();                     // slightly irregular rim
                    for (int i = 0; i <= 24; i++) {
                        double t = i * Math.PI / 12, r = 7.2 + (i % 3 == 0 ? 0.9 : 0) + Math.sin(i * 1.7) * 0.5;
                        double px = 12.5 + Math.cos(t) * r, py = 13.5 + Math.sin(t) * r;
                        if (i == 0) seal.moveTo(px, py); else seal.lineTo(px, py);
                    }
                    seal.closePath();
                    g.setPaint(new RadialGradientPaint(new Point2D.Double(10.5, 11.5), 10f, new float[]{0f, 1f}, new Color[]{a, b}));
                    g.fill(seal);
                    g.setColor(new Color(0, 0, 0, 80));
                    g.setStroke(new BasicStroke(0.8f));
                    g.draw(new Ellipse2D.Double(8, 9, 9, 9));              // pressed ring
                    g.setColor(new Color(255, 255, 255, dis ? 40 : 90));
                    g.fill(new Ellipse2D.Double(8.5, 8.5, 3.5, 2.2));      // highlight
                }
            }
        };
    }

    private interface Draw { void draw(Graphics2D g); }

    private static Icon icon(int w, int h, Draw d) {
        return new Icon() {
            @Override public void paintIcon(Component c, Graphics g0, int x, int y) {
                Graphics2D g = ScrollArt.aa(g0);
                g.translate(x, y);
                d.draw(g);
                g.dispose();
            }
            @Override public int getIconWidth() { return w; }
            @Override public int getIconHeight() { return h; }
        };
    }

    private static Icon res(Icon i) {
        return new ResIcon(i);
    }

    private static final class ResIcon implements Icon, UIResource {
        private final Icon in;
        ResIcon(Icon in) { this.in = in; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) { in.paintIcon(c, g, x, y); }
        @Override public int getIconWidth() { return in.getIconWidth(); }
        @Override public int getIconHeight() { return in.getIconHeight(); }
    }

    /** Tree expander: small ink box with + or -. */
    public static Icon treeToggle(boolean expanded) {
        return res(icon(13, 13, g -> {
            g.setColor(new Color(0xf6e8bd));
            g.fill(new Rectangle2D.Double(1.5, 1.5, 10, 10));
            g.setColor(INK);
            g.setStroke(new BasicStroke(1.1f));
            g.draw(new Rectangle2D.Double(1.5, 1.5, 10, 10));
            g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new Line2D.Double(4, 6.5, 9, 6.5));
            if (!expanded) g.draw(new Line2D.Double(6.5, 4, 6.5, 9));
        }));
    }

    /** Tree folder: tabbed ink outline on parchment. */
    public static Icon folder(boolean open) {
        return res(icon(18, 16, g -> {
            Path2D p = new Path2D.Double();
            p.moveTo(1.5, 3); p.lineTo(6.5, 3); p.lineTo(8, 5); p.lineTo(16.5, 5); p.lineTo(16.5, 14); p.lineTo(1.5, 14); p.closePath();
            g.setPaint(new GradientPaint(0, 3, new Color(0xf6e8bd), 0, 14, new Color(0xd6b676)));
            g.fill(p);
            g.setColor(INK);
            g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(p);
            if (open) { g.setColor(RUBRIC); g.draw(new Line2D.Double(3.5, 8.5, 14.5, 8.5)); }
        }));
    }

    /** Tree leaf: a small sheet with ink lines. */
    public static Icon leaf() {
        return res(icon(14, 16, g -> {
            Path2D p = new Path2D.Double();
            p.moveTo(2, 1.5); p.lineTo(9, 1.5); p.lineTo(12.5, 5); p.lineTo(12.5, 14.5); p.lineTo(2, 14.5); p.closePath();
            g.setPaint(new GradientPaint(0, 1, new Color(0xfbeec3), 0, 15, new Color(0xe0c488)));
            g.fill(p);
            g.setColor(INK);
            g.setStroke(new BasicStroke(1.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(p);
            g.setStroke(new BasicStroke(0.9f));
            g.setColor(ScrollArt.INK_SOFT);
            for (int i = 0; i < 3; i++) g.draw(new Line2D.Double(4.5, 7 + i * 2.6, 10.5, 7 + i * 2.6));
        }));
    }

    /** Message-dialog icon: wax seal in the given colours with an embossed glyph. */
    public static Icon message(String glyph, Color lit, Color dark) {
        return res(icon(46, 46, g -> {
            ScrollArt.seal(g, 23, 22, 20, false, lit, dark);
            g.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 28));
            FontMetrics fm = g.getFontMetrics();
            float x = 23 - fm.stringWidth(glyph) / 2f, y = 22 + (fm.getAscent() - fm.getDescent()) / 2f;
            g.setColor(new Color(0, 0, 0, 120));
            g.drawString(glyph, x - 1, y - 1);
            g.setColor(new Color(255, 235, 190, 220));
            g.drawString(glyph, x + 0.7f, y + 0.7f);
            g.setColor(new Color(lit.getRed() * 3 / 4, lit.getGreen() * 3 / 4, lit.getBlue() * 3 / 4));
            g.drawString(glyph, x, y);
        }));
    }
}
