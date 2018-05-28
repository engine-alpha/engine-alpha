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
import org.jbox2d.collision.shapes.Shape;

import java.awt.*;

/**
 * Beschreiben Sie hier die Klasse Rectangle.
 *
 * @author Michael Andonie
 */
@SuppressWarnings ( "serial" )
public class Rectangle extends Geometry {
	/**
	 * Die Laenge
	 */
	private float laenge;

	/**
	 * Die Breite
	 */
	private float breite;

	/**
	 * Konstruktor fuer Objekte der Klasse Rectangle
	 *
	 * @param width
	 * 		Die Breite des Rechtecks
	 * @param height
	 * 		Die hoehe des Rechtecks
	 */
	public Rectangle(float width, float height) {
		super();
        setDimension(width, height);
	}

	/**
	 * Setzt beide Masse feur dieses Rectangle neu.
	 *
	 * @param width
	 * 		Die neue Breite des Rechtecks
	 * @param height
	 * 		Die neue Hoehe des Rechtecks
	 */
	public void setDimension(float width, float height) {
		setWidth(width);
        setHeight(height);
	}

	/**
	 * Setzt die Breite fuer dieses Rectangle neu.
	 *
	 * @param width
	 * 		Die neue Breite des Rechtecks
	 *
	 * @see #setHeight(float)
	 */
	public void setWidth(float width) {
        this.breite = width;
	}

	/**
	 * Setzt die Hoehe fuer dieses Rectangle neu.
	 *
	 * @param height
	 * 		Die neue Hoehe des Rechtecks
	 *
	 * @see #setWidth(float)
	 */
	public void setHeight(float height) {
		this.laenge = height;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(Graphics2D g) {
        g.setColor(getColor());
		g.fillRect(0, 0, (int) breite, (int) laenge);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Shape createShape(float pixelProMeter) {
        return berechneBoxShape(pixelProMeter, breite, laenge);
    }
}
