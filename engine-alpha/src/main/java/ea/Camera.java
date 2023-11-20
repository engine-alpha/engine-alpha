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

import ea.actor.Actor;
import ea.internal.Bounds;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

import java.awt.Point;

/**
 * Die Kamera "blickt" auf die Zeichenebene, das, was sie sieht, beschreibt den Teil der Zeichenebene;
 * das, was im Window dargestellt wird.<br> Sie kann ein Objekt fokussieren und ihm so folgen.
 * Hierbei besteht auch die Möglichkeit, diesen Fokus in Grenzen zu halten. Und zwar durch die
 * Fokus-Bounds. Diese 4 Grenzwerte können individuell verstellt und aktiviert werden. auch kann
 * man den von der Kamera darzustellenden Bereich durch eine einzige Methode definieren, in dem man
 * den Bereich als Bounds beschreibt.<br> <br> <br> <br> <code> Bounds
 * grenzen = new Bounds(0, 0, 1500, 1000);<br> meineCam.setBounds(grenzen);<br>
 * </code> <br> <br> <br> Hierdurch wird automatisch der gesamte Fokusapparat (auf den Bereich
 * zwischen den Punkten (0|0) und (1500|1000) ) eingestellt. Bei spezielleren Fokuswünschen lässt
 * sich dies ebenfalls arrangieren durch die einzelnen Methoden, mit denen alle vier Bounds (N, S,
 * O, W) einzeln verstellt und (de)aktiviert werden können.<br> <b>!!Achtung!!</b><br> Bei den
 * Fokuseinstellungen sollte immer ein Bereich gewählt werden, der die Größe des Anzeigefensters
 * (oder Vollbildes) bei weitem übersteigt.<br> Allgemein wirken diese Bounds auch ohne
 * aktivierten Fokus. jedoch ist dies meist weniger sinnvoll.
 *
 * @author Michael Andonie
 */
public final class Camera {
    public static final float DEFAULT_ZOOM = 30;

    /**
     * Aktuelle Position des Mittelpunkts der Kamera.
     */
    private Vector position;

    /**
     * Die Bounds der Kamera (sofern vorhanden), die sie in der Bewegung einschränken.
     */
    private Bounds bounds;

    /**
     * Der eventuelle Fokus der Kamera.
     */
    private Actor focus = null;

    /**
     * Der Kameraverzug.
     */
    private Vector offset = Vector.NULL;

    /**
     * Der aktuelle Kamerazoom.
     */
    private float zoom = DEFAULT_ZOOM;

    /**
     * Die aktuelle Drehung in Grad.
     */
    private float rotation = 0;

    /**
     * Konstruktor erstellt eine neue Kamera mit Fokus auf <code>(0, 0)</code>.
     */
    @Internal
    public Camera() {
        this.position = new Vector(0, 0);
    }

    /**
     * Setzt den Fokus der Kamera auf ein Objekt.
     * <p>
     * Dieses Objekt ist ab dann im 'Zentrum' der Kamera. Die Art des Fokus (rechts, links, oben,
     * unten, mittig, etc.) kann über die Methode {@link #setOffset(Vector)} geändert
     * werden. Soll das Fokusverhalten beendet werden, kann einfach {@code null} übergeben werden,
     * dann bleibt die Kamera bis auf Weiteres in der aktuellen Position.
     *
     * @param focus Der Fokus.
     */
    @API
    public void setFocus(Actor focus) {
        this.focus = focus;
    }

    /**
     * Gibt an, ob die Kamera ein Fokus-Objekt verfolgt oder "steif" ist.
     *
     * @return <code>true</code>, wenn die Kamera ein Fokus-Objekt hat, sonst <code>false</code>.
     *
     * @see #setFocus(Actor)
     */
    @API
    public boolean hasFocus() {
        return focus != null;
    }

    /**
     * Setzt einen Kameraverzug. Der Standardwert hierfür ist <code>(0, 0)</code>.
     * <p>
     * Der Verzug ist ein Vector, um den das Image, das den Fokus exakt im Zentrum hat,
     * verschoben wird. Das heißt, dass eine Figur im Fokus um 100 Pixel tiefer als im
     * absoluten Bildzentrum liegt, wenn der Fokusverzug mit folgender Methode gesetzt wurde:
     * <code>camera.setOffset(new Vector(0, -100));</code>
     *
     * @param offset Der Vector, um den ab sofort die Kamera vom Zentrum des Fokus verschoben wird.
     */
    @API
    public void setOffset(Vector offset) {
        this.offset = offset;
    }

