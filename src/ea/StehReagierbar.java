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

package ea;

/**
 * Dieses Interface beschreibt einen Listener der Physik, der immer dann informiert wird, wenn das <code>Raum</code>-Objekt,
 * an dem er angemeldet wurde, nach dem Fall wieder sicher auf einem Passiv-Objekt steht.
 * 
 * @author Michael Andonie
 */
public interface StehReagierbar {

    /**
     * Diese Methode wird immer dann aufgerufen, wenn das <code>Raum</code>-Objekt, an dem dieser Listener angemeldet wurde,
     * nach einem Fall/Sprung wieder auf einem Passiv-Objekt zum Stehen kommt.
     */
    public abstract void stehReagieren();
}
