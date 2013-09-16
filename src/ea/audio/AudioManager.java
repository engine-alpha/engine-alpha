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

package ea.audio;

import ea.game.Kanal;
import ea.game.Manager;

/**
 * Der Audiomanager organisiert das Abspielen verschiedener Sounddateien.<br />
 * Er organisiert deren Abspielen ueber mehrere Threads.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class AudioManager
extends Manager
{
    /**
     * Die einzelnen Soundkanaele, die zur Verfuegung stehen.
     */
    private Kanal[] kanal;

    /**
     * Konstruktor
     * @param   anzahlKanaele   Die anzahl an Kanaelen, die das AudioSystem hat.<br />
     * Jeder Kanal ist ein eigenstaendiger Thread, ist also unabhaengig, jedoch Speicherlastig. Hier gilt
     * es zu erwaegen, wie viele Kanaele noetig sind, und wie viele das System ueberlasten wuerden.
     */
    public AudioManager(int anzahlKanaele){
        super("Audio-Manager");
        kanal = new Kanal[anzahlKanaele];
        for(int i = 0; i < kanal.length; i++) {
            kanal[i] = new Kanal(this);
        }
    }
    
    /**
     * Spielt ein Sound-Objekt ab. Wenn alle Kanaele belegt sind, wird eine Fehlermeldung mit empfehlung zur
     * Erhoehung der Kanalanzahl ausgegeben.<br />
     * Egal ob der Sound auf Dauerschleife laeuft oder nicht, ueber <code>anhalten(meinSound)</code> kann er jederzeit
     * wieder angehalten werden.
     * @param   s   Der abzuspielende Sound
     * @param   wiederholen Ob der Sound als dauerschleife gespielt werden soll.<br />
     * Dies bietet sich vor allem fuer Hintergrundmusik an, aber auch zum Beispiel fuer ein Maschinengewehrrattern.
     * @see anhalten( Sound )
     */
    public void abspielen(Sound s, boolean wiederholen) {
        Kanal k = null;
        for(int i = 0; i < kanal.length; i++) {
            if(kanal[i].frei()) {
                k = kanal[i];
            }
            if(kanal[i].spieltAb(s)) {
                k = null;
                break;
            }
        }
        if(k != null) {
            k.abspielen(s, wiederholen);
        }
    }
    
    /**
     * Spielt einen Sound <b>ohne Wiederholung</b> ab.<br />
     * Vereinfachte ersion der Methode <code>abspielen(Sound, boolean)</code>.
     * @param   s   Der abzuspielende Sound
     * @see abspielen(Sound, boolean)
     */
    public void abspielen(Sound s) {
        abspielen(s, false);
    }
    
    /**
     * Haelt einen Sound an.<br />
     * Dabei wird der Sound wieder zurueck zum Anfang gespult. Ist dies nicht gewuenscht, so bietet sich die Methode
     * <code>pausieren(Sound s)</code> an.
     * Wird der Sound momentan gar nicht abgespielt, passiert genau <b>gar nichts</b>.
     * @param   s   Der anzuhaltende Sound
     * @see pausieren(Sound)
     */
    public void anhalten(Sound s) {
        for(int i = 0; i < kanal.length; i++) {
            if(kanal[i].spieltAb(s)) {
                kanal[i].anhalten();
            }
        }
    }
    
    /**
     * Pausiert das Abspielen eines Sounds.<br />
     * Dabei wird der Sound nicht wieder zurueck zum Anfang gespult. Ist dies allerdings gewuenscht, so bietet sich die Methode 
     * <code>anhalten(Sound s)</code> an.
     * @param s
     * @see anhalten(Sound)
     */
    public void pausieren(Sound s) {
        for(int i = 0; i < kanal.length; i++) {
            if(kanal[i].spieltAb(s)) {
                kanal[i].pausieren();
            }
        }
    }

    /**
     * Haelt alle Kanaele zum Audio-abspielen an.<br />
     * Das heisst, ueber diesen Manager wird kein Sound mehr laufen, bis ein neuer Clip wieder abgespielt wird.
     */
    public void allesAnhalten() {
        for(int i = 0; i < kanal.length; i++) {
            kanal[i].anhalten();
        }
    }

    /**
     * Beendet jede moegliche Wirkung des Audiomanagers.
     */
    public void neutralize() {
        for(int i = 0; i < kanal.length; i++ )
            kanal[i].anhalten();
    }
}
