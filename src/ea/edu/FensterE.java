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
        this(808, 629, "Darstellung");
    }
    
    /**
     * Konstruktor Typ 2: Freie Wahl der Fenstereigenschaften.
     * @param breite	Die gewünschte Breite.
     * @param hoehe		Die gewünschte Höhe.
     * @param titel		Der gewünschte Titel.
     */
    private FensterE(int breite, int hoehe, String titel) {
    	super(breite, hoehe, titel);
    }

    /**
     * Gibt das "edu"-Fenster aus. Dies funktioniert nach dem <i>Singleton</i>-Prinzip:<br /><br />
     * - <b>Alle Objekte</b> aus dem <code>edu</code>-Paket sind in DIESEM Fenster angezeigt.<br />
     * - Es gibt nur <b>ein einziges</b> Fenster in jedem laufenden Programm. Dieses Fenster wird beim
     * ersten Aufruf dieser Methode erstellt.
     * @return  Das Fenster, in dem alle "edu"-Grafiken wiedergegeben werden.
     */
    public static final FensterE getFenster() {
        return getFenster(808, 629);
    }
    
    /**
     * Gibt das "edu"-Fenster aus. Dies funktioniert nach dem <i>Singleton</i>-Prinzip:<br /><br />
     * - <b>Alle Objekte</b> aus dem <code>edu</code>-Paket sind in DIESEM Fenster angezeigt.<br />
     * - Es gibt nur <b>ein einziges</b> Fenster in jedem laufenden Programm. Dieses Fenster wird beim
     * ersten Aufruf dieser Methode erstellt.
     * @param breite	Die Wunschbreite, falls noch kein Fenster erstellt wurde.
     * @param hoehe		Die Wunschhöhe, falls noch kein Fenster erstellt wurde.
     * @return  Das Fenster, in dem alle "edu"-Grafiken wiedergegeben werden.
     */
    public static final FensterE getFenster(int breite, int hoehe) {
    	if(fenster == null) {
            return fenster = new FensterE(breite, hoehe, "Darstellung");
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
