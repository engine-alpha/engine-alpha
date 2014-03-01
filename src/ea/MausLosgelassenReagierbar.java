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
 * Das Listener-Interface fuer losgelassene Tasten auf der Maus. Einfach an 
 * dem aktiven <code>Maus</code>-Objekt anzumelden:<br /><br />
 * <code>
 * MausLosgelassenReagierbar listener; //<- Mein Listener
 * maus.mausLosgelassenReagierbarAnmelden(listener);
 * </code>
 * 
 * @author Michael Andonie
 */
public interface MausLosgelassenReagierbar {
    /**
     * Diese Methode wird bei jedem an der aktiven Maus angemeldeten Listener 
     * ausgefuehrt, sobald eine Maustaste losgelassen wird.
     * @param x Die X-Koordinate der Maus zum Zeitpunkt des Loslassens
     * @param y Die Y-Koordinate der Maus zum Zeitpunkt des Loslassens
     * @param linksklick Ist dieser Wert <code>true</code>, war die Losgelassene
     * Maustaste die Linke. Ansonsten ist dieser Wert <code>false</code>.
     */
    public abstract void mausLosgelassen(int x, int y, boolean linksklick);
}
