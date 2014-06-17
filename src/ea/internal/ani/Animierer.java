/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
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
 *
 * <div class="achtung">Es können auch Animationen gekoppelt werden, z.B. kann ein Raum-Objekt gleichzeitig linear animiert werden, aber
 * <b>gleichzeitig auch noch von einer zweiten Animierer-Klasse</b> bewegt werden, z.B. einer Kreisanimation. Das
 * Ergebnis hieraus waere eine "eiernde" Bewegung vorwärts.</div>
 * 
 * @author Michael Andonie
 */
public abstract class Animierer implements Ticker {
	/**
	 * Festgelegter Normwert, der die Anzahl an Unterschritten pro "Animationsetappe" der einzelnen
	 * Animierer wiedergibt. Je kleiner er ist, desto durchschaubarer ist die Animation
	 */
	protected static final int schritte = 100;
	
	/**
	 * Das Raum-Objekt, das animiert wird.
	 */
	protected Raum ziel;
	
	/**
	 * Das Intervall, in dem ein <code>animationsSchritt()</code> ausgefuehrt wird.
	 */
	protected int intervall;
	
	/**
	 * Der count an bisher getanen Bewegungsschritten.
	 */
	protected int count;
	
	/**
	 * Gibt an, ob die Animation in einer Dauerschleife ausgefuehrt werden soll.
	 */
	protected boolean loop;
	
	/** Referenz auf den ausfuehrenden Tick-Manager */
	private Manager manager;
	
	/** Gibt an, ob dieser Animierer bereits beim Manager angemeldet ist */
	private boolean angemeldet;
	
	private AnimationsEndeReagierbar listener;
	
	/**
	 * Konstruktor.
	 * 
	 * @param ziel
	 *            Das zu animierende Objekt
	 * @param intervall
	 *            Der TickerIntervall; fuer die tick()-Geschwindikeit.
	 * @param loop
	 *            Ob die Animation dauerhaft wiederholt (geloopt) werden soll.
	 * @param m
	 *            Der Manager, an dem spaeter animiert werden soll.
	 * @param listener
	 *            Der AnimationsEndeReagierbar-Listener, der am Ende der Animation aufgerufen wird.
	 */
	public Animierer(Raum ziel, int intervall, boolean loop, Manager m, AnimationsEndeReagierbar listener) {
		this.ziel = ziel;
		this.intervall = intervall;
		this.loop = loop;
		this.manager = m;
		this.listener = listener;
		this.count = 0;

		this.manager.anmelden(this);
		this.angemeldet = true;
	}
	
	/**
	 * Starten den Tick-Algorythmus.
	 */
	public void starten() {
		if (angemeldet) {
			manager.starten(this, intervall);
		} else {
			Logger.error("Dieser Animierer ist bereits abgemeldet!");
		}
	}
	
	/**
	 * Hält den Tick-Algorythmus an. Dies bedeutet, dass die Animation pausiert wird.
	 */
	public void anhalten() {
		if (!angemeldet) { // TODO Exception?
			Logger.warning("Dieser Animierer kann nicht angehalten werden, da seine Animation bereits beendet wurde.");
			return;
		}

		manager.anhalten(this);
		angemeldet = false;
	}
	
	/**
	 * Hält den Tick-Algorythmus an. Macht genau dasselbe wie <code>anhalten</code> und ist nur dazu da,
	 * eine weitere Assoziation der Verwendung dieser Methode zu repraesentieren.
	 * 
	 * @see #anhalten()
	 */
	public void pausieren() { // TODO Eine der beiden Methoden auf deprecated setzen bzw. später entfernen
		this.anhalten();
	}
	
	/**
	 * Beendet diese Animation ein für alle mal.
	 */
	public void beenden() {
		manager.abmelden(this);
		listener.endeReagieren(this);
	}
	
	/**
	 * Die Tick-Methode.
	 */
	public void tick() {
		animationsSchritt();
		count++;
	}
	
	/**
	 * Gibt das Ziel dieser Animation aus.
	 * 
	 * @return Das gemerkte Ziel-Objekt, das von diesem Animierer animiert wird
	 */
	public Raum ziel() {
		return ziel;
	}
	
	/**
	 * In dieser Methode werden die individuellen Methoden fuer die verschiedenen Animierer festgehalten.<br />
	 * Sie wird automatisch von der Super-Klasse <code>Animierer</code> aufgerufen, sooft, bis sie intern beendet oder
	 * angehalten wird.<br />
	 * In ihr sollte <b>höchstens einmal</b> das Ziel-Objekt bewegt werden! Ansonsten wird die Interaktion mit der Klasse
	 * <code>Physik</code> und damit das mögliche Einrechnen für die Objekte nicht möglich.
	 * 
	 * @see #tick()
	 * @see #beenden()
	 * @see #anhalten()
	 */
	public abstract void animationsSchritt();
}