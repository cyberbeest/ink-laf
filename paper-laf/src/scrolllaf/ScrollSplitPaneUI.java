package scrolllaf;

import java.awt.*;
import java.awt.geom.Path2D;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/** Divider: a tan ribbon between two ink rules, with a row of small diamonds as the grip. */
public class ScrollSplitPaneUI extends BasicSplitPaneUI {
    public static ComponentUI createUI(JComponent c) { return new ScrollSplitPaneUI(); }

    @Override public BasicSplitPaneDivider createDefaultDivider() {
        return new BasicSplitPaneDivider(this) {
            @Override public void paint(Graphics g0) {
                Graphics2D g = ScrollArt.aa(g0);
                int w = getWidth(), h = getHeight();
                boolean horizSplit = orientation == JSplitPane.HORIZONTAL_SPLIT;   // vertical bar between left/right
                g.setPaint(horizSplit ? new GradientPaint(0, 0, new Color(0xb89a60), w, 0, new Color(0xe9d7a4))
                                      : new GradientPaint(0, 0, new Color(0xb89a60), 0, h, new Color(0xe9d7a4)));
                g.fillRect(0, 0, w, h);
                g.setColor(ScrollArt.INK_SOFT);
                g.setStroke(new BasicStroke(1f));
                if (horizSplit) { g.drawLine(0, 0, 0, h); g.drawLine(w - 1, 0, w - 1, h); }
                else { g.drawLine(0, 0, w, 0); g.drawLine(0, h - 1, w, h - 1); }
                g.setColor(ScrollArt.INK_SOFT);
                for (int i = -2; i <= 2; i++) {
                    double cx = horizSplit ? w / 2.0 : w / 2.0 + i * 12, cy = horizSplit ? h / 2.0 + i * 12 : h / 2.0, r = 2.6;
                    Path2D d = new Path2D.Double();
                    d.moveTo(cx, cy - r); d.lineTo(cx + r, cy); d.lineTo(cx, cy + r); d.lineTo(cx - r, cy); d.closePath();
                    g.fill(d);
                }
                g.dispose();
            }
        };
    }
}
