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

package ea.physics;

import ea.*;
import ea.animations.KollisionsReagierbar;
import ea.graphic.Vektor;
import ea.graphic.geo.BoundingRechteck;
import ea.graphic.geo.Raum;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;
/**
 * Ein Objekt der Klasse Physik behandelt eigenstaendig verschiedene Raum-Objekte als Physik-Engine.<br />
 * Grundlegend behandelt sie Trefferkollisionen eigenstaendig, also bietet es sich an, diese Eigenschaft zu nutzen, 
 * und zwar in dem Interface <code>KollisionsReagierbar</code>.<br /><br />
 * 
 * Weiterhin - und dies ist eine <b>essentielle Aufgabe fuer viele 2D-Spiele</b> - kann diese Maschine<br />
 * - An gewuenschtren Raum-Objekten Schwerkraft erzeugen<br />
 * - Aktive Raum-Objekte (<b>Aktivobjekte</b>, zum Beispiel Spielfiguren) so beeinlfussen, dass es fuer sie nicht moeglich ist, 
 * passive Raum-Objekte (<b>PassivObjekte</b>, zum Beispiel Mauer, Waende, Boeden) zu schneiden.<br /><br />
 * Diese beiden Eigenschaften in Kombination erzeugen die Moeglichkeit, sehr einfach eine funktionierende Spielewelt zu programmieren, 
 * in der bereits ein funktionierendes System zum Fallen und Grenzen abstecken existiert.<br /><br />
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Physik
extends Manager
implements Ticker {
    
    /**
     * Die Liste aller Kollisionstestauftraege
     */
    private ArrayList<Auftrag> kollisionsListe = new ArrayList<Auftrag>();

    /**
     * Eine Liste aller Passiven Objekte.
     */
    private CopyOnWriteArrayList<Passivator> passive = new CopyOnWriteArrayList<Passivator>();
    
    /**
     * Eine Liste aller Gravitatoren (indirekt Aktivobjekte)
     */
    private CopyOnWriteArrayList<Gravitator> gravitatoren = new CopyOnWriteArrayList<Gravitator>();

    /**
     * Der Rundenzaehler der Physik
     */
    private int runde = 1;

    /**
     * Die EINE Physik
     */
    private static Physik physik;
    
    /**
     * Neutralisiert die aktuelle Physik und macht Platz fuer eine neue.
     */
    public static void neutralize() {
        if(physik == null)
            return;
        //Beendet Berechnungen
        physik.kill();
        for(Passivator p : physik.passive) {
            p.ziel().neutralMachen();
        }
        for(Gravitator g : physik.gravitatoren) {
            g.ziel().neutralMachen();
        }
        physik = null;
    }

    /**
     * Konstruktor fuer Objekte der Klasse Physik
     * @param   kapazitaet  Gibt an, wie viele Kollisionspartner maximal merkbar sind
     */
    private Physik() {
        super("Physik-Management");
        Manager.standard.anmelden(new Ticker() {
            public void tick() {
                for(Auftrag a : kollisionsListe) {
                    a.test();
                }
            }
        }, 35);
        this.anmelden(this, 1);
    }

    /**
     * Realisierung eines <i>Singleton</i>. Da es nur eine Physik pro Anwendung gibt,
     * garantiert diese statische Methode, dass es nur ein Physik Objekt gibt, da keine
     * Physik-Objekte erstellt werden koennen.
     * @return  Das aktive Physik-Objekt.
     */
    public static final Physik getPhysik() {
        if(physik == null) {
            physik = new Physik();
        }
        return physik;
    }

    /**
     * Meldet einen Passivator an.
     * @param p Der Passivator, der anzumelden ist.
     */
    public void passivAnmelden(Passivator p) {
        passive.add(p);
    }

    /**
     * Meldet einen Passivator wieder ab - Vorausgesetzt er war auch angemeldet.
     * @param p Der abzumeldende Passivator
     */
    public void passivAbmelden(Passivator p) {
        passive.remove(p);
    }

    /**
     * Meldet einen Gravitator fuer Aktiv-Objekte an.
     * @param g Der anzumeldende Gravitator
     */
    public void aktivAnmelden(Gravitator g) {
        gravitatoren.add(g);
    }

    /**
     * Meldet einen Gravitator wieder ab - Vorausgesetzt er war auch angemeldet.
     * @param p Der abzumeldende Gravitator
     */
    public void aktivAbmelden(Gravitator g) {
        gravitatoren.remove(g);
    }

    /**
     * Setzt alle Aktiv-Objekte, die  eine bestimmte Flaeche uebertreten, in einen Knoten.
     * @param k In diesen Knoten werden alle Aktiv-Objekte, die die Flaeche betreten eingefuegt.
     * @param b Dieses BoundingRechteck beschreibt die kritische Flaeche.
     */
    public synchronized void alleAktivenEinsetzen(Knoten k, BoundingRechteck b) {
        for(Gravitator g : gravitatoren) {
            if(g.ziel().inFlaeche(b)) {
                k.add(g.ziel());
            }
        }
    }

    /**
     * Setzt alle Aktiv-Objekte, die  eine bestimmte Flaeche uebertreten, nicht jedoch nach der Verschiebung ein Passiv-Objekt
     * schneiden, in einen Knoten.
     * @param k In diesen Knoten werden alle Aktiv-Objekte, die die Flaeche betreten, nicht jedoch nach der Verschiebung
     * problematisch waeren eingefuegt.
     * @param b Dieses BoundingRechteck beschreibt die kritische Flaeche.
     * @param v Die kritische Verschiebung.
     */
    public synchronized void alleAktivenTestenUndEinsetzen(Knoten k, BoundingRechteck b, Vektor v) {
        for(Gravitator g : gravitatoren) {
            if(g.ziel().inFlaeche(b) && !inPassivem(g.ziel().dimension().verschobeneInstanz(v))) {
                k.add(g.ziel());
            }
        }
    }

    /**
     * Setzt alle Aktiv-Objekte, die  eine bestimmte Flaeche uebertreten, nicht jedoch nach der Verschiebung ein Passiv-Objekt
     * schneiden - mit einer bestimmten Ausnahme - in einen Knoten.
     * @param k In diesen Knoten werden alle Aktiv-Objekte, die die Flaeche betreten, nicht jedoch nach der Verschiebung
     * problematisch waeren eingefuegt.
     * @param b Dieses BoundingRechteck beschreibt die kritische Flaeche.
     * @param v Die kritische Verschiebung.
     * @param p Die eine Ausnahme als Passivator
     */
    public synchronized void alleAktivenTestenUndEinsetzenOhne(Knoten k, BoundingRechteck b, Vektor v, Passivator p) {
        for(Gravitator g : gravitatoren) {
            if(g.ziel().inFlaeche(b) && !inPassivemAusser(g.ziel().dimension().verschobeneInstanz(v), p)) {
                k.add(g.ziel());
            }
        }
    }

    /**
     * Prueft, ob eine Flaeche ein Passiv-Objekt schneidet.
     * @param r Die Flaeche der Ueberprueftung, als BoundingRechteck
     * @return  <code>true</code>, wenn diese Flaeche ein Passivobjekt schneidet,
     * sonst <code>false</code>.
     */
    public synchronized boolean inPassivem(BoundingRechteck r) {
        for(Passivator p : passive) {
            if(p.in(r)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt die Verschiebung zurueck, die noetig waere um das geblockte Bounding-Rechteck aus
     * seinem Zustand des Passiv-Blockiertseins zu loesen.
     * @param r Das zu entblockende BoundingRechteck
     * @return  Die Verschiebung, die noetig waere, um das BoundingRechteck aus dem Passiv-Geblocktsein
     * zu Loesen. Hat die Werte (0|0) fuer den Fall, dass das Bounding-Rechteck gar nicht passiv blockiert ist.
     */
    public synchronized Vektor entblocken(BoundingRechteck r) {
        for(Passivator p : passive) {
            if(p.in(r)) {
                int x = 0, y = 0;
                BoundingRechteck pas = p.ziel().dimension();
                if(pas.y < r.y && pas.y+pas.hoehe > r.y+r.hoehe) {
                    //X
                    if(r.x > pas.x) {
                        x = pas.x+pas.breite-r.x;
                    } else {
                        x = pas.x-(r.x+r.breite);
                    }
                } else {
                    //Y
                    if(r.y > pas.y) {
                        y = pas.y+pas.hoehe-r.y;
                    } else {
                        y = pas.y-(r.y+r.hoehe);
                    }
                }
                Vektor retA = new Vektor(x, y);
                return retA.summe(this.entblocken(r.verschobeneInstanz(retA), retA));
            }
        }
        return Vektor.NULLVEKTOR;
    }

    /**
     * Die Interne Block methode zum garantierten entblocken eines BoundingRechtecks OHNE
     * StackOverflow.
     * @param r Das zu entblockende BR
     * @param letzte Die letzte Verschiebung (aus der Methode <code>entblocken(Vektor)</code>)
     * @return  Die noch noetige Verschiebung, um das Bounding-Rechteck sicher zu entblocken.
     * @see entblocken(BoundingRechteck)
     */
    private synchronized Vektor entblocken(BoundingRechteck r, Vektor letzte) {
        for(Passivator p : passive) {
            if(p.in(r)) {
                int x = 0, y = 0;
                BoundingRechteck pas = p.ziel().dimension();
                if(letzte.x != 0) {
                    //X
                    if(letzte.x > 0) {
                        x = pas.x+pas.breite-r.x;
                    } else {
                        x = pas.x-(r.x+r.breite);
                    }
                } else {
                    //Y
                    if(letzte.y > 0) {
                        y = pas.y+pas.hoehe-r.y;
                    } else {
                        y = pas.y-(r.y+r.hoehe);
                    }
                }
                Vektor retA = new Vektor(x, y);
                return retA.summe(this.entblocken(r.verschobeneInstanz(retA), retA));
            }
        }
        return Vektor.NULLVEKTOR;
    }

    /**
     * Prueft, ob eine Flaeche ein Passiv-Objekt - bis auf eine Ausnahme schneidet.
     * @param r Die Flaeche der Ueberprueftung, als BoundingRechteck
     * @param aus Die eine Ausnahme, die bei den Kollisionstests nicht beruecksichtigt wird.
     * @return  <code>true</code>, wenn diese Flaeche ein Passivobjekt - ausser der einen Ausnahme - schneidet,
     * sonst <code>false</code>.
     */
    public synchronized boolean inPassivemAusser(BoundingRechteck r, Passivator aus) {
        for(Passivator p : passive) {
            if(p.equals(aus)) {
                continue;
            }
            if(p.in(r)) {
                return true;
            }
        }
        return false;
    }

    /**
     * In diesem Tick findet ein DELTA-t der Physik statt (= 1ms).
     */
    @Override
    public void tick() {
        for(Gravitator g : gravitatoren) {
            g.tick(runde);
        }
        if(runde == 10) {
            runde = 1;
        } else {
            runde++;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="KollisionReagierbar">
    /**
     * Meldet ein KollisionsReagierbar-Interface bei der Physik an. Zusammen mit den 2 auf Kollision zu ueberwachenden
     * Raum-Objekten sowie dem Code, der bei dem Aufruf beim Treffer zwischen den beiden mitgegeben werden soll.<br /><br /><br /><br />
     *
     * Die <code>kollision(int code)</code>-Methode des anzumeldenden <code>KollisionsReagierbar</code>-Interfaces wird ab sofort immer
     * dann aufgerufen wenn:<br />
     * 1. beide Raum-Objekte schneiden<br />
     * und 2. beide Raum-Objekte sichtbar sind<br /> <br /><br /><br /><br /><br />
     *
     * Diese Methode wird solange immer wieder aufgerufen, wie die Kollision besteht! Wird also in der <code>kollision(int code)</code>-Methode
     * nicht dafuer gesorgt, dass sich die Objekte nicht mehr schneiden, <b>so wird diese Methode wieder und wieder aufgerufen!</b>
     * <br /><br /><br /><br /><br /><br />
     *
     * <b>ACHTUNG</b><br />
     * Es sollten niemals 2 Knoten oder Geometrie (oder ein Knoten und ein Geometrie) - Objekte gleichzeitig angemeldet werden. Ist dies der
     * Fall, ist der Kollisionstest nicht so genau, wie er seien koennte.
     * @param   k   Das KollisionsReagierbar-Objekt, das benachrichtigt wird, wenn beide Objekte kollidieren
     * @param   r1  Der erste Raum-Teil. Kollidieren beide, so wird das KollisionsReagierbar-Objekt benachrichtigt
     * @param   r2  Der zweite Raum-Teil. Kollidieren beide, so wird das KollisionsReagierbar-Objekt benachrichtigt
     * @param   code    Der Code, der <b>dem <code>KollisionsReagierbar</code>-Objekt als Parameter in seiner reagieren()-Methode
     * mitgegeben werden soll</b>.
     */
    public void anmelden(KollisionsReagierbar k, Raum r1, Raum r2, int code) {
        if (r2 instanceof Knoten) {
            Raum r = r1;
            r1 = r2;
            r2 = r;
        }
        kollisionsListe.add(new Auftrag(r1, r2, k, code));
    }

    /**
     * Vereinfachte Form der anmelden()-Methode fuer Kollisionstests.<br />
     * Hierbei wird immer der Code </code>code = 0</code> bei der <code>reagieren()</code>-Methode mitgegeben, somit
     * ist beim Aufruf dieser Methode <b>keine Fallunterscheidung innerhalb eines <code>FallReagierbar</code>-Objektes moeglich!</b>
     * @param   k   Das KollisionsReagierbar-Objekt, das benachrichtigt wird, wenn beide Objekte kollidieren
     * @param   r1  Der erste Raum-Teil. Kollidieren beide, so wird das KollisionsReagierbar-Objekt benachrichtigt
     * @param   r2  Der zweite Raum-Teil. Kollidieren beide, so wird das KollisionsReagierbar-Objekt benachrichtigt
     * @see anmelden(KollisionsReagierbar, Raum, Raum, int)
     */
    public void anmelden(KollisionsReagierbar k, Raum r1, Raum r2) {
        anmelden(k, r1, r2, 0);
    }

    /**
     * Sorgt dafuer, das saemtliche Kollsiionsueberwachungsauftraege eines <code>KollisionsReagierbar</code>-Interfaces nicht mehr
     * ausgefuehrt werden.
     * @param k Das Interface, an dem jede Ueberwachung von Raum-Objekten abgebrochen werden soll.
     */
    public void entfernen(KollisionsReagierbar k) {
        ArrayList<Auftrag> out = new ArrayList<Auftrag>();
        for (Auftrag a : kollisionsListe) {
            if (a.benachrichtigt(k)) {
                out.add(a);
            }
        }
        for (Auftrag a : out) {
            kollisionsListe.remove(a);
        }
    }

    /**
     * Die Auftraege, der Kollisionstests
     */
    private final class Auftrag {
        //

        /**
         * Kollisionspartner eins/Ausgangspunkt der Kollisionstests
         */
        private final Raum r1;
        /**
         * Kollisionspartner 2
         */
        private final Raum r2;
        /**
         * Der Listener
         */
        private final KollisionsReagierbar listener;
        /**
         * Der Code dieses Auftrags
         */
        private final int code;

        /**
         * Konstruktor
         * @param r1    Koll-Data 1
         * @param r2    Koll-Data 2
         * @param k     Listener
         * @param code  Code
         */
        public Auftrag(Raum r1, Raum r2, KollisionsReagierbar k, int code) {
            this.r1 = r1;
            this.r2 = r2;
            listener = k;
            this.code = code;
        }

        /**
         * Fuert einen Kollisionstest durch
         */
        public void test() {
            if (r1.schneidet(r2) && r1.sichtbar() && r2.sichtbar()) {
                listener.kollision(code);
            }
        }

        /**
         *
         * @param k
         * @return  TRUE, wenn dieser Listener benachrichtigt wird
         */
        public boolean benachrichtigt(KollisionsReagierbar k) {
            return listener == k;
        }
    }// </editor-fold>
}
