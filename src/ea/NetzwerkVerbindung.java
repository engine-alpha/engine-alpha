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

import java.io.BufferedWriter;

import ea.edu.net.NetzwerkInterpreter;

/**
 * Diese Klasse sammelt die beiden wesentlichen Objekte einer Netzverbindung
 * (<code>NetwerkInterpreter</code>, der die Informationen an alle angemeldeten
 * <code>Empfaenger</code> weitergibt sowie dem <code>Sender</code>, mit dem man Informationen an
 * die andere Seite schicken kann)und gibt dieser einen Namen.
 *
 * @author Michael Andonie, Niklas Keller <me@kelunik.com>
 */
public class NetzwerkVerbindung extends Sender {

	/**
	 * Der Name der Verbindung. Dies ist der Name, den der Client dem Server mitgegeben hat.
	 */
	private final String name;

	/**
	 * Der interpreter, der die Sendungen des Partners grob verarbeitet und weitterreicht.
	 */
	private final NetzwerkInterpreter interpreter;

	/**
	 * IP des Remote-Geräts
	 */
	private final String ip;

	public NetzwerkVerbindung (String name, String ip, BufferedWriter bw, NetzwerkInterpreter interpreter) {
		super(bw);
		this.name = name;
		this.ip = ip;
		this.interpreter = interpreter;
	}

	/**
	 * Gibt an, ob diese Verbindung aktiv ist, also derzeit eine Kommunikation über diese Sender /
	 * Empfaenger denkbar ist.
	 *
	 * @return <code>true</code>, wenn man ueber diese Verbindung senden und empfangen kann. Sonst
	 * <code>false</code>.
	 */
	public boolean istAktiv () {
		return super.verbindungAktiv() && interpreter.verbindungAktiv();
	}

	/**
	 * Gibt den Namen der Verbindung aus.
	 *
	 * @return Der Name der Verbindung.
	 */
	public String getName () {
		return name;
	}

	/**
	 * Gibt den Interpreter der Verbindung aus.
	 *
	 * @return Der Interpreter der Verbindung.
	 */
	public NetzwerkInterpreter getInterpreter () {
		return interpreter;
	}

	/**
	 * Fuegt dieser Verbindung einen Empfänger zu, der ab sofort bei allen
	 * empfangenen Nachrichten informiert wird.
	 * @param e ein neuer Empfänger, der ab sofort bei jeder gesendeten
	 * 				Nachricht informiert wird.
	 */
	public void empfaengerHinzufuegen(Empfaenger e) {
		this.interpreter.empfaengerHinzufuegen(e);
	}
	
	/**
	 * Beendet die Verbindung zum Kommunikationspartner.
	 * Anschließend werden alle Ressourcen der Verbindung wieder freigegeben.
	 */
	@Override
	public void beendeVerbindung () {
		super.beendeVerbindung();
		interpreter.quitCommunication();
	}

	public String getRemoteIP () {
		return ip;
	}
}
