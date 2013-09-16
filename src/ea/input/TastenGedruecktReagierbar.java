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

package ea.input;

/**
 * Dieses Interface kennzeichnet eine Klasse, die auf gedrueckt gehaltene Tasten reagieren kann.<br />
 * Das bedeutet, dass der <b>Unterschied zu <code>TastenReagierbar</code></b> darin besteht, dass 
 * diese Methode nicht bei jedem Druck der Taste aktiviert wird, sondern in regelmaessigen Abstaenden 
 * (der <b>Interpolation</b>) aufgerufen.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */

public interface TastenGedruecktReagierbar
{
    /**
     * Wird bei einem am <code>Fenster</code> angemeldeten <code>TastenGedruecktReagierbar</code>-
     * Objekt ausgefuehrt, fuer jede Taste, die gedrueckt gehalten wird.<br /><br />
     * Diese wird immer wieder, solange Tasten gedrueckt werden, immer im Abstand von <b>50 Millisekunden</b> 
     * aufgerufen.
     * @param   tastenCode  Der  Code der gedrueckt gehaltenen Taste. Welche Zahl fuer welche Taste steht, 
     * ist im <b>Handbuch</b> ablesbar!
     * 
     */
    public abstract void tasteGedrueckt(int tastenCode);
}
