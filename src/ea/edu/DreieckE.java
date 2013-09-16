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

package ea.edu;

import ea.graphic.geo.Dreieck;
import ea.graphic.geo.Punkt;

/**
 * Das einfache Dummie-Dreieck. Kann nur in seiner Position veraendert werden.
 * @author Andonie
 */
public class DreieckE 
extends Dreieck {

    /**
     * Konstruktor eines "edu"-Dreiecks.<br />
     * Erstellt ein solches und macht es im "edu"-Standartfenster sichtbar.
     */
    public DreieckE() {
        super(new Punkt(100,100), new Punkt(200,100), new Punkt(150,50));
        farbeSetzen("Gruen");
        FensterE.getFenster().wurzel.add(this);
    }

    /**
     * Setzt die Punkte dieses "edu"-Dreiecks neu.
     * @param x1    Die X-Koordinate des 1. Punktes
     * @param y1    Die Y-Koordinate des 1. Punktes
     * @param x2    Die X-Koordinate des 2. Punktes
     * @param y2    Die Y-Koordinate des 2. Punktes
     * @param x3    Die X-Koordinate des 3. Punktes
     * @param y3    Die Y-Koordinate des 3. Punktes
     */
    public void punkteSetzen(int x1, int y1, int x2, int y2, int x3, int y3) {
        super.punkteSetzen(new int[] {x1, x2, x3}, new int[] {y1, y2, y3});
    }
}
