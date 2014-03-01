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

import java.awt.Graphics2D;

import ea.internal.gra.Listung;

/**
 * Das Dreieck ist die Basiszeichenklasse.<br />
 * Jeder Koerper laesst sich aus solchen darstellen.<br />
 * Daher ist dies die <b>einzige</b> Klasse, die in sich eine Zeichenroutine hat
 * 
 * @author Michael Andonie
 */
@SuppressWarnings("serial")
public class Dreieck extends Geometrie {
	/**
	 * Die X-Koordinaten der Punkte
	 */
	private int[] x = new int[3];
	
	/**
	 * Die Y-Koordinaten der Punkte
	 */
	private int[] y = new int[3];
	
	/**
	 * Die Darstellungsfarbe
	 */
	private java.awt.Color farbe = java.awt.Color.white;
	
	/**
	 * Konstruktor fuer Objekte der Klasse Dreieck
	 * 
	 * @param p1
	 *            Der erste Punkt des Dreiecks
	 * @param p2
	 *            Der zweite Punkt des Dreiecks
	 * @param p3
	 *            Der dritte Punkt des Dreiecks
	 */
	public Dreieck(Punkt p1, Punkt p2, Punkt p3)
	{
		super(0, 0);
		x[0] = p1.x;
		x[1] = p2.x;
		x[2] = p3.x;
		y[0] = p1.y;
		y[1] = p2.y;
		y[2] = p3.y;
		aktualisierenFirst();
	}
	
	/**
	 * Konstruktor
	 * 
	 * @param x
	 *            Alle X-Koordinaten als Feld
	 * @param y
	 *            Alle Y-Koordinaten als Feld
	 */
	public Dreieck(int[] x, int[] y) {
		super(0, 0);
		if (x.length == 3 && y.length == 3) {
			this.x = x;
			this.y = y;
		} else {
			System.out.println("Läuft nicht, falsche Arraylengen bei Dreiecksbildung!");
		}
	}
	
	/**
	 * Setzt die Farbe ueber die JAVA-Farbklasse.
	 * 
	 * @param c
	 *            Die Farbe dieses Dreiecks, anhand der Klasse <code>Color</code>.
	 */
	public void setColor(java.awt.Color c) {
		farbe = c;
	}
	
	/**
	 * @return Die Farbe dieses Dreiecks
	 */
	public java.awt.Color getColor() {
		return farbe;
	}
	
	/**
	 * Setzt die drei Punkte dieses Dreiecks neu.
	 * 
	 * @param p1
	 *            Der 1. neue Punkt des Dreiecks
	 * @param p2
	 *            Der 2. neue Punkt des Dreiecks
	 * @param p3
	 *            Der 3. neue Punkt des Dreiecks
	 * @see punkteSetzen(int[], int[])
	 */
	public void punkteSetzen(Punkt p1, Punkt p2, Punkt p3) {
		x[0] = p1.x;
		x[1] = p2.x;
		x[2] = p3.x;
		y[0] = p1.y;
		y[1] = p2.y;
		y[2] = p3.y;
	}
	
