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

/**
 * Dieses Interface benachrichtigt ein Listener-Objekt, wenn eine Netzwerkverbindung zu diesem
 * Server erfolgreich hergestellt werden konnte.
 */
public interface VerbindungHergestelltReagierbar {
	/**
	 * Wird aufgerufen, wenn eine Verbindung zu diesem Server erfolgreich hergestellt werden
	 * konnte.
	 *
	 * @param ip
	 * 		IP des Clients
	 */
	public void verbindungHergestellt (String ip);
}
