package scrolllaf;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.ButtonModel;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicArrowButton;

/** Small parchment tile with an ink arrow; used by scrollbars, spinners and combo boxes. */
public class ScrollArrowButton extends BasicArrowButton implements UIResource {
    public ScrollArrowButton(int direction) {
        super(direction);
        setRolloverEnabled(true);
        setFocusable(false);
        setBorder(null);
        setOpaque(false);
    }

    @Override public Dimension getPreferredSize() { return new Dimension(20, 20); }
    @Override public Dimension getMinimumSize() { return new Dimension(14, 14); }

    @Override public void paint(Graphics g0) {
        Graphics2D g = ScrollArt.aa(g0);
        ButtonModel m = getModel();
        boolean dis = !isEnabled(), down = m.isPressed() && m.isArmed();
        int w = getWidth(), h = getHeight();
        Rectangle2D r = new Rectangle2D.Double(0.5, 0.5, w - 1, h - 1);
        ScrollArt.paper(g, r, !dis, m.isRollover(), down);
        g.setColor(ScrollArt.ink(!dis));
        g.setStroke(new BasicStroke(1f));
        g.draw(r);
        double s = Math.min(w, h) * 0.22, o = down ? 1 : 0;
        ScrollArt.arrow(g, getDirection(), w / 2.0 + o, h / 2.0 + o, s, dis ? ScrollArt.GREY_INK : ScrollArt.INK);
        g.dispose();
    }
}