	/**
	 * Setzt die drei Punkte dieses Dreiecks nue
	 * 
	 * @param x
	 *            Die Koordinaten aller X-Punkte. Der Index gibt den Punkt an (x[0] und y[0] bilden einen Punkt)
	 * @param y
	 *            Die Koordinaten aller Y-Punkte. Der Index gibt den Punkt an (x[0] und y[0] bilden einen Punkt)
	 */
	public void punkteSetzen(int[] x, int[] y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Methode zum Verschieben
	 * 
	 * @param v
	 *            Die Verschiebung als Vektor
	 * @see Raum.verschieben(Vektor)
	 */
	@Override
	public void verschieben(Vektor v) {
		for (int i = 0; i < 3; i++) {
			x[i] += v.x;
			y[i] += v.y;
		}
	}
	
	/**
	 * Test, ob ein anderes Raum-Objekt von diesem geschnitten wird.<br />
	 * Die Wichtigste Schneidemethode, da diese auch ausserhalb der Engine verwendet werden soll!
	 */
	@Override
	public boolean schneidet(Raum r) {
		if (r instanceof Listung) {
			BoundingRechteck[] f = r.flaechen();
			for (int i = 0; i < f.length; i++) {
				if (this.schneidetBasic(f[i])) {
					return true;
				}
			}
		} else {
			return this.dimension().schneidetBasic(r.dimension());
		}
		return false;
	}
	
	/**
	 * Gibt an, ob diese Dreieck sich mit einem anderen schneidet.<br />
	 * Dem Test zugrunde liegt folgene Mathematische Schlussfolgerung als Bedingung fuer das schneiden:<br/ >
	 * <b> 2 Dreiecke schneiden sich,<br />
	 * ->sobald mindestens ein Punkt des einen Dreiecks innerhalb des anderen liegt.</b><br />
	 * Dies ist die Grundlegende Testeinheit fuer alle geometrischen Formen Formen der Engine.
	 * 
	 * @return <code>true</code>, wenn sich die beiden Dreiecke theoretisch schneiden wuerden, sonst <code>false</code>.
	 */
	public boolean schneidetBasic(Dreieck d) {
		return false;
	}
	
	public boolean schneidetBasic(BoundingRechteck r) {
		return r.schneidet(this);
	}
	
	/**
	 * Die implementierte dimension()-Methode.
	 * 
	 * @return Das BoundingRechteck, das das Dreieck exakt umschreibt.
	 */
	@Override
	public BoundingRechteck dimension() {
		int kleinstesX = x[0];
		int groesstesX = x[0];
		int kleinstesY = y[0];
		int groesstesY = y[0];
		
		for (int i = 0; i < 3; i++) {
			if (x[i] > groesstesX) {
				groesstesX = x[i];
			}
			if (x[i] < kleinstesX) {
				kleinstesX = x[i];
			}
			if (y[i] > groesstesY) {
				groesstesY = y[i];
			}
			if (y[i] < kleinstesY) {
				kleinstesY = y[i];
			}
		}
		
		return new BoundingRechteck(kleinstesX, kleinstesY, (groesstesX - kleinstesX), (groesstesY - kleinstesY));
	}
	
	/**
	 * @return Ein Punkt-Array der Groesse 3, das die drei das Dreieck beschreibenden Punkte enthaelt.
	 */
	public Punkt[] punkte() {
		Punkt[] ret = new Punkt[3];
		for (int i = 0; i < 3; i++) {
			ret[i] = new Punkt(x[i], y[i]);
		}
		return ret;
	}
	
	/**
	 * @return Ein Gerade-Array der Groesse 3, das die Gerade-Objekte beinhaltet, die dieses Dreieck beschreiben.
	 */
	public Gerade[] geraden() {
		Gerade[] ret = {
			new Gerade(new Punkt(x[0], y[0]), new Punkt(x[1], y[1])),
			new Gerade(new Punkt(x[0], y[0]), new Punkt(x[2], y[2])),
			new Gerade(new Punkt(x[1], y[1]), new Punkt(x[2], y[2])),
		};
		return ret;
	}
	
	/**
	 * @return Die obere Gerade des Rechtecks.<br />
	 *         <b>Zur Erlaeuterung der Geradennamen: Siehe Handbuch!</b>
	 */
	public Gerade obere() {
		Punkt[] punkte = punkte();
		Punkt p1 = punkte[0];
		Punkt p2;
		for (int i = 1; i < 3; i++) {
			if (punkte[i].y > p1.y) {
				p1 = punkte[i];
			}
		}
		if (p1 == punkte[0]) {
			p2 = punkte[1];
		} else {
			p2 = punkte[0];
		}
		for (int i = 0; i < 3; i++) {
			if (punkte[i].y > p2.y && punkte[i] != p1) {
				p2 = punkte[i];
			}
		}
		return new Gerade(p1, p2);
	}
	
	/**
	 * Zeichnet das Objekt.
	 * 
	 * @param g
	 *            Das zutaendige Graphics-Objekt
	 * @param r
	 *            Das BoundingRechteck, das das Kamerabild beschreibt.
	 */
	@Override
	public void zeichnen(Graphics2D g, BoundingRechteck r) {
		if (!r.schneidetBasic(this.dimension())) {
			return;
		}
		int[] x = {
			this.x[0],
			this.x[1],
			this.x[2]
		};
		int[] y = {
			this.y[0],
			this.y[1],
			this.y[2]
		};
		for (int i = 0; i < 3; i++) {
			x[i] -= r.x;
			y[i] -= r.y;
		}
		g.setColor(farbe);
		g.fillPolygon(x, y, 3);
	}
	
	public Dreieck[] neuBerechnen() {
		Dreieck[] e = { this };
		return e;
	}
}
