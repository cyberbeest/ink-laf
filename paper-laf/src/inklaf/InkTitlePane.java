package inklaf;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

/** The window's title strip: the title written on the sheet, window buttons as pen glyphs, a wobbly rule beneath. */
final class InkTitlePane extends JComponent {
    private static final int HEIGHT = 46, BUTTON = 32;

    private enum Kind { MIN, MAX, CLOSE }

    private final JRootPane root;
    private final WindowButton min = new WindowButton(Kind.MIN), max = new WindowButton(Kind.MAX), close = new WindowButton(Kind.CLOSE);
    private Window window;
    private PropertyChangeListener titleListener;
    private WindowStateListener stateListener;

    InkTitlePane(JRootPane root) {
        this.root = root;
        setOpaque(false);
        setLayout(null);
        Font f = UIManager.getFont("Label.font");
        if (f != null) setFont(f.deriveFont(f.getSize2D() * 1.2f));
        add(min); add(max); add(close);
        min.addActionListener(e -> { if (window instanceof Frame f2) f2.setExtendedState(f2.getExtendedState() | Frame.ICONIFIED); });
        max.addActionListener(e -> toggleMaximize());
        close.addActionListener(e -> { if (window != null) window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING)); });
        MouseInputAdapter mover = new MouseInputAdapter() {
            private Point press, origin;
            @Override public void mousePressed(MouseEvent e) {
                press = e.getLocationOnScreen();
                origin = window == null ? null : window.getLocation();
                shapeSuspended(true);      // moving a shaped window is costly, see InkRootPaneUI
            }
            @Override public void mouseReleased(MouseEvent e) {
                press = null;
                shapeSuspended(false);
            }
            @Override public void mouseDragged(MouseEvent e) {
                if (press == null || origin == null || maximized()) return;
                Point p = e.getLocationOnScreen();
                window.setLocation(origin.x + p.x - press.x, origin.y + p.y - press.y);
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) toggleMaximize();
            }
        };
        addMouseListener(mover);
        addMouseMotionListener(mover);
    }

    private void shapeSuspended(boolean on) {
        if (root.getUI() instanceof InkRootPaneUI ui) ui.setShapeSuspended(on);
    }

    private boolean isFrame() { return root.getWindowDecorationStyle() == JRootPane.FRAME && window instanceof Frame; }

    boolean maximized() { return window instanceof Frame f && (f.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0; }

    private boolean resizable() { return window instanceof Frame f ? f.isResizable() : window instanceof Dialog d && d.isResizable(); }

    private void toggleMaximize() {
        if (!isFrame() || !resizable()) return;
        Frame f = (Frame) window;
        f.setExtendedState(maximized() ? Frame.NORMAL : Frame.MAXIMIZED_BOTH);
    }

    private String title() {
        return window instanceof Frame f ? f.getTitle() : window instanceof Dialog d ? d.getTitle() : null;
    }

    @Override public void addNotify() {
        super.addNotify();
        window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            titleListener = e -> repaint();
            stateListener = e -> repaint();
            window.addPropertyChangeListener("title", titleListener);
            window.addWindowStateListener(stateListener);
        }
    }

    @Override public void removeNotify() {
        if (window != null) {
            window.removePropertyChangeListener("title", titleListener);
            window.removeWindowStateListener(stateListener);
        }
        super.removeNotify();
    }

    @Override public Dimension getPreferredSize() { return new Dimension(200, HEIGHT); }

    @Override public void doLayout() {
        int x = getWidth() - 14, y = (getHeight() - BUTTON) / 2 - 1;
        close.setBounds(x -= BUTTON, y, BUTTON, BUTTON);
        boolean frame = isFrame();
        max.setVisible(frame && resizable());
        min.setVisible(frame);
        if (max.isVisible()) max.setBounds(x -= BUTTON + 2, y, BUTTON, BUTTON);
        if (min.isVisible()) min.setBounds(x - BUTTON - 2, y, BUTTON, BUTTON);
    }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = Pen.aa(g0);
        int w = getWidth(), h = getHeight();
        String t = title();
        if (t != null) {
            g.setFont(getFont());
            FontMetrics fm = g.getFontMetrics();
            int avail = Math.max(0, (min.isVisible() ? min.getX() : close.getX()) - 28);
            while (t.length() > 1 && fm.stringWidth(t) > avail) t = t.substring(0, t.length() - 2) + "…";
            g.setColor(Pen.INK);
            g.drawString(t, 18, (h + fm.getAscent() - fm.getDescent()) / 2 - 2);
        }
        Pen.line(g, 8, h - 6, w - 8, h - 6, 1.7, w * 3L, Pen.INK);
        Pen.line(g, 14, h - 3, w - 30, h - 3, 0.9, w * 3L + 1, Pen.alpha(Pen.INK, 0.5));
        g.dispose();
    }

    /** Minimise / maximise / close, drawn as quick pen marks; hover turns them red. */
    private final class WindowButton extends JButton {
        private final Kind kind;

        WindowButton(Kind kind) {
            this.kind = kind;
            setFocusable(false);
            setBorder(null);
            setContentAreaFilled(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setToolTipText(switch (kind) { case MIN -> "Minimise"; case MAX -> "Maximise"; case CLOSE -> "Close"; });
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Pen.aa(g0);
            boolean hot = getModel().isRollover(), down = getModel().isArmed() && getModel().isPressed();
            Color c = hot ? Pen.RUBRIC : Pen.INK;
            double o = down ? 1 : 0, cx = getWidth() / 2.0 + o, cy = getHeight() / 2.0 + o;
            long seed = kind.ordinal() * 101L + 17;
            switch (kind) {
                case MIN -> Pen.line(g, cx - 8, cy + 6, cx + 8, cy + 6, 2.2, seed, c);
                case MAX -> {
                    if (maximized()) {
                        Pen.rect(g, cx - 4, cy - 8, 12, 11, 1.8, seed, c);
                        Pen.rect(g, cx - 9, cy - 3, 12, 11, 1.8, seed + 1, c);
                    } else Pen.rect(g, cx - 8, cy - 8, 16, 16, 1.9, seed, c);
                }
                default -> {
                    Pen.line(g, cx - 8, cy - 8, cx + 8, cy + 8, 2.3, seed, c);
                    Pen.line(g, cx + 8, cy - 8, cx - 8, cy + 8, 2.3, seed + 1, c);
                }
            }
            g.dispose();
        }
    }
}
