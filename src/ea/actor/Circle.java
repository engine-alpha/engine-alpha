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

package ea.actor;

import ea.Point;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.collision.shapes.Shape;

import java.awt.*;

/**
 * Ein Circle ist ein regelmaessiges n-Eck, dessen Eckenzahl gegen unendlich geht.<br /> Dies ist mit
 * einem Computer nicht moeglich, daher wird fuer einen Circle eine ausrechend grosse Anzahl an Ecken
 * gewaehlt. Diese ist ueber die Genauigkeit im Konstruktor mitzugeben oder im vereinfachten
 * konstruktor bereits voreingestellt.
 *
 * @author Michael Andonie
 */
@SuppressWarnings ("serial")
public class Circle extends Geometry {

    private float durchmesser;

	/**
	 * Alternativkonstruktor mit vorgefertigter Genauigkeit
	 * @param diameter
	 * 		Der Durchmesser des Kreises
	 */
	public Circle(float diameter) {
        this.durchmesser = diameter;
	}

	/**
	 * Gibt den Radius des Kreises aus
	 *
	 * @return Der Radius des Kreises
	 */
	public float getRadius() {
		return durchmesser/2;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(Graphics2D g) {
        g.setColor(getColor());
        g.fillOval(0, 0, Math.round(durchmesser), Math.round(durchmesser));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Shape createShape(float pixelProMeter) {
        CircleShape shape = new CircleShape();
        shape.m_radius = getRadius()/pixelProMeter;
        shape.m_p.set(shape.m_radius,shape.m_radius);
        return shape;
    }
}
