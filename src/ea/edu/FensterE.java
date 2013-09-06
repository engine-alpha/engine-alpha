/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
 *
 * Copyright (C) 2011  Michael Andonie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ea.edu;

import ea.*;

/**
 * Das Standartspielfenster fuer Unterricht. Bei diesem "<code>Game</code>-Objekt" wie
 * auch bei dem gesamten Paket <code>ea.edu</code> handelt es sich um eine <b>drastische
 * Reduktion</b> der ohnehin aeusserst geringen Anforderungen.<br />
 * Entwickelt als <i>Singleton</i>, da dies das Generalfenster aller "edu"-Grafiken ist.
 * @author Andonie
 */
public class FensterE 
extends Game {

    /**
     * Referenz auf das Fenster. Nicht anfechtbar; ebenfalls nach <i>Singleton</i>.
     */
    private static FensterE fenster;

    /**
     * Konstruktor. Private, da dies als <i>Singleton</i> gehandhabt wird.
     */
    private FensterE() {
        super(808, 629, "Darstellung");
    }

    /**
     * Gibt das "edu"-Fenster aus. Dies funktioniert nach dem <i>Singleton</i>-Prinzip:<br /><br />
     * - <b>Alle Objekte</b> aus dem <code>edu</code>-Paket sind in DIESEM Fenster angezeigt.<br />
     * - Es gibt nur <b>ein einziges</b> Fenster in jedem laufenden Programm. Dieses Fenster wird beim
     * ersten Aufruf dieser Methode erstellt.
     * @return  Das Fenster, in dem alle "edu"-Grafiken wiedergegeben werden.
     */
    public static final FensterE getFenster() {
        if(fenster == null) {
            return fenster = new FensterE();
        }
        return fenster;
    }

    /**
     * Diese Methode sorgt fuer das Reagieren auf Tastendruck. Dies jedoch ist bei den "edu"-Klassen
     * bisher noch nicht noetig.
     * @param code  Der Code, der die gedrueckte Taste repraesentiert.
     */
    public void tasteReagieren(int code) {
        //
    }
}
