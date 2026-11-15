package inklaf;

import java.awt.*;
import javax.swing.ButtonModel;
import javax.swing.SwingConstants;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicArrowButton;

/** Arrow drawn as a two-stroke chevron. */
public class InkArrowButton extends BasicArrowButton implements UIResource {
    public InkArrowButton(int direction) {
        super(direction);
        setRolloverEnabled(true);
        setFocusable(false);
        setBorder(null);
        setOpaque(false);
    }

    @Override public Dimension getPreferredSize() { return new Dimension(22, 22); }
    @Override public Dimension getMinimumSize() { return new Dimension(14, 14); }

    @Override public void paint(Graphics g0) {
        Graphics2D g = Pen.aa(g0);
        ButtonModel m = getModel();
        boolean on = isEnabled(), down = m.isPressed() && m.isArmed();
        double cx = getWidth() / 2.0 + (down ? 1 : 0), cy = getHeight() / 2.0 + (down ? 1 : 0), s = Math.min(getWidth(), getHeight()) * 0.24;
        Color c = !on ? Pen.GREY : m.isRollover() ? Pen.RUBRIC : Pen.INK;
        double[] xs, ys;
        switch (getDirection()) {
            case SwingConstants.NORTH -> { xs = new double[]{cx - s, cx, cx + s}; ys = new double[]{cy + s * .6, cy - s * .6, cy + s * .6}; }
            case SwingConstants.EAST -> { xs = new double[]{cx - s * .6, cx + s * .6, cx - s * .6}; ys = new double[]{cy - s, cy, cy + s}; }
            case SwingConstants.WEST -> { xs = new double[]{cx + s * .6, cx - s * .6, cx + s * .6}; ys = new double[]{cy - s, cy, cy + s}; }
            default -> { xs = new double[]{cx - s, cx, cx + s}; ys = new double[]{cy - s * .6, cy + s * .6, cy - s * .6}; }
        }
        Pen.polyline(g, xs, ys, 2.0, getWidth() * 5L + getDirection(), c);
        g.dispose();
    }
}
