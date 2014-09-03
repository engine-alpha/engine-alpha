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
 * TODO Dokumentation
 *
 * @author Michael Andonie
 */
public interface SenderInterface {

	/**
	 * Versendet einen String an den Kommunikationspartner.
	 *
	 * @param string
	 * 		Der String, der gesendet werden soll.
	 */
	public abstract void sendeString (String string);

	/**
	 * Versendet einen Integer an den Kommunikationspartner.
	 *
	 * @param i
	 * 		Der int-Wert, der gesendet werden soll.
	 */
	public abstract void sendeInt (int i);

	/**
	 * Versendet ein Byte an den Kommunikationspartner.
	 *
	 * @param b
	 * 		Das Byte, das gesendet werden soll.
	 */
	public abstract void sendeByte (byte b);

	/**
	 * Versendet einen Double an den Kommunikationspartner.
	 *
	 * @param d
	 * 		Der double-Wert, der gesendet werden soll.
	 */
	public abstract void sendeDouble (double d);

	/**
	 * Versendet einen Character an den Kommunikationspartner
	 *
	 * @param c
	 * 		Der char-Wert, der gesendet werden soll.
	 */
	public abstract void sendeChar (char c);

	/**
	 * Versendet einen Booleschen Wert an den Kommunikationspartner
	 *
	 * @param b
	 * 		Der boolean-Wert, der gesendet werden soll.
	 */
	public abstract void sendeBoolean (boolean b);

	/**
	 * Beendet die Verbindung. Nach dem Aufruf dieser Methode kann man keine Verbindung mehr
	 * aufbauen.
	 */
	public abstract void beendeVerbindung ();
}