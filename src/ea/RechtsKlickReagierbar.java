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
 * Eine Klasse, die dieses Interface implementiert, kann auf Rechtsklicks der Maus reagieren
 * 
 * @author Michael Andonie
 */
public interface RechtsKlickReagierbar {

    /**
     * Diese Methode wird bei einer an einer aktiven Maus angemeldeten Klasse immer dann
     * aufgerufen, wenn ein Rechtsklick gemacht wurde. Um ein Interface anzumelden, wird
     * in der Maus folgende Methode aufgerufen:<br /><br />
     * <code>
     * //Die aktive, INSTANZIIERTE UND ANGEMELDETE Maus<br />
     * Maus maus;<br /><br />
     *
     * //Das INSTANZIIERTE Interface<br />
     * RechtsKlickReagierbar klick;<br /><br />
     *
     * //SO meldet man das Interface an der Maus an.<br />
     * //NACH dem Aufruf dieser Methode wird es bei jedem Rechtsklick benachrichtigt.<br />
     * maus.rechtsKlickReagierbarAnmelden(klick);
     * </code>
     * @param x Die X-Koordinate der Maus beim Klick <b>auf der Zeichenebene</b>
     * @param y Die Y-Koordinate der Maus beim Klick <b>auf der Zeichenebene</b>
     * @see ea.Maus
     */
    public abstract void rechtsKlickReagieren(int x, int y);
}
