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

import ea.edu.net.NetzwerkInterpreter;
import ea.internal.util.Logger;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Diese Klasse ermöglicht das Aufbauen einer Client-Verbindung zu einem Server.
 *
 * @author Michael Andonie
 */
public class Client extends Thread implements Empfaenger, SenderInterface {

	/**
	 * Die gewünschte Ziel-IP-Adresse des Socket
	 */
	private final String ipAdresse;

	/**
	 * Der Name, mit dem sich der Client beim Server vorstellt.
	 */
	private final String name;

	/**
	 * Der Port des Socket.
	 */
	private final int port;

	/**
	 * Der Socket, über den die Verbindung aufgebaut wird.
	 */
	private Socket socket;

	/**
	 * Diese Verbindung ist ungleich null, sobald die Verbindung mit dem Server aufgebaut wurde.
	 */
	private NetzwerkVerbindung verbindung;

	/**
	 * Falls Verbindungsversuch scheitert, wird diese Variable <code>true</code>.
	 */
	private boolean connectFailed;

	/**
	 * Erstellt einen neuen Client.
	 * @param ipAdresse	Die IP-Adresse des Servers, mit dem sich der Client verbinden soll.
	 * @param port		Der Port, an dem sich der Client mit dem Server verbinden soll.
	 */
	public Client (String ipAdresse, int port) {
		this("Unbenannter Client", ipAdresse, port);
	}

	/**
	 * Erstellt einen neuen Client.
	 * @param name		Der Name, mit dem sich der Client (im Hintergrund) dem Server vorstellt. Wird nur
	 * 					intern verwendet.
	 * @param ipAdresse	Die IP-Adresse des Servers, mit dem sich der Client verbinden soll.
	 * @param port		Der Port, an dem sich der Client mit dem Server verbinden soll.
	 */
	public Client (String name, String ipAdresse, int port) {
		this.setDaemon(true);
		this.name = name;
		this.ipAdresse = ipAdresse;
		this.port = port;
		start();
	}

	/**
	 * Die run-Methode des Threads baut eine Verbindung zum Server aus. Sobald dieser Thread
	 * erfolgreich abgeschlossen ist, kann die Verbindung zur Kommunikation genutzt werden.
	 */
	@Override
	public void run () {
		try {
			socket = new Socket(ipAdresse, port);

			// Stelle sicher, dass der Socket auch wieder geschlossen wird.
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run () {
					verbindungSchliessen();
				}
			});

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			InputStream is = socket.getInputStream();

			bw.write("xe" + name);
			bw.newLine();
			bw.flush();

			String ip = socket.getInetAddress().getHostAddress();

			// set up interpreter
			NetzwerkInterpreter interpreter = new NetzwerkInterpreter(ip, null, new BufferedReader(new InputStreamReader(is)));
			interpreter.empfaengerHinzufuegen(this);

			NetzwerkVerbindung vb = new NetzwerkVerbindung(name, ip, bw, interpreter);

			verbindung = vb;

