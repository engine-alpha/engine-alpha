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

import ea.internal.ani.Animierer;

/**
 * AnimationsEndeReagierbar kann auf das Ende einer Animation reagieren und entsprechend der Lage
 * etwas tun.
 *
 * @author Michael Andonie
 */

public interface AnimationsEndeReagierbar {
	/**
	 * Diese Methode wird einmal dann aufgerufen, wenn die Animation zu Ende ist.<br /> Dadurch kann
	 * das Ende der Animation in Programmiercode gefestigt und speziell genommen werden.
	 *
	 * @param animierer
	 * 		Der Animierer, der sich gerade beendet hat.
	 */
	public abstract void endeReagieren (Animierer animierer);
}