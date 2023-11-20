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

package ea.collision;

import ea.actor.Actor;
import ea.internal.annotations.API;

/**
 * Beschreibt allgemein ein Objekt, das auf die <b>Kollision zweier Actor-Objekte</b> reagieren
 * kann.
 * <h2>Funktionsweise</h2> <p>Eine <code>KollisionsReagierbar</code>-Instanz wird bei Kollisionen
 * zwischen verschiedenen <code>Actor</code>-Objekten aufgerufen. Die genauen Umstände hängen von der
 * Art der Anmeldung ab.</p> <ul> <li>Wurde das Objekt mit einem (oder mehrmals mit verschiedenen)
 * Ziel-Actor-Objekten angemeldet, so wird es nur bei Kollision zwischen den spezifizierten Paaren
 * informiert.</li> <li>Wurde das Objekt nur mit einem einzigen Actor-Objekt angemeldet, so wird es
 * bei jeder Kollision zwischen dem Objekt und jedem anderen (an der Wurzel angemeldeten)
 * <code>Actor</code>-Objekt angemeldet.</li> </ul>
 *
 * @param <E> Typ des anderen Objekts bei Kollisionen.
 * @author Michael Andonie
 */
public interface CollisionListener<E extends Actor> {
    /**
     * Wird bei einer (korrekt angemeldeten) Instanz immer dann aufgerufen, wenn der hiermit
     * angemeldete Actor mit einem (relevanten) Actor-Objekt kollidiert.
     *
     * @param collisionEvent Ein {@link CollisionEvent}-Objekt, dass alle Informationen der
     *                       Kollision beschreibt.
     *
     * @see CollisionEvent
     */
    @API
    void onCollision(CollisionEvent<E> collisionEvent);

    /**
     * Wird bei einer (korrekt angemeldeten) Instanz immer dann aufgerufen, wenn die Kollision eines
     * hiermit angemeldeten Actors mit einem (relevanten) Actor-Objekt beendet ist.
     *
     * @param collisionEvent Ein {@link CollisionEvent}-Objekt, dass alle Informationen der
     */
    @API
    default void onCollisionEnd(CollisionEvent<E> collisionEvent) {
        // Ist selten genug wichtig um zu rechtfertigen, dass eine Default-Implementierung leer ist.
    }
}
