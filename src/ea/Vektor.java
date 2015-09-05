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
 * Ein Vektor bezeichnet eine relative Punktangabe, ansonsten unterscheidet er sich nicht weiter von
 * einem Punkt.<br /> Vektoren werden meist für die Beschreibung einer Bewegung benutzt.
 *
 * @author Michael Andonie
 */
public final class Vektor implements Cloneable {
	/**
	 * Konstante für einen "bewegungslosen" Vektor (0, 0)
	 */
	public static final Vektor NULLVEKTOR = new Vektor(0, 0);

	/**
	 * Konstante für eine einfache Verschiebung nach rechts (1, 0)
	 */
	public static final Vektor RECHTS = new Vektor(1, 0);

	/**
	 * Konstante für eine einfache Verschiebung nach links (-1, 0)
	 */
	public static final Vektor LINKS = new Vektor(-1, 0);

	/**
	 * Konstante für eine einfache Verschiebung nach oben (0, -1)
	 */
	public static final Vektor OBEN = new Vektor(0, -1);

	/**
	 * Konstante für eine einfache Verschiebung nach unten (0, 1)
	 */
	public static final Vektor UNTEN = new Vektor(0, 1);

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
	 * 		Bewegungsanteil <code>x</code>.
	 * @param y
	 * 		Bewegungsanteil <code>y</code>.
	 */
	public Vektor (float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Konstruktor. <br /><br /> Vektor wird erzeugt als die nötige Bewegung von einem Punkt zu
	 * einem zweiten.
	 *
	 * @param start
	 * 		Ausgangspunkt
	 * @param ziel
	 * 		Zielpunkt
	 */
	public Vektor (Punkt start, Punkt ziel) {
		this.x = ziel.x - start.x;
		this.y = ziel.y - start.y;
	}

	/**
	 * Gibt eine <b>Normierung</b> des Vektors aus. Dies ist ein Vektor, der <li>in die selbe
	 * Richtung wie der ursprüngliche Vektor zeigt.</li> <li>eine Länge von (möglichst) exakt 1
	 * hat.</li>
	 *
	 * @return Normierter Vektor zu diesem Vektor
	 */
	public Vektor normiert () {
		return this.teilen(this.laenge());
	}

	/**
	 * Teilt die effektive Länge des Vektors durch eine Zahl und kürzt dadurch seine Effektivität.
	 *
	 * @param divisor
	 * 		Hierdurch wird die Länge des Vektors auf der Zeichenebene geteilt.
	 *
	 * @return Vektor-Objekt, das eine Bewegung in dieselbe Richtung beschreibt, allerdings in der
	 * Länge gekürzt um den angegebenen Divisor.
	 *
	 * @throws java.lang.ArithmeticException
	 * 		Falls <code>divisor</code> <code>0</code> ist.
	 * @see #multiplizieren(float)
	 */
	public Vektor teilen (float divisor) {
		if (divisor == 0) {
			throw new ArithmeticException("Der Divisor für das Teilen war 0!");
		}

		return new Vektor(x / divisor, y / divisor);
	}

	/**
	 * Gibt die Länge des Vektors aus.
	 *
	 * @return Länge des Vektors.
	 */
	public float laenge () {
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * Berechnet die Gegenrichtung des Vektors.
	 *
	 * @return Neues Vektor-Objekt, das genau die Gegenbewegung zu dem eigenen beschreibt.
	 */
	public Vektor gegenrichtung () {
		return new Vektor(-this.x, -this.y);
	}

	/**
	 * Berechnet die effektive Bewegung, die dieser Vektor und ein weiterer zusammen ausüben.
	 *
	 * @param v
	 * 		zweiter Vektor
	 *
	 * @return Neues Vektor-Objekt, das die Summe der beiden ursprünglichen Bewegungen darstellt.
	 */
	public Vektor summe (Vektor v) {
		return new Vektor(this.x + v.x, this.y + v.y);
	}

	/**
	 * Berechnet die Differenz zwischen diesem und einem weiteren Vektor.
	 *
	 * @param v
	 * 		zweiter Vektor
	 *
	 * @return Die Differenz der beiden Vektoren (<code>"this - v"</code>)
	 */
	public Vektor differenz (Vektor v) {
		return new Vektor(this.x - v.x, this.y - v.y);
	}

	/**
	 * Multipliziert die effektiven Längen beider Anteile des Vektors (<code>x</code> und
	 * <code>y</code>) mit einem festen Faktor. <br /> Dadurch entsteht ein neuer Vektor mit anderen
	 * Werten, welcher zurückgegeben wird.
	 *
	 * @param faktor
	 * 		Der Faktor, mit dem die <code>x</code>- und <code>y</code>-Werte des Vektors multipliziert
	 * 		werden
	 *
	 * @return Der Vektor mit den multiplizierten Werten
	 *
	 * @see #teilen(float)
	 */
	public Vektor multiplizieren (float faktor) {
		return new Vektor(x * faktor, y * faktor);
	}

	/**
	 * Berechnet das <b>Skalarprodukt</b> von diesem Vektor mit einem weiteren. Das Skalarprodukt
	 * für zweidimensionale Vektoren ist: <code>(a, b) o (c, d) = a * b + c * d</code>
	 *
	 * @param v
	 * 		zweiter Vektor
	 *
	 * @return Skalarprodukt dieses Vektoren mit dem Vektor <code>v</code>.
	 */
	public float skalarprodukt (Vektor v) {
		return this.x * v.x + this.y * v.y;
	}

	/**
	 * Berechnet, ob dieser Vektor keine Wirkung hat. Dies ist der Fall, wenn beide Komponenten
	 * (<code>x</code> und <code>y</code>) 0 sind.
	 *
	 * @return <code>true</code>, wenn dieser keine Auswirkungen macht, sonst <code>false</code>.
	 */
	public boolean unwirksam () {
		return this.x == 0 && this.y == 0;
	}

	/**
	 * Gibt zurück, ob dieser Vektor <i>echt ganzzahlig</i> ist, also ob seine <b>tatsächlichen
	 * Delta-Werte</b> beide Ganzzahlen sind.
	 *
	 * @return <code>true</code>, wenn <b>beide</b> Delta-Werte dieses Punktes ganzzahlig sind,
	 * sonst <code>false</code>.
	 */
	public boolean istEchtGanzzahlig () {
		return x == (int) x && y == (int) y;
	}

	/**
	 * Gibt die <code>x</code>-Verschiebung dieses Vektors wider.
	 *
	 * @return <code>x</code>-Verschiebung dieses Vektors. Positive Werte verschieben nach rechts,
	 * negative Werte verschieben nach links.
	 *
	 * @see #realY()
	 */
	public float realX () {
		return x;
	}

	/**
	 * Gibt die <code>y</code>-Verschiebung dieses Vektors wider.
	 *
	 * @return <code>y</code>-Verschiebung dieses Vektors. Positive Werte verschieben nach unten,
	 * negative Werte verschieben nach oben.
	 *
	 * @see #realX()
	 */
	public float realY () {
		return y;
	}

	/**
	 * Gibt einen einfachen Vektor zurück, dessen Richtungskomponenten nur <code>-1</code>,
	 * <code>0</code> oder <code>1</code> annehmen. <br /><br /> <li>-1 bei Werten < 0</li> <li>0
	 * bei Werten = 0</li> <li>1 bei Werten > 0</li>
	 *
	 * @return einfacher Vektor, der die Richtung des ursprünglichen mit einfachen Werten
	 * beschreibt.
	 */
	public Vektor einfacher () {
		return vonKonstante(richtung());
	}

	/**
	 * Berechnet einen einfachen Vektor (maximale Auslenkung bei jeder Achse 1 (positiv wie
	 * negativ)), der der entsprechenden Konstante dieser Klasse entspricht möglich sind: <br />
	 * <code>N</code>, <code>S</code>, <code>O</code>, <code>W</code>, <code>NO</code>,
	 * <code>NW</code>, <code>SO</code>, <code>SW</code>
	 *
	 * @param konstante
	 * 		Konstante, die die Bewegungsrichtung beschreibt
	 *
	 * @return Vektor, der mit einer einfachen Auslenkung (d.h. für <code>x</code> und
	 * <code>y</code> je ein Wertebereich von {-1, 0, 1}) die entsprechende Bewegung macht.<br />
	 *
	 * @throws java.lang.IllegalArgumentException
	 * 		Falls die Konstante einen nicht verwendbaren Wert hat
	 */
	public static Vektor vonKonstante (int konstante) {
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
				return new Vektor(+1, -1);
			case NW:
				return new Vektor(-1, -1);
			case SO:
				return new Vektor(+1, +1);
			case SW:
				return new Vektor(-1, +1);
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
	public int richtung () {
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
	 * Prüft, ob ein beliebiges Objekt gleich diesem Vektor ist. <br /><br /> Zwei Vektoren gelten
	 * als gleich, wenn <code>x</code> und <code>y</code> der beiden Vektoren übereinstimmen.
	 *
	 * @param o
	 * 		Das auf Gleichheit mit diesem zu überprüfende Objekt.
	 *
	 * @return <code>true</code>, wenn beide Vektoren gleich sind, sonst <code>false</code>.
	 */
	@Override
	public boolean equals (Object o) {
		if (o instanceof Vektor) {
			Vektor v = (Vektor) o;

			return x == v.x && y == v.y;
		}

		return false;
	}

	/**
	 * Erstellt ein <b>neues <code>Vektor</code>-Objekt mit demselben Zustan</b>.
	 *
	 * @return Neue Instanz von <code>Vektor</code> mit den selben Koordinaten (x, y)
	 */
	@Override
	public Vektor clone () {
		return new Vektor(x, y);
	}

	/**
	 * Gibt die String-Repräsentation dieses Objektes aus.
	 *
	 * @return String-Repräsentation dieses Vektors
	 */
	@Override
	public String toString () {
		return "ea.Vektor [x = " + x + "; y = " + y + "]";
	}

	/**
	 * Gibt die <code>x</code>-Verschiebung dieses Vektors mit Ganzzahlen wider.
	 *
	 * @return Die <code>x</code>-Verschiebung dieses Vektors. Positive Werte verschieben nach
	 * rechts, negative Werte verschieben nach links.
	 *
	 * @see #dY()
	 */
	public int dX () {
		return (int) x;
	}

	/**
	 * Gibt die <code>y</code>-Verschiebung dieses Vektors mit Ganzzahlen wider.
	 *
	 * @return Die <code>y</code>-Verschiebung dieses Vektors. Positive Werte verschieben nach
	 * unten, negative Werte verschieben nach oben.
	 *
	 * @see #dX()
	 */
	public int dY () {
		return (int) y;
	}

	/**
	 * Gibt diesen Ortsvektor vom Ursprung der Zeichenebene als Punkt aus.<br> Dieser hat die exakt
	 * selben <code>x</code>- / <code>y</code>-Komponenten. Das bedeutet:<br> <code> Vektor v = new
	 * Vektor (10, 20);<br />Punkt p = v.alsPunkt(); -> p == new Punkt(10, 20); </code>
	 *
	 * @return Ortsvektor dieses Punktes
	 *
	 * @see Punkt#alsVektor()
	 */
	@API
	@SuppressWarnings ( "unused" )
	public Punkt alsPunkt () {
		return new Punkt(x, y);
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
     * Berechnet den Winkel zwischen diesem Vektor und einem weiteren. Hierzu wird diese Formel
     * verwendet: <br />
     * <code>cos t = [a o b] / [|a| * |b|]</code><br />
     * <ul>
     *     <li>cos ist der Kosinus</li>
     *     <li>t ist der gesuchte Winkel</li>
     *     <li>a und b sind die Vektoren</li>
     *     <li>|a| ist die Länge des Vektors a</li>
     * </ul>
     * @param anderer   Ein zweiter Vektor.
     * @return          Der Winkel zwischen diesem Vektor und dem zweiten.
     *                  Ist zwischen 0 und 180 Grad.
     */
    @API
    public float winkelZwischen(Vektor anderer) {
        //System.out.println("Acos of " + this.skalarprodukt(anderer) / this.laenge() * anderer.laenge() + " is "
        //+ (float)Math.acos((double)(this.skalarprodukt(anderer) / ((this.laenge() * anderer.laenge())))));
        return (float)Math.acos((double)(this.skalarprodukt(anderer) / (this.laenge() * anderer.laenge())));
    }
}
