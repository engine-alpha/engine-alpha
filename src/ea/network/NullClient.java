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

package ea.network;

import ea.FallReagierbar;
import ea.physics.PhysikClient;
import ea.graphic.geo.Raum;
import ea.StehReagierbar;
import ea.graphic.Vektor;

/**
 * Der Client fuer nicht vorhandene Teilnahme an der Physik.<br />
 * Standartmaessig der Initialclient.
 * @author Andonie
 */
public class NullClient 
extends PhysikClient {

    /**
     * Konstruktor
     * @param ziel  Das Ziel des Clients.
     */
    public NullClient(Raum ziel) {
        super(ziel);
    }

    /**
     * Bewegt das Ziel-Objekt ohne wenn und aber.
     * @param v Der Vektor, der die Bewegung beschreibt.
     * @return Immer <code>true</code>, da die Bewegung eines physikalisch neutralen Objektes immer in vollem
     * Zuge moeglich ist!
     */
    @Override
    public boolean bewegen(Vektor v) {
        ziel.verschieben(v);
        return true;
    }

    /**
     * Diese Methode wird immer dann aufgerufen, wenn ein Client nicht weiter benoetigt
     * wird, und er alle seine Funktionen beenden soll, um die von ihm belegten Ressourcen
     * freizugeben.
     */
    @Override
    public void aufloesen() {
        //Nichts zu tun
    }

    /**
     * Die ueberschriebene Sprung-Methode. Da sie nicht benoetigt wird, wird hierbei
     * eine Fehlermeldung ausgegeben. Denn es wurde ein nichtaktives Objekt zum Sprung
     * gebracht.
     * @param kraft Die (theoretische) Sprungkraft.
     */
    @Override
    public boolean sprung(int kraft) {
        System.err.println("Achtung! Ein fuer die Physik neutrales Objekt wurde zum Sprung gezwungen. Es passiert nichts.");
        return false;
    }

    /**
     * Diese Methode soll setzen, ob Schwerkraft aktiv ist. Dies macht jedoch bei einem Neutral-Objekt keinen Sinn.<br />
     * Daher wird nur eine Fehlermeldung ausgegeben.
     * @param aktiv Ob die Schwerkraft aktiv sein soll. Ist jedoch hier absolut irrelevant.
     */
    @Override
    public void schwerkraftAktivSetzen(boolean aktiv) {
        System.err.println("Achtung! Das Objekt, bei dem der Einfluss der Schwerkraft gesetzt werden sollte, ist ein neutrales " +
                "Objekt. Folglich macht der Aufruf dieser Methode keinen Sinn. Dafuer muesste das entsprechende Objekt ein Aktiv-Objekt sein!");
    }

    /**
     * Diese Methode setzt die kritische Tiefe eines Aktiv-Objektes. Aber hierin wird nur eine Fehlermeldung ausgegeben,
     * da bei dieser Klasse ein neutrales Objekt vorliegt, das keine kritische Falltiefe haben kann
     * @param tiefe Die Tiefe, ab der das anliegende <code>FallReagierbar</code>-Interface informiert werden soll.
     * @see fallReagierbarAnmelden( ea.FallReagierbar , int)
     */
    @Override
    public void kritischeTiefeSetzen(int tiefe) {
        System.err.println("Achtung! Das Raum-Objekt, dem eine kritische Tiefe gegeben werden sollte, ist kein Aktiv-Objekt, sondern neutral! " +
                "Bitte erst dieses Objekt aktiv machen, dann diese Methode aufrufen!");
    }

    /**
     * In dieser Methode wird der <code>FallReagierbar</code>-Listener angemeldet.<br />
     * Aber hierin wird nur eine Fehlermeldung ausgegeben, 
     * da bei dieser Klasse ein neutrales Objekt vorliegt, das keine kritische Falltiefe haben kann.
     * @param f     Das <code>FallReagierbar</code>-Objekt, das ab sofort im Grenzfall informiert wird.
     * @param tiefe Die kritische Tiefe, ab der das Interface informiert wird.
     * @see kritischeTiefeSetzen
     */
    @Override
    public void fallReagierbarAnmelden(FallReagierbar f, int tiefe) {
        System.err.println("Achtung! Das Raum-Objekt, dem ein FallReagierbar-Listener zugewiesen werden sollte, ist kein Aktiv-Objekt, sondern neutral! " +
                "Bitte erst dieses Objekt aktiv machen, dann diese Methode aufrufen!");
    }

    /**
     * In diese Methode wird ein <code>StehReagierbar</code>-Listener angemeldet.<br />
     * Aber in dieser Klasse wird nur eine Fehlermeldung ausgegeben, da das zu ueberwachende Objekt neutral und nicht aktiv ist.
     * @param s Der theoretisch anzumeldende Listener.
     */
    @Override
    public void stehReagierbarAnmelden(StehReagierbar s) {
        System.err.println("Achtung! Das Raum-Objekt, dem ein StehReagierbar-Listener zugewiesen werden sollte, ist kein Aktiv-Objekt, sonder ein " +
                "neutrales Objekt! Bitte erst dieses Objekt aktiv machen, dann diese Methode aufrufen!");
    }

    /**
     * Soll testen, ob das Ziel-Objekt steht. <br />
     * Gibt jedoch eine Fehlermeldung aus, da das Ziel-Objekt kein Aktiv-Objekt ist und damit nicht Stehen/Fallen kann.
     * @return  Immer <code>false</code>, da die Eigenschaft stehen in diesem Fall nicht konsistent definierbar ist.
     */
    @Override
    public boolean steht() {
        System.err.println("Achtung! Das Raum-Objekt, an dem das Stehen erfragt werden sollte, ist kein Aktiv-Objekt, sonder ein " +
                "neutrales Objekt! Bitte erst dieses Objekt aktiv machen, dann diese Methode aufrufen! Solange ist die Rueckgabe immer false.");
        return false;
    }

    /**
     * Setzt die Schwerkraft fuer dieses Objekt.<br />
     * Da dies jedoch bei einem neutralen Objekt nicht moeglich ist,
     * gibt es eine Fehlermeldung.
     * @param schwerkraft Der Wert fuer die Schwerkraft der Physik.<br />
     * <b>Wichtig:</b> Dies repraesentiert <i>keinen</i> Wert fuer die (Erd-)
     * Beschleunigungszahl "g" aus der Physik. Schon allein deshalb, weil die 
     * Zahl umgekehrt wirkt (s. oben).
     * @see ea.graphic.geo.Raum#aktivMachen()
     */
    @Override
    public void schwerkraftSetzen(int schwerkraft) {
        System.err.println("Achtung! Ein neutrales Raum-Objekt sollte eine neue Schwerkraft verpasst bekommen. Das ist nicht moeglich. "
                + "Nur Aktiv-Objekte koennen eine Schwerkraft gesetzt bekommen.");
    }
}
