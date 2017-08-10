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

package ea.raum;

import ea.*;
import ea.handle.Physik;
import ea.handle.Position;
import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import ea.internal.phy.BodyCreateStrategy;
import ea.internal.phy.NullHandler;
import ea.internal.phy.PhysikHandler;
import ea.internal.util.Logger;
import ea.keyboard.KeyListener;
import ea.mouse.MouseClickListener;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Raum bezeichnet alles, was sich auf der Zeichenebene befindet.<br /> Dies ist die absolute
 * Superklasse aller grafischen Objekte. Umgekehrt kann somit jedes grafische Objekt die folgenden
 * Methoden nutzen.
 *
 * @author Michael Andonie, Niklas Keller
 */
public abstract class Raum implements Comparable<Raum> {
    /**
     * Szene, zu der der Raum gehört.
     */
    private Scene scene;

    /**
     * Zum Überprüfen, dass ein Raum nur einmal zu einer Szene hinzugefügt wird.
     */
    private boolean attached = false;

    /**
     * Gibt an, ob das Objekt zur Zeit ueberhaupt sichtbar sein soll.<br /> Ist dies nicht der Fall,
     * so wird die Zeichenroutine direkt uebergangen.
     */
    private boolean sichtbar = true;

    /**
     * Z-Index des Raumes, je höher, desto weiter oben wird der Raum gezeichnet
     */
    private int zIndex = 1;

    /**
     * Opacity = Durchsichtigkeit des Raumes
     * <p/>
     * <ul><li><code>0.0f</code> entspricht einem komplett durchsichtigen Bild.</li>
     * <li><code>1.0f</code> entspricht einem undurchsichtigem Bild.</li></ul>
     */
    private float opacity = 1;

    /**
     * Composite des Grafik-Objekts. Zwischenspeicherung des letzten Zustands
     */
    private Composite composite;

    /**
     * Die Implementierung der Body-Erstellungsstrategie.
     */
    private BodyCreateStrategy bodyCreateStrategy;

    /**
     * Der JB2D-Handler für dieses spezifische Objekt.
     */
    protected PhysikHandler physikHandler = new NullHandler(this);

    /* _________________________ Die Handler _________________________ */

    /**
     * Über das <code>position</code>-Objekt lassen sich alle Operationen und Abfragen ausführen,
     * die direkt die Position dieses <code>Raum</code>-Objekts betreffen. Dazu gehört: <ul> <li>Das
     * Abfragen der aktuellen Position.</li> <li>Das Setzen einer Position das verschieben.</li>
     * <li>Das Rotieren um einen Winkel.</li> </ul>
     * <p>
     * Die zugehörige Dokumentation gibt hierzu detaillierte Informationen.
     *
     * @see Position
     */
    public final Position position = new Position(this);

    /**
     * Über das <code>physik</code>-Objekt lassen sich alle Operationen und Abfragen ausführen, die
     * direkt die physikalischen Eigenschaften und Ümstände dieses <code>Raum</code>-Objekts
     * betreffen. Dazu gehört: <ul> <li>Das Abfragen und Setzen von physikalischen Eigenschaften des
     * Objekt, wie zum Beispiel der <i>Masse</i> oder der <i>Elastizität</i>.</li> <li>Das Anwenden
     * von physikalischen Effekten (z.B. <i>Kräfte</i> oder <i>Impulse</i>) auf das Objekt.</li>
     * <li>Das Ändern des <i>physikalischen Verhaltens</i> des Objekts.</li> </ul>
     * <p>
     * Die zugehörige Dokumentation gibt hierzu detaillierte Informationen.
     *
     * @see Position
     */
    public final Physik physik = new Physik(this);

