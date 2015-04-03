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

package ea;

import ea.internal.gra.Zeichenebene;
import ea.internal.util.Logger;

import java.awt.*;

/**
 * Die Kamera "blickt" auf die Zeichenebene, das was sie sieht beschreibt den Teil der Zeichenebene;
 * das, was im Fenster dargestellt wird.<br /> Sie kann ein Objekt fokussieren und ihm so folgen.
 * Hierbei besteht auch die Moeglichkeit, diesen Fokus in Grenzen zu halten. Und zwar durch die
 * Fokus-Bounds. Diese 4 Grenzwerte koennen individuell verstellt und aktiviert werden. auch kann
 * man den von der Kamera darzustellenden Bereich durch eine einzige Methode definieren, in dem man
 * den Bereich als BoundingRechteck beschreibt.<br /> <br /> <br /> <br /> <code> BoundingRechteck
 * grenzen = new BoundingRechteck(0, 0, 1500, 1000);<br /> meineCam.boundsSetzen(grenzen);<br />
 * </code> <br /> <br /> <br /> Hierdurch wird automatisch der gesamte Fokusapparat (auf den Bereich
 * zwischen den Punkten (0|0) und (1500|1000) ) eingestellt. Bei spezielleren Fokuswuenschen laesst
 * sich dies ebenfalls arrangieren durch die einzelnen Methoden, mit denen alle vier Bounds (N, S,
 * O, W) einzeln verstellt und (de)aktiviert werden koennen.<br /> <b>!!Achtung!!</b><br /> Bei den
 * Fokuseinstellungen sollte immer ein Bereich gewaehlt werden, der die Groesse des Anzeigefensters
 * (oder Vollbildes) bei weitem uebersteigt.<br /> Allgemein wirken diese Bounds auch ohne
 * aktivierten Fokus. jedoch ist dies meist weniger sinnvoll.
 *
 * @author Michael Andonie
 */
public class Kamera {
    /**
     * Die Zeichenebene, auf die sie "blickt"
     */
    private Zeichenebene ebene;

    /**
     * Die aktuelle Bemessung der Kameraperspektive
     */
    private BoundingRechteck bild;

    /**
     * Die Bounds der Kamera (sofern vorhanden)
     */
    private BoundingRechteck bounds;

    /**
     * Der eventuelle Fokuspunkt der Kamera.
     */
    private Raum fokus = null;

    /**
     * Der Fokus-Verzug
     */
    private Vektor verzug = Vektor.NULLVEKTOR;

    /**
     * Gibt an, ob es Bounds gibt.
     */
    private boolean hatBounds = false;

    /**
     * Konstruktor fuer Objekte der Klasse Kamera
     *
     * @param sizeX Die X-Laenge des Fensters
     * @param sizeY Die Y-Laenge des Fensters
     */
    public Kamera(int sizeX, int sizeY, Zeichenebene z) {
        ebene = z;
        bild = new BoundingRechteck(0, 0, sizeX, sizeY);
    }

    /**
     * Setze einen Fokus der Kamera auf ein Objekt.<br /> Dieses Objekt ist ab dann im 'Zentrum' der
     * Kamera.<br /> Die Art des Fokus (vorne, hinten, oben, unten, mittig etc...) kann ueber die
     * Methode <b>setzeFokusArt()</b> geaendert werden.<br /> Soll das Fokusverhalten beendet
     * werden, muss die paramterlose Methode <b>loescheFokus()</b> ausgefuehrt werden; dann bleibt
     * die Kamera bis auf weiteres in der letzten Position.<br /> Diese Methode wrappt lediglich
     * <code>setzeFokus(Raum)</code>, wurde daher zur verhinderung des Orientierungsverlustes
     * geschrieben.
     *
     * @param r Der Fokuspunkt.
     * @see #setzeFokus(Raum)
     */
    public void fokusSetzen(Raum r) {
        this.setzeFokus(r);
    }

