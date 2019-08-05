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

package ea.handle;

import ea.actor.Actor;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

/**
 * Aufzählung der drei verschiedenen Typen von Objekten innerhalb der Physics der EA.
 * <ul>
 * <li>
 * <b>Statische</b> Objekte:
 * <ul>
 * <li>Haben keine Geschwindigkeit</li>
 * <li>Bewegen sich nicht in der Simulation, Kräfte haben keinen Einfluss auf sie.</li>
 * </ul>
 * Diese Eigenschaft gehört zum Beispiel zu <i>Wänden, Böden und Decken</i>.
 * </li>
 * <li>
 * <b>Dynamische</b> Objekte:
 * <ul>
 * <li>Verhalten sich wie Objekte der newton'schen Mechanik.</li>
 * <li>Können Kräfte auf sich wirken lassen und miteinander interagieren.</li>
 * </ul>
 * Diese Eigenschaft gehört zum Beispiel zu <i>Billiardkugeln, Spielfiguren und Wurfgeschossen</i>.
 * </li>
 * <li>
 * <b>Kinematische</b> Objekte:
 * <ul>
 * <li>Können eine Geschwindigkeit haben, aber onKeyDownInternal nicht auf Kräfte.</li>
 * <li>Kollidieren (im Sinne der Physics) nur mit dynamischen Objekten.</li>
 * </ul>
 * Doese Eigenschaft gehört zum Beispiel zu <i>beweglichen Plattformen</i>.
 * </li>
 * <li>
 * <b>Passive</b> Objekte:
 * <ul>
 * <li>Nehmen nicht an der Physics teil. Sie werden von der Physics so behandelt,
 * <i>als wären sie nicht da</i>.</li>
 * <li>Dies ist die <b>Standardeinstellung</b> für Objekte.</li>
 * </ul>
 * </li>
 * </ul>
 *
 * @see Actor#setBodyType(BodyType)
 * @see Actor#getBodyType()
 */
@API
public enum BodyType {
    STATIC(1), DYNAMIC(1), KINEMATIC(1), PASSIVE(0), PARTICLE(0);

    private float defaultGravityScale;

    BodyType(float defaultGravityScale) {
        this.defaultGravityScale = defaultGravityScale;
    }

    /**
     * Konvertierungsmethode zwischen Engine-Physiktyp und JB2D-Physiktyp.
     *
     * @return Der zugehörige JB2D-Phy-Type zu diesem Engine-Phy-Type.
     */
    @Internal
    public BodyType convert() {
        switch (this) {
            case STATIC:
                return STATIC;
            case DYNAMIC:
            case PASSIVE:
            case PARTICLE:
                return DYNAMIC;
            case KINEMATIC:
                return KINEMATIC;
        }
        return null;
    }

    public float getDefaultGravityScale() {
        return this.defaultGravityScale;
    }

    public boolean isSensorType() {
        return this == PASSIVE;
    }
}