    /**
     * Diese Methode wird aufgerufen, sobald ein Raumobjekt zu einer Szene hinzugefügt wird, also am
     * Wurzelknoten der Szene direkt oder indirekt angemeldet wurde.
     *
     * @param scene Szene, an der das Raumobjekt angemeldet wurde.
     */
    public void onAttach(Scene scene) {
        if (this.attached) {
            throw new IllegalStateException("Ein Raumobjekt kann nur einmal einer Szene hinzugefügt werden. Um ein Objekt temporär auszublenden, kann sein Typ auf passiv gestellt werden und das Objekt unsichtbar gemacht werden.");
        }

        this.scene = scene;
        this.physikHandler.update(scene.getWorldHandler());

        if (this instanceof MouseClickListener) {
            scene.addMouseClickListener((MouseClickListener) this);
        }

        if (this instanceof KeyListener) {
            scene.addKeyListener((KeyListener) this);
        }

        if (this instanceof FrameUpdateListener) {
            scene.addFrameUpdateListener((FrameUpdateListener) this);
        }

        this.attached = true; // Set this after everything is done
    }

    /**
     * Diese Methode wird aufgerufen, sobald ein Raumobjekt von einer Szene entfernt wird, also am
     * Wurzelknoten der Szene direkt oder indirekt abgemeldet wurde.
     */
    public void onDetach() {
        if (!this.attached) {
            throw new IllegalStateException("Das Raumobjekt war bei keiner Szene angemeldet, wurde nun aber von einer entfernt?!");
        }

        this.physikHandler.killBody();
        this.physikHandler = new NullHandler(this);

        if (this instanceof MouseClickListener) {
            this.scene.removeMouseClickListener((MouseClickListener) this);
        }

        if (this instanceof KeyListener) {
            this.scene.removeKeyListener((KeyListener) this);
        }

        if (this instanceof FrameUpdateListener) {
            this.scene.removeFrameUpdateListener((FrameUpdateListener) this);
        }

        this.scene = null;
    }

    /* _________________________ Getter & Setter (die sonst nicht zuordbar) _________________________ */

    /**
     * Setzt den Z-Index dieses Raumes. Je größer, desto weiter vorne wird ein Raum gezeichnet.
     * <b>Diese Methode muss ausgeführt werden, bevor der Raum zu einem Knoten hinzugefügt
     * wird.</b>
     *
     * @param z zu setzender Index
     */
    @API
    public void zIndexSetzen(int z) {
        zIndex = z;
    }

    /**
     * Setzt die Sichtbarkeit des Objektes.
     *
     * @param sichtbar Ob das Objekt sichtbar sein soll oder nicht.<br /> Ist dieser Wert
     *                 <code>false</code>, so wird es nicht im Window gezeichnet.<br />
     *
     * @see #sichtbar()
     */
    @API
    public final void sichtbarSetzen(boolean sichtbar) {
        this.sichtbar = sichtbar;
    }

    /**
     * Gibt an, ob das Raum-Objekt sichtbar ist.
     *
     * @return Ist <code>true</code>, wenn das Raum-Objekt zur Zeit sichtbar ist.
     *
     * @see #sichtbarSetzen(boolean)
     */
    @API
    public final boolean sichtbar() {
        return this.sichtbar;
    }

    /**
     * Gibt die aktuelle Opacity des Raumes zurück.
     *
     * @return Gibt die aktuelle Opacity des Raumes zurück.
     */
    @API
    @SuppressWarnings ( "unused" )
    public float getOpacity() {
        return opacity;
    }

    /**
     * Setzt die Opacity des Raumes.
     * <p/>
     * <ul><li><code>0.0f</code> entspricht einem komplett durchsichtigen Raum.</li>
     * <li><code>1.0f</code> entspricht einem undurchsichtigem Raum.</li></ul>
     */
    @API
    @SuppressWarnings ( "unused" )
    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    /* _________________________ API-Methoden in der Klasse direkt _________________________ */

    /**
     * Prueft, ob ein bestimmter Punkt innerhalb des Raum-Objekts liegt.
     *
     * @param p Der Punkt, der auf Inhalt im Objekt getestet werden soll.
     *
     * @return TRUE, wenn der Punkt innerhalb des Objekts liegt.
     */
    @API
    public final boolean beinhaltet(Punkt p) {
        return physikHandler.beinhaltet(p);
    }

