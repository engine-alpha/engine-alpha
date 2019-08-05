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

package ea.example.showcase.billard;

import ea.Vector;
import ea.actor.Polygon;
import ea.handle.Physics;

import java.awt.*;

public class Edge extends Polygon {
    public static final float WIDTH = 500;
    public static final float HEIGHT = 20;

    public Edge(float x, float y) {
        super(new Vector(0, 0), new Vector(HEIGHT, HEIGHT), new Vector(WIDTH- HEIGHT, HEIGHT), new Vector(WIDTH, 0));

        position.set(x, y);
        setColor(new Color(45, 90, 40));
        setBodyType(Physics.Type.STATIC);
    }
}
