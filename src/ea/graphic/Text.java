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

package ea.graphic;

import ea.game.Manager;
import ea.graphic.geo.BoundingRechteck;
import ea.graphic.geo.Raum;
import ea.graphic.lights.Leuchtend;
import ea.graphic.windows.Fenster;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Zur Darstellung von Texten im Programmbildschirm.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Text
extends Raum
implements Leuchtend
{
    /**
     * Die X-Koordinate des Anfangs des Textes
     */
    protected int x;
    
    /**
     * Die Y-Koordinate des Anfangs des Textes
     */
    protected int y;
    
    /**
     * Die Schriftgroesse des Textes
     */
    protected int groesse;
    
    /**
     * Die Schriftart (<b>fett, kursiv, oder fett  & kursiv</b>).<br />
     * Dies wird dargestellt als int.Wert:<br />
     * 0:   Normaler Text<br />
     * 1:   Fett<br />
     * 2:   Kursiv<br />
     * 3:   Fett & Kursiv 
     */
    protected int schriftart;
    
    /**
     * Der Wert des Textes.
     */
    protected String inhalt;
    
    /**
     * Der Font der Darstellung
     */
    protected Font font;
    
    /**
     * Die Farbe, in der der Text dargestellt wird.
     */
    protected Color farbe;
    
    /**
     * Referenz auf die Farbe, die vor dem leuchten da war (zum wiederherstellen)
     */
    private Color alte;
    
    /**
     * Gibt an, ob dieser Text gerade leuchtet
     */
    private boolean leuchtet = false;
    
    /**
     * Der Zaehler fuer die Leuchtanimation
     */
    private int leuchtzaehler;

    /**
     * Ein Feld aller existenten Fonts, die im Hauptprojektordner gespeichert sind.<br />
     * Macht das interne verwenden dieser Fonts moeglich, ohne das Vorhandensein der Fonts in den 
     * Computerressourcen selber zur Voraussetzung zu haben.
     */
    private static Font[] eigene;

    /**
     * static-Konstruktor.<br />
     * hier werden die externen Fonts geladen.
     */
    static {
        ArrayList<File> alleFonts =new ArrayList<File>();
        fontsEinbauen(alleFonts, new File(System.getProperty("user.dir")));
        File[] unter = alleFonts.toArray(new File[] {});
        eigene = new Font[unter.length];
        for(int i = 0; i < unter.length; i++) {
            try {
                FileInputStream s = new FileInputStream(unter[i]);
                eigene[i] = Font.createFont(Font.TRUETYPE_FONT, s);
                s.close();
            } catch(FileNotFoundException e) {
                System.err.println("Interner Lesefehler. Dies haette unter keinen Umstaenden passieren duerfen.");
            } catch(FontFormatException e) {
                System.err.println("Das TrueType-Font-Format einer Datei (" + unter[i].getPath() + ") war nicht einlesbar!!");
            } catch(IOException e) {
                System.err.println("Lesefehler beim Laden der eigenen Fonts! Zugriffsrechte ueberpruefen.");
            }
        }
    }

    private static void fontsEinbauen(final ArrayList<File> liste, File akt) {
        File[] files = akt.listFiles();
        for(int i = 0; i < files.length; i++) {
            if(files[i].equals(akt)) {
                System.err.println("Das Sub-Directory war das Directory selbst. Das darf nicht passieren!");
                continue;
            }
            if(files[i].isDirectory()) {
                fontsEinbauen(liste, files[i]);
            }
            if(files[i].getName().endsWith(".ttf")) {
                liste.add(files[i]);
            }
        }
    }

    /**
     * Konstruktor fuer Objekte der Klasse Text<br />
     * Moeglich ist es auch, Fonts zu laden, die im Projektordner sind.
     *  Diese werden zu Anfang einmalig geladen und stehen dauerhaft zur Verfuegung.
     * @param   inhalt  Die Zeichenkette, die dargestellt werden soll
     * @param   x   Die X-Koordinate des Anfangs
     * @param   y   Die Y-Koordinate des Anfangs
     * @param   fontName    Der Name des zu verwendenden Fonts.<br />
     * Wird hierfuer ein Font verwendet, der in dem Projektordner vorhanden sein soll, <b>und dies ist immer
     * und in jedem Fall zu empfehlen</b>, muss der Name der Schriftart hier ebenfalls einfach nur eingegeben werden, <b>nicht der Name der
     * schriftart-Datei!!!!!!!!!!!!!!!!!!!!!!!!</b>
     * @param   schriftGroesse  Die Groesse, in der die Schrift dargestellt werden soll
     * @param   schriftart  Die Schriftart dieses Textes. Folgende Werte entsprechen folgendem:<br />
     * 0:   Normaler Text<br />
     * 1:   Fett<br />
     * 2:   Kursiv<br />
     * 3:   Fett & Kursiv <br /><br />
     * Alles andere sorgt nur fuer einen normalen Text.
     * @param   farbe   Die Farbe, die fuer den Text benutzt werden soll.
     */
    public Text(String inhalt, int x, int y, String fontName, int schriftGroesse, int schriftart, String farbe)
    {
        this.inhalt = inhalt;
        this.x = x;
        this.y = y;
        this.groesse = schriftGroesse;
        this.farbe = zuFarbeKonvertieren(farbe);
        if(schriftart >= 0 && schriftart <= 3) {
            this.schriftart = schriftart;
        } else {
            this.schriftart = 0;
        }
        setzeFont(fontName);
        super.leuchterAnmelden(this);
    }

    /**
     * Konstruktor ohne Farb- und sonderartseingabezwang. In diesem Fall ist die Farbe "Weiss" und der Text weder kursiv noch fett.
     * @param   inhalt  Die Zeichenkette, die dargestellt werden soll
     * @param   x   Die X-Koordinate des Anfangs
     * @param   y   Die Y-Koordinate des Anfangs
     * @param   fontName    Der Name des zu verwendenden Fonts.<br />
     * Wird hierfuer ein Font verwendet, der in dem Projektordner vorhanden sein soll, <b>und dies ist immer
     * und in jedem Fall zu empfehlen</b>, muss der Name der Schriftart hier ebenfalls einfach nur eingegeben werden.
     */
    public Text(String inhalt, int x, int y, String fontName, int schriftGroesse) {
        this(inhalt, x, y, fontName, schriftGroesse, 0, "Weiss");
    }


    /**
     * Ebenefalls ein vereinfachter Konstruktor. Hierbei ist die Farbe "Weiss" und der Text weder kursiv noch fett; weiterhin ist 
     * die Schriftgroesse automatisch 24.
     * @param   inhalt  Die Zeichenkette, die dargestellt werden soll
     * @param   x   Die X-Koordinate des Anfangs
     * @param   y   Die Y-Koordinate des Anfangs
     * @param   fontName    Der Name des zu verwendenden Fonts.<br />
     * Wird hierfuer ein Font verwendet, der in dem Projektordner vorhanden sein soll, <b>und dies ist immer
     * und in jedem Fall zu empfehlen</b>, muss der Name der Schriftart hier ebenfalls einfach nur eingegeben werden, <b>nicht der Name der 
     * schriftart-Datei!!!!!!!!!!!!!!!!!!!!!!!!</b>
     */
    public Text(String inhalt, int x, int y, String fontName) {
        this(inhalt, x, y, fontName, 24);
    }

    /**
     * Einfacherer Konstruktor.<br />
     * Hierbei wird automatisch die Schriftart auf eine Standartmaessige gesetzt
     * @param   inhalt  Die Zeichenkette, die dargestellt werden soll
     * @param   x   Die X-Koordinate des Anfangs
     * @param   y   Die Y-Koordinate des Anfangs
     * @param   schriftGroesse  Die Groesse, in der die Schrift dargestellt werden soll
     */
    public Text(String inhalt, int x, int y, int schriftGroesse) {
        this(inhalt, x, y, "SansSerif", schriftGroesse, 0, "Weiss");
    }
    
     /**
     * Ein vereinfachter Konstruktor.<br />
     * Hierbei wird eine Standartschriftart, die Farbe weiss und eine Groesse von 24 gewaehlt.
     * @param   inhalt  Der Inhalt des Textes
     * @param   x   X-Koordinate
     * @param   y   Y-Koordinate
     */
    public Text(String inhalt, int x, int y) {
        this(inhalt, x, y, "SansSerif", 24, 0, "Weiss");
    }

    /**
     * Ein vereinfachter parallerer Konstruktor.<br />
     * Diesen gibt es inhaltlich genauso bereits, jedoch sind hier die Argumente vertauscht; dies dient der Praevention undgewollter falscher
     * Konstruktorenaufrufe.
     * Hierbei wird eine Standartschriftart, die Farbe weiss und eine Groesse von 24 gewaehlt.
     * @param   inhalt  Der Inhalt des Textes
     * @param   x   X-Koordinate
     * @param   y   Y-Koordinate
     */
    public Text(int x, int y, String inhalt) {
        this(inhalt, x, y, "SansSerif", 24, 0, "Weiss");
    }

    /**
     * Ein vereinfachter parallerer Konstruktor.<br />
     * Diesen gibt es inhaltlich genauso bereits, jedoch sind hier die Argumente vertauscht; dies dient der Praevention undgewollter falscher
     * Konstruktorenaufrufe.
     * Hierbei wird eine Standartschriftart und die Farbe weiss gewaehlt.
     * @param   inhalt  Der Inhalt des Textes
     * @param   x   X-Koordinate
     * @param   y   Y-Koordinate
     * @param   schriftGroesse  Die Schriftgroesse, die der Text haben soll
     */
    public Text(int x, int y, int schriftGroesse, String inhalt) {
        this(inhalt, x, y, "SansSerif", schriftGroesse, 0, "Weiss");
    }
    
    /**
     * Setzt den Inhalt des Textes.
     * @param   inhalt  Der neue Inhalt des Textes
     */
    public void setzeInhalt(String inhalt) {
        this.inhalt = inhalt;
    }
    
    /**
     * Setzt den Inhalt des Textes.<br />
     * Parallele Methode zu <code>setzeInhalt()</code>
     * @param   inhalt  Der neue Inhalt des Textes
     * @see setzeInhalt(String)
     */
    public void inhaltSetzen(String inhalt) {
        setzeInhalt(inhalt);
    }
    
    /**
     * Setzt die Schriftart.
     * @param   art Die Repraesentation der Schriftart als Zahl:<br/>
     * 0:   Normaler Text<br />
     * 1:   Fett<br />
     * 2:   Kursiv<br />
     * 3:   Fett & Kursiv<br /><br />
     * Ist die Eingabe nicht eine dieser 4 Zahlen, so wird nichts geaendert.
     */
    public void setzeSchriftart(int art) {
        if(art >= 0 && art <= 3) {
            schriftart = art;
            aktualisieren();
        }
    }
    
    /**
     * Setzt die Schriftart.
     * @param   art Die Repraesentation der Schriftart als Zahl:<br/>
     * 0:   Normaler Text<br />
     * 1:   Fett<br />
     * 2:   Kursiv<br />
     * 3:   Fett & Kursiv<br /><br />
     * Ist die Eingabe nicht eine dieser 4 Zahlen, so wird nichts geaendert.<br />
     * Parallele Methode zu <code>setzeSchriftart()</code>
     * @see setzeSchriftart(int)
     */
    public void schriftartSetzen(int art) {
        setzeSchriftart(art);
    }
    
    /**
     * Setzt die Fuellfarbe
     * @param   c   Die neue Fuellfarbe
     */
    public void setzeFarbe(Color c) {
        farbe = c;
        aktualisieren();
    }
    
    /**
     * Setzt die Fuellfarbe
     * @param   farbe   Der Name der neuen Fuellfarbe
     */
    public void setzeFarbe(String farbe) {
        this.setzeFarbe(zuFarbeKonvertieren(farbe));
    }
    
    /**
     * Setzt die Fuellfarbe<br />
     * Parallele Methode zu <code>setzeFarbe()</code>
     * @param   farbe   Der Name der neuen Fuellfarbe
     * @see setzeFarbe(String)
     * @see farbeSetzen( Farbe )
     */
    public void farbeSetzen(String farbe) {
        setzeFarbe(farbe);
    }
    
    /**
     * Setzt die Fuellfarbe
     * @param   f   Das Farbe-Objekt, das die neue Fuellfarbe beschreibt
     * @see farbeSetzen(String)
     */
    public void farbeSetzen(Farbe f) {
        setzeFarbe(f.wert());
    }
    
    /**
     * Setzt die Schriftgroesse
     * @param   groesse Die neue Schriftgroesse
     */
    public void setzeGroesse(int groesse) {
        this.groesse = groesse;
        aktualisieren();
    }
    
    /**
     * Setzt die Schriftgroesse.<br />
     * Wrappt hierbei die Methode <code>setzeGroesse</code>.
     * @param groesse   Die neue Schriftgroesse
     * @see #setzeGroesse(int)
     */
    public void groesseSetzen(int groesse) {
        setzeGroesse(groesse);
    }

    /**
     * Diese Methode gibt die aktuelle Groesse des Textes aus
     * @return  Die aktuelle Schriftgroesse des Textes
     * @see #groesseSetzen(int)
     */
    public int groesse() {
        return groesse;
    }

    /**
     * Setzt einen neuen Font fuer den Text
     * @param   fontName    Der Name des neuen Fonts fuer den Text
     */
    public void setzeFont(String fontName) {
        Font base = null;
        for(int i = 0; i < eigene.length; i++) {
            if(eigene[i].getName().equals(fontName)) {
                base = eigene[i];
                break;
            }
        }
        if(base != null) {
            this.font = base.deriveFont(schriftart, groesse);
        } else {
            if(!Manager.fontExistiert(fontName)) {
                fontName = "SansSerif";
                System.err.println("Achtung! Die gewuenschte Schriftart existiert nicht im Font-Verzeichnis dieses PC! " +
                        "Wurde der Name falsch geschrieben? Oder existiert der Font nicht?");
            }
            this.font = new Font(fontName, schriftart, groesse);
        }
    }

    public static Font holeFont(String fontName) {
        Font base = null;
        for(int i = 0; i < eigene.length; i++) {
            if(eigene[i].getName().equals(fontName)) {
                base = eigene[i];
                break;
            }
        }
        if(base != null) {
            return base;
        } else {
            if(!Manager.fontExistiert(fontName)) {
                fontName = "SansSerif";
                System.err.println("Achtung! Die gewuenschte Schriftart existiert weder als geladene Sonderdatei noch im Font-Verzeichnis dieses PC! " +
                        "Wurde der Name falsch geschrieben? Oder existiert der Font nicht?");
            }
            return new Font(fontName, 0, 12);
        }
    }

    /**
     * Setzt einen neuen Font fuer den Text.<br />
     * Parallele Methode zu <code>setzeFont()</code>
     * @param   name    Der Name des neuen Fonts fuer den Text
     * @see setzeFont(String)
     */
    public void fontSetzen(String name) {
        setzeFont(name);
    }
    
    /**
     * Klasseninterne Methode zum aktualisieren des Font-Objektes
     */
    private void aktualisieren() {
        this.font = this.font.deriveFont(schriftart, groesse);
    }
    
    /**
     * Verschiebt das Objekt.
     * @param   v   Der Vektor, der die Verschiebung des Objekts angibt.
     */
    public void verschieben(Vektor v) {
        this.x += v.x;
        this.y += v.y;
    }
    
    /**
     * Test, ob ein anderes Raum-Objekt von diesem geschnitten wird.
     * @param   r   Das Objekt, das auf Kollision mit diesem getestet werden soll.
     * @return  TRUE, wenn sich beide Objekte schneiden.
     */
    public boolean schneidet(Raum r) {
        return r.dimension().schneidetBasic(this.dimension());
    }
    
    /**
     * Zeichnet das Objekt.
     * @param   g   Das zeichnende Graphics-Objekt
     * @param   r    Das BoundingRechteck, dass die Kameraperspektive Repraesentiert.<br />
     *                         Hierbei soll zunaechst getestet werden, ob das Objekt innerhalb der Kamera liegt, und erst dann gezeichnet werden.
     */
    public void zeichnen(java.awt.Graphics g, BoundingRechteck r) {
        if(!r.schneidetBasic(this.dimension())) {
            return;
        }
        g.setColor(farbe);
        g.setFont(font);
        g.drawString(inhalt, x-r.x, y-r.y+groesse);
    }
    
    /**
     * @return  Ein BoundingRechteck mit dem minimal noetigen Umfang, um das Objekt <b>voll einzuschliessen</b>.
     */
    public BoundingRechteck dimension() {
        FontMetrics f = Fenster.metrik(font);
        return new BoundingRechteck(x, y, f.stringWidth(inhalt), f.getHeight());
    }
    
    /**
     * Setzt, ob dieses Leuchtend-Objekt leuchten soll.<br />
     * Ist dies der Fall, so werden immer wieder schnell dessen Farben geaendert; so entsteht ein Leuchteffekt.
     * @param   leuchtet    Ob dieses Objekt nun leuchten soll oder nicht (mehr).<br />
     * <b>Achtung:</b> Die Leuchtfunktion kann bei bestimmten Klassen sehr psychadelisch und aufreizend wirken! Daher 
     * sollte sie immer mit Bedacht und in Nuancen verwendet werden!
     */
    @Override
    public void leuchtetSetzen(boolean leuchtet) {
        if(this.leuchtet == leuchtet) {
            return;
        }
        this.leuchtet = leuchtet;
        if(leuchtet) {
            alte = farbe;
        } else {
            this.setzeFarbe(alte);
        }
    }
    
    /**
     * Fuehrt einen Leuchtschritt aus.<br />
     * Dies heisst, dass in dieser Methode die Farbe einfach gewechselt wird. Da diese Methode schnell und oft hintereinander 
     * ausgefuehrt wird, soll so der Leuchteffekt entstehen.<br />
     * <b>Diese Methode sollte nur innerhalb der Engine ausgefuehrt werden! Also nicht fuer den Entwickler gedacht.</b>
     */
    @Override
    public void leuchtSchritt() {
        this.setzeFarbe(farbzyklus[leuchtzaehler = ((++leuchtzaehler)%farbzyklus.length)]);
    }
    
    /**
     * Gibt wieder, ob das Leuchtet-Objekt gerade leuchtet oder nicht.
     * @return  <code>true</code>, wenn das Objekt gerade leuchtet, wenn nicht, dann ist die Rueckgabe <code>false</code>
     */
    @Override
    public boolean leuchtet() {
        return this.leuchtet;
    }
    
    /**
     * Diese Methode loescht alle eventuell vorhandenen Referenzen innerhalb der Engine auf dieses Objekt, damit es problemlos geloescht werden kann.<br />
     * <b>Achtung:</b> zwar werden hierdurch alle Referenzen geloescht, die <b>nur innerhalb</b> der Engine liegen (dies betrifft vor allem Animationen etc), jedoch nicht die 
     * innerhalb eines <code>Knoten</code>-Objektes!!!!!!!!!<br />
     * Das heisst, wenn das Objekt an einem Knoten liegt (was <b>immer der Fall ist, wenn es auch gezeichnet wird (siehe die Wurzel des Fensters)</b>), muss es trotzdem 
     * selbst geloescht werden, <b>dies erledigt diese Methode nicht!!</b>.<br />
     * Diese Klasse ueberschreibt die Methode wegen des Leuchtens.
     */
    @Override
    public void loeschen() {
        super.leuchterAbmelden(this);
        super.loeschen();
    }

    /**
     * Sehr wichtige Methode!<br />
     * Diese Methode liefert als Protokoll an die Konsole alle Namen, mit denen die aus dem Projektordner geladenen ".ttf"-Fontdateien
     * gewaehlt werden koennen.<br />
     * Diese Namen werden als <code>String</code>-Argument erwartet, wenn die eigens eingebauten Fontarten verwendet werden sollen.<br />
     * Der Aufruf dieser Methode wird <b>UMGEHEND</b> empfohlen, nach dem alle zu verwendenden Arten im Projektordner liegen, denn nur unter dem
     * an die Konsole projezierten Namen <b>koennen diese ueberhaupt verwendet werden</b>!!<br />
     * Daher dient diese Methode der Praevention von Verwirrung, wegen "nicht darstellbarer" Fonts.
     */
    public static void geladeneSchriftartenAusgeben() {
        System.out.println("Protokoll aller aus dem Projektordner geladener Fontdateien");
        System.out.println();
        if(eigene.length == 0) {
            System.out.println("Es wurden keine \".ttf\"-Dateien im Projektordner gefunden");
        } else {
            System.out.println("Es wurden " + eigene.length + " \".ttf\"-Dateien im Projektordner gefunden.");
            System.out.println("Diese sind unter folgenden Namen abrufbar:");
            for(int i = 0; i < eigene.length; i++) {
                System.out.println(eigene[i].getName());
            }
        }
        System.out.println("|Ende| des Protokolls");
    }
}
