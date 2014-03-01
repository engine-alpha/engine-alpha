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

package ea.internal.gra;

import java.awt.*;

import ea.BoundingRechteck;

/**
 * Ein PixelFeld ist eine Ansammlzung vieler Pixel, es kann gezeichnet werden.<br />
 * Es besteht aus mehreren Quadraten gleicher Groesse, die aneinandergereiht das Rechteck mit deren Groesse darstellen.
 * <br /><b>Achtung!</b> Pixelfelder leiten sich nicht aus der notwendigen Ueberklasse <code>Raum</code> ab, um direkt
 * grafisch dargestellt werden zu koennen ein einzelnes Pixelfeld kann in einer unanimierten Figur dargestellt werden!
 * 
 * @author Michael Andonie
 */
public class PixelFeld
implements java.io.Serializable
{
    /**Die Serialisierungs-Konstante dieser Klasse. In keiner Weise fuer die Programmierung mit der Engine bedeutsam.*/
    public static final long serialVersionUID = 78L;

    /**
     * Die Farbinformation der einzelnen Pixel.<br />
     * Ist einer dieser Werte <code>null</code>, so wird an dieser Position nicht gezeichnet.
     */
    private Color[][] farbe;

    /**
     * Alternative Farbe fuer das einfarbige Zeichnen
     */
    private Color alternativ = null;

    /**
     * Der Genauigkeitsfaktor.<br />
     * Die Groesse eines Unterquadrats ist das Multiplikationsergebnis des Faktors mit der Pixelgroesse.<br />
     * Daher <b>muss</b> Der Faktor groesser als 0 sein!
     */
    private int faktor;

    /**
     * Gibt an, ob die Laenge sich seit dem letzten Wert geaendert haben KOENNTE.
     */
    private boolean changed = true;

    /**
     * Die Pixelanzahl des letzten Kollisionstests.
     */
    private int pixel=0;

    /**
     * Konstruktor fuer Objekte der Klasse PixelFeld
     * @param   grX Die Breite der Figur in Quadraten
     * @param   grY Die Hoehe der Figur in Quadraten
     * @param   faktor  Der Genauigkeitsfaktor der Figur. <b>MUSS</b> groesser als 0 zu sein !
     */
    public PixelFeld(int grX, int grY, int faktor)
    {
        farbe = new Color[grX][grY];
        if(faktor < 1) {
            System.err.println("Der Eingabefaktor war kleiner oder gleich 0!");
        } else {
            this.faktor = faktor;
        }
    }
    
    /**
     * Setzt den Groessenfaktor des Feldes.
     * @param   faktor  Der neue Groessenfaktor
     */
    public void faktorSetzen(int faktor) {
        if(faktor <= 0) {
            System.err.println("Der Groessenfaktor muss groesser als 0 sein!");
        }
        this.faktor = faktor;
    }
    
    /**
     * Setzt an einer bestimmten Position eine Farbe.
     * @param   x   Die Relative X-Position des zu aendernden Quadrats
     * @param   y   Die Relative Y-Position des zu aendernden Quadrats
     * @param   c   Die neu zu setzende Farbe. Ist dieser Wert null, so wird dieses Unterquadrat nicht mitgezeichnet.
     */
    public void farbeSetzen(int x, int y, Color c) {
        farbe[x][y] = c;
    }
    
    /**
     * @return die Breite des Feldes in der Zeichenebene.
     */
    public int breite() {
        return farbe.length * faktor;
    }
    
    /**
     * @return die Hoehe des Feldes in der Zeichenebene.
     */
    public int hoehe() {
        return farbe[0].length * faktor;
    }
    
    /**
     * @return die Anzahl an Unterquadraten in Richtung X
     */
    public int breiteN() {
        return farbe.length;
    }
    
    /**
     * @return die Anzahl an Unterquadraten in Richtung Y
     */
    public int hoeheN() {
        return farbe[0].length;
    }
    
    /**
     * @return  Der Groessenfaktor, der dieses Bild zeichnet.
     */
    public int faktor() {
        return faktor;
    }
    
    /**
     * Gleicht dieses PixelFeld an ein anderes an, sodass beide genau dieselben Inhalte haben.<br />
     * <b>Achtung!!</b><br />
     * Hierfuer muessen beide PixelFelder dieselben Masse in Laenge und Breite haben (hierbei zaehlt nicht der Groessenfaktor,
     * sondern die Anzahl an Unterquadraten in Richtung X und Y.
     */
    public void angleichen(PixelFeld f) {
        if(f.hoeheN() == this.hoeheN() && f.breiteN() == this.breiteN()) {
            for(int i = 0; i < farbe.length; i++) {
                for(int j = 0; j < farbe[0].length; j++) {
                    this.farbe[i][j] = f.farbe[i][j];
                }
            }
        } else {
            System.err.println("Achtung!  Die beiden zum Angleich angefuehrten PixelFelder haben unterschiedliche Masse in Hoehe und/oder Breite!!");
        }
    }
    
    /**
     * Aendert alle Farben des Feldes in ihr Negativ um.
     */
    public void negativ() {
        for(int i = 0; i < farbe.length; i++) {
            for(int j = 0; j < farbe[0].length; j++) {
                if(this.farbe[i][j] != null) {
                    this.farbe[i][j] = new Color(255-farbe[i][j].getRed(), 255-farbe[i][j].getGreen(), 255-farbe[i][j].getBlue());
                }
            }
        }
    }

    /**
     * Hellt alle Farbwerte auf.
     */
    public void heller() {
        for(int i = 0; i < farbe.length; i++) {
            for(int j = 0; j < farbe[0].length; j++) {
                if(this.farbe[i][j] != null) {
                    this.farbe[i][j] = this.farbe[i][j].brighter();
                }
            }
        }
    }

    /**
     * Dunkelt alle Farbwerte ab.
     */
    public void dunkler() {
        for(int i = 0; i < farbe.length; i++) {
            for(int j = 0; j < farbe[0].length; j++) {
                if(this.farbe[i][j] != null) {
                    this.farbe[i][j] = this.farbe[i][j].darker();
                }
            }
        }
    }

    /**
     * Transformiert alle Farbwerte um einen entsprechenden Betrag.<br />
     * Bei Uebertreten des Definitionsbereiches bleibtwird bei den Grenzen (0 bzw. 255) gehalten.
     * @param r Der Rot-Aenderungswert
     * @param g Der Gruen-Aenderungswert
     * @param b Der Blau-Aenderungswert
     */
    public void transformieren(int r, int g, int b) {
        for(int i = 0; i < farbe.length; i++) {
            for(int j = 0; j < farbe[0].length; j++) {
                if(this.farbe[i][j] != null) {
                    Color c = farbe[i][j];
                    farbe[i][j] = new Color(zahlenSumme(c.getRed(), r), zahlenSumme(c.getGreen(), g), zahlenSumme(c.getBlue(), b));
                }
            }
        }
    }

    /**
     * Errechnet aus zwei Zahlen die Summe und setzt das Ergebnis, sofern nicht im Definitionsbereich der Farbwerte
     * auf den naeheren Grenzwert (0 bzw. 255)
     * @param a Wert 1
     * @param b Wert 2
     * @return  Die Summe, unter Garantie im Definitionsbereich
     */
    private final int zahlenSumme(int a, int b) {
        int ret = a+b;
        if(ret < 0) {
            ret = 0;
        } else if(ret > 255) {
            ret = 255;
        }
        return ret;
    }

    /**
     * Sorgt fuer die einfarbige Darstellung des Feldes
     * @param c Diese Farbe ist nun fuer alle farbeigen Quadrate die Farbe
     */
    public void einfaerben(Color c) {
        alternativ = c;
    }

    /**
     * Sorgt fuer die normale Darstellung des Feldes
     */
    public void zurueckFaerben() {
        alternativ = null;
    }

    /**
     * Zeichnet das Feld mit einem bestimmten Verzug.
     * @param   g   Das zeichnende Graphics-Objekt
     * @param   x   Der Verzug in Richtung X
     * @param   y   Der Verzug in Richtung Y
     * @param   spiegelX Ob dieses Pixelfeld entlang der X-Achse gespiegelt werden soll
     * @param   spiegelY Ob dieses Pixelfeld entlang der Y-Achse gespiegelt werden soll
     */
    public void zeichnen(Graphics g, int x, int y, boolean spiegelX, boolean spiegelY) {
        for(int i = 0; i < farbe.length; i++) {
            for(int j = 0; j < farbe[0].length; j++) {
                int cX, cY;
                if(spiegelY) {
                    cX = farbe.length-i-1;
                } else {
                    cX = i;
                }
                if(spiegelX) {
                    cY = farbe[0].length-j-1;
                } else {
                    cY = j;
                }
                Color c = farbe[cX][cY];
                if(c == null) {
                    continue;
                }
                if(alternativ == null) {
                    g.setColor(c);
                } else {
                    g.setColor(alternativ);
                }
                g.fillRect(x+(i*faktor), y+(j*faktor), faktor, faktor);
            }
        }
    }
    
    /**
     * In dieser Methode werden die einzelnen Quadrate von ihrer Informationsdichte her zurueckgegeben.
     * @return  Die Farbinformationen ueber dieses Pixelfeld.
     */
    public Color[][] getPic() {
        return farbe;
    }
    
    /**
     * Berechnet die Anzahl an Pixeln, die auf diesem PixelFeld liegen.
     * @return  Die Anzahl an tatsaechlichen Pixeln.
     */
    public int anzahlPixel() {
        if(changed) {
            int neu = 0;
            for(int i = 0; i < farbe.length; i++) {
                for(int j = 0; j < farbe[0].length; j++) {
                    if(farbe[i][j] != null) {
                        neu++;
                    }
                }
            }
            pixel = neu;
            changed = false;
        }
        return pixel;
    }

    /**
     * Erstellt ein neues PixelFeld mit exakt denselben Eigenschaften wie dieses.<br />
     * Diese Methode wird vor allem intern im FigurenEditor verwendet.
     * @return  Ein neues PixelFeld-Objekt mit genau demselben Zustand wie dieses.
     */
    public PixelFeld erstelleKlon() {
        PixelFeld ret = new PixelFeld(farbe.length, farbe[0].length, faktor);
        for(int i = 0; i < farbe.length; i++) {
            for(int j = 0; j < farbe[0].length; j++) {
                ret.farbeSetzen(i, j, farbe[i][j]);
            }
        }
        return ret;
    }

    /**
     * Berechnet <b>EXAKT</b> die Flaechen aus denen dieses Pixel-Feld besteht.
     * @param x Die X-Startkoordinate der linken oberen Ecke
     * @param y Die Y-Startkoordinate der linken oberen Ecke
     * @return  Alle Flaechen dieses Pixel-Feldes als Array aus Bounding-Rechtecken.
     */
    public BoundingRechteck[] flaechen(float x, float y) {
        BoundingRechteck[] ret = new BoundingRechteck[anzahlPixel()];
        int cnt = 0;
        for(int i = 0; i < farbe.length; i++) {
                for(int j = 0; j < farbe[0].length; j++) {
                    if(farbe[i][j] != null) {
                        ret[cnt++] = new BoundingRechteck(x + i*faktor, y + j*faktor, faktor, faktor);
                    }
                }
        }

        return ret;
    }
}
