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

package ea.internal.ani;

import ea.AnimationsEndeReagierbar;
import ea.Manager;
import ea.Raum;
import ea.Ticker;
import ea.internal.util.Logger;

/**
 * Jede Klasse, die ein Raum-Objekt animieren kann, leitet sich hieraus ab.<br />
 * <p/>
 * <div class="achtung">Es können auch Animationen gekoppelt werden, z.B. kann ein Raum-Objekt
 * gleichzeitig linear animiert werden, aber <b>gleichzeitig auch noch von einer zweiten
 * Animierer-Klasse</b> bewegt werden, z.B. einer Kreisanimation. Das Ergebnis hieraus wäre eine
 * "eiernde" Bewegung vorwärts.</div>
 *
 * @author Michael Andonie
 */
public abstract class Animierer implements Ticker {

	/**
	 * Die Anzahl an Millisekunden, die zwischen allen Animationsticks vergeht
	 */
	public static final int MILLISPERTICK=5;

	/**
	 * Objekt, welches animiert wird
	 */
	protected Raum ziel;

	/**
	 * Intervall, in dem ein <code>animationsSchritt()</code> ausgeführt wird.
	 */
	private int intervall;

	/**
	 * Zähler für bereits ausgeführte Bewegungsschritte
	 */
	protected int count;

	/**
	 * Gibt an, ob die Animation in einer Dauerschleife ausgeführt werden soll.
	 */
	protected boolean loop;

	/**
	 * Referenz auf den ausführenden Manager
	 */
	private Manager manager;

	/**
	 * Gibt an, ob dieser Animierer bereits beim Manager angemeldet ist.
	 */
	private boolean angemeldet;

	/**
	 * Der <code>AnimationsEndeReagierbar</code>-Listener, der beim Beenden dieser Animation
	 * informiert wird.
	 */
	private AnimationsEndeReagierbar listener;

	/**
	 * Konstruktor.
	 *
	 * @param ziel
	 * 		zu animierendes Objekt
	 * @param intervall
	 * 		Ticker-Intervall für die <code>tick()</code>-Geschwindikeit
	 * @param loop
	 * 		Ob die Animation dauerhaft wiederholt (geloopt) werden soll
	 * @param m
	 * 		Manager, über den später animiert werden soll
	 * @param listener
	 * 		Listener, der am Ende der Animation aufgerufen wird
	 */
	public Animierer (Raum ziel, boolean loop, Manager m, AnimationsEndeReagierbar listener) {
		this.ziel = ziel;
		this.intervall = MILLISPERTICK;
		this.loop = loop;
		this.manager = m;
		this.listener = listener;
		this.count = 0;

		this.manager.anmelden(this);
		this.angemeldet = true;
	}

	/**
	 * Startet den Tick-Algorythmus
	 */
	public void starten () {
		if (angemeldet) {
			manager.starten(this, intervall);
		} else {
			Logger.error("Dieser Animierer ist bereits abgemeldet!");
		}
	}

	/**
	 * Hält den Tick-Algorythmus an. Macht genau dasselbe wie {@link #anhalten()}, weswegen diese
	 * Methode nun als veraltet gilt. <br> Bitte nutze <code>anhalten()</code> statt dieser Methode,
	 * die alte Methode wird in Zukunft entfernt werden.
	 *
	 * @see #anhalten()
	 * @deprecated v3.0.3
	 */
	@Deprecated
	public void pausieren () {
		this.anhalten();
	}

	/**
	 * Hält den Tick-Algorythmus an. Dies bedeutet, dass die Animation pausiert wird.
	 */
	public void anhalten () {
		if (!angemeldet) { // TODO Exception?
			Logger.warning("Dieser Animierer kann nicht angehalten werden, da seine Animation bereits beendet wurde.");
			return;
		}

		manager.anhalten(this);
		angemeldet = false;
	}

	/**
	 * Beendet diese Animation
	 */
	public void beenden () {
		manager.abmelden(this);
		listener.endeReagieren(this);
	}

	/**
	 * Führt einen Animationsschritt aus
	 */
	public void tick () {
		animationsSchritt();
		count++;
	}

	/**
	 * In dieser Methode werden die individuellen Methoden für die verschiedenen Animierer
	 * festgehalten.<br /> Sie wird automatisch von der Super-Klasse <code>Animierer</code>
	 * aufgerufen, sooft, bis sie intern beendet oder angehalten wird.<br /> In ihr sollte
	 * <b>höchstens einmal</b> das Ziel-Objekt bewegt werden! Ansonsten wird die Interaktion mit der
	 * Klasse <code>Physik</code> und damit das mögliche Einrechnen für die Objekte unmöglich.
	 *
	 * @see #tick()
	 * @see #beenden()
	 * @see #anhalten()
	 */
	public abstract void animationsSchritt ();

	/**
	 * Gibt das Ziel dieser Animation aus.
	 *
	 * @return gemerktes Ziel-Objekt, das von diesem Animierer animiert wird
	 */
	public Raum ziel () {
		return ziel;
	}
}