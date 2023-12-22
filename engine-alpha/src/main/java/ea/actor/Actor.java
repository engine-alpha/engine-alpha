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

import ea.FrameUpdateListener;
import ea.Game;
import ea.Layer;
import ea.Vector;
import ea.animation.ValueAnimator;
import ea.animation.interpolation.EaseInOutFloat;
import ea.collision.CollisionEvent;
import ea.collision.CollisionListener;
import ea.event.*;
import ea.internal.Bounds;
import ea.internal.FixtureBuilder;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.internal.physics.*;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.joints.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Jedes Objekt auf der Zeichenebene ist ein Actor.<br>Dies ist die absolute Superklasse aller
 * grafischen Objekte. Umgekehrt kann somit jedes grafische Objekt die folgenden Methoden nutzen.
 *
 * @author Michael Andonie
 * @author Niklas Keller
 */
@SuppressWarnings("OverlyComplexClass")
public abstract class Actor implements KeyListenerContainer, MouseClickListenerContainer, MouseWheelListenerContainer, FrameUpdateListenerContainer {
    private <T> Supplier<T> createParentSupplier(Function<Layer, T> supplier) {
        return () -> {
            Layer layer = getLayer();
            if (layer == null) {
                return null;
            }

            return supplier.apply(layer);
        };
    }

    /**
     * Gibt an, ob das Objekt zurzeit überhaupt sichtbar sein soll.<br> Ist dies nicht der Fall, so wird die
     * Zeichenroutine direkt übergangen.
     */
    private boolean visible = true;

    /**
     * Z-Index des Objekts, je höher, desto weiter im Vordergrund wird das Objekt gezeichnet.
     */
    private int layerPosition = 1;

    /**
     * Opacity = Durchsichtigkeit des Objekts
     * <p>
     * <ul><li><code>0.0f</code> entspricht einem komplett durchsichtigen Bild.</li>
     * <li><code>1.0f</code> entspricht einem undurchsichtigem Bild.</li></ul>
     */
    private float opacity = 1;

    /**
     * Der JB2D-Handler für dieses spezifische Objekt.
     */
    private PhysicsHandler physicsHandler;

    private final EventListeners<Runnable> mountListeners = new EventListeners<>();
    private final EventListeners<Runnable> unmountListeners = new EventListeners<>();
    private final EventListeners<KeyListener> keyListeners = new EventListeners<>(createParentSupplier(Layer::getKeyListeners));
    private final EventListeners<MouseClickListener> mouseClickListeners = new EventListeners<>(createParentSupplier(Layer::getMouseClickListeners));
    private final EventListeners<MouseWheelListener> mouseWheelListeners = new EventListeners<>(createParentSupplier(Layer::getMouseWheelListeners));
    private final EventListeners<FrameUpdateListener> frameUpdateListeners = new EventListeners<>(createParentSupplier(Layer::getFrameUpdateListeners));

    /**
     * Erstellt ein neues Objekt.
     *
     * @param defaultFixtureSupplier Ein Supplier, der die Default-Shape für dieses Objekt generiert.
     *                               Die ist in der Regel ein optimal gelegtes Rechteck parallel zu den Axen bei
     *                               Rotationswinkel 0.
     */
    public Actor(Supplier<FixtureData> defaultFixtureSupplier) {
        this.physicsHandler = new NullHandler(new PhysicsData(() -> Collections.singletonList(defaultFixtureSupplier.get())));
        EventListenerHelper.autoRegisterListeners(this);
    }

    /**
     * Fügt einen Listener hinzu, der ausgeführt wird, sobald das Objekt angemeldet wurde.
     *
     * @param listener Listener-Implementierung
     */
    @API
    public final void addMountListener(Runnable listener) {
        mountListeners.add(listener);

        if (isMounted()) {
            listener.run();
        }
    }

    /**
     * Entfernt einen Listener, der ausgeführt wird, sobald das Objekt angemeldet wurde.
     *
     * @param listener Listener-Implementierung
     */
    @API
    public final void removeMountListener(Runnable listener) {
        mountListeners.remove(listener);
    }

    /**
     * Fügt einen Listener hinzu, der ausgeführt wird, sobald das Objekt abgemeldet wurde.
     *
     * @param listener Listener-Implementierung
     */
    @API
    public final void addUnmountListener(Runnable listener) {
        unmountListeners.add(listener);
    }

    /**
     * Entfernt einen Listener, der ausgeführt wird, sobald das Objekt abgemeldet wurde.
     *
     * @param listener Listener-Implementierung
     */
    @API
    public final void removeUnmountListener(Runnable listener) {
        unmountListeners.remove(listener);
    }

    /**
     * Setzt die Layer-Position dieses Objekts. Je größer, desto weiter vorne wird das Objekt gezeichnet.
     *
     * @param position Layer-Index
     * @see #getLayerPosition()
     */
    @API
    public final void setLayerPosition(int position) {
        this.layerPosition = position;
    }

    /**
     * Gibt die Layer-Position zurück. Je größer, desto weiter vorne wird das Objekt gezeichnet.
     *
     * @return Layer-Index
     * @see #setLayerPosition(int)
     */
    @API
    public final int getLayerPosition() {
        return this.layerPosition;
    }

    /**
     * Setzt die Sichtbarkeit des Objektes.
     *
     * @param visible Ob das Objekt isVisible sein soll oder nicht.<br> Ist dieser Wert
     *                <code>false</code>, so wird es nicht gezeichnet.
     * @see #isVisible()
     */
    @API
    public final void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Gibt an, ob das Objekt sichtbar ist.
     *
     * @return Ist <code>true</code>, wenn das Objekt zurzeit sichtbar ist.
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
    public final float getOpacity() {
        return opacity;
    }

