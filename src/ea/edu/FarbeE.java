/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2017 Michael Andonie and contributors.
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

package ea.edu;

import java.awt.*;
import java.util.Locale;

/**
 * Die Klasse FarbeE ermöglicht ein sehr einfaches Handling mit allen Farben. Sie basiert auf der
 * Klasse <code>java.awt.Color</code>, erweitert diese jedoch aufgrund ihrer vielen finalen
 * Eigenschaften nicht, sondern arbeitet mit einer Referenz auf ein solches Objekt.
 *
 * @author Michael Andonie
 */
public final class FarbeE {
    /**
     * Der Wert dieser Farbe.
     */
    private final Color color;

    /**
     * Vereinfachter Konstruktor.<br /> Erstellt eine Farbe mit dem übergebenen RGB-Wert, die
     * vollkommen <b>undurchsichtig</b> ist.<br /> Für eine Erklärung der RGB/Alpha-Werte, siehe
     * den komplizierteren Konstruktor.
     *
     * @param rot   Der Rot-Anteil dieser Farbe (zwischen 0 und 255).
     * @param gruen Der Grün-Anteil dieser Farbe (zwischen 0 und 255).
     * @param blau  Der Blau-Anteil dieser Farbe (zwischen 0 und 255).
     *
     * @see #FarbeE(int, int, int, int)
     */
    public FarbeE(int rot, int gruen, int blau) {
        this(rot, gruen, blau, 255);
    }

    /**
     * Konstruktor für die Klasse FarbeE. Hier wird der Farbe der RGB-Wert zugeordnet, dies sind
     * 3 Zahlen <b>zwischen 0 und 255</b>, die jeweils die Menge der Komplementärfarbe in der
     * ganzen Farbe angeben. Ist der Wert 255, ist der Anteil der Farbe sehr stark. Ist er 0, ist
     * der Anteil leer.<br /> Weiterhin lässt sich auch der Alpha-Wert dieser Farbe bestimmen.
     * Dies bietet die großartige Möglichkeit auch durchsichtige Farben zu erstellen (z.B. für
     * Glaswände). Der Alpha-Wert funktioniert so wie die R/G/B-Werte, er nimmt eine Zahl
     * zwischen 0 und 255 ein, wobei bei 255 die Farbe vollkommen undurchsichtig ist und bei 0
     * unsichtbar.
     *
     * @param rot   Der Rot-Anteil dieser FarbeE (zwischen 0 und 255)
     * @param gruen Der Gruen-Anteil dieser Farben(zwischen 0 und 255)
     * @param blau  Der Blau-Anteil dieser FarbeE (zwischen 0 und 255)
     * @param alpha Die Alpha-Helligkeit der FarbeE (zwischen 0 und 255)
     */
    public FarbeE(int rot, int gruen, int blau, int alpha) {
        this.color = new Color(rot, gruen, blau, alpha);
    }

    /**
     * Interner Konstruktor.
     *
     * @param color Wert der Farbe.
     */
    private FarbeE(Color color) {
        this.color = color;
    }

    /**
     * Gibt ein <code>FarbeE</code>-Objekt aus, dass die selben Eigenschaften wie eine der
     * EA-Standardfarben hat.
     *
     * @param name Der Name der FarbeE.
     *
     * @return Das Farb-Objekt, das diese FarbeE beinhaltet.
     *
     * @see FarbeE#zuFarbeKonvertieren(String)
     */
    public static FarbeE vonString(String name) {
        return new FarbeE(zuFarbeKonvertieren(name));
    }

    /**
     * Diese Methode ordnet einem String ein Color-Objekt zu.<br /> Hierdurch ist in den Klassen
     * außerhalb der Engine keine awt-Klasse nötig.
     *
     * @param name Der Name der Farbe.<br /> Ein Katalog mit allen möglichen Namen findet sich im
     *             Wiki.
     *
     * @return Das Farbobjekt zum String. Ist Color.black bei unzuordnembaren String.
     */
    public static Color zuFarbeKonvertieren(String name) {
        Color color;

        switch (name.toLowerCase(Locale.GERMAN)) {
            case "gelb":
                color = Color.yellow;
                break;
            case "weiss":
                color = Color.white;
                break;
            case "orange":
                color = Color.orange;
                break;
            case "grau":
                color = Color.gray;
                break;
            case "gruen":
                color = Color.green;
                break;
            case "blau":
                color = Color.blue;
                break;
            case "rot":
                color = Color.red;
                break;
            case "pink":
                color = Color.pink;
                break;
            case "magenta":
            case "lila":
                color = Color.magenta;
                break;
            case "cyan":
            case "tuerkis":
                color = Color.cyan;
                break;
            case "dunkelgrau":
                color = Color.darkGray;
                break;
            case "hellgrau":
                color = Color.lightGray;
                break;
            default:
                color = Color.black;
                break;
        }

        return color;
    }

    /**
     * Gibt den internen Wert zurück.
     *
     * @return Der Wert der FarbeE als Color-Objekt.
     */
    public Color getColor() {
        return color;
    }
}