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

import ea.internal.collision.Collider;

import java.awt.*;

/**
 * Das Dreieck ist die Basiszeichenklasse.<br /> Jeder Koerper laesst sich aus solchen
 * darstellen.<br /> Daher ist dies die <b>einzige</b> Klasse, die in sich eine Zeichenroutine hat
 *
 * @author Michael Andonie
 */
public class Dreieck extends Geometrie {
	/**
	 * Die X-Koordinaten der Punkte
	 */
	private float[] x = new float[3];

	/**
	 * Die Y-Koordinaten der Punkte
	 */
	private float[] y = new float[3];

	/**
	 * Die Darstellungsfarbe
	 */
	private java.awt.Color farbe = java.awt.Color.white;

	/**
	 * Konstruktor fuer Objekte der Klasse Dreieck
	 *
	 * @param p1
	 * 		Der erste Punkt des Dreiecks
	 * @param p2
	 * 		Der zweite Punkt des Dreiecks
	 * @param p3
	 * 		Der dritte Punkt des Dreiecks
	 */
	public Dreieck (Punkt p1, Punkt p2, Punkt p3) {
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
	 * 		Alle X-Koordinaten als Feld
	 * @param y
	 * 		Alle Y-Koordinaten als Feld
	 */
	public Dreieck (float[] x, float[] y) {
		super(0, 0);
		if (x.length == 3 && y.length == 3) {
			this.x = x;
			this.y = y;
		} else {
			System.out.println("Läuft nicht, falsche Arraylengen bei Dreiecksbildung!");
		}
	}

	/**
	 * @return Die Farbe dieses Dreiecks
	 */
	public java.awt.Color getColor () {
		return farbe;
	}

	/**
	 * Setzt die Farbe ueber die JAVA-Farbklasse.
	 *
	 * @param c
	 * 		Die Farbe dieses Dreiecks, anhand der Klasse <code>Color</code>.
	 */
	public void setColor (Color c) {
		farbe = c;
	}

	/**
	 * Setzt die drei Punkte dieses Dreiecks neu.
	 *
	 * @param p1
	 * 		Der 1. neue Punkt des Dreiecks
	 * @param p2
	 * 		Der 2. neue Punkt des Dreiecks
	 * @param p3
	 * 		Der 3. neue Punkt des Dreiecks
	 *
	 * @see #punkteSetzen(float[], float[])
	 */
	public void punkteSetzen (Punkt p1, Punkt p2, Punkt p3) {
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
	 * 		Die Koordinaten aller X-Punkte. Der Index gibt den Punkt an (x[0] und y[0] bilden einen
	 * 		Punkt)
	 * @param y
	 * 		Die Koordinaten aller Y-Punkte. Der Index gibt den Punkt an (x[0] und y[0] bilden einen
	 * 		Punkt)
	 */
	public void punkteSetzen (float[] x, float[] y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Methode zum Verschieben
	 *
	 * @param v
	 * 		Die Verschiebung als Vektor
	 *
	 * @see Raum#verschieben(Vektor)
	 */
	@Override
	public void verschieben (Vektor v) {
		for (int i = 0; i < 3; i++) {
			x[i] += v.x;
			y[i] += v.y;
		}
	}

	/**
	 * Zeichnet das Objekt.
	 *
	 * @param g
	 * 		Das zutaendige Graphics-Objekt
	 * @param r
	 * 		Das BoundingRechteck, das das Kamerabild beschreibt.
	 */
	@Override
	public void zeichnen (Graphics2D g, BoundingRechteck r) {
		super.beforeRender(g, r);

		if (!r.schneidetBasic(this.dimension())) {
			return;
		}

		int[] x = {
				(int) this.x[0],
				(int) this.x[1],
				(int) this.x[2]
		};

		int[] y = {
				(int) this.y[0],
				(int) this.y[1],
				(int) this.y[2]
		};

		for (int i = 0; i < 3; i++) {
			x[i] -= r.x;
			y[i] -= r.y;
		}

		g.setColor(farbe);
		g.fillPolygon(x, y, 3);

		super.afterRender(g, r);
	}

	/**
	 * Die implementierte dimension()-Methode.
	 *
	 * @return Das BoundingRechteck, das das Dreieck exakt umschreibt.
	 */
	@Override
	public BoundingRechteck dimension () {
		float kleinstesX = x[0];
		float groesstesX = x[0];
		float kleinstesY = y[0];
		float groesstesY = y[0];

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
	 * {@inheritDoc} Collider wird direkt aus dem das <code>Raum</code>-Objekt umfassenden
	 * <code>BoundingRechteck</code> erzeugt, dass über die <code>dimension()</code>-Methode
	 * berechnet wird.
	 */
	@Override
	public Collider erzeugeCollider () {
		return erzeugeLazyCollider();
	}

	public Dreieck[] neuBerechnen () {
		Dreieck[] e = {this};
		return e;
	}

	/**
	 * Gibt an, ob diese Dreieck sich mit einem anderen schneidet.<br /> Dem Test zugrunde liegt
	 * folgene Mathematische Schlussfolgerung als Bedingung fuer das schneiden:<br/ > <b> 2 Dreiecke
	 * schneiden sich,<br /> ->sobald mindestens ein Punkt des einen Dreiecks innerhalb des anderen
	 * liegt.</b><br /> Dies ist die Grundlegende Testeinheit fuer alle geometrischen Formen Formen
	 * der Engine.
	 *
	 * @return <code>true</code>, wenn sich die beiden Dreiecke theoretisch schneiden wuerden, sonst
	 * <code>false</code>.
	 */
	public boolean schneidetBasic (Dreieck d) {
		return false;
	}

	public boolean schneidetBasic (BoundingRechteck r) {
		return r.schneidet(this);
	}

	/**
	 * @return Ein Punkt-Array der Groesse 3, das die drei das Dreieck beschreibenden Punkte
	 * enthaelt.
	 */
	public Punkt[] punkte () {
		Punkt[] ret = new Punkt[3];
		for (int i = 0; i < 3; i++) {
			ret[i] = new Punkt(x[i], y[i]);
		}
		return ret;
	}
}
