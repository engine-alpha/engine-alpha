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

import ea.internal.util.Logger;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

import java.awt.*;

/**
 * Das Dreieck ist die Basiszeichenklasse.<br /> Jeder Koerper laesst sich aus solchen
 * darstellen.<br /> Daher ist dies die <b>einzige</b> Klasse, die in sich eine Zeichenroutine hat
 *
 * @author Michael Andonie
 */
public class Dreieck extends Geometrie {
	/**
	 * Die X-Koordinaten der Punkte
	 */
	private float[] x = new float[3];

	/**
	 * Die Y-Koordinaten der Punkte
	 */
	private float[] y = new float[3];

	/**
	 * Die Darstellungsfarbe
	 */
	private java.awt.Color farbe = java.awt.Color.white;

	/**
	 * Konstruktor
	 *
	 * @param x
	 * 		Alle X-Koordinaten als Feld
	 * @param y
	 * 		Alle Y-Koordinaten als Feld
	 */
	public Dreieck (float[] x, float[] y) {
		super();

		if (x.length == 3 && y.length == 3) {
			this.x = x;
			this.y = y;
		} else {
			Logger.error("Raum", String.format("Die Längen der beiden float-Arrays hatten nicht die passende Länge (3). Waren: %d und %d.",
                    x.length, y.length));
		}
	}

    public Dreieck(Punkt p1, Punkt p2, Punkt p3) {
        this(new float[] {p1.x, p2.x, p3.x}, new float[] {p1.y, p2.y, p3.y});
    }

	/**
	 * @return Die Farbe dieses Dreiecks
	 */
	public Color getColor() {
		return farbe;
	}

	/**
	 * Setzt die Farbe ueber die JAVA-Farbklasse.
	 *
	 * @param c
	 * 		Die Farbe dieses Dreiecks, anhand der Klasse <code>Color</code>.
	 */
	public void setColor (Color c) {
		farbe = c;
	}

	/**
	 * Setzt die drei Punkte dieses Dreiecks neu.
	 *
	 * @param p1
	 * 		Der 1. neue Punkt des Dreiecks
	 * @param p2
	 * 		Der 2. neue Punkt des Dreiecks
	 * @param p3
	 * 		Der 3. neue Punkt des Dreiecks
	 *
	 * @see #punkteSetzen(float[], float[])
	 */
	public void punkteSetzen (Punkt p1, Punkt p2, Punkt p3) {
		x[0] = p1.x;
		x[1] = p2.x;
		x[2] = p3.x;
		y[0] = p1.y;
		y[1] = p2.y;
		y[2] = p3.y;
	}

	/**
	 * Setzt die drei Punkte dieses Dreiecks nue
	 *
	 * @param x
	 * 		Die Koordinaten aller X-Punkte. Der Index gibt den Punkt an (x[0] und y[0] bilden einen
	 * 		Punkt)
	 * @param y
	 * 		Die Koordinaten aller Y-Punkte. Der Index gibt den Punkt an (x[0] und y[0] bilden einen
	 * 		Punkt)
	 */
	public void punkteSetzen (float[] x, float[] y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(Graphics2D g) {

        Punkt pos = position.get();

		int[] x = {(int) this.x[0], (int) this.x[1], (int) this.x[2]};

		int[] y = {(int) this.y[0], (int) this.y[1], (int) this.y[2]};

		for (int i = 0; i < 3; i++) {
			x[i] += pos.x;
			y[i] += pos.y;
		}

		g.setColor(farbe);
		g.fillPolygon(x, y, 3);
	}

    @Override
    public Shape berechneShape(float pixelProMeter) {
        PolygonShape shape = new PolygonShape();
        shape.set(new Vec2[] {
                new Vec2(x[0] / pixelProMeter, y[0] / pixelProMeter),
                new Vec2(x[1] / pixelProMeter, y[1] / pixelProMeter),
                new Vec2(x[1] / pixelProMeter, y[1] / pixelProMeter)}, 3);
        shape.m_centroid.set((x[0]+x[1]+x[2])/3, (y[0]+y[1]+y[2])/3);
        return shape;
    }
}