    /* _________________________ Utilities, interne & überschriebene Methoden _________________________ */

    @NoExternalUse
    public void bodyTypeSetzen(Physik.Typ typ) {
        this.physikHandler = physikHandler.typ(typ);
    }

    /**
     * Hilfsmethode für die Sortierung der Räume nach dem Z-Index. <b><i>Diese Methode sollte nicht
     * außerhalb der Engine verwendet werden.</i></b>
     *
     * @see #zIndex
     * @see #zIndexSetzen(int)
     */
    @Override
    @NoExternalUse
    public int compareTo(Raum r) {
        if (zIndex < r.zIndex) {
            return 1;
        }

        if (zIndex > r.zIndex) {
            return -1;
        }

        return 0;
    }

    /**
     * Die Basiszeichenmethode.<br /> Sie schließt eine Fallabfrage zur Sichtbarkeit ein. Diese
     * Methode wird bei den einzelnen Gliedern eines Knotens aufgerufen.
     *
     * @param g Das zeichnende Graphics-Objekt
     * @param r Das BoundingRechteck, dass die Kameraperspektive Repraesentiert.<br /> Hierbei soll
     *          zunaechst getestet werden, ob das Objekt innerhalb der Kamera liegt, und erst dann
     *          gezeichnet werden.
     */
    @NoExternalUse
    public void renderBasic(Graphics2D g, BoundingRechteck r) {
        if (sichtbar && this.camcheck(r)) {

            //Hole Rotation und Position absolut auf der Zeichenebene.
            float rotation;
            Punkt position;

            synchronized (physikHandler.getBody()) {
                rotation = physikHandler.rotation();
                position = physikHandler.position();
            }

            // ____ Pre-Render ____

            AffineTransform transform = g.getTransform();

            g.rotate(rotation, position.x, position.y); //TODO ist das die korrekte Rotation, Ursprung als Zentrum?
            g.translate(position.x, position.y);

            //Opacity Update
            if (opacity != 1) {
                composite = g.getComposite();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, opacity));
            } else {
                composite = null;
            }

            // ____ Render ____

            render(g);

            if (EngineAlpha.isDebug()) {
                //Visualisiere die Shape
                float ppm = getPhysikHandler().worldHandler().getPixelProMeter();
                Shape shape = createShape(ppm);
                g.setColor(Color.red);
                renderShape(shape, g, ppm);
            }

            // ____ Post-Render ____

            //Opacity Update
            if (composite != null) {
                g.setComposite(composite);
            }

            //Transform zurücksetzen
            g.setTransform(transform);

