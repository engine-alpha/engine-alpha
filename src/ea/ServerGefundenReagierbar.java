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
 * Dieses Interface benachrichtigt ein Listener-Objekt, wenn eine Netzwerkverbindung zu einem Server
 * hergestellt werden kann, weil seine IP nun bekannt ist.
 */
public interface ServerGefundenReagierbar {
	/**
	 * Diese Methode muss überschrieben werden, um eine Benachrichtigung zu erhalten, wenn ein
	 * Server im lokalen Netzwerk gefunden wurde.
	 *
	 * @param ip
	 * 		IP des Servers im lokalen Netzwerk
	 */
	public void serverGefunden (String ip);
}