    /**
     * Setze einen Fokus der Kamera auf ein Objekt.<br /> Dieses Objekt ist ab dann im 'Zentrum' der
     * Kamera.<br /> Die Art des Fokus (vorne, hinten, oben, unten, mittig etc...) kann ueber die
     * Methode <b>setzeFokusArt()</b> geaendert werden.<br /> Soll das Fokusverhalten beendet
     * werden, muss die paramterlose Methode <b>loescheFokus()</b> ausgefuehrt werden; dann bleibt
     * die Kamera bis auf weiteres in der letzten Position.
     *
     * @param r Der Fokuspunkt.
     */
    public void setzeFokus(Raum r) {
        fokus = r;
    }

    /**
     * Setzt einen Fokus-Verzug. Der Standartwert hierfuer ist (0|0).<br /> Der Fokusverzug ist ein
     * Vektor, um den das Bild, das <b>den Fokus exakt im Zentrum hat</b>, verschoben wird.<br />
     * Das heisst, dass eine FIgur im Fokus um 100 Pixel tiefer als im Absoluten Bildzentrum liegt,
     * wenn der Fokusverzug mit folgender Methode gesetzt wurde:<br /> <br /> <code> /*Irgendwo in
     * der spielsteuernden Klasse* /<br /> cam.fokusVerzugSetzen(new Vektor(0, -100));<br /> <br
     * /></code>
     *
     * @param v Der Vektor, um den ab sofort die Kamera vom Zentrum des Fokus verschoben wird.
     */
    public void fokusVerzugSetzen(Vektor v) {
        verzug = v;
    }

    /**
     * Mit dieser Methode kann man direkt saemtliche Kamera-Bounds-Einstellungen machen.<br /> Ein
     * Bounding-Rechteck gibt die Begrenzung an, die die Kameraperspektive, <b>solange die Bounds
     * nicht deaktiviert werden</b> (ueber die Methode <code>hatBoundsSetzen</code> moeglich),
     * niemals uebertreten wird.
     *
     * @param r Das BoundingRechteck, das die Begrenzung des Raumes angibt
     * @see #hatBoundsSetzen(boolean)
     */
    public void boundsSetzen(BoundingRechteck r) {
        if (r == null) {
            Logger.error("Der Eingabewert fuer den Fokusbereich war null!");
            return;
        }
        hatBoundsSetzen(true);
        bounds = r;
    }

    /**
     * Setzt, ob das Fokusverhalten durch Bounds begrenzt wird.
     *
     * @param b Ob die gesetzten Minimum- und Maximum-Werte auch aktiviert werden sollen.
     * @see #boundsSetzen(BoundingRechteck)
     */
    public void hatBoundsSetzen(boolean b) {
        hatBounds = b;
    }

    /**
     * Loescht den Fokus.<br /> Die Kamera bleibt in ihrer letzten Position zurueck bis entweder ein
     * neuer Fokus gesetzt wird oder sie einfach nur verschoben wird.<br /> Diese Methode macht das
     * selbe wie <code>loescheFokus</code>, und ist wegen einer einheitlichen Schreibweise
     * eingeführt.
     *
     * @see #loescheFokus()
     */
    public void fokusLoeschen() {
        loescheFokus();
    }

    /**
     * Loescht den Fokus.<br /> Die Kamera bleibt in ihrer letzten Position zurueck.
     */
    public void loescheFokus() {
        fokus = null;
    }

    /**
     * Verschiebt die Kamera um einen bestimmten Wert in X- und Y-Richtung.<br /> Alternative
     * Methode fuer diejenigen, denen ein Vektor zu umstaendlich ist.
     *
     * @param x Die Verschiebung in X-Richtung
     * @param y Die Verschiebung in Y-Richtung
     * @see #verschieben(Vektor)
     */
    public void verschieben(int x, int y) {
        this.verschieben(new Vektor(x, y));
    }

    /**
     * Verschiebt die Kamera um einen bestimmten Wert in X- und Y-Richtung.
     *
     * @param v Der die Bewegung beschreibende Vektor.
     * @see #verschieben(int, int)
     */
    public void verschieben(Vektor v) {
        bild = bild.verschobeneInstanz(v);
    }

