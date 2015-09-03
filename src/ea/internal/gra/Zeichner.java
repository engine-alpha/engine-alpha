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
import ea.internal.phy.Physik;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

/**
 * Dies ist das Panel, in dem die einzelnen Dinge gezeichnet werden
 *
 * @author Michael Andonie, Niklas Keller <me@kelunik.com>
 */
public class Zeichner extends Canvas implements Runnable {
	/**
	 * Das Intervall, in dem das Fenster upgedated wird.
	 */
	public static final int UPDATE_INTERVALL = 40;

	private static final long serialVersionUID = 188647530006553893L;

	/**
	 * Die Liste der einfachen Geometrischen Koerper, die gezeichnet werden sollen.
	 *
	 * @see ea.SimpleGraphic
	 */
	private final ArrayList<SimpleGraphic> simples = new ArrayList<>();

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
	 * Der relative Hintergrund, ist immer das Hinterste.
	 */
	private Raum hintergrund;

	/**
	 * Der absolute Vordergrund. Er liegt immer im Zentrum<br /> Reserviert fuer die absolute Maus.
	 */
	private Raum vordergrund;

	/**
	 * Gibt an, ob der Thread noch arbeiten soll.
	 */
	private boolean work = true;

	private Thread thread;

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
	public Zeichner(int x, int y, Kamera c) {
		this.setSize(x, y);
		this.setPreferredSize(getSize());
		this.setFocusable(true);

		this.groesse = new BoundingRechteck(0, 0, x, y);
		this.cam = c;
	}

	public void init() {
		if (thread == null) {
			thread = new Thread(this, "Zeichenthread") {{
				setDaemon(true);
			}};
			thread.start();
		}
	}

	/**
	 * Hierin findet in einer Dauerschleife die Zeichenroutine statt.
	 */
	@Override
	public void run() {
		createBufferStrategy(2);
		BufferStrategy bs = getBufferStrategy();
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();

		// have to be the same @ Game.screenshot!
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		while (work) {
			render(g);
			bs.show();

			try {
				Thread.sleep(UPDATE_INTERVALL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Die render()-Methode, sie führt die gesamte Zeichenroutine aus.
	 *
	 * @param g
	 * 		Das zum Zeichnen uebergebene Graphics-Objekt
	 */
	public void render(Graphics2D g) {
		// Absoluter Hintergrund
		g.setColor(Color.black);
		g.fillRect(0, 0, (int) groesse.breite, (int) groesse.hoehe);

		// Relativer Hintergrund
		if (hintergrund != null) {
			hintergrund.zeichnenBasic(g, groesse.verschobeneInstanz(new Vektor(cam.getX() / 5, cam.getY() / 10)));
		}

		// Die Objekte
		cam.zeichne(g);

		// Die simplen Grafikobjekte (nicht in Raum)
		BoundingRechteck camBounds = cam.position();
		for (SimpleGraphic gr : simples) {
			gr.paint(g, (int) camBounds.x, (int) camBounds.y);
		}

		// Die statischen Objekte
		statNode.zeichnen(g, groesse);

		// Die Maus
		if (vordergrund != null) {
			vordergrund.zeichnen(g, groesse);
		}
	}

	/**
	 * Tötet den Zeichenprozess und entfernt alle Elemente von der Wurzel und neutralisiert die
	 * Phyisk.
	 */
	public void kill() {
		work = false;

		try {
			thread.join();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Physik.neutralize();
		AnimationsManager.neutralize();
	}

	/**
	 * @return Die Kamera, die dieser Zeichner aufruft
	 */
	public Kamera cam() {
		return cam;
	}

	/**
	 * @return Der statische Basisknoten
	 */
	public Knoten statNode() {
		return statNode;
	}

	/**
	 * Meldet einen Vordergrund an.
	 *
	 * @param vordergrund
	 * 		Der neue Vordergrund
	 */
	public void anmelden(Raum vordergrund) {
		this.vordergrund = vordergrund;
	}

	/**
	 * Meldet den zu zeichnenden Hintergrund an.
	 *
	 * @param hintergrund
	 * 		Der neue Hintergrund
	 */
	public void hintergrundAnmelden(Raum hintergrund) {
		this.hintergrund = hintergrund;
	}

	/**
	 * Löscht den absoluten Vordergrund
	 */
	void vordergrundLoeschen() {
		vordergrund = null;
	}

	/**
	 * @return Ein BoundingRechteck, dass die Breite und Höhe des Fensters hat.
	 */
	public BoundingRechteck masse() {
		return groesse;
	}

	public void addSimple(SimpleGraphic g) {
		simples.add(g);
	}

	public void removeSimple(SimpleGraphic g) {
		simples.remove(g);
	}
}
