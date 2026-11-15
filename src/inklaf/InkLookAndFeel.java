package inklaf;

import java.awt.*;
import java.util.Arrays;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Spike: the window is one sheet of parchment and controls are pen scribbles on it.
 * Containers paint paper in window coordinates; some parts (scroll panes, tab pages, popups) are torn slips on top.
 */
public class InkLookAndFeel extends MetalLookAndFeel {
    static final Color INK = Pen.INK, RUBRIC = Pen.RUBRIC;
    static final Color CLEAR = new Color(0, 0, 0, 0), MARKER = new Color(255, 200, 40, 120);

    private static Font hand(float size, int style) {
        boolean z003 = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()).contains("Z003");
        return z003 ? new Font("Z003", style & ~Font.ITALIC, Math.round(size * 1.25f)) : new Font("Serif", style | Font.ITALIC, Math.round(size));
    }

    static final class Theme extends DefaultMetalTheme {
        private final ColorUIResource p1 = new ColorUIResource(0x2b1b10), p2 = new ColorUIResource(0x8a6a3a), p3 = new ColorUIResource(0xc9a566),
                s1 = new ColorUIResource(0x6b4a2a), s2 = new ColorUIResource(0xc9a566), s3 = new ColorUIResource(InkPaper.BASE);
        private final FontUIResource body = new FontUIResource(hand(16, Font.PLAIN)), bold = new FontUIResource(hand(16, Font.BOLD));
        @Override public String getName() { return "Scroll & Ink"; }
        @Override protected ColorUIResource getPrimary1() { return p1; }
        @Override protected ColorUIResource getPrimary2() { return p2; }
        @Override protected ColorUIResource getPrimary3() { return p3; }
        @Override protected ColorUIResource getSecondary1() { return s1; }
        @Override protected ColorUIResource getSecondary2() { return s2; }
        @Override protected ColorUIResource getSecondary3() { return s3; }
        @Override public ColorUIResource getBlack() { return new ColorUIResource(INK); }
        @Override public FontUIResource getControlTextFont() { return body; }
        @Override public FontUIResource getSystemTextFont() { return body; }
        @Override public FontUIResource getUserTextFont() { return body; }
        @Override public FontUIResource getMenuTextFont() { return body; }
        @Override public FontUIResource getWindowTitleFont() { return bold; }
        @Override public FontUIResource getSubTextFont() { return body; }
    }

    public InkLookAndFeel() { MetalLookAndFeel.setCurrentTheme(new Theme()); }

    @Override public String getName() { return "Scroll & Ink"; }
    @Override public String getID() { return "ScrollAndInk"; }
    @Override public String getDescription() { return "Pen scribbles on a sheet of parchment"; }
    @Override public boolean isNativeLookAndFeel() { return false; }
    @Override public boolean isSupportedLookAndFeel() { return true; }
    @Override public boolean getSupportsWindowDecorations() { return true; }

    @Override protected void initClassDefaults(UIDefaults t) {
        super.initClassDefaults(t);
        String p = "inklaf.";
        t.put("ButtonUI", p + "InkButtonUI");
        t.put("ToggleButtonUI", p + "InkLookAndFeel$ToggleUI");
        for (String n : new String[]{"Panel", "Viewport", "ScrollPane", "List", "Table", "TableHeader", "Tree", "ToolBar",
                "TextField", "PasswordField", "FormattedTextField", "TextArea", "TextPane", "EditorPane",
                "CheckBox", "RadioButton", "MenuBar", "PopupMenu", "ToolTip", "OptionPane", "MenuItem", "Menu", "CheckBoxMenuItem", "RadioButtonMenuItem"})
            t.put(n + "UI", p + "InkUIs$" + n);
        for (String n : new String[]{"TabbedPane", "ScrollBar", "Slider", "ProgressBar", "ComboBox", "Spinner", "SplitPane", "RootPane"})
            t.put(n + "UI", p + "Ink" + n + "UI");
    }

    public static class ToggleUI {
        public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent c) { return InkButtonUI.createToggleUI(c); }
    }

    @Override protected void initComponentDefaults(UIDefaults t) {
        super.initComponentDefaults(t);
        Font body = hand(16, Font.PLAIN), bold = hand(16, Font.BOLD);
        for (String n : new String[]{"Button", "ToggleButton", "CheckBox", "RadioButton", "Label", "TextField", "PasswordField", "FormattedTextField",
                "TextArea", "TextPane", "EditorPane", "List", "Table", "Tree", "ComboBox", "Spinner", "TabbedPane", "TitledBorder", "ToolTip", "Menu", "MenuItem",
                "MenuBar", "CheckBoxMenuItem", "RadioButtonMenuItem", "ProgressBar", "OptionPane.messageFont", "OptionPane.buttonFont"})
            t.put(n.contains(".") ? n : n + ".font", body);
        t.put("TableHeader.font", bold);
        t.put("Button.foreground", INK);
        t.put("Button.disabledText", Pen.GREY);
        t.put("Button.margin", new Insets(4, 10, 4, 10));
        t.put("Label.foreground", INK);
        t.put("CheckBox.icon", InkIcons.checkBox());
        t.put("RadioButton.icon", InkIcons.radioButton());
        t.put("CheckBox.textIconGap", 6);
        t.put("RadioButton.textIconGap", 6);
        // text: no box, just a fill-in line
        for (String n : new String[]{"TextField", "PasswordField", "FormattedTextField"}) t.put(n + ".border", new InkBorders.Underline());
        for (String n : new String[]{"TextField", "PasswordField", "FormattedTextField", "TextArea", "TextPane", "EditorPane", "List", "Table", "Tree", "TableHeader"}) {
            t.put(n + ".background", CLEAR);
            t.put(n + ".foreground", INK);
            t.put(n + ".selectionBackground", MARKER);   // highlighter pen
            t.put(n + ".selectionForeground", INK);
            t.put(n + ".caretForeground", RUBRIC);
            t.put(n + ".inactiveBackground", CLEAR);
        }
        t.put("Tree.textBackground", CLEAR);
        t.put("Tree.textForeground", INK);
        t.put("Tree.selectionBorderColor", RUBRIC);
        t.put("Tree.line", new Color(0x2b, 0x1b, 0x10, 110));
        t.put("Tree.hash", new Color(0x2b, 0x1b, 0x10, 110));
        t.put("Table.gridColor", new Color(0x2b, 0x1b, 0x10, 55));
        t.put("Table.focusCellHighlightBorder", new javax.swing.plaf.BorderUIResource.LineBorderUIResource(RUBRIC, 1));
        t.put("List.focusCellHighlightBorder", new javax.swing.plaf.BorderUIResource.LineBorderUIResource(RUBRIC, 1));
        t.put("TableHeader.cellBorder", new InkBorders.Header());
        t.put("Viewport.background", CLEAR);
        t.put("ScrollPane.border", new InkBorders.Slip(2));
        t.put("Table.scrollPaneBorder", new InkBorders.Slip(2));
        t.put("TitledBorder.border", new InkBorders.Box(-2));
        t.put("TitledBorder.titleColor", RUBRIC);
        t.put("ToolTip.background", InkPaper.SLIP);
        t.put("ToolTip.foreground", INK);
        t.put("ToolTip.border", new InkBorders.Slip(-3, false));
        t.put("TabbedPane.foreground", INK);
        t.put("TabbedPane.tabInsets", new Insets(7, 18, 7, 18));
        t.put("TabbedPane.selectedTabPadInsets", new Insets(2, 2, 2, 2));
        t.put("TabbedPane.tabAreaInsets", new Insets(4, 4, 0, 4));
        t.put("TabbedPane.contentBorderInsets", new Insets(12, 12, 14, 14));
        t.put("TabbedPane.tabsOverlapBorder", Boolean.TRUE);    // tabs lie on top of the page
        t.put("ScrollBar.width", 22);
        t.put("ScrollBar.minimumThumbSize", new Dimension(34, 34));
        t.put("Slider.tickColor", new Color(0x2b, 0x1b, 0x10, 160));
        t.put("ProgressBar.horizontalSize", new Dimension(200, 26));
        t.put("ProgressBar.verticalSize", new Dimension(26, 200));
        t.put("ProgressBar.border", new javax.swing.plaf.BorderUIResource.EmptyBorderUIResource(2, 2, 2, 2));
        t.put("ProgressBar.selectionBackground", INK);
        t.put("ProgressBar.selectionForeground", INK);
        for (String n : new String[]{"ComboBox", "Spinner"}) {
            t.put(n + ".background", CLEAR);
            t.put(n + ".foreground", INK);
            t.put(n + ".selectionBackground", MARKER);
            t.put(n + ".selectionForeground", INK);
            t.put(n + ".border", new InkBorders.Underline());
        }
        t.put("SplitPane.dividerSize", 12);
        t.put("SplitPane.border", new javax.swing.plaf.BorderUIResource.EmptyBorderUIResource(0, 0, 0, 0));
        t.put("MenuBar.border", new InkBorders.Rule());
        t.put("ToolBar.border", new InkBorders.Rule());
        t.put("Separator.foreground", new Color(0x2b, 0x1b, 0x10, 140));
        t.put("PopupMenu.background", InkPaper.BASE);
        t.put("PopupMenu.border", new InkBorders.Slip(-2));
        for (String n : new String[]{"Menu", "MenuItem", "CheckBoxMenuItem", "RadioButtonMenuItem"}) {
            t.put(n + ".background", InkPaper.SLIP);
            t.put(n + ".foreground", INK);
            t.put(n + ".selectionBackground", new Color(0xf0d98a));
            t.put(n + ".selectionForeground", INK);
        }
    }
}
