/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
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

package ea.edu.net;

import java.util.concurrent.ConcurrentLinkedQueue;

import ea.Empfaenger;
import ea.Server;

public abstract class SimplerNetzwerkAdapter {

	private final ConcurrentLinkedQueue<String> messages
		= new ConcurrentLinkedQueue<String>();
	
	protected final Empfaenger messageUpdater = new Empfaenger(){
		@Override
		public void empfangeString(String string) {
			messages.add(string);
			synchronized(messages) {
				messages.notify();
			}
		}
		@Override
		public void empfangeInt(int i) {}
		@Override
		public void empfangeByte(byte b) {}
		@Override
		public void empfangeDouble(double d) {}
		@Override
		public void empfangeChar(char c) {}
		@Override
		public void empfangeBoolean(boolean b) {}
		@Override
		public void verbindungBeendet() {}
	};
	
	public synchronized String lauschen() {
		if(messages.isEmpty()) {
			try {
				synchronized(messages) {
					messages.wait();
				}
			} catch (InterruptedException e) {
				//
			}
			if(!messages.isEmpty())
				return messages.poll();
			else
				return null;
		}
		return messages.poll();
	}
	
	/**
	 * Versendet eine Nachricht.
	 * @param string	Die zu sendende Nachricht
	 * 					als String.
	 */
	public abstract void senden(String string);
}
