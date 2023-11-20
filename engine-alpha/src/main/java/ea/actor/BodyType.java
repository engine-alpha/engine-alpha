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

package ea.actor;

import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

/**
 * Aufzählung der verschiedenen Typen von Objekten innerhalb der Physics der EA.
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
 * <li>Können Kräfte auf sich wirken lassen und interagieren miteinander.</li>
 * </ul>
 * Diese Eigenschaft gehört zum Beispiel zu <i>Billardkugeln, Spielfiguren und Wurfgeschossen</i>.
 * </li>
 * <li>
 * <b>Kinematische</b> Objekte:
 * <ul>
 * <li>Können eine Geschwindigkeit haben, aber reagieren nicht auf Kräfte.</li>
 * <li>Kollidieren (im Sinne der Physics) nur mit dynamischen Objekten.</li>
 * </ul>
 * Diese Eigenschaft gehört zum Beispiel zu <i>beweglichen Plattformen</i>.
 * </li>
 * <li>
 * <b>Sensoren</b>:
 * <ul>
 * <li>Nehmen nicht an der Physiksimulation teil. Sie werden von der Physics so behandelt,
 * <i>als wären sie nicht da</i>.</li>
 * <li>Generieren trotzdem Collision Events</li>
 * </ul>
 * Dies ist die <b>Standardeinstellung</b> für Actors, wenn sie erstellt werden.
 * </li>
 * <li>
 *  <b>Particles</b>:
 *  <ul>
 *  <li>Nehmen wie Sensoren <b>nicht teil an der Physiksimulation</b></li>
 *  <li>Generieren trotzdem Collision Events</li>
 *  </ul>
 *  Dieser Typ ist hilfreich, wenn du viele Actors generieren willst, diese aber rein optisch auf das Spiel wirken
 *  sollen, wie zum Beispiel Dreck, den ein Auto beim Anfahren aufwühlt oder Funken, die von einer Wand nach einem
 *  Schuss sprühen.
 *  </li>
 * </ul>
 *
 * @see Actor#setBodyType(BodyType)
 * @see Actor#getBodyType()
 */
@API
public enum BodyType {

    /**
     * <b>Statische</b> Objekte haben keine Geschwindigkeit. Sie bewegen sich
     * nicht in der Simulation, Kräfte haben keinen Einfluss auf sie. Diese
     * Eigenschaft gehört zum Beispiel zu <i>Wänden, Böden und Decken</i>.
     */
    STATIC,

    /**
     * <b>Dynamische</b> Objekte verhalten sich wie Objekte der Newton’schen
     * Mechanik. Sie können Kräfte auf sich wirken lassen und interagieren
     * miteinander. Diese Eigenschaft gehört zum Beispiel zu <i>Billiardkugeln,
     * Spielfiguren und Wurfgeschossen</i>.
     */
    DYNAMIC,

    /**
     * <b>Kinematische</b> Objekte können eine Geschwindigkeit haben, aber
     * reagieren nicht auf Kräfte. Sie kollidieren (im Sinne der
     * Physics) nur mit dynamischen Objekten. Diese Eigenschaft gehört zum
     * Beispiel zu <i>beweglichen Plattformen</i>.
     */
    KINEMATIC,

    /**
     * <b>Sensoren</b> nehmen nicht an der Physiksimulation teil. Sie werden von
     * der Physics so behandelt, <i>als wären sie nicht da</i>. Sie generieren
     * trotzdem Collision Events. Dies ist die <b>Standardeinstellung</b> für
     * Actors, wenn sie erstellt werden.
     */
    SENSOR,

    /**
     * <b>Particles</b> nehmen wie Sensoren <b>nicht an der
     * Physiksimulation</b> teil, sie generieren trotzdem Collision Events.
     * Dieser Typ ist hilfreich, wenn du viele Actors generieren willst, diese
     * aber rein optisch auf das Spiel wirken sollen, wie zum Beispiel Dreck,
     * den ein Auto beim Anfahren aufwühlt oder Funken, die von einer Wand nach
     * einem Schuss sprühen.
     */
    PARTICLE;

    BodyType() {
        //
    }

    /**
     * Konvertierungsmethode zwischen Engine-Physiktyp und JB2D-Physiktyp.
     *
     * @return Der zugehörige JB2D-Physiktyp zu diesem Engine-Physiktyp.
     */
    @Internal
    public org.jbox2d.dynamics.BodyType toBox2D() {
        switch (this) {
            case STATIC:
                return org.jbox2d.dynamics.BodyType.STATIC;
            case DYNAMIC:
            case SENSOR:
            case PARTICLE:
                return org.jbox2d.dynamics.BodyType.DYNAMIC;
            case KINEMATIC:
                return org.jbox2d.dynamics.BodyType.KINEMATIC;
            default:
                throw new RuntimeException("Unhandled body type: " + this);
        }
    }

    public boolean isSensor() {
        return this == SENSOR;
    }
}
