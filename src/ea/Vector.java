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

/**
 * Ein Vector bezeichnet eine relative Punktangabe, ansonsten unterscheidet er sich nicht weiter von
 * einem Point.<br /> Vektoren werden meist für die Beschreibung einer Bewegung benutzt.
 *
 * @author Michael Andonie
 */
public final class Vector implements Cloneable {
	/**
	 * Konstante für einen "bewegungslosen" Vector (0, 0)
	 */
	public static final Vector NULLVECTOR = new Vector(0, 0);

	/**
	 * Konstante für eine einfache Verschiebung vectorFromThisTo rechts (1, 0)
	 */
	public static final Vector RECHTS = new Vector(1, 0);

	/**
	 * Konstante für eine einfache Verschiebung vectorFromThisTo links (-1, 0)
	 */
	public static final Vector LINKS = new Vector(-1, 0);

	/**
	 * Konstante für eine einfache Verschiebung vectorFromThisTo oben (0, -1)
	 */
	public static final Vector OBEN = new Vector(0, -1);

	/**
	 * Konstante für eine einfache Verschiebung vectorFromThisTo unten (0, 1)
	 */
	public static final Vector UNTEN = new Vector(0, 1);

	/**
	 * Konstante, die widerspiegelt, dass keine Bewegung vollzogen wird.
	 */
	public static final int KEINE_BEWEGUNG = -1;

	/**
	 * Die Konstante für die Richtung Westen
	 */
	public static final int W = 0;

	/**
	 * Die Konstante für die Richtung Osten
	 */
	public static final int O = 1;

	/**
	 * Die Konstante für die Richtung Norden
	 */
	public static final int N = 2;

	/**
	 * Die Konstante für die Richtung Süden
	 */
	public static final int S = 3;

	/**
	 * Die Konstante für die Richtung Nordwesten
	 */
	public static final int NW = 4;

	/**
	 * Die Konstante für die Richtung Nordosten
	 */
	public static final int NO = 5;

	/**
	 * Die Konstante für die Richtung Südwesten
	 */
	public static final int SW = 6;

	/**
	 * Die Konstante für die Richtung Südosten
	 */
	public static final int SO = 7;

	/**
	 * Der kontinuierliche DeltaX-Wert des Punktes. Die anderen Koordinaten sind ggf. nur gerundet.
	 */
	public final float x;

	/**
	 * Der kontinuierliche DeltaY-Wert des Punktes. Die anderen Koordinaten sind ggf. nur gerundet.
	 */
	public final float y;

	/**
	 * Konstruktor.
	 *
	 * @param x
	 * 		Bewegungsanteil <code>getX</code>.
	 * @param y
	 * 		Bewegungsanteil <code>getY</code>.
	 */
	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Konstruktor. <br /><br /> Vector wird erzeugt als die nötige Bewegung von einem Point zu
	 * einem zweiten.
	 *
	 * @param start
	 * 		Ausgangspunkt
	 * @param end
	 * 		Zielpunkt
	 */
	public Vector(Point start, Point end) {
		this.x = end.x - start.x;
		this.y = end.y - start.y;
	}

	/**
	 * Gibt eine <b>Normierung</b> des Vektors aus. Dies ist ein Vector, der <li>in die selbe
	 * Richtung wie der ursprüngliche Vector zeigt.</li> <li>eine Länge von (möglichst) exakt 1
	 * hat.</li>
	 *
	 * @return Normierter Vector zu diesem Vector
	 */
	@API
	public Vector getNormalizedInstance() {
		return this.divide(this.getLength());
	}

