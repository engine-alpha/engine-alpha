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

import ea.Vector;
import ea.internal.physics.FixtureData;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

public final class FixtureBuilder {
    /**
     * Erstellt eine <i>einfache</i> rechteckige Shape. Einfach bedeutet: Sie beginnt immer bei (0|0) und Breite/Höhe
     * ist parallel zu den Koordinatenaxen.
     *
     * @param width  Die Breite der rechteckigen Shape.
     * @param height Die Höhe der rechteckigen Shape.
     */
    public static FixtureData createSimpleRectangularFixture(float width, float height) {
        PolygonShape shape = new PolygonShape();
        shape.set(new Vec2[] {new Vec2(0, 0), new Vec2(0, height), new Vec2(width, height), new Vec2(width, 0)}, 4);
        shape.m_centroid.set(new Vec2(width / 2, height / 2));

        return new FixtureData(shape);
    }

    /**
     * Erschafft eine kreisförmige Shape.
     *
     * @param mx Der Mittelpunkt des Kreises, X-Koordinate.
     * @param my Der Mittelpunkt des Kreises, Y-Koordinate.
     * @param r  Der Radius des Kreises
     */
    public static FixtureData createCircleShape(float mx, float my, float r) {
        CircleShape circleShape = new CircleShape();
        circleShape.m_p.set(mx, my);
        circleShape.setRadius(r);
        return new FixtureData(circleShape);
    }

    /**
     * Erstellt eine polygonale Shape. Kann nur konvexe Shapes erstellen. Konkave Shapes werden automatisch zur
     * umspannenden konvexen Shape formatiert.
     *
     * @param points Eine Reihe an Punkten, die nacheinander diese Shape beschreiben (mindestens 3 Punkte).
     */
    public static FixtureData createPolygonShape(Vector... points) {
        if (points.length < 3) {
            throw new IllegalArgumentException("Eine polygonale Shape benötigt mindestens 3 Punkte.");
        }
        Vec2[] vec2s = new Vec2[points.length];
        for (int i = 0; i < points.length; i++) {
            vec2s[i] = points[i].toVec2();
        }
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vec2s, vec2s.length);
        return new FixtureData(polygonShape);
    }

    /**
     * Erstellt eine rechteckige Shape, die parallel zu den Koordinatenaxen läuft.
     *
     * @param sx     Linke untere Ecke, X-Koordinate.
     * @param sy     Linke untere Ecke, Y-Koordinate.
     * @param width  Breite der rechteckigen Shape.
     * @param height Höhe der rechteckigen Shape.
     */
    public static Shape createAxisParallelRectangularShape(float sx, float sy, float width, float height) {
        PolygonShape rectShape = new PolygonShape();
        rectShape.set(new Vec2[] {new Vec2(sx, sy), new Vec2(sx, sy + height), new Vec2(sx + width, sy + height), new Vec2(sx + width, sy)}, 4);
        return rectShape;
    }

    /**
     * Erstellt einen Shape-Supplier basierend auf einem String.
     *
     * @param code <ul>
     *             <li>Shapes werden getrennt durch "&amp;"</li>
     *             <li>Rechteck: <code>R0,0,40,50</code> Rechteck mit Startpunkt (0|0), Breite 40, Höhe 50</li>
     *             <li>Polygon: <code>P40,40,50,50,10,20</code> Polygon mit drei Punkten: (40|40), (50|50), (10|20)</li>
     *             <li>Kreis:  <code>C10,10,40</code> Kreis mit Mittelpunkt (10|10) und Radius 40</li>
     *             </ul>
     */
    public static Supplier<List<FixtureData>> fromString(String code) {
        try (Scanner scanner = new Scanner(code.replace(" ", ""))) {
            scanner.useDelimiter("&");

            ArrayList<FixtureData> shapeList = new ArrayList<>();
            while (scanner.hasNext()) {
                String line = scanner.next();
                Shape shape = fromLine(line);
                shapeList.add(new FixtureData(shape));
            }

            return () -> shapeList;
        }
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
                float sx = Float.parseFloat(split[0]);
                float sy = Float.parseFloat(split[1]);
                float width = Float.parseFloat(split[2]);
                float height = Float.parseFloat(split[3]);
                return createAxisParallelRectangularShape(sx, sy, width, height);
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
}
