/* Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
 *
 * Copyright (C) 2011  Michael Andonie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package ea;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Server-Klasse für einfache Verwendung von Kommunikation.
 * @author Andonie
 *
 */
public class Server
extends Thread
implements Empfaenger {
	
	/**
	 * Diese Liste speichert alle <b>aktiven></b> Netzwerkverbindungen.
	 */
	private final CopyOnWriteArrayList<NetzwerkVerbindung> verbindungen = 
			new CopyOnWriteArrayList<NetzwerkVerbindung>();
	
	/**
	 * Die Queue, in der Verbindungen liegen, um vom API-Nutzer zusätzlichen Empfaengern
	 * zugeordnet zu werden sowie die Sender auszugeben.<br />
	 * Diese Struktur wird benutzt, um den eigentliche (ggf. vorhandenen) Warteprozess auf einem
	 * dem API-Nutzer zugänglichen Thread zu simulieren.<br /><br />
	 * Damit ist diese Referenz das Verbindungsglied fuer eine <i>Consumer/Producer - Struktur</i>.
	 */
	private final Queue<NetzwerkVerbindung> waitingQueue = new LinkedList<NetzwerkVerbindung>();
	
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
	 * Der globale Empfaenger bekommt ueber Methodenaufrufe 
	 * Benachrichtigungen ueber jede Kommunikation an 
	 * diesen Server. <b>Es sei denn, jemand ueberschreibt diese
	 * Klasse und die Empfaenger-Methoden</b>.
	 */
	private Empfaenger globalerEmpfaenger;
	
	/**
	 * Erstellt einen neuen Server.
	 * @param port Der Port, auf dem dieser Server auf anfragende
	 * <code>Client</code>s antworten soll.
	 */
	public Server(int port) {
		this.setDaemon(true);
		this.port = port;
		start();
	}
	
	/**
	 * Ueberschriebene run-Methode. Hierin wird auf neue Verbindungen gewartet und
	 * diese werden weiterverarbeitet.
	 */
	@Override
	public void run() {
		try {
			this.socket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Konnte keinen Server aufstellen. Ausreichend Rechte"
					+ " vorhanden?\n");
			 e.printStackTrace();
		}
		while(!isInterrupted() && active) {
			try {
				Socket got = socket.accept();
				
				BufferedReader br = new BufferedReader(
						new InputStreamReader(got.getInputStream()));
				OutputStream os = got.getOutputStream();
				//Check for initial message.
				String init = br.readLine();
				if(init.length() < 1 || !init.startsWith("xe")) {
					System.err.println("Client gefunden! Dieser hat sich aber falsch angemeldet! "
							+ "Kommt er sicher von der EA?");
					continue;
				}
				
				String name;
				if(init.length() == 1) {
					System.err.println("Client hat sich angemeldet, jedoch keinen Namen hinterlassen. "
							+ "Verbindung wird trotzdem aufgebaut.");
					name = "";
				} else {
					name = init.substring(2);
				}
				
				//Set up interpreter
				NetzwerkInterpreter interpreter = new NetzwerkInterpreter(br);
				interpreter.empfaengerHinzufuegen(this);
				
				NetzwerkVerbindung verbindung = new NetzwerkVerbindung(
						name, new Sender(new BufferedWriter(new OutputStreamWriter(os))), interpreter);
				
				waitingQueue.add(verbindung);
				verbindungen.add(verbindung);
				
				waitingQueue.notify();
			} catch (IOException e) {
				System.err.println("Beim Herstellen einer Verbindung ist ein Input/Output - "
						+ "Fehler aufgetreten.");
			}
		}
	}
	
	/**
	 * Gibt die nächste Verbindung mit diesem Server aus, die noch nicht 
	 * ausgegeben wurde. Gibt es keine Verbindung, die noch nicht ueber diese Methode
	 * ausgegeben wurde, so <b>hält der Thread solange an, bis eine neue Verbindung
	 * entstanden ist und diese zurückgegeben werden kann</b>.
	 * @return	Die älteste, noch nicht über diese Methode zurückgegebene Verbindung.
	 * 			Diese Methode hat stets einen Rückgabewert <code>!= null</code>. Nötigenfalls
	 * 			hält sie so lange wie nötig den laufenden Thread an, bis eine Verbindung
	 * 			zurückgegeben werden kann.
	 */
	public NetzwerkVerbindung naechsteVerbindungAusgeben() {
		if(waitingQueue.isEmpty()) {
			try {
				waitingQueue.wait();
			} catch (InterruptedException e) {
				//
			}
		}
		return waitingQueue.poll();
	}
	
	/**
	 * Setzt einen <b>globalen Empfaenger</b> fuer diesen Server. Der
	 * globale Empfaenger wird ueber jede Nachricht an diesen Server informiert,
	 * immer. Bei dem <b>Beenden</b> einer einzelnen Verbindung ist allerdings
	 * vorsichtig geboten. Nicht unbedingt muss zu diesem Zeitpunkt <i>jede 
	 * Verbindung</i> bereits aufgelöst sein.
	 * @param e	Der neue globale Empfaenger.
	 */
	public void globalenEmpfaengerSetzen(Empfaenger e) {
		this.globalerEmpfaenger = e;
	}
	
	public void sendeString(String string) {
		for(NetzwerkVerbindung v : verbindungen) {
			v.getSender().sendeString(string);
		}
	}
	
	public void sendeInt(int i) {
		for(NetzwerkVerbindung v : verbindungen) {
			v.getSender().sendeInt(i);
		}
	}
	
	public void sendeByte(byte b) {
		for(NetzwerkVerbindung v : verbindungen) {
			v.getSender().sendeByte(b);
		}
	}
	
	public void sendeDouble(double d) {
		for(NetzwerkVerbindung v : verbindungen) {
			v.getSender().sendeDouble(d);
		}
	}
	
	public void sendeChar(char c) {
		for(NetzwerkVerbindung v : verbindungen) {
			v.getSender().sendeChar(c);
		}
	}
	
	public void sendeBoolean(boolean b) {
		for(NetzwerkVerbindung v : verbindungen) {
			v.getSender().sendeBoolean(b);
		}
	}
	
	public void beendeVerbindung() {
		for(NetzwerkVerbindung v : verbindungen) {
			v.getSender().beendeVerbindung();
		}
	}

	@Override
	public void empfangeString(String string) {
		if(globalerEmpfaenger != null)
			globalerEmpfaenger.empfangeString(string);
	}

	@Override
	public void empfangeInt(int i) {
		if(globalerEmpfaenger != null)
			globalerEmpfaenger.empfangeInt(i);
	}

	@Override
	public void empfangeByte(byte b) {
		if(globalerEmpfaenger != null)
			globalerEmpfaenger.empfangeByte(b);
	}

	@Override
	public void empfangeDouble(double d) {
		if(globalerEmpfaenger != null)
			globalerEmpfaenger.empfangeDouble(d);
	}

	@Override
	public void empfangeChar(char c) {
		if(globalerEmpfaenger != null)
			globalerEmpfaenger.empfangeChar(c);
	}

	@Override
	public void empfangeBoolean(boolean b) {
		if(globalerEmpfaenger != null)
			globalerEmpfaenger.empfangeBoolean(b);
	}

	@Override
	public void verbindungBeendet() {
		if(globalerEmpfaenger != null)
			globalerEmpfaenger.verbindungBeendet();
	}

	
}
