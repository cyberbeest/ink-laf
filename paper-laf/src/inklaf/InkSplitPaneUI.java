package inklaf;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/** Divider: one wavy pen line. */
public class InkSplitPaneUI extends BasicSplitPaneUI {
    public static ComponentUI createUI(JComponent c) { return new InkSplitPaneUI(); }

    @Override public void update(Graphics g, JComponent c) { InkUIs.bg(g, c); paint(g, c); }

    @Override public BasicSplitPaneDivider createDefaultDivider() {
        return new BasicSplitPaneDivider(this) {
            @Override public void paint(Graphics g0) {
                Graphics2D g = Pen.aa(g0);
                InkPaper.paint(g, this);
                int w = getWidth(), h = getHeight();
                if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
                    int n = Math.max(4, h / 2);
                    double[] xs = new double[n], ys = new double[n];
                    for (int i = 0; i < n; i++) { ys[i] = (double) i * h / (n - 1); xs[i] = w / 2.0 + Math.sin(ys[i] / 9) * 1.6; }
                    Pen.polyline(g, xs, ys, 1.6, h, Pen.INK);
                } else Pen.wave(g, 0, w, h / 2.0, 1.6, 18, 1.6, w, Pen.INK);
                g.dispose();
            }
        };
    }
}
