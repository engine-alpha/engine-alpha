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

package ea.graphic.lights;

/**
 * Leuchtend implementieren intern alle Klassen, die leuchten koennen.<br />
 * Bei den erbenden Klassen muss nur die Methode <code>leuchtetSetzen()</code> ausgefuehrt werden.
 */
public interface Leuchtend {
    /**
     * Setzt, ob dieses Leuchtend-Objekt leuchten soll.<br />
     * Ist dies der Fall, so werden immer wieder schnell dessen Farben geaendert; so entsteht ein Leuchteffekt.
     * @param   leuchtet    Ob dieses Objekt nun leuchten soll oder nicht (mehr).<br />
     * <b>Achtung:</b> Die Leuchtfunktion kann bei bestimmten Klassen sehr psychadelisch und aufreizend wirken! Daher 
     * sollte sie immer mit Bedacht und in Nuancen verwendet werden!
     */
    public abstract void leuchtetSetzen(boolean leuchtet);
    
    /**
     * Fuehrt einen Leuchtschritt aus.<br />
     * Dies heisst, dass in dieser Methode die Farbe einfach gewechselt wird. Da diese Methode schnell und oft hintereinander 
     * ausgefuehrt wird, soll so der Leuchteffekt entstehen.<br />
     * <b>Diese Methode sollte nur innerhalb der Engine ausgefuehrt werden! Also nicht fuer den Entwickler gedacht.</b>
     */
    public abstract void leuchtSchritt();
    
    /**
     * Gibt wieder, ob das Leuchtet-Objekt gerade leuchtet oder nicht.
     * @return  <code>true</code>, wenn das Objekt gerade leuchtet, wenn nicht, dann ist die Rueckgabe <code>false</code>
     */
    public abstract boolean leuchtet();
}
