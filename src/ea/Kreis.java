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

import org.jbox2d.collision.shapes.*;
import org.jbox2d.collision.shapes.Shape;

import java.awt.*;

/**
 * Ein Kreis ist ein regelmaessiges n-Eck, dessen Eckenzahl gegen unendlich geht.<br /> Dies ist mit
 * einem Computer nicht moeglich, daher wird fuer einen Kreis eine ausrechend grosse Anzahl an Ecken
 * gewaehlt. Diese ist ueber die Genauigkeit im Konstruktor mitzugeben oder im vereinfachten
 * konstruktor bereits voreingestellt.
 *
 * @author Michael Andonie
 */
@SuppressWarnings ("serial")
public class Kreis extends Geometrie {

    private float durchmesser;

	/**
	 * Alternativkonstruktor mit vorgefertigter Genauigkeit
	 *
	 * @param x
	 * 		Die X-Koordinate der Linken oberen Ecke des den Kreis umschreibenden Rechtecks, <b>nicht
	 * 		die des Mittelpunktes</b>
	 * @param y
	 * 		Die Y-Koordinate der Linken oberen Ecke des den Kreis umschreibenden Rechtecks, <b>nicht
	 * 		die des Mittelpunktes</b>
	 * @param durchmesser
	 * 		Der Durchmesser des Kreises
	 */
	public Kreis (float x, float y, float durchmesser) {
		super(new Punkt(x,y));
        this.durchmesser = durchmesser;
	}

	/**
	 * Gibt den Radius des Kreises aus
	 *
	 * @return Der Radius des Kreises
	 */
	public float radius () {
		return durchmesser/2;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(Graphics2D g) {
        g.setColor(getColor());
        g.fillOval((int) position.x(), (int) position.y(), (int) durchmesser, (int) durchmesser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Shape berechneShape(float pixelProMeter) {
        CircleShape shape = new CircleShape();
        shape.m_radius = radius()/pixelProMeter;
        return shape;
    }
}
