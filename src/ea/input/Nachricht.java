/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
 *
 * Copyright (C) 2011  Michael Andonie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ea.input;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Nachricht ist ein modaler Dialog, der einfach eine Nachricht an den Benutzer ausgibt.<br />
 * Diese KLasse wird innerhalb des Fensters gehandelt. Hierzu muss nur folgendes passieren:
 * <b>Beispiel:</b><br /><br />
 * <code>
 * //Das instanziierte Fenster<br />
 * Fenster fenster;<br />
 * <br />
 * //Senden einer Fensternachricht<br />
 * fenster.nachrichtAusgeben("Hallo Benutzer", true);<br />
 * </code><br />
 * Das <code>true</code> bei der Methode sorgt dafuer, das die Nachricht erst beendet werden muss, bevor 
 * die Methode beendet ist. Fuer naeheres siehe die Dokumentation der Methode in der Klasse Fenster.
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Nachricht 
extends JDialog {
    
    /**
     * Der Konstruktor der Klasse Nachricht.
     * @param   parent  Das noetige Parent-Fenster
     * @param   modal   Ob die Nachricht modal ist oder nicht.
     * @param   nachricht   Die Nachricht, die angezeigt werden soll.
     * @param font Der Darstellungsfont
     */
    public Nachricht(Frame parent, boolean modal, String nachricht, Font font) {
        super(parent, "Nachricht", modal);
        setLayout(new BorderLayout());
        Dimension screenSize = getToolkit().getScreenSize();
        this.setLocation(screenSize.width / 4, screenSize.height / 4);
        JLabel l = new JLabel(nachricht);
        l.setFont(font);
        getContentPane().add(l, BorderLayout.CENTER);
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton b = new JButton("OK");
        b.setFont(font);
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        p.add(b);
        getContentPane().add(p, BorderLayout.SOUTH);
        try{
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        }catch(Exception e) {}
        pack();
        setVisible(true);
    }
}
