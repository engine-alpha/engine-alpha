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

import ea.actor.Rectangle;
import ea.internal.ano.NoExternalUse;

/**
 * Ein nicht-grafisches Rectangle auf der Zeichenebene, das eine allgemeine Fläche beschreibt.
 *
 * @author Michael Andonie
 */
@NoExternalUse
public final class BoundingRechteck {
    /**
     * <b>Reelle</b> <code>getX</code>-Position des Rechtecks
     */
    public final float x;

    /**
     * <b>Reelle</b> <code>getY</code>-Position des Rechtecks
     */
    public final float y;

    /**
     * <b>Reelle</b> Breite des Rechtecks
     */
    public final float width;

    /**
     * <b>Reelle</b> Höhe des Rechtecks
     */
    public final float height;

    /**
     * Konstruktor mit <b>reellen</b> Werten.
     *
     * @param x  Die <code>getX</code>-Koordinate der <i>oberen linken Ecke</i> des Rechtecks
     * @param y  Die <code>getY</code>-Koordinate der <i>oberen linken Ecke</i> des Rechtecks
     * @param dX Die Breite des Bounding-Rechtecks
     * @param dY Die Höhe des Bounding-Rechtecks
     */
    public BoundingRechteck(float x, float y, float dX, float dY) {
        this.x = x;
        this.y = y;
        this.width = dX;
        this.height = dY;
    }

    /**
     * Berechnet aus diesem rein aus Zahlen bestehenden Rahmen ein Rectangle, das in der Zeichenebene
     * darstellbar ist.
     *
     * @return Ein neues Rectangle-Objekt, das genau dieses BoundingRechteck abdeckt
     */
    public Rectangle ausDiesem() {
        Rectangle rectangle = new Rectangle(width, height);
        rectangle.position.set(x,y);
        return rectangle;
    }

    /**
     * Ein Mittenangleich mit einem anderen BoundingRechteck
     *
     * @param r Das BoundingRechteck, an dessen Mitte auch die dieses Rechtecks sein soll.
     */
    public BoundingRechteck mittenAngleichInstanz(BoundingRechteck r) {
        return this.mittenAngleichInstanz(r.zentrum());
    }

    /**
     * Gibt ein neues BoundingRechteck mit der selben Höhe und Breite zurück,
     * das seinen Point genau im angegebenen Zentrum hat.
     *
     * @param p Das Zentrum des zurückzugebenden BoundingRechtecks.
     *
     * @return Ein BoundingRechteck mit der gleichen Höhe und Breite wie dieses, jedoch so
     * verschoben, dass es mit seiner Mitte im angegebenen Zentrum liegt.
     */
    public BoundingRechteck mittenAngleichInstanz(Point p) {
        Point z = this.zentrum();
        return this.verschobeneInstanz(new Vector(p.getRealX() - z.getRealX(), p.getRealY() - z.getRealY()));
    }

    /**
     * Berechnet den Mittelpunkt dieses BoundingRechtecks in der Zeichenebene.
     *
     * @return Der Point mit den Koordinaten, der im Zentrum des Rechtecks liegt (bei ungeraden
     * Koordinaten mit Abrundung)
     */
    public Point zentrum() {
        return new Point(x + ((width) / 2), y + ((height) / 2));
    }

    /**
     * Berechnet ein neues BoundingRechteck mit denselben Maßen wie dieses, jedoch um einen
     * bestimmten Vector verschoben.
     *
     * @param v Der Vector, der die Verschiebung des neuen Objektes von diesem beschreibt.
     *
     * @return Ein neues <code>BoundingRechteck</code>-Objekt, das die selbe Maße wie dieses hat,
     * jedoch um die entsprechende Verschiebung verschoben ist.
     */
    public BoundingRechteck verschobeneInstanz(Vector v) {
        return new BoundingRechteck(x + v.x, y + v.y, width, height);
    }

    /**
     * Berechnet aus diesem und einem weiteren BoundingRechteck ein neues, dass die beiden genau
     * fasst.
     *
     * @param r Das zweite Rectangle fuer die Berechnung
     *
     * @return Ein neues BoundingRechteck, dass die beiden Rechtecke genau umfasst.
     */
    public BoundingRechteck summe(BoundingRechteck r) {
        float x, y, dX, dY;

        if (r.x < this.x) {
            x = r.x;
        } else {
            x = this.x;
        }

        if (r.y < this.y) {
            y = r.y;
        } else {
            y = this.y;
        }

        if (r.x + r.width > this.x + this.width) {
            dX = (r.x + r.width) - x;
        } else {
            dX = (this.x + this.width) - x;
        }

        if (r.y + r.height > this.y + this.height) {
            dY = (r.y + r.height) - y;
        } else {
            dY = (this.y + this.height) - y;
        }

        return new BoundingRechteck(x, y, dX, dY);
    }

