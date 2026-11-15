package inklaf;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.*;

/** Delegates that only change how the background is painted: paper instead of a flat colour. */
public final class InkUIs {
    private InkUIs() {}

    static void bg(Graphics g, JComponent c) {
        if (c.isOpaque()) InkPaper.paint(g, c);
    }

    public static final class Panel extends BasicPanelUI {
        public static ComponentUI createUI(JComponent c) { return new Panel(); }
        @Override public void update(Graphics g, JComponent c) { bg(g, c); paint(g, c); }
    }

    public static final class Viewport extends BasicViewportUI {
        public static ComponentUI createUI(JComponent c) { return new Viewport(); }
        @Override protected void installDefaults(JComponent c) {
            super.installDefaults(c);
            ((JViewport) c).setScrollMode(JViewport.SIMPLE_SCROLL_MODE);   // blitting would drag the paper texture along
        }
        @Override public void update(Graphics g, JComponent c) { bg(g, c); paint(g, c); }
    }

    public static final class ScrollPane extends BasicScrollPaneUI {
        public static ComponentUI createUI(JComponent c) { return new ScrollPane(); }
        @Override public void update(Graphics g, JComponent c) { bg(g, c); paint(g, c); }
    }

    public static final class List extends BasicListUI {
        public static ComponentUI createUI(JComponent c) { return new List(); }
        @Override public void update(Graphics g, JComponent c) { bg(g, c); paint(g, c); }
    }

    public static final class Table extends BasicTableUI {
        public static ComponentUI createUI(JComponent c) { return new Table(); }
        @Override public void update(Graphics g, JComponent c) { bg(g, c); paint(g, c); }
    }

    public static final class TableHeader extends BasicTableHeaderUI {
        public static ComponentUI createUI(JComponent c) { return new TableHeader(); }
        @Override public void update(Graphics g, JComponent c) { bg(g, c); paint(g, c); }
    }

    public static final class Tree extends BasicTreeUI {
        public static ComponentUI createUI(JComponent c) { return new Tree(); }
        @Override public void update(Graphics g, JComponent c) { bg(g, c); paint(g, c); }
    }

    public static final class ToolBar extends BasicToolBarUI {
        public static ComponentUI createUI(JComponent c) { return new ToolBar(); }
        @Override public void update(Graphics g, JComponent c) { bg(g, c); paint(g, c); }
    }

    public static final class TextField extends BasicTextFieldUI {
        public static ComponentUI createUI(JComponent c) { return new TextField(); }
        @Override protected void paintBackground(Graphics g) { bg(g, getComponent()); }
    }

    public static final class PasswordField extends BasicPasswordFieldUI {
        public static ComponentUI createUI(JComponent c) { return new PasswordField(); }
        @Override protected void paintBackground(Graphics g) { bg(g, getComponent()); }
    }

    public static final class FormattedTextField extends BasicFormattedTextFieldUI {
        public static ComponentUI createUI(JComponent c) { return new FormattedTextField(); }
        @Override protected void paintBackground(Graphics g) { bg(g, getComponent()); }
    }

    public static final class TextArea extends BasicTextAreaUI {
        public static ComponentUI createUI(JComponent c) { return new TextArea(); }
        @Override protected void paintBackground(Graphics g) { bg(g, getComponent()); }
    }

    public static final class TextPane extends BasicTextPaneUI {
        public static ComponentUI createUI(JComponent c) { return new TextPane(); }
        @Override protected void paintBackground(Graphics g) { bg(g, getComponent()); }
    }

    public static final class EditorPane extends BasicEditorPaneUI {
        public static ComponentUI createUI(JComponent c) { return new EditorPane(); }
        @Override protected void paintBackground(Graphics g) { bg(g, getComponent()); }
    }

    public static final class CheckBox extends javax.swing.plaf.metal.MetalCheckBoxUI {
        public static ComponentUI createUI(JComponent c) { return new CheckBox(); }
        @Override public void installDefaults(AbstractButton b) { super.installDefaults(b); LookAndFeel.installProperty(b, "opaque", Boolean.FALSE); }
    }

    public static final class RadioButton extends javax.swing.plaf.metal.MetalRadioButtonUI {
        public static ComponentUI createUI(JComponent c) { return new RadioButton(); }
        @Override public void installDefaults(AbstractButton b) { super.installDefaults(b); LookAndFeel.installProperty(b, "opaque", Boolean.FALSE); }
    }

    public static final class MenuBar extends BasicMenuBarUI {
        public static ComponentUI createUI(JComponent c) { return new MenuBar(); }
        @Override public void update(Graphics g, JComponent c) { bg(g, c); paint(g, c); }
    }

    public static final class PopupMenu extends BasicPopupMenuUI {
        public static ComponentUI createUI(JComponent c) { return new PopupMenu(); }
        @Override public void update(Graphics g, JComponent c) { bg(g, c); paint(g, c); }
    }

    public static final class ToolTip extends BasicToolTipUI {
        public static ComponentUI createUI(JComponent c) { return new ToolTip(); }
        @Override public void update(Graphics g, JComponent c) {
            bg(g, c);
            InkPaper.slip((Graphics2D) g, c, 4, 4, c.getWidth() - 11, c.getHeight() - 11, c.getWidth() * 29L + c.getHeight());
            paint(g, c);
        }
    }

    public static final class OptionPane extends BasicOptionPaneUI {
        public static ComponentUI createUI(JComponent c) { return new OptionPane(); }
        @Override public void update(Graphics g, JComponent c) { bg(g, c); paint(g, c); }
    }

    /** Menu entries: paper behind, a highlighter swipe when armed. */
    static void menuBg(Graphics g0, JMenuItem mi) {
        InkPaper.paint(g0, mi);
        boolean hot = mi.getModel().isArmed() || (mi instanceof JMenu m && m.isSelected());
        if (hot && mi.isEnabled()) {
            Graphics2D g = Pen.aa(g0);
            g.setColor(InkLookAndFeel.MARKER);
            g.fillRoundRect(2, 2, mi.getWidth() - 4, mi.getHeight() - 4, 8, 8);
            g.dispose();
        }
    }

    public static final class MenuItem extends BasicMenuItemUI {
        public static ComponentUI createUI(JComponent c) { return new MenuItem(); }
        @Override protected void paintBackground(Graphics g, JMenuItem mi, java.awt.Color bg) { menuBg(g, mi); }
    }

    public static final class Menu extends BasicMenuUI {
        public static ComponentUI createUI(JComponent c) { return new Menu(); }
        @Override protected void paintBackground(Graphics g, JMenuItem mi, java.awt.Color bg) { menuBg(g, mi); }
    }

    public static final class CheckBoxMenuItem extends BasicCheckBoxMenuItemUI {
        public static ComponentUI createUI(JComponent c) { return new CheckBoxMenuItem(); }
        @Override protected void paintBackground(Graphics g, JMenuItem mi, java.awt.Color bg) { menuBg(g, mi); }
    }

    public static final class RadioButtonMenuItem extends BasicRadioButtonMenuItemUI {
        public static ComponentUI createUI(JComponent c) { return new RadioButtonMenuItem(); }
        @Override protected void paintBackground(Graphics g, JMenuItem mi, java.awt.Color bg) { menuBg(g, mi); }
    }
}
