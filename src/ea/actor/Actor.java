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

package ea.actor;

import ea.*;
import ea.collision.CollisionListener;
import ea.event.EventListeners;
import ea.event.ListenerEvent;
import ea.handle.BodyType;
import ea.handle.Physics;
import ea.handle.Position;
import ea.internal.ShapeBuilder;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.internal.physics.NullHandler;
import ea.internal.physics.PhysicsHandler;
import ea.internal.physics.ProxyData;
import ea.internal.physics.WorldHandler;
import ea.internal.util.Logger;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Actor bezeichnet alles, was sich auf der Zeichenebene befindet.<br> Dies ist die absolute Superklasse aller
 * grafischen Objekte. Umgekehrt kann somit jedes grafische Objekt die folgenden Methoden nutzen.
 *
 * @author Michael Andonie
 * @author Niklas Keller
 */
public abstract class Actor {
    /**
     * Gibt an, ob der Actor bereits zerstört wurde.
     */
    private boolean alive = true;

    /**
     * Gibt an, ob das Objekt zur Zeit überhaupt sichtbar sein soll.<br> Ist dies nicht der Fall, so wird die
     * Zeichenroutine direkt übergangen.
     */
    private boolean visible = true;

    /**
     * Z-Index des Raumes, je höher, desto weiter oben wird der Actor gezeichnet
     */
    private int layerPosition = 1;

    /**
     * Opacity = Durchsichtigkeit des Raumes
     * <p>
     * <ul><li><code>0.0f</code> entspricht einem komplett durchsichtigen Image.</li>
     * <li><code>1.0f</code> entspricht einem undurchsichtigem Image.</li></ul>
     */
    private float opacity = 1;

    /**
     * Composite des Grafik-Objekts. Zwischenspeicherung des letzten Zustands
     */
    private Composite composite;

    /**
     * Der JB2D-Handler für dieses spezifische Objekt.
     */
    private PhysicsHandler physicsHandler;

    private final EventListeners<Consumer<ListenerEvent>> mountListeners = new EventListeners<>();
    private final EventListeners<Consumer<ListenerEvent>> unmountListeners = new EventListeners<>();
    private final EventListeners<FrameUpdateListener> frameUpdateListeners = new EventListeners<>();

    /* _________________________ Die Handler _________________________ */

    /**
     * Über das <code>position</code>-Objekt lassen sich alle Operationen und Abfragen ausführen, die direkt die
     * Position dieses <code>Actor</code>-Objekts betreffen. Dazu gehört: <ul> <li>Das Abfragen der aktuellen
     * Position.</li> <li>Das Setzen einer Position das move.</li>
     * <li>Das Rotieren um einen Winkel.</li> </ul>
     * <p>
     * Die zugehörige Dokumentation gibt hierzu detaillierte Informationen.
     *
     * @see Position
     */
    @API
    public final Position position = new Position(this);

    /**
     * Über das <code>physics</code>-Objekt lassen sich alle Operationen und Abfragen ausführen, die direkt die
     * physikalischen Eigenschaften und Ümstände dieses <code>Actor</code>-Objekts betreffen. Dazu gehört: <ul> <li>Das
     * Abfragen und Setzen von physikalischen Eigenschaften des Objekt, wie zum Beispiel der <i>Masse</i> oder der
     * <i>Elastizität</i>.</li> <li>Das Anwenden von physikalischen Effekten (z.B. <i>Kräfte</i> oder <i>Impulse</i>)
     * auf das Objekt.</li>
     * <li>Das Ändern des <i>physikalischen Verhaltens</i> des Objekts.</li> </ul>
     * <p>
     * Die zugehörige Dokumentation gibt hierzu detaillierte Informationen.
     *
     * @see Position
     */
    @API
    public final Physics physics = new Physics(this);

    public Actor(Supplier<Shape> shapeSupplier) {
        this.physicsHandler = new NullHandler(this, new ProxyData(shapeSupplier));
        //this.physicsHandler = createBodyHandler(shapeSupplier.get());
    }

    @API
    public boolean isAlive() {
        return alive;
    }

    @API
    public final void addMountListener(Consumer<ListenerEvent> listener) {
        mountListeners.addListener(listener);

        if (physicsHandler.getWorldHandler() != null) {
            listener.accept(() -> mountListeners.removeListener(listener));
        }
    }

