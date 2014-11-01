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
import ea.internal.net.DiscoveryServer;
import ea.internal.util.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Server-Klasse für einfache Verwendung von Kommunikation.<br /><br />
 * 
 * Lauscht stets nach Verbindungsaufnahmen durch beliebig viele Clients.<br />
 * Sendet stets an alle Verbundenen Teilnehmer.
 * 
 * @see ea.Client
 *
 * @author Michael Andonie, Niklas Keller <me@kelunik.com>
 */
public class Server extends Thread implements Empfaenger, SenderInterface {
	/**
	 * Diese Liste speichert alle <b>aktiven></b> Netzwerkverbindungen.
	 */
	private final CopyOnWriteArrayList<NetzwerkVerbindung> verbindungen = new CopyOnWriteArrayList<>();

	/**
	 * Die Queue, in der Verbindungen liegen, um vom API-Nutzer zusätzlichen Empfaengern zugeordnet
	 * zu werden sowie die Sender auszugeben.<br /> Diese Struktur wird benutzt, um den eigentliche
	 * (ggf. vorhandenen) Warteprozess auf einem dem API-Nutzer zugänglichen Thread zu
	 * simulieren.<br /><br /> Damit ist diese Referenz das Verbindungsglied fuer eine
	 * <i>Consumer/Producer - Struktur</i>.
	 */
	private final Queue<NetzwerkVerbindung> waitingQueue = new LinkedList<>();

	/**
	 * Der Port des Servers.
	 */
	private final int port;

	/**
	 * Der Server-Socket, über den die Streams gezogen werden.
	 */
	private ServerSocket socket;

	/**
	 * Gibt an, ob der Server noch aktiv ist.
	 */
	private boolean active = true;

	/**
	 * Der globale Empfaenger bekommt ueber Methodenaufrufe Benachrichtigungen ueber jede
	 * Kommunikation an diesen Server. <b>Es sei denn, jemand ueberschreibt diese Klasse und die
	 * Empfaenger-Methoden</b>.
	 */
	private Empfaenger globalerEmpfaenger;

	/**
	 * Listener, der ausgeführt wird, sobald ein Client sich verbindet.
	 */
	private VerbindungHergestelltReagierbar verbindungHergestelltReagierbar;

	/**
	 * Ob der Netzwerk-Teilnehmer die empfangenen Nachrichten an alle Clients weiterleitens soll
	 */
	private boolean broadcast;

	/**
	 * Erstellt einen neuen Server.
	 *
	 * @param port
	 * 		Der Port, auf dem dieser Server auf anfragende <code>Client</code>s antworten soll.
	 */
	public Server (int port) {
		this.port = port;
		this.setDaemon(true);
		this.start();
	}

