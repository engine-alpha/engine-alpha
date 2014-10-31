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

import ea.Empfaenger;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Diese abstrakte Klasse beschreibt die allgemeinen Eigenschaften
 * einer <i>simplen</i> Netzwerkklasse. <i>Simpel</i> bedeutet in 
 * diesem Zusammenhang, dass es um eine Vereinfachung im Sinne der
 * EDU-Version der Engine handelt.<br /><br />
 * 
 * Der simple Netzwerkadapter arbeitet <i>nur auf Basis von Strings</i> kann:
 * <ul>
 * <li> Jederzeit Strings senden</li>
 * <li> Den nächsten noch nicht gelesenen String ausgeben (bzw. auf den nächsten
 * zu String vom Kommunikationspartner warten).</li>
 * </ul>
 * @author andonie
 *
 */
public abstract class SimplerNetzwerkAdapter {

	/**
	 * Die Liste, die die noch nicht abgearbeiteten Nachrichten speichert.
	 */
	private final ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();

	/**
	 * Der Standard-Empfänger für beide Seiten der simplen Kommunikation.
	 * Empfangene Strings werden in die Queue <code>messages</code> eingereiht.
	 * Andere Datentypen sollten nicht empfangen werden; die entsprechenden
	 * Methoden sind daher leer.
	 */
	protected final Empfaenger messageUpdater = new Empfaenger() {
		@Override
		public void empfangeString (String string) {
			messages.add(string);
			synchronized (messages) {
				messages.notify();
			}
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
		}
	};

	/**
	 * Diese Methode gibt die <i>nächste ungelesene String-Nachricht</i> aus.
	 * Das bedeutet:<br/>
	 * <ul>
	 * <li>Gibt es bereits (mindestens) eine noch nicht "abgehörte" Nachricht, wird
	 * einfach die nächste in der Schlange ausgegeben.</li>
	 * <li>Gibt es derzeit keine neue Nachricht, wird solange gewartet, bis eine
	 * neue Nachricht vom Kommunikationspartner eingetroffen ist.</li>
	 * </ul>
	 * @return die nächste Nachricht des Kommunikationspartners.
	 */
	public final synchronized String lauschen () {
		if (messages.isEmpty()) {
			try {
				synchronized (messages) {
					messages.wait();
				}
			} catch (InterruptedException e) {
				//
			}
			if (!messages.isEmpty()) {
				return messages.poll();
			} else {
				return null;
			}
		}
		return messages.poll();
	}

	/**
	 * Versendet eine Nachricht.
	 *
	 * @param string
	 * 		Die zu sendende Nachricht als String.
	 */
	public abstract void senden (String string);
}
