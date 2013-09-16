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

import java.awt.Graphics;

/**
 * Ein Kreis ist ein regelmaessiges n-Eck, dessen Eckenzahl gegen unendlich geht.<br />
 * Dies ist mit einem Computer nicht moeglich, daher wird fuer einen Kreis eine ausrechend grosse Anzahl
 * an Ecken gewaehlt. Diese ist ueber die Genauigkeit im Konstruktor mitzugeben oder im vereinfachten konstruktor
 * bereits voreingestellt.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Kreis
extends RegEck
{
    
    /**
     * Konstruktor fuer Objekte der Klasse Kreis
     * @param   x   Die X-Koordinate der Linken oberen Ecke des den Kreis umschreibenden Rechtecks, <b>nicht die des MIttelpunktes</b>
     * @param   y   Die Y-Koordinate der Linken oberen Ecke des den Kreis umschreibenden Rechtecks, <b>nicht die des MIttelpunktes</b>
     * @param   durchmesser Der Durchmesser des Kreises
     * @param   genauigkeit Die Genauigkeitsstufe des Kreises.<br />
     * <b>je hoeher sie ist, desto besser sieht der KReis aus, jedoch auch desto hoeher ist die Computerbelastung</b>
     */
    public Kreis(int x, int y, int durchmesser, int genauigkeit) {
        super(x, y, (int)Math.pow(genauigkeit, 2), durchmesser);
    }
    
    /**
     * Alternativkonstruktor mit vorgefertigter Genauigkeit
     */
    public Kreis(int x, int y, int durchmesser) {
        this(x, y, durchmesser, 6);
    }

    /**
     * Gibt den Radius des Kreises aus
     * @return  Der Radius des Kreises
     */
    public int radius() {
        return radius;
    }

    /**
     * Test, ob ein anderes Raum-Objekt von diesem geschnitten wird.
     * @param   r   Das Objekt, das auf Kollision mit diesem getestet werden soll.
     * @return  TRUE, wenn sich beide Objekte schneiden.
     */
    @Override
    public boolean schneidet(Raum m) {
        if(m instanceof Kreis) {
            Punkt p1 = this.mittelPunkt();
            Punkt p2 = m.mittelPunkt();
            double dif = Math.sqrt(((p1.x-p2.x)*(p1.x-p2.x))+((p1.y-p2.y)*(p1.y-p2.y)));
            if(dif < this.radius() + ((Kreis)m).radius()) {
                return true;
            } else {
                return false;
            }
        } else {
            return super.schneidet(m);
        }

    }
    
    @Override
    public void zeichnen(Graphics g, BoundingRechteck r) {
        if(!r.schneidetBasic(this.dimension())) {
            return;
        }
        g.setColor(this.formen()[0].getColor());
        g.fillOval(x-r.x, y-r.y, 2*radius, 2*radius);
    }
}
