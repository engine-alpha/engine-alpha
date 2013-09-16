/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
 *
 * Copyright (C) 2011  Michael Andonie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ea.graphic.geo;


import ea.graphic.Vektor;

/**
 * Ein nicht grafisches Rechteck auf der Zeichenebene, das eine allgemeine Flaeche beschreibt
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public final class BoundingRechteck
implements java.io.Serializable
{
    /**Die Serialisierungs-Konstante dieser Klasse. In keiner Weise fuer die Programmierung mit der Engine bedeutsam.*/
    public static final long serialVersionUID = 99L;

    /**
     * Die X-Position des Rechtecks
     */
    public final int x;

    /**
     * Die Y-Position des Rechtecks
     */
    public final int y;
    
    /**
     * Die Breite des Rechtecks
     */
    public final int breite;
    
    /**
     * Die Laenge des Rechtecks
     */
    public final int hoehe;
    
    /**
     * Konstruktor fuer Objekte der Klasse <code>BoundingRechteck</code>.
     * @param x Die X-Koordinate der <i>oberen linken Ecke</i> des Rechtecks
     * @param y Die Y-Koordinate der <i>oberen linken Ecke</i> des Rechtecks
     * @param dX Die Breite des Bounding-Rechtecks
     * @param dY Die Hoehe des Bounding-Rechtecks
     */
    public BoundingRechteck(int x, int y, int dX, int dY) {
        this.x = x;
        this.y = y;
        breite = dX;
        hoehe = dY;
    }
    
    /**
     * Berechnet ein neues BoundingRechteck mit denselben Massen wie dieses, jedoch um einen bestimmten Vektor verschoben.
     * @param   v   Der Vektor, der die Verschiebung des neuen Objektes von diesem beschreibt.
     * @return  Ein neues <code>BoundingRechteck</code>-Objekt, das die selbe Masse wie dieses hat, jedoch um die entsprechende Verschiebung verschoben ist.
     */
    public BoundingRechteck verschobeneInstanz(Vektor v) {
        return new BoundingRechteck(x+v.x, y+v.y, breite, hoehe);
    }
    
    /**
     * Berechnet aus diesem rein aus Zahlen bestehenden Rahmen ein Rechteck, das in der Zeichenebene darstellbar ist.
     * @return  Ein neues Rechteck-Objekt, das genau dieses BoundingRechteck abdeckt
     */
    public Rechteck ausDiesem() {
        return new Rechteck(x, y, breite, hoehe);
    }


    /**
     * Gibt ein neues BoundingRechteck zurueck, das seinen Punkt genau im angegebenen Zentrum hat.
     * @param p Das Zentrum des zurueckzugebenden BoundingRechtecks.
     * @return  Ein BoundingRechteck mit der gleichen Hoehe und Breite wie dieses, jedoch so verschoben,
     * dass es mit seiner Mitte im angegebenen Zentrum liegt.
     */
    public BoundingRechteck mittenAngleichInstanz(Punkt p) {
        Punkt z = this.zentrum();
        return this.verschobeneInstanz(new Vektor(p.x-z.x, p.y-z.y));
    }
    
    /**
     * Ein Mittenangleich mit einem anderen BoundingRechteck
     * @param   r   Das BoundingRechteck, an dessen Mitte auch die dieses Rechtecks sein soll.
     */
    public BoundingRechteck mittenAngleichInstanz(BoundingRechteck r) {
        return this.mittenAngleichInstanz(r.zentrum());
    }
    
    /**
     * Berechnet aus diesem und einem weiteren BoundingRechteck ein neues, dass die beiden genau fasst.
     * @param   r   Das zweite Rechteck fuer die Berechnung
     * @return  Ein neues BoundingRechteck, dass die beiden Rechtecke genau umfasst.
     */
    public BoundingRechteck summe(BoundingRechteck r) {
        int x, y, dX, dY;
        if(r.x < this.x) {
            x = r.x;
        } else {
            x = this.x;
        }
        if(r.y < this.y) {
            y = r.y;
        } else {
            y = this.y;
        }
        if(r.x + r.breite > this.x + this.breite) {
            dX = (r.x + r.breite)-x;
        } else {
            dX = (this.x+this.breite)-x;
        }
        if(r.y + r.hoehe > this.y + this.hoehe) {
            dY = (r.y + r.hoehe)-y;
        } else {
            dY = (this.y + this.hoehe)-y;
        }
        return new BoundingRechteck(x, y, dX, dY);
    }
    
    /**
     * Berechnet, ob dieses Rechteck ueber einer Grenze liegt, und wenn <b>nicht</b>, dann berechnet es eines, 
     * das gerade so an der Untergrenze liegt.
     * @param   untergrenze Die Grenze, auf der das Ergebnis maximal liegen darf.
     * @return  Ein BoundingRechteck derselben Hoehe und Breite wie dieses, das in jedem Fall ueber, oder auf der Grenze liegt, wenn es passt, ist es <code>this</code>.
     */
    public BoundingRechteck ueber(int untergrenze) {
        if(y + hoehe < untergrenze) {
            return this;
        } else {
            return new BoundingRechteck(x, untergrenze-hoehe, breite, hoehe);
        }
    }
    
    /**
     * Berechnet, ob dieses Rechteck unter einer Grenze liegt, und wenn <b>nicht</b>, dann berechnet es eines, 
     * das gerade so an der Obergrenze liegt.
     * @param   obergrenze Die Grenze, auf der das Ergebnis maximal liegen darf.
     * @return  Ein BoundingRechteck derselben Hoehe und Breite wie dieses, das in jedem Fall unter, oder auf der Grenze liegt, wenn es passt, ist es <code>this</code>.
     */
    public BoundingRechteck unter(int obergrenze) {
        if(y > obergrenze) {
            return this;
        } else {
            return new BoundingRechteck(x, obergrenze, breite, hoehe);
        }
    }
    
    /**
     * Berechnet, ob dieses Rechteck rechts von einer bestimmten Grenze liegt, und wenn <b>nicht</b>, dann berechnet es eines, 
     * das gerade so an der linken Extremgrenze liegt.
     * @param   grenzeLinks Der Wert, den das Ergebnisrechteck maximal links sein darf
     * @return  Ein BoundingRechteck derselben Hoehe und Breite, das in jedem rechts jenseits oder auf der Grenze liegt.<br />
     * Wenn diese Eigenschaften bereits von diesem Objekt erfuellt werden, so wird <code>this</code> zurueckgegeben.
     */
    public BoundingRechteck rechtsVon(int grenzeLinks) {
        if(x > grenzeLinks) {
            return this;
        } else {
            return new BoundingRechteck(grenzeLinks, y, breite, hoehe);
        }
    }
    
    /**
     * Berechnet, ob dieses Rechteck links von einer bestimmten Grenze liegt, und wenn <b>nicht</b>, dann berechnet es eines, 
     * das gerade so an der rechten Extremgrenze liegt.
     * @param   grenzeRechts Der Wert, den das Ergebnisrechteck maximal rechts sein darf
     * @return  Ein BoundingRechteck derselben Hoehe und Breite, das in jedem Fall links jenseits oder auf der Grenze liegt.<br />
     * Wenn diese Eigenschaften bereits von diesem Objekt erfuellt werden, so wird <code>this</code> zurueckgegeben.
     */
    public BoundingRechteck linksVon(int grenzeRechts) {
        if(x+breite < grenzeRechts) {
            return this;
        } else {
            return new BoundingRechteck(grenzeRechts-breite, y, breite, hoehe);
        }
    }
    
    /**
     * Gibt ein neues BoundingRechteck mit selber Hoehe und Breite, jedoch einer bestimmten, zu definierenden Position.<br />
     * Diese Position ist die der <i>linken oberen Ecke</i> des BoundingRechtecks.
     * @param x Die <i>X-Koordinate der linken oberen Ecke</i> des BoundingRechtecks
     * @param y Die <i>Y-Koordinate der linken oberen Ecke</i> des BoundingRechtecks
     * @return  Ein neues BoundingRechteck mit der eingegebenen Position und derselben Breite und Hoehe.
     */
    public BoundingRechteck anPosition(int x, int y) {
        return new BoundingRechteck(x, y, breite, hoehe);
    }

    /**
     * Testet, ob sich ein Dreieck in dem BoundingRechteck befindet.<br />
     * Hierbei wird zuerst getestet, ob ein Punkt des Dreiecks im Rechteck ist, dann, falls noetig ob ein Punkt des Rechtecks im Dreieck ist.
     */
    public boolean schneidet(Dreieck d) {
        Punkt[] punkte = d.punkte();
        for(int i = 0; i < punkte.length; i++) {
            if(istIn(punkte[i])) {
                return true;
            }
        }
        punkte = this.punkte();
        for(int i = 0; i < punkte.length; i++) {
            if(d.beinhaltet(punkte[i])) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Testet, ob ein Punkt sich in dem BoundingRechteck befindet.
     * @param p Der Punkt, der getestet werden soll
     * @return  true, wenn der Punkt in dem BoundingRechteck ist
     */
    public boolean istIn(Punkt p) {
        return (p.x >= this.x && p.y >= this.y && p.x <= (x+breite) && p.y <= (y + hoehe));
    }
    
    /**
     * Berechnet den Mittelpunkt dieses BoundingRechtecks in der Zeichenebene.
     * @return  Der Punkt mit den Koordinaten, der im Zentrum des Rechtecks liegt (bei ungeraden Koordinaten mit Abrundung)
     */
    public Punkt zentrum() {
        return new Punkt(x+((breite)/2), y+((hoehe)/2));
    }
    
    /**
     * @return  Ein Punkt-Array der Laenge 4, dessen Inhalt die 4 beschreibenden Punkte des BoundingRechteck 's darstellt.
     */
    public Punkt[] punkte() {
        Punkt[] p = {
            new Punkt(x, y),
            new Punkt(x+breite, y),
            new Punkt(x, y+hoehe),
            new Punkt(x+breite, y+hoehe)
        };
        return p;
    }
    
    /**
     * Berschnet, ob dieses BoundingRechteck links von einem zweiten ist
     * @param   Das Rechteck, bei dem dies getestet werden soll
     * @return  true, wenn dieses Rechteck rechts von dem anderen ist.
     */
    public boolean linksVon(BoundingRechteck r) {
        return ((this.x)<(r.x));
    }
    
    /**
     * Berechnet, ob dieses BoundingRechteck  ueber einem zweiten ist
     * @param   Das Rechteck, bei dem dies getestet werden soll
     * @return  true, wenn dieses Rechteck rechts von dem anderen ist.
     */
    public boolean ueber(BoundingRechteck r) {
        return((this.y)<(r.y));
    }
    
    /**
     * Testet, ob ein anderes BoundingRechteck dieses schneidet.<br />
     * Schneiden bedeutet folgendes im Sinne der Engine Alpha:<br />
     * <i>Beide Rechtecke teilen sich mindestens einen (aber meistens mehrere) Punkte auf der Zeichenebene</i>.
     * @param   fig Das zweite zu testende BoundingRechteck
     * @return  <code>true</code>, wenn sich die beiden schneiden, sonst <code>false</code>.
     */
    public boolean schneidetBasic(BoundingRechteck fig) {
        if(fig.y < (this.y+this.hoehe) && (fig.y+fig.hoehe) > this.y) {
            if((fig.x+fig.breite) > this.x && fig.x < (this.x+this.breite)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Diese Methoden prueft, ob dieses Bounding-Rechteck ein zweites vollkommen umschliesst.<br />
     * <i>Gemeinsame Raender zaehlen <b>AUCH</b> als umschliessen!</i>
     * @param innen Das Innere Bounding-Rechteck. Es soll geprueft werden, ob dieses Vollkommen von dem die Methode
     * ausfuehrenden Rechteck umschlossen wird.
     * @return  <code>true</code>, wenn das <b>ausfuehrende Bounding-Rechteck das als Argument uebergebene BoundingRechteck voll
     * umschliesst</b>, sonst <code>false</code>.
     */
    public boolean umschliesst(BoundingRechteck innen) {
        return (this.x <= innen.x && this.y <= innen.y && (this.x+this.breite) >= (innen.x+innen.breite) && (this.y + this.hoehe) >= (innen.y + innen.hoehe));
    }
    
    /**
     * Berechnet, ob dieses BoundingRechteck auf einem zweiten "steht".
     * @param   r   Das BoundingRechteck, auf dem dieses stehen koennte
     * @return  <code>true</code>, wenn dies so ist.
     */
    public boolean stehtAuf(BoundingRechteck r) {
        if((r.x+r.breite) > this.x && r.x < (this.x+this.breite)) {
            return (r.y == this.y+this.hoehe);
        }
        return false;
    }
    
    /**
     * Berechnet, wie weit man waagrecht ein BoundingRechteck verschieben muesste, damit es dieses nicht mehr beruehrt.
     * @param   r   Das BoundingRechteck, das eventuell verschoben werden muesste.
     * @return  Die Zahl, die angibt, wie weit man es verschieben muesste, oder 0 wenn sich die beiden nicht beruehren.
     */
    public int verschiebenX(BoundingRechteck r) {
        if(!this.schneidetBasic(r)) {
            return 0;
        }
        if(r.linksVon(this)) {
            return this.x-(r.x+r.breite);
        } else {
            return (this.x + this.breite) - r.x;
        }
    }
    
    /**
     * Berechnet, wie weit man senkrecht ein BoundingRechteck verschieben muesste, damit es dieses nicht mehr beruehrt.
     * @param   r   Das BoundingRechteck, das eventuell verschoben werden muesste.
     * @return  Die Zahl, die angibt, wie weit man es verschieben muesste, oder 0 wenn sich die beiden nicht beruehren.
     */
    public int verschiebenY(BoundingRechteck r) {
        if(!this.schneidetBasic(r)) {
            return 0;
        }
        if(r.ueber(this)) {
            return this.y - (r.y+r.hoehe);
        } else {
            return (this.y+this.hoehe) - r.y;
        }
    }
    
    /**
     * Berechnet den Hoehenunterschied zwischen dem Fuss des hoeheren und dem Kopf des tieferen BoundingRechtecks.
     * @param   r   Das BoundingRechteck, dessen Hoehenunterschied zu diesem gefunden werden soll
     * @return  Der <b>absolute (also niemals negative)</b> Unterschied in der Hoehe zwischen den beiden Objekten. <b>Ueberlagern sie sich, so ist der Rueckgabewert 0</b>!
     */
    public int hoehenUnterschied(BoundingRechteck r) {
        if(this.schneidetBasic(r)) {
            return 0;
        }
        if(this.y < r.y) { //Dieses Rechteck ist das Hoehere!!
            return r.y - (this.y+this.hoehe);
        } else { //Das andere Rechteck ist hoeher!!
            return this.y - (r.y + r.hoehe);
        }
    }

    /**
     * Transformiert dieses Boudning-Rechteck auf 2 Weisen: Einmal in der Postion und zusaetzlich in
     * seiner Hoehe.
     * @param v         Der Vektor, der die Verschiebung beschreibt.
     * @param dHoehe    Die Hoehen<b>aenderung</b>.
     * @return  Ein neues BoundingRechteck, das verschoben und in seiner Hoehe geaendert ist.
     */
    public BoundingRechteck verschErhoeht(Vektor v, int dHoehe) {
        return new BoundingRechteck(x+v.x, y + v.y, breite, hoehe+dHoehe);
    }

    /**
     * Sollte dieses Bounding-Rechteck nicht voll innerhalb eines bestimmten anderen,
     * aeusseren Rechteck liegen, so wird versucht, dieses Bounding-Rechteck <i>in das
     * andere mit moeglichst wenig Verschiebung</i> zu bringen. Diese Methode wird intern
     * fuer die Beschraenkung des Kamera-Bereiches genutzt.<br /><br />
     * <b>ACHTUNG!!</b><br />
     * Voraussetzung dafuer, dass dieser Algorithmus Sinn macht ist, dass das aeussere
     * Rechteck ausreichend groesser als dieses ist!!
     * @param aussen    Das aeussere Rechteck, innerhalb dessen sich das Ergebnis-Rechteck
     * befinden wird (sollte das aeussere ausreichend gross sein).
     * @return  Das Ergebnis-Rechteck, das sich im aeusseren Rechteck befinden wird.
     */
    public BoundingRechteck in(BoundingRechteck aussen) {
        int x = this.x,y = this.y;
        if(this.x < aussen.x) {
            x = aussen.x;
        }
        if(this.x+this.breite > aussen.x+aussen.breite) {
            x = aussen.x+aussen.breite-this.breite;
        }
        if(this.y < aussen.y) {
            y = aussen.y;
        }
        if(this.y+this.hoehe > aussen.y + aussen.hoehe) {
            y = aussen.y + aussen.hoehe - this.hoehe;
        }
        return new BoundingRechteck(x, y, this.breite, this.hoehe);
    }
    
    /**
     * Gibt die X-Koordinate der oberen linken Ecke aus.
     * @return  Die X-Koordinate der oberen linken Ecke dieses BoundingRechtecks.
     * @see y()
     * @see breite()
     * @see hoehe()
     */
    public int x() {
        return x;
    }

    /**
     * Gibt die Y-Koordinate der oberen linken Ecke aus.
     * @return  Die Y-Koordinate der oberen linken Ecke dieses BoundingRechtecks.
     * @see x()
     * @see breite()
     * @see hoehe()
     */
    public int y() {
        return y;
    }

    /**
     * Gibt die Breite aus.
     * @return  Die Breite dieses BoundingRechtecks.
     * @see x()
     * @see y()
     * @see hoehe()
     */
    public int breite() {
        return breite;
    }

    /**
     * Gibt die Hoehe aus.
     * @return  Die Hoehe dieses BoundingRechtecks.
     * @see x()
     * @see y()
     * @see breite()
     */
    public int hoehe() {
        return hoehe;
    }

    /**
     * Erstellt einen Klon von diesem BoundingRechteck.
     * @return  Ein neues BoundingRechteck mit genau demselben Zustand wie dieses.
     */
    public BoundingRechteck klon() {
        return new BoundingRechteck(x, y, breite, hoehe);
    }


    /**
     * Gibt eine String-Repraesentation dieses Objektes aus.
     * @return  Die String-Repraesentation dieses Objektes. Hierin wird auskunft ueber alle 4
     * ausschlag gebenden Zahlen (x, y, dX, dY gemacht)
     */
    @Override
    public String toString() {
        return "Bounding-Rechteck: x:" +x+ " y: " + y +" dX: " + breite + " dY: " + hoehe;
    }
}
