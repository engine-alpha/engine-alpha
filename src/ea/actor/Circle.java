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

package ea.actor;

import ea.internal.ano.API;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;

import java.awt.*;

/**
 * Beschreibt einen Kreis.
 *
 * @author Michael Andonie
 * @author Niklas Keller
 */
@SuppressWarnings ( "serial" )
public class Circle extends Geometry {
    private float diameter;
    private int diameterInt;

    /**
     * Konstruktor.
     *
     * @param diameter Durchmesser des Kreises
     */
    public Circle(float diameter) {
        this.diameter = diameter;
        this.diameterInt = Math.round(diameter);
    }

    /**
     * Gibt den Durchmesser des Kreises aus.
     *
     * @return Durchmesser des Kreises.
     */
    @API
    public float getDiameter() {
        return diameter;
    }

    /**
     * Gibt den Radius des Kreises aus.
     *
     * @return Radius des Kreises.
     */
    @API
    public float getRadius() {
        return diameter / 2;
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(getColor());
        g.fillOval(0, -this.diameterInt, this.diameterInt, this.diameterInt);
    }

    @Override
    public Shape createShape(float pixelPerMeter) {
        CircleShape shape = new CircleShape();
        shape.m_radius = getRadius() / pixelPerMeter;
        shape.m_p.set(shape.m_radius, shape.m_radius);

        return shape;
    }
}
