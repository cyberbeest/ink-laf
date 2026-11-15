package demo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import inklaf.InkLookAndFeel;

/**
 * SwingSet-style demo for the Ink look and feel, with Metal and Nimbus for comparison.
 *   java demo.Demo                 interactive
 *   java demo.Demo --shot DIR      headless: one PNG per tab into DIR
 */
public class Demo {
    /** key (command line), label (dropdown), class. */
    static final String[][] LAFS = {
        {"ink", "Ink", InkLookAndFeel.class.getName()},
        {"metal", "Metal", "javax.swing.plaf.metal.MetalLookAndFeel"},
        {"nimbus", "Nimbus", "javax.swing.plaf.nimbus.NimbusLookAndFeel"},
    };

    JTabbedPane tabs;
    JLabel status = new JLabel(" ");

    JComponent buildContent() {
        tabs = new JTabbedPane();
        tabs.addTab("Buttons", buttonsTab());
        tabs.addTab("Text", textTab());
        tabs.addTab("Choices", choicesTab());
        tabs.addTab("Data", dataTab());
        tabs.addTab("Containers", containersTab());
        tabs.addTab("Menus", menusTab());

        JPanel root = new JPanel(new BorderLayout());
        root.add(tabs, BorderLayout.CENTER);
        status.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        root.add(status, BorderLayout.SOUTH);
        return root;
    }

    JButton btn(String text) {
        JButton b = new JButton(text);
        b.addActionListener(e -> status.setText("Clicked: " + text));
        return b;
    }

