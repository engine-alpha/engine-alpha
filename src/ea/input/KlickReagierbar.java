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
 *  KlickReagierbar implementierende Klassen koennen auf jeden einzelnen <b>Rechtsklick</b> reagieren,
 *  unabhaengig davon, ob dies ein spezielles Objekt trifft oder nicht.
 * 
 * @author Michael Andonie
 */

public interface KlickReagierbar {
    /**
     * Diese Methode wird bei jedem Rechtsklick aktiviert, unabhaengig davon, ob etwas getroffen wurde oder nicht.<br />
     * Natuerlick muss dafuer erst das <code>KlickReagierbar</code> bei der Maus angemeldet werden. <br /><br />
     * <b>Beispiel:</b><br /><br />
     * <code>
     * //Instanziierte, fertige, am Fenster angemeldete Maus<br />
     * Maus maus;<br /><br />
     * 
     * //Mein Interface, in welcher Form auch immer, instanziiert<br />
     * KlickReagierbar meinKlick;<br /><br />
     * 
     * //Mein Interface an der Maus anmelden<br />
     * maus.anmelden(meinKlick);<br />
     * </code><br />
     * Ab dann wird dieses Interface ueber seine Methode <code>klick()</code> benachrichtigt, immer wenn ein <b>Rechtsklick</b> mit der Maus 
     * passiert ist.
     * @param   x   Die X-Koordinate des Klickpunktes <b>auf der Zeichenebene</b>
     * @param   y   Die Y-Koordinate des Klickpunktes <b>auf der Zeichenebene</b>
     */
    public abstract void klickReagieren(int x, int y);
}
