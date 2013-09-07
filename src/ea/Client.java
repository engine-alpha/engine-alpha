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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Diese Klasse ermoeglicht das Aufbauen einer Client-Verbindung zu einem
 * Server.
 * @author Andonie
 */
public class Client 
extends Thread 
implements Empfaenger {
	
	/**
	 * Der socket, ueber den die Verbindung aufgebaut wird.
	 */
	private Socket socket;
	
	/**
	 * Die gewünschte Ziel-IP-Adresse des Socket
	 */
	private final String ipAdresse;
	
	/**
	 * Der Name, mit dem sich der Client beim
	 * Server vorstellt.
	 */
	private final String name;
	
	/**
	 * Der Port des Socket.
	 */
	private final int port;
	
	/**
	 * Diese Verbindung ist ungleich null, sobald die Verbindung mit dem
	 * Server aufgebaut wurde.
	 */
	private NetzwerkVerbindung verbindung;
	
	public Client(String name, String ipAdresse, int port) {
		this.setDaemon(true);
		this.name = name;
		this.ipAdresse = ipAdresse;
		this.port = port;
		start();
	}
	
	public Client(String ipAdresse, int port) {
		this("Unbenannter Client", ipAdresse, port);
	}
	
	@Override
	public void run() {
		try {
			socket = new Socket(ipAdresse, port);
			
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
			InputStream is = socket.getInputStream();
			
			bw.write("xe"+name);
			bw.newLine();
			bw.flush();
			
			//Set up interpreter
			NetzwerkInterpreter interpreter = new NetzwerkInterpreter(
					new BufferedReader(new InputStreamReader(is)));
			interpreter.empfaengerHinzufuegen(this);
			
			NetzwerkVerbindung vb = new NetzwerkVerbindung(
					name, new Sender(bw), interpreter);
			verbindung = vb;
		} catch (UnknownHostException e) {
			System.err.println("Konnte die IP-Adresse nicht zuordnen...");
		} catch (IOException e) {
			System.err.println("Es gab Input/Output - Schwierigkeiten. Sind ausreichende Rechte fuer"
					+ " Internet etc. vorhanden?");
		}
	}
	
	public void sendeString(String string) {
		if(verbindung == null) {
			return;
		}
		verbindung.getSender().sendeString(string);
	}
	
	public void sendeInt(int i) {
		if(verbindung == null) {
			return;
		}
		verbindung.getSender().sendeInt(i);
	}
	
	public void sendeByte(byte b) {
		if(verbindung == null) {
			return;
		}
		verbindung.getSender().sendeByte(b);
	}
	
	public void sendeDouble(double d) {
		if(verbindung == null) {
			return;
		}
		verbindung.getSender().sendeDouble(d);
	}
	
	public void sendeChar(char c) {
		if(verbindung == null) {
			return;
		}
		verbindung.getSender().sendeChar(c);
	}
	
	public void sendeBoolean(boolean b) {
		if(verbindung == null) {
			return;
		}
		verbindung.getSender().sendeBoolean(b);
	}
	
	public void beendeVerbindung() {
		if(verbindung == null) {
			return;
		}
		verbindung.getSender().beendeVerbindung();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void empfangeString(String string) {
		System.out.println("EMPFANGEN: " + string);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void empfangeInt(int i) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void empfangeByte(byte b) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void empfangeDouble(double d) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void empfangeChar(char c) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void empfangeBoolean(boolean b) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void verbindungBeendet() {
		// TODO Auto-generated method stub
		
	}
}
