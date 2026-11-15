package inklaf;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicRootPaneUI;

/**
 * Window decorations for undecorated frames and dialogs: the window is the sheet. Adds a title pane, resizing from the edges
 * and, where the system allows, a torn-paper window shape.
 */
public class InkRootPaneUI extends BasicRootPaneUI {
    private static final int EDGE = 9, CORNER = 18;   // torn edge eats ~4px of the outside, so the band is wider than the margin looks

    private JRootPane root;
    private InkTitlePane titlePane;
    private LayoutManager savedLayout;
    private MouseInputAdapter handler;
    private ComponentListener resizeListener;
    private WindowStateListener stateListener;
    private Window window;
    private boolean shapeSuspended;

    public static ComponentUI createUI(JComponent c) { return new InkRootPaneUI(); }

    @Override public void installUI(JComponent c) {
        super.installUI(c);
        root = (JRootPane) c;
        if (root.getWindowDecorationStyle() != JRootPane.NONE) installClientDecorations();
    }

    @Override public void uninstallUI(JComponent c) {
        uninstallClientDecorations();
        root = null;
        super.uninstallUI(c);
    }

    @Override public void propertyChange(PropertyChangeEvent e) {
        super.propertyChange(e);
        if ("windowDecorationStyle".equals(e.getPropertyName()) && root != null) {
            uninstallClientDecorations();
            if (root.getWindowDecorationStyle() != JRootPane.NONE) installClientDecorations();
        }
    }

    @Override public void update(Graphics g, JComponent c) {
        if (c.isOpaque()) InkPaper.paint(g, c);
        paint(g, c);
    }

    private void installClientDecorations() {
        if (root.getBorder() == null || root.getBorder() instanceof UIResource) root.setBorder(new WindowBorder());
        titlePane = new InkTitlePane(root);
        root.getLayeredPane().add(titlePane, JLayeredPane.FRAME_CONTENT_LAYER);
        savedLayout = root.getLayout();
        root.setLayout(new RootLayout());
        window = SwingUtilities.getWindowAncestor(root);
        handler = new ResizeHandler();
        root.addMouseListener(handler);
        root.addMouseMotionListener(handler);
        resizeListener = new ComponentAdapter() { @Override public void componentResized(ComponentEvent e) { updateShape(); } };
        root.addComponentListener(resizeListener);
        if (window != null) {
            stateListener = e -> { root.revalidate(); root.repaint(); updateShape(); };
            window.addWindowStateListener(stateListener);
        }
        root.revalidate();
        root.repaint();
    }

    private void uninstallClientDecorations() {
        if (root == null || titlePane == null) return;
        root.getLayeredPane().remove(titlePane);
        titlePane = null;
        if (root.getBorder() instanceof UIResource) root.setBorder(null);
        if (savedLayout != null) root.setLayout(savedLayout);
        root.removeMouseListener(handler);
        root.removeMouseMotionListener(handler);
        root.removeComponentListener(resizeListener);
        if (window != null && stateListener != null) window.removeWindowStateListener(stateListener);
        try { if (window != null && !GraphicsEnvironment.isHeadless()) window.setShape(null); } catch (RuntimeException ignored) { }
        window = null;
        root.revalidate();
        root.repaint();
    }

    private static boolean maximized(Window w) { return w instanceof Frame f && (f.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0; }

    private static long edgeSeed(int w, int h) { return w * 13L + h; }

    /** Off by -Dink.noShape=true or UIManager "Ink.tornWindow" = false: plain rectangular windows. */
    private static boolean tornWanted() {
        return !Boolean.getBoolean("ink.noShape") && !Boolean.FALSE.equals(UIManager.get("Ink.tornWindow"));
    }

    /**
     * A shaped window is clipped by the X server on every move, which roughly doubles the cost per step. So the shape is
     * dropped while the window is being dragged or resized and put back when the mouse is released.
     */
    void setShapeSuspended(boolean suspended) {
        shapeSuspended = suspended;
        updateShape();
    }

    /** Torn paper outline as the window shape (silently skipped where the system can't shape windows). */
    private void updateShape() {
        Window w = window != null ? window : SwingUtilities.getWindowAncestor(root);
        if (w == null || GraphicsEnvironment.isHeadless()) return;
        try {
            boolean ok = tornWanted() && !shapeSuspended && GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                    .isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT);
            if (ok && !maximized(w) && w.getWidth() > 40 && w.getHeight() > 40)
                w.setShape(InkPaper.torn(2, 2, w.getWidth() - 4, w.getHeight() - 4, edgeSeed(w.getWidth(), w.getHeight()), 2.0));
            else w.setShape(null);
        } catch (RuntimeException ignored) { }
    }

