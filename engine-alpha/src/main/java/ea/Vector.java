/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2018 Michael Andonie and contributors.
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

import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import org.jbox2d.common.Vec2;

/**
 * Beschreibt einen zweidimensionalen Vektor auf der Zeichenebene.
 * Diese Klasse wird für alle Positions- und Richtungsangaben genutzt.
 *
 * @author Michael Andonie
 */
@API
@SuppressWarnings("StaticVariableOfConcreteClass")
public final class Vector implements Cloneable {

    @Internal
    public static Vector of(Vec2 vector) {
        return new Vector(vector.x, vector.y);
    }

    /**
     * Konstante für einen "bewegungslosen" Vector (0, 0).
     */
    @API
    public static final Vector NULL = new Vector(0, 0);

    /**
     * Konstante für eine einfache Verschiebung nach rechts (1, 0).
     */
    @API
    public static final Vector RIGHT = new Vector(1, 0);

    /**
     * Konstante für eine einfache Verschiebung nach links (-1, 0).
     */
    @API
    public static final Vector LEFT = new Vector(-1, 0);

    /**
     * Konstante für eine einfache Verschiebung nach oben (0, -1).
     */
    @API
    public static final Vector UP = new Vector(0, 1);

    /**
     * Konstante für eine einfache Verschiebung nach unten (0, 1).
     */
    @API
    public static final Vector DOWN = new Vector(0, -1);

    /**
     * Der kontinuierliche DeltaX-Wert des Punktes. Die anderen Koordinaten sind ggf. nur gerundet.
     */
    private final float x;

    /**
     * Der kontinuierliche DeltaY-Wert des Punktes. Die anderen Koordinaten sind ggf. nur gerundet.
     */
    private final float y;

    /**
     * Konstruktor.
     *
     * @param x Bewegungsanteil <code>x</code>.
     * @param y Bewegungsanteil <code>y</code>.
     */
    @API
    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @API
    public Vector(double x, double y) {
        this((float) x, (float) y);
    }

    /**
     * Konstruktor. <br><br> Vector wird erzeugt als die nötige Bewegung von einem Point zu einem zweiten.
     *
     * @param start Ausgangspunkt
     * @param end   Zielpunkt
     */
    @API
    public Vector(Vector start, Vector end) {
        this.x = end.x - start.x;
        this.y = end.y - start.y;
    }

    public static Vector ofAngle(float angleInDegree) {
        double rad = Math.toRadians(angleInDegree);
        return new Vector(Math.cos(rad), Math.sin(rad));
    }

    @API
    public float getX() {
        return x;
    }

    @API
    public float getY() {
        return y;
    }

    /**
     * Gibt eine <b>Normierung</b> des Vektors aus. Dies ist ein Vector, der <ul><li>in die selbe Richtung wie der
     * ursprüngliche Vector zeigt.</li> <li>eine Länge von (möglichst) exakt 1 hat.</li></ul>
     *
     * @return Normierter Vector zu diesem Vector
     */
    @API
    public Vector normalize() {
        return divide(getLength());
    }

    /**
     * Teilt die effektive Länge des Vektors durch eine Zahl und kürzt dadurch seine Effektivität.
     *
     * @param divisor Hierdurch wird die Länge des Vektors auf der Zeichenebene geteilt.
     * @return Vector-Objekt, das eine Bewegung in dieselbe Richtung beschreibt, allerdings in der Länge gekürzt um den
     * angegebenen Divisor.
     * @throws java.lang.ArithmeticException Falls <code>divisor</code> <code>0</code> ist.
     * @see #multiply(float)
     */
    @API
    public Vector divide(float divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Der Divisor für das Teilen war 0");
        }

