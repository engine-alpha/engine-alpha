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

package ea;

import ea.audio.Sound;

import javax.sound.sampled.*;
import java.io.IOException;

/**
 * Diese Klasse ist diejenige, die das eigentliche abspielen auf einem eigenen Thread, aus dem sie sich ableitet,<br />
 * Auf einem Kanal kann eine Sounddatei abgespielt werden.<br /><br />
 * Auch bietet diese Klasse eine Moeglichkeit, mp3-Dateien nach selbst verfasstem Algorythmus wiederzugeben, jedoch ist dieser nicht so 
 * konsistent und vielfaeltig wie der fuer ".wav"-Dateien, daher werden ".wav"-Dateien am ehesten fuer die Soundwiedergabe in der Engine empfohlen
 */
public class Kanal
implements Ticker
{
    /**
     * Ob dieser Kanal gerade eine Sounddatei abspielt.
     */
    private boolean frei = true;

    /**
     * Gibt an, ob das aktuelle Abspielen gestoppt werden soll.
     */
    private boolean stoppen = false;
    
    /**
     * Gibt an, ob der abzuspielende Sound wiederholt werden soll.
     */
    private boolean wiederholen;

    /**
     * Gibt an, ob der Sound zurueckgespult werden soll oder nciht.
     */
    private boolean pause;

    /**
     * Der Sound zum Abspielen
     */
    private volatile Sound s;
    
    /**
     * Der Manager, an dem der Kanal anliegt
     */
    private Manager manager;
    
    /**
     * Der AudioInputStream, der beim mp3-Abspielen verwendet wird
     */
    private AudioInputStream stream;
    
    /**
     * Die SourceDataLine fuer mp3-Abspielen
     */
    private SourceDataLine line;
    
    /**
     * Der Zaehler aller gelesenen Bytes (mp3)
     */
    private int nBytesRead = 0;
    
    /**
     * Der Zaehler aller geschriebenen Bytes (mp3)
     */
    private int nBytesWritten = 0;
    
    /**
     * Das eingelesen und zu schreibende byste-Array bei mp3-Dateien
     */
    private byte[] data;
    
    /**
     * Gibt an, ob das Abspielen der mp3-Datei fertig ist
     */
    private boolean mp3Fertig = false;
    
    /**
     * Gibt an, ob dieser kanal eine mp3-Datei wiedergibt.
     */
    private boolean machtMP3 = false;
    
    
    
    /**
     * Der Standartkonstruktor.
     * @param   m   Der Manager, an dem der Kanal anliegen soll
     */
    public Kanal(Manager m) {
        m.anmelden(this);
        this.manager = m;
    }
   
    /**
     * Diese Methode beginnt den Vorgang des Abspielens des an diesem Kanal anliegenden Sound-Objekts.
     */
    public void anfangen() {
        //Dateiformat rausfinden
        int time = 25;
        switch(s.typ()) {
            case Sound.MIDI:
                s.sequencer().start();
                break;
            case Sound.WAV:
                s.clip().start();
                break;
            case Sound.MP3:
                time = 1;
                machtMP3 = true;
                break;
        }
        //Entsprechende resourcen holen (nicht laden!)
        //in do-while()-Schleife abspielen
        manager.starten(this, time);
    }
    
    public void tick() {
        boolean fertig = true;
        if(s == null) {
            return;
        }
        switch(s.typ()) {
            case Sound.MIDI:
               fertig = (!s.sequencer().isRunning());
               break;
            case Sound.WAV:
                fertig = (!s.clip().isRunning());
                break;
            case Sound.MP3:
                fertig = mp3Schritt();
                if(fertig) { //Aufraeumen
                    mp3Ende();
                }
                break;
        }
        if(!fertig && !stoppen) {
            return;
        }
        boolean jetztAnhalten = true;
        if(stoppen) {
            jetztAnhalten = true;
            wiederholen = false;
        } else {
            jetztAnhalten = (!wiederholen);
        }
        if(wiederholen) {
            switch(s.typ()) {
                case Sound.MIDI:
                    s.sequencer().setTickPosition(0);
                    s.sequencer().start();
                    break;
                case Sound.WAV:
                    s.clip().setFramePosition(0);
                    s.clip().start();
                    break;
                 case Sound.MP3:
                    mp3Start(s);
                    break;
            }
            return;
        }
        if(!jetztAnhalten) {
            return;
        }
        switch(s.typ()) {
            case Sound.MIDI:
                s.sequencer().stop();
                if(!pause)
                    s.sequencer().setTickPosition(0);
                break;
            case Sound.WAV:
                s.clip().stop();
                if(!pause)
                    s.clip().setFramePosition(0);
                break;
        }
        manager.anhalten(this);
        s = null;
        stoppen = false;
        frei = true;
    }
    
    /**
     * Spielt eine Sounddatei nach erfolgreichem Fehlertest ab.
     * @param   s   Der abzuspielende Sound.
     */
    public void abspielen(Sound s, boolean wiederholen) {
        if(!frei) {
            System.err.println("Auf diesem Kanal wird bereits etwas abgespielt!");
        }
        else if(!s.funktioniert()) {
            System.err.println("Achtung! Das angegebene Verzeichnis des Sounds ist nicht eine unterstuetze Sounddatei!");
        }
        else {
            this.frei = false;
            this.wiederholen = wiederholen;
            this.s = s;
            this.anfangen();
        }
    }
    
    /**
     * Haelt das abspielen an.<br />
     * Laeuft der Thread gerade nicht, ist dieser Aufruf wirkungslos.
     */
    public void anhalten() {
        stoppen = true;
        wiederholen = false;
        pause = false;
    }
    
    /**
     * Pausiert das Abspielen. Spult den Sound aber nciht zurueck.
     */
    public void pausieren() {
        stoppen = true;
        wiederholen = false;
        pause = true;
    }

    /**
     * Testet, ob ein bestimmter Sound gerade abgespielt wird.<br  />
     * Zugrunde liegt der einfache Test mit dem Gleichheitsoperator '=='. Zurueckgegeben wird also einfach
     * <code>(this.s == s)</code>.
     * @param   s   Der Sound, auf den getestet werden soll, ob er gerade abgespielt wird.
     * @return  <code>true</code>, wenn der Sound gerade abgespielt wird.
     */
    public boolean spieltAb(Sound s) {
        return (this.s == s);
    }
    
    /**
     * @return  <code>true</code>, wenn der Kanal gerade nicht abspielt.
     */
    public boolean frei() {
        return frei;
    }
    
    /**
     * Dies ist die einzige Methode, die beim Anfang einer MP3-Datei abgespielt wird.
     * @param   s   Der Sound, der die MP3-Datei wiederspiegelt.
     */
    private void mp3Start(Sound s) {
        data = new byte[4096];
        line = s.getLine();
        stream = s.stream();
    }
    
    /**
     * Methode zum Loeschen aller Ressourcen nach dem mp3-Abspielen.
     */
    private void mp3Ende() {
        data = null;
        line.drain();
        line.stop();
        line.close();
        try {
            stream.close();
        } catch(IOException e) {
            System.err.println("Fehler beim Schliessen des AudioStreams!");
        }
    }
    
    /**
     * Diese Methode vollfuehrt einen Abspielschritt einer mp3-Datei
     * @return  <code>true</code>, wenn die Datei nun fertig abgespielt ist.
     */
    private boolean mp3Schritt() {
        try {
            nBytesRead = stream.read(data, 0, data.length);
            if (nBytesRead != -1) nBytesWritten = line.write(data, 0, nBytesRead);
        } catch (IOException e) {
            System.err.println("Fehler bei der Ausgabe der Datei");
        }
        return (nBytesRead == -1);
    }
}