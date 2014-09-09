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

package ea;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Der Leuchtmacher sorgt fuer das Leuchten seiner angemeldeten Leuchtend-Objekte.
 *
 * @author Michael Andonie
 */
@SuppressWarnings ("serial")
public class LeuchtMacher implements Ticker {
	/**
	 * Die Liste aller Leuchter.
	 */
	private CopyOnWriteArrayList<Leuchtend> leuchter = new CopyOnWriteArrayList<Leuchtend>();

	/**
	 * Konstruktor fuer Objekte der Klasse LeuchtMacher.<br /> <b>Dieser sollte niemals vom
	 * Entwickler aufgerufen werden!</b><br /> Dies passiert einmalig intern!!
	 */
	public LeuchtMacher () {
		Manager.standard.anmelden(this, 20);
	}

	/**
	 * Fuegt ein neues Objekt zum evtl leuchten lassen Hinzu.
	 *
	 * @param l
	 * 		Das hinzuzufuegende Leuchtend-Objekt
	 */
	public void add (Leuchtend l) {
		leuchter.add(l);
	}

	/**
	 * Entfernt ein bestimmtes Leuchtend-Objekt, wenn vorhanden
	 *
	 * @param l
	 * 		Das zu entfernende Leuchtend-Objekt, ist dies nicht vorhanden, passiert gar nichts.
	 */
	public void entfernen (Leuchtend l) {
		leuchter.remove(l);
	}

	/**
	 * Die tick()-Methode.<br /> In ihr wird fuer das eventuelle leuchten der einzelnen
	 * Leuchtend-Objekte gesorgt.
	 */
	@Override
	public void tick () {
		for (Leuchtend l : leuchter) {
			if (l.leuchtet()) {
				l.leuchtSchritt();
			}
		}
	}
}
