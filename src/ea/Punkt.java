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

import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import org.jbox2d.common.Vec2;

/**
 * Ein Punkt beschreibt einen exakt bestimmten eindimensionalen Punkt auf der Zeichenebene.<br /> Er
 * ist durch 2 Koordinaten exakt bestimmt.
 *
 * @author Michael Andonie
 */
public final class Punkt {
	/**
	 * Konstante für den Punkt mit den reellen Koordinaten (0|0)
	 */
	public static final Punkt ZENTRUM = new Punkt(0f, 0f);

	/**
	 * Der kontinuierliche(re) X-Wert des Punktes. Die anderen Koordinaten sind ggf. nur gerundet.
	 */
	public final float x;

	/**
	 * Der kontinuierliche(re) Y-Wert des Punktes. Die anderen Koordinaten sind ggf. nur gerundet.
	 */
	public final float y;

	/**
	 * Konstruktor.
	 *
	 * @param x
	 * 		<code>x</code>-Wert
	 * @param y
	 * 		<code>y</code>-Wert
	 */
	public Punkt (float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Beschreibt den Abstand zwischen diesem und einem anderen Punkt in der Luftlinie.<br />
	 * Hierbei wird lediglich der Satz des Pythagoras angewendet (<code>a² + b² = c²</code>).
	 *
	 * @param p
	 * 		Punkt, zu dem der Abstand bestimmt werden soll.
	 *
	 * @return Die Länge der Luftlinie zwischen diesem und dem anderen Punkt. (Abstände sind nie
	 * negativ!)
	 */
	public float abstand (Punkt p) {
		double x = this.x - p.x, y = this.y - p.y;
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * Gibt einen Punkt aus, der die um eine Verschiebung veraenderten Koordinaten dieses Punktes
	 * hat.<br /> Also quasi diesen Punkt, waere er um eine Verschiebeung veraendert.<br /> <br />
	 * Diese Methode ist identisch mit <code>verschobenerPunkt(Vektor)</code>. Sie existiert der
	 * einheitlichen Methodennomenklatur der Zeichenebenen-Klassen halber.
	 *
	 * @param v
	 * 		Der Vektor, der diese Verschiebung beschreibt.
	 *
	 * @return Der Punkt, der die Koordinaten dieses Punktes - verschoben um den Vektor - hat.
	 *
	 * @see #verschobenerPunkt(Vektor)
	 */
	public Punkt verschobeneInstanz (Vektor v) {
		return verschobenerPunkt(v);
	}

	/**
	 * Gibt einen Punkt aus, der um eine bestimmte Verschiebung verschobenen Koordinaten dieses
	 * Punktes hat.
	 *
	 * @param v
	 * 		Die Verschiebung, die dieser Punkt erhalten würde, um mit der Ausgabe übereinzustimmen.
	 *
	 * @return Ein Punkt, mit der X-Koordinate <code>p.x + v.x</code> und der Y-Koordinate <code>p.y
	 * + v.y</code>. (tatsächlich werden die <b>reellen</b> Werte addiert.
	 *
	 * @see #verschobeneInstanz(Vektor)
	 */
	public Punkt verschobenerPunkt (Vektor v) {
		return new Punkt(this.x + v.x, this.y + v.y);
	}

	/**
	 * Gibt diesen Punkt als Ortsvektor vom Ursprung der Zeichenebene aus.<br> Dieser hat die exakt
	 * selben X- / Y-Komponenten. Das bedeutet:<br> <code> Punkt p = new Punkt(10, 20); Vektor v =
	 * p.alsVektor(); -> v == new Vektor(10, 20); </code>
	 *
	 * @return Ortsvektor dieses Punktes
	 *
	 * @see Vektor#alsPunkt()
	 */
	@API
	public Vektor alsVektor () {
		return new Vektor(x, y);
	}

	/**
	 * {@inheritDoc} Überschriebene Equals-Methode. Zwei Punkte sind gleich, wenn sie <b>Exakt</b>
	 * aufeinanderliegen. Daher müssen die <b>reellen</b> Koordinaten übereinstimmen.
	 */
	@Override
	public boolean equals (Object o) {
		if (o instanceof Punkt) {
			Punkt p = (Punkt) o;
			return this.x == p.x && this.y == p.y;
		}

		return false;
	}

	/**
	 * Überschriebene <code>toString</code>-Methode gibt eine sinnvolle, lesbare
	 * String-Repräsentation dieses Punktes der Form "(x|y)" aus.
	 *
	 * @return String-Repräsentation dieses Punktes der Form: <code>Punkt: (x|y)</code>
	 */
	@Override
	public String toString () {
		return "Punkt: (" + x + "|" + y + ")";
	}

	/**
	 * Berechnet den Vektor, der <b>von diesem Punkt zu einem anderen</b> führt.
	 *
	 * @param p2
	 * 		Ein weiterer Punkt.
	 *
	 * @return Ein Vektor, der <b>von diesem</b> Punkt <b>nach <code>p2</code></b> zeigt.
	 */
	public Vektor nach (Punkt p2) {
		return new Vektor(p2.x - this.x, p2.y - this.y);
	}

	/**
	 * Gibt zurück, ob dieser Punkt <i>echt ganzzahlig</i> ist, also ob seine <b>tatsächlichen
	 * Koordinaten</b> beide Ganzzahlen sind.
	 *
	 * @return <code>true</code>, wenn <b>beide</b> Koordinaten dieses Punktes ganzzahlig sind,
	 * sonst <code>false</code>.
	 */
	public boolean istEchtGanzzahlig () {
		return x == (int) x && y == (int) y;
	}

	/**
	 * Gibt die <code>x</code>-Koordinate dieses Punktes zurück.
	 *
	 * @return Die <code>x</code>-Koordinate dieses Punktes.
	 *
	 * @see #realY()
	 * @see #x()
	 * @see #y()
	 */
	public float realX () {
		return x;
	}

	/**
	 * Gibt die <code>y</code>-Koordinate dieses Punktes zurück.
	 *
	 * @return Die <code>y</code>-Koordinate dieses Punktes.
	 *
	 * @see #realX()
	 * @see #x()
	 * @see #y()
	 */
	public float realY () {
		return y;
	}

	/**
	 * Gibt die <code>x</code>-Koordinate dieses Punktes als Integer zurück.
	 *
	 * @return Die <code>x</code>-Koordinate dieses Punktes.
	 *
	 * @see #y()
	 * @see #realX()
	 * @see #realY()
	 */
	public int x () {
		return (int) x;
	}

	/**
	 * Gibt die <code>y</code>-Koordinate dieses Punktes als Integer zurück.
	 *
	 * @return Die <code>y</code>-Koordinate dieses Punktes.
	 *
	 * @see #x()
	 * @see #realX()
	 * @see #realY()
	 */
	public int y () {
		return (int) y;
	}

	/**
	 * Konvertiert diesen Punkt zu einer Vec2-Instanz mit denselben X/Y Werten.
	 * Wird intern verwendet, um mit der JBox2D-Physics-Engine zu kommunizieren.
	 * @return	Ein Vec2-Vektor mit denselben X und Y Werten.
	 */
	@NoExternalUse
    public Vec2 toVec2() {
		return new Vec2(x,y);
    }
}