    /**
     * Berechnet, ob dieses Rectangle über einer Grenze liegt und wenn <b>nicht</b>, dann berechnet
     * es eines, das gerade so an der Untergrenze liegt.
     *
     * @param untergrenze Die Grenze, auf der das Ergebnis maximal liegen darf.
     *
     * @return Ein BoundingRechteck derselben Höhe und Breite wie dieses, das in jedem Fall über,
     * oder auf der Grenze liegt, wenn es passt, ist es <code>this</code>.
     */
    public BoundingRechteck ueber(int untergrenze) {
        if (y + height < untergrenze) {
            return this;
        } else {
            return new BoundingRechteck(x, untergrenze - height, width, height);
        }
    }

    /**
     * Berechnet, ob dieses Rectangle unter einer Grenze liegt, und wenn <b>nicht</b>, dann berechnet
     * es eines, das gerade so an der Obergrenze liegt.
     *
     * @param obergrenze Die Grenze, auf der das Ergebnis maximal liegen darf.
     *
     * @return Ein BoundingRechteck derselben Hoehe und Breite wie dieses, das in jedem Fall unter,
     * oder auf der Grenze liegt, wenn es passt, ist es <code>this</code>.
     */
    public BoundingRechteck unter(int obergrenze) {
        if (y > obergrenze) {
            return this;
        } else {
            return new BoundingRechteck(x, obergrenze, width, height);
        }
    }

    /**
     * Berechnet, ob dieses Rectangle rechts von einer bestimmten Grenze liegt, und wenn
     * <b>nicht</b>, dann berechnet es eines, das gerade so an der linken Extremgrenze liegt.
     *
     * @param grenzeLinks Der Wert, den das Ergebnisrechteck maximal links sein darf
     *
     * @return Ein BoundingRechteck derselben Höhe und Breite, das in jedem rechts jenseits oder auf
     * der Grenze liegt.<br /> Wenn diese Eigenschaften bereits von diesem Objekt erfüllt werden, so
     * wird <code>this</code> zurückgegeben.
     */
    public BoundingRechteck rechtsVon(int grenzeLinks) {
        if (x > grenzeLinks) {
            return this;
        } else {
            return new BoundingRechteck(grenzeLinks, y, width, height);
        }
    }

    /**
     * Berechnet, ob dieses Rectangle links von einer bestimmten Grenze liegt, und wenn <b>nicht</b>,
     * dann berechnet es eines, das gerade so an der rechten Extremgrenze liegt.
     *
     * @param grenzeRechts Der Wert, den das Ergebnisrechteck maximal rechts sein darf
     *
     * @return Ein BoundingRechteck derselben Höhe und Breite, das in jedem Fall links jenseits oder
     * auf der Grenze liegt.<br /> Wenn diese Eigenschaften bereits von diesem Objekt erfüllt
     * werden, so wird <code>this</code> zurückgegeben.
     */
    public BoundingRechteck linksVon(int grenzeRechts) {
        if (x + width < grenzeRechts) {
            return this;
        } else {
            return new BoundingRechteck(grenzeRechts - width, y, width, height);
        }
    }

    /**
     * Gibt ein neues BoundingRechteck mit selber Höhe und Breite, jedoch einer bestimmten, zu
     * definierenden Position.<br /> Diese Position ist die der <i>linken oberen Ecke</i> des
     * BoundingRechtecks.
     *
     * @param realX Die <i>X-Koordinate der linken oberen Ecke</i> des BoundingRechtecks
     * @param realY Die <i>Y-Koordinate der linken oberen Ecke</i> des BoundingRechtecks
     *
     * @return Ein neues BoundingRechteck mit der eingegebenen Position und derselben Breite und
     * Höhe.
     */
    public BoundingRechteck anPosition(float realX, float realY) {
        return new BoundingRechteck(realX, realY, width, height);
    }

    /**
     * Testet, ob ein Point sich in dem BoundingRechteck befindet.
     *
     * @param p Der Point, der getestet werden soll
     *
     * @return true, wenn der Point in dem BoundingRechteck ist
     */
    public boolean istIn(Point p) {
        return (p.getRealX() >= this.x && p.getRealY() >= this.y && p.getRealX() <= (x + width) && p.getRealY() <= (y + height));
    }

    /**
     * Berechnet die vier Eckpunkte des umfassenden {@link ea.BoundingRechteck}s
     *
     * @return Array mit den vier Eckpunkten des umfassenden {@link ea.BoundingRechteck}s
     */
    public Point[] punkte() {
        return new Point[] {new Point(x, y), new Point(x + width, y), new Point(x, y + height), new Point(x + width, y + height)};
    }

