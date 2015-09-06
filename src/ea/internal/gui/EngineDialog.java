package ea.internal.gui;

import ea.internal.util.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * Basisklassen für die Dialogfenster der Engine.
 * Created by andonie on 06.09.15.
 */
public class EngineDialog
extends JDialog {

    public EngineDialog(Frame parent, String titel, boolean modal) {
        super(parent, titel, modal);


        Dimension screenSize = getToolkit().getScreenSize();
        this.setLocation(screenSize.width / 4, screenSize.height / 4);


        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            Logger.error("Dialog", e.getLocalizedMessage());
        }
    }

}
