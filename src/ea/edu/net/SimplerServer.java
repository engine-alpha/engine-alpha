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

import ea.Server;

/**
 * Ein <code>SimplerServer</code> ist die vereinfachte Version der Serverseite
 * der Netzwerkkommunikation in der EDU-Variante der Engine. Damit ist er Teil der
 * simpelsten Netzwerkkommunikation der Engine Alpha. <br /> <br />
 * 
 * <ul>
 * <li>Ein Simpler Server kann mit beliebig vielen simplen Clients gleichzeitig kommunizieren</li>
 * <li>Ein Simpler Server kann <i>nicht</i> zwischen seinen Clients unterscheiden. Er kann nur
 * die selbe Nachricht an alle Clients senden (sog. Broadcasts).</li>
 * <li>Die Kernfunktionalität ist in der Klasse <code>SimplerNetzwerkAdapter</code> beschrieben.</li>
 * </ul>
 * @author andonie
 * @see ea.edu.net.SimplerClient
 * @see ea.edu.net.SimplerNetzwerkAdapter
 */
public class SimplerServer extends SimplerNetzwerkAdapter {

	/**
	 * Der Server, über den diese Wrapper-Klasse arbeitet.
	 */
	private final Server server;

	/**
	 * Erstellt einen neuen simplen Server.
	 * @param port			Der Port, unter dem der Server mit den Clients kommunizieren will.
	 * 						Muss identisch mit dem des Servers sein. <b>Achtung:</b> Es ist
	 * 						empfehlenswert einen Port > 1024 zu wählen, da die darunter stehenden
	 * 						Ports für feste Dienste (wie Telnet / HTTP) reserviert sind (sog.
	 * 						<i>well known ports</i>).
	 */
	public SimplerServer (int port) {
		this.server = new Server(port);
		server.globalenEmpfaengerSetzen(messageUpdater);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void senden (String string) {
		server.sendeString(string);
	}
}