        return new Vector(x / divisor, y / divisor);
    }

    /**
     * Gibt die Länge des Vektors aus.
     *
     * @return Länge des Vektors.
     */
    @API
    public float getLength() {
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Berechnet die Gegenrichtung des Vektors.
     *
     * @return Neues Vector-Objekt, das genau die Gegenbewegung zu dem eigenen beschreibt.
     */
    @API
    public Vector negate() {
        return new Vector(-this.x, -this.y);
    }

    /**
     * Berechnet die Gegenrichtung des Vektors in X-Richtung.
     *
     * @return Neues Vector-Objekt, das genau die Gegenbewegung zu dem eigenen beschreibt.
     */
    @API
    public Vector negateX() {
        return new Vector(-this.x, this.y);
    }

    /**
     * Berechnet die Gegenrichtung des Vektors in Y-Richtung.
     *
     * @return Neues Vector-Objekt, das genau die Gegenbewegung zu dem eigenen beschreibt.
     */
    @API
    public Vector negateY() {
        return new Vector(this.x, -this.y);
    }

    /**
     * Berechnet die effektive Bewegung, die dieser Vector und ein weiterer zusammen ausüben.
     *
     * @param x Änderung in X-Richtung
     * @param y Änderung in Y-Richtung
     * @return Neues Vector-Objekt, das die Summe der beiden ursprünglichen Bewegungen darstellt.
     */
    @API
    public Vector add(float x, float y) {
        return new Vector(this.x + x, this.y + y);
    }

    /**
     * Berechnet die effektive Bewegung, die dieser Vector und ein weiterer zusammen ausüben.
     *
     * @param v zweiter Vector
     * @return Neues Vector-Objekt, das die Summe der beiden ursprünglichen Bewegungen darstellt.
     */
    @API
    public Vector add(Vector v) {
        return new Vector(this.x + v.x, this.y + v.y);
    }

    /**
     * Berechnet die Differenz zwischen diesem und einem weiteren Vector.
     *
     * @param x Änderung in X-Richtung
     * @param y Änderung in Y-Richtung
     * @return Die Differenz der beiden Vektoren (<code>"this - v"</code>)
     */
    @API
    public Vector subtract(float x, float y) {
        return new Vector(this.x - x, this.y - y);
    }

    /**
     * Berechnet die Differenz zwischen diesem und einem weiteren Vector.
     *
     * @param v zweiter Vector
     * @return Die Differenz der beiden Vektoren (<code>"this - v"</code>)
     */
    @API
    public Vector subtract(Vector v) {
        return new Vector(this.x - v.x, this.y - v.y);
    }

    /**
     * Berechnet eine rotierte Version.
     *
     * @param degree Rotation in Grad
     * @return Neues Vector-Objekt, das entsprechend der Gradzahl rotiert wurde.
     */
    @API
    public Vector rotate(float degree) {
        double angle = Math.toRadians(degree);

        return new Vector( //
                Math.cos(angle) * x + Math.sin(angle) * y, //
                -Math.sin(angle) * x + Math.cos(angle) * y //
        );
    }

    /**
     * Gibt den Vektor an, der den Punkt, den dieser Vektor beschreibt, zu dem Punkt verschieben würde, den ein
     * weiterer Vektor beschreibt.
     *
     * @param v Ein weiterer Vektor.
     * @return Der Vektor, der <code>(this.x|this.y)</code> verschieben würde zu <code>(v.x|v.y)</code>.
     */
    @API
    public Vector getDistance(Vector v) {
        return v.subtract(this);
    }

    /**
     * Multipliziert die effektiven Längen beider Anteile des Vektors (<code>getX</code> und
     * <code>getY</code>) mit einem festen Faktor. <br> Dadurch entsteht ein neuer Vector mit anderen
     * Werten, welcher zurückgegeben wird.
     *
     * @param factor Der Faktor, mit dem die <code>getX</code>- und <code>getY</code>-Werte des Vektors multipliziert
     *               werden
     * @return Der Vector mit den multiplizierten Werten
     * @see #divide(float)
     */
    @API
    public Vector multiply(float factor) {
        return new Vector(x * factor, y * factor);
    }

    /**
     * Multipliziert die effektive Länge des X-Anteils des Vektors mit einem festen Faktor. <br>
     * Dadurch entsteht ein neuer Vector mit anderen Werten, welcher zurückgegeben wird.
     *
     * @param factor Der Faktor, mit dem der x-Wert des Vektors multipliziert wird
     * @return Der Vector mit den multiplizierten Werten
     * @see #multiply(float)
     */
    @API
    public Vector multiplyX(float factor) {
        return new Vector(x * factor, y);
    }

    /**
     * Multipliziert die effektive Länge des X-Anteils des Vektors mit einem festen Faktor. <br>
     * Dadurch entsteht ein neuer Vector mit anderen Werten, welcher zurückgegeben wird.
     *
     * @param factor Der Faktor, mit dem der x-Wert des Vektors multipliziert wird
     * @return Der Vector mit den multiplizierten Werten
     * @see #multiply(float)
     */
    @API
    public Vector multiplyY(float factor) {
        return new Vector(x, y * factor);
    }

    /**
     * Berechnet das <b>Skalarprodukt</b> von diesem Vector mit einem weiteren. Das Skalarprodukt für zweidimensionale
     * Vektoren ist: <code>(a, b) o (c, d) = a * b + c * d</code>
     *
     * @param v zweiter Vector
     * @return Skalarprodukt dieser Vektoren mit dem Vector <code>v</code>.
     */
    @API
    public float getScalarProduct(Vector v) {
        return this.x * v.x + this.y * v.y;
    }

    /**
     * Berechnet, ob dieser Vector keine Wirkung hat. Dies ist der Fall, wenn beide Komponenten (<code>getX</code> und
     * <code>getY</code>) 0 sind.
     *
     * @return <code>true</code>, wenn dieser keine Auswirkungen macht, sonst <code>false</code>.
     */
    @API
    public boolean isNull() {
        return this.x == 0 && this.y == 0;
    }

    /**
     * Gibt zurück, ob dieser Vector <i>echt ganzzahlig</i> ist, also ob seine <b>tatsächlichen Delta-Werte</b> beide
     * Ganzzahlen sind.
     *
     * @return <code>true</code>, wenn <b>beide</b> Delta-Werte dieses Punktes ganzzahlig sind,
     * sonst <code>false</code>.
     */
    @API
    public boolean isIntegral() {
        return x == (int) x && y == (int) y;
    }

    /**
     * Berechnet die Richtung des Vektors, in die er wirkt.
     *
     * @return Der Wert der Konstanten, die diese Bewegung wiederspiegelt.
     */
    @API
    public Direction getDirection() {
        if (x == 0 && y == 0) {
            return Direction.NONE;
        }

        if (x == 0) {
            return y > 0 ? Direction.DOWN : Direction.UP;
        }

        if (y == 0) {
            return x > 0 ? Direction.RIGHT : Direction.LEFT;
        }

        if (y < 0) {
            return x < 0 ? Direction.UP_LEFT : Direction.UP_RIGHT;
        }

        return x > 0 ? Direction.DOWN_LEFT : Direction.DOWN_RIGHT;
    }

    /**
     * Prüft, ob ein beliebiges Objekt gleich diesem Vector ist. <br><br> Zwei Vektoren gelten als gleich, wenn
     * <code>getX</code> und <code>getY</code> der beiden Vektoren übereinstimmen.
     *
     * @param o Das auf Gleichheit mit diesem zu überprüfende Objekt.
     * @return <code>true</code>, wenn beide Vektoren gleich sind, sonst <code>false</code>.
     */
    @API
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof Vector v) {
            return x == v.x && y == v.y;
        }

        return false;
    }

    @Override
    public Vector clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * Gibt die String-Repräsentation dieses Objektes aus.
     * <p>
     * Diese Methode sollte nur zu Debugging-Zwecken benutzt werden.
     *
     * @return String-Repräsentation dieses Vektors
     */
    @Override
    public String toString() {
        return "ea.Vector [ x = " + x + "; y = " + y + " ]";
    }

    /**
     * Gibt die Manhattan-Länge des Vektors zurück. Diese ist für v=(a, b) definiert als a+b.
     *
     * @return Die Summe von delta X und delta Y des Vektors.
     */
    @API
    public float getManhattanLength() {
        float length = x + y;
        return length < 0 ? -length : length;
    }

    /**
     * Berechnet den Winkel zwischen diesem Vector und einem weiteren. Hierzu wird diese Formel verwendet: <br>
     * <code>cos t = [a o b] / [|a| * |b|]</code><br>
     * <ul>
     * <li>cos ist der Kosinus</li>
     * <li>t ist der gesuchte Winkel</li>
     * <li>a und b sind die Vektoren</li>
     * <li>|a| ist die Länge des Vektors a</li>
     * </ul>
     *
     * @param other Ein zweiter Vector.
     * @return Der Winkel zwischen diesem Vector und dem zweiten. Ist zwischen 0 und 180.
     */
    @API
    public float getAngle(Vector other) {
        if (this.y < other.y) {
            return (float) Math.toDegrees(Math.acos(this.getScalarProduct(other) / (this.getLength() * other.getLength())));
        } else {
            return (float) (360 - Math.toDegrees(Math.acos(this.getScalarProduct(other) / (this.getLength() * other.getLength()))));
        }
    }

    @Internal
    public Vec2 toVec2() {
        return new Vec2(x, y);
    }

    @API
    public boolean isNaN() {
        return Float.isNaN(x) || Float.isNaN(y);
    }
}
