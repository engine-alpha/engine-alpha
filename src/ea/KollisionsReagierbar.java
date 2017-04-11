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
 * Beschreibt allgemein ein Objekt, das auf die <b>Kollision zweier Raum-Objekte</b> reagieren kann.
 *
 * <h3>Funktionsweise</h3>
 * <p>Eine <code>KollisionsReagierbar</code>-Instanz wird bei Kollisionen zwischen verschiedenen
 * <code>Raum</code>-Objekten aufgerufen. Die genauen Umstände hängen von der Art der Anmeldung ab.</p>
 * <ul>
 *     <li>Wurde das Objekt mit einem (oder mehrmals mit verschiedenen) Ziel-Raum-Objekten angemeldet,
 *     so wird es nur bei Kollision zwischen den spezifizierten Paaren informiert.</li>
 *     <li>Wurde das Objekt nur mit einem einzigen Actor-Objekt angemeldet, so wird es bei jeder Kollision zwischen
 *     dem Objekt und jedem anderen (an der Wurzel angemeldeten) <code>Raum</code>-Objekt angemeldet.</li>
 * </ul>
 *
 * @see ea.Anmelden#kollisionsReagierbar(KollisionsReagierbar, Raum)
 * @see ea.Anmelden#kollisionsReagierbar(KollisionsReagierbar, Raum, Raum)
 * @author Michael Andonie
 * @version 11.04.17
 */

public interface KollisionsReagierbar<E extends Raum> {

	/**
	 * Wird bei einer (korrekt angemeldeten) Instanz immer dann aufgerufen, wenn der hiermit angemeldete Actor mit
	 * einem (relevanten) Raum-Objekt kollidiert.
	 * @param colliding Ein <code>Raum</code>-Objekt, das mit dem zugehörig angemeldeten Actor-Objekt kollidiert. Je
	 *                  nach Anmeldeart können dies nur ausgewählte Objekte sein.
	 * @see ea.Anmelden#kollisionsReagierbar(KollisionsReagierbar, Raum)
	 * @see ea.Anmelden#kollisionsReagierbar(KollisionsReagierbar, Raum, Raum)
	 */
    @API
	void kollision (E colliding);

    /**
     * Wird bei einer (korrekt angemeldeten) Instanz immer dann aufgerufen, wenn die Kollision eines hiermit
	 * angemeldeten Actors mit einem (relevanten) Raum-Objekt beendet ist.
	 * @param colliding Ein <code>Raum</code>-Objekt, das mit dem zugehörig angemeldeten Actor-Objekt kollidiert hattte.
	 *                  Je nach Anmeldeart können dies nur ausgewählte Objekte sein.
	 * @see ea.Anmelden#kollisionsReagierbar(KollisionsReagierbar, Raum)
	 * @see ea.Anmelden#kollisionsReagierbar(KollisionsReagierbar, Raum, Raum)
	 */
    @API
    default void kollisionBeendet(E colliding) {
    	//Ist selten genug wichtig um zu rechtfertigen, dass eine Default-Implementierung leer ist.
	}
}
