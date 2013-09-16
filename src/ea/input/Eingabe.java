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
 *
 * @author Andonie
 */
public class Eingabe 
extends JDialog{

    /**
     * Das Ergebnis der juengsten Eingabe.<br />
     * Oeffentlich abrufbar.
     */
    public static String ergebnis;

    /**
     * Konstruktor fuer das Eingabefeld
     * @param parent    Das Parent-Fenster
     * @param nachricht Die Nachricht zur Eingabeaufforderung
     * @param font      Der Darstellungsfont
     */
    public Eingabe(Frame parent, String nachricht, Font font) {
        super(parent, true);
        ergebnis = null;
        setLayout(new BorderLayout());
        Dimension screenSize = getToolkit().getScreenSize();
        this.setLocation(screenSize.width / 4, screenSize.height / 4);
        JLabel l = new JLabel(nachricht);
        l.setFont(font);
        getContentPane().add(l, BorderLayout.NORTH);

        final JTextField feld = new JTextField(40);
        getContentPane().add(feld, BorderLayout.CENTER);
        feld.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ergebnis = feld.getText();
                dispose();
            }
        });
        feld.setFont(font);

        JButton fine = new JButton("OK");
        getContentPane().add(fine, BorderLayout.SOUTH);
        fine.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ergebnis = feld.getText();
                dispose();
            }
        });
        fine.setFont(font);

        pack();
        setVisible(true);
    }
}