    @API
    public final void addUnmountListener(Consumer<ListenerEvent> listener) {
        unmountListeners.addListener(listener);

        if (physicsHandler.getWorldHandler() == null) {
            listener.accept(() -> unmountListeners.removeListener(listener));
        }
    }

    @API
    public final void addFrameUpdateListener(FrameUpdateListener listener) {
        frameUpdateListeners.addListener(listener);
    }

    @API
    public final void removeFrameUpdateListener(FrameUpdateListener listener) {
        frameUpdateListeners.removeListener(listener);
    }

    /* _________________________ Getter & Setter (die sonst nicht zuordbar) _________________________ */

    /**
     * Setzt den Layer dieses Actors. Je größer, desto weiter vorne wird ein Actor gezeichnet.
     * <b>Diese Methode muss ausgeführt werden, bevor der Actor zu einer ActorGroup hinzugefügt
     * wird.</b>
     *
     * @param position Layer-Index
     */
    @API
    public void setLayerPosition(int position) {
        this.layerPosition = position;
    }

    /**
     * Gibt den Layer zurück.
     *
     * @return Layer-Index
     */
    @API
    public int getLayerPosition() {
        return this.layerPosition;
    }

    /**
     * Setzt die Sichtbarkeit des Objektes.
     *
     * @param visible Ob das Objekt isVisible sein soll oder nicht.<br> Ist dieser Wert
     *                <code>false</code>, so wird es nicht im Window gezeichnet.
     *
     * @see #isVisible()
     */
    @API
    public final void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Gibt an, ob das Actor-Objekt isVisible ist.
     *
     * @return Ist <code>true</code>, wenn das Actor-Objekt zur Zeit isVisible ist.
     *
     * @see #setVisible(boolean)
     */
    @API
    public final boolean isVisible() {
        return this.visible;
    }

    /**
     * Gibt die aktuelle Opacity des Raumes zurück.
     *
     * @return Gibt die aktuelle Opacity des Raumes zurück.
     */
    @API
    public float getOpacity() {
        return opacity;
    }

    /**
     * Setzt die Opacity des Raumes.
     * <p>
     * <ul><li><code>0.0f</code> entspricht einem komplett durchsichtigen Actor.</li>
     * <li><code>1.0f</code> entspricht einem undurchsichtigem Actor.</li></ul>
     */
    @API
    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    /* _________________________ API-Methoden in der Klasse direkt _________________________ */

    /**
     * Prueft, ob ein bestimmter Point innerhalb des Actor-Objekts liegt.
     *
     * @param p Der Point, der auf Inhalt im Objekt getestet werden soll.
     *
     * @return TRUE, wenn der Point innerhalb des Objekts liegt.
     */
    @API
    public final boolean contains(Vector p) {
        return physicsHandler.contains(p);
    }

    /**
     * Prueft, ob dieser Actor sich mit einem weiteren Actor schneidet.<br> Für die Überprüfung des Überlappens werden
     * die internen <b>Collider</b> genutzt. Je nach Genauigkeit der Collider kann die Überprüfung unterschiedlich
     * befriedigend ausfallen. Die Collider können im <b>Debug-Modus</b> der Engine eingesehen werden.
     *
     * @param other Ein weiteres Actor-Objekt.
     *
     * @return <code>true</code>, wenn dieses Actor-Objekt sich mit <code>another</code> schneidet. Sonst
     * <code>false</code>.
     *
     * @see ea.Game#setDebug(boolean)
     */
    @API
    public final boolean overlaps(Actor other) {
        return WorldHandler.isBodyCollision(physicsHandler.getBody(), other.getPhysicsHandler().getBody());
    }

    /* _________________________ Utilities, interne & überschriebene Methoden _________________________ */

    /**
     * Setzt, was für eine Type physikalisches Objekt das Objekt sein soll. Erläuterung findet
     * sich im <code>enum Type</code>.
     *
     * @param type Der Type Physics-Objekt, der ab sofort dieses Objekt sein soll.
     *
     * @see BodyType
     */
    @API
    public void setBodyType(BodyType type) {
        this.physicsHandler.setType(type);
    }

    /**
     * Gibt aus, was für ein Type Physics-Objekt dieses Objekt momentan ist.
     *
     * @return der Type Physics-Objekt, der das entsprechende <code>Actor</code>-Objekt momentan ist.
     *
     * @see BodyType
     */
    @API
    public BodyType getBodyType() {
        return physicsHandler.getType();
    }