    /**
     * Setzt die Sichtbarkeit des Objekts.
     *
     * @param opacity <ul><li><code>0.0f</code> entspricht einem komplett durchsichtigen (transparenten) Objekt.</li>
     *                <li><code>1.0f</code> entspricht einem undurchsichtigem Objekt.</li></ul>
     */
    @API
    public final void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    /**
     * Prüft, ob ein bestimmter Punkt innerhalb des Objekts liegt.
     *
     * @param p Der Punkt, der auf Inhalt im Objekt getestet werden soll.
     * @return <code>true</code>, wenn der Point innerhalb des Objekts liegt.
     */
    @API
    public final boolean contains(Vector p) {
        return physicsHandler.contains(p);
    }

    /**
     * Prüft, ob dieses Objekt sich mit einem weiteren Objekt schneidet.<br> Für die Überprüfung des Überlappens werden
     * die internen <b>Collider</b> genutzt. Je nach Genauigkeit der Collider kann die Überprüfung unterschiedlich
     * befriedigend ausfallen. Die Collider können im <b>Debug-Modus</b> der Engine eingesehen werden.
     *
     * @param other Ein weiteres Actor-Objekt.
     * @return <code>true</code>, wenn dieses Actor-Objekt sich mit <code>another</code> schneidet. Sonst
     * <code>false</code>.
     * @see ea.Game#setDebug(boolean)
     */
    @API
    public final boolean overlaps(Actor other) {
        Body a = physicsHandler.getBody();
        Body b = other.getPhysicsHandler().getBody();

        return WorldHandler.isBodyCollision(a, b);
    }

    public final List<CollisionEvent<Actor>> getCollisions() {
        return physicsHandler.getCollisions();
    }

    /**
     * Setzt das allgemeine Verhalten dieses Objekts im Rahmen der Physics-Engine (und Collision Detection)
     * haben soll.
     * Eine Erläuterung der verschiedenen Verhaltenstypen finden sich in der Dokumentation von <code>BodyType</code>.
     *
     * @param type Der neue <code>BodyType</code>, für den Actor.
     * @see BodyType
     */
    @API
    public final void setBodyType(BodyType type) {
        Objects.requireNonNull(type, "Typ darf nicht null sein");

        this.physicsHandler.setType(type);
    }

    /**
     * Gibt aus, was für ein Physics-Typ dieses Objekt momentan ist.
     *
     * @return der Physics-Typ, der das entsprechende <code>Actor</code>-Objekt momentan ist.
     * @see BodyType
     */
    @API
    public final BodyType getBodyType() {
        return physicsHandler.getType();
    }

    /**
     * Setzt neue Shapes für das Objekt. Hat Einfluss auf die Physik (Kollisionen, Masse, etc.)
     *
     * @param shapeCode der Shape-Code
     * @see FixtureBuilder#fromString(String)
     * @see #setFixture(Supplier)
     * @see #setFixtures(Supplier)
     */
    @API
    public final void setFixtures(String shapeCode) {
        this.setFixtures(FixtureBuilder.fromString(shapeCode));
    }

    /**
     * Ändert die Fixture des Actors neu in eine einzige alternative Fixture.
     *
     * @param fixtureSupplier Der Supplier, der die neue Shape des Objektes ausgibt.
     * @see #setFixtures(Supplier)
     */
    @API
    public final void setFixture(Supplier<FixtureData> fixtureSupplier) {
        this.setFixtures(() -> Collections.singletonList(fixtureSupplier.get()));
    }

    /**
     * Ändert die Fixtures dieses Actors in eine Reihe neuer Fixtures.
     *
     * @param fixturesSupplier Ein Supplier, der eine Liste mit allen neuen Shapes für den Actor angibt.
     * @see #setFixture(Supplier)
     */
    @API
    public final void setFixtures(Supplier<List<FixtureData>> fixturesSupplier) {
        this.physicsHandler.setFixtures(fixturesSupplier);
    }

