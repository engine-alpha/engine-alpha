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

package ea.edu.mine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Diese Klasse der EDU-Version ermoeglicht das <b>extrem einfache, Schulunterrichtorientierte</b> umsetzen des Unterrichtszieles "Minesweeper".<br />
 * Sie spiegelt eine echte <code>Game</code>-Klasse wider.
 * 
 * @author Andonie
 * @since 2.1
 */
public abstract class MineGameE {
	
	/**
	 * Das <code>Game</code>-Objekt als Spiegel
	 */
	private final Spiegel game;
	
	/**
	 * Das Objekt, dessen Interaktionsmethoden aufgerufen werden.
	 */
	private final Object aim = this;
	
	/**
	 * Die Tasten-Methode des AIMs
	 */
	private Method taste;
	
	/**
	 * Die Tick-Methode des AIMs
	 */
	private Method klick;
	
	/**
	 * Die Rechtsklick-Methode des AIMs
	 */
	private Method klickR;
	
	/**
	 * Konstruktor eines Mine-Games. Erstellt im Hintergrund alles Noetige.
	 * 
	 * @param breiteF
	 *            Fensterbreite
	 * @param hoeheF
	 *            Fensterhoehe
	 * @param titelF
	 *            Fenstertitel
	 */
	protected MineGameE(int breiteF, int hoeheF, String titelF) {
		game = Spiegel.getSpiegel(this, breiteF, hoeheF, titelF);
		Method[] meth = aim.getClass().getMethods();
		for (int i = 0; i < meth.length; i++) {
			if (meth[i].getName().equals("tasteReagieren")) {
				taste = meth[i];
			} else if (meth[i].getName().equals("linksKlickReagieren")) {
				klick = meth[i];
			} else if (meth[i].getName().equals("rechtsKlickReagieren")) {
				klickR = meth[i];
			}
		}
	}
	
	/**
	 * Interner Konstruktor fuer den Error-Frame
	 */
	MineGameE() {
		this.game = null;
	}
	
	/**
	 * Diese Methode wird beim Tastendruck aufgerufen.<br />
	 * Sie wird an das <code>aim</code>-Objekt weitergeleitet.
	 * 
	 * @param code
	 *            Der Tastencode der gedrueckten Taste.
	 * @see #MineGameE(int, int, String)
	 */
	public final void taste(int code) {
		if (taste == null) {
			return;
		}
		try {
			taste.invoke(aim, new Object[] { code });
		} catch (IllegalAccessException ex) {
			System.err.println("Achtung! Der Zugriff auf die Methode fuer Tasten hat nicht funktioniert. BUG!");
		} catch (InvocationTargetException ex) {
			System.err.println("Achtung! Das Objekt, an dem die Methode aufzurufen war, besass selbige nicht. BUG!");
		}
	}
	
	/**
	 * Diese Methode wird beim Linksklick aufgerufen.<br />
	 * Sie wird an das <code>aim</code>-Objekt weitergeleitet.
	 * 
	 * @param x
	 *            Die X-Koordinate des Klicks.
	 * @param y
	 *            Die Y-Koordinate des Klicks.
	 * @see #MineGameE(int, int, String)
	 */
	public final void klick(int x, int y) {
		if (klick == null) {
			return;
		}
		try {
			klick.invoke(aim, new Object[] { x, y });
		} catch (IllegalAccessException ex) {
			System.err.println("Achtung! Der Zugriff auf die Methode fuer Klicks hat nicht funktioniert. BUG!");
		} catch (InvocationTargetException ex) {
			System.err.println("Achtung! Das Objekt, an dem die Methode aufzurufen war, besass selbige nicht. BUG!");
		}
	}
	
	/**
	 * Diese Methode wird beim Rechtsklick aufgerufen.<br />
	 * Sie wird an das <code>aim</code>-Objekt weitergeleitet.
	 * 
	 * @param x
	 *            Die X-Koordinate des Klicks.
	 * @param y
	 *            Die Y-Koordinate des Klicks.
	 * @see #MineGameE(int, int, String)
	 */
	public final void klickR(int x, int y) {
		if (klickR == null) {
			return;
		}
		try {
			klickR.invoke(aim, new Object[] { x, y });
		} catch (IllegalAccessException ex) {
			System.err.println("Achtung! Der Zugriff auf die Methode fuer Klicks hat nicht funktioniert. BUG!");
		} catch (InvocationTargetException ex) {
			System.err.println("Achtung! Das Objekt, an dem die Methode aufzurufen war, besass selbige nicht. BUG!");
		}
	}
}
