package inklaf;

import java.awt.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

/** Borders drawn as pen strokes or paper slips. */
final class InkBorders {
    private InkBorders() {}

    /** Form-style fill-in line under a text field, with small end ticks. */
    static final class Underline extends AbstractBorder implements UIResource {
        @Override public Insets getBorderInsets(Component c, Insets i) { i.set(4, 6, 9, 6); return i; }
        @Override public void paintBorder(Component c, Graphics g0, int x, int y, int w, int h) {
            Graphics2D g = Pen.aa(g0);
            long seed = w * 13L + h;
            Color col = !c.isEnabled() ? Pen.GREY : c.hasFocus() ? Pen.RUBRIC : Pen.INK;
            Pen.line(g, x + 2, y + h - 5, x + w - 2, y + h - 5, 1.8, seed, col);
            Pen.line(g, x + 2.5, y + h - 5, x + 3, y + h - 12, 1.3, seed + 1, col);
            Pen.line(g, x + w - 2.5, y + h - 5, x + w - 3, y + h - 12, 1.3, seed + 2, col);
            g.dispose();
        }
    }

    /** A sketched box: two loose passes of the pen. */
    static final class Box extends AbstractBorder implements UIResource {
        private final int pad;
        Box(int pad) { this.pad = pad; }
        @Override public Insets getBorderInsets(Component c, Insets i) { int n = 6 + pad; i.set(n, n + 2, n, n + 2); return i; }
        @Override public void paintBorder(Component c, Graphics g0, int x, int y, int w, int h) {
            Graphics2D g = Pen.aa(g0);
            Color col = c.isEnabled() ? Pen.INK : Pen.GREY;
            long seed = w * 17L + h * 3;
            Pen.rect(g, x + 4, y + 4, w - 8, h - 8, 1.8, seed, col);
            Pen.rect(g, x + 5, y + 5, w - 10, h - 10, 0.9, seed + 99, Pen.alpha(col, 0.45));
            g.dispose();
        }
    }

    /** A torn slip of paper lying on the page; it paints its own shadow and body, content goes inside. */
    static final class Slip extends AbstractBorder implements UIResource {
        private final int pad;
        private final boolean paints;
        Slip(int pad) { this(pad, true); }
        /** paints=false: only reserve the room; the component's UI draws the slip itself before its own content. */
        Slip(int pad, boolean paints) { this.pad = pad; this.paints = paints; }
        @Override public Insets getBorderInsets(Component c, Insets i) { int n = 8 + pad; i.set(n, n, n + 3, n + 3); return i; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            if (paints) InkPaper.slip((Graphics2D) g, c, x + 4, y + 4, w - 11, h - 11, w * 29L + h);
        }
    }

    /** Table header cell: a wobbly double underline. */
    static final class Header extends AbstractBorder implements UIResource {
        @Override public Insets getBorderInsets(Component c, Insets i) { i.set(4, 8, 9, 8); return i; }
        @Override public void paintBorder(Component c, Graphics g0, int x, int y, int w, int h) {
            Graphics2D g = Pen.aa(g0);
            long seed = x * 7L + w;
            Pen.line(g, x, y + h - 6, x + w, y + h - 6, 1.6, seed, Pen.INK);
            Pen.line(g, x + 3, y + h - 3, x + w - 6, y + h - 3, 0.9, seed + 1, Pen.alpha(Pen.INK, 0.6));
            g.dispose();
        }
    }

    /** Single wobbly line under a bar. */
    static final class Rule extends AbstractBorder implements UIResource {
        @Override public Insets getBorderInsets(Component c, Insets i) { i.set(2, 6, 7, 6); return i; }
        @Override public void paintBorder(Component c, Graphics g0, int x, int y, int w, int h) {
            Graphics2D g = Pen.aa(g0);
            Pen.line(g, x, y + h - 3, x + w, y + h - 3, 1.7, w * 3L + h, Pen.INK);
            g.dispose();
        }
    }
}
