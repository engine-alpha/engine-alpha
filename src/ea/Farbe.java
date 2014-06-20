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
import java.io.Serializable;

/**
 * Die Klasse Farbe ermöglicht ein sehr einfaches Handling mit allen Farben. Sie basiert auf der
 * Klasse <code>java.awt.Color</code>, erweitert diese jedoch aufgrund ihrer vielen finalen
 * Eigenschaften nicht, sondern arbeitet mit einer Referenz auf ein solches Objekt.
 * <p/>
 * <b>Ein Verwendungsbeispiel:</b> <pre> // Ein Rechteck Rechteck rechteck = new Rechteck(20, 20,
 * 100, 200);
 * <p/>
 * // Die Farbe erstellen, die das Rechteck erhalten soll (ein helles Grau) Farbe farbe = new
 * Farbe(200, 200, 200);
 * <p/>
 * // Die Farbe dem Rechteck uebergeben rechteck.farbeSetzen(farbe);
 * <p/>
 * // Das Rechteck an der Wurzel (hierzu siehe Klasse Knoten und Klasse Game) der Game-Klasse
 * anmelden, // um ihn auch im Fenster sehen zu koennen! wurzel.add(rechteck);
 * <p/>
 * // Geschachtelte Form, so empfiehlt es sich meistens eher (da platzsparender):
 * rechteck.farbeSetzen(new Farbe(200, 200, 200)); </pre>
 *
 * @author Michael Andonie
 */
public final class Farbe implements Serializable {
	/**
	 * Der Wert dieser Farbe
	 */
	private final Color wert;

	/**
	 * Konstruktor fuer die Klasse Farbe, hier wird der Farbe der RGB-Wert zugeordnet, dies sind die
	 * 3 Zahlen <b>zwischen 0 und 255</b>, die jeweils die Menge der Komplementaerfarbe in der
	 * ganzen Farbe angeben. Ist der Wert 255 ist der Anteil der Farbe sehr stark, ist er 0, ist der
	 * Anteil leer.<br /> Weiterhin laesst sich auch der Alpha-Wert dieser Farbe bestimmen. Dies
	 * bietet die grossartige Moeglichkeit auch durchsichtige Farben zu erstellen (zB fuer
	 * Glaswaende). Der Alpha-Wert funktioniert sso, wie die R/G/B-Werte, er nimmt eine Zahl
	 * zwischen 0 und 255 ein, wobei bei 255 die Farbe vollkommen undurchsichtig ist, und bei 0
	 * unsichtbar.
	 *
	 * @param r
	 * 		Der Rot-Anteil dieser Farbe (zwischen 0 und 255)
	 * @param g
	 * 		Der Gruen-Anteil dieser Farben(zwischen 0 und 255)
	 * @param b
	 * 		Der Blau-Anteil dieser Farbe (zwischen 0 und 255)
	 * @param alpha
	 * 		Die Alpha-Helligkeit der Farbe (zwischen 0 und 255)
	 */
	public Farbe (int r, int g, int b, int alpha) {
		wert = DateiManager.ausListe(new Color(r, g, b, alpha));
	}

	/**
	 * Vereinfachter Konstruktor.<br /> Erstellt eine Farbe mit dem uebergebenen RGB-Wert, die
	 * vollkommen <b>undurchsichtig</b> ist.<br /> Fuer eine Erklaerung der RGB/Alpha-Werte, siehe
	 * den Komplizierteren Konstruktor.
	 *
	 * @param r
	 * 		Der Rot-Anteil dieser Farbe (zwischen 0 und 255)
	 * @param g
	 * 		Der Gruen-Anteil dieser Farben(zwischen 0 und 255)
	 * @param b
	 * 		Der Blau-Anteil dieser Farbe (zwischen 0 und 255)
	 *
	 * @see #Farbe(int, int, int, int)
	 */
	public Farbe (int r, int g, int b) {
		this(r, g, b, 255);
	}

	/**
	 * Sonder-Konstruktor. Dieser wird nur Intern gebraucht.
	 *
	 * @param c
	 * 		Das JAVA-Farbobjekt, das diese Farbe beinhalten soll.
	 */
	public Farbe (Color c) {
		this.wert = c;
	}

	/**
	 * Gibt ein <code>Farbe</code>-Objekt aus, dass die selben Eigenschaften wie eine der
	 * EA-Standardfarben hat.
	 *
	 * @param s
	 * 		Der Name der Farbe.
	 *
	 * @return Das Farb-Objekt, das diese Farbe beinhaltet.
	 *
	 * @see Raum#zuFarbeKonvertieren(String)
	 */
	public static final Farbe vonString (String s) {
		return new Farbe(Raum.zuFarbeKonvertieren(s));
	}

	/**
	 * Gibt den Wert der Farbe aus.
	 *
	 * @return Der Wert der Farbe als Color-Objekt
	 */
	public Color wert () {
		return wert;
	}

	/**
	 * Gibt eine Farbe mit dem Halben Alpha-Wert dieser zurueck.
	 *
	 * @return eine Farbe desselbe Farbtons wie diese, jedoch doppelt so durchsichtig wie diese.
	 */
	public Farbe halbesAlpha () {
		return new Farbe(wert.getRed(), wert.getGreen(), wert.getBlue(), wert.getAlpha() / 2);
	}

	/**
	 * Gibt an, ob diese Farbe ueberhaupt nicht durchsichtig ist.
	 *
	 * @return <code>true</code>, wenn der Alpha-Wert der Farbe <b>nicht 255</b> ist, sonst
	 * automatisch <code>false</code>.
	 */
	public boolean undurchsichtig () {
		return wert.getAlpha() == 255;
	}
}