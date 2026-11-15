package scrolllaf;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/** Design-phase prototype: a "paper scroll" button painted with plain Java2D. */
public final class ScrollPainter {
    private ScrollPainter() {}

    public enum State { NORMAL, HOVER, PRESSED, DISABLED }

    static final Color INK = new Color(0x3b2412);
    static final Color INK_SOFT = new Color(0x6b4423);
    static final Color RUBRIC = new Color(0x8e1b12);

    public static void paintScroll(Graphics2D g, int w, int h, String text, State st) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        boolean pressed = st == State.PRESSED, dis = st == State.DISABLED, hot = st == State.HOVER;
        int roll = 30;                       // width of each rolled end
        int top = pressed ? 8 : 6;           // paper sags a little when pressed
        int bot = h - top;
        Random rnd = new Random(42);

        // soft drop shadow under everything
        g.setColor(new Color(0, 0, 0, 70));
        g.fill(new RoundRectangle2D.Double(roll / 2.0 + 3, top + 6, w - roll - 3, bot - top, 6, 6));

        // ---- paper body with ragged top/bottom edge ----
        Path2D body = new Path2D.Double();
        double x0 = roll / 2.0, x1 = w - roll / 2.0;
        body.moveTo(x0, top);
        for (double x = x0; x <= x1; x += 6)
            body.lineTo(x, top + Math.sin(x * 0.13) * 1.6 + rnd.nextDouble() * 1.4 - 0.7);
        body.lineTo(x1, bot);
        for (double x = x1; x >= x0; x -= 6)
            body.lineTo(x, bot + Math.sin(x * 0.11 + 2) * 1.6 + rnd.nextDouble() * 1.4 - 0.7);
        body.closePath();

        Color pLight = dis ? new Color(0xd9d3c6) : hot ? new Color(0xfbeec3) : pressed ? new Color(0xd9bf8a) : new Color(0xf1dfae);
        Color pDark  = dis ? new Color(0xa9a392) : hot ? new Color(0xd9b573) : pressed ? new Color(0xa57e45) : new Color(0xc9a468);
        g.setPaint(new LinearGradientPaint(0, top, 0, bot, new float[]{0f, .5f, 1f},
                new Color[]{pDark, pLight, pDark}));
        g.fill(body);

