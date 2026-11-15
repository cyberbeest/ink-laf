package scrolllaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * "Paper scroll" look and feel. Buttons are fully custom; every other component is Metal
 * restyled through a flat parchment theme until it gets its own UI delegate.
 */
public class ScrollLookAndFeel extends MetalLookAndFeel {
    static final class Theme extends DefaultMetalTheme {
        private final ColorUIResource p1 = new ColorUIResource(0x5a3a1a), p2 = new ColorUIResource(0xa57e45),
                p3 = new ColorUIResource(0xd9bf8a), s1 = new ColorUIResource(0x6b4423),
                s2 = new ColorUIResource(0xc9a468), s3 = new ColorUIResource(0xf1dfae);
        private final FontUIResource serif = new FontUIResource("Serif", Font.PLAIN, 16),
                serifBold = new FontUIResource("Serif", Font.BOLD, 16);
        @Override public String getName() { return "Parchment"; }
        @Override protected ColorUIResource getPrimary1() { return p1; }
        @Override protected ColorUIResource getPrimary2() { return p2; }
        @Override protected ColorUIResource getPrimary3() { return p3; }
        @Override protected ColorUIResource getSecondary1() { return s1; }
        @Override protected ColorUIResource getSecondary2() { return s2; }
        @Override protected ColorUIResource getSecondary3() { return s3; }
        @Override public ColorUIResource getBlack() { return new ColorUIResource(0x3b2412); }
        @Override public FontUIResource getControlTextFont() { return serif; }
        @Override public FontUIResource getSystemTextFont() { return serif; }
        @Override public FontUIResource getUserTextFont() { return serif; }
        @Override public FontUIResource getMenuTextFont() { return serif; }
        @Override public FontUIResource getWindowTitleFont() { return serifBold; }
        @Override public FontUIResource getSubTextFont() { return new FontUIResource("Serif", Font.PLAIN, 13); }
    }

    public ScrollLookAndFeel() {
        MetalLookAndFeel.setCurrentTheme(new Theme());
    }

    /** Table header cell: ink rule underneath and a thin one to the right. */
    static final class HeaderBorder extends javax.swing.border.AbstractBorder implements javax.swing.plaf.UIResource {
        @Override public Insets getBorderInsets(Component c, Insets i) { i.set(4, 8, 5, 8); return i; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.setColor(new Color(0x6b4423));
            g.fillRect(x, y + h - 2, w, 2);
            g.setColor(new Color(0x6b, 0x44, 0x23, 120));
            g.fillRect(x + w - 1, y + 3, 1, h - 7);
        }
    }

    @Override public String getName() { return "Scroll"; }
    @Override public String getID() { return "Scroll"; }
    @Override public String getDescription() { return "Fantasy paper-scroll look and feel"; }
    @Override public boolean isNativeLookAndFeel() { return false; }
    @Override public boolean isSupportedLookAndFeel() { return true; }