    /**
     * Setzt das Zentrum der Kamera. Von nun an ist der Punkt mit den eingegebenen Koordinaten im
     * Zentrum des Bildes.
     *
     * @param x Die X-Koordinate des Zentrums des Bildes
     * @param y Die Y-Koordinate des Zentrums des Bildes
     * @see #zentrumSetzen(Punkt)
     */
    public void zentrumSetzen(int x, int y) {
        this.zentrumSetzen(new Punkt(x, y));
    }

    /**
     * Setzt das Zentrum der Kamera. Von nun an ist der Eingegebene Punkt im Zentrum des Bildes.
     *
     * @param zentrum Das neue Zentrum der Kamera
     * @see #zentrumSetzen(int, int)
     */
    public void zentrumSetzen(Punkt zentrum) {
        bild = bild.mittenAngleichInstanz(zentrum);
    }

    /**
     * Setzt die Position der <i>linken oberen Ecke</i> der Kameraperspektive.
     *
     * @param p Der Punkt der linken oberen Ecke der Kameraperspektive
     * @see #positionSetzen(float, float)
     */
    public void positionSetzen(Punkt p) {
        positionSetzen(p.x, p.y);
    }

    /**
     * Setzt die Position der <i>linken oberen Ecke</i> der Kameraperspektive.
     *
     * @param x Die <i>X-Koordinate der linken oberen Ecke</i> der Kameraperspektive
     * @param y Die <i>Y-Koordinate der linken oberen Ecke</i> der Kameraperspektive
     * @see #positionSetzen(Punkt)
     */
    public void positionSetzen(float x, float y) {
        bild = bild.anPosition(x, y);
    }

    /**
     * @return Der Knoten, an dem jedes Raum-Objekt liegen muss, um gezeichnet zu werden.
     */
    public Knoten wurzel() {
        return ebene.basis();
    }

    /**
     * Die aktuelle Position der Kamera wird zurueckgegeben.
     *
     * @return Das aktuelle BoundingRechteck, dass die aktuelle Fensterdarstellung beschreibt.
     */
    public BoundingRechteck position() {
        return bild;
    }

    /**
     * @return Der Verzug in Richtung X, den die Kamera bis jetzt vom Urspruenglichen Standort (0,
     * 0) hat.
     */
    public int getX() {
        return (int) bild.x;
    }

    /**
     * @return Der Verzug in Richtung Y, den die Kamera bis jetzt vom Urspruenglichen Standort (0,
     * 0) hat.
     */
    public int getY() {
        return (int) bild.y;
    }

    /**
     * Zeichnet alle Objekte neu, die sich auf der Zeichenebene und im Blickfeld der Kamera
     * befinden.
     */
    public void zeichne(Graphics2D g) {
        if (hatFokus()) {
            // Nachjustieren
            bild = bild.mittenAngleichInstanz(fokus.dimension());
            bild = bild.verschobeneInstanz(verzug);
        }

        if (hatBounds) {
            bild = bild.in(bounds);
        }

        ebene.basis().zeichnen(g, bild);

        if (EngineAlpha.isDebug()) {

            //Debug Grid

            int tx = (int) bild.x;
            int ty = (int) bild.y;
            int gridSize = 50;

            g.translate(-tx, -ty);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            g.setColor(new Color(255, 255, 255, 100));

            for (int x = tx / gridSize * gridSize; x < tx + bild.breite; x += gridSize) {
                g.drawLine(x, ty, x, ty + (int) bild.hoehe);
                g.drawString("" + x, x + 10, ty + 20);
            }

            for (int y = ty / gridSize * gridSize; y < ty + bild.hoehe; y += gridSize) {
                g.drawLine(tx, y, tx + (int) bild.breite, y);
                g.drawString("" + y, tx + 10, y + 20);
            }

            g.translate(tx, ty);


            //Show Colliders

            Raum debugBoxes = ebene.basis().aktuellerCollider().visualize(ebene.basis().position(),
                    new Farbe(255,255,255,20));
            debugBoxes.zeichnen(g, bild);
        }
    }

    /**
     * @return Ob die Kamera steif ist, oder sich mit einem Fokuspunkt mitbewegt.
     */
    public boolean hatFokus() {
        return (fokus != null);
    }
}