    /**
     * Setzt neue Shapes für das Actor Objekt. Hat Einfluss auf die Physik (Kollisionen, Masse, etc.)
     *
     * @param shapeCode der Shape-Code
     *
     * @see ShapeBuilder#fromString(String)
     * @see #setShape(Supplier)
     * @see #setShapes(Supplier)
     */
    @API
    public final void setShapes(String shapeCode) {
        this.physicsHandler.setShapes(ShapeBuilder.fromString(shapeCode));
    }

    /**
     * Ändert die Shape des Actors neu in eine alternative Shape.Alle anderen physikalischen Eigenschaften bleiben
     * weitgehend erhalten.
     *
     * @param shapeSupplier Der Supplier, der die neue Shape des Objektes ausgibt.
     *
     * @see #setShapes(Supplier)
     */
    @API
    public final void setShape(Supplier<Shape> shapeSupplier) {
        this.physicsHandler.setShapes(() -> {
            List<Shape> list = new ArrayList<>(1);
            list.add(shapeSupplier.get());
            return list;
        });
    }

    /**
     * Ändert die Shapes dieses Actors in eine Reihe neuer Shapes. Alle anderen physikalischen Eigenschaften bleiben
     * weitgehend erhalten.
     *
     * @param shapesSupplier Ein Supplier, der eine Liste mit allen neuen Shapes für den Actor angibt.
     *
     * @see #setShape(Supplier)
     */
    @API
    public final void setShapes(Supplier<List<Shape>> shapesSupplier) {
        this.physicsHandler.setShapes(shapesSupplier);
    }

