/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2023 Michael Andonie and contributors.
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

import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Diese Klasse liefert Methoden, die <b>zufällig verteilte Rückgaben</b> haben.
 */
@API
public final class Random {
    /**
     * Privater Konstruktor.
     */
    @Internal
    private Random() {
        // Es sollen keine Instanzen dieser Klasse erstellt werden.
    }

    /**
     * Gibt einen <b>zufälligen</b> <code>boolean</code>-Wert zurück.<br> Die Wahrscheinlichkeiten für
     * <code>true</code> bzw. <code>false</code> sind gleich groß.
     *
     * @return Mit 50 % Wahrscheinlichkeit <code>false</code>, mit 50 % Wahrscheinlichkeit
     * <code>true</code>.
     */
    @API
    public static boolean toggle() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    /**
     * Gibt einen <b>zufälligen</b> <code>int</code>-Wert zwischen <code>0</code> und einer festgelegten Obergrenze
     * zurück.<br> Die Wahrscheinlichkeiten für die Werte zwischen
     * <code>0</code> und der Obergrenze sind gleich groß.
     *
     * @param upperLimit Die höchste Zahl, die im Ergebnis vorkommen kann.
     *
     * @return Eine Zahl <code>getX</code>, wobei <code>0 &lt;= getX &lt;= upperLimit</code> gilt. Die Wahrscheinlichkeit für
     * alle möglichen Rückgaben ist <i>gleich groß</i>.
     */
    @API
    public static int range(int upperLimit) {
        if (upperLimit < 0) {
            throw new IllegalArgumentException("Achtung! Für eine Zufallszahl muss die definierte Obergrenze (die inklusiv in der Ergebnismenge ist) eine nichtnegative Zahl sein!");
        }

        return ThreadLocalRandom.current().nextInt(upperLimit + 1);
    }

    /**
     * Gibt einen <b>zufälligen</b> <code>int</code>-Wert zwischen einer festgelegten Unter- und Obergrenze
     * zurück.<br> Die Wahrscheinlichkeiten für die Werte zwischen Unter- und Obergrenze sind gleich groß.
     *
     * @param lowerLimit Die niedrigste Zahl, die im Ergebnis vorkommen kann.
     * @param upperLimit Die höchste Zahl, die im Ergebnis vorkommen kann.
     *
     * @return Eine Zahl <code>x</code>, wobei <code>lowerLimit &lt;= x &lt;= upperLimit</code> gilt. Die Wahrscheinlichkeit für
     * alle möglichen Rückgaben ist <i>gleich groß</i>.
     */
    @API
    public static int range(int lowerLimit, int upperLimit) {
        if (lowerLimit == upperLimit) {
            return lowerLimit;
        } else if (lowerLimit < upperLimit) {
            return lowerLimit + ThreadLocalRandom.current().nextInt(upperLimit - lowerLimit + 1);
        } else {
            return upperLimit + ThreadLocalRandom.current().nextInt(lowerLimit - upperLimit + 1);
        }
    }

    /**
     * Gibt einen <b>zufälligen</b> <code>float</code>-Wert im Intervall <code>[0;1)</code> zurück. Die
     * Wahrscheinlichkeit ist für alle möglichen Werte in diesem Intervall gleich groß.
     *
     * @return Ein <code>float</code>Wert im Intervall <code>[0;1]</code>. Die Wahrscheinlichkeit für alle möglichen
     * Rückgaben ist <i>gleich groß</i>.
     */
    @API
    public static float range() {
        return ThreadLocalRandom.current().nextFloat();
    }

    /**
     * Gibt einen <b>zufälligen</b> <code>float</code>-Wert zwischen einer festgelegten Unter- und Obergrenze
     * zurück.<br> Die Wahrscheinlichkeiten für die Werte zwischen Unter- und Obergrenze sind gleich groß.
     *
     * @param lowerLimit Die niedrigste Zahl, die im Ergebnis vorkommen kann.
     * @param upperLimit Die höchste Zahl, die im Ergebnis vorkommen kann.
     *
     * @return Eine Zahl <code>x</code>, wobei <code>lowerLimit &lt;= x &lt;= upperLimit</code> gilt. Die Wahrscheinlichkeit für
     * alle möglichen Rückgaben ist <i>gleich groß</i>.
     */
    @API
    public static float range(float lowerLimit, float upperLimit) {
        if (lowerLimit == upperLimit) {
            return lowerLimit;
        } else if (lowerLimit < upperLimit) {
            return lowerLimit + ThreadLocalRandom.current().nextFloat() * (upperLimit - lowerLimit);
        } else {
            return upperLimit + ThreadLocalRandom.current().nextFloat() * (lowerLimit - upperLimit);
        }
    }
}
