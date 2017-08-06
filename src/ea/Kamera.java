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

import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import ea.internal.gra.Zeichenebene;
import ea.internal.gui.Fenster;
import ea.internal.util.Logger;
import ea.raum.Knoten;
import ea.raum.Raum;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RopeJoint;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

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
    private final Zeichenebene ebene;

    /**
     * Die aktuelle Bemessung der Kameraperspektive
     */
    private BoundingRechteck kameraAuschschnitt;

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
     * Referenz auf das Fenster, in dem diese Kamera den sichtbaren Bereich bestimmt.
     */
    private final Fenster fenster;

    /**
     * Der aktuelle Kamerazoom.
     * TODO Zoom erklärung (was macht größer/kleiner?)
     */
    private float zoom =1f;

    /**
     * Konstruktor fuer Objekte der Klasse Kamera
     *  @param sizeX Die X-Laenge des Fensters
     * @param sizeY Die Y-Laenge des Fensters
     * @param fenster
     */
    public Kamera(int sizeX, int sizeY, Zeichenebene z, Fenster fenster) {
        ebene = z;
        kameraAuschschnitt = new BoundingRechteck(0, 0, sizeX, sizeY);
        this.fenster = fenster;
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
     * der spielsteuernden Klasse* /<br /> kamera.fokusVerzugSetzen(new Vektor(0, -100));<br /> <br
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
            Logger.error("Raum", "Der Eingabewert fuer den Fokusbereich war null!");
            return;
        }
        hatBoundsSetzen(true);
        bounds = r;
    }

    /**
     * Setzt den Zoom der Kamera. Der Zoom bestimmt wie "nah" die Kamera auf die Zeichenebene guckt. Die Größe eines
     * Objektes im Fenster entspricht der Größe auf der Zeichenebene multipliziert mit dem Zoom-Faktor (Default-Wert
     * des Zoom-Faktors ist <code>1</code>).
     *
     * @param zoom Der neue Zoom-Wert der Kamera. <ul>
     *             <li><code>1</code> ist der Standard-Wert. Der Ausgangszoom.</li>
     *             <li>Werte größer als 1 "zoomen rein". <code>2</code> macht alles <b>doppelt so groß</b>.</li>
     *             <li>Werte zwischen 1 und 0 (jeweils exklusiv) "zoomen raus". <code>0,5</code> macht alles
     *             <b>halb so groß</b>.</li>
     * </ul>
     */
    @API
    public void zoomSetzen(float zoom) {
        if(zoom <= 0) {
            Logger.error("Kamera", "Der Kamerazoom kann nicht kleiner oder gleich 0 sein.");
            return;
        }
        this.zoom = zoom;
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
    public void verschieben(float x, float y) {
        this.verschieben(new Vektor(x, y));
    }

    /**
     * Verschiebt die Kamera um einen bestimmten Wert in X- und Y-Richtung.
     *
     * @param v Der die Bewegung beschreibende Vektor.
     * @see #verschieben(float, float)
     */
    public void verschieben(Vektor v) {
        kameraAuschschnitt = kameraAuschschnitt.verschobeneInstanz(v);
    }

    /**
     * Setzt das Zentrum der Kamera. Von nun an ist der Punkt mit den eingegebenen Koordinaten im
     * Zentrum des Bildes.
     *
     * @param x Die X-Koordinate des Zentrums des Bildes
     * @param y Die Y-Koordinate des Zentrums des Bildes
     * @see #zentrumSetzen(Punkt)
     */
    @API
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
        kameraAuschschnitt = kameraAuschschnitt.mittenAngleichInstanz(zentrum);
    }

    /**
     * Setzt die Position der <i>linken oberen Ecke</i> der Kameraperspektive.
     *
     * @param p Der Punkt der linken oberen Ecke der Kameraperspektive
     * @see #positionSetzen(float, float)
     */
    @API
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
    @API
    public void positionSetzen(float x, float y) {
        kameraAuschschnitt = kameraAuschschnitt.anPosition(x, y);
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
    @API
    public BoundingRechteck position() {
        return kameraAuschschnitt;
    }

    /**
     * @return Der Verzug in Richtung X, den die Kamera bis jetzt vom Urspruenglichen Standort (0,
     * 0) hat.
     */
    @API
    public int getX() {
        return (int) kameraAuschschnitt.x;
    }

    /**
     * @return Der Verzug in Richtung Y, den die Kamera bis jetzt vom Urspruenglichen Standort (0,
     * 0) hat.
     */
    @API
    public int getY() {
        return (int) kameraAuschschnitt.y;
    }

    /**
     * Gibt den aktuellen Zoom aus.
     * @return  Der aktuelle Zoom der Kamera.
     */
    @API
    public float getZoom() {
        return zoom;
    }

    /**
     * Zeichnet alle Objekte neu, die sich auf der Zeichenebene und im Blickfeld der Kamera
     * befinden.
     */
    @NoExternalUse
    public void zeichne(Graphics2D g) {
        if (hatFokus()) {
            // Nachjustieren
            kameraAuschschnitt = kameraAuschschnitt.mittenAngleichInstanz(fokus.position.mittelPunkt());
            kameraAuschschnitt = kameraAuschschnitt.verschobeneInstanz(verzug);
        }

        if (hatBounds) {
            kameraAuschschnitt = kameraAuschschnitt.in(bounds);
        }

        AffineTransform transform = g.getTransform();

        //Setze Clip mit etwas Extra-Rand (Rundungsfehler von Float zu Int)
        g.setClip(0,0, (int) (kameraAuschschnitt.breite)+4, (int) (kameraAuschschnitt.hoehe)+4);



        g.scale(zoom, zoom);
        g.translate(-kameraAuschschnitt.x, -kameraAuschschnitt.y);
        ebene.basis().renderBasic(g, position());


        if (EngineAlpha.isDebug()) {

            //Debug Grid

            int tx = (int) kameraAuschschnitt.x;
            int ty = (int) kameraAuschschnitt.y;
            int gridSize = 50;

            g.translate(-tx, -ty);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            g.setColor(new Color(255, 255, 255, 100));

            for (int x = tx / gridSize * gridSize; x < tx + kameraAuschschnitt.breite; x += gridSize) {
                g.drawLine(x, ty, x, ty + (int) kameraAuschschnitt.hoehe);
                g.drawString("" + x, x + 10, ty + 20);
            }

            for (int y = ty / gridSize * gridSize; y < ty + kameraAuschschnitt.hoehe; y += gridSize) {
                g.drawLine(tx, y, tx + (int) kameraAuschschnitt.breite, y);
                g.drawString("" + y, tx + 10, y + 20);
            }



            //Display Joints
            Joint j = ebene.basis() .getPhysikHandler() .worldHandler() .getWorld() .getJointList();

            while(j != null) {
                renderJoint(j, g);
                j = j.m_next;
            }


            g.translate(tx, ty);

            //Display FPS
            g.setColor(new Color(200,200,200, 150));

            String fpsMessage = " FPS: " + (1000 /fenster.getFrameThread().getLastFrameTime());
            Font displayFont = new Font("Monospaced", Font.PLAIN, 12);
            FontMetrics fm = g.getFontMetrics(displayFont);

            Rectangle2D r2d = fm.getStringBounds(fpsMessage, g);

            g.fillRect(10, 20, (int)r2d.getWidth(), (int)r2d.getHeight());

            g.setColor(Color.black);
            g.drawString(fpsMessage, 10, 30);
        }

        g.setTransform(transform);
    }

    private void renderJoint(Joint j, Graphics2D g) {
        final int CIRC_RAD=10; //(Basis-)Radius für die Visualisierung von Kreisen
        final int RECT_SID=12; //(Basis-)Breite für die VIsualisierung von Rechtecken

        Vec2 anchorA = new Vec2(), anchorB = new Vec2();
        j.getAnchorA(anchorA);
        j.getAnchorB(anchorB);

        Vektor aOnZE =ebene.basis().getPhysikHandler().worldHandler().fromVec2(anchorA);
        Vektor bOnZE =ebene.basis().getPhysikHandler().worldHandler().fromVec2(anchorB);

        if(j instanceof RevoluteJoint) {
            g.setColor(Color.blue);
            g.drawOval((int)aOnZE.realX()-(CIRC_RAD/2), (int)aOnZE.realY()-(CIRC_RAD/2), CIRC_RAD, CIRC_RAD);
        } else if (j instanceof RopeJoint) {
            g.setColor(Color.cyan);
            g.drawRect((int)aOnZE.realX()-(CIRC_RAD/2), (int)aOnZE.realY()-(CIRC_RAD/2), RECT_SID, RECT_SID);
            g.drawRect((int)bOnZE.realX()-(CIRC_RAD/2), (int)bOnZE.realY()-(CIRC_RAD/2), RECT_SID, RECT_SID);
            g.drawLine((int)aOnZE.realX(),(int)aOnZE.realY(),(int)bOnZE.realX(), (int)bOnZE.realY());
        } else if (j instanceof DistanceJoint) {
            g.setColor(Color.orange);
            g.drawRect((int)aOnZE.realX()-(CIRC_RAD/2), (int)aOnZE.realY()-(CIRC_RAD/2), RECT_SID, RECT_SID);
            g.drawRect((int)bOnZE.realX()-(CIRC_RAD/2), (int)bOnZE.realY()-(CIRC_RAD/2), RECT_SID, RECT_SID);
            g.drawLine((int)aOnZE.realX(),(int)aOnZE.realY(),(int)bOnZE.realX(), (int)bOnZE.realY());
        }
    }

    /**
     * Gibt an, ob die Kamera ein Fokus-Objekt verfolgt oder "steif" ist.
     * @return <code>true</code>, wenn die Kamera ein Fokus-Objekt hat (und sich mit dem mitbewegt).
     * Sonst <code>false</code>.
     * @see #fokusSetzen(Raum)
     * @see #fokusLoeschen()
     */
    @API
    public boolean hatFokus() {
        return (fokus != null);
    }

    /**
     * Setzt die Perspektive der Kamera auf einen festen Bereich auf der Zeichenebene.
     * @param ausschnitt  Der Bereich auf der Zeichenebene, der von der Kamera vollständig möglichst groß in den
     *                          Fokus genommen werden soll.
     */
    @API
    public void blickeAuf(BoundingRechteck ausschnitt) {

        kameraAuschschnitt = new BoundingRechteck(ausschnitt.x, ausschnitt.y,
                kameraAuschschnitt.breite, kameraAuschschnitt.hoehe);

        //TODO Rotation einbinden (sobald eingebaut)
    }
}
