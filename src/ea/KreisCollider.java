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

package ea;

/**
 * Alternative zum <code>BoundingRechteck</code>. Ein Kreis mit Mittelpunkt und Radius
 * für Kollisionsdetektion.
 * @author Michael Andonie
 *
 */
public class KreisCollider {
	/**
	 * Das Zentrum des Kreises - X-Koordinate
	 */
	private float x;
	
	/**
	 * Das Zentrum des Kreises - Y-Koordinate
	 */
	private float y;
	
	/**
	 * Der Radius des Kreises
	 */
	private final float radius;
	
	/**
	 * Erstellt einen neuen BoundingKreis.
	 * @param zentrum	Das Zentrum des Kreises.
	 * @param radius	Der Radius des Kreises.
	 */
	public KreisCollider(Punkt zentrum, float radius) {
		this.x = zentrum.x;
		this.y = zentrum.y;
		this.radius = radius;
	}
	
	/**
	 * Verschiebt den Collider um einen Vektor.
	 * @param v	Der Vektor, um den der Collider verschoben werden soll.
	 */
	public void verschieben(Vektor v) {
		this.x += v.x;
		this.y += v.y;
	}
	
	/**
	 * Effizienter, genaue Methode, die überprüft, ob dieser Bounding-Kreis sich mit
	 * einem zweiten <i>schneidet oder berührt</i>.
	 * @param k2	Ein zweiter Kreis.
	 * @return		<code>true</code>, wenn sich dieser Kreis mit </code>k2</code> <i>schneidet
	 * 				oder berührt</i>. Sonst <code>false</code>.
	 */
	public boolean schneidet(KreisCollider k2) {
		if(k2 == null) {
			return false;
		}

		return quadrieren(x - k2.x) + quadrieren(y - k2.y) <= quadrieren(radius + k2.radius);
	}
	
	/**
	 * Hilfstmethode zum möglichst effizienten Quadrieren einer Zahl.
	 * Wird benutzt, um die doppelte Laengenberechnung zu umgehen.
	 * @param x	eine Zahl.
	 * @return	x*x
	 */
	public static float quadrieren(float x) {
		return x*x;
	}

	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	/**
	 * @return the radius
	 */
	public float getRadius() {
		return radius;
	}
	
	
}