			synchronized (this) {
				this.notifyAll();
			}
		} catch (UnknownHostException e) {
			Logger.error("Konnte die IP-Adresse nicht zuordnen...");
			connectFailed = true;
		} catch (IOException e) {
			Logger.error("Es gab Input/Output - Schwierigkeiten. Sind ausreichende Rechte fuer" + " Internet etc. vorhanden? Das System könnte die Netzwerkanfrage ablehnen.");
			connectFailed = true;
		}
	}

	/**
	 * Schließt die Verbindung mit dem Server.
	 */
	public void verbindungSchliessen () {
		if (!socket.isClosed()) {
			verbindung.beendeVerbindung();
			try {
				socket.close();
			} catch (IOException e) {
				Logger.error("Konnte den Verbindungs-Socket nicht mehr schliessen.");
			}
		}
	}

	/**
	 * Setzt den Empfänger, der über jede Nachricht an diesen Client informiert wird.
	 *
	 * @param e
	 * 		Der Empfaenger, and den alle Nachrichten an diesen Client weitergereicht werden sollen.
	 */
	public void empfaengerHinzufuegen (Empfaenger e) {
		warteAufVerbindung();
		this.verbindung.getInterpreter().empfaengerHinzufuegen(e);
	}

	/**
	 * Diese Methode <b>stellt sicher</b>, dass eine Verbindung mit dem Server besteht.<br /> Diese
	 * Methode friert den ausführenden Thread ein, wenn noch keine Verbindung besteht und endet
	 * erst, wenn die Verbindung aufgebaut wurde.
	 */
	public void warteAufVerbindung () {
		if (verbindung == null) {
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					Logger.warning("Achtung. Es könnte trotz warteAufVerbindung() noch " + "keine Verbindung bestehen, da der Warteprozess unterbrochen wurde.");
				}
			}
		}
		//Additional Waiting: 200 ms to ensure synchronization buffer on server side.
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {}
	}

	/**
	 * {@inheritDoc} Sendet, sofern die Verbindung zum Server bereits aufgebaut wurde. Sonst
	 * passiert <b>wird solange gewartet, bis der Client sich mit einem Server verbinden
	 * konnte.</b>.
	 */
	@Override
	public void sendeString (String string) {
		warteAufVerbindung();
		verbindung.sendeString(string);
	}

	/**
	 * {@inheritDoc} Sendet, sofern die Verbindung zum Server bereits aufgebaut wurde. Sonst
	 * passiert <b>wird solange gewartet, bis der Client sich mit einem Server verbinden
	 * konnte.</b>.
	 */
	@Override
	public void sendeInt (int i) {
		warteAufVerbindung();
		verbindung.sendeInt(i);
	}

	/**
	 * {@inheritDoc} Sendet, sofern die Verbindung zum Server bereits aufgebaut wurde. Sonst
	 * passiert <b>wird solange gewartet, bis der Client sich mit einem Server verbinden
	 * konnte.</b>.
	 */
	@Override
	public void sendeByte (byte b) {
		warteAufVerbindung();
		verbindung.sendeByte(b);
	}

	/**
	 * {@inheritDoc} Sendet, sofern die Verbindung zum Server bereits aufgebaut wurde. Sonst
	 * passiert <b>wird solange gewartet, bis der Client sich mit einem Server verbinden
	 * konnte.</b>.
	 */
	@Override
	public void sendeDouble (double d) {
		warteAufVerbindung();
		verbindung.sendeDouble(d);
	}

	/**
	 * {@inheritDoc} Sendet, sofern die Verbindung zum Server bereits aufgebaut wurde. Sonst
	 * passiert <b>wird solange gewartet, bis der Client sich mit einem Server verbinden
	 * konnte.</b>.
	 */
	@Override
	public void sendeChar (char c) {
		warteAufVerbindung();
		verbindung.sendeChar(c);
	}

	/**
	 * {@inheritDoc} Sendet, sofern die Verbindung zum Server bereits aufgebaut wurde. Sonst
	 * passiert <b>wird solange gewartet, bis der Client sich mit einem Server verbinden
	 * konnte.</b>.
	 */
	@Override
	public void sendeBoolean (boolean b) {
		warteAufVerbindung();
		verbindung.sendeBoolean(b);
	}

	/**
	 * {@inheritDoc} Sendet, sofern die Verbindung zum Server bereits aufgebaut wurde. Sonst
	 * passiert <b>wird solange gewartet, bis der Client sich mit einem Server verbinden
	 * konnte.</b>.
	 */
	@Override
	public void beendeVerbindung () {
		warteAufVerbindung();
		if (!verbindung.istAktiv()) {
			Logger.error("Die Verbindung zum Server wurde bereits beendet.");
		}
		verbindung.beendeVerbindung();
		try {
			socket.close();
		} catch (IOException e) {
			Logger.error("Konnte den Verbindungs-Socket nicht mehr schliessen.");
		}
	}

	/**
	 * {@inheritDoc} Diese Methode kann von einer anderen Klasse ueberschrieben werden.<br /> So
	 * wird man moeglichst einfach von neuen Nachrichten an den Client informiert. Natuerlich kann
	 * man auch direkt einen <code>Empfaenger</code> an diesem Client anmelden. Der Effekt ist
	 * derselbe.
	 *
	 * @see #empfaengerHinzufuegen(Empfaenger)
	 */
	@Override
	public void empfangeString (String string) {
		// To be overwritten
	}

	/**
	 * {@inheritDoc} Diese Methode kann von einer anderen Klasse ueberschrieben werden.<br /> So
	 * wird man moeglichst einfach von neuen Nachrichten an den Client informiert. Natuerlich kann
	 * man auch direkt einen <code>Empfaenger</code> an diesem Client anmelden. Der Effekt ist
	 * derselbe.
	 *
	 * @see #empfaengerHinzufuegen(Empfaenger)
	 */
	@Override
	public void empfangeInt (int i) {
		// To be overwritten
	}

	/**
	 * {@inheritDoc} Diese Methode kann von einer anderen Klasse ueberschrieben werden.<br /> So
	 * wird man moeglichst einfach von neuen Nachrichten an den Client informiert. Natuerlich kann
	 * man auch direkt einen <code>Empfaenger</code> an diesem Client anmelden. Der Effekt ist
	 * derselbe.
	 *
	 * @see #empfaengerHinzufuegen(Empfaenger)
	 */
	@Override
	public void empfangeByte (byte b) {
		// To be overwritten
	}

	/**
	 * {@inheritDoc} Diese Methode kann von einer anderen Klasse ueberschrieben werden.<br /> So
	 * wird man moeglichst einfach von neuen Nachrichten an den Client informiert. Natuerlich kann
	 * man auch direkt einen <code>Empfaenger</code> an diesem Client anmelden. Der Effekt ist
	 * derselbe.
	 *
	 * @see #empfaengerHinzufuegen(Empfaenger)
	 */
	@Override
	public void empfangeDouble (double d) {
		// To be overwritten
	}

	/**
	 * {@inheritDoc} Diese Methode kann von einer anderen Klasse ueberschrieben werden.<br /> So
	 * wird man moeglichst einfach von neuen Nachrichten an den Client informiert. Natuerlich kann
	 * man auch direkt einen <code>Empfaenger</code> an diesem Client anmelden. Der Effekt ist
	 * derselbe.
	 *
	 * @see #empfaengerHinzufuegen(Empfaenger)
	 */
	@Override
	public void empfangeChar (char c) {
		// To be overwritten
	}

	/**
	 * {@inheritDoc} Diese Methode kann von einer anderen Klasse ueberschrieben werden.<br /> So
	 * wird man moeglichst einfach von neuen Nachrichten an den Client informiert. Natuerlich kann
	 * man auch direkt einen <code>Empfaenger</code> an diesem Client anmelden. Der Effekt ist
	 * derselbe.
	 *
	 * @see #empfaengerHinzufuegen(Empfaenger)
	 */
	@Override
	public void empfangeBoolean (boolean b) {
		// To be overwritten
	}

	/**
	 * {@inheritDoc} Diese Methode kann von einer anderen Klasse ueberschrieben werden.<br /> So
	 * wird man moeglichst einfach von neuen Nachrichten an den Client informiert. Natuerlich kann
	 * man auch direkt einen <code>Empfaenger</code> an diesem Client anmelden. Der Effekt ist
	 * derselbe.
	 *
	 * @see #empfaengerHinzufuegen(Empfaenger)
	 */
	@Override
	public void verbindungBeendet () {
		// To be overwritten
	}

	public boolean verbindungGescheitert () {
		return connectFailed;
	}
}