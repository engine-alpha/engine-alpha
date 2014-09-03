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
 * Ein FallDummy ist das <i>Null-Objekt</i> fuer einen Gravitator-Listener und wird intern solange
 * gehalten, bis ein eigenes FallReagierbar-Interface genutz wird.
 * 
 * @author Michael Andonie
 */
public class FallDummy
implements FallReagierbar {

	/**
	 * <i>Singleton</i>-Referenz auf die eine Instanz.
	 */
	private static FallDummy instance = null;
	
    /**
     * Ueberschrieben Reaktionsmethode. Hierin passiert GAR NICHTS.
     */
    @Override
    public void fallReagieren() {
        //Nichts tun
    }

    /**
     * <i>Singleton</i>-Getter-Methode für den Dummy.
     * @return	Die eine existente Instanz des <code>FallDummy</code>-Objekts.
     */
	public static FallReagierbar getDummy() {
		return instance == null ? instance = new FallDummy() : instance;
	}

}
