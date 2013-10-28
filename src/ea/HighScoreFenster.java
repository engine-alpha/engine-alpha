/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
 * 
 * Copyright (C) 2011 Michael Andonie
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

package ea;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Dieses Fenster ist ein Dialog, der ein Array von Namen neben ein Array von Punkten Setzt.
 * 
 * @author Michael Andonie
 */
@SuppressWarnings("serial")
public class HighScoreFenster extends JDialog {
	
	/**
	 * Erstellt ein Fenster zur Darstellung von Highscores.
	 * 
	 * @param parent
	 *            Das Parent-Fenster
	 * @param titel
	 *            Der Titel des Dialogs
	 * @param namen
	 *            Die Namen zur Darstellung
	 * @param punkte
	 *            Die dazugehoerigen Punkte
	 * @param font
	 *            Der Darstellungsfont
	 */
	public HighScoreFenster(Frame parent, String titel, String[] namen, int[] punkte, Font font) {
		super(parent, titel, true);
		
		if (namen.length != punkte.length) {
			System.err.println("Achtung!! Die eingegebenen String- und int-Arrays haben nicht die selbe Laenge!!!");
			return;
		}
		
		Dimension screenSize = getToolkit().getScreenSize();
		this.setLocation(screenSize.width / 4, screenSize.height / 4);
		
		this.setLayout(new BorderLayout());
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(namen.length, 3, 5, 5));
		for (int i = 0; i < namen.length; i++) {
			JLabel l1 = new JLabel("" + (i + 1) + ".");
			l1.setFont(font);
			panel.add(l1);
			JLabel l2 = new JLabel(namen[i]);
			l2.setFont(font);
			panel.add(l2);
			JLabel l3 = new JLabel("" + punkte[i]);
			l3.setFont(font);
			panel.add(l3);
		}
		getContentPane().add(new JScrollPane(panel), BorderLayout.CENTER);
		
		JButton b = new JButton("OK");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		getContentPane().add(b, BorderLayout.SOUTH);
		
		validate();
		this.setSize(200, 300);
		this.setVisible(true);
	}
	
}