    /**
     * Die Basiszeichenmethode.<br> Sie schließt eine Fallabfrage zur Sichtbarkeit ein. Diese Methode wird bei den
     * einzelnen Gliedern eines Knotens aufgerufen.
     *
     * @param g Das zeichnende Graphics-Objekt
     * @param r Das Bounds, dass die Kameraperspektive Repraesentiert.<br> Hierbei soll zunaechst getestet
     *          werden, ob das Objekt innerhalb der Kamera liegt, und erst dann gezeichnet werden.
     */
    @Internal
    public void renderBasic(Graphics2D g, Bounds r, float pixelPerMeter) {
        if (visible && this.isWithinBounds(r)) {
            synchronized (this) {
                float rotation = physicsHandler.getRotation();
                Vector position = physicsHandler.getPosition();

                // ____ Pre-Render ____

                AffineTransform transform = g.getTransform();

                g.rotate(-rotation, position.x * pixelPerMeter, -position.y * pixelPerMeter);
                g.translate(position.x * pixelPerMeter, -position.y * pixelPerMeter);

                //Opacity Update
                if (opacity != 1) {
                    composite = g.getComposite();
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, opacity));
                } else {
                    composite = null;
                }

                // ____ Render ____

                render(g, pixelPerMeter);

                if (Game.isDebug()) {
                    synchronized (this) {
                        // Visualisiere die Shape
                        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                        Body body = physicsHandler.getBody();

                        if (body != null) {
                            Fixture fixture = body.m_fixtureList;
                            while (fixture != null && fixture.m_shape != null) {
                                renderShape(fixture.m_shape, g, pixelPerMeter);
                                fixture = fixture.m_next;
                            }
                        }

                        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    }
                }

                // ____ Post-Render ____

                // Opacity Update
                if (composite != null) {
                    g.setComposite(composite);
                }

                // Transform zurücksetzen
                g.setTransform(transform);
            }
        }
    }

    /**
     * Rendert eine Shape von JBox2D vectorFromThisTo den gegebenen Voreinstellungen im Graphics-Objekt.
     *
     * @param shape Die Shape, die zu rendern ist.
     * @param g     Das Graphics2D-Object, das die Shape rendern soll. Farbe &amp; Co. sollte im Vorfeld
     *              eingestellt sein. Diese Methode übernimmt nur das direkte rendern.
     */
    @Internal
    public static void renderShape(Shape shape, Graphics2D g, float pixelPerMeter) {
        AffineTransform pre = g.getTransform();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        g.setColor(Color.YELLOW);
        g.drawLine(0, 0, 0, 0);
        g.setColor(Color.RED);

        if (shape instanceof PolygonShape) {
            PolygonShape polygonShape = (PolygonShape) shape;
            Vec2[] vec2s = polygonShape.getVertices();
            int[] xs = new int[polygonShape.getVertexCount()], ys = new int[polygonShape.getVertexCount()];
            for (int i = 0; i < xs.length; i++) {
                xs[i] = (int) (vec2s[i].x * pixelPerMeter);
                ys[i] = (-1) * (int) (vec2s[i].y * pixelPerMeter);
            }

            g.drawPolygon(xs, ys, xs.length);
        } else if (shape instanceof CircleShape) {
            CircleShape circleShape = (CircleShape) shape;
            float diameter = (circleShape.m_radius * 2);
            g.drawOval((int) ((circleShape.m_p.x - circleShape.m_radius) * pixelPerMeter), (int) ((-circleShape.m_p.y - circleShape.m_radius) * pixelPerMeter), (int) (diameter * (double) pixelPerMeter), (int) (diameter * (double) pixelPerMeter));
        } else {
            Logger.error("Debug/Render", "Konnte die Shape (" + shape + ") nicht rendern. Unerwartete Shape.");
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setTransform(pre);
    }

    /**
     * Interne Methode. Prüft, ob das anliegende Objekt (teilweise) innerhalb des sichtbaren Bereichs liegt.
     *
     * @param r Die Bounds der Kamera.
     *
     * @return <code>true</code>, wenn das Objekt (teilweise) innerhalb des derzeit sichtbaren
     * Breichs liegt, sonst <code>false</code>.
     */
    @Internal
    private boolean isWithinBounds(Bounds r) {
        // FIXME : Parameter ändern (?) und Funktionalität implementieren.
        return true;
    }

    /**
     * Gibt den aktuellen, internen Physics-Handler aus.
     *
     * @return der aktuellen, internen WorldHandler-Handler aus.
     */
    @Internal
    public PhysicsHandler getPhysicsHandler() {
        return physicsHandler;
    }

    /* _________________________ Listeners _________________________ */

    /**
     * Meldet einen neuen {@link CollisionListener} an, der auf alle Kollisionen zwischen diesem Actor und dem Actor
     * <code>collider</code> reagiert.
     *
     * @param listener Der Listener, der bei Kollisionen zwischen dem <b>ausführenden Actor</b> und
     *                 <code>collider</code> informiert werden soll.
     * @param collider Ein weiteres Actor-Objekt.
     * @param <E>      Typ-Parameter. SOllte im Regelfall exakt die Klasse von <code>collider</code> sein. Dies
     *                 ermöglicht die Nutzung von spezifischen Methoden aus spezialisierteren Klassen der
     *                 Actor-Hierarchie.
     *
     * @see #addCollisionListener(CollisionListener)
     */
    @API
    public <E extends Actor> void addCollisionListener(E collider, CollisionListener<E> listener) {
        addMountListener(e -> WorldHandler.addSpecificCollisionListener(this, collider, listener));
    }

    /**
     * Meldet einen neuen {@link CollisionListener} an, der auf alle Kollisionen reagiert, die dieser Actor mit seiner
     * Umwelt erlebt.
     *
     * @param listener Der Listener, der bei Kollisionen informiert werden soll, die der  <b>ausführende Actor</b> mit
     *                 allen anderen Objekten der Scene erlebt.
     *
     * @see #addCollisionListener(Actor, CollisionListener)
     */
    @API
    public void addCollisionListener(CollisionListener<Actor> listener) {
        addMountListener((e) -> WorldHandler.addGenericCollisionListener(listener, this));
    }

    /* _________________________ Kontrakt: Abstrakte Methoden/Funktionen eines Actor-Objekts _________________________ */

    /**
     * Rendert das Objekt am Ursprung. <ul> <li>Die Position ist (0|0).</li> <li>Die Roation ist 0.</li> </ul>
     *
     * @param g Das zeichnende Graphics-Objekt
     */
    @Internal
    public abstract void render(Graphics2D g, float pixelPerMeter);

    @Internal
    public void setPhysicsHandler(PhysicsHandler handler) {
        physicsHandler = handler;

        if (handler.getWorldHandler() == null) {
            unmountListeners.invoke(listener -> listener.accept(() -> unmountListeners.removeListener(listener)));
        } else {
            mountListeners.invoke(listener -> listener.accept(() -> mountListeners.removeListener(listener)));
        }
    }

    public Layer getLayer() {
        WorldHandler worldHandler = physicsHandler.getWorldHandler();
        if (worldHandler == null) {
            return null;
        }

        return worldHandler.getLayer();
    }

    public void removeFromScene() {
        Layer layer = getLayer();
        if (layer != null) {
            layer.remove(this);
        }
    }
}