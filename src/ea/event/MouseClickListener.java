/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2019 Michael Andonie and contributors.
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

package ea.event;

import ea.Vector;
import ea.internal.annotations.API;

/**
 * Implementierende Klassen können auf jeden einzelnen Klick reagieren, unabhängig davon, ob dies
 * ein spezielles Objekt trifft oder nicht.
 *
 * @author Michael Andonie
 * @author Niklas Keller
 */
public interface MouseClickListener {
    /**
     * Diese Methode wird bei jedem <b>Klick</b> aufgerufen, unabhängig davon an welcher Position
     * sich die Maus befindet.
     * <p>
     * Natürlick muss dafür erst der {@link MouseClickListener} angemeldet werden.
     *
     * @param position Der Point auf der Zeichenebene.
     * @param button   Die Maustaste, die gedrückt wurde.
     */
    @API
    void onMouseDown(Vector position, MouseButton button);

    /**
     * Diese Methode wird bei jedem <b>Loslassen</b> einer Maustaste aufgerufen, unabhängig davon
     * an welcher Position sich die Maus befindet.
     * <p>
     * Natürlick muss dafür erst der {@link MouseClickListener} angemeldet werden.
     *
     * @param position Der Point auf der Zeichenebene.
     * @param button   Die Maustaste, die gedrückt wurde.
     */
    @API
    default void onMouseUp(Vector position, MouseButton button) {
        // empty by default
    }
}
