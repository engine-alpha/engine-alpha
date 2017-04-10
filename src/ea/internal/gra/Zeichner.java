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

package ea.internal.gra;

import ea.*;

import java.awt.*;
import java.awt.image.BufferStrategy;

/**
 * Dies ist das Panel, in dem die einzelnen Dinge gezeichnet werden
 *
 * @author Michael Andonie, Niklas Keller <me@kelunik.com>
 */
public class Zeichner extends Canvas {

	private static final long serialVersionUID = 188647530006553893L;


	/**
	 * Die Kamera.<br /> Letzendlich wird das gezeichnet, was sich in ihr befindet
	 */
	private Kamera cam;

	/**
	 * Das BoundingRechteck, dass das Panel in seiner Groesse beschreibt.
	 */
	private BoundingRechteck groesse;

	/**
	 * Der Knoten, der die statischen Objekte beinhaltet.
	 */
	private Knoten statNode = new Knoten();

    /**
     * Gibt die Buffer Strategy aus.
     * @return  Die BufferStrategy für den Zeichner.
     */
    public BufferStrategy getBs() {
        return bs;
    }

    /**
     * Gibt das Graphics-Object des Zeichners aus.
     * @return das Graphics-Objekt des Zeichners.
     */
    public Graphics2D getG() {
        return g;
    }

    /**
     * Die BufferedStrategy, die hier visualisiert wird
     */
    private BufferStrategy bs;
    private Graphics2D g;

    /**
	 * Konstruktor für Objekte der Klasse Zeichner
	 *
	 * @param x
	 * 		Die Größe des Einflussbereichs des Panels in Richtung <code>x</code>.
	 * @param y
	 * 		Die Größe des Einflussbereichs des Panels in Richtung <code>y</code>.
	 * @param c
	 * 		Die Kamera, deren Sicht grafisch dargestellt werden soll.
	 */
	public Zeichner (int x, int y, Kamera c) {
		this.setSize(x, y);
		this.setPreferredSize(getSize());
		this.setFocusable(true);

		this.groesse = new BoundingRechteck(0, 0, x, y);
		this.cam = c;

	}

    public void init() {
        createBufferStrategy(2);
        bs = getBufferStrategy();
        g = (Graphics2D) bs.getDrawGraphics();

        // have to be the same @ Game.screenshot!
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
    }

	/**
	 * Die render()-Methode, sie führt die gesamte Zeichenroutine aus.
	 *
	 * @param g
	 * 		Das zum Zeichnen uebergebene Graphics-Objekt
	 */
	public void render (Graphics2D g) {
		// Absoluter Hintergrund
		g.setColor(Color.red);
		g.fillRect(0, 0, (int) groesse.breite, (int) groesse.hoehe);


		// Die Objekte
		cam.zeichne(g);

		// Die statischen Objekte
		statNode.renderBasic(g, cam.position()); // TODO StatNode checken
	}

	/**
	 * @return Die Kamera, die dieser Zeichner aufruft
	 */
	public Kamera cam () {
		return cam;
	}

	/**
	 * @return Der statische Basisknoten
	 */
	public Knoten statNode () {
		return statNode;
	}

	/**
	 * @return Ein BoundingRechteck, dass die Breite und Höhe des Fensters hat.
	 */
	public BoundingRechteck masse () {
		return groesse;
	}
}