    @Override protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        table.put("ButtonUI", ScrollButtonUI.class.getName());
        table.put("ToggleButtonUI", ToggleUI.class.getName());
        for (String n : new String[]{"TabbedPane", "ScrollBar", "Slider", "ProgressBar", "ComboBox", "Spinner", "SplitPane"})
            table.put(n + "UI", "scrolllaf.Scroll" + n + "UI");
    }

    /** Separate entry point so JToggleButton gets its own factory method. */
    public static class ToggleUI {
        public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent c) { return ScrollButtonUI.createToggleUI(c); }
    }

    @Override protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        Color ink = new Color(0x3b2412);
        table.put("Button.foreground", ink);
        table.put("Button.disabledText", new Color(0x8f897c));

        Color paper = new Color(0xf8ecc8), selBg = new Color(0xc9a468), rubric = new Color(0x8e1b12);
        for (String t : new String[]{"TextField", "PasswordField", "FormattedTextField", "TextArea", "TextPane", "EditorPane"}) {
            table.put(t + ".background", paper);
            table.put(t + ".foreground", ink);
            table.put(t + ".caretForeground", rubric);
            table.put(t + ".selectionBackground", selBg);
            table.put(t + ".selectionForeground", ink);
            table.put(t + ".inactiveBackground", new Color(0xe3dccb));
        }
        for (String t : new String[]{"TextField", "PasswordField", "FormattedTextField"})
            table.put(t + ".border", new ScrollBorder(2));
        table.put("ScrollPane.border", new ScrollBorder(0));
        table.put("TitledBorder.border", new ScrollBorder(-2));
        table.put("TitledBorder.titleColor", rubric);
        table.put("TitledBorder.font", new javax.swing.plaf.FontUIResource("Serif", Font.BOLD | Font.ITALIC, 16));
        table.put("CheckBox.icon", ScrollIcons.checkBox());
        table.put("RadioButton.icon", ScrollIcons.radioButton());
        table.put("CheckBox.textIconGap", 8);
        table.put("RadioButton.textIconGap", 8);

        Color ink2 = new Color(0x6b4423), tan = new Color(0xc9a468);
        javax.swing.plaf.FontUIResource italic = new javax.swing.plaf.FontUIResource("Serif", Font.ITALIC, 16),
                boldItalic = new javax.swing.plaf.FontUIResource("Serif", Font.BOLD | Font.ITALIC, 16);
        Insets i0 = new Insets(0, 0, 0, 0);
        // tabs
        table.put("TabbedPane.font", italic);
        table.put("TabbedPane.foreground", ink);
        table.put("TabbedPane.tabInsets", new Insets(7, 18, 7, 18));
        table.put("TabbedPane.selectedTabPadInsets", new Insets(2, 2, 2, 2));
        table.put("TabbedPane.tabAreaInsets", new Insets(4, 4, 0, 4));
        table.put("TabbedPane.contentBorderInsets", new Insets(6, 6, 6, 6));
        table.put("TabbedPane.tabsOverlapBorder", Boolean.FALSE);
        // scrollbar / slider / progress / combo / spinner / split pane
        table.put("ScrollBar.width", 20);
        table.put("ScrollBar.minimumThumbSize", new Dimension(34, 34));
        table.put("Slider.tickColor", ink2);
        table.put("Slider.focus", rubric);
        table.put("ProgressBar.horizontalSize", new Dimension(200, 24));
        table.put("ProgressBar.verticalSize", new Dimension(24, 200));
        table.put("ProgressBar.border", new javax.swing.plaf.BorderUIResource.EmptyBorderUIResource(2, 2, 2, 2));
        table.put("ProgressBar.font", boldItalic);
        table.put("ProgressBar.selectionBackground", ink);
        table.put("ProgressBar.selectionForeground", paper);
        table.put("ComboBox.background", paper);
        table.put("ComboBox.foreground", ink);
        table.put("ComboBox.selectionBackground", tan);
        table.put("ComboBox.selectionForeground", ink);
        table.put("ComboBox.border", new ScrollBorder(-2));
        table.put("Spinner.border", new ScrollBorder(-2));
        table.put("Spinner.background", paper);
        table.put("SplitPane.dividerSize", 12);
        table.put("SplitPane.border", new javax.swing.plaf.BorderUIResource.EmptyBorderUIResource(0, 0, 0, 0));
        // lists, tables, trees
        for (String t : new String[]{"List", "Table", "Tree"}) {
            table.put(t + ".background", paper);
            table.put(t + ".foreground", ink);
            table.put(t + ".selectionBackground", tan);
            table.put(t + ".selectionForeground", ink);
        }
        table.put("Tree.textBackground", paper);
        table.put("Tree.textForeground", ink);
        table.put("Tree.selectionBorderColor", rubric);
        table.put("Tree.hash", ink2);
        table.put("Tree.line", ink2);
        table.put("Tree.expandedIcon", ScrollIcons.treeToggle(true));
        table.put("Tree.collapsedIcon", ScrollIcons.treeToggle(false));
        table.put("Tree.openIcon", ScrollIcons.folder(true));
        table.put("Tree.closedIcon", ScrollIcons.folder(false));
        table.put("Tree.leafIcon", ScrollIcons.leaf());
        table.put("Table.gridColor", tan);
        table.put("Table.scrollPaneBorder", new ScrollBorder(0));
        table.put("Table.focusCellHighlightBorder", new javax.swing.plaf.BorderUIResource.LineBorderUIResource(rubric, 1));
        table.put("List.focusCellHighlightBorder", new javax.swing.plaf.BorderUIResource.LineBorderUIResource(rubric, 1));
        table.put("TableHeader.background", tan);
        table.put("TableHeader.foreground", ink);
        table.put("TableHeader.font", boldItalic);
        table.put("TableHeader.cellBorder", new HeaderBorder());
        // message-dialog icons
        table.put("OptionPane.informationIcon", ScrollIcons.message("i", new Color(0x3f6a8a), new Color(0x14283a)));
        table.put("OptionPane.questionIcon", ScrollIcons.message("?", new Color(0x4a7a4a), new Color(0x152a15)));
        table.put("OptionPane.warningIcon", ScrollIcons.message("!", new Color(0xc88a1e), new Color(0x5a3a08)));
        table.put("OptionPane.errorIcon", ScrollIcons.message("\u00d7", new Color(0xb52a1c), new Color(0x5e0f08)));
        table.put("OptionPane.messageForeground", ink);
        table.put("OptionPane.background", new Color(0xf1dfae));
        // menus, tool tips, tool bars, separators
        Color menuBg = new Color(0xe3cf9b);
        for (String t : new String[]{"MenuBar", "Menu", "MenuItem", "CheckBoxMenuItem", "RadioButtonMenuItem", "PopupMenu"}) {
            table.put(t + ".background", t.equals("MenuBar") ? menuBg : paper);
            table.put(t + ".foreground", ink);
            table.put(t + ".selectionBackground", tan);
            table.put(t + ".selectionForeground", ink);
            table.put(t + ".acceleratorForeground", ink2);
            table.put(t + ".acceleratorSelectionForeground", ink);
        }
        table.put("MenuBar.border", new javax.swing.plaf.BorderUIResource.MatteBorderUIResource(0, 0, 2, 0, ink2));
        table.put("PopupMenu.border", new ScrollBorder(-3));
        table.put("Separator.foreground", ink2);
        table.put("Separator.background", paper);
        table.put("ToolTip.background", paper);
        table.put("ToolTip.foreground", ink);
        table.put("ToolTip.font", italic);
        table.put("ToolTip.border", new ScrollBorder(-3));
        table.put("ToolBar.background", menuBg);
        table.put("ToolBar.border", new javax.swing.plaf.BorderUIResource.MatteBorderUIResource(0, 0, 2, 0, ink2));
    }
}
