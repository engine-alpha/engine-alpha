/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2014 Michael Andonie and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ea.internal.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * TODO Dokumentation
 *
 * @author Michael Andonie
 */
public class Eingabe extends EngineDialog {

	/**
	 * Das Ergebnis der juengsten Eingabe.<br /> Oeffentlich abrufbar.
	 */
	public static String ergebnis;

	/**
	 * Konstruktor fuer das Eingabefeld
	 *
	 * @param parent
	 * 		Das Parent-Fenster
	 * @param nachricht
	 * 		Die Nachricht zur Eingabeaufforderung
	 * @param font
	 * 		Der Darstellungsfont
	 */
	public Eingabe (Frame parent, String titel, String nachricht, Font font) {
		super(parent, titel, true);
		ergebnis = null;
		setLayout(new BorderLayout());
		JLabel l = new JLabel(nachricht);
		l.setFont(font);
		getContentPane().add(l, BorderLayout.NORTH);

		final JTextField feld = new JTextField(40);
		getContentPane().add(feld, BorderLayout.CENTER);
		feld.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				ergebnis = feld.getText();
				dispose();
			}
		});
		feld.setFont(font);

		JButton fine = new JButton("OK");
		getContentPane().add(fine, BorderLayout.SOUTH);
		fine.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				ergebnis = feld.getText();
				dispose();
			}
		});
		fine.setFont(font);

		pack();
		setVisible(true);
	}
}
