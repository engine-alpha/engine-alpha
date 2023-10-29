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

import ea.internal.annotations.API;

/**
 * Repräsentation einer Richtung.
 */
@API
public enum Direction {
    /**
     * Repräsentation einer Richtung, die nach <strong>oben</strong> zeigt.
     */
    UP,

    /**
     * Repräsentation einer Richtung, die nach <strong>oben rechts</strong> zeigt.
     */
    UP_RIGHT,

    /**
     * Repräsentation einer Richtung, die nach <strong>rechts</strong> zeigt.
     */
    RIGHT,

    /**
     * Repräsentation einer Richtung, die nach <strong>oben rechts</strong> zeigt.
     */
    DOWN_RIGHT,

    /**
     * Repräsentation einer Richtung, die nach <strong>unten</strong> zeigt.
     */
    DOWN,

    /**
     * Repräsentation einer Richtung, die nach <strong>unten links</strong> zeigt.
     */
    DOWN_LEFT,

    /**
     * Repräsentation einer Richtung, die nach <strong>links</strong> zeigt.
     */
    LEFT,

    /**
     * Repräsentation einer Richtung, die nach <strong>oben links</strong> zeigt.
     */
    UP_LEFT,

    /**
     * Repräsentation einer Richtung, die nirgendwo hinzeigt.
     */
    NONE;

    /**
     * Berechnet einen einfachen Vector (maximale Auslenkung bei jeder Achse 1 – positiv wie negativ).
     *
     * @return Vector, der mit einer einfachen Auslenkung (d.h. für <code>x</code> und
     * <code>y</code> je ein Wertebereich von {-1, 0, 1}) die entsprechende Bewegung macht.
     */
    @API
    public Vector toVector() {
        return switch (this) {
            case UP -> Vector.UP;
            case UP_RIGHT -> Vector.UP.add(Vector.RIGHT);
            case RIGHT -> Vector.RIGHT;
            case DOWN_RIGHT -> Vector.DOWN.add(Vector.RIGHT);
            case DOWN -> Vector.DOWN;
            case DOWN_LEFT -> Vector.DOWN.add(Vector.LEFT);
            case LEFT -> Vector.LEFT;
            case UP_LEFT -> Vector.UP.add(Vector.LEFT);
            case NONE -> Vector.NULL;
        };
    }
}
