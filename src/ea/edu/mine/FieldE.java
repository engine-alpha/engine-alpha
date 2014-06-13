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
import java.lang.reflect.*;

/**
 * Diese Klasse beschreibt ein Minenfeld fuer das EDU-Spiel nach "Minesweeper".
 * @author Andonie
 * @since 2.1
 */
public abstract class FieldE {

    /**
     * Der Inhalt-Text dieses Feldes
     */
    private Text text;

    /**
     * Das aessere Rechteck.
     */
    private Rechteck aussen;

    /**
     * Das innere Rechteck
     */
    private Rechteck innen;

    /**
     * Die Figur im inneren
     */
    private Figur fig;

    /**
     * Aim des ONCLICK
     */
    private Object aim;

    /**
     * Die ONCLICK-Methode
     */
    private Method linksclick;

    /**
     * Die rechte ONCLICK-Methode
     */
    private Method rechtsclick;

    /**
     * Konstruktor eines Feldes fuer das "Minesweeper"-Spiel
     * @param x Die X-Koordinate (Ecke links oben)
     * @param y Die Y-Koordinate (Ecke links oben)
     * @param laenge Die Seitenlaenge des Feldes
     */
    public FieldE(final int x, final int y, final int laenge) {
        if(laenge < 10) {
            System.err.println("ACHTUNG! Die Laenge dieses Feldes ist laecherlich klein (" + laenge + " Pixel).");
            System.err.println("Ein groesseres stellt die Funktionsfaehigkeit sicher.");
        }
        Spiegel s = Spiegel.getSpiegel();
        aussen = new Rechteck(x, y, laenge, laenge);
        aussen.farbeSetzen(new Farbe(30, 30, 30));
        innen = new Rechteck(x+2, y+2, laenge-4, laenge-4);
        innen.farbeSetzen(new Farbe(60, 60, 60));
        text = new Text("", x+3, y+3);
        s.wurzel.add(aussen, innen, text);
        aim = this;
        Method[] meth = aim.getClass().getMethods();
        for(int i = 0; i < meth.length; i++) {
            if(meth[i].getName().equals("linksKlick")) {
                linksclick = meth[i];
            } else if (meth[i].getName().equals("rechtsKlick")) {
                rechtsclick = meth[i];
            }
        }
        s.maus().mausReagierbarAnmelden(new MausReagierbar() {
            @Override
            public void mausReagieren(int code) {
                klick();
            }
        }, aussen);
        s.maus().rechtsKlickReagierbarAnmelden(new RechtsKlickReagierbar() {
            public void rechtsKlickReagieren(int x, int y) {
                if(aussen.dimension().istIn(new Punkt(x, y))) {
                    try {
                        rechtsclick.invoke(aim, new Object[]{});
                    } catch (IllegalAccessException ex) {
                        System.err.println("Achtung! Der Zugriff auf die Methode fuer On-Klicks hat nicht funktioniert. BUG!");
                    } catch (InvocationTargetException ex) {
                        System.err.println("Achtung! Das Objekt, an dem die Methode aufzurufen war, besass selbige nicht. BUG!");
                    }
                }
            }
        });
    }

    /**
     * Diese Methode wird automatisch bei jedem Klick auf das aussere Rechteck aufgerufen.
     */
    private void klick() {
        try {
            linksclick.invoke(aim, new Object[]{});
        } catch (IllegalAccessException ex) {
            System.err.println("Achtung! Der Zugriff auf die Methode fuer On-Klicks hat nicht funktioniert. BUG!");
        } catch (InvocationTargetException ex) {
            System.err.println("Achtung! Das Objekt, an dem die Methode aufzurufen war, besass selbige nicht. BUG!");
        }
    }

    /**
     * Setzt die Farbe des inneren Rechtecks.
     * @param farbe Die neue Fuellfarbe des inneren Rechtecks als <code>String</code>.
     */
    public void fuellFarbeSetzen(String farbe) {
        innen.farbeSetzen(farbe);
    }

    /**
     * Setzt die Randfarbe neu.
     * @param   farbe   Die Neue Fuellfarbe des ausseren (Rand-)Rechtecks als <code>String</code>.
     */
    public void randFarbeSetzen(String farbe) {
        aussen.farbeSetzen(farbe);
    }
    
    /**
     * Setzt die Fuellfarbe des Textes neu.
     * @param farbe Die neue Fuellfarbe des Textes als <code>String</code>.
     */
    public void textFarbeSetzen(String farbe) {
        text.farbeSetzen(farbe);
    }

    /**
     * Setzt einen neuen Inhalt fuer den Text dieses
     * @param inhalt    Der neue Inhalt für den Text
     */
    public void textSetzen(String inhalt) {
        text.sichtbarSetzen(false);
        text.inhaltSetzen(inhalt);
        text.groesseSetzen(40);
        while(!innen.dimension().umschliesst(text.dimension()) && text.groesse() > 10) {
            text.groesseSetzen(text.groesse()-1);
        }
        text.mittelpunktSetzen(aussen.mittelPunkt());
        text.sichtbarSetzen(true);
    }

    /**
     * Bringt eine Figur - basierend auf einer Figurdatei - in die Mitte dieses
     * Fekdes ein. Mit <code>figurEntfernen()</code> kann die Figur entfernt
     * werden und durch erneuten Aufruf dieser Methode das Bild auch gewechselt
     * werden.
     * @param datei Der Name der Datei, die die darzustellende Figur beinhaltet.
     * Diese Datei sollte sich im Projektordner des BlueJ-Projektes befinden!
     * @see #figurEntfernen()
     */
    public void figurEinsetzen(String datei) {
        if(!datei.endsWith(".eaf")) {
            System.err.println("Achtung! Der eingegebene Dateiname endet nicht mit .eaf!");
            return;
        }
        if(fig != null) {
            Spiegel.getSpiegel().wurzel.entfernen(fig);
        }
        fig = new Figur(0, 0, datei);
        fig.faktorSetzen(1);
        do {
            fig.faktorSetzen(fig.animation()[0].faktor()+1);
            fig.mittelpunktSetzen(aussen.mittelPunkt());
        } while(aussen.dimension().umschliesst(fig.dimension()));
        fig.faktorSetzen(fig.animation()[0].faktor()-1);
        fig.mittelpunktSetzen(aussen.mittelPunkt());
        Spiegel.getSpiegel().wurzel.add(fig);
    }

    /**
     * Entfernt die Figur von dem Feld - sofern eine vorhanden war.
     * @see #figurEinsetzen(java.lang.String) 
     */
    public void figurEntfernen() {
        if(fig == null) {
            return;
        }
        Spiegel.getSpiegel().wurzel.entfernen(fig);
        fig = null;
    }
}
