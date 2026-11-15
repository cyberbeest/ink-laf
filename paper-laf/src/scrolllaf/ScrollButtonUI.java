package scrolllaf;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.plaf.metal.MetalToggleButtonUI;

/** Paints a JButton as a parchment scroll. The label is drawn into the cached image. */
public class ScrollButtonUI extends BasicButtonUI {
    private static final ScrollButtonUI INSTANCE = new ScrollButtonUI();
    private static final int MIN_H = 76, MIN_W = 170, SIDE = 140;   // SIDE: rolls + frame + padding, both sides

    /** Only plain JButtons get the scroll; arrow buttons of combos, spinners and scrollbars are subclasses and stay Metal. */
    public static ComponentUI createUI(JComponent c) {
        return c.getClass() == JButton.class ? INSTANCE : MetalButtonUI.createUI(c);
    }

    /** Plain JToggleButtons share the scroll (selected = unrolled/pressed); checkboxes and radios are subclasses. */
    public static ComponentUI createToggleUI(JComponent c) {
        return c.getClass() == JToggleButton.class ? INSTANCE : MetalToggleButtonUI.createUI(c);
    }

    @Override protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        // UI-owned values (not app-set), so another L&F can take them over again on a switch
        LookAndFeel.installProperty(b, "opaque", Boolean.FALSE);
        LookAndFeel.installProperty(b, "rolloverEnabled", Boolean.TRUE);
        if (b.getBorder() == null || b.getBorder() instanceof UIResource)
            b.setBorder(new BorderUIResource.EmptyBorderUIResource(0, 0, 0, 0));
    }

    /** Buttons in tool bars (or marked with client property "Scroll.compact") get a small parchment tile instead of a scroll. */
    private static boolean compact(JComponent c) {
        return c.getParent() instanceof JToolBar || Boolean.TRUE.equals(c.getClientProperty("Scroll.compact"));
    }

    @Override public Dimension getPreferredSize(JComponent c) {
        if (compact(c)) {
            Dimension d = super.getPreferredSize(c);
            return new Dimension(d.width + 14, d.height + 10);
        }
        AbstractButton b = (AbstractButton) c;
        String t = b.getText() == null ? "" : b.getText();
        int h = MIN_H;
        Font f = new Font("Serif", Font.BOLD | Font.ITALIC, Math.round(h * 0.26f));
        int tw = c.getFontMetrics(f).stringWidth(t) + Math.round(h * 0.1f);
        return new Dimension(Math.max(MIN_W, tw + SIDE), h);
    }

    @Override public Dimension getMinimumSize(JComponent c) { return getPreferredSize(c); }
    @Override public Dimension getMaximumSize(JComponent c) { return getPreferredSize(c); }

    @Override public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel m = b.getModel();
        ScrollPainter.State st = !b.isEnabled() ? ScrollPainter.State.DISABLED
                : (m.isArmed() && m.isPressed()) || m.isSelected() ? ScrollPainter.State.PRESSED
                : m.isRollover() ? ScrollPainter.State.HOVER : ScrollPainter.State.NORMAL;

        if (compact(c)) {
            Graphics2D t = ScrollArt.aa(g);
            Shape tile = new java.awt.geom.RoundRectangle2D.Double(.5, .5, c.getWidth() - 1, c.getHeight() - 1, 8, 8);
            ScrollArt.paper(t, tile, b.isEnabled(), m.isRollover(), st == ScrollPainter.State.PRESSED);
            t.setColor(ScrollArt.ink(b.isEnabled()));
            t.setStroke(new BasicStroke(1.2f));
            t.draw(tile);
            t.dispose();
            super.paint(g, c);
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        double scale = g2.getDeviceConfiguration().getDefaultTransform().getScaleX();
        int w = c.getWidth(), h = c.getHeight();
        var img = ScrollPainter.image(w, h, b.getText(), st, scale);
        AffineTransform keep = g2.getTransform();
        g2.scale(1 / scale, 1 / scale);          // draw device pixels 1:1
        g2.drawImage(img, 0, 0, null);
        g2.setTransform(keep);

        if (b.isFocusPainted() && b.hasFocus()) {   // dotted ink ring just inside the frame
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0x8e1b12));
            g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{1f, 3f}, 0f));
            g2.draw(new RoundRectangle2D.Double(37, 17, w - 74, h - 34, 6, 6));
        }
        g2.dispose();
    }
}
