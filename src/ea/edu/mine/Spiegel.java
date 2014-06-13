/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
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

package ea.edu.mine;

import ea.*;

/**
 * Diese Interne Klasse ermoeglicht folgendes:<br />
 * Das sich aus der Hauptklasse ableitende Spiel ist minimal umfangreich. Die volle Funktionsfaehigkeit der Klasse
 * <code>Game</code> wird hier nach aussen nicht zugaenglich gemacht. Aus folgenden Gruenden:<br />
 * - Der volle Funktionsumfang soll in der echten Engine bleiben, damit ausreichender Reiz besteht, nicht innerhalb der beschraenkten
 * EDU-Version zu bleiben.<br />
 * - Weniger Verwirrung in der Analyse der moeglichen Methode.
 * @see ea.edu.mine.Spiegel
 */
public class Spiegel
extends Game
implements KlickReagierbar, RechtsKlickReagierbar {

    /**
     * Das MINE-Game
     */
    private final MineGameE mine;

    /**
     * Die aktive Maus des Spiels.
     */
    private final Maus maus;

    /**
     * Die aktuellste Spiegelinstanz
     */
    private static Spiegel instanz = null;

    /**
     * Konstruktor eines Spiegels.
     * @param mine  Das MINE-Game
     * @param breiteF Fensterbreite
     * @param hoeheF Fensterhoehe
     * @param titelF Fenstertitel
     */
    public Spiegel(MineGameE mine, int breiteF, int hoeheF, String titelF) {
        super(breiteF, hoeheF, titelF);
        this.mine = mine;
        maus = new Maus(3, false, false);
        mausAnmelden(maus);
        maus.klickReagierbarAnmelden(this);
        maus.rechtsKlickReagierbarAnmelden(this);
    }

    /**
     * Die Methode, um auf den aktiven Spiegel allgemein zugreifen zu koennen.
     * @return  Die aktive Spiegel-Instanz.
     */
    public static final Spiegel getSpiegel() {
        if(instanz == null) {
            instanz = new ErrorSpiegel();
            return instanz;
        }
        return instanz;
    }

    /**
     * Singelton-aehnliche Methode zum erhalten des korrekten Spiegels. Garantiert, das nur eine
     * Instanz eine <code>Game</code>-Objekt im Minesweeper-Projekt vorliegt.
     * @param mine  Das MINE-Game
     * @param breiteF Fensterbreite
     * @param hoeheF Fensterhoehe
     * @param titelF Fenstertitel
     * @return Der aktive Spiegel bzw. ein neuer, jetzt aktiver Spiegel gemaess den Parametern.
     */
    public static final Spiegel getSpiegel(MineGameE mine, int breiteF, int hoeheF, String titelF) {
        if(instanz == null) {
            return instanz = new Spiegel(mine, breiteF, hoeheF, titelF);
        }
        System.err.println("Achtung! Es existiert schon ein Spiel! Ein zweites kann nicht erstellt werden.");
        return instanz;
    }

    /**
     * Diese Methode gibt die aktive Maus aus.
     * @return  Eine Referenz auf die aktive Maus des Spielfensters dieses Spiegels.
     */
    public Maus maus() {
        return maus;
    }

    /**
     * Die Methode zum "echten" Reagieren auf den Klick im Spiel.<br />
     * Wird jedoch direkt an die falsche Game-Klasse <code>MineGameE</code> weitergeleitet.
     * @param x Die X-Koordinate des Klicks
     * @param y Die Y-Koordinate des Klicks
     */
    @Override
    public void klickReagieren(int x, int y) {
        mine.klick(x, y);
    }

    /**
     * Tasten-Reaktionsmethode.
     * @param code Der Tastencode.
     * @see ea.Game#tasteReagieren(int)
     */
    @Override
    public void tasteReagieren(int code) {
        mine.taste(code);
    }

    /**
     * Rechtsklick-Reaktionsmethode
     * @param x Die X-Koordinate des Klicks
     * @param y Die Y-Koordinate des Klicks
     */
    @Override
    public void rechtsKlickReagieren(int x, int y) {
        mine.klickR(x, y);
    }

    
}

/**
 * Ein Errorspiegel ist ein Fenster mit klarer Fehlermeldung.<br />
 * Es signalisiert dem Programmierer, dass ein Feld erstellt wurde, bevor
 * ein Fenster erstellt wurde.
 */
class ErrorSpiegel
extends Spiegel {

    /**
     * Erstellt einen Error-Spiegel
     */
    public ErrorSpiegel() {
        super(new MineGameE() { }, 500, 500, "FEHLER!!!");
        Rechteck r = super.fensterGroesse().ausDiesem();
        r.farbeSetzen("Rot");
        Text t = new Text("FEHLER! Erst muss eine eigene", 10, 230);
        Text t2 = new Text("Spielklasse erstellt werden!", 10, 260);
        t.schriftartSetzen(1);
        t2.schriftartSetzen(1);
        wurzel.add(r, t, t2);
        nachrichtSchicken("Achtung! Als erstes muss ein Spiel erstellt werden, dann koennen Spielelemente korrekt instanziiert werden!");
    }
}