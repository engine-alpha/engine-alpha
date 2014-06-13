/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
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
 * Sorgt dafür, dass auf Mausbewegungen reagiert werden kann.
 *
 * @author Niklas Keller <me@kelunik.com>
 */
public interface MausBewegungReagierbar {
	/**
	 * Wird immer aufgerufen, wenn die Maus bewegt wurde.
	 *
	 * @param dx Delta-x
	 * @param dy Delta-y
	 */
	public void mausBewegt(int dx, int dy);
}
