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

package ea.internal.ani;

import ea.*;
import ea.internal.util.Logger;

/**
 * Animiert ein Raum-Objekt auf einer Geraden, bis eine gewisse Maximallaenge abgelaufen wurde, dann
 * wird die Animation beendet.<br /> Dies ist eine einfache Moeglichkeit, um z.B. Schuesse zu
 * realisieren.
 *
 * @author Michael Andonie
 */
@SuppressWarnings ("serial")
public class GeradenAnimierer extends Animierer {
	/**
	 * Der eine Bewegungsschritt.
	 */
	private final Vektor bewegung;

	/**
	 * Gibt an, bei welcher Runde die Animation beendet werden soll.
	 */
	private final int ende;

	/**
	 * Konstruktor der Animation.<br />
	 *
	 * @param ziel
	 * 		Das zu animierende Raum-Objekt
	 * @param richtung
	 * 		Die Richtung der Bewegung wird durch diese Punkt, den das Objekt bei seiner Bewegung
	 * 		treffen wird, und dem jetztigen Mittelpunkt des Objektes ab.<br /> <b>ACHTUNG:</b> Die
	 * 		Animation wird nicht beendet wenn dieser Punkt erreicht wurde, dies kann sowohl vorher als
	 * 		auch nachher geschehen! Wann haengt von dem Parameter <code>dauer</code> ab.
	 * @param geschwindigkeit
	 * 		Die Geschwindigkeit der Animation. Der Wert gibt an, wie lange es in Millisekunden dauern
	 * 		soll, bis die Animation das Zielobjekt hin zu dem Zielpunkt bewegt hat.
	 * @param dauer
	 * 		gibt an, wie viele <b>Millisekunden</b> diese Animation dauern soll, bevor sie abgebrochen
	 * 		wird.
	 * @param m
	 * 		Der Manager, ueber den die Animation laufen soll.
	 */
	public GeradenAnimierer (Raum ziel, Punkt richtung, int geschwindigkeit, int dauer, Manager m, AnimationsEndeReagierbar listener) {
		super(ziel, false, m, listener);
		bewegung = new Vektor(ziel.zentrum(), richtung).teilen(AnimationsManager.intervall(((float)geschwindigkeit) / ((float)Animierer.MILLISPERTICK)));
		if (dauer <= 0) {
			Logger.error("Die Dauer fuer die Geraden-Animation kann nie 0 oder negativ sein!!!");
			ende = 0;
		} else {
			ende = dauer / Animierer.MILLISPERTICK;
		}
	}

	/**
	 * In dieser Methode werden die individuellen Methoden fuer die verschiedenen Animierer
	 * festgehalten.<br /> Sie wird automatisch von der Super-Klasse <code>Animierer</code>
	 * aufgerufen, sooft, bis sie intern beendet oder angehalten wird.
	 *
	 * @see #tick()
	 * @see #beenden()
	 * @see #anhalten()
	 */
	@Override
	public void animationsSchritt () {
		ziel.bewegen(bewegung);
		if (count == ende) {
			beenden();
		}
	}
}
