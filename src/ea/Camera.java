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
import ea.raum.Raum;

/**
 * Die Kamera "blickt" auf die Zeichenebene, das was sie sieht beschreibt den Teil der Zeichenebene;
 * das, was im Window dargestellt wird.<br /> Sie kann ein Objekt fokussieren und ihm so folgen.
 * Hierbei besteht auch die Moeglichkeit, diesen Fokus in Grenzen zu halten. Und zwar durch die
 * Fokus-Bounds. Diese 4 Grenzwerte koennen individuell verstellt und aktiviert werden. auch kann
 * man den von der Kamera darzustellenden Bereich durch eine einzige Methode definieren, in dem man
 * den Bereich als BoundingRechteck beschreibt.<br /> <br /> <br /> <br /> <code> BoundingRechteck
 * grenzen = new BoundingRechteck(0, 0, 1500, 1000);<br /> meineCam.setBounds(grenzen);<br />
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
public class Camera implements FrameUpdateListener {
    /**
     * Aktuelle Position des Mittelpunkts der Kamera.
     */
    private Punkt position;

    /**
     * Die Bounds der Kamera (sofern vorhanden), die sie in der Bewegung einschränken.
     */
    private BoundingRechteck bounds;

    /**
     * Der eventuelle Fokus der Kamera.
     */
    private Raum focus = null;

    /**
     * Der Kameraverzug.
     */
    private Vektor offset = Vektor.NULLVEKTOR;

    /**
     * Der aktuelle Kamerazoom.
     */
    private float zoom = 1;

    /**
     * Konstruktor erstellt eine neue Kamera mit Fokus auf <code>(0, 0)</code>.
     */
    public Camera() {
        this.position = new Punkt(0, 0);
    }

    /**
     * Setzt den Fokus der Kamera auf ein Objekt.
     * <p>
     * Dieses Objekt ist ab dann im 'Zentrum' der Kamera. Die Art des Fokus (rechts, links, oben,
     * unten, mittig, etc.) kann über die Methode {@link #setOffset(Vektor)} geändert
     * werden. Soll das Fokusverhalten beendet werden, kann einfach {@code null} übergeben werden,
     * dann bleibt die Kamera bis auf Weiteres in der aktuellen Position.
     *
     * @param focus Der Fokus.
     */
    @API
    public void setFocus(Raum focus) {
        this.focus = focus;
    }

    /**
     * Gibt an, ob die Kamera ein Fokus-Objekt verfolgt oder "steif" ist.
     *
     * @return <code>true</code>, wenn die Kamera ein Fokus-Objekt hat, sonst <code>false</code>.
     *
     * @see #setFocus(Raum)
     */
    @API
    public boolean hasFocus() {
        return focus != null;
    }

    /**
     * Setzt einen Kameraverzug. Der Standardwert hierfür ist <code>(0, 0)</code>.
     * <p>
     * Der Verzug ist ein Vektor, um den das Bild, das den Fokus exakt im Zentrum hat,
     * verschoben wird. Das heißt, dass eine Figur im Fokus um 100 Pixel tiefer als im
     * absoluten Bildzentrum liegt, wenn der Fokusverzug mit folgender Methode gesetzt wurde:
     * <code>camera.setOffset(new Vektor(0, -100));</code>
     *
     * @param offset Der Vektor, um den ab sofort die Kamera vom Zentrum des Fokus verschoben wird.
     */
    @API
    public void setOffset(Vektor offset) {
        this.offset = offset;
    }

    /**
     * Mit dieser Methode kann die Kamerabewegung eingeschränkt werden.
     * <p>
     * Ein Rechteck gibt die Begrenzung an, die die Kameraperspektive niemals übertreten wird.
     *
     * @param bounds Das Rechteck, das die Grenzen der Kamera angibt.
     */
    @API
    public void setBounds(BoundingRechteck bounds) {
        this.bounds = bounds;
    }

    /**
     * Gibt an, ob die Kamera durch Bounds in ihrer Bewegung beschränkt ist.
     *
     * @return <code>true</code> falls ja, sonst <code>false</code>.
     */
    @API
    public boolean hasBounds() {
        return this.bounds != null;
    }

    /**
     * Setzt den Zoom der Kamera.
     * <p>
     * Der Zoom bestimmt wie "nah" die Kamera an der Zeichenebene ist. Die Größe eines Objektes
     * entspricht der Größe auf der Zeichenebene multipliziert mit dem Zoomfaktor. Defaultwert des
     * Zoomfaktors ist <code>1</code>.
     *
     * @param zoom Der neue Zoom-Wert der Kamera. Werte größer als 1 "zoomen rein". Werte zwischen 1
     *             und 0 (jeweils exklusiv) "zoomen raus".
     */
    @API
    public void setZoom(float zoom) {
        if (zoom <= 0) {
            throw new IllegalArgumentException("Der Kamerazoom kann nicht kleiner oder gleich 0 sein.");
        }

        this.zoom = zoom;
    }

    /**
     * Gibt den aktuellen Zoom aus.
     *
     * @return Der aktuelle Zoom der Kamera.
     */
    @API
    public float getZoom() {
        return zoom;
    }

    /**
     * Verschiebt die Kamera um einen bestimmten Wert in <code>x</code>- und
     * <code>y</code>-Richtung (relativ).
     *
     * @param x Die Verschiebung in <code>x</code>-Richtung.
     * @param y Die Verschiebung in <code>y</code>-Richtung.
     */
    @API
    public void move(float x, float y) {
        this.position = this.position.verschobeneInstanz(new Vektor(x, y));
    }

    /**
     * Verschiebt das Zentrum der Kamera zur angegebenen Position (absolute Verschiebung). Von nun
     * an ist der Punkt mit den eingegebenen Koordinaten im Zentrum des Bildes.
     *
     * @param x Die <code>x</code>-Koordinate des Zentrums des Bildes.
     * @param y Die <code>y</code>-Koordinate des Zentrums des Bildes.
     */
    @API
    public void moveTo(int x, int y) {
        this.position = new Punkt(x, y);
    }

    /**
     * Die aktuelle Position der Kamera.
     *
     * @return Die aktuelle Position der Kamera.
     */
    @API
    public Punkt getPosition() {
        return this.position;
    }

    @Override
    public void onFrameUpdate(int frameDuration) {
        if (this.hasFocus()) {
            this.position = focus.position.mittelPunkt();
        }

        this.position = this.position.verschobeneInstanz(this.offset);

        if (this.hasBounds()) {
            float x = Math.max(this.bounds.getRealX(), Math.min(this.position.x(), this.bounds.getRealX() + this.bounds.getRealBreite()));
            float y = Math.max(this.bounds.getRealX(), Math.min(this.position.x(), this.bounds.getRealX() + this.bounds.getRealBreite()));

            this.position = new Punkt(x, y);
        }
    }
}