	/**
	 * Überschriebene run-Methode. Hierin wird auf neue Verbindungen gewartet und diese werden
	 * weiterverarbeitet.
	 */
	@Override
	public void run () {
		try {
			this.socket = new ServerSocket(port);
		} catch (IOException e) {
			Logger.error("Konnte keinen Server aufstellen. Ausreichend Rechte vorhanden?\n");
			e.printStackTrace();
		}

		while (!isInterrupted() && active) {
			try {
				Socket got = socket.accept();

				// Stelle sicher, dass der Socket auch wieder geschlossen wird.
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run () {
						beendeVerbindung();
					}
				});

				BufferedReader br = new BufferedReader(new InputStreamReader(got.getInputStream()));
				OutputStream os = got.getOutputStream();
				// Check for initial message.
				String init = br.readLine();
				if (init.length() < 1 || !init.startsWith("xe")) {
					Logger.error("Client gefunden! Dieser hat sich aber falsch angemeldet! " + "Kommt er sicher von der EA?");
					continue;
				}

				String name;
				if (init.length() == 1) {
					Logger.error("Client hat sich angemeldet, jedoch keinen Namen hinterlassen. " + "Verbindung wird trotzdem aufgebaut.");
					name = "";
				} else {
					name = init.substring(2);
				}

				String ip = got.getInetAddress().getHostAddress();

				// set up interpreter
				NetzwerkInterpreter interpreter = new NetzwerkInterpreter(ip, this, br);
				interpreter.empfaengerHinzufuegen(this);

				final NetzwerkVerbindung verbindung = new NetzwerkVerbindung(name, ip, new BufferedWriter(new OutputStreamWriter(os)), interpreter);

				waitingQueue.add(verbindung);
				verbindungen.add(verbindung);

				verbindung.getInterpreter().empfaengerHinzufuegen(new Empfaenger() {
					@Override
					public void empfangeString (String string) {
					}

					@Override
					public void empfangeInt (int i) {
					}

					@Override
					public void empfangeByte (byte b) {
					}

					@Override
					public void empfangeDouble (double d) {
					}

					@Override
					public void empfangeChar (char c) {
					}

					@Override
					public void empfangeBoolean (boolean b) {
					}

					@Override
					public void verbindungBeendet () {
						verbindungen.remove(verbindung);
						waitingQueue.remove(verbindung);
					}
				});

				if (verbindungHergestelltReagierbar != null) {
					verbindungHergestelltReagierbar.verbindungHergestellt(ip);
				}

				synchronized (waitingQueue) {
					waitingQueue.notifyAll();
				}
			} catch (IOException e) {
				Logger.error("Beim Herstellen einer Verbindung ist ein Input/Output - Fehler aufgetreten.");
			}
		}
	}

	/**
	 * Setzt, ob der Teilnehmer empfangene Nachrichten an alle anderen Clients weiterleitet.
	 *
	 * @param broadcast
	 * 		<code>true</code>, falls der Teilnehmer die Nachrichten verteilen soll, sonst
	 * 		<code>false</code>.
	 */
	public void setBroadcast (boolean broadcast) {
		this.broadcast = broadcast;
	}

	/**
	 * Gibt an, ob der Teilnehmer empfangene Nachrichten an alle anderen Clients weiterleitet.
	 *
	 * @return <code>true</code>, falls der Teilnehmer die Nachrichten verteilt, sonst
	 * <code>false</code>.
	 */
	public boolean isBroadcasting () {
		return this.broadcast;
	}

	/**
	 * Gibt die Liste der Netzwerkverbindungen aus, die am diesem Server liegen.
	 * @return	Die Liste aller
	 */
	public CopyOnWriteArrayList<NetzwerkVerbindung> getVerbindungen () {
		return this.verbindungen;
	}

	/**
	 * Setze den Listener, der informiert wird, wenn ein Client sich verbindet.
	 */
	public void setVerbindungHergestelltReagierbar (VerbindungHergestelltReagierbar listener) {
		verbindungHergestelltReagierbar = listener;
	}

	/**
	 * Gibt die nächste Verbindung mit diesem Server aus, die noch nicht ausgegeben wurde. Gibt es
	 * keine Verbindung, die noch nicht ueber diese Methode ausgegeben wurde, so <b>hält der Thread
	 * solange an, bis eine neue Verbindung entstanden ist und diese zurückgegeben werden kann</b>.
	 *
	 * @return Die älteste, noch nicht über diese Methode zurückgegebene Verbindung. Diese Methode
	 * hat stets einen Rückgabewert <code>!= null</code>. Nötigenfalls hält sie so lange wie nötig
	 * den laufenden Thread an, bis eine Verbindung zurückgegeben werden kann.
	 */
	public NetzwerkVerbindung naechsteVerbindungAusgeben () {
		if (waitingQueue.isEmpty()) {
			try {
				synchronized (waitingQueue) {
					waitingQueue.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		

		return waitingQueue.poll();
	}

	/**
	 * Setzt einen <b>globalen Empfaenger</b> fuer diesen Server. Der globale Empfaenger wird ueber
	 * jede Nachricht an diesen Server informiert, immer. Bei dem <b>Beenden</b> einer einzelnen
	 * Verbindung ist allerdings vorsichtig geboten. Nicht unbedingt muss zu diesem Zeitpunkt
	 * <i>jede Verbindung</i> bereits aufgelöst sein.
	 *
	 * @param e
	 * 		Der neue globale Empfaenger.
	 */
	public void globalenEmpfaengerSetzen (Empfaenger e) {
		this.globalerEmpfaenger = e;
	}

	/**
	 * {@inheritDoc} Der Befehl wird an alle verbundenen Clients weitergegeben.
	 */
	@Override
	public void sendeString (String string) {
		for (NetzwerkVerbindung v : verbindungen) {
			v.sendeString(string);
		}
	}

	/**
	 * {@inheritDoc} Der Befehl wird an alle verbundenen Clients weitergegeben.
	 */
	@Override
	public void sendeInt (int i) {
		for (NetzwerkVerbindung v : verbindungen) {
			v.sendeInt(i);
		}
	}

	/**
	 * {@inheritDoc} Der Befehl wird an alle verbundenen Clients weitergegeben.
	 */
	@Override
	public void sendeByte (byte b) {
		for (NetzwerkVerbindung v : verbindungen) {
			v.sendeByte(b);
		}
	}

	/**
	 * {@inheritDoc} Der Befehl wird an alle verbundenen Clients weitergegeben.
	 */
	@Override
	public void sendeDouble (double d) {
		for (NetzwerkVerbindung v : verbindungen) {
			v.sendeDouble(d);
		}
	}

	/**
	 * {@inheritDoc} Der Befehl wird an alle verbundenen Clients weitergegeben.
	 */
	@Override
	public void sendeChar (char c) {
		for (NetzwerkVerbindung v : verbindungen) {
			v.sendeChar(c);
		}
	}

	/**
	 * {@inheritDoc} Der Befehl wird an alle verbundenen Clients weitergegeben.
	 */
	@Override
	public void sendeBoolean (boolean b) {
		for (NetzwerkVerbindung v : verbindungen) {
			v.sendeBoolean(b);
		}
	}

	/**
	 * {@inheritDoc} Der Befehl wird an alle verbundenen Clients weitergegeben.
	 */
	@Override
	public void beendeVerbindung () {
		if (!socket.isClosed()) {
			for (NetzwerkVerbindung v : verbindungen) {
				if(v.istAktiv()) v.beendeVerbindung();
			}
			try {
				socket.close();
			} catch (IOException e) {
				Logger.error("Konnte den Verbindungs-Socket nicht mehr schliessen.");
			}
		}
	}

	/**
	 * {@inheritDoc} Gibt die Nachricht an den globalen Empfänger weiter, sofern einer vorhanden
	 * ist.
	 *
	 * @param string
	 * 		Die Nachricht vom Client.
	 */
	@Override
	public void empfangeString (String string) {
		if (globalerEmpfaenger != null) {
			globalerEmpfaenger.empfangeString(string);
		}
	}

	/**
	 * {@inheritDoc} Gibt die Nachricht an den globalen Empfänger weiter, sofern einer vorhanden
	 * ist.
	 *
	 * @param i
	 * 		Die Nachricht vom Client.
	 */
	@Override
	public void empfangeInt (int i) {
		if (globalerEmpfaenger != null) {
			globalerEmpfaenger.empfangeInt(i);
		}
	}

	/**
	 * {@inheritDoc} Gibt die Nachricht an den globalen Empfänger weiter, sofern einer vorhanden
	 * ist.
	 *
	 * @param b
	 * 		Die Nachricht vom Client.
	 */
	@Override
	public void empfangeByte (byte b) {
		if (globalerEmpfaenger != null) {
			globalerEmpfaenger.empfangeByte(b);
		}
	}

	/**
	 * {@inheritDoc} Gibt die Nachricht an den globalen Empfänger weiter, sofern einer vorhanden
	 * ist.
	 *
	 * @param d
	 * 		Die Nachricht vom Client.
	 */
	@Override
	public void empfangeDouble (double d) {
		if (globalerEmpfaenger != null) {
			globalerEmpfaenger.empfangeDouble(d);
		}
	}

	/**
	 * {@inheritDoc} Gibt die Nachricht an den globalen Empfänger weiter, sofern einer vorhanden
	 * ist.
	 *
	 * @param c
	 * 		Die Nachricht vom Client.
	 */
	@Override
	public void empfangeChar (char c) {
		if (globalerEmpfaenger != null) {
			globalerEmpfaenger.empfangeChar(c);
		}
	}

	/**
	 * {@inheritDoc} Gibt die Nachricht an den globalen Empfänger weiter, sofern einer vorhanden
	 * ist.
	 *
	 * @param b
	 * 		Die Nachricht vom Client.
	 */
	@Override
	public void empfangeBoolean (boolean b) {
		if (globalerEmpfaenger != null) {
			globalerEmpfaenger.empfangeBoolean(b);
		}
	}
	
	/**
	 * Schließt die Verbindung mit dem Server.
	 */
	public void verbindungSchliessen () {
		if (!socket.isClosed()) {
			for(NetzwerkVerbindung verbindung : verbindungen) {
				verbindung.beendeVerbindung();
			}
			try {
				socket.close();
			} catch (IOException e) {
				Logger.error("Konnte den Verbindungs-Socket nicht mehr schliessen.");
			}
		}
	}

	/**
	 * {@inheritDoc} Gibt die Nachricht an den globalen Empfänger weiter, sofern einer vorhanden
	 * ist.<br /> <b>ACHTUNG:</b> Dies betrifft stets nur eine Verbindung. Es ist daher gut möglich,
	 * dass der Server trotzdem noch kommunizieren kann und muss - mit anderen Clients.
	 */
	@Override
	public void verbindungBeendet () {
		if (globalerEmpfaenger != null) {
			globalerEmpfaenger.verbindungBeendet();
		}
	}

	/**
	 * 
	 * @param sichtbar
	 */
	public void netzwerkSichtbarkeit (boolean sichtbar) {
		if (sichtbar) {
			DiscoveryServer.startServer();
		} else {
			DiscoveryServer.stopServer();
		}
	}
}
