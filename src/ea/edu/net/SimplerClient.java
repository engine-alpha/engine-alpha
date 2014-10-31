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

package ea.edu.net;

import ea.Client;

/**
 * Ein <code>SimpleClient</code> ist die vereinfachte Version der Clientseite
 * der Netzwerkkommunikation in der EDU-Variante der Engine. Damit ist er Teil der
 * simpelsten Netzwerkkommunikation der Engine Alpha.
 * 
 * <ul>
 * <li>Ein Simpler Client kann nur mit einem Simplen Server kommunizieren.</li>
 * <li>Die Kernfunktionalität ist in der Klasse <code>SimplerNetzwerkAdapter</code> beschrieben.</li>
 * </ul>
 * @author andonie
 *
 * @see ea.edu.net.SimplerServer
 * @see ea.edu.net.SimplerNetzwerkAdapter
 */
public class SimplerClient extends SimplerNetzwerkAdapter {

	/**
	 * Der Client, uber den der Client kommuniziert.
	 */
	private Client client;
	
	/**
	 * Interner Counter für die kanonische Standard-Benennung simpler Clients.
	 */
	private static int clientcounter=0;

	/**
	 * Erstellt ein neues Objekt der Klasse <code>SimplerClient</code>. Das
	 * Objekt ist - sollten alle Eingaben sinnvoll sein - direkt startklar.
	 * @param name			Der Name des Clients. Kann beliebig gewählt werden.
	 * @param ipAdresse		Die IP Adresse des Servers, mit dem sich dieser simple Client
	 * 						verbinden soll. (z.B. "198.162.0.2")
	 * @param port			Der Port, unter dem der Client mit dem Server kommunizieren will.
	 * 						Muss identisch mit dem des Servers sein. <b>Achtung:</b> Es ist
	 * 						empfehlenswert einen Port > 1024 zu wählen, da die darunter stehenden
	 * 						Ports für feste Dienste (wie Telnet / HTTP) reserviert sind (sog.
	 * 						<i>well known ports</i>).
	 */
	public SimplerClient (String name, String ipAdresse, int port) {
		client = new Client(name, ipAdresse, port);
		client.empfaengerHinzufuegen(messageUpdater);
		clientcounter++;
	}

	/**
	 * Erstellt ein neues Objekt der Klasse <code>SimplerClient</code> mit automatischem
	 * Client-Namen. Das Objekt ist - sollten alle Eingaben sinnvoll sein - direkt startklar.
	 * @param ipAdresse		Die IP Adresse des Servers, mit dem sich dieser simple Client
	 * 						verbinden soll. (z.B. "198.162.0.2")
	 * @param port			Der Port, unter dem der Client mit dem Server kommunizieren will.
	 * 						Muss identisch mit dem des Servers sein. <b>Achtung:</b> Es ist
	 * 						empfehlenswert einen Port > 1024 zu wählen, da die darunter stehenden
	 * 						Ports für feste Dienste (wie Telnet / HTTP) reserviert sind (sog.
	 * 						<i>well known ports</i>).
	 */
	public SimplerClient(String ipAdresse, int port) {
		this("Simple Client " + clientcounter, ipAdresse, port);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void senden (String string) {
		client.sendeString(string);
	}
}