    /**
     * Gibt den Offset/Verzug der Kamera aus.
     *
     * @return Der aktuelle Verzug der Kamera.
     *
     * @see #setOffset(Vector)
     */
    @API
    public Vector getOffset() {
        return this.offset;
    }

    /**
     * Mit dieser Methode kann die Kamerabewegung eingeschränkt werden.
     * <p>
     * Ein Rectangle gibt die Begrenzung an, die die Kameraperspektive niemals übertreten wird.
     *
     * @param bounds Das Rectangle, das die Grenzen der Kamera angibt.
     */
    @API
    public void setBounds(Bounds bounds) {
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
     * Verschiebt die Kamera um einen bestimmten Wert in <code>getX</code>- und
     * <code>getY</code>-Richtung (relativ).
     *
     * @param x Die Verschiebung in <code>getX</code>-Richtung.
     * @param y Die Verschiebung in <code>getY</code>-Richtung.
     */
    @API
    public void moveBy(float x, float y) {
        this.moveBy(new Vector(x, y));
    }

    @API
    public void moveBy(Vector vector) {
        this.position = this.position.add(vector);
    }

    /**
     * Verschiebt das Zentrum der Kamera zur angegebenen Position (absolute Verschiebung). Von nun
     * an ist der Point mit den eingegebenen Koordinaten im Zentrum des Bildes.
     *
     * @param x Die <code>getX</code>-Koordinate des Zentrums des Bildes.
     * @param y Die <code>getY</code>-Koordinate des Zentrums des Bildes.
     */
    @API
    public void moveTo(int x, int y) {
        this.moveTo(new Vector(x, y));
    }

    @API
    public void moveTo(Vector vector) {
        this.position = vector;
    }

    @API
    public void rotateBy(float degree) {
        this.rotation += degree;
    }

    @API
    public void rotateTo(float degree) {
        this.rotation = degree;
    }

    /**
     * Setzt die aktuelle Position der Kamera
     *
     * @param position die neue Position der Kamera
     */
    @API
    public void setPosition(Vector position) {
        this.position = position;
    }

    /**
     * Setzt die aktuelle Position der Kamera
     *
     * @param x Neue X-Koordinate des Kamerazentrums
     * @param y Neue Y-Koordinate des Kamerazentrums
     */
    @API
    public void setPostion(float x, float y) {
        this.setPosition(new Vector(x, y));
    }

    /**
     * Die aktuelle Position der Kamera.
     *
     * @return Die aktuelle Position der Kamera.
     */
    @API
    public Vector getPosition() {
        return moveIntoBounds(this.position.add(this.offset));
    }

    /**
     * Gibt die Position eines Punktes in der World an, relativ zu seiner aktuell zu zeichnenden Position und in Px.
     *
     * @param locationInWorld Ein Punkt in der Welt
     * @param pixelPerMeter   Umrechnungsfaktor pixelPerMeter
     *
     * @return Die zu zeichnende Position des Punktes in Px
     *
     * @hidden
     */
    @Internal
    public Point toScreenPixelLocation(Vector locationInWorld, float pixelPerMeter) {
        Vector cameraRelativeLocInPx = position.multiply(pixelPerMeter);

        Vector frameSize = Game.getFrameSizeInPixels();

        return new Point((int) (frameSize.getX() / 2 + cameraRelativeLocInPx.getX()), (int) (frameSize.getY() / 2 + cameraRelativeLocInPx.getY()));
    }

    // Does not implement FrameUpdateListener by design, as it's updated at a special moment
    public void onFrameUpdate() {
        if (this.hasFocus()) {
            this.position = focus.getCenter();
        }

        this.position = moveIntoBounds(this.position);
    }

    public float getRotation() {
        return rotation;
    }

    private Vector moveIntoBounds(Vector position) {
        if (!this.hasBounds()) {
            return position;
        }

        float x = Math.max(this.bounds.getX(), Math.min(position.getX(), this.bounds.getX() + this.bounds.getWidth()));
        float y = Math.max(this.bounds.getY(), Math.min(position.getY(), this.bounds.getY() + this.bounds.getHeight()));

        return new Vector(x, y);
    }
}
