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
 * Dieses Interface wird implementiert, um auf Die verschiedenen Tastaturbefehle ans Fenster Reagieren zu koennen.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */

public interface TastenReagierbar
{
    /**
     * Wird aufgerufen bei einem angemeldeten TastenReagierbar, sobald eine Taste, die in der Kennungstabelle liegt, bei entsprechend aktiviertem Fenster gedrueckt wird.<br />
     * Die Tabelle liegt der Anleitung der Engine bei.
     * @param   tastenkuerzel   Die Repraesentation der Taste als Zahl. Hierdurch wird ein sehr einfaches Handeln in benoetigter Groesse ueber eine <b>switch()-Anweisung</b>moeglich.<br />
     * Welche Zahl welchem Tastenkuerzel entspricht, ist dem <b>Handbuch</b> zu entnehmen!!
     */
    public abstract void reagieren(int tastenkuerzel);
}
