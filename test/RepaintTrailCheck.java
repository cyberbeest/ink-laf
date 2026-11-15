import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import javax.swing.*;

/**
 * Moving thumbs must not leave trails. Swing repaints only the union of the old and new thumb rectangles, so the
 * result of that partial repaint has to equal a full repaint. Checked for scrollbars and sliders .
 * Exits with status 1 if any pixel differs noticeably.
 */
public class RepaintTrailCheck {
    private static final int TOLERANCE = 32;   // per channel; rasteriser rounding differences stay far below this

    static BufferedImage paint(JComponent c, Rectangle clip, BufferedImage into) {
        BufferedImage img = into != null ? into : new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        if (clip != null) g.setClip(clip);
        c.paint(g);
        g.dispose();
        return img;
    }

    static int differing(BufferedImage a, BufferedImage b) {
        int n = 0;
        for (int y = 0; y < a.getHeight(); y++)
            for (int x = 0; x < a.getWidth(); x++) {
                int p = a.getRGB(x, y), q = b.getRGB(x, y);
                for (int sh = 0; sh < 32; sh += 8)
                    if (Math.abs(((p >> sh) & 255) - ((q >> sh) & 255)) > TOLERANCE) { n++; break; }
            }
        return n;
    }

    static Rectangle thumb(JComponent c) throws Exception {
        Object ui = c instanceof JScrollBar ? ((JScrollBar) c).getUI() : ((JSlider) c).getUI();
        Method m = ui.getClass().getMethod("thumbRect");
        return new Rectangle((Rectangle) m.invoke(ui));
    }

    public static void main(String[] args) throws Exception {
        int[] failures = {0};
        String[][] lafs = {{"Ink", "inklaf.InkLookAndFeel"}};
        SwingUtilities.invokeAndWait(() -> {
            try {
                for (String[] laf : lafs) {
                    UIManager.setLookAndFeel(laf[1]);
                    for (boolean horizontal : new boolean[]{false, true}) {
                        JScrollBar sb = new JScrollBar(horizontal ? Adjustable.HORIZONTAL : Adjustable.VERTICAL, 0, 20, 0, 100);
                        sb.setSize(horizontal ? new Dimension(300, 22) : new Dimension(22, 300));
                        failures[0] += run(laf[0] + " scrollbar " + (horizontal ? "horizontal" : "vertical"), sb, new int[]{7, 15, 31, 44, 60, 80});
                        JSlider sl = new JSlider(horizontal ? SwingConstants.HORIZONTAL : SwingConstants.VERTICAL, 0, 100, 10);
                        sl.setSize(horizontal ? new Dimension(320, 60) : new Dimension(60, 320));
                        failures[0] += run(laf[0] + " slider " + (horizontal ? "horizontal" : "vertical"), sl, new int[]{20, 33, 51, 70, 90});
                    }
                }
            } catch (Exception e) { e.printStackTrace(); failures[0]++; }
        });
        System.out.println(failures[0] == 0 ? "PASS" : "FAIL");
        System.exit(failures[0] == 0 ? 0 : 1);
    }

    private static int run(String name, JComponent c, int[] values) throws Exception {
        c.doLayout();
        BufferedImage img = paint(c, null, null);
        int bad = 0;
        for (int v : values) {
            Rectangle old = thumb(c);
            if (c instanceof JScrollBar) ((JScrollBar) c).setValue(v); else ((JSlider) c).setValue(v);
            paint(c, old.union(thumb(c)), img);                     // what Swing would repaint
            bad += differing(img, paint(c, null, null));            // what a full repaint gives
        }
        System.out.printf("%-34s %s%n", name, bad == 0 ? "ok" : bad + " stray pixels");
        return bad == 0 ? 0 : 1;
    }
}
