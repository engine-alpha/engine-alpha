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

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

public final class ShapeHelper {
    public static Shape createRectangularShape(float width, float height) {
        PolygonShape shape = new PolygonShape();

        shape.set(new Vec2[] {new Vec2(0, 0), new Vec2(0, height), new Vec2(width, height), new Vec2(width, 0)}, 4);

        shape.m_centroid.set(new Vec2(width / 2, height / 2));

        return shape;
    }

    /**
     * Erstellt einen Shape-Supplier basierend auf einem String.
     *
     * @param code <ul>
     *             <li>Shapes werden getrennt durch "&"</li>
     *             <li>Rechteck: <code>R0,0,40,50</code> Rechteck mit Startpunkt (0|0), Breite 40, Höhe 50</li>
     *             <li>Polygon: <code>P40,40,50,50,10,20</code> Polygon mit drei Punkten: (40|40), (50|50), (10|20)</li>
     *             <li>Kreis:  <code>C10,10,40</code> Kreis mit Mittelpunkt (10|10) und Radius 40</li>
     *             </ul>
     */
    public static Supplier<List<Shape>> fromString(String code) {
        //Leerzeichen raus
        code.replace(" ", "");

        Scanner scanner = new Scanner(code);
        scanner.useDelimiter("&");
        ArrayList<Shape> shapeList = new ArrayList<>();
        while (scanner.hasNext()) {
            String line = scanner.next();
            Shape shape = fromLine(line);
            shapeList.add(shape);
        }
        return () -> shapeList;
    }

    private static Shape fromLine(String line) {
        char shape = line.charAt(0);
        line = line.substring(1);
        String[] split = line.split(",");

        switch (shape) {
            case 'R':
                if (split.length != 4) {
                    throw new IllegalArgumentException("Fehlerhafte Eingabe");
                }
                float x = Float.parseFloat(split[0]);
                float y = Float.parseFloat(split[1]);
                float w = Float.parseFloat(split[2]);
                float h = Float.parseFloat(split[3]);
                PolygonShape rectShape = new PolygonShape();
                rectShape.set(new Vec2[] {new Vec2(x, y), new Vec2(x, y + h), new Vec2(x + w, y + h), new Vec2(x + w, y)}, 4);
                return rectShape;
            case 'P':
                if (split.length % 2 != 0) {
                    throw new IllegalArgumentException("Fehlerhafte Eingabe");
                }
                Vec2[] polyPoints = new Vec2[split.length / 2];
                for (int i = 0; i < polyPoints.length; i++) {
                    float px = Float.parseFloat(split[2 * i]);
                    float py = Float.parseFloat(split[2 * i + 1]);
                    polyPoints[i] = new Vec2(px, py);
                }
                PolygonShape polygonShape = new PolygonShape();
                polygonShape.set(polyPoints, polyPoints.length);
                return polygonShape;
            case 'C':
                if (split.length != 3) {
                    throw new IllegalArgumentException("Fehlerhafte Eingabe");
                }
                CircleShape circleShape = new CircleShape();
                circleShape.m_p.set(Float.parseFloat(split[0]), Float.parseFloat(split[1]));
                circleShape.setRadius(Float.parseFloat(split[2]));
                return circleShape;
            default:
                throw new IllegalArgumentException("Fehlerhafte Eingabe!");
        }
    }

    public static void main(String[] args) {
        //fromString("R0,0,40,10&C80,10,200");
    }

    //private static Vector nextVector()
}