        // fibre/stain texture, clipped to the body
        Shape oldClip = g.getClip();
        g.clip(body);
        for (int i = 0; i < w * h / 14; i++) {
            float a = rnd.nextFloat() * 0.07f;
            g.setColor(new Color(0.35f, 0.22f, 0.08f, a));
            g.fill(new Ellipse2D.Double(rnd.nextDouble() * w, rnd.nextDouble() * h, 1 + rnd.nextDouble() * 2, 1 + rnd.nextDouble()));
        }
        for (int i = 0; i < 5; i++) {   // foxing / water stains
            double cx = x0 + rnd.nextDouble() * (x1 - x0), cy = top + rnd.nextDouble() * (bot - top), r = 8 + rnd.nextDouble() * 14;
            g.setPaint(new RadialGradientPaint(new Point2D.Double(cx, cy), (float) r, new float[]{0f, 1f},
                    new Color[]{new Color(0.5f, 0.3f, 0.1f, 0.0f), new Color(0.5f, 0.3f, 0.1f, 0.0f)}));
            g.setPaint(new RadialGradientPaint(new Point2D.Double(cx, cy), (float) r, new float[]{0.6f, 1f},
                    new Color[]{new Color(0.5f, 0.3f, 0.1f, 0.0f), new Color(0.45f, 0.25f, 0.08f, 0.12f)}));
            g.fill(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));
        }
        // burnt/aged edge: stacked strokes of the outline, clipped inside
        for (int i = 7; i >= 1; i--) {
            g.setStroke(new BasicStroke(i * 2.2f));
            g.setColor(new Color(0.3f, 0.15f, 0.04f, dis ? 0.025f : 0.055f));
            g.draw(body);
        }
        if (hot) {   // warm candle glow from the middle
            g.setPaint(new RadialGradientPaint(new Point2D.Double(w / 2.0, h / 2.0), w / 2f, new float[]{0f, 1f},
                    new Color[]{new Color(1f, 0.85f, 0.4f, 0.35f), new Color(1f, 0.85f, 0.4f, 0f)}));
            g.fill(body);
        }
        // paper curling into each roll: dark where it turns under, a faint lit band just before it
        float curl = 44f;
        float gx0 = roll, gx1 = w - roll;   // start where the roll's edge ends
        float[] cf = {0f, .35f, .7f, 1f};
        Color shade = new Color(0.22f, 0.10f, 0.02f, dis ? 0.38f : 0.62f);
        Color clear = new Color(0.22f, 0.10f, 0.02f, 0f);
        Color lit = new Color(1f, 0.95f, 0.75f, dis ? 0.10f : 0.22f);
        g.setPaint(new LinearGradientPaint(gx0, 0, gx0 + curl, 0, cf,
                new Color[]{shade, new Color(0.22f, 0.10f, 0.02f, 0.18f), lit, new Color(1f, 0.95f, 0.75f, 0f)}));
        g.fill(body);
        g.setPaint(new LinearGradientPaint(gx1, 0, gx1 - curl, 0, cf,
                new Color[]{shade, new Color(0.22f, 0.10f, 0.02f, 0.18f), lit, new Color(1f, 0.95f, 0.75f, 0f)}));
        g.fill(body);
        g.setClip(oldClip);
        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(0x5a3a1a, false));
        g.setColor(new Color(0x5a, 0x3a, 0x1a, 120));
        g.draw(body);

        // ---- ink frame with corner flourishes ----
        Color ink = dis ? new Color(0x8a8578) : INK_SOFT;
        double fx0 = x0 + 15, fx1 = x1 - 15, fy0 = top + 7, fy1 = bot - 7;
        g.setColor(ink);
        g.setStroke(new BasicStroke(1.6f));
        g.draw(new Rectangle2D.Double(fx0, fy0, fx1 - fx0, fy1 - fy0));
        g.setStroke(new BasicStroke(0.8f));
        g.draw(new Rectangle2D.Double(fx0 + 3, fy0 + 3, fx1 - fx0 - 6, fy1 - fy0 - 6));
        g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        corner(g, fx0, fy0, 1, 1);
        corner(g, fx1, fy0, -1, 1);
        corner(g, fx0, fy1, 1, -1);
        corner(g, fx1, fy1, -1, -1);
        // diamonds + pen-stroke tails flank the label; tails shrink (or vanish) to leave the text clear
        double cy = h / 2.0;
        double textL = text == null || text.isEmpty() ? w / 2.0 : (w - labelWidth(g, h, text)) / 2.0, textR = w - textL;
        double dl = fx0 + 24, dr = fx1 - 24;
        if (textL - 10 > dl + 4) {
            diamond(g, dl, cy, 3.5);
            diamond(g, dr, cy, 3.5);
            double len = Math.min(34, textL - 12 - (dl + 8));
            if (len >= 14) {
                g.setStroke(new BasicStroke(1.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(new CubicCurve2D.Double(dl + 8, cy, dl + 8 + len / 3, cy - 6, dl + 8 + 2 * len / 3, cy + 6, dl + 8 + len, cy));
                g.draw(new CubicCurve2D.Double(dr - 8, cy, dr - 8 - len / 3, cy - 6, dr - 8 - 2 * len / 3, cy + 6, dr - 8 - len, cy));
            }
        }

        // ---- the two rolled ends ----
        paintRoll(g, 0, h, roll, dis, pressed, true);
        paintRoll(g, w - roll, h, roll, dis, pressed, false);

        if (text == null || text.isEmpty()) return;

        // ---- label: rubricated drop capital + ink text ----
        Font big = new Font("Serif", Font.BOLD | Font.ITALIC, Math.round(h * 0.36f));
        Font small = new Font("Serif", Font.BOLD | Font.ITALIC, Math.round(h * 0.26f));
        FontMetrics fb = g.getFontMetrics(big), fs = g.getFontMetrics(small);
        String first = text.substring(0, 1), rest = text.substring(1);
        double tw = fb.stringWidth(first) + fs.stringWidth(rest);
        double tx = (w - tw) / 2.0 + (pressed ? 1 : 0);
        double ty = h / 2.0 + fs.getAscent() / 2.0 - 3 + (pressed ? 1.5 : 0);
        g.setFont(big);
        g.setColor(new Color(255, 245, 210, dis ? 60 : 150));
        g.drawString(first, (float) tx + 1, (float) ty + 1);      // letterpress highlight
        g.setColor(dis ? new Color(0x9a9487) : RUBRIC);
        g.drawString(first, (float) tx, (float) ty);
        g.setFont(small);
        double rx = tx + fb.stringWidth(first);
        g.setColor(new Color(255, 245, 210, dis ? 60 : 150));
        g.drawString(rest, (float) rx + 1, (float) ty + 1);
        g.setColor(dis ? new Color(0x8f897c) : INK);
        g.drawString(rest, (float) rx, (float) ty);
    }

    /** Width of the label as paintScroll will draw it (drop capital plus rest). */
    static double labelWidth(Graphics2D g, int h, String text) {
        FontMetrics fb = g.getFontMetrics(new Font("Serif", Font.BOLD | Font.ITALIC, Math.round(h * 0.36f)));
        FontMetrics fs = g.getFontMetrics(new Font("Serif", Font.BOLD | Font.ITALIC, Math.round(h * 0.26f)));
        return fb.stringWidth(text.substring(0, 1)) + fs.stringWidth(text.substring(1));
    }

    static void corner(Graphics2D g, double x, double y, int sx, int sy) {
        Path2D p = new Path2D.Double();
        p.moveTo(x - sx * 2, y + sy * 14);
        p.curveTo(x - sx * 2, y + sy * 5, x + sx * 4, y - sy * 2, x + sx * 14, y - sy * 2);  // sweeping arm
        g.draw(p);
        Path2D q = new Path2D.Double();   // inner curl
        q.moveTo(x + sx * 4, y + sy * 12);
        q.curveTo(x + sx * 4, y + sy * 6, x + sx * 6, y + sy * 5, x + sx * 12, y + sy * 5);
        q.curveTo(x + sx * 17, y + sy * 5, x + sx * 17, y + sy * 10, x + sx * 12, y + sy * 10);
        q.curveTo(x + sx * 9, y + sy * 10, x + sx * 9, y + sy * 7.5, x + sx * 11, y + sy * 7.5);
        g.draw(q);
        g.fill(new Ellipse2D.Double(x - sx * 2 - 1.8, y + sy * 14 - 1.8, 3.6, 3.6));
    }

    static void diamond(Graphics2D g, double cx, double cy, double r) {
        Path2D d = new Path2D.Double();
        d.moveTo(cx, cy - r); d.lineTo(cx + r, cy); d.lineTo(cx, cy + r); d.lineTo(cx - r, cy); d.closePath();
        g.fill(d);
    }

    /** A rolled-up end of the scroll, viewed from slightly above: spiral top cap, curved lower rim. */
    static void paintRoll(Graphics2D g, int x, int h, int rw, boolean dis, boolean pressed, boolean left) {
        int cap = 12;                        // apparent height of the cap ellipse
        double topY = cap / 2.0, botY = h - cap / 2.0;   // ellipse centre lines
        Color a = dis ? new Color(0x9c978a) : pressed ? new Color(0x8a6431) : new Color(0xa97f45);
        Color b = dis ? new Color(0xd6d0c2) : pressed ? new Color(0xcaa564) : new Color(0xf0dca6);
        Color edge = new Color(0x4a, 0x2d, 0x12, 170);

        // tube silhouette: straight sides, bottom closed by the lower half of the ellipse
        Path2D tube = new Path2D.Double();
        tube.moveTo(x, topY);
        tube.lineTo(x, botY);
        tube.append(new Arc2D.Double(x, botY - cap / 2.0, rw, cap, 180, 180, Arc2D.OPEN), true);
        tube.lineTo(x + rw, topY);
        tube.closePath();
        g.setPaint(new LinearGradientPaint(x, 0, x + rw, 0, new float[]{0f, .3f, .55f, 1f},
                new Color[]{a.darker(), b, a, a.darker().darker()}));
        g.fill(tube);

        // paper layers: downward-bowed arcs, like horizontal circles seen from above
        Shape old = g.getClip();
        g.clip(tube);
        g.setStroke(new BasicStroke(0.6f));
        for (int i = 1; i < 9; i++) {
            double yy = topY + i * (botY - topY) / 8.0;
            g.setColor(new Color(0.25f, 0.13f, 0.03f, 0.09f));
            g.draw(new Arc2D.Double(x, yy - cap / 2.0, rw, cap, 180, 180, Arc2D.OPEN));
        }
        // ribbon tie, bowed the same way
        double ry = h * 0.5;
        g.setColor(dis ? new Color(0x6d6a62) : new Color(0x6a1a12));
        g.setStroke(new BasicStroke(9f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        g.draw(new Arc2D.Double(x - 1, ry - cap / 2.0, rw + 2, cap, 180, 180, Arc2D.OPEN));
        g.setColor(new Color(0, 0, 0, 90));      // shaded lower edge and cast shadow on the paper
        g.setStroke(new BasicStroke(1.2f));
        g.draw(new Arc2D.Double(x - 1, ry - cap / 2.0 + 4.5, rw + 2, cap, 180, 180, Arc2D.OPEN));
        g.setColor(new Color(255, 255, 255, 70)); // satin highlight on the upper edge
        g.draw(new Arc2D.Double(x - 1, ry - cap / 2.0 - 3.5, rw + 2, cap, 180, 180, Arc2D.OPEN));
        g.setClip(old);

        g.setColor(edge);
        g.setStroke(new BasicStroke(1f));
        g.draw(tube);

        // top cap: full ellipse showing the spiral of rolled paper
        Ellipse2D.Double e = new Ellipse2D.Double(x, topY - cap / 2.0, rw, cap);
        g.setPaint(new GradientPaint(x, 0, b, x + rw, 0, a));
        g.fill(e);
        g.setColor(edge);
        g.draw(e);
        g.setStroke(new BasicStroke(0.7f));
        for (double k = 0.78; k > 0.15; k -= 0.17) {
            double ew = rw * k, eh = cap * k;
            g.draw(new Arc2D.Double(x + (rw - ew) / 2 + (left ? 1 : -1), topY - eh / 2, ew, eh, left ? 60 : 120, 270, Arc2D.OPEN));
        }
    }

    private static final Map<String, BufferedImage> CACHE = new LinkedHashMap<>(64, .75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<String, BufferedImage> e) { return size() > 96; }
    };

    /** The scroll rendered once per (size, label, state, device scale); HiDPI-sharp when drawn at 1/scale. */
    public static synchronized BufferedImage image(int w, int h, String text, State st, double scale) {
        String key = w + "x" + h + "@" + scale + "/" + st + "/" + text;
        BufferedImage img = CACHE.get(key);
        if (img == null) {
            img = new BufferedImage((int) Math.ceil(w * scale), (int) Math.ceil(h * scale), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.scale(scale, scale);
            paintScroll(g, w, h, text, st);
            g.dispose();
            CACHE.put(key, img);
        }
        return img;
    }
}
