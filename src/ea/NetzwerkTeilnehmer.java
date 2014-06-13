/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
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
 * Diese abstrakte Klasse beschreibt die allgemeinen Eigenschaften
 * einer Netzwerkkommunikation. Ist sie etabliert, kann man darueber
 * Informationen austauschen, also Daten senden und empfangen.
 * 
 * @author Michael Andonie
 */
public abstract class NetzwerkTeilnehmer {
	
	/**
	 * Prueft bzw. gibt an, ob eine Verbindung bereits besteht.
	 * @return	Ist der Rückgabewert <code>true</code>, so besteht eine
	 * 			Verbindung. Das Senden bzw. Empfangen macht also in
	 * 			diesem Sinn.
	 * 			Ist dieser Rückgabewert <code>false</code>, besteht noch keine
	 * 			Verbindung.
	 */
	public abstract boolean verbindungSteht();
	
	/**
	 * Methode zur Verarbeitung einer direkte
	 * @param string Der String
	 */
	public abstract void empfange(String string);
}
