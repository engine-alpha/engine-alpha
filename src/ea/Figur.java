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

import java.util.ArrayList;
/**
 * Eine Figur ist eine aus einer Datei geladene Sammlung von Pixeln, die orientierungsmaessig rechteckig gehandelt werden.<br />
 * <code>
 * //Die Figur laden<br />
 * Figur meineFigur = new Figur(30, 100, "meineFigurDateiImProjektordner.eaf"); //Laedt die Figur und setzt sie an Position (30|100) <br /><br />
 *  //Die Bewegung der Figur starten (muss nicht ausgefuehrt werden, Sa standard) <br />
 * meineFigur.animiertSetzen(true);<br /><br />
 * // Die Figur an dem entsprechenden Knoten zum halten und Zeichnen anmelden (In diesem Fall die Wurzel in der Klasse Game)<br />
 * wurzel.add(meineFigur);<br />
 * </code><br /><br /><br />
 * Dies ist einfachste Methode, eine Figur zu laden.<br />
 * Der Figureneditor zum Erstellen der zu ladenden ".eaf"-Dateien ist als ausfuehrbare ".jar"-Datei fester Bestandteil des Engine-Alpha-Programmierkits.
 * 
 * @author Michael Andonie
 * @version 1
 */
public class Figur
extends Raum
{
    /**
     * Die Position X dieser Figur<br />
     * Die Position gibt den links oberen Rand der Figur an.
     */
    private int x;

    /**
     * Die Position Y dieser Figur<br />
     * Die Position gibt den links oberen Rand der Figur an.
     */
    private int y;

    /**
     * In diesem Intervall wird die Figur animiert.
     */
    private int intervall = 100;

    /**
     * Die einzelnen Bilder der Figur.<br />
     * hat es mehr als eines, so wird ein periodischer Wechsel vollzogen.
     */
    protected PixelFeld[] animation;

    /**
     * Der Index des Aktuelle benutzten PixelFeldes.
     */
    private int aktuelle = 0;

    /**
     * Gibt an, ob die Figur gerade animiert werden soll.
     */
    private boolean laeuft;

    /**
     * Eine Liste aller Figuren.
     */
    private static ArrayList<Figur> liste;
    
    /**
     * Gibt an, ob diese Figur gerade entlang der X-Achse (waagrecht) gespiegelt wird.
     */
    private boolean spiegelX = false;

    /**
     * Gibt an, ob diese Figur gerade entlang der Y-Achse (senkrecht) gespiegelt wird.
     */
    private boolean spiegelY = false;

    static {
        liste = new ArrayList<Figur>();
        Manager.standard.anmelden((new Ticker() {
            int runde = 0;

            public void tick() {
                runde++;
                try {
                    for(Figur f : liste) {
                        if(f.animiert()) {
                            f.animationsSchritt(runde);
                        }
                    }
                } catch(java.util.ConcurrentModificationException e) {
                    //
                }
            }
        }), 1);
    }

    /**
     * Besonderer Konstruktor fuer Objekte der Klasse <code>Figur</code>. Dieser
     * Konstruktor wird vor allem intern (fuer Actionfiguren) verwendet. Anders ist
     * nur die Option darauf, dass die Figur am Animationssystem direkt teilnimmt.
     * Dies ist beim Standart-Konstruktor immer der Fall.
     * @param   x   X-Position; die links obere Ecke
     * @param   y   Y-Position; die links obere Ecke
     * @param   verzeichnis Das verzeichnis, aus dem die Figur zu laden ist.
     * @param   add Ob diese Figur am Animationssystem direkt teilnehmen soll. (Standard)
     */
    public Figur(int x, int y, String verzeichnis, boolean add) {
        super();
        this.x = x;
        this.y = y;
        Figur spiegel = DateiManager.figurEinlesen(verzeichnis);
        this.animation = spiegel.animation;
        if(add) {
            liste.add(this);
        }
        laeuft = true;
    }

    /**
     * Standart-Konstruktor fuer Objekte der Klasse <code>Figur</code>.
     * @param   x   X-Position; die links obere Ecke
     * @param   y   Y-Position; die links obere Ecke
     * @param   verzeichnis Das verzeichnis, aus dem die Figur zu laden ist.
     */
    public Figur(int x, int y, String verzeichnis) {
        this(x, y, verzeichnis, true);
    }

    /**
     * Der parameterlose Konstruktor.<br />
     * Hiebei wird nichts gesetzt, die Figur hat die Position (0|0) sowie keine Animationen, die Referenz auf die einzelnen Pixelfelder ist <code>null</code>.<br />
     * Dieser Konstruktor wird intern verwendet, um Figurdaten zu laden.
     */
    public Figur() {
        this.x = 0;
        this.y = 0;
        liste.add(this);
    }

    /**
     * Loescht ein Animationsbild an einem bestimmten Index und rueckt den Rest nach.
     * @param   index   Der Index des zu loeschenden Einzelbildes.<br />
     * Dieser muss natuerlich im Bereich der Groesse an PixelFeldern liegen.
     * @throws  ArrayIndexOutOfBoundsException wenn der Index falsch gewaehlt wurde!
     */
    public void animationLoeschen(int index) {
        animation[index] = null;
        for(int i = index; i < animation.length-1; i++) {
            animation[i] = animation[i+1];
        }
        PixelFeld[] neu = new PixelFeld[animation.length-1];
        for(int i = 0; i < neu.length; i++) {
            neu[i] = animation[i];
        }
        aktuelle = 0;
        animation = neu;
    }
    
    /**
     * Setzt das Animationsbild auf einer bestimmten Position zu einem neuen PixelFeld.
     * @param   bild    Das neue PixelFeld an dieser Position; sollte nicht <code>null</code> sein!!
     * @param   index   Der Index des zu ersetzenden Bildes.
     * @throws  ArrayIndexOutOfBoundsException wenn der Index falsch gewaehlt wurde!
     */
    public void animationsBildSetzen(PixelFeld bild, int index) {
        animation[index] = bild;
    }
    
    /**
     * Verschiebt die Position eines Animationsbildes.<br />
     * Hierbei wird ein bisschen mit den Werten des Arrays gespielt, jedoch kein neues Array erstellt. Sind beide Eingabeparameter exakt gleich, 
     * passiert gar nichts, auch wenn die beiden Werte ausserhalb des Arrays liegen sollten.
     * @param   indexAlt    Der Index des zu verschiebenden Bildes
     * @param   indexNeu    Der Index, den das Bild nach dem erschieben haben soll.
     * @throws  ArrayIndexOutOfBoundsException wenn einer der Indizes falsch gewaehlt wurde!
     */
    public void animationsBildVerschieben(int indexAlt, int indexNeu) {
        if(indexAlt == indexNeu) {
            return;
        }
        PixelFeld bild = animation[indexAlt];
        animation[indexAlt] = null;
        if(indexAlt > indexNeu) {
            for(int i = indexAlt; i > indexNeu; i--) {
                animation[i] = animation[i-1];
            }
        } else { //___________ indexNeu > indexAlt
            for(int i = indexAlt; i < indexNeu; i++) {
                animation[i] = animation[i+1];
            }
        }
        animation[indexNeu] = bild;
    }
    
    /**
     * Ruft das naechste Bild im Animationszyklus auf.<br />
     * Sollte nicht von aussen aufgerufen werden, stellt aber in keinem mathematisch greifbaren Fall ein Problem dar.
     * @param runde Die Runde dieses Schrittes; dieser Wert wird intern benoetigt, um zu entscheiden, ob etwas passieren soll oder nicht.
     */
    public void animationsSchritt(int runde) {
        if(runde%intervall != 0) {
            return;
        }
        if(aktuelle == animation.length-1) {
            aktuelle = 0;
        } else {
            aktuelle++;
        }
    }
    
    /**
     * Setzt eine neue Animationsreihe.
     * @param   f   Die neue Animationsreihe. Das Array muss mindestens ein Pixelfeld zum Inhalt haben<br />
     * Diese <b>muss</b> aus Pixelfeldern gleicher Masse bestehen!!
     */
    public void animationSetzen(PixelFeld[] f) {
        animation = f;
        aktuelle = 0;
    }

    /**
     * Setzt die Animationsarbeit bei dieser Figur.
     * @param   animiert    ob die Figur animiert werden soll, oder ob sie ein Standbild sein soll.
     */
    public void animiertSetzen(boolean animiert) {
        this.laeuft = animiert;
    }

    /**
     * Gibt an, ob das Bild momentan animiert wird bzw. animiert werden soll.
     */
    public boolean animiert() {
        return laeuft;
    }

    /**
     * Setzt das aktuelle Animationsbild.<br />
     * Die Figur, die im Pixelfeldeditor erstellt wurde besteht den Bildern (1, 2, ..., n), aber <b>ACHTUNG</b>, die Indizes fangen bei <b>0</b>
     * an und hoeren dann eins frueher auf (0, 1, ..., (n-1)). Das heisst, dass wenn als Index <code>5</code> eingegeben wird, ist das Bild
     * gemeint, das im Figureneditor als <code>Bild 6</code> bezeichnet wurde.
     * @param bildIndex Der Index des anzuzeigenden Bildes
     */
    public void animationsBildSetzen(int bildIndex) {
        if(bildIndex >= animation.length) {
            System.err.println("Achtung! Der zu setzende Bildindex war groesser als der groesste vorhandene Index!! " +
                    "Daher wird nichts gesetzt.");
            return;
        }
        aktuelle = bildIndex;
    }

    /**
     * Setzt den Groessenfaktor dieser Figur neu.<br />
     * Das heisst, ab sofort wird ein Unterquadrat dieser Figur
     * eine Seitenlaenge von Pixeln in Groesse des Faktors haben
     * @param faktor    Der neue Groessenfaktor
     */
    public void faktorSetzen(int faktor) {
        for(int i = 0; i < animation.length; i++) {
            animation[i].faktorSetzen(faktor);
        }
    }

    /**
     * Setzt saemtlicher Farbwerte saemtlicher Bilder der Figur ins negative.<br />
     * Dadurch aendert sich die Erscheinung der Figur.
     * @see heller()
     * @see dunkler()
     * @see farbenTransformieren(int, int, int)
     */
    public void negativ() {
        for(int i = 0; i < animation.length; i++) {
            animation[i].negativ();
        }
    }

    /**
     * Hellt alle Farbwerte der Figur auf.<br />
     * Gegenstueck zur Methode <code>dunkler()</code>.<br />
     * <b>Achtung:</b><br />
     * Wegen Rundungsfehlern muss der Aufruf von <code>dunkler()</code> nach dem Aufruf 
     * von <code>heller()</code> nicht zwanghaft zum urspruenglichen Zustand fuehren!
     * @see dunkler()
     * @see negativ()
     * @see farbenTransformieren(int, int, int)
     */
    public void heller() {
        for(int i = 0; i < animation.length; i++) {
            animation[i].heller();
        }
    }

    /**
     * Dunkelt alle Farbwerte der Figur ab.<br />
     * Gegenstueck zur Methode <code>heller()</code>.<br />
     * <b>Achtung:</b><br />
     * Wegen Rundungsfehlern muss der Aufruf von <code>dunkler()</code> nach dem Aufruf
     * von <code>heller()</code> nicht zwanghaft zum urspruenglichen Zustand fuehren!
     * @see heller()
     * @see negativ()
     * @see farbenTransformieren(int, int, int)
     */
    public void dunkler() {
        for(int i = 0; i < animation.length; i++) {
            animation[i].dunkler();
        }
    }

    /**
     * Sorgt fuer eine Farbtransformation.<br />
     * Das heißt, zu jeder Farbe der Figur werden die RGB-Werte um feste Betraege geaendert (positive oder negative).
     * Sprengt ein entstehender Wert den Rahmen [0; 255] (zum Beispiel 160 + 100 oder 50 - 80), so bleibt der Farbwert am 
     * Rand des moeglichen Bereiches (also 0 bzw. 255).
     * @param r Der Rot-Aenderungswert (positiv und negativ moeglich)
     * @param g Der Gruen-Aenderungswert (positiv und negativ moeglich)
     * @param b Der Blau-Aenderungswert (positiv und negativ moeglich)
     * @see heller()
     * @see dunkler()
     * @see negativ()
     */
    public void farbenTransformieren(int r, int g, int b) {
        for(int i = 0; i < animation.length; i++) {
            animation[i].transformieren(r, g, b);
        }
    }

    /**
     * Faerbt alle Elemente in einer Farbe ein.<br />
     * Dieser Zustand laesst sich zuruecksetzen mit der Methode <code>zurueckFaerben()</code>.
     * @param f Die Farbe, mit der alle farbenthaltenden Unterquadrate der Figur eingefaerbt werden.
     * @see zurueckFaerben()
     * @see einfaerben(String)
     */
    public void einfaerben(Farbe f) {
        for(int i = 0; i < animation.length; i++) {
            animation[i].einfaerben(f.wert());
        }
    }

    /**
     * Faerbt alle Elemente in einer Farbe ein.<br />
     * Dieser Zustand laesst sich zuruecksetzen mit der Methode <code>zurueckFaerben()</code>.
     * @param farbe Die Farbe, mit der alle farbenthaltenden Unterquadrate der Figur eingefaerbt werden.<br />
     * Eingabe als <code>String</code>, so wie bei den anderen einfachen Farbeingaben auch.
     * @see zurueckFaerben()
     * @see einfaerben(Farbe)
     */
    public void einfaerben(String farbe) {
        einfaerben(Farbe.vonString(farbe));
    }

    /**
     * Setzt, ob diese Figur bei der Darstellung waagrecht zentral gespiegelt werden soll oder nicht.<br />
     * Dies aendert die Drehungsrichtung einer Figur von N nach S bzw. umgekehrt.
     * @param spiegel   Ist dieser Wert <code>true</code>, so wird die Figur waagrecht gespiegelt im Vergleich zu ihrer
     * Quelldatei dargestellt. Durch <code>false</code> kann dieser Zustand schnell wieder zurueckgesetzt werden.
     * @see #spiegelYSetzen(boolean)
     * @see #yGespiegelt()
     * @see #xGespiegelt()
     */
    public void spiegelXSetzen(boolean spiegel) {
        this.spiegelX = spiegel;
    }

    /**
     * Setzt, ob diese Figur bei der Darstellung senkrecht zentral gespiegelt werden soll oder nicht.<br />
     * So laesst sich extrem schnell z.B. Drehung einer Spielfigur von links nach rechts im Spiel realisieren.
     * @param spiegel   Ist dieser Wert <code>true</code>, so wird die Figur senkrecht gespiegelt im Vergleich zu ihrer
     * Quelldatei dargestellt. Durch <code>false</code> kann dieser Zustand schnell wieder zurueckgesetzt werden.
     * @see #spiegelXSetzen(boolean)
     * @see #yGespiegelt()
     * @see #xGespiegelt()
     */
    public void spiegelYSetzen(boolean spiegel) {
        this.spiegelY = spiegel;
    }

    /**
     * Diese Methode gibt aus, ob diese Figur derzeit an der X-Achse (waagrecht) gespiegelt ist.
     * @return  Ist dieser Wert <code>true</code>, wird diese Figur derzeit genau an der X-Achse gespiegelt dargestellt, im
     * Verhältnis zu der ursprünglichen Figurdatei.
     * @see #spiegelXSetzen(boolean)
     * @see #spiegelYSetzen(boolean)
     * @see #yGespiegelt()
     */
    public boolean xGespiegelt() {
        return spiegelX;
    }

    /**
     * Diese Methode gibt aus, ob diese Figur derzeit an der Y-Achse (senkrecht) gespiegelt ist.
     * @return  Ist dieser Wert <code>true</code>, wird diese Figur derzeit genau an der Y-Achse gespiegelt dargestellt, im
     * Verhältnis zu der ursprünglichen Figurdatei.
     * @see #spiegelXSetzen(boolean)
     * @see #spiegelYSetzen(boolean)
     * @see #xGespiegelt()
     */
    public boolean yGespiegelt() {
        return spiegelY;
    }


    /**
     * Sorgt dafuer, dass nach dem Aufruf von <code>einfaerben(Farbe)</code> die Figur wieder
     * ihre normalen Farbgegebenheiten kriegt.
     * @see #einfaerben()
     */
    public void zurueckFaerben() {
        for(int i = 0; i < animation.length; i++) {
            animation[i].zurueckFaerben();
        }
    }

    /**
     * Verschiebt das Objekt.
     * @param   v   Der Vektor, der die Verschiebung des Objekts angibt.
     */
    @Override
    public void verschieben(Vektor v) {
        this.x += v.x;
        this.y += v.y;
    }

    /**
     * Diese Methode wird verwendet, um die Figur vom direkten Animationssystem zu 
     * loesen. Sie ist <i>package private</i>, da diese Einstellung nur intern vorgenommen 
     * werden soll.
     */
    void entfernen() {
        liste.remove(this);
    }

    /**
     * Test, ob ein anderes Raum-Objekt von diesem geschnitten wird.
     * @param   r   Das Objekt, das auf Kollision mit diesem getestet werden soll.
     * @return  <code>true</code>, wenn sich beide Objekte schneiden. <code>false</code>, wenn nicht.
     */
    @Override
    public boolean schneidet(Raum r) {
        if(roh) {
            return this.dimension().schneidetBasic(r.dimension());
        } else {
            BoundingRechteck[] a = this.flaechen();
            BoundingRechteck[] b = r.flaechen();
            for(int i = 0; i < a.length; i++) {
                for(int j = 0; j < b.length; j++) {
                    if(a[i].schneidetBasic(b[j])) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    @Override
    public BoundingRechteck[] flaechen() {
        return animation[aktuelle].flaechen(x, y);
    }

    /**
     * Zeichnet das Objekt.
     * @param   g   Das zeichnende Graphics-Objekt
     * @param   r    Das BoundingRechteck, dass die Kameraperspektive Repraesentiert.<br />
     *                         Hierbei soll zunaechst getestet werden, ob das Objekt innerhalb der Kamera liegt, und erst dann gezeichnet werden.
     */
    @Override
    public void zeichnen(java.awt.Graphics g, BoundingRechteck r) {
        if(r.schneidetBasic(this.dimension())) {
            animation[aktuelle].zeichnen(g, x-r.x, y-r.y, spiegelX, spiegelY);
        }
    }

    /**
     * @return  Ein BoundingRechteck mit dem minimal noetigen Umfang, um das Objekt <b>voll einzuschliessen</b>.
     */
    @Override
    public BoundingRechteck dimension() {
        if(animation != null && animation[aktuelle] != null)
            return new BoundingRechteck(x, y, animation[0].breite(), animation[0].hoehe());
        else
            return new BoundingRechteck(x, y, animation[aktuelle].breite(), animation[aktuelle].hoehe());
    }

    /**
     * Gibt den Index des aktuellen Bildes zurueck.<br />
     * Die Figur, die im Pixelfeldeditor erstellt wurde besteht den Bildern (1, 2, ..., n), aber <b>ACHTUNG</b>, die Indizes fangen bei <b>0</b> 
     * an und hoeren dann eins frueher auf (0, 1, ..., (n-1)). Das heisst, dass wenn als Index <code>5</code> zurueckgegeben wird, wird zur Zeit 
     * das Bild gezeigt, das im Figureneditor als <code>Bild 6</code> bezeichnet wurde.
     * @return  Der Index des aktuell angezeigten Bildes
     */
    public int aktuellesBild() {
        return aktuelle;
    }

    /**
     * @return  Alle PixelFelder der Animation.
     */
    public PixelFeld[] animation() {
        return animation;
    }
    
    /**
     * Gibt das Intervall dieser Figur zurueck.
     * @return  Das Intervall dieser Figur. Dies ist die Zeit in Millisekunden, die ein Animationsbild 
     * zu sehen bleibt
     * @see animationsGeschwindigkeitSetzen(int)
     */
    public int intervall() {
        return intervall;
    }
    
    /**
     * Setzt die Geschwindigkeit der Animation, die diese Figur Figuren steuert.<br />
     * Jed groesser Die Zahl ist, desto langsamer laeuft die Animation, da der Eingabeparamter die Wartezeit zwischen der Schaltung der Animationsbilder 
     * in Millisekunden angibt!
     * @param   intervall   Die Wartezeit in Millisekunden zwischen den Bildaufrufen dieser Figur.
     */
    public void animationsGeschwindigkeitSetzen(int intervall) {
        this.intervall = intervall;
    }
}
