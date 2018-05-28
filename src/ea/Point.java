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
 * Ein Point beschreibt einen exakt bestimmten eindimensionalen Point auf der Zeichenebene.<br /> Er
 * ist durch 2 Koordinaten exakt bestimmt.
 *
 * @author Michael Andonie
 */
public final class Point {
	/**
	 * Konstante für den Point mit den reellen Koordinaten (0|0)
	 */
	public static final Point CENTRE = new Point(0f, 0f);

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
	 * 		<code>getX</code>-Wert
	 * @param y
	 * 		<code>getY</code>-Wert
	 */
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Beschreibt den Abstand zwischen diesem und einem anderen Point in der Luftlinie.<br />
	 * Hierbei wird lediglich der Satz des Pythagoras angewendet (<code>a² + b² = c²</code>).
	 *
	 * @param p
	 * 		Point, zu dem der Abstand bestimmt werden soll.
	 *
	 * @return Die Länge der Luftlinie zwischen diesem und dem anderen Point. (Abstände sind nie
	 * negativ!)
	 */
	@API
	public float distanceTo(Point p) {
		double x = this.x - p.x, y = this.y - p.y;
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * Gibt einen Point aus, der die um eine Verschiebung veraenderten Koordinaten dieses Punktes
	 * hat.<br /> Also quasi diesen Point, waere er um eine Verschiebeung veraendert.<br /> <br />
	 * Diese Methode ist identisch mit <code>moveInstanceBy(Vector)</code>. Sie existiert der
	 * einheitlichen Methodennomenklatur der Zeichenebenen-Klassen halber.
	 *
	 * @param v
	 * 		Der Vector, der diese Verschiebung beschreibt.
	 *
	 * @return Der Point, der die Koordinaten dieses Punktes - verschoben um den Vector - hat.
	 *
	 * @see #moveInstanceBy(Vector)
	 */
	public Point verschobeneInstanz (Vector v) {
		return moveInstanceBy(v);
	}

	/**
	 * Gibt einen Point aus, der um eine bestimmte Verschiebung verschobenen Koordinaten dieses
	 * Punktes hat.
	 *
	 * @param v
	 * 		Die Verschiebung, die dieser Point erhalten würde, um mit der Ausgabe übereinzustimmen.
	 *
	 * @return Ein Point, mit der X-Koordinate <code>p.getX + v.getX</code> und der Y-Koordinate <code>p.getY
	 * + v.getY</code>. (tatsächlich werden die <b>reellen</b> Werte addiert.
	 *
	 * @see #verschobeneInstanz(Vector)
	 */
	@API
	public Point moveInstanceBy(Vector v) {
		return new Point(this.x + v.x, this.y + v.y);
	}

	/**
	 * Gibt diesen Point als Ortsvektor vom Ursprung der Zeichenebene aus.<br> Dieser hat die exakt
	 * selben X- / Y-Komponenten. Das bedeutet:<br> <code> Point p = new Point(10, 20); Vector v =
	 * p.asVector(); -> v == new Vector(10, 20); </code>
	 *
	 * @return Ortsvektor dieses Punktes
	 *
	 * @see Vector#asPoint()
	 */
	@API
	public Vector asVector() {
		return new Vector(x, y);
	}

	/**
	 * {@inheritDoc} Überschriebene Equals-Methode. Zwei Punkte sind gleich, wenn sie <b>Exakt</b>
	 * aufeinanderliegen. Daher müssen die <b>reellen</b> Koordinaten übereinstimmen.
	 */
	@Override
	public boolean equals (Object o) {
		if (o instanceof Point) {
			Point p = (Point) o;
			return this.x == p.x && this.y == p.y;
		}

		return false;
	}

	/**
	 * Überschriebene <code>toString</code>-Methode gibt eine sinnvolle, lesbare
	 * String-Repräsentation dieses Punktes der Form "(getX|getY)" aus.
	 *
	 * @return String-Repräsentation dieses Punktes der Form: <code>Point: (getX|getY)</code>
	 */
	@Override
	public String toString () {
		return "Point: (" + x + "|" + y + ")";
	}

	/**
	 * Berechnet den Vector, der <b>von diesem Point zu einem anderen</b> führt.
	 *
	 * @param p2
	 * 		Ein weiterer Point.
	 *
	 * @return Ein Vector, der <b>von diesem</b> Point <b>vectorFromThisTo <code>p2</code></b> zeigt.
	 */
	public Vector vectorFromThisTo(Point p2) {
		return new Vector(p2.x - this.x, p2.y - this.y);
	}

	/**
	 * Gibt die <code>getX</code>-Koordinate dieses Punktes zurück.
	 *
	 * @return Die <code>getX</code>-Koordinate dieses Punktes.
	 *
	 * @see #getRealY()
	 * @see #getX()
	 * @see #getY()
	 */
	public float getRealX() {
		return x;
	}

	/**
	 * Gibt die <code>getY</code>-Koordinate dieses Punktes zurück.
	 *
	 * @return Die <code>getY</code>-Koordinate dieses Punktes.
	 *
	 * @see #getRealX()
	 * @see #getX()
	 * @see #getY()
	 */
	public float getRealY() {
		return y;
	}

	/**
	 * Gibt die <code>getX</code>-Koordinate dieses Punktes als Integer zurück.
	 *
	 * @return Die <code>getX</code>-Koordinate dieses Punktes.
	 *
	 * @see #getY()
	 * @see #getRealX()
	 * @see #getRealY()
	 */
	public int getX() {
		return (int) x;
	}

	/**
	 * Gibt die <code>getY</code>-Koordinate dieses Punktes als Integer zurück.
	 *
	 * @return Die <code>getY</code>-Koordinate dieses Punktes.
	 *
	 * @see #getX()
	 * @see #getRealX()
	 * @see #getRealY()
	 */
	public int getY() {
		return (int) y;
	}

	/**
	 * Konvertiert diesen Point zu einer Vec2-Instanz mit denselben X/Y Werten.
	 * Wird intern verwendet, um mit der JBox2D-Physics-Engine zu kommunizieren.
	 * @return	Ein Vec2-Vector mit denselben X und Y Werten.
	 */
	@NoExternalUse
    public Vec2 toVec2() {
		return new Vec2(x,y);
    }
}
