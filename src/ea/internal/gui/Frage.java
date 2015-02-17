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

import ea.internal.util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Eine Frage ist ein modaler Dialog, der eine Frage zwischen "OK" und "Abbrechen" ausgibt.<br />
 * Verwendet werden sollte allerdings dies ueber die Klasse Fenster, folgendermassen:
 * <b>Beispiel:</b><br /> <br /> <code> //Das instanziierte Fenster<br /> Fenster fenster;<br /> <br
 * /> //Senden einer Fensternachricht mit boolean rueckgabe<br /> boolean istOK =
 * fenster.frage("Wollen sie das Programm beenden?");<br /> if(istOK) {<br /> <tab
 * />fenster.beenden();<br /> }<br /> else {<br /> //Nichts<br /> }<br /> </code><br />
 *
 * @author Michael Andonie
 */
@SuppressWarnings ("serial")
public class Frage extends JDialog {

	/**
	 * Das Ergebnis der Frage.
	 */
	public static boolean ergebnis;

	/**
	 * Der Konstruktor. Erstellt das Objekt und setzt es sichtbar.
	 *
	 * @param parent
	 * 		Das noetioge Fenster-Parent-Objekt
	 * @param frage
	 * 		Die Frage, die im Dialog gezeigt wird.
	 * @param janein
	 * 		Ob Ja-Nein zur Auswahl stehen soll oder Ok-Abbrechen
	 * @param font
	 * 		Der Font, in dem die Texte dargestellt werden.
	 */
	public Frage (Frame parent, String frage, boolean janein, Font font) {
		super(parent, true);
		ergebnis = false;
		setLayout(new BorderLayout());
		Dimension screenSize = getToolkit().getScreenSize();
		this.setLocation(screenSize.width / 4, screenSize.height / 4);
		JLabel l = new JLabel(frage);
		l.setFont(font);
		getContentPane().add(l, BorderLayout.CENTER);
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton b;
		JButton d;
		if (janein) {
			b = new JButton("Ja");
			d = new JButton("Nein");
		} else {
			b = new JButton("OK");
			d = new JButton("Abbrechen");
		}
		b.setFont(font);
		d.setFont(font);
		b.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				ergebnis = true;
				dispose();
			}
		});
		p.add(b);
		d.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				ergebnis = false;
				dispose();
			}
		});
		p.add(d);
		getContentPane().add(p, BorderLayout.SOUTH);

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			Logger.error("Dialog", e.getLocalizedMessage());
		}

		pack();
		setVisible(true);
	}
}
