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

package ea.edu;

import ea.Kreis;

/**
 * Ein einfacher "edu"-Dummie-Kreis.
 * 
 * @author Michael Andonie
 */
public class KreisE extends Kreis {
	private static final long serialVersionUID = 4781036275816114585L;

	/**
	 * Konstruktor eines "edu"-Kreises.<br />
	 * Erstellt standartmaessig einen solchen und macht ihn im "edu"-Standartfenster sichtbar.
	 */
	public KreisE() {
		super(300, 200, 100);
		farbeSetzen("Blau");
		FensterE.getFenster().wurzel.add(this);
	}
}
