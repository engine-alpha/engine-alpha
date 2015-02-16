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

import java.awt.*;

/**
 * Beschreiben Sie hier die Klasse Rechteck.
 *
 * @author Michael Andonie
 */
@SuppressWarnings ( "serial" )
public class Rechteck extends Geometrie {
	/**
	 * Die Laenge
	 */
	private float laenge;

	/**
	 * Die Breite
	 */
	private float breite;

	/**
	 * Konstruktor fuer Objekte der Klasse Rechteck
	 *
	 * @param x
	 * 		Die X Position (Koordinate der linken oberen Ecke) des Rechtecks
	 * @param y
	 * 		Die X Position (Koordinate der linken oberen Ecke) des Rechtecks
	 * @param breite
	 * 		Die Breite des Rechtecks
	 * @param hoehe
	 * 		Die hoehe des Rechtecks
	 */
	public Rechteck (float x, float y, float breite, float hoehe) {
		super(x, y);
        masseSetzen(breite, hoehe);
	}

	/**
	 * Setzt beide Masse feur dieses Rechteck neu.
	 *
	 * @param breite
	 * 		Die neue Breite des Rechtecks
	 * @param hoehe
	 * 		Die neue Hoehe des Rechtecks
	 */
	public void masseSetzen (float breite, float hoehe) {
		breiteSetzen(breite);
        hoeheSetzen(hoehe);
	}

	/**
	 * Setzt die Breite fuer dieses Rechteck neu.
	 *
	 * @param breite
	 * 		Die neue Breite des Rechtecks
	 *
	 * @see #hoeheSetzen(float)
	 */
	public void breiteSetzen (float breite) {
        this.breite = breite;
	}

	/**
	 * Setzt die Hoehe fuer dieses Rechteck neu.
	 *
	 * @param hoehe
	 * 		Die neue Hoehe des Rechtecks
	 *
	 * @see #breiteSetzen(float)
	 */
	public void hoeheSetzen (float hoehe) {
		this.laenge = hoehe;
	}

	/**
	 * Zeichnet das Objekt.
	 *
	 * @param g
	 * 		Das zeichnende Graphics-Objekt
	 * @param r
	 * 		Das BoundingRechteck, dass die Kameraperspektive Repraesentiert.<br /> Hierbei soll
	 * 		zunaechst getestet werden, ob das Objekt innerhalb der Kamera liegt, und erst dann
	 * 		gezeichnet werden.
	 */
	@Override
	public void render(Graphics2D g, BoundingRechteck r) {



        g.setColor(getColor());
		g.fillRect((int) (position.x - r.x), (int) (position.y - r.y), (int) breite, (int) laenge);


	}
}
