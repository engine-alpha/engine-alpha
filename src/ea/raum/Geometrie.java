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

package ea.raum;

import ea.Farbe;
import ea.Punkt;
import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;

import java.awt.*;

/**
 * Ein Objekt, das aus n primitiven Geometrischen Formen - <b>Dreiecken</b> - besteht
 *
 * @author Michael Andonie
 */
public abstract class Geometrie extends Raum {

    /**
     * Die Farbe dieses Geometrie-Objekts.
     */
    private Color color;

	/**
	 * Konstruktor.
	 *
	 * @param position  Die Ausgangsposition dieses Geometrie-Objekts.
	 */
	public Geometrie (Punkt position) {
        this.position.set(position);
	}

	/**
	 * Setzt ganzheitlich die Farbe der gesamten geometrischen Figur auf eine Farbe.
	 *
	 * @param f
	 * 		Die Farbe, die das Objekt haben soll.
	 *
	 * @see Farbe
	 */
	@API
	public void setColor(Farbe f) {
		setColor(f.wert());
	}

	/**
	 * Setzt ganzheitlich die Farbe aller Formen auf eine bestimmte Farbe.<br /> Dadurch faerbt sich
	 * im Endeffekt das ganze Objekt neu ein.
	 *
	 * @param c
	 * 		Die neue Farbe
	 */
	@NoExternalUse
	public void setColor(Color c) {
		this.color = c;
	}

    /**
     * Gibt die AWT-Farbe aus.
     * @return  Das AWT-Color-Objekt.
     */
    @NoExternalUse
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
	@API
	public void setColor(String farbe) {
		setColor(Farbe.zuFarbeKonvertieren(farbe));
	}
}
