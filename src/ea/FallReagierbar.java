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

package ea;


/**
 * FallReagierbar kann darauf reagieren, das das mit ihr angemeldete Raum-Objekt ueber <code>Physik</code> faellt und einen
 * gewissen Maximalwert erreicht. Dann wird ihre Methode <code>fallReagieren()</code> aufgerufen.<br />
 */
public interface FallReagierbar {
    /**
     * Diese Methode wird aufgerufen, <b>solange</b> das fallende Raum-Objekt unter der mit diesem Listener 
     * angemeldeten mindesthoehe faellt.<br />
     * Das heisst, wird das Fallen im problematischen bereich hierin <b>nicht behoben</b>, so wird diese Methode wieder und 
     * wieder aufgerufen, bis das Objekt nicht mehr im gefaehrlichen Bereich ist.
     */
    public abstract void fallReagieren();
}
