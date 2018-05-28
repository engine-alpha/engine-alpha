/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2017 Michael Andonie and contributors.
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

package ea.keyboard;

import ea.internal.ano.API;

/**
 * Dieses Interface wird implementiert, um auf gedrückte Tasten onKeyDown zu können.
 *
 * @author Niklas Keller
 */
@API
public interface KeyListener {
    /**
     * Wird bei einem angemeldeten Listener aufgerufen, sobald eine Taste gedrückt wird. Die Methode
     * wird erst wieder aufgerufen, wenn die Key losgelassen und erneut gedrückt wurde.
     *
     * @param key Zahlencode zur Taste, die gedrückt wurde. {@link Key} stellt eine Konstante für
     *            jede berücksichtigte Taste zur Verfügung.
     * @see ea.keyboard.Key
     */
    @API
    void onKeyDown(int key);

    /**
     * Wird bei einem angemeldeten Listener aufgerufen, sobald eine Taste losgelassen wurde, die
     * vorher gedrückt war.
     *
     * @param code Zahlencode zur Taste, die losgelassen wurde. {@link Key} stellt eine Konstante
     *             für jede berücksichtigte Taste zur Verfügung.
     * @see ea.keyboard.Key
     */
    @API
    void onKeyUp(int code);
}
