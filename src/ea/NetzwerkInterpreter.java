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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Diese Klasse ist dafuer konzipiert, reine empfangene Nachrichten <b> zu verarbeiten und die
 * enthaltene Information an einen oder auch mehrere <code>Empfaenger</code> weiterzugeben.<br />
 * Die Weitergabe der Signale erfolg <b>sequentiell über einen eigenen Thread</b>. Die Signale
 * werden ebenso <b>sequentiell verarbeitet</b>.
 *
 * @author Michael Andonie
 */
public class NetzwerkInterpreter extends Thread {
	/**
	 * Diese Liste speichert alle Empfänger, an die der Interpreter eine empfangene Information
	 * weitergeben soll.
	 */
	private final ArrayList<Empfaenger> outputListe = new ArrayList<>();

	/**
	 * Der Reader, über den die Informationen gelesen werden.
	 */
	private BufferedReader reader;

	/**
	 * Gibt an, ob die Verbindung noch aktiv ist. Zu Beginn ist dem so.
	 */
	private boolean connectionActive;

	private String remoteIP;

	/**
	 * Enthält eine Referenz auf den Server, falls diese Verbindung von einem Server ausgeht.
	 */
	private Server server;

	public NetzwerkInterpreter (String remoteIP, Server server, BufferedReader br) {
		this.remoteIP = remoteIP;
		this.server = server;
		this.reader = br;
		this.connectionActive = true;
		this.setDaemon(true);
		this.start();
	}

	/**
	 * Gibt an, ob die Verbindung über diesen Interpreter noch aktiv ist.
	 *
	 * @return <code>true</code>, wenn nicht vom Kommunikationspartner gesendet wurde, dass die
	 * Verbindung beendet wird. Sonst <code>false</code>.
	 */
	public boolean verbindungAktiv () {
		return connectionActive;
	}

	/**
	 * Fügt einen Empfänger dem Interpreter hinzu.
	 *
	 * @param e
	 * 		Der hinzuzufuegende Empfaenger
	 */
	public void empfaengerHinzufuegen (Empfaenger e) {
		outputListe.add(e);
	}

	@Override
	public void run () {
		while (!isInterrupted() && connectionActive) {
			try {
				String got = reader.readLine();

				if (got == null) {
					connectionActive = false;
					break;
				}

				process(got);
			} catch (IOException e) {
				Logger.error("Konnte nicht vom Kommunikationspartner einlesen.");
				connectionActive = false;
			}
		}

		try {
			reader.close();
		} catch (IOException e) {
			Logger.error("Konnte die Verbindung nicht schließen.");
		}
	}

	/**
	 * Diese Methode verarbeitet die unveränderte Ausgabe des Kommunikationspartners. Diese wird
	 * analysiert und anschließend die entsprechende Information an alle angehängten
	 * <code>Empfaenger</code> weitergegeben.
	 *
	 * @param raw
	 * 		Die ungeschnittene Nachricht des Partners.
	 */
	private void process (String raw) {
		// Die Information
		String rest = raw.substring(1);

		if (server != null && server.isBroadcasting()) {
			switch (raw.charAt(0)) {
				case 's':
				case 'i':
				case 'b':
				case 'd':
				case 'c':
				case 'k':
					for (NetzwerkVerbindung v : server.getVerbindungen()) {
						if (!v.getRemoteIP().equals(remoteIP)) {
							v.sende(raw);
						}
					}

					break;
			}
		}

		// Fallunterscheidung gemäß Informationstyp
		switch (raw.charAt(0)) {
			case 's': // String
				for (Empfaenger e : outputListe) {
					e.empfangeString(rest);
				}

				break;
			case 'i': // Int
				int i = Integer.parseInt(rest);

				for (Empfaenger e : outputListe) {
					e.empfangeInt(i);
				}

				break;
			case 'b': // Byte
				byte b = Byte.parseByte(rest);

				for (Empfaenger e : outputListe) {
					e.empfangeByte(b);
				}

				break;
			case 'd': // Double
				double d = Double.parseDouble(rest);

				for (Empfaenger e : outputListe) {
					e.empfangeDouble(d);
				}

				break;
			case 'c': // Char
				char c = rest.charAt(0);

				for (Empfaenger e : outputListe) {
					e.empfangeChar(c);
				}

				break;
			case 'k': // Boolean
				boolean bo = Boolean.parseBoolean(rest);

				for (Empfaenger e : outputListe) {
					e.empfangeBoolean(bo);
				}

				break;
			case 'x': // Steuerzeichen
				switch (rest.charAt(0)) {
					case 'q': //quit communication
						quitCommunication();
						break;
					case 'e': // name entry
						break;
				}

				break;
		}
	}

	public void quitCommunication () {
		for (Empfaenger e : outputListe) {
			e.verbindungBeendet();
		}

		try {
			reader.close();
		} catch (IOException e1) {
			Logger.error("Konnte den Kommunikationskanal nicht mehr schließen.");
		}

		connectionActive = false;
	}
}