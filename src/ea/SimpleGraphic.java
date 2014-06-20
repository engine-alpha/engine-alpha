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

import ea.internal.gui.Fenster;

import java.awt.*;

/**
 * Simple grafische Klassen bestehen nicht aus Flaechen. Sie haben nicht die vielfaeltigen
 * Eigenschaften von Objekten der Klasse <code>Raum</code>.<br /> Ihre Verwendung ist nur im Mangel
 * von Alternativen aus der <code>Raum</code>-Hierarchie zu empfehlen.
 *
 * @author Andonie
 */
public abstract class SimpleGraphic {
	/**
	 * Konstruktor initialisiert bereits die Darstellung.
	 */
	public SimpleGraphic () {
		Fenster.instanz().fillSimple(this);
	}

	/**
	 * Methode zum zeichnen. Wird individuell ueberschrieben.
	 *
	 * @param g
	 * 		Das Graphics-Objekt.
	 * @param dx
	 * 		Die X-Verschiebung der Kamera
	 * @param dy
	 * 		Die Y-Verschiebung der Kamera
	 */
	public abstract void paint (Graphics g, int dx, int dy);

	public void loeschen () {
		Fenster.instanz().removeSimple(this);
	}
}
