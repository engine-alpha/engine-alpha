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

package ea.internal;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

public final class ShapeHelper {
    public static Shape createRectangularShape(float width, float height) {
        PolygonShape shape = new PolygonShape();

        shape.set(new Vec2[] {
                new Vec2(0, 0),
                new Vec2(0, height),
                new Vec2(width, height),
                new Vec2(width, 0)
        }, 4);

        shape.m_centroid.set(new Vec2(width / 2, height / 2));

        return shape;
    }
}
