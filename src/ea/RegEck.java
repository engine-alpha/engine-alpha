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

/**
 * Basisklasse fuer ein regelmaessiges n-Eck.<br /> Tatsaechlich ist aufgrund von moeglicherweise
 * Auftretenden Rundungsfehlern dies ein n+1 - Eck.<br /> Dies faellt jedoch nicht auf und ist nur
 * da um eventuell auftretende Abrundungsfehler wieder auszuduennen.
 *
 * @author Michael Andonie
 */
@SuppressWarnings ("serial")
public class RegEck extends Geometrie {
	/**
	 * Die Anzahl an Ecken.<br /> Es kann keine Form mit weniger als 3 Ecken geben!
	 */
	protected final int eckenzahl;

	/**
	 * Der Radius des Umkreises des n-Ecks
	 */
	protected float radius;

	/**
	 * Konstruktor fuer Objekte der Klasse N-Eck
	 *
	 * @param x
	 * 		Die X-Koordinate der Linken oberen Ecke des das n-Eck umschreibenden Rechtecks, <b>nicht
	 * 		die des Mittelpunktes</b>
	 * @param y
	 * 		Die Y-Koordinate der Linken oberen Ecke des das n-Eck umschreibenden Rechtecks, <b>nicht
	 * 		die des Mittelpunktes</b>
	 * @param ecken
	 * 		Die Anzahl der Ecken des Ecks
	 * @param durchmesser
	 * 		Der Durchmesser des Kreises, der das n-Eck umschreibt
	 */
	public RegEck (float x, float y, int ecken, float durchmesser) {
		super(x, y);
        this.eckenzahl = ecken;
        //FIXME
	}

	/**
	 * Setzt einen neuen Durchmesser fuer das regelmaessige n-Eck.
	 *
	 * @param durchmesser
	 * 		Der neue Durchmesser
	 */
	public void durchmesserSetzen (int durchmesser) {
		//FIXME
	}

	/**
	 * Setzt einen neuen Radius fuer das regelmaessige n-Eck.
	 *
	 * @param radius
	 * 		Der neue Radius.
	 */
	public void radiusSetzen (int radius) {
		//FIXME
	}
}
