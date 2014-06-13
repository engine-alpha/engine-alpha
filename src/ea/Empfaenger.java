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
 * Dieses Interface beschreibt die Eigenschaften, die ein Empfaenger von
 * Informationsnachrichten bei der Netzwerkkommunikation hat.<br />
 * Implementierende Klassen ermoeglichen also die Kommunikation mit anderen
 * EA-Instanzen.
 * 
 * @author Michael Andonie
 */
public interface Empfaenger {
	/**
	 * Diese Methode wird aufgerufen, wenn ein String empfangen
	 * wird. Hierin kann die Eingabe verarbeitet werden.
	 * @param string	Der empfangene String.
	 */
	public abstract void empfangeString(String string);
	
	/**
	 * Diese Methode wird aufgerufen, wenn ein int empfangen
	 * wird. Hierin kann die Eingabe verarbeitet werden.
	 * @param i	Der empfangene int.
	 */
	public abstract void empfangeInt(int i);
	
	/**
	 * Diese Methode wird aufgerufen, wenn ein String empfangen
	 * wird. Hierin kann die Eingabe verarbeitet werden.
	 * @param string	Der empfangene String.
	 */
	public abstract void empfangeByte(byte b);
	
	/**
	 * Diese Methode wird aufgerufen, wenn ein Double empfangen
	 * wird. Hierin kann die Eingabe verarbeitet werden.
	 * @param d	Der empfangene double.
	 */
	public abstract void empfangeDouble(double d);
	
	/**
	 * Diese Methode wird aufgerufen, wenn ein char empfangen
	 * wird. Hierin kann die Eingabe verarbeitet werden.
	 * @param c	Der empfangene char.
	 */
	public abstract void empfangeChar(char c);
	
	/**
	 * Diese Methode wird aufgerufen, wenn ein boolean empfangen
	 * wird. Hierin kann die Eingabe verarbeitet werden.
	 * @param b	Der empfangene boolean.
	 */
	public abstract void empfangeBoolean(boolean b);
	
	/**
	 * Diese Methode wird aufgerufen, wenn der Kommunikationspartner
	 * die Verbindung abbricht, um den Empfaenger eben davon zu
	 * informieren.
	 */
	public abstract void verbindungBeendet();
}