	/**
	 * Teilt die effektive Länge des Vektors durch eine Zahl und kürzt dadurch seine Effektivität.
	 *
	 * @param divisor
	 * 		Hierdurch wird die Länge des Vektors auf der Zeichenebene geteilt.
	 *
	 * @return Vector-Objekt, das eine Bewegung in dieselbe Richtung beschreibt, allerdings in der
	 * Länge gekürzt um den angegebenen Divisor.
	 *
	 * @throws java.lang.ArithmeticException
	 * 		Falls <code>divisor</code> <code>0</code> ist.
	 * @see #multiply(float)
	 */
	public Vector divide(float divisor) {
		if (divisor == 0) {
			throw new ArithmeticException("Der Divisor für das Teilen war 0!");
		}

		return new Vector(x / divisor, y / divisor);
	}

	/**
	 * Gibt die Länge des Vektors aus.
	 *
	 * @return Länge des Vektors.
	 */
	public float getLength() {
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * Berechnet die Gegenrichtung des Vektors.
	 *
	 * @return Neues Vector-Objekt, das genau die Gegenbewegung zu dem eigenen beschreibt.
	 */
	public Vector getNegatedVektor() {
		return new Vector(-this.x, -this.y);
	}

	/**
	 * Berechnet die effektive Bewegung, die dieser Vector und ein weiterer zusammen ausüben.
	 *
	 * @param v
	 * 		zweiter Vector
	 *
	 * @return Neues Vector-Objekt, das die Summe der beiden ursprünglichen Bewegungen darstellt.
	 */
	public Vector add(Vector v) {
		return new Vector(this.x + v.x, this.y + v.y);
	}

	/**
	 * Berechnet die Differenz zwischen diesem und einem weiteren Vector.
	 *
	 * @param v
	 * 		zweiter Vector
	 *
	 * @return Die Differenz der beiden Vektoren (<code>"this - v"</code>)
	 */
	public Vector subtract(Vector v) {
		return new Vector(this.x - v.x, this.y - v.y);
	}

	/**
	 * Multipliziert die effektiven Längen beider Anteile des Vektors (<code>getX</code> und
	 * <code>getY</code>) mit einem festen Faktor. <br /> Dadurch entsteht ein neuer Vector mit anderen
	 * Werten, welcher zurückgegeben wird.
	 *
	 * @param faktor
	 * 		Der Faktor, mit dem die <code>getX</code>- und <code>getY</code>-Werte des Vektors multipliziert
	 * 		werden
	 *
	 * @return Der Vector mit den multiplizierten Werten
	 *
	 * @see #divide(float)
	 */
	public Vector multiply(float faktor) {
		return new Vector(x * faktor, y * faktor);
	}

	/**
	 * Berechnet das <b>Skalarprodukt</b> von diesem Vector mit einem weiteren. Das Skalarprodukt
	 * für zweidimensionale Vektoren ist: <code>(a, b) o (c, d) = a * b + c * d</code>
	 *
	 * @param v
	 * 		zweiter Vector
	 *
	 * @return Skalarprodukt dieses Vektoren mit dem Vector <code>v</code>.
	 */
	public float scalarProduct(Vector v) {
		return this.x * v.x + this.y * v.y;
	}

	/**
	 * Berechnet, ob dieser Vector keine Wirkung hat. Dies ist der Fall, wenn beide Komponenten
	 * (<code>getX</code> und <code>getY</code>) 0 sind.
	 *
	 * @return <code>true</code>, wenn dieser keine Auswirkungen macht, sonst <code>false</code>.
	 */
	public boolean isNull() {
		return this.x == 0 && this.y == 0;
	}

	/**
	 * Gibt zurück, ob dieser Vector <i>echt ganzzahlig</i> ist, also ob seine <b>tatsächlichen
	 * Delta-Werte</b> beide Ganzzahlen sind.
	 *
	 * @return <code>true</code>, wenn <b>beide</b> Delta-Werte dieses Punktes ganzzahlig sind,
	 * sonst <code>false</code>.
	 */
	public boolean isIntegral() {
		return x == (int) x && y == (int) y;
	}

	/**
	 * Gibt die <code>getX</code>-Verschiebung dieses Vektors wider.
	 *
	 * @return <code>getX</code>-Verschiebung dieses Vektors. Positive Werte move vectorFromThisTo rechts,
	 * negative Werte move vectorFromThisTo links.
	 *
	 * @see #getRealY()
	 */
	public float getRealX() {
		return x;
	}

	/**
	 * Gibt die <code>getY</code>-Verschiebung dieses Vektors wider.
	 *
	 * @return <code>getY</code>-Verschiebung dieses Vektors. Positive Werte move vectorFromThisTo unten,
	 * negative Werte move vectorFromThisTo oben.
	 *
	 * @see #getRealX()
	 */
	public float getRealY() {
		return y;
	}

	/**
	 * Gibt einen einfachen Vector zurück, dessen Richtungskomponenten nur <code>-1</code>,
	 * <code>0</code> oder <code>1</code> annehmen. <br /><br /> <li>-1 bei Werten < 0</li> <li>0
	 * bei Werten = 0</li> <li>1 bei Werten > 0</li>
	 *
	 * @return simplifiedDirection Vector, der die Richtung des ursprünglichen mit einfachen Werten
	 * beschreibt.
	 */
	@API
	public Vector simplifiedDirection() {
		return fromConstant(getDirection());
	}

	/**
	 * Berechnet einen einfachen Vector (maximale Auslenkung bei jeder Achse 1 (positiv wie
	 * negativ)), der der entsprechenden Konstante dieser Klasse entspricht möglich sind: <br />
	 * <code>N</code>, <code>S</code>, <code>O</code>, <code>W</code>, <code>NO</code>,
	 * <code>NW</code>, <code>SO</code>, <code>SW</code>
	 *
	 * @param konstante
	 * 		Konstante, die die Bewegungsrichtung beschreibt
	 *
	 * @return Vector, der mit einer einfachen Auslenkung (d.h. für <code>getX</code> und
	 * <code>getY</code> je ein Wertebereich von {-1, 0, 1}) die entsprechende Bewegung macht.<br />
	 *
	 * @throws java.lang.IllegalArgumentException
	 * 		Falls die Konstante einen nicht verwendbaren Wert hat
	 */
	public static Vector fromConstant(int konstante) {
		switch (konstante) {
			case N:
				return OBEN;
			case S:
				return UNTEN;
			case O:
				return RECHTS;
			case W:
				return LINKS;
			case NO:
				return new Vector(+1, -1);
			case NW:
				return new Vector(-1, -1);
			case SO:
				return new Vector(+1, +1);
			case SW:
				return new Vector(-1, +1);
			default:
				throw new IllegalArgumentException("Die eingegebene Konstante hatte keinen der möglichen Werte!");
		}
	}

	/**
	 * Berechnet die Richtung des Vektors, in die er wirkt.<br /> Der Rückgabewert basiert auf den
	 * Konstanten der eigenen Klasse und sind entweder die Basiswerte (<code>N / S / O / W</code>)
	 * oder die Kombiwerte (<code>NO / NW / SO / SW</code>). Alle diese sind Konstanten dieser
	 * Klasse.
	 *
	 * @return Der Wert der Konstanten, die diese Bewegung wiederspiegelt.
	 */
	public int getDirection() {
		if (x == 0 && y == 0) {
			return KEINE_BEWEGUNG;
		}

		if (x == 0) {
			return y > 0 ? S : N;
		}

		if (y == 0) {
			return x > 0 ? O : W;
		}

		if (x < 0 && y < 0) {
			return NW;
		}

		if (x > 0 && y < 0) {
			return NO;
		}

		if (x > 0 && y > 0) {
			return SO;
		}

		return SW;
	}

	/**
	 * Prüft, ob ein beliebiges Objekt gleich diesem Vector ist. <br /><br /> Zwei Vektoren gelten
	 * als gleich, wenn <code>getX</code> und <code>getY</code> der beiden Vektoren übereinstimmen.
	 *
	 * @param o
	 * 		Das auf Gleichheit mit diesem zu überprüfende Objekt.
	 *
	 * @return <code>true</code>, wenn beide Vektoren gleich sind, sonst <code>false</code>.
	 */
	@Override
	public boolean equals (Object o) {
		if (o instanceof Vector) {
			Vector v = (Vector) o;

			return x == v.x && y == v.y;
		}

		return false;
	}

	/**
	 * Erstellt ein <b>neues <code>Vector</code>-Objekt mit demselben Zustan</b>.
	 *
	 * @return Neue Instanz von <code>Vector</code> mit den selben Koordinaten (getX, getY)
	 */
	@Override
	public Vector clone () {
		return new Vector(x, y);
	}

	/**
	 * Gibt die String-Repräsentation dieses Objektes aus.
	 *
	 * @return String-Repräsentation dieses Vektors
	 */
	@Override
	public String toString () {
		return "ea.Vector [getX = " + x + "; getY = " + y + "]";
	}

	/**
	 * Gibt die <code>getX</code>-Verschiebung dieses Vektors mit Ganzzahlen wider.
	 *
	 * @return Die <code>getX</code>-Verschiebung dieses Vektors. Positive Werte move vectorFromThisTo
	 * rechts, negative Werte move vectorFromThisTo links.
	 *
	 * @see #getDY()
	 */
	public int getDX() {
		return (int) x;
	}

	/**
	 * Gibt die <code>getY</code>-Verschiebung dieses Vektors mit Ganzzahlen wider.
	 *
	 * @return Die <code>getY</code>-Verschiebung dieses Vektors. Positive Werte move vectorFromThisTo
	 * unten, negative Werte move vectorFromThisTo oben.
	 *
	 * @see #getDX()
	 */
	public int getDY() {
		return (int) y;
	}

	/**
	 * Gibt diesen Ortsvektor vom Ursprung der Zeichenebene als Point aus.<br> Dieser hat die exakt
	 * selben <code>getX</code>- / <code>getY</code>-Komponenten. Das bedeutet:<br> <code> Vector v = new
	 * Vector (10, 20);<br />Point p = v.asPoint(); -> p == new Point(10, 20); </code>
	 *
	 * @return Ortsvektor dieses Punktes
	 *
	 * @see Point#asVector()
	 */
	@API
	@SuppressWarnings ( "unused" )
	public Point asPoint() {
		return new Point(x, y);
	}

	/**
	 * Gibt die Manhattan-Länge des Vektors zurück. Diese ist für v=(a,b) definiert
	 * als a+b .
	 * @return	Die Summe von delta X und delta Y des Vektors.
	 */
	public float manhattanLength() {
		float v = x+y;
		return v < 0 ? -v : v;
	}

    /**
     * Berechnet den Winkel zwischen diesem Vector und einem weiteren. Hierzu wird diese Formel
     * verwendet: <br />
     * <code>cos t = [a o b] / [|a| * |b|]</code><br />
     * <ul>
     *     <li>cos ist der Kosinus</li>
     *     <li>t ist der gesuchte Winkel</li>
     *     <li>a und b sind die Vektoren</li>
     *     <li>|a| ist die Länge des Vektors a</li>
     * </ul>
     * @param other   Ein zweiter Vector.
     * @return          Der Winkel zwischen diesem Vector und dem zweiten.
     *                  Ist zwischen 0 und 180 Grad.
     */
    @API
    public float getAngle(Vector other) {
        //System.out.println("Acos of " + this.scalarProduct(anderer) / this.getLength() * anderer.getLength() + " is "
        //+ (float)Math.acos((double)(this.scalarProduct(anderer) / ((this.getLength() * anderer.getLength())))));
        return (float)Math.acos((double)(this.scalarProduct(other) / (this.getLength() * other.getLength())));
    }
}