    JComponent buttonsTab() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        p.add(btn("Proceed"));
        p.add(btn("Enchant"));
        JButton off = btn("Forbidden");
        off.setEnabled(false);
        p.add(off);
        p.add(btn("OK"));
        p.add(btn("Summon the Mighty Dragon of the North"));
        JButton def = btn("Default");
        p.add(def);
        JButton dialog = new JButton("Open Decree");
        dialog.addActionListener(e -> JOptionPane.showMessageDialog(p, "Hear ye, hear ye!\nThe scroll hath spoken.",
                "Royal Decree", JOptionPane.INFORMATION_MESSAGE));
        p.add(dialog);
        JButton confirm = new JButton("Ask Question");
        confirm.addActionListener(e -> JOptionPane.showConfirmDialog(p, "Dost thou wish to continue?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION));
        p.add(confirm);
        return p;
    }

    JComponent textTab() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0; c.gridy = 0; p.add(new JLabel("Name:"), c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1; p.add(new JTextField("Sir Reginald", 24), c);
        c.gridx = 0; c.gridy = 1; c.fill = 0; c.weightx = 0; p.add(new JLabel("Password:"), c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; p.add(new JPasswordField("secret", 24), c);
        c.gridx = 0; c.gridy = 2; c.fill = 0; c.weightx = 0; p.add(new JLabel("Gold coins:"), c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;
        JFormattedTextField gold = new JFormattedTextField(java.text.NumberFormat.getIntegerInstance());
        gold.setValue(1250);
        p.add(gold, c);
        c.gridx = 0; c.gridy = 3; c.fill = 0; c.weightx = 0; p.add(new JLabel("Disabled:"), c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;
        JTextField dis = new JTextField("Sealed by order of the Crown");
        dis.setEnabled(false);
        p.add(dis, c);
        c.gridx = 0; c.gridy = 4; c.fill = 0; c.weightx = 0; c.anchor = GridBagConstraints.NORTHWEST; p.add(new JLabel("Chronicle:"), c);
        JTextArea ta = new JTextArea("In the days of old, when the great forests still whispered\n"
                + "the names of forgotten kings, there lived a scribe who\n"
                + "wrote upon scrolls of finest vellum...\n", 8, 40);
        ta.setLineWrap(true);
        c.gridx = 1; c.fill = GridBagConstraints.BOTH; c.weighty = 1; p.add(new JScrollPane(ta), c);
        return p;
    }

    JComponent choicesTab() {
        JPanel p = new JPanel(new GridLayout(1, 3, 16, 0));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel checks = new JPanel(new GridLayout(0, 1, 0, 4));
        checks.setBorder(BorderFactory.createTitledBorder("Provisions"));
        checks.add(new JCheckBox("Bread", true));
        checks.add(new JCheckBox("Mead"));
        checks.add(new JCheckBox("Dried apples", true));
        JCheckBox nope = new JCheckBox("Dragon eggs");
        nope.setEnabled(false);
        checks.add(nope);
        p.add(north(checks));

        JPanel radios = new JPanel(new GridLayout(0, 1, 0, 4));
        radios.setBorder(BorderFactory.createTitledBorder("Steed"));
        ButtonGroup g = new ButtonGroup();
        for (String s : new String[]{"Horse", "Mule", "Griffin"}) {
            JRadioButton r = new JRadioButton(s, s.equals("Horse"));
            g.add(r);
            radios.add(r);
        }
        p.add(north(radios));

        JPanel misc = new JPanel();
        misc.setLayout(new BoxLayout(misc, BoxLayout.Y_AXIS));
        misc.setBorder(BorderFactory.createTitledBorder("Misc"));
        JComboBox<String> plain = new JComboBox<>(new String[]{"North", "South", "East", "West"});
        misc.add(plain);
        misc.add(Box.createVerticalStrut(8));
        JComboBox<String> edit = new JComboBox<>(new String[]{"Edit me", "Second"});
        edit.setEditable(true);
        misc.add(edit);
        misc.add(Box.createVerticalStrut(8));
        JSlider sl = new JSlider(0, 100, 40);
        sl.setMajorTickSpacing(25);
        sl.setMinorTickSpacing(5);
        sl.setPaintTicks(true);
        sl.setPaintLabels(true);
        misc.add(sl);
        misc.add(Box.createVerticalStrut(8));
        misc.add(new JSpinner(new SpinnerNumberModel(7, 0, 99, 1)));
        misc.add(Box.createVerticalStrut(8));
        misc.add(new JProgressBar(0, 100) {{ setValue(65); setStringPainted(true); }});
        misc.add(Box.createVerticalStrut(8));
        misc.add(new JProgressBar() {{ setIndeterminate(true); }});
        misc.add(Box.createVerticalStrut(8));
        JToggleButton tb = new JToggleButton("Toggle");
        misc.add(tb);
        p.add(north(misc));
        return p;
    }

    JComponent containersTab() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        for (String n : new String[]{"Open", "Save", "Print"}) {
            JButton b = btn(n);
            b.setToolTipText("A scribe's tool: " + n);
            tb.add(b);
        }
        tb.addSeparator();
        tb.add(new JLabel(" Quill: "));
        JComboBox<String> quill = new JComboBox<>(new String[]{"Goose", "Raven", "Swan"});
        quill.setMaximumSize(quill.getPreferredSize());
        tb.add(quill);
        p.add(tb, BorderLayout.NORTH);

        JTabbedPane bottom = new JTabbedPane(JTabbedPane.BOTTOM);
        bottom.addTab("Almanac", new JScrollPane(new JTextArea("Winter solstice: feast of lanterns.\nSpring: sowing of the first barley.\n", 6, 30)));
        bottom.addTab("Bestiary", new JLabel("  Here be dragons.", SwingConstants.LEFT));
        JTabbedPane left = new JTabbedPane(JTabbedPane.LEFT);
        left.addTab("Maps", new JLabel("  Parchment maps"));
        left.addTab("Spells", new JLabel("  Forbidden spells"));
        JTabbedPane off = new JTabbedPane();
        off.addTab("Open", new JLabel("  Enabled tab"));
        off.addTab("Sealed", new JLabel("  Locked"));
        off.setEnabledAt(1, false);
        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, bottom, left);
        sp.setDividerLocation(420);
        JSplitPane sp2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sp, off);
        sp2.setDividerLocation(230);
        p.add(sp2, BorderLayout.CENTER);
        return p;
    }

    JMenuBar menuBar() {
        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem open = new JMenuItem("Open scroll…");
        open.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        JMenuItem save = new JMenuItem("Seal & save");
        save.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        JMenuItem gone = new JMenuItem("Burn");
        gone.setEnabled(false);
        file.add(open); file.add(save); file.addSeparator(); file.add(gone);
        JMenu recent = new JMenu("Recent");
        recent.add(new JMenuItem("Chronicle.txt")); recent.add(new JMenuItem("Bestiary.txt"));
        file.add(recent);
        file.addSeparator();
        JMenuItem quit = new JMenuItem("Quit");
        quit.addActionListener(e -> System.exit(0));
        file.add(quit);
        JMenu view = new JMenu("View");
        view.add(new JCheckBoxMenuItem("Show candlelight", true));
        view.add(new JCheckBoxMenuItem("Show margins"));
        view.addSeparator();
        ButtonGroup g = new ButtonGroup();
        for (String z : new String[]{"Small", "Medium", "Large"}) {
            JRadioButtonMenuItem r = new JRadioButtonMenuItem(z, z.equals("Medium"));
            g.add(r);
            view.add(r);
        }
        JMenu help = new JMenu("Help");
        help.add(new JMenuItem("About the scroll"));
        mb.add(file); mb.add(view); mb.add(help);
        return mb;
    }

    JComponent menusTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(menuBar(), BorderLayout.NORTH);
        JPanel mid = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        JLabel hint = new JLabel("Right-click anywhere in this area for a popup menu.");
        mid.add(hint);
        JPopupMenu popup = new JPopupMenu();
        popup.add(new JMenuItem("Cut"));
        popup.add(new JMenuItem("Copy"));
        popup.add(new JMenuItem("Paste"));
        popup.addSeparator();
        popup.add(new JCheckBoxMenuItem("Word wrap", true));
        mid.setComponentPopupMenu(popup);
        JButton tip = btn("Hover for a tip");
        tip.setToolTipText("Hark! A tooltip upon the scroll.");
        mid.add(tip);
        p.add(mid, BorderLayout.CENTER);
        return p;
    }

