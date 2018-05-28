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

import ea.internal.ano.API;

/**
 * FallListener kann darauf onKeyDown, das das mit ihr angemeldete Actor-Objekt ueber
 * <code>WorldHandler</code> faellt und einen gewissen Maximalwert erreicht. Dann wird ihre Methode
 * <code>onFall()</code> aufgerufen.<br />
 *
 * @author Michael Andonie
 */
public interface FallListener {
	/**
	 * Diese Methode wird aufgerufen, <b>solange</b> das fallende Actor-Objekt unter der mit diesem
	 * Listener angemeldeten mindesthoehe faellt.<br /> Das heisst, wird das Fallen im
	 * problematischen bereich hierin <b>nicht behoben</b>, so wird diese Methode wieder und wieder
	 * aufgerufen, bis das Objekt nicht mehr im gefaehrlichen Bereich ist.
	 */
	@API
	void onFall();
}
