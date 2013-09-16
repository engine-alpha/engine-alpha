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
 * Ein Punkt beschreibt einen exakt bestimmten eindimensionalen Punkt auf der Zeichenebene.<br />
 * Er ist durch 2 Koordinaten exakt bestimmt.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Punkt
{
    /**
     * Die X-Koordinate
     */
    public final int x;
    
    /**
     * Die Y-Koordinate
     */
    public final int y;

    /**
     * Konstruktor fuer Objekte der Klasse Punkt
     */
    public Punkt(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Beschreibt den Abstand zwischen diesem und einem anderen Punkt in der Luftlinie.<br />
     * Hierbei wird lediglich der Satz des Pythagoras angewendet (a^2 + b^2 = c^2).
     * @param   p   Der Punkt, zu dem die direkte Laenge hin berechnet werden soll.
     * @return  Die Laenge der Luftlinie zwischen diesem und dem anderen Punkt.<br />
     * Dieser Wert ist nie negativ.
     */
    public double abstand(Punkt p) {
        double x, y;
        x = Math.abs(this.x - p.x);
        y = Math.abs(this.y - p.y);
        return Math.sqrt((x*x) + (y*y));
    }

    /**
     * Gibt einen Punkt aus, der um eine bestimmte Verschiebung verschobenen Koordinaten dieses
     * Punktes hat.
     * @param v Die Verschiebung, die dieser Punkt erhalten wuerde, um mit der Ausgabe uebereinzustimmen.
     * @return  Ein Punkt, mit der X-Koordinate <code>p.x + v.x</code> und der Y-Koordinate <code>p.y + v.y</code>.
     * @see verschobeneInstanz( ea.graphic.Vektor )
     */
    public Punkt verschobenerPunkt(Vektor v) {
        return new Punkt(this.x+v.x, this.y+v.y);
    }

    /**
     * Gibt einen Punkt aus, der die um eine Verschiebung veraenderten Koordinaten dieses Punktes hat.<br />
     * Also quasi diesen Punkt, waere er um eine Verschiebeung veraendert.<br /><br />
     * Diese Methode ist identisch mit <code>verschobenerPunkt(Vektor)</code>. Sie existiert der einheitlichen Methodennomenklatur
     * der Zeichenebenen-Klassen halber.
     * @param v Der Vektor, der diese Verschiebung beschreibt.
     * @return  Der Punkt, der die Koordinaten dieses Punktes - verschoben um den Vektor - hat.
     * @see verschobenerPunkt(Vektor)
     */
    public Punkt verschobeneInstanz(Vektor v) {
        return verschobenerPunkt(v);
    }

    /**
     * Gibt die X-Koordinate dieses Punktes zurueck.
     * @return  Die X-Koordinate dieses Punktes.
     * @see y()
     */
    public int x() {
        return x;
    }

    /**
     * Gibt die Y-Koordinate dieses Punktes zurueck.
     * @return  Die Y-Koordinate dieses Punktes.
     * @see x()
     */
    public int y() {
        return y;
    }
}