    /** Pale fibre rim and a thin ink line along the torn edge; nothing when maximised. */
    private static final class WindowBorder extends AbstractBorder implements UIResource {
        @Override public Insets getBorderInsets(Component c, Insets i) {
            int n = maximized(SwingUtilities.getWindowAncestor(c)) ? 0 : EDGE;
            i.set(n, n, n, n);
            return i;
        }
        @Override public void paintBorder(Component c, Graphics g0, int x, int y, int w, int h) {
            if (maximized(SwingUtilities.getWindowAncestor(c))) return;
            Graphics2D g = Pen.aa(g0);
            Shape torn = InkPaper.torn(x + 2, y + 2, w - 4, h - 4, edgeSeed(w, h), 2.0);
            Shape old = g.getClip();
            g.clip(torn);
            g.setColor(new Color(255, 250, 232, 150));
            g.setStroke(new BasicStroke(3.4f));
            g.draw(torn);
            g.setClip(old);
            g.setColor(new Color(0x6b, 0x44, 0x23, 130));
            g.setStroke(new BasicStroke(0.9f));
            g.draw(torn);
            g.dispose();
        }
    }

    /** Title pane on top, then the menu bar, then the content, all inside the border. */
    private final class RootLayout implements LayoutManager2 {
        @Override public void layoutContainer(Container parent) {
            JRootPane r = (JRootPane) parent;
            Insets i = r.getInsets();
            int w = r.getWidth() - i.left - i.right, h = r.getHeight() - i.top - i.bottom, y = 0;
            if (r.getLayeredPane() != null) r.getLayeredPane().setBounds(i.left, i.top, w, h);
            if (r.getGlassPane() != null) r.getGlassPane().setBounds(i.left, i.top, w, h);
            if (titlePane != null) {
                int th = titlePane.getPreferredSize().height;
                titlePane.setBounds(0, 0, w, th);
                y += th;
            }
            if (r.getJMenuBar() != null) {
                int mh = r.getJMenuBar().getPreferredSize().height;
                r.getJMenuBar().setBounds(0, y, w, mh);
                y += mh;
            }
            if (r.getContentPane() != null) r.getContentPane().setBounds(0, y, w, Math.max(0, h - y));
        }

        private Dimension size(Container parent, boolean preferred) {
            JRootPane r = (JRootPane) parent;
            Insets i = r.getInsets();
            Dimension c = r.getContentPane() == null ? new Dimension() : preferred ? r.getContentPane().getPreferredSize() : r.getContentPane().getMinimumSize();
            Dimension m = r.getJMenuBar() == null ? new Dimension() : preferred ? r.getJMenuBar().getPreferredSize() : r.getJMenuBar().getMinimumSize();
            Dimension t = titlePane == null ? new Dimension() : titlePane.getPreferredSize();
            return new Dimension(Math.max(c.width, Math.max(m.width, t.width)) + i.left + i.right, c.height + m.height + t.height + i.top + i.bottom);
        }

