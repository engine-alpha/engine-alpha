/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2023 Michael Andonie and contributors.
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

import ea.internal.annotations.API;

/**
 * Beschreibt ein Objekt, dass auf jedes Frame-Update reagieren kann.
 */
@API
public interface FrameUpdateListener {
    /**
     * Diese Methode wird bei einem (angemeldeten) Objekt jeden Frame erneut aufgerufen.
     *
     * @param deltaSeconds Die Zeit <b>in Sekunden</b>, die seit dem letzten Update vergangen
     */
    @API
    void onFrameUpdate(float deltaSeconds);
}
