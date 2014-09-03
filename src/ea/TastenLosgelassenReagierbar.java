/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2014 Michael Andonie and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ea;

/**
 * Beschreiben Sie hier die Klasse TasteLosgelassenReagierbar.
 * 
 * @author Michael Andonie
 */
public interface TastenLosgelassenReagierbar {
    /**
     * Wird aufgerufen bei einem angemeldeten TastenReagierbar, sobald eine Taste, die in der Kennungstabelle liegt, bei entsprechend aktiviertem Fenster losgelassen wird.<br />
     * Die Tabelle liegt der Anleitung der Engine bei. Wie die Interfaces anzumelden sind, liegt ebenfalls dem <b>Handbuch</b> bei<br />
     * @param   code   Die Repraesentation der Taste als Zahl. Hierdurch wird ein sehr einfaches Handeln in benoetigter Groesse ueber eine <b>switch()-Anweisung</b>moeglich.<br />
     * Welche Zahl welchem Tastenkuerzel entspricht, ist dem <b>Handbuch</b> zu entnehmen!!
     */
    public abstract void tasteLosgelassen(int code);
}
