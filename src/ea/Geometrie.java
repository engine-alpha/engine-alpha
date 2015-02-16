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

import java.awt.*;

/**
 * Ein Objekt, das aus n primitiven Geometrischen Formen - <b>Dreiecken</b> - besteht
 *
 * @author Michael Andonie
 */
public abstract class Geometrie extends Raum {
	/**
	 * Die Dimension des Objektes; zur schnellen Ausgabe
	 */
	protected BoundingRechteck dimension;

    /**
     * Die Farbe dieses Geometrie-Objekts.
     */
    private Color color;

	/**
	 * Gibt an, ob dieses Geometrie-Objekt gerade leuchtet
	 */
	private boolean leuchtet = false;

	/**
	 * Der Leuchtzaehler fuer die Leucht-Animationen
	 */
	private int leuchtzaehler = 0;

	/**
	 * Die Farbe, die sich das Objekt merkt, wenn es zu leuchten anfaengt, um wieder die alte
	 * herstellen zu koennen.
	 */
	private Color alte = Color.white;

	/**
	 * Konstruktor.
	 *
	 * @param x
	 * 		bestimmende x-Koordinate
	 * @param y
	 * 		bestimmende y-Koordinate
	 */
	public Geometrie (float x, float y) {
		position = new Punkt(x, y);
		dimension = new BoundingRechteck(x, y, 0, 0);
	}

	/**
	 * Zeichnet das Objekt.<br /> heisst in diesem Fall das saemtliche Unterdreiecke gezeichnet
	 * werden.
	 */
	public void render(Graphics2D g, BoundingRechteck r) {
        throw new UnsupportedOperationException("Noch nicht implementiert (4.0)");
	}

    /**
	 * Verschiebt das Objekt.
	 *
	 * @param v
	 * 		Der Vektor, der die Verschiebung des Objekts angibt.
	 *
	 * @see Vektor
	 */
	@Override
	public void verschieben (Vektor v) {
		//FIXME Implementation
	}

	/**
	 * Diese Methode loescht alle eventuell vorhandenen Referenzen innerhalb der Engine auf dieses
	 * Objekt, damit es problemlos geloescht werden kann.<br /> <b>Achtung:</b> zwar werden
	 * hierdurch alle Referenzen geloescht, die <b>nur innerhalb</b> der Engine liegen (dies
	 * betrifft vor allem Animationen etc), jedoch nicht die innerhalb eines
	 * <code>Knoten</code>-Objektes!!!!!!!!!<br /> Das heisst, wenn das Objekt an einem Knoten liegt
	 * (was <b>immer der Fall ist, wenn es auch gezeichnet wird (siehe die Wurzel des
	 * Fensters)</b>), muss es trotzdem selbst geloescht werden, <b>dies erledigt diese Methode
	 * nicht!!</b>.<br /> Diese Klasse ueberschreibt die Methode wegen des Leuchtens.
	 */
	@Override
	public void loeschen () {
		super.loeschen();
	}

	/**
	 * Setzt ganzheitlich die Farbe der gesamten geometrischen Figur auf eine Farbe.
	 *
	 * @param f
	 * 		Die Farbe, die das Objekt haben soll.
	 *
	 * @see Farbe
	 */
	public void farbeSetzen (Farbe f) {
		farbeSetzen(f.wert());
	}

	/**
	 * Setzt ganzheitlich die Farbe aller Formen auf eine bestimmte Farbe.<br /> Dadurch faerbt sich
	 * im Endeffekt das ganze Objekt neu ein.
	 *
	 * @param c
	 * 		Die neue Farbe
	 */
	public void farbeSetzen (Color c) {
		this.color = c;
	}

    /**
     * TODO
     * @return
     */
    public Color getColor() {
        return color;
    }

	/**
	 * Setzt ganzheitlich die Farbe aller Formen auf eine bestimmte Farbe.<br /> Dadurch faerbt sich
	 * im Endeffekt das ganze Objekt neu ein.
	 *
	 * @param farbe
	 * 		Der String-Wert der Farbe. Zu der Zuordnung siehe <b>Handbuch</b>
	 */
	public void farbeSetzen (String farbe) {
		farbeSetzen(Farbe.zuFarbeKonvertieren(farbe));
	}
}