            //System.out.println("R: " + getPosition + " - " + rotation);
        }
    }

    /**
     * Rendert eine Shape von JBox2D nach den gegebenen Voreinstellungen im Graphics-Objekt.
     *
     * @param shape         Die Shape, die zu rendern ist.
     * @param g             Das Graphics2D-Object, das die Shape rendern soll. Farbe & Co. sollte im
     *                      Vorfeld eingestellt sein. Diese Methode übernimmt nur das direkte
     *                      Rendern
     * @param pixelPerMeter die Umrechnungsgröße, von Meter (JBox2D) auf Pixel (EA)
     */
    @NoExternalUse
    public static void renderShape(Shape shape, Graphics2D g, float pixelPerMeter) {
        if (shape instanceof PolygonShape) {
            PolygonShape polygonShape = (PolygonShape) shape;
            Vec2[] vec2s = polygonShape.getVertices();
            int[] xs = new int[polygonShape.getVertexCount()],
                    ys = new int[polygonShape.getVertexCount()];
            for (int i = 0; i < xs.length; i++) {
                xs[i] = (int) (vec2s[i].x * pixelPerMeter);
                ys[i] = (int) (vec2s[i].y * pixelPerMeter);
            }
            g.drawPolygon(xs, ys, xs.length);
        } else if (shape instanceof CircleShape) {
            int diameter = (int) (((CircleShape) shape).m_radius * 2 * pixelPerMeter);
            g.drawOval(0, 0, diameter, diameter);
        } else {
            Logger.error("Debug/Render", "Konnte die Shape (" + shape + ") nicht rendern. Unerwartete Shape.");
        }
    }

    /**
     * Interne Methode. Prüft, ob das anliegende Objekt (teilweise) innerhalb des sichtbaren
     * Bereichs liegt.
     *
     * @param r Die Bounds der Kamera.
     *
     * @return <code>true</code>, wenn das Objekt (teilweise) innerhalb des derzeit sichtbaren
     * Breichs liegt, sonst <code>false</code>.
     */
    @NoExternalUse
    private boolean camcheck(BoundingRechteck r) {
        //FIXME : Parameter ändern (?) und Funktionalität implementieren.
        //throw new UnsupportedOperationException("4.0 Implementierung steht aus.");
        return true;
    }

    /**
     * Gibt den aktuellen, internen Physik-Handler aus.
     *
     * @return der aktuellen, internen WorldHandler-Handler aus.
     */
    @NoExternalUse
    public PhysikHandler getPhysikHandler() {
        return physikHandler;
    }

    /**
     * Berechnet eine boxartige Shape. Alle Seiten sind parallel zu den Achsen, die linke obere Ecke
     * liegt auf (0|0).
     *
     * @param pixelProMeter PPM-Umrechnungskonstante.
     * @param breite        Die <b>Breite in Pixel</b> der Box.
     * @param laenge        Die <b>Laenge in Pixel</b> der Box.
     *
     * @return Eine Polygon-Shape, die die oben beschriebenen Eigenschaften erfüllt.
     */
    @NoExternalUse
    protected Shape berechneBoxShape(float pixelProMeter, float breite, float laenge) {
        PolygonShape shape = new PolygonShape();
        float breiteInM = breite / pixelProMeter;
        float laengeInM = laenge / pixelProMeter;
        Vec2 relativeCenter = new Vec2(breiteInM / 2, laengeInM / 2);
        shape.set(new Vec2[] {
                new Vec2(0, 0),
                new Vec2(0, laengeInM),
                new Vec2(breiteInM, laengeInM),
                new Vec2(breiteInM, 0)
        }, 4);
        shape.m_centroid.set(relativeCenter);
        return shape;
    }

    /* _________________________ Kontrakt: Abstrakte Methoden/Funktionen eines Raum-Objekts _________________________ */

    /**
     * Rendert das Objekt am Ursprung. <ul> <li>Die Position ist (0|0).</li> <li>Die Roation ist
     * 0.</li> </ul>
     *
     * @param g Das zeichnende Graphics-Objekt
     */
    @NoExternalUse
    public abstract void render(Graphics2D g);

    /**
     * Berechnet eine Form, die für die Kollisionsberechnungen dieses <code>Raum</code>-Objekts
     * verwendet werden.
     *
     * @param pixelProMeter Die [px/m]-Konstante für die Umrechnung.
     *
     * @return Die zu dem Objekt zugehörige Shape in <b>[m]-Einheit, nicht in [px]</b>. Die
     * Berechnung berücksichtigt die <b>aktuelle Position</b>.PositionHandlePositionalUse
     */
    @NoExternalUse
    public abstract Shape createShape(final float pixelProMeter);

    /**
     * Überschriebene <code>finalize</code>-Methode. Loggt verbose die Garbage Collection des
     * Raum-Objekts.
     *
     * @throws Throwable Übernommen von Object
     */
    @Override
    public void finalize()
            throws Throwable {
        super.finalize();
        //Logge die Zerstörung
        Logger.verboseInfo("Raum", "Raum-Objekt in Garbage Collection: " + toString());
    }

    public Scene getScene() {
        return scene;
    }
}