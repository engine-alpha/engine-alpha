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

import java.io.*;

/**
 * Diese Klasse wird verwendet, um Daten ueber eine Server-Client-Verbindung
 * zu <b>senden</b>. Sie bietet eine einfache <i>Schnittstelle</i> zur Kommunikation,
 * da Exceptions etc. nicht zu beachten sind.<br />
 * Ein Sender bekommt also einen <code>OutputStream</code>, ueber den die Kommunikation verlaeuft.
 * @author Andonie
 */
public class Sender {
	
	/**
	 * Gibt an, ob noch eine Verbindung zum anderen Ende der
	 * Kommunikation besteht.
	 */
	private boolean active;
	
	/**
	 * Der Writer, ueber den geschrieben wird.
	 */
	private final BufferedWriter writer;
	
	/**
	 * Konstruktur erstellt den Sender.
	 * @param os Der OutputStream, ueber den ab sofort gesendet werden soll.
	 */
	public Sender(BufferedWriter bw) {
		writer = bw;
		active = true;
	}
	
	/**
	 * Gibt an, ob die Verbindung ueber diesen Sender
	 * noch aktiv ist.
	 * @return	<code>true</code>, wenn der Sender dem Kommunikationspartner
	 * 			(noch) nicht gesendet hat, dass die Verbindung beendet wird.
	 * 			Sonst <code>false</code>.
	 */
	public boolean verbindungAktiv() {
		return active;
	}
	
	/**
	 * Interne Routine. Sendet eine Nachricht, wobei wesentliche
	 * Eigenschaften geprueft werden und Fehler ausgegeben werden.
	 * @param s	Der String, der uebertragen werden soll.
	 */
	private void sende(String s) {
		if(!active) {
			System.err.println("Kann nach dem schlieﬂen nicht mehr senden.");
			return;
		}
		try {
			writer.write(s);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			System.err.println("Es gab einen internen Fehler beim Schreiben.");
		}
	}
	
	public void sendeString(String s) {
		sende("s"+s);
	}
	
	public void sendeInt(int i) {
		sende("i"+Integer.toString(i));
	}
	
	public void sendeByte(byte b) {
		sende("b"+Byte.toString(b));
	}
	
	public void sendeDouble(double d) {
		sende("d"+Double.toString(d));
	}
	
	public void sendeChar(char c) {
		sende("c"+Character.toString(c));
	}
	
	public void sendeBoolean(boolean b) {
		sende("k"+Boolean.toString(b));
	}
	
	public void beendeVerbindung() {
		sende("xq");
	}
}
