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
import org.jbox2d.collision.shapes.Shape;

import java.awt.*;

/**
 * Beschreibt ein RechteckTreeSet.
 *
 * @author Michael Andonie
 * @author Niklas Keller
 */
@SuppressWarnings ( "serial" )
public class Rectangle extends Geometry {
    /**
     * Die Breite
     */
    private float width;

    /**
     * Die Breite als Integer gerundet
     */
    private int widthInt;

    /**
     * Die Höhe
     */
    private float height;

    /**
     * Die Höhe als Integer gerundet
     */
    private int heightInt;

    /**
     * Konstruktor.
     *
     * @param width  Die Breite des Rechtecks
     * @param height Die Höhe des Rechtecks
     */
    public Rectangle(float width, float height) {
        this.width = width;
        this.height = height;
        this.widthInt = Math.round(width);
        this.heightInt = Math.round(height);
    }

    @API
    public float getWidth() {
        return width;
    }

    @API
    public float getHeight() {
        return height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(Graphics2D g) {
        g.setColor(getColor());
        g.fillRect(0, 0, widthInt, heightInt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Shape createShape(float pixelProMeter) {
        return berechneBoxShape(pixelProMeter, width, height);
    }
}
