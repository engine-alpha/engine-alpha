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

package ea.internal.ani;

import ea.AnimationsEndeReagierbar;
import ea.Animierer;
import ea.Gerade;
import ea.Manager;
import ea.Punkt;
import ea.Raum;
import ea.RegEck;
import ea.Vektor;

/**
 * Beschreiben Sie hier die Klasse KreisAnimierer.
 * Er animiert vorlaeufig nur im Urzeigersinn.
 * 
 * @author Michael Andonie
 */
@SuppressWarnings("serial")
public class KreisAnimierer extends Animierer {
	/**
	 * Der aktuelle Winkel im Gradmass zur Vertikalen.<br />
	 * Im <b>Bogenmass</b>
	 */
	private double winkel;
	
	/**
	 * Der Radius des Kreises, der die Bewegung beschreibt.
	 */
	private final double radius;
	
	/**
	 * Der Schritt, der bei einem Aufruf gemacht wird
	 */
	private static final double schritt = Math.PI / 100;
	
	/**
	 * Das Zentrum des die Drehbewegung beschreibenden Kreises.
	 */
	private final Punkt zentrum;
	
	/**
	 * Der Endpunkt des letzten Animationsschrittes
	 */
	private Punkt letzter;
	
	/**
	 * Konstruktor fuer Objekte der Klasse KreisAnimierer
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
	public KreisAnimierer(Raum ziel, Punkt zentrum, int intervall, boolean loop, Manager m, AnimationsEndeReagierbar listener) {
		super(ziel, intervall, loop, m, listener);
		this.zentrum = zentrum;
		this.letzter = ziel.zentrum();
		Punkt zielMitte = ziel.zentrum();
		radius = zentrum.abstand(zielMitte);
		winkel = new Gerade(zielMitte, zentrum).winkel();
		if (zentrum.x < zielMitte.x && zentrum.y < zielMitte.y) {
			winkel = Math.PI - winkel;
		}
	}
	
	public void animationsSchritt() {
		winkel += schritt;
		int x, y;
		x = RegEck.runden((-Math.sin(winkel)) * radius) + zentrum.x;
		y = RegEck.runden((Math.cos(winkel)) * radius) + zentrum.y;
		Vektor v = new Vektor(x - letzter.x, y - letzter.y);
		ziel.bewegen(v);
		letzter = letzter.verschobenerPunkt(v);
	}
}
