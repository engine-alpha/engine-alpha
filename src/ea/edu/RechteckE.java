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

import ea.Rechteck;

/**
 * Ein einfaches "edu"-Dummmie-Rechteck.
 * 
 * @author Michael Andonie
 */
public class RechteckE extends Rechteck {
	private static final long serialVersionUID = -3793677493595048830L;

	/**
	 * Konstruktor eines "edu"-Rechtecks. Erstellt selbiges und macht es im
	 * "edu"-Standartfenster sichtbar.
	 */
	public RechteckE() {
		super(50, 200, 200, 130);
		farbeSetzen("Rot");
		FensterE.getFenster().wurzel.add(this);
	}
}