    static JComponent north(JComponent c) {
        JPanel w = new JPanel(new BorderLayout());
        w.add(c, BorderLayout.NORTH);
        return w;
    }

    JComponent dataTab() {
        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Kingdom");
        DefaultMutableTreeNode north = new DefaultMutableTreeNode("The North");
        north.add(new DefaultMutableTreeNode("Frostkeep"));
        north.add(new DefaultMutableTreeNode("Wolfden"));
        root.add(north);
        DefaultMutableTreeNode south = new DefaultMutableTreeNode("The South");
        south.add(new DefaultMutableTreeNode("Sunport"));
        root.add(south);
        JTree tree = new JTree(root);
        for (int i = 0; i < tree.getRowCount(); i++) tree.expandRow(i);
        sp.setLeftComponent(new JScrollPane(tree));

        JTable table = new JTable(new DefaultTableModel(new Object[][]{
                {"Aldric", "Knight", 34}, {"Mirelle", "Alchemist", 28}, {"Torvald", "Bard", 41}, {"Ysolde", "Ranger", 25},
                {"Gunnar", "Smith", 52}, {"Elowen", "Healer", 31}, {"Bram", "Rogue", 23}, {"Sigrid", "Scribe", 37}, {"Odo", "Cook", 45}},
                new String[]{"Name", "Class", "Age"}));
        table.setRowHeight(26);
        String[] items = new String[40];
        for (int i = 0; i < items.length; i++) items[i] = new String[]{"Sword", "Shield", "Healing potion", "Map", "Lantern", "Rope", "Torch", "Spellbook"}[i % 8] + " no. " + (i + 1);
        JList<String> list = new JList<>(items);
        JSplitPane right = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(table), new JScrollPane(list));
        JScrollPane tsp = (JScrollPane) right.getTopComponent();
        tsp.setColumnHeaderView(table.getTableHeader());   // normally added on realization; explicit so headless renders match
        right.setDividerLocation(170);
        sp.setRightComponent(right);
        sp.setDividerLocation(180);
        return sp;
    }

    JMenuBar menuBarForFrame() { return menuBar(); }

    void show(String start) { show(start, null, 0); }

    void show(String start, Rectangle bounds, int tab) {
        boolean decorated = start.equalsIgnoreCase("Ink");     // Ink draws its own window frame
        JFrame.setDefaultLookAndFeelDecorated(decorated);
        JDialog.setDefaultLookAndFeelDecorated(decorated);
        JFrame f = new JFrame("Ink — Look & Feel Demo");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JComboBox<String> lafBox = new JComboBox<>();
        for (String[] l : LAFS) lafBox.addItem(l[1]);
        for (int i = 0; i < LAFS.length; i++) if (LAFS[i][0].equalsIgnoreCase(start)) lafBox.setSelectedIndex(i);
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        bar.add(new JLabel(" Look & Feel: "));
        bar.add(lafBox);
        f.setJMenuBar(menuBarForFrame());
        f.add(bar, BorderLayout.NORTH);
        f.add(buildContent(), BorderLayout.CENTER);
        tabs.setSelectedIndex(tab);
        lafBox.addActionListener(e -> {
            String name = LAFS[lafBox.getSelectedIndex()][0];
            try { UIManager.setLookAndFeel(LAFS[lafBox.getSelectedIndex()][2]); }
            catch (Exception ex) { status.setText(ex.toString()); return; }
            // window decorations are fixed when a frame is created, so switching builds a fresh frame
            new Demo().show(name, f.getBounds(), tabs.getSelectedIndex());
            f.dispose();
        });
        if (bounds != null) f.setBounds(bounds);
        else { f.setSize(1000, 640); f.setLocationRelativeTo(null); }
        f.setVisible(true);
    }

    static void shots(File dir, String key) throws Exception {
        dir.mkdirs();
        String cls = LAFS[0][2];
        for (String[] l : LAFS) if (l[0].equalsIgnoreCase(key)) cls = l[2];
        UIManager.setLookAndFeel(cls);
        String prefix = key.equalsIgnoreCase("ink") ? "" : key.toLowerCase() + "-";
        Demo app = new Demo();
        JComponent content = app.buildContent();
        JRootPane host = new JRootPane();   // a real root pane, so root-level painting (e.g. paper) happens
        host.setContentPane(content);
        for (int i = 0; i < app.tabs.getTabCount(); i++) {
            app.tabs.setSelectedIndex(i);
            int scale = 1;
            host.setSize(1000, 560);
            layoutAll(host);
            BufferedImage img = new BufferedImage(1000 * scale, 560 * scale, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            host.paint(g);
            g.dispose();
            File out = new File(dir, prefix + "tab-" + (i + 1) + "-" + app.tabs.getTitleAt(i).toLowerCase() + ".png");
            ImageIO.write(img, "png", out);
            System.out.println("wrote " + out);
        }
        shotMenus(dir, prefix);
    }

    static void shotMenus(File dir, String prefix) throws Exception {
        Demo app = new Demo();
        JMenuBar mb = app.menuBar();
        JPanel inner = new JPanel(null);
        JRootPane host = new JRootPane();
        host.setContentPane(inner);
        host.setSize(640, 330);
        mb.setBounds(0, 0, 640, mb.getPreferredSize().height);
        inner.add(mb);
        layoutAll(host);
        BufferedImage img = new BufferedImage(640, 330, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        host.paint(g);
        JPopupMenu pm = mb.getMenu(0).getPopupMenu();
        pm.setSize(pm.getPreferredSize());
        layoutAll(pm);
        Graphics2D g2 = (Graphics2D) g.create(10, 36, pm.getWidth(), pm.getHeight());
        pm.paint(g2);
        JPopupMenu pv = mb.getMenu(1).getPopupMenu();
        pv.setSize(pv.getPreferredSize());
        layoutAll(pv);
        Graphics2D g3 = (Graphics2D) g.create(300, 36, pv.getWidth(), pv.getHeight());
        pv.paint(g3);
        JToolTip tt = new JButton().createToolTip();
        tt.setTipText("Hark! A tooltip upon the scroll.");
        tt.setSize(tt.getPreferredSize());
        Graphics2D g4 = (Graphics2D) g.create(300, 220, tt.getWidth(), tt.getHeight());
        tt.paint(g4);
        g.dispose();
        File out = new File(dir, prefix + "menus-popups.png");
        ImageIO.write(img, "png", out);
        System.out.println("wrote " + out);
    }

    static void layoutAll(Container c) {
        c.doLayout();
        for (Component k : c.getComponents()) if (k instanceof Container cc) layoutAll(cc);
    }

    public static void main(String[] args) throws Exception {
        if (args.length >= 2 && args[0].equals("--shot")) {
            System.setProperty("java.awt.headless", "true");
            String key = args.length > 2 ? args[2] : "ink";
            SwingUtilities.invokeAndWait(() -> { try { shots(new File(args[1]), key); } catch (Exception e) { throw new RuntimeException(e); } });
            return;
        }
        String start = args.length > 0 ? args[0] : "ink";
        SwingUtilities.invokeLater(() -> {
            try {
                String cls = LAFS[0][2];
                for (String[] l : LAFS) if (l[0].equalsIgnoreCase(start)) cls = l[2];
                UIManager.setLookAndFeel(cls);
            } catch (Exception e) { e.printStackTrace(); }
            new Demo().show(start);
        });
    }
}