        @Override public Dimension preferredLayoutSize(Container parent) { return size(parent, true); }
        @Override public Dimension minimumLayoutSize(Container parent) { return size(parent, false); }
        @Override public Dimension maximumLayoutSize(Container target) { return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE); }
        @Override public void addLayoutComponent(String name, Component comp) { }
        @Override public void addLayoutComponent(Component comp, Object constraints) { }
        @Override public void removeLayoutComponent(Component comp) { }
        @Override public float getLayoutAlignmentX(Container target) { return 0; }
        @Override public float getLayoutAlignmentY(Container target) { return 0; }
        @Override public void invalidateLayout(Container target) { }
    }

    /** Resize cursors and dragging along the window border. */
    private final class ResizeHandler extends MouseInputAdapter {
        private int dir;
        private Point start;
        private Rectangle startBounds;

        private boolean resizable() {
            if (maximized(window)) return false;
            return window instanceof Frame f ? f.isResizable() : window instanceof Dialog d && d.isResizable();
        }

        private int edge(MouseEvent e) {
            if (window == null || !resizable()) return Cursor.DEFAULT_CURSOR;
            int x = e.getX(), y = e.getY(), w = root.getWidth(), h = root.getHeight();
            boolean l = x < EDGE, r = x >= w - EDGE, t = y < EDGE, b = y >= h - EDGE;
            boolean nl = x < CORNER, nr = x >= w - CORNER, nt = y < CORNER, nb = y >= h - CORNER;
            if ((t && nl) || (l && nt)) return Cursor.NW_RESIZE_CURSOR;
            if ((t && nr) || (r && nt)) return Cursor.NE_RESIZE_CURSOR;
            if ((b && nl) || (l && nb)) return Cursor.SW_RESIZE_CURSOR;
            if ((b && nr) || (r && nb)) return Cursor.SE_RESIZE_CURSOR;
            if (t) return Cursor.N_RESIZE_CURSOR;
            if (b) return Cursor.S_RESIZE_CURSOR;
            if (l) return Cursor.W_RESIZE_CURSOR;
            if (r) return Cursor.E_RESIZE_CURSOR;
            return Cursor.DEFAULT_CURSOR;
        }

        @Override public void mouseMoved(MouseEvent e) { root.setCursor(Cursor.getPredefinedCursor(edge(e))); }
        @Override public void mouseExited(MouseEvent e) { if (start == null) root.setCursor(Cursor.getDefaultCursor()); }

        @Override public void mousePressed(MouseEvent e) {
            dir = edge(e);
            if (dir != Cursor.DEFAULT_CURSOR) { start = e.getLocationOnScreen(); startBounds = window.getBounds(); setShapeSuspended(true); }
        }

        @Override public void mouseReleased(MouseEvent e) {
            boolean was = start != null;
            dir = Cursor.DEFAULT_CURSOR;
            start = null;
            if (was) setShapeSuspended(false);
        }

        @Override public void mouseDragged(MouseEvent e) {
            if (dir == Cursor.DEFAULT_CURSOR || start == null) return;
            Point p = e.getLocationOnScreen();
            int dx = p.x - start.x, dy = p.y - start.y;
            // the layout's own minimum (all tabs' contents) is huge; use it only if the app set one on purpose
            Dimension min = window.isMinimumSizeSet() ? window.getMinimumSize() : new Dimension(360, 260);
            int minW = min.width, minH = min.height;
            boolean west = dir == Cursor.W_RESIZE_CURSOR || dir == Cursor.NW_RESIZE_CURSOR || dir == Cursor.SW_RESIZE_CURSOR;
            boolean east = dir == Cursor.E_RESIZE_CURSOR || dir == Cursor.NE_RESIZE_CURSOR || dir == Cursor.SE_RESIZE_CURSOR;
            boolean north = dir == Cursor.N_RESIZE_CURSOR || dir == Cursor.NW_RESIZE_CURSOR || dir == Cursor.NE_RESIZE_CURSOR;
            boolean south = dir == Cursor.S_RESIZE_CURSOR || dir == Cursor.SW_RESIZE_CURSOR || dir == Cursor.SE_RESIZE_CURSOR;
            Rectangle b = new Rectangle(startBounds);
            if (east) b.width = Math.max(minW, startBounds.width + dx);
            if (south) b.height = Math.max(minH, startBounds.height + dy);
            if (west) { int nw = Math.max(minW, startBounds.width - dx); b.x = startBounds.x + startBounds.width - nw; b.width = nw; }
            if (north) { int nh = Math.max(minH, startBounds.height - dy); b.y = startBounds.y + startBounds.height - nh; b.height = nh; }
            window.setBounds(b);
            window.validate();
        }
    }
}
