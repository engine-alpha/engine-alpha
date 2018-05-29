/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2018 Michael Andonie and contributors.
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

@API
public interface Attachable {
    /**
     * Diese Methode wird aufgerufen, wenn ein Objekt angemeldet wird.
     *
     * @param scene Szene, an der das Objekt angemeldet wird.
     */
    @API
    default void onAttach(Scene scene) {
        // override if special behavior is needed
    }

    /**
     * Diese Methode wird aufgerufen, wenn ein Objekt abgemeldet wird.
     *
     * @param scene Szene, an der das Objekt abgemeldet wird.
     */
    @API
    default void onDetach(Scene scene) {
        // override if special behavior is needed
    }
}
