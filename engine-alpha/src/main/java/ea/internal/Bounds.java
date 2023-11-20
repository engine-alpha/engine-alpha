/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2019 Michael Andonie and contributors.
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

package ea.internal;

import ea.Vector;
import ea.internal.annotations.Internal;

/**
 * Ein nicht-grafisches Rectangle auf der Zeichenebene, das eine allgemeine Fläche beschreibt.
 *
 * @author Michael Andonie
 */
@Internal
public final class Bounds {
    /**
     * <b>Reelle</b> <code>getX</code>-Position des Rechtecks
     */
    private final float x;

    /**
     * <b>Reelle</b> <code>getY</code>-Position des Rechtecks
     */
    private final float y;

    /**
     * <b>Reelle</b> Breite des Rechtecks
     */
    private final float width;

    /**
     * <b>Reelle</b> Höhe des Rechtecks
     */
    private final float height;

    /**
     * Konstruktor mit <b>reellen</b> Werten.
     *
     * @param x      Die <code>getX</code>-Koordinate der <i>unteren linken Ecke</i> des Rechtecks
     * @param y      Die <code>getY</code>-Koordinate der <i>unteren linken Ecke</i> des Rechtecks
     * @param width  Die Breite des Bounding-Rechtecks
     * @param height Die Höhe des Bounding-Rechtecks
     */
    public Bounds(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Ein Mittenangleich mit einem anderen Bounds
     *
     * @param r Das Bounds, an dessen Mitte auch die dieses Rechtecks sein soll.
     */
    public Bounds withCenterAtBoundsCenter(Bounds r) {
        return this.withCenterPoint(r.getCenter());
    }

    /**
     * Gibt ein neues Bounds mit derselben Höhe und Breite zurück,
     * das seinen Point genau im angegebenen Zentrum hat.
     *
     * @param p Das Zentrum des zurückzugebenden BoundingRechtecks.
     *
     * @return Ein Bounds mit der gleichen Höhe und Breite wie dieses, jedoch so
     * verschoben, dass es mit seiner Mitte im angegebenen Zentrum liegt.
     */
    public Bounds withCenterPoint(Vector p) {
        return this.moveBy(p.subtract(this.getCenter()));
    }

    /**
     * Berechnet den Mittelpunkt dieses BoundingRechtecks in der Zeichenebene.
     *
     * @return Der Point mit den Koordinaten, der im Zentrum des Rechtecks liegt.
     */
    public Vector getCenter() {
        return new Vector(x + (width / 2), y + (height / 2));
    }

    /**
     * Berechnet ein neues Bounds mit denselben Maßen wie dieses, jedoch um einen
     * bestimmten Vector verschoben.
     *
     * @param v Der Vector, der die Verschiebung des neuen Objektes von diesem beschreibt.
     *
     * @return Ein neues <code>Bounds</code>-Objekt, das dieselben Maße wie dieses hat,
     * jedoch um die entsprechende Verschiebung verschoben ist.
     */
    public Bounds moveBy(Vector v) {
        return new Bounds(x + v.getX(), y + v.getY(), width, height);
    }

    /**
     * Berechnet aus diesem und einem weiteren Bounds ein neues, dass die beiden genau
     * fasst.
     *
     * @param bounds Das zweite Rectangle für die Berechnung
     *
     * @return Ein neues Bounds, dass die beiden Rechtecke genau umfasst.
     */
    public Bounds smallestCommon(Bounds bounds) {
        float x, y, dX, dY;

        if (bounds.x < this.x) {
            x = bounds.x;
        } else {
            x = this.x;
        }

        if (bounds.y < this.y) {
            y = bounds.y;
        } else {
            y = this.y;
        }

        if (bounds.x + bounds.width > this.x + this.width) {
            dX = (bounds.x + bounds.width) - x;
        } else {
            dX = (this.x + this.width) - x;
        }

        if (bounds.y + bounds.height > this.y + this.height) {
            dY = (bounds.y + bounds.height) - y;
        } else {
            dY = (this.y + this.height) - y;
        }

        return new Bounds(x, y, dX, dY);
    }

    /**
     * Berechnet, ob dieses Rectangle über einer Grenze liegt und wenn <b>nicht</b>, dann berechnet
     * es eines, das gerade so an der Untergrenze liegt.
     *
     * @param lowerBound Die Grenze, auf der das Ergebnis maximal liegen darf.
     *
     * @return Ein Bounds derselben Höhe und Breite wie dieses, das in jedem Fall über,
     * oder auf der Grenze liegt, wenn es passt, ist es <code>this</code>.
     */
    public Bounds above(float lowerBound) {
        if (y + height < lowerBound) {
            return this;
        } else {
            return new Bounds(x, lowerBound - height, width, height);
        }
    }

    /**
     * Berechnet, ob dieses Rectangle below einer Grenze liegt, und wenn <b>nicht</b>, dann berechnet
     * es eines, das gerade so an der Obergrenze liegt.
     *
     * @param upperBound Die Grenze, auf der das Ergebnis maximal liegen darf.
     *
     * @return Ein Bounds derselben Höhe und Breite wie dieses, das in jedem Fall below,
     * oder auf der Grenze liegt, wenn es passt, ist es <code>this</code>.
     */
    public Bounds below(float upperBound) {
        if (y > upperBound) {
            return this;
        } else {
            return new Bounds(x, upperBound, width, height);
        }
    }

    /**
     * Berechnet, ob dieses Rectangle rechts von einer bestimmten Grenze liegt, und wenn
     * <b>nicht</b>, dann berechnet es eines, das gerade so an der linken Extremgrenze liegt.
     *
     * @param border Der Wert, den das Ergebnisrechteck maximal links sein darf
     *
     * @return Ein Bounds derselben Höhe und Breite, das in jedem rechts jenseits oder auf
     * der Grenze liegt.<br> Wenn diese Eigenschaften bereits von diesem Objekt erfüllt werden, so
     * wird <code>this</code> zurückgegeben.
     */
    public Bounds rightOf(float border) {
        if (x > border) {
            return this;
        } else {
            return new Bounds(border, y, width, height);
        }
    }

    /**
     * Berechnet, ob dieses Rectangle links von einer bestimmten Grenze liegt, und wenn <b>nicht</b>,
     * dann berechnet es eines, das gerade so an der rechten Extremgrenze liegt.
     *
     * @param border Der Wert, den das Ergebnisrechteck maximal rechts sein darf
     *
     * @return Ein Bounds derselben Höhe und Breite, das in jedem Fall links jenseits oder
     * auf der Grenze liegt.<br> Wenn diese Eigenschaften bereits von diesem Objekt erfüllt
     * werden, so wird <code>this</code> zurückgegeben.
     */
    public Bounds leftOf(float border) {
        if (x + width < border) {
            return this;
        } else {
            return new Bounds(border - width, y, width, height);
        }
    }

    /**
     * Gibt ein neues Bounds mit selber Höhe und Breite, jedoch einer bestimmten, zu
     * definierenden Position.<br> Diese Position ist die der <i>linken unteren Ecke</i> des
     * BoundingRechtecks.
     *
     * @param realX Die <i>X-Koordinate der linken unteren Ecke</i> des BoundingRechtecks
     * @param realY Die <i>Y-Koordinate der linken unteren Ecke</i> des BoundingRechtecks
     *
     * @return Ein neues Bounds mit der eingegebenen Position und derselben Breite und
     * Höhe.
     */
    public Bounds atPosition(float realX, float realY) {
        return new Bounds(realX, realY, width, height);
    }

    /**
     * Testet, ob ein Point sich in dem Bounds befindet.
     *
     * @param v Der Point, der getestet werden soll
     *
     * @return true, wenn der Point in dem Bounds ist
     */
    public boolean contains(Vector v) {
        return (v.getX() >= this.x && v.getY() >= this.y && v.getX() <= (x + width) && v.getY() <= (y + height));
    }

    /**
     * Berechnet die vier Eckpunkte des umfassenden {@link Bounds}s
     *
     * @return Array mit den vier Eckpunkten des umfassenden {@link Bounds}s
     */
    public Vector[] points() {
        return new Vector[] {new Vector(x, y), new Vector(x + width, y), new Vector(x, y + height), new Vector(x + width, y + height)};
    }

    /**
     * Diese Methoden prüft, ob dieses Bounding-Rectangle ein zweites vollkommen enthält.<br>
     * <i>Gemeinsame Ränder zählen <b>AUCH</b> als umschliessen!</i>
     *
     * @param inner Das Innere Bounding-Rectangle. Es soll geprüft werden, ob dieses Vollkommen von
     *              dem die Methode ausführenden Rectangle umschlossen wird.
     *
     * @return <code>true</code>, wenn das <b>ausfuehrende Bounding-Rectangle das als Argument
     * übergebene Bounds voll contains</b>, sonst <code>false</code>.
     */
    public boolean contains(Bounds inner) {
        return (this.x <= inner.x && this.y <= inner.y && (this.x + this.width) >= (inner.x + inner.width) && (this.y + this.height) >= (inner.y + inner.height));
    }

    /**
     * Berechnet, ob dieses Bounds oberhalb eines zweiten ist.
     *
     * @param r Das Rectangle, bei dem dies getestet werden soll
     *
     * @return <code>true</code>, wenn dieses Rectangle rechts von dem anderen ist, sonst
     * <code>false</code>.
     */
    public boolean above(Bounds r) {
        return ((this.y) < (r.y));
    }

    /**
     * Sollte dieses Bounding-Rectangle nicht voll innerhalb eines bestimmten anderen, äußeren
     * Rechtecks liegen, so wird versucht, dieses Bounding-Rectangle <i>in das andere mit möglichst
     * wenig Verschiebung</i> zu bringen. Diese Methode wird intern für die Beschränkung des
     * Kamera-Bereiches genutzt.
     * <p>
     * <div class='hinweisProbleme'><b>Achtung</b>: Voraussetzung dafür, dass dieser Algorithmus
     * Sinn macht ist, dass das äußere Rectangle ausreichend größer als dieses ist!</div>
     *
     * @param outer Das äußere Rectangle, innerhalb dessen sich das Ergebnis-Rectangle befinden wird
     *               (sollte das äußere ausreichend groß sein).
     *
     * @return Das Ergebnis-Rectangle, das sich im äußeren Rectangle befinden wird.
     */
    public Bounds in(Bounds outer) {
        float realX = this.x, realY = this.y;

        if (this.x < outer.x) {
            realX = outer.x;
        }

        if (this.x + this.width > outer.x + outer.width) {
            realX = outer.x + outer.width - this.width;
        }

        if (this.y < outer.y) {
            realY = outer.y;
        }

        if (this.y + this.height > outer.y + outer.height) {
            realY = outer.y + outer.height - this.height;
        }

        return new Bounds(realX, realY, this.width, this.height);
    }

    /**
     * Erstellt einen Klon von diesem Bounds.
     *
     * @return Ein neues Bounds mit genau demselben Zustand wie dieses.
     */
    @Override
    public Bounds clone() {
        return new Bounds(x, y, width, height);
    }

    /**
     * Gibt eine String-Repräsentation dieses Objektes aus.
     *
     * @return Die String-Repräsentation dieses Objektes. Hierin wird Auskunft über alle 4
     * ausschlaggebenden Zahlen (<code>getX</code>, <code>getY</code>, <code>getWidth</code> und <code>getHeight</code>
     * gemacht)
     */
    @Override
    public String toString() {
        return "Bounding-Rectangle: getX:" + x + " getY: " + y + " getWidth: " + width + " getHeight: " + height;
    }

    /**
     * Gibt die <b>reelle</b> X-Koordinate der unteren linken Ecke aus.
     *
     * @return Die <b>reelle</b> X-Koordinate der unteren linken Ecke dieses BoundingRechtecks.
     *
     * @see #getY()
     * @see #getWidth()
     * @see #getHeight()
     */
    public float getX() {
        return x;
    }

    /**
     * Gibt die <b>reelle</b> Y-Koordinate der unteren linken Ecke aus.
     *
     * @return Die <b>reelle</b> Y-Koordinate der unteren linken Ecke dieses BoundingRechtecks.
     *
     * @see #getX()
     * @see #getWidth()
     * @see #getHeight()
     */
    public float getY() {
        return y;
    }

    /**
     * Gibt die <b>reelle</b> Breite aus.
     *
     * @return Die <b>reelle</b> Breite dieses BoundingRechtecks.
     *
     * @see #getX()
     * @see #getY()
     * @see #getHeight()
     */
    public float getWidth() {
        return width;
    }

    /**
     * Gibt die <b>reelle</b> Höhe aus.
     *
     * @return Die <b>reelle</b> Höhe dieses BoundingRechtecks.
     *
     * @see #getX()
     * @see #getY()
     * @see #getWidth()
     */
    public float getHeight() {
        return height;
    }

    /**
     * Gibt die exakte Position der linken unteren Ecke dieses Bounding-Rechtecks
     * aus.
     *
     * @return die Position des BoundingRechtecks, beschrieben durch den Point der linken unteren
     * Ecke dieses Objekts.
     */
    public Vector getPosition() {
        return new Vector(x, y);
    }
}