    /**
     * Die Basiszeichenmethode.<br> Sie schließt eine Fallabfrage zur Sichtbarkeit ein.
     *
     * @param g             Das zeichnende Graphics-Objekt
     * @param r             Das Bounds, dass die Kameraperspektive repräsentiert.<br> Hierbei soll zunächst getestet
     *                      werden, ob das Objekt innerhalb der Kamera liegt, und erst dann gezeichnet werden.
     * @param pixelPerMeter Pixel pro Meter.
     */
    @Internal
    public final void renderBasic(Graphics2D g, Bounds r, float pixelPerMeter) {
        if (visible && this.isWithinBounds(r)) {
            float rotation = physicsHandler.getRotation();
            Vector position = physicsHandler.getPosition();

            // ____ Pre-Render ____

            AffineTransform transform = g.getTransform();

            g.rotate(-Math.toRadians(rotation), position.getX() * pixelPerMeter, -position.getY() * pixelPerMeter);
            g.translate(position.getX() * pixelPerMeter, -position.getY() * pixelPerMeter);

            // Opacity Update
            Composite composite;
            if (opacity != 1) {
                composite = g.getComposite();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
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

    /**
     * Rendert eine Shape von JBox2D nach den gegebenen Voreinstellungen im Graphics-Objekt.
     *
     * @param shape Die Shape, die zu rendern ist.
     * @param g     Das Graphics-Objekt, das die Shape rendern soll. Farbe &amp; Co. sollte im Vorfeld
     *              eingestellt sein. Diese Methode übernimmt nur das direkte rendern.
     */
    @Internal
    private static void renderShape(Shape shape, Graphics2D g, float pixelPerMeter) {
        if (shape == null) {
            return;
        }

        AffineTransform pre = g.getTransform();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        g.setColor(Color.YELLOW);
        g.drawOval(-1, -1, 2, 2);
        g.setColor(Color.RED);

        if (shape instanceof PolygonShape polygonShape) {
            Vec2[] vec2s = polygonShape.getVertices();
            int[] xs = new int[polygonShape.getVertexCount()], ys = new int[polygonShape.getVertexCount()];
            for (int i = 0; i < xs.length; i++) {
                xs[i] = (int) (vec2s[i].x * pixelPerMeter);
                ys[i] = (-1) * (int) (vec2s[i].y * pixelPerMeter);
            }

            g.drawPolygon(xs, ys, xs.length);
        } else if (shape instanceof CircleShape circleShape) {
            float diameter = (circleShape.m_radius * 2);
            g.drawOval((int) ((circleShape.m_p.x - circleShape.m_radius) * pixelPerMeter), (int) ((-circleShape.m_p.y - circleShape.m_radius) * pixelPerMeter), (int) (diameter * (double) pixelPerMeter), (int) (diameter * (double) pixelPerMeter));
        } else {
            throw new RuntimeException("Konnte die Shape (" + shape + ") nicht rendern, unerwartete Shape");
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setTransform(pre);
    }

    /**
     * Interne Methode. Prüft, ob das anliegende Objekt (teilweise) innerhalb des sichtbaren Bereichs liegt.
     *
     * @param bounds Die Bounds der Kamera.
     * @return <code>true</code>, wenn das Objekt (teilweise) innerhalb des derzeit sichtbaren
     * Breichs liegt, sonst <code>false</code>.
     */
    @Internal
    private boolean isWithinBounds(Bounds bounds) {
        // FIXME : Parameter ändern (?) und Funktionalität implementieren.
        return true;
    }

    /**
     * Gibt den aktuellen, internen Physics-Handler aus.
     *
     * @return der aktuellen, internen Physics-Handler aus.
     */
    @Internal
    public final PhysicsHandler getPhysicsHandler() {
        return physicsHandler;
    }

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
     * @see #addCollisionListener(CollisionListener)
     */
    @API
    public final <E extends Actor> void addCollisionListener(E collider, CollisionListener<E> listener) {
        WorldHandler.addSpecificCollisionListener(this, collider, listener);
    }

    /**
     * Meldet einen neuen {@link CollisionListener} an, der auf alle Kollisionen reagiert, die dieser Actor mit seiner
     * Umwelt erlebt.
     *
     * @param <E>      Typ des anderen Objekts bei Kollisionen.
     * @param clazz    Typ des anderen Objekts bei Kollisionen.
     * @param listener Der Listener, der bei Kollisionen informiert werden soll, die der  <b>ausführende Actor</b> mit
     *                 allen anderen Objekten der Scene erlebt.
     * @see #addCollisionListener(Actor, CollisionListener)
     */
    @API
    public final <E extends Actor> void addCollisionListener(Class<E> clazz, CollisionListener<E> listener) {
        // noinspection OverlyComplexAnonymousInnerClass
        WorldHandler.addGenericCollisionListener(new CollisionListener<>() {
            @Override
            public void onCollision(CollisionEvent<Actor> collisionEvent) {
                if (clazz.isInstance(collisionEvent.getColliding())) {
                    // noinspection unchecked
                    listener.onCollision((CollisionEvent<E>) collisionEvent);
                }
            }

            @Override
            public void onCollisionEnd(CollisionEvent<Actor> collisionEvent) {
                if (clazz.isInstance(collisionEvent.getColliding())) {
                    // noinspection unchecked
                    listener.onCollisionEnd((CollisionEvent<E>) collisionEvent);
                }
            }
        }, this);
    }

    /**
     * Meldet einen neuen {@link CollisionListener} an, der auf alle Kollisionen reagiert, die dieser Actor mit seiner
     * Umwelt erlebt.
     *
     * @param listener Der Listener, der bei Kollisionen informiert werden soll, die der  <b>ausführende Actor</b> mit
     *                 allen anderen Objekten der Szene erlebt.
     * @see #addCollisionListener(Actor, CollisionListener)
     */
    @API
    public final void addCollisionListener(CollisionListener<Actor> listener) {
        WorldHandler.addGenericCollisionListener(listener, this);
    }

    /**
     * Rendert das Objekt am Ursprung. <ul> <li>Die Position ist (0|0).</li> <li>Die Roation ist 0.</li> </ul>
     *
     * @param g             Das zeichnende Graphics-Objekt
     * @param pixelPerMeter Pixel pro Meter.
     */
    @Internal
    public abstract void render(Graphics2D g, float pixelPerMeter);

    @Internal
    public final void setPhysicsHandler(PhysicsHandler handler) {
        WorldHandler worldHandler = handler.getWorldHandler();
        WorldHandler previousWorldHandler = physicsHandler.getWorldHandler();

        if (worldHandler == null) {
            if (previousWorldHandler == null) {
                return;
            }

            Layer layer = previousWorldHandler.getLayer();

            keyListeners.invoke(layer::removeKeyListener);
            mouseClickListeners.invoke(layer::removeMouseClickListener);
            mouseWheelListeners.invoke(layer::removeMouseWheelListener);
            frameUpdateListeners.invoke(layer::removeFrameUpdateListener);

            unmountListeners.invoke(Runnable::run);

            physicsHandler = handler;
        } else {
            if (previousWorldHandler != null) {
                return;
            }

            physicsHandler = handler;

            Layer layer = worldHandler.getLayer();

            mountListeners.invoke(Runnable::run);

            keyListeners.invoke(layer::addKeyListener);
            mouseClickListeners.invoke(layer::addMouseClickListener);
            mouseWheelListeners.invoke(layer::addMouseWheelListener);
            frameUpdateListeners.invoke(layer::addFrameUpdateListener);
        }
    }

    /**
     * @return Gibt die Ebene zurück, an der das aktuelle Objekt angemeldet ist, sonst {@code null}.
     */
    public final Layer getLayer() {
        WorldHandler worldHandler = physicsHandler.getWorldHandler();
        if (worldHandler == null) {
            return null;
        }

        return worldHandler.getLayer();
    }

    /**
     * Entfernt das aktuelle Objekt aus seiner aktuellen Ebene, falls das Objekt gerade einer Ebene zugeordnet ist.
     */
    public final void remove() {
        Layer layer = getLayer();
        if (layer != null) {
            layer.remove(this);
        }
    }

    /**
     * @return Liste der {@link KeyListener}.
     */
    @API
    public final EventListeners<KeyListener> getKeyListeners() {
        return keyListeners;
    }

    /**
     * @return Liste der {@link MouseClickListener}.
     */
    @API
    public final EventListeners<MouseClickListener> getMouseClickListeners() {
        return mouseClickListeners;
    }

    /**
     * @return Liste der {@link MouseWheelListener}.
     */
    @API
    public final EventListeners<MouseWheelListener> getMouseWheelListeners() {
        return mouseWheelListeners;
    }

    /**
     * @return Liste der {@link FrameUpdateListener}.
     */
    @API
    public final EventListeners<FrameUpdateListener> getFrameUpdateListeners() {
        return frameUpdateListeners;
    }

    /**
     * Setzt, ob <i>im Rahmen der physikalischen Simulation</i> die Rotation dieses Objekts
     * blockiert werden soll. <br>
     * Das Objekt kann in jedem Fall weiterhin über einen direkten Methodenaufruf rotiert
     * werden. Der folgende Code ist immer wirksam, unabhängig davon, ob die Rotation
     * im Rahmen der physikalischen Simulation blockiert ist:<br>
     * <code>
     * actor.getPosition.rotate(4.31f);
     * </code>
     *
     * @param rotationLocked Ist dieser Wert <code>true</code>, rotiert sich dieses
     *                       Objekts innerhalb der physikalischen Simulation <b>nicht mehr</b>.
     *                       Ist dieser Wert <code>false</code>, rotiert sich dieses
     *                       Objekt innerhalb der physikalsichen Simulation.
     * @see #isRotationLocked()
     */
    @API
    public final void setRotationLocked(boolean rotationLocked) {
        physicsHandler.setRotationLocked(rotationLocked);
    }

    /**
     * Gibt an, ob die Rotation dieses Objekts derzeit innerhalb der physikalischen Simulation
     * blockiert ist.
     *
     * @return <code>true</code>, wenn die Rotation dieses Objekts derzeit innerhalb der
     * physikalischen Simulation blockiert ist.
     * @see #setRotationLocked(boolean)
     */
    @API
    public final boolean isRotationLocked() {
        return physicsHandler.isRotationLocked();
    }

    /**
     * Gibt die aktuelle Masse des Ziel-Objekts aus. Die Form bleibt unverändert, daher ändert sich
     * die <b>Dichte</b> in der Regel.
     *
     * @return Die Masse des Ziel-Objekts in <b>[kg]</b>.
     */
    @API
    public final float getMass() {
        return physicsHandler.getMass();
    }

    /**
     * Setzt die Dichte des Objekts neu. Die Form bleibt dabei unverändert, daher ändert sich die
     * <b>Masse</b> in der Regel.
     *
     * @param densityInKgProQM die neue Dichte des Objekts in <b>[kg/m^2]</b>
     */
    @API
    public final void setDensity(float densityInKgProQM) {
        physicsHandler.setDensity(densityInKgProQM);
    }

    /**
     * Gibt die aktuelle Dichte des Objekts an.
     *
     * @return Die aktuelle Dichte des Objekts in <b>[kg/m^2]</b>.
     */
    @API
    public final float getDensity() {
        return physicsHandler.getDensity();
    }

    /**
     * Setzt den Gravitationsfaktor, normalerweise im Bereich [0, 1].
     *
     * @param factor Gravitationsfaktor
     */
    @API
    public final void setGravityScale(float factor) {
        physicsHandler.setGravityScale(factor);
    }

    /**
     * Gibt den aktuellen Gravitationsfaktor des Objekts an.
     *
     * @return Gravitationsfaktor
     */
    @API
    public final float getGravityScale() {
        return physicsHandler.getGravityScale();
    }

    /**
     * Setzt den Reibungskoeffizient für das Objekt. Hat Einfluss auf
     * die Bewegung des Objekts.
     *
     * @param friction Der Reibungskoeffizient. In der Regel im Bereich <b>[0; 1]</b>.
     * @see #getFriction()
     */
    @API
    public final void setFriction(float friction) {
        physicsHandler.setFriction(friction);
    }

    /**
     * Gibt den Reibungskoeffizienten für dieses Objekt aus.
     *
     * @return Der Reibungskoeffizient des Objekts. Ist in der Regel (in der Realität)
     * ein Wert im Bereich <b>[0; 1]</b>.
     * @see #setFriction(float)
     */
    @API
    public final float getFriction() {
        return physicsHandler.getFriction();
    }

    /**
     * @param damping Dämpfung der Rotationsgeschwindigkeit
     */
    @API
    public final void setAngularDamping(float damping) {
        physicsHandler.setAngularDamping(damping);
    }

    /**
     * @return Dämpfung der Rotationsgeschwindigkeit
     */
    @API
    public final float getAngularDamping() {
        return physicsHandler.getAngularDamping();
    }

    /**
     * @param damping Dämpfung der Geschwindigkeit
     */
    @API
    public final void setLinearDamping(float damping) {
        physicsHandler.setLinearDamping(damping);
    }

    /**
     * @return Dämpfung der Geschwindigkeit
     */
    @API
    public final float getLinearDamping() {
        return physicsHandler.getLinearDamping();
    }

    /**
     * Setzt die Geschwindigkeit "hart" für dieses Objekt. Damit wird die aktuelle
     * Bewegung (nicht aber die Rotation) des Objekts ignoriert und hart auf den
     * übergebenen Wert gesetzt.
     *
     * @param velocityInMPerS Die Geschwindigkeit, mit der sich dieses Objekt ab sofort
     *                        bewegen soll. In <b>[m / s]</b>
     * @see #getVelocity()
     */
    @API
    public final void setVelocity(Vector velocityInMPerS) {
        if (velocityInMPerS.isNaN()) {
            return;
        }

        physicsHandler.setVelocity(velocityInMPerS);
    }

    /**
     * Gibt die Geschwindigkeit aus, mit der sich dieses Objekt gerade (also in diesem Frame) bewegt.
     *
     * @return Die Geschwindigkeit, mit der sich dieses Objekt gerade (also in diesem Frame) bewegt.
     * In <b>[m / s]</b>
     * @see #setVelocity(Vector)
     * @see #getAngularVelocity()
     */
    @API
    public final Vector getVelocity() {
        return physicsHandler.getVelocity();
    }

    /**
     * Gibt die aktuelle Drehgeschwindigkeit aus.
     *
     * @return Die aktuelle Drehgeschwindigkeit.
     * @see #setAngularVelocity(float)
     * @see #getVelocity()
     * @see #getAngularDamping()
     */
    @API
    public final float getAngularVelocity() {
        return physicsHandler.getAngularVelocity();
    }

    /**
     * Setzt die Drehgeschwindigkeit "hart" für dieses Objekt. Damit wird die aktuelle
     * Rotation des Objekts ignoriert und hart auf den übergebenen Wert gesetzt.
     *
     * @param rotationsPerSecond Die Geschwindigkeit, mit der sich dieses Objekt ab sofort
     *                           bewegen soll. In <b>[Umdrehnungen / s]</b>
     * @see #getAngularVelocity()
     * @see #setVelocity(Vector)
     * @see #setAngularDamping(float)
     */
    @API
    public final void setAngularVelocity(float rotationsPerSecond) {
        if (Float.isNaN(rotationsPerSecond)) {
            return;
        }

        physicsHandler.setAngularVelocity(rotationsPerSecond);
    }

    @API
    public final void setRestitution(float restitution) {
        if (Float.isNaN(restitution)) {
            return;
        }

        physicsHandler.setRestitution(restitution);
    }

    @API
    public final float getRestitution() {
        return physicsHandler.getRestitution();
    }

    /**
     * Wirkt ein Drehmoment auf das Objekt.
     *
     * @param torque Drehmoment, der auf das Ziel-Objekt wirken soll. In [N*m]
     */
    @API
    public final void applyTorque(float torque) {
        if (Float.isNaN(torque)) {
            return;
        }

        physicsHandler.applyTorque(torque);
    }

    /**
     * Wirkt eine Kraft auf den <i>Schwerpunkt</i> des Objekts.
     *
     * @param newton Kraftvektor in <b>[N]</b>
     */
    @API
    public final void applyForce(Vector newton) {
        if (newton.isNaN()) {
            return;
        }

        physicsHandler.applyForce(newton);
    }

    /**
     * Wirkt eine Kraft auf einem bestimmten <i>Punkt in der Welt</i>.
     *
     * @param newton      Kraft in <b>[N]</b>
     * @param globalPoint Ort auf der <i>Zeichenebene</i>, an dem die Kraft wirken soll.
     */
    @API
    public final void applyForce(Vector newton, Vector globalPoint) {
        if (newton.isNaN() || globalPoint.isNaN()) {
            return;
        }

        physicsHandler.applyForce(newton, globalPoint);
    }

    /**
     * Wirkt einen Impuls auf den <i>Schwerpunkt</i> des Objekts.
     *
     * @param newtonSeconds Impuls in <b>[Ns]</b>, der auf den Schwerpunkt wirken soll
     */
    @API
    public final void applyImpulse(Vector newtonSeconds) {
        if (newtonSeconds.isNaN()) {
            return; // ignore invalid impulses, they make box2d hang
        }

        physicsHandler.applyImpulse(newtonSeconds, physicsHandler.getCenter());
    }

    /**
     * Wirkt einen Impuls an einem bestimmten <i>Point in der Welt</i>.
     *
     * @param newtonSeconds Impuls in <b>[Ns]</b>
     * @param globalPoint   Ort auf der <i>Zeichenebene</i>, an dem der Impuls wirken soll
     */
    @API
    public final void applyImpulse(Vector newtonSeconds, Vector globalPoint) {
        physicsHandler.applyImpulse(newtonSeconds, globalPoint);
    }

    /**
     * Versetzt das Objekt - unabhängig von aktuellen Kräften und Geschwindigkeiten -
     * <i>in Ruhe</i>. Damit werden alle (physikalischen) Bewegungen des Objektes zurückgesetzt.
     * Sollte eine konstante <i>Schwerkraft</i> (oder etwas Vergleichbares) existieren, wo
     * wird dieses Objekt jedoch möglicherweise aus der Ruhelage wieder in Bewegung versetzt.
     */
    @API
    public final void resetMovement() {
        physicsHandler.resetMovement();
    }

    /**
     * Testet, ob das Objekt "steht". Diese Funktion ist below anderem hilfreich für die Entwicklung von Platformern
     * (z.B. wenn der Spieler nur springen können soll, wenn er auf dem Boden steht).<br>
     * Diese Funktion ist eine <b>Heuristik</b>, sprich sie ist eine Annäherung. In einer Physik-Simulation ist die
     * Definition von "stehen" nicht unbedingt einfach. Hier bedeutet es Folgendes:<br>
     * <i>Ein Objekt steht genau dann, wenn alle Eigenschaften erfüllt sind:</i>
     * <ul>
     * <li>Es ist ein <b>dynamisches Objekt</b>.</li>
     * <li>Direkt below der Mitte der minimalen
     * <a href="https://en.wikipedia.org/wiki/Minimum_bounding_box#Axis-aligned_minimum_bounding_box">AABB</a>,
     * die das gesamte Objekt umspannt, befindet sich ein <b>statisches Objekt</b>.</li>
     * </ul>
     *
     * @return {@code true}, falls das Objekt auf einem anderen Objekt steht, siehe Beschreibung.
     */
    @API
    public final boolean isGrounded() {
        return physicsHandler.isGrounded();
    }

    /* _________________________ JOINTS _________________________ */

    /**
     * Erstellt einen Revolute-Joint zwischen dem zugehörigen <code>Actor</code>-Objekt und einem weiteren.
     *
     * <h4>Definition Revolute-Joint</h4>
     * <p>Verbindet zwei <code>Actor</code>-Objekte <b>untrennbar an einem Anchor-Point</b>. Die Objekte können sich
     * ab sofort nur noch <b>relativ zueinander drehen</b>.</p>
     *
     * @param other          Das zweite <code>Actor</code>-Objekt, das ab sofort mit dem zugehörigen
     *                       <code>Actor</code>-Objekt
     *                       über einen <code>RevoluteJoint</code> verbunden sein soll.
     * @param relativeAnchor Der Ankerpunkt <b>relativ zu diesem Actor</b>. Es wird davon
     *                       ausgegangen, dass beide Objekte bereits korrekt positioniert sind.
     * @return Ein <code>Joint</code>-Objekt, mit dem der Joint weiter gesteuert werden kann.
     * @see org.jbox2d.dynamics.joints.RevoluteJoint
     */
    @API
    public final RevoluteJoint createRevoluteJoint(Actor other, Vector relativeAnchor) {
        return WorldHandler.createJoint(this, other, (world, a, b) -> {
            RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
            revoluteJointDef.initialize(a, b, getPosition().add(relativeAnchor).toVec2());
            revoluteJointDef.collideConnected = false;

            return (org.jbox2d.dynamics.joints.RevoluteJoint) world.createJoint(revoluteJointDef);
        }, new RevoluteJoint());
    }

    /**
     * Erstellt einen Rope-Joint zwischen diesem und einem weiteren <code>Actor</code>-Objekt.
     *
     * @param other               Das zweite <code>Actor</code>-Objekt, das ab sofort mit dem zugehörigen
     *                            <code>Actor</code>-Objekt
     *                            über einen <code>RopeJoint</code> verbunden sein soll.
     * @param relativeAnchor      Der Ankerpunkt für das zugehörige <code>Actor</code>-Objekt. Der erste
     *                            Befestigungspunkt
     *                            des Lassos. Angabe relativ zur Position vom zugehörigen Objekt.
     * @param relativeAnchorOther Der Ankerpunkt für das zweite <code>Actor</code>-Objekt, also <code>other</code>.
     *                            Der zweite Befestigungspunkt des Lassos. Angabe relativ zur Position vom zugehörigen
     *                            Objekt.
     * @param ropeLength          Die Länge des Lassos. Dies ist ab sofort die maximale Länge, die die beiden
     *                            Ankerpunkte
     *                            der Objekte voneinader entfernt sein können.
     * @return Ein <code>Joint</code>-Objekt, mit dem der Joint weiter gesteuert werden kann.
     * @see org.jbox2d.dynamics.joints.RopeJoint
     */
    @API
    public final RopeJoint createRopeJoint(Actor other, Vector relativeAnchor, Vector relativeAnchorOther, float ropeLength) {
        return WorldHandler.createJoint(this, other, (world, a, b) -> {
            RopeJointDef ropeJointDef = new RopeJointDef();
            ropeJointDef.bodyA = a;
            ropeJointDef.bodyB = b;
            ropeJointDef.localAnchorA.set(relativeAnchor.toVec2());
            ropeJointDef.localAnchorB.set(relativeAnchorOther.toVec2());
            ropeJointDef.collideConnected = true;
            ropeJointDef.maxLength = ropeLength;

            return (org.jbox2d.dynamics.joints.RopeJoint) world.createJoint(ropeJointDef);
        }, new RopeJoint());
    }

    /**
     * Erstellt einen neuen {@link PrismaticJoint} zwischen zwei Objekten.
     *
     * @param other     Objekt, das mit dem aktuellen verbunden werden soll
     * @param anchor    Verbindungspunkt
     * @param axisAngle Winkel
     * @return Objekt für die weitere Steuerung des Joints
     */
    @API
    public final PrismaticJoint createPrismaticJoint(Actor other, Vector anchor, float axisAngle) {
        return WorldHandler.createJoint(this, other, (world, a, b) -> {
            double angleInRadians = Math.toRadians(axisAngle);

            PrismaticJointDef prismaticJointDef = new PrismaticJointDef();
            prismaticJointDef.initialize(a, b, getPosition().add(anchor).toVec2(), new Vec2((float) Math.cos(angleInRadians), (float) Math.sin(angleInRadians)));
            prismaticJointDef.collideConnected = false;

            return (org.jbox2d.dynamics.joints.PrismaticJoint) world.createJoint(prismaticJointDef);
        }, new PrismaticJoint());
    }

    /**
     * Erstellt einen Distance-Joint zwischen diesem und einem weiteren <code>Actor</code>-Objekt.
     *
     * @param other                 Das zweite <code>Actor</code>-Objekt, das ab sofort mit dem zugehörigen
     *                              <code>Actor</code>-Objekt
     *                              über einen <code>DistanceJoint</code> verbunden sein soll.
     * @param anchorRelativeToThis  Der Ankerpunkt für das zugehörige <code>Actor</code>-Objekt. Der erste
     *                              Befestigungspunkt
     *                              des Joints. Angabe relativ zu <code>this</code> also absolut.
     * @param anchorRelativeToOther Der Ankerpunkt für das zweite <code>Actor</code>-Objekt, also <code>other</code>.
     *                              Der zweite Befestigungspunkt des Joints. Angabe relativ zu <code>other</code>
     * @return Ein <code>Joint</code>-Objekt, mit dem der Joint weiter gesteuert werden kann.
     * @see org.jbox2d.dynamics.joints.DistanceJoint
     */
    @API
    public final DistanceJoint createDistanceJoint(Actor other, Vector anchorRelativeToThis, Vector anchorRelativeToOther) {
        return WorldHandler.createJoint(this, other, (world, a, b) -> {
            DistanceJointDef distanceJointDef = new DistanceJointDef();

            distanceJointDef.bodyA = a;
            distanceJointDef.bodyB = b;
            distanceJointDef.localAnchorA.set(anchorRelativeToThis.toVec2());
            distanceJointDef.localAnchorB.set(anchorRelativeToOther.toVec2());
            Vector distanceBetweenBothActors = (this.getPosition().add(anchorRelativeToThis)).getDistance(other.getPosition().add(anchorRelativeToOther));
            distanceJointDef.length = distanceBetweenBothActors.getLength();

            return (org.jbox2d.dynamics.joints.DistanceJoint) world.createJoint(distanceJointDef);
        }, new DistanceJoint());
    }

    /**
     * Erstellt einen Weld-Joint zwischen diesem und einem weiteren <code>Actor</code>-Objekt.
     *
     * @param other                 Das zweite <code>Actor</code>-Objekt, das ab sofort mit dem zugehörigen
     *                              <code>Actor</code>-Objekt
     *                              über einen <code>DistanceJoint</code> verbunden sein soll.
     * @param anchorRelativeToThis  Der Ankerpunkt für das zugehörige <code>Actor</code>-Objekt. Der erste
     *                              Befestigungspunkt
     *                              des Joints. Angabe relativ zu <code>this</code> also absolut.
     * @param anchorRelativeToOther Der Ankerpunkt für das zweite <code>Actor</code>-Objekt, also <code>other</code>.
     *                              Der zweite Befestigungspunkt des Joints. Angabe relativ zu <code>other</code>
     * @return Ein <code>Joint</code>-Objekt, mit dem der Joint weiter gesteuert werden kann.
     * @see org.jbox2d.dynamics.joints.DistanceJoint
     */
    @API
    public final WeldJoint createWeldJoint(Actor other, Vector anchorRelativeToThis, Vector anchorRelativeToOther) {
        return WorldHandler.createJoint(this, other, (world, a, b) -> {
            WeldJointDef weldJointDef = new WeldJointDef();

            weldJointDef.bodyA = a;
            weldJointDef.bodyB = b;
            weldJointDef.localAnchorA.set(anchorRelativeToThis.toVec2());
            weldJointDef.localAnchorB.set(anchorRelativeToOther.toVec2());

            return (org.jbox2d.dynamics.joints.WeldJoint) world.createJoint(weldJointDef);
        }, new WeldJoint());
    }

    /**
     * Setzt die Position des <code>Actor</code>-Objektes gänzlich neu auf der Zeichenebene. Das Setzen ist technisch
     * gesehen eine Verschiebung von der aktuellen Position an die neue.
     *
     * @param x neue <code>getX</code>-Koordinate
     * @param y neue <code>getY</code>-Koordinate
     * @see #setPosition(Vector)
     * @see #setCenter(float, float)
     * @see #setX(float)
     * @see #setY(float)
     */
    @API
    public final void setPosition(float x, float y) {
        this.setPosition(new Vector(x, y));
    }

    /**
     * Setzt die Position des Objektes gänzlich neu auf der Zeichenebene. Das Setzen ist technisch
     * gesehen eine Verschiebung von der aktuellen Position an die neue.
     *
     * @param position Der neue Zielpunkt
     * @see #setPosition(float, float)
     * @see #setCenter(float, float)
     * @see #setX(float)
     * @see #setY(float)
     */
    @API
    public final void setPosition(Vector position) {
        this.moveBy(position.subtract(getPosition()));
    }

    /**
     * Verschiebt das Objekt ohne Bedingungen auf der Zeichenebene.
     *
     * @param v Der Vector, der die Verschiebung des Objekts angibt.
     * @see Vector
     * @see #moveBy(float, float)
     */
    @API
    public final void moveBy(Vector v) {
        physicsHandler.moveBy(v);
    }

    /**
     * Verschiebt die Actor-Figur so, dass ihr Mittelpunkt die eingegebenen Koordinaten hat.
     * <p>
     * Diese Methode arbeitet nach dem Mittelpunkt des das Objekt abdeckenden BoundingRechtecks
     * durch den Aufruf der Methode <code>center()</code>. Daher ist diese Methode in der Anwendung
     * auf ein ActorGroup-Objekt nicht unbedingt sinnvoll.
     *
     * @param x Die <code>getX</code>-Koordinate des neuen Mittelpunktes des Objektes
     * @param y Die <code>getY</code>-Koordinate des neuen Mittelpunktes des Objektes
     * @see #setCenter(Vector)
     * @see #moveBy(float, float)
     * @see #moveBy(Vector)
     * @see #setPosition(float, float)
     * @see #setPosition(Vector)
     * @see #getCenter()
     */
    @API
    public final void setCenter(float x, float y) {
        this.setCenter(new Vector(x, y));
    }

    /**
     * Verschiebt die Actor-Figur so, dass ihr Mittelpunkt die eingegebenen Koordinaten hat.<br>
     * Diese Methode arbeitet mit dem Mittelpunkt des das Objekt abdeckenden Bounding-Rechtecks
     * durch den Aufruf der Methode <code>getCenter()</code>.
     *
     * @param center Der neue Mittelpunkt des Objekts
     * @see #setCenter(float, float)
     * @see #moveBy(float, float)
     * @see #moveBy(Vector)
     * @see #setPosition(float, float)
     * @see #setPosition(Vector)
     * @see #getCenter()
     */
    @API
    public final void setCenter(Vector center) {
        this.moveBy(this.getCenter().negate().add(center));
    }

    /**
     * Gibt die x-Koordinate der linken oberen Ecke zurück. Sollte das Raumobjekt nicht rechteckig
     * sein, so wird die Position der linken oberen Ecke des umschließenden Rechtecks genommen.
     *
     * @return <code>getX</code>-Koordinate
     * @see #getY()
     * @see #getPosition()
     */
    @API
    public final float getX() {
        return this.getPosition().getX();
    }

    /**
     * Setzt die x-Koordinate der Position des Objektes gänzlich neu auf der Zeichenebene. Das
     * Setzen ist technisch gesehen eine Verschiebung von der aktuellen Position an die neue.
     *
     * @param x neue <code>getX</code>-Koordinate
     * @see #setPosition(float, float)
     * @see #setCenter(float, float)
     * @see #setY(float)
     */
    @API
    public final void setX(float x) {
        this.moveBy(x - getX(), 0);
    }

    /**
     * Gibt die y-Koordinate der linken unteren Ecke zurück. Sollte das Raumobjekt nicht rechteckig
     * sein, so wird die Position der linken unteren Ecke des umschließenden Rechtecks genommen.
     *
     * @return <code>getY</code>-Koordinate
     * @see #getX()
     * @see #getPosition()
     */
    @API
    public final float getY() {
        return this.getPosition().getY();
    }

    /**
     * Setzt die y-Koordinate der Position des Objektes gänzlich neu auf der Zeichenebene. Das
     * Setzen ist technisch gesehen eine Verschiebung von der aktuellen Position an die neue. <br>
     * <br> <b>Achtung!</b><br> Bei <b>allen</b> Objekten ist die eingegebene Position die
     * linke, untere Ecke des Rechtecks, das die Figur optimal umfasst. Das heißt, dass dies bei
     * Kreisen z.B. <b>nicht</b> der Mittelpunkt ist! Hierfür gibt es die Sondermethode
     * {@link #setCenter(float, float)}.
     *
     * @param y neue <code>getY</code>-Koordinate
     * @see #setPosition(float, float)
     * @see #setCenter(float, float)
     * @see #setX(float)
     */
    @API
    public final void setY(float y) {
        this.moveBy(0, y - getY());
    }

    /**
     * Gibt den Mittelpunkt des Objektes in der Scene aus.
     *
     * @return Die Koordinaten des Mittelpunktes des Objektes
     * @see #getPosition()
     */
    @API
    public final Vector getCenter() {
        return physicsHandler.getCenter();
    }

    @API
    public final Vector getCenterRelative() {
        return getCenter().subtract(getPosition());
    }

    /**
     * Verschiebt das Objekt.<br> Hierbei wird nichts anderes gemacht, als <code>move(new
     * Vector(dx, dy))</code> auszuführen. Insofern ist diese Methode dafür gut, sich nicht mit
     * der Klasse Vector auseinandersetzen zu müssen.
     *
     * @param dX Die Verschiebung in Richtung X
     * @param dY Die Verschiebung in Richtung Y
     * @see #moveBy(Vector)
     */
    @API
    public final void moveBy(float dX, float dY) {
        this.moveBy(new Vector(dX, dY));
    }

    /**
     * Gibt die Position dieses Actor-Objekts aus.
     *
     * @return die aktuelle Position dieses <code>Actor</code>-Objekts.
     */
    @API
    public final Vector getPosition() {
        return physicsHandler.getPosition();
    }

    /**
     * Rotiert das Objekt.
     *
     * @param degree Der Winkel (in <b>Grad</b>), um den das Objekt rotiert werden soll.
     */
    @API
    public final void rotateBy(float degree) {
        physicsHandler.rotateBy(degree);
    }

    /**
     * Gibt den Winkel aus, um den das Objekt derzeit rotiert ist.
     *
     * @return Der Winkel (in <b>Grad</b>), um den das Objekt derzeit rotiert ist. Jedes Objekt ist bei
     * Initialisierung nicht rotiert (<code>getRotation()</code> gibt direkt ab Initialisierung
     * <code>0</code> zurück).
     */
    @API
    public final float getRotation() {
        return physicsHandler.getRotation();
    }

    /**
     * Setzt den Rotationswert des Objekts.
     *
     * @param degree Der Winkel (in <b>Grad</b>), um den das Objekt <b>von seiner Ausgangsposition bei
     *               Initialisierung</b> rotiert werden soll.
     */
    @API
    public final void setRotation(float degree) {
        physicsHandler.setRotation(degree);
    }

    @API
    public final boolean isMounted() {
        return getLayer() != null;
    }

    /**
     * Setzt den BodyType auf PARTICLE und animiert das Partikel, sodass es ausblasst und nach der Lebenszeit komplett
     * verschwindet.
     *
     * @param lifetime Lebenszeit in Sekunden
     * @return Objekt, das die Animation kontrolliert
     */
    @API
    public final ValueAnimator<Float> animateParticle(float lifetime) {
        setBodyType(BodyType.PARTICLE);

        setOpacity(1);
        ValueAnimator<Float> animator = animateOpacity(lifetime, 0);
        animator.addCompletionListener(value -> remove());

        return animator;
    }

    /**
     * Animiert die Opacity dieses Actors über einen festen Zeitraum: Beginnend von der aktuellen Opacity, ändert sie
     * sich "smooth" (mit {@code EaseInOutFloat}-Interpolation) vom aktuellen Opacity-Wert (die Ausgabe von
     * {@code getOpacity()}) bis hin zum angegebenen Opacity-Wert.
     *
     * @param time           Die Animationszeit in Sekunden
     * @param toOpacityValue Der Opacity-Wert, zu dem innerhalb von {@code time} zu interpolieren ist.
     * @return Ein {@code ValueAnimator}, der diese Animation ausführt. Der Animator ist bereits aktiv, es muss nichts
     * an dem Objekt getan werden, um die Animation auszuführen.
     * @see ea.animation.interpolation.EaseInOutFloat
     */
    @API
    public final ValueAnimator<Float> animateOpacity(float time, float toOpacityValue) {
        ValueAnimator<Float> animator = new ValueAnimator<>(time, this::setOpacity, new EaseInOutFloat(getOpacity(), toOpacityValue), this);
        addFrameUpdateListener(animator);

        return animator;
    }

    @Internal
    static void assertPositiveWidthAndHeight(float width, float height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Breite und Höhe müssen größer 0 sein! " + width + " / " + height);
        }
    }
}