    /**
     * Diese Methoden prüft, ob dieses Bounding-Rectangle ein zweites vollkommen umschliesst.<br />
     * <i>Gemeinsame Ränder zählen <b>AUCH</b> als umschliessen!</i>
     *
     * @param innen Das Innere Bounding-Rectangle. Es soll geprüft werden, ob dieses Vollkommen von
     *              dem die Methode ausführenden Rectangle umschlossen wird.
     *
     * @return <code>true</code>, wenn das <b>ausfuehrende Bounding-Rectangle das als Argument
     * übergebene BoundingRechteck voll umschliesst</b>, sonst <code>false</code>.
     */
    public boolean umschliesst(BoundingRechteck innen) {
        return (this.x <= innen.x && this.y <= innen.y && (this.x + this.width) >= (innen.x + innen.width) && (this.y + this.height) >= (innen.y + innen.height));
    }

    /**
     * Berechnet, ob dieses BoundingRechteck auf einem zweiten "steht".
     *
     * @param r Das BoundingRechteck, auf dem dieses stehen koennte
     *
     * @return <code>true</code>, wenn dies so ist, sonst <code>false</code>.
     */
    public boolean stehtAuf(BoundingRechteck r) {
        if ((r.x + r.width) > this.x && r.x < (this.x + this.width)) {
            return (r.y == this.y + this.height);
        }
        return false;
    }

    /**
     * Berechnet, wie weit man waagrecht ein BoundingRechteck move müsste, damit es dieses
     * nicht mehr berührt.
     *
     * @param r Das BoundingRechteck, das eventuell verschoben werden müsste.
     *
     * @return Die Zahl, die angibt, wie weit man es move muesste, oder 0 wenn sich die
     * beiden nicht berühren.
     */
    public float verschiebenX(BoundingRechteck r) {
        if (!this.schneidetBasic(r)) {
            return 0;
        }
        if (r.linksVon(this)) {
            return this.x - (r.x + r.width);
        } else {
            return (this.x + this.width) - r.x;
        }
    }

