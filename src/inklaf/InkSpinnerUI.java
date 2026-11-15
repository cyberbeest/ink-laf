package inklaf;

import java.awt.Component;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSpinnerUI;

public class InkSpinnerUI extends BasicSpinnerUI {
    public static ComponentUI createUI(JComponent c) { return new InkSpinnerUI(); }

    @Override protected Component createNextButton() {
        JComponent b = new InkArrowButton(SwingConstants.NORTH);
        b.setName("Spinner.nextButton");
        installNextButtonListeners(b);
        return b;
    }

    @Override protected Component createPreviousButton() {
        JComponent b = new InkArrowButton(SwingConstants.SOUTH);
        b.setName("Spinner.previousButton");
        installPreviousButtonListeners(b);
        return b;
    }
}
