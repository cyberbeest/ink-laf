package inklaf;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.plaf.metal.MetalToggleButtonUI;

/** A button is a box scribbled on the page; hover traces it again in red, pressing hatches it in. */
public class InkButtonUI extends BasicButtonUI {
    private static final InkButtonUI INSTANCE = new InkButtonUI();

    public static ComponentUI createUI(JComponent c) {
        return c.getClass() == JButton.class ? INSTANCE : MetalButtonUI.createUI(c);
    }

    public static ComponentUI createToggleUI(JComponent c) {
        return c.getClass() == JToggleButton.class ? INSTANCE : MetalToggleButtonUI.createUI(c);
    }

    @Override protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        LookAndFeel.installProperty(b, "opaque", Boolean.FALSE);
        LookAndFeel.installProperty(b, "rolloverEnabled", Boolean.TRUE);
        if (b.getBorder() == null || b.getBorder() instanceof UIResource) b.setBorder(new BorderUIResource.EmptyBorderUIResource(0, 0, 0, 0));
    }

    /** Tool bar buttons (or client property "Ink.compact") get a plain single-stroke box and tighter sizing. */
    private static boolean compact(JComponent c) {
        return c.getParent() instanceof JToolBar || Boolean.TRUE.equals(c.getClientProperty("Ink.compact"));
    }

    @Override public Dimension getPreferredSize(JComponent c) {
        Dimension d = super.getPreferredSize(c);
        if (compact(c)) return new Dimension(d.width + 18, d.height + 10);
        return new Dimension(Math.max(96, d.width + 30), Math.max(40, d.height + 16));
    }

    @Override public void paint(Graphics g0, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel m = b.getModel();
        Graphics2D g = Pen.aa(g0);
        int w = c.getWidth(), h = c.getHeight();
        long seed = w * 31L + h + Pen.seedFor(c);
        boolean on = b.isEnabled(), down = (m.isArmed() && m.isPressed()) || m.isSelected();
        Color ink = on ? Pen.INK : Pen.GREY;
        if (down) Pen.hatch(g, new RoundRectangle2D.Double(6, 6, w - 12, h - 12, 6, 6), 4.5, 0.9, 1.1, seed, Pen.alpha(ink, 0.5));
        if (compact(c)) Pen.rect(g, 3, 3, w - 6, h - 6, 1.6, seed, ink);
        else {
            Pen.rect(g, 4, 4, w - 8, h - 8, 2.1, seed, ink);
            Pen.rect(g, 5.5, 5, w - 11, h - 10, 0.9, seed + 50, Pen.alpha(ink, 0.45));
        }
        if (on && m.isRollover() && !down) Pen.rect(g, 4.5, 4.5, w - 9, h - 9, 1.2, seed + 77, Pen.alpha(Pen.RUBRIC, 0.85));
        super.paint(g, c);
        g.dispose();
    }

    @Override protected void paintText(Graphics g, JComponent c, Rectangle r, String text) {
        AbstractButton b = (AbstractButton) c;
        FontMetrics fm = g.getFontMetrics();
        g.setColor(b.isEnabled() ? b.getForeground() : Pen.GREY);
        int o = getTextShiftOffset();
        g.drawString(text, r.x + o, r.y + fm.getAscent() + o);
    }

    @Override protected void paintFocus(Graphics g0, AbstractButton b, Rectangle view, Rectangle text, Rectangle icon) {
        Graphics2D g = Pen.aa(g0);                                   // a red squiggle under the label
        Pen.wave(g, text.x, text.x + text.width, text.y + text.height + 2, 1.3, 13, 1.4, text.x * 5L + text.width, Pen.RUBRIC);
        g.dispose();
    }
}