    /**
     * Testet, ob ein anderes BoundingRechteck dieses schneidet.<br /> Schneiden bedeutet folgendes
     * im Sinne der Engine Alpha:<br /> <i>Beide Rechtecke divide sich mindestens einen (aber
     * meistens mehrere) Punkte auf der Zeichenebene</i>.
     *
     * @param fig Das zweite zu testende BoundingRechteck
     *
     * @return <code>true</code>, wenn sich die beiden schneiden, sonst <code>false</code>.
     */
    public boolean schneidetBasic(BoundingRechteck fig) {
        if (fig.y < (this.y + this.height) && (fig.y + fig.height) > this.y) {
            if ((fig.x + fig.width) > this.x && fig.x < (this.x + this.width)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Berechnet, ob dieses BoundingRechteck links von einem zweiten ist
     *
     * @param r Das Rectangle, bei dem dies getestet werden soll
     *
     * @return <code>true</code>, wenn dieses Rectangle rechts von dem anderen ist, sonst
     * <code>false</code>.
     */
    public boolean linksVon(BoundingRechteck r) {
        return ((this.x) < (r.x));
    }

    /**
     * Berechnet, wie weit man senkrecht ein BoundingRechteck move müsste, damit es dieses
     * nicht mehr berührt.
     *
     * @param r Das BoundingRechteck, das eventuell verschoben werden müsste.
     *
     * @return Die Zahl, die angibt, wie weit man es move müsste, oder 0 wenn sich die beiden
     * nicht berühren.
     */
    public float verschiebenY(BoundingRechteck r) {
        if (!this.schneidetBasic(r)) {
            return 0;
        }
        if (r.ueber(this)) {
            return this.y - (r.y + r.height);
        } else {
            return (this.y + this.height) - r.y;
        }
    }

    /**
     * Berechnet, ob dieses BoundingRechteck ueber einem zweiten ist
     *
     * @param r Das Rectangle, bei dem dies getestet werden soll
     *
     * @return <code>true</code>, wenn dieses Rectangle rechts von dem anderen ist, sonst
     * <code>false</code>.
     */
    public boolean ueber(BoundingRechteck r) {
        return ((this.y) < (r.y));
    }

    /**
     * Berechnet den Höhenunterschied zwischen dem Fuß des höheren und dem Kopf des tieferen
     * BoundingRechtecks.
     *
     * @param r Das BoundingRechteck, dessen Höhenunterschied zu diesem gefunden werden soll
     *
     * @return Der <b>absolute (also niemals negative)</b> Unterschied in der Höhe zwischen den
     * beiden Objekten. <b>Überlagern sie sich, so ist der Rückgabewert 0</b>!
     */
    public float hoehenUnterschied(BoundingRechteck r) {
        if (this.schneidetBasic(r)) {
            return 0;
        }
        if (this.y < r.y) { // Dieses Rectangle ist das Hoehere!!
            return r.y - (this.y + this.height);
        } else { // Das andere Rectangle ist realHoeher!!
            return this.y - (r.y + r.height);
        }
    }

    /**
     * Transformiert dieses Boudning-Rectangle auf 2 Weisen: Einmal in der Postion und zusätzlich in
     * seiner Höhe.
     *
     * @param v      Der Vector, der die Verschiebung beschreibt.
     * @param dHoehe Die Höhen<b>änderung</b>.
     *
     * @return Ein neues BoundingRechteck, das verschoben und in seiner Höhe geändert ist.
     */
    public BoundingRechteck verschErhoeht(Vector v, int dHoehe) {
        return new BoundingRechteck(x + v.x, y + v.y, width, height + dHoehe);
    }

    /**
     * Sollte dieses Bounding-Rectangle nicht voll innerhalb eines bestimmten anderen, äußeren
     * Rechtecks liegen, so wird versucht, dieses Bounding-Rectangle <i>in das andere mit möglichst
     * wenig Verschiebung</i> zu bringen. Diese Methode wird intern für die Beschränkung des
     * Kamera-Bereiches genutzt.
     * <p/>
     * <div class='hinweisProbleme'><b>Achtung</b>: Voraussetzung dafuer, dass dieser Algorithmus
     * Sinn macht ist, dass das äußere Rectangle ausreichend größer als dieses ist!</div>
     *
     * @param aussen Das äußere Rectangle, innerhalb dessen sich das Ergebnis-Rectangle befinden wird
     *               (sollte das äußere ausreichend groß sein).
     *
     * @return Das Ergebnis-Rectangle, das sich im äußeren Rectangle befinden wird.
     */
    public BoundingRechteck in(BoundingRechteck aussen) {
        float realX = this.x, realY = this.y;

        if (this.x < aussen.x) {
            realX = aussen.x;
        }

        if (this.x + this.width > aussen.x + aussen.width) {
            realX = aussen.x + aussen.width - this.width;
        }

        if (this.y < aussen.y) {
            realY = aussen.y;
        }

        if (this.y + this.height > aussen.y + aussen.height) {
            realY = aussen.y + aussen.height - this.height;
        }

        return new BoundingRechteck(realX, realY, this.width, this.height);
    }

    /**
     * Erstellt einen Klon von diesem BoundingRechteck.
     *
     * @return Ein neues BoundingRechteck mit genau demselben Zustand wie dieses.
     */
    public BoundingRechteck klon() {
        return new BoundingRechteck(x, y, width, height);
    }

    /**
     * Gibt eine String-Repräsentation dieses Objektes aus.
     *
     * @return Die String-Repräsentation dieses Objektes. Hierin wird Auskunft über alle 4
     * ausschlaggebenden Zahlen (<code>getX</code>, <code>getY</code>, <code>getDX</code> und <code>getDY</code>
     * gemacht)
     */
    @Override
    public String toString() {
        return "Bounding-Rectangle: getX:" + x + " getY: " + y + " getDX: " + width + " getDY: " + height;
    }

    /**
     * Gibt die <b>reelle</b> X-Koordinate der oberen linken Ecke aus.
     *
     * @return Die <b>reelle</b> X-Koordinate der oberen linken Ecke dieses BoundingRechtecks.
     *
     * @see #getRealY()
     * @see #getRealBreite()
     * @see #getRealHoehe()
     */
    public float getRealX() {
        return x;
    }

    /**
     * Gibt die <b>reelle</b> Y-Koordinate der oberen linken Ecke aus.
     *
     * @return Die <b>reelle</b> Y-Koordinate der oberen linken Ecke dieses BoundingRechtecks.
     *
     * @see #getRealX()
     * @see #getRealBreite()
     * @see #getRealHoehe()
     */
    public float getRealY() {
        return y;
    }

    /**
     * Gibt die <b>reelle</b> Breite aus.
     *
     * @return Die <b>reelle</b> Breite dieses BoundingRechtecks.
     *
     * @see #getRealX()
     * @see #getRealY()
     * @see #getRealHoehe()
     */
    public float getRealBreite() {
        return width;
    }

    /**
     * Gibt die <b>reelle</b> Hoehe aus.
     *
     * @return Die <b>reelle</b> Hoehe dieses BoundingRechtecks.
     *
     * @see #getRealX()
     * @see #getRealY()
     * @see #getRealBreite()
     */
    public float getRealHoehe() {
        return height;
    }

    /**
     * Gibt die exakte Position der linken oberen Ecke dieses Bounding-Rechtecks
     * aus.
     *
     * @return die Position des BoundingRechtecks, beschrieben durch den Point der linken oberen
     * Ecke dieses Objekts.
     */
    public Point getPosition() {
        return new Point(x, y);
    }
}