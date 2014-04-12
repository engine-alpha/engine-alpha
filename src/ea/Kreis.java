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

import ea.internal.collision.Collider;
import ea.internal.collision.SphereCollider;

/**
 * Ein Kreis ist ein regelmaessiges n-Eck, dessen Eckenzahl gegen unendlich geht.<br />
 * Dies ist mit einem Computer nicht moeglich, daher wird fuer einen Kreis eine ausrechend grosse Anzahl
 * an Ecken gewaehlt. Diese ist ueber die Genauigkeit im Konstruktor mitzugeben oder im vereinfachten konstruktor
 * bereits voreingestellt.
 * 
 * @author Michael Andonie
 */
@SuppressWarnings("serial")
public class Kreis extends RegEck {
	
	/**
	 * Konstruktor fuer Objekte der Klasse Kreis
	 * 
	 * @param x
	 *            Die X-Koordinate der Linken oberen Ecke des den Kreis umschreibenden Rechtecks, <b>nicht die des MIttelpunktes</b>
	 * @param y
	 *            Die Y-Koordinate der Linken oberen Ecke des den Kreis umschreibenden Rechtecks, <b>nicht die des MIttelpunktes</b>
	 * @param durchmesser
	 *            Der Durchmesser des Kreises
	 * @param genauigkeit
	 *            Die Genauigkeitsstufe des Kreises.<br />
	 *            <b>je hoeher sie ist, desto besser sieht der KReis aus, jedoch auch desto hoeher ist die Computerbelastung</b>
	 */
	@Deprecated
	public Kreis(int x, int y, float durchmesser, int genauigkeit) {
		super(x, y, (int) Math.pow(genauigkeit, 2), durchmesser);
	}
	
	/**
	 * Alternativkonstruktor mit vorgefertigter Genauigkeit
	 * 
	 * @param x
	 *            Die X-Koordinate der Linken oberen Ecke des den Kreis umschreibenden Rechtecks, <b>nicht die des Mittelpunktes</b>
	 * @param y
	 *            Die Y-Koordinate der Linken oberen Ecke des den Kreis umschreibenden Rechtecks, <b>nicht die des Mittelpunktes</b>
	 * @param durchmesser
	 *            Der Durchmesser des Kreises
	 */
	public Kreis(float x, float y, float durchmesser) {
		super(x, y, 6, durchmesser);
	}
	
	/**
	 * Gibt den Radius des Kreises aus
	 * 
	 * @return Der Radius des Kreises
	 */
	public float radius() {
		return radius;
	}
	
	@Override
	public void zeichnen(Graphics2D g, BoundingRechteck r) {
		// Kreis muss nicht gedreht werden,
		// aber es könnten hier in Zukunft noch andere wichtige Funktionen aufgerunfen werden
		super.beforeRender(g);
		
		if (!r.schneidetBasic(this.dimension())) {
			return;
		}
		
		g.setColor(this.formen()[0].getColor());
		g.fillOval((int) (position.x - r.x), (int) (position.y - r.y), (int) (2 * radius), (int) (2 * radius));
		
		super.afterRender(g);
	}
	
	@Override
	public Collider erzeugeCollider() {
		return new SphereCollider(radius*2);
	}
}
