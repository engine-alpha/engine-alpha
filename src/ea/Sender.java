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

import ea.internal.util.Logger;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Diese Klasse wird verwendet, um Daten ueber eine Server-Client-Verbindung zu <b>senden</b>. Sie
 * bietet eine einfache <i>Schnittstelle</i> zur Kommunikation, da Exceptions etc. nicht zu beachten
 * sind.<br /> Ein Sender bekommt also einen <code>OutputStream</code>, ueber den die Kommunikation
 * verlaeuft.
 *
 * @author Michael Andonie
 */
public class Sender implements SenderInterface {

	/**
	 * Der Writer, ueber den geschrieben wird.
	 */
	private final BufferedWriter writer;

	/**
	 * Gibt an, ob noch eine Verbindung zum anderen Ende der Kommunikation besteht.
	 */
	private boolean active;

	/**
	 * Konstruktur erstellt den Sender.
	 *
	 * @param bw
	 * 		Der OutputStream, ueber den ab sofort gesendet werden soll.
	 */
	public Sender (BufferedWriter bw) {
		writer = bw;
		active = true;
	}

	/**
	 * Gibt an, ob die Verbindung über diesen Sender noch aktiv ist.
	 *
	 * @return <code>true</code>, wenn der Sender dem Kommunikationspartner (noch) nicht gesendet
	 * hat, dass die Verbindung beendet wird. Sonst <code>false</code>.
	 */
	public boolean verbindungAktiv () {
		return active;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendeString (String s) {
		sende("s" + s);
	}

	/**
	 * Interne Routine. Sendet eine Nachricht, wobei wesentliche Eigenschaften geprueft werden und
	 * Fehler ausgegeben werden.
	 *
	 * @param s
	 * 		Der String, der uebertragen werden soll.
	 *
	 * @return <code>true</code>, wenn die Nachricht erfolgreich gesendet werden konnte, sonst
	 * <code>false</code>.
	 */
	boolean sende (String s) { // package private, weil NetzwerkInterpreter das braucht!
		if (!active) {
			Logger.error("Kann nach dem Schließen nicht mehr senden.");
			return false;
		}

		try {
			writer.write(s);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			Logger.error("Es gab einen internen Fehler beim Schreiben.");
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendeInt (int i) {
		sende("i" + Integer.toString(i));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendeByte (byte b) {
		sende("b" + Byte.toString(b));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendeDouble (double d) {
		sende("d" + Double.toString(d));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendeChar (char c) {
		sende("c" + Character.toString(c));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendeBoolean (boolean b) {
		sende("k" + Boolean.toString(b));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beendeVerbindung () {
		if (!sende("xq")) return;

		active = false;

		try {
			writer.close();
		} catch (IOException e) {
			Logger.error("Konnte die Verbindung nicht schließen.");
		}
	}
}
