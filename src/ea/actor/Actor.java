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

import ea.BoundingRechteck;
import ea.Game;
import ea.Scene;
import ea.Vector;
import ea.collision.CollisionListener;
import ea.handle.Physics;
import ea.handle.Position;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.internal.physics.*;
import ea.internal.util.Logger;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
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
     * Szene, zu der der Actor gehört.
     */
    private Scene scene;

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
    private int layer = 1;

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

    private final Collection<Runnable> destructionListeners = new CopyOnWriteArrayList<>();

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

    /**
     * Setzt die Scene für diesen Actor.
     *
     * @param scene Die neu anzumeldende Scene. Ist der Wert <code>null</code>, wird der zugehörige Body zerstört.
     *              Ist der Wert eine Scene, so wird die Scene
     */
    @Internal
    public void setScene(Scene scene) {
        //TODO
        ProxyData proxyData = physicsHandler.getProxyData();
        if (scene == null) {
            //Von Scene entfernen
            scene.getWorldHandler().removeAllInternalReferences(physicsHandler.getBody());
            scene.getWorldHandler().getWorld().destroyBody(getPhysicsHandler().getBody());
            this.scene = null;
            this.physicsHandler = new NullHandler(this, proxyData);
        } else {
            //An Scene anmelden
            if (this.scene != null) {
                throw new IllegalStateException("Kann einen Actor nicht an mehr als einer Scene angemeldet haben.");
            }
            //Neue Scene = neuer Handler
            physicsHandler = new BodyHandler(this, proxyData, scene.getWorldHandler());
            this.scene = scene;
        }
    }

    @API
    public boolean isAlive() {
        return alive;
    }

    @API
    public final void addDestructionListener(Runnable listener) {
        destructionListeners.add(listener);
    }

    /* _________________________ Getter & Setter (die sonst nicht zuordbar) _________________________ */

    /**
     * Setzt den Layer dieses Actors. Je größer, desto weiter vorne wird ein Actor gezeichnet.
     * <b>Diese Methode muss ausgeführt werden, bevor der Actor zu einer ActorGroup hinzugefügt
     * wird.</b>
     *
     * @param layer Layer-Index
     */
    @API
    public void setLayer(int layer) {
        this.layer = layer;
    }

    /**
     * Gibt den Layer zurück.
     *
     * @return Layer-Index
     */
    @API
    public int getLayer() {
        return this.layer;
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
     * @param another Ein weiteres Actor-Objekt.
     *
     * @return <code>true</code>, wenn dieses Actor-Objekt sich mit <code>another</code> schneidet. Sonst
     * <code>false</code>.
     *
     * @see ea.Game#setDebug(boolean)
     */
    @API
    public final boolean overlaps(Actor another) {
        return WorldHandler.bodyCollisionCheckup(physicsHandler.getBody(), another.getPhysicsHandler().getBody());
    }

    /* _________________________ Utilities, interne & überschriebene Methoden _________________________ */

    /**
     * Setzt, was für eine Type physikalisches Objekt das Objekt sein soll. Erläuterung findet
     * sich im <code>enum Type</code>.
     *
     * @param type Der Type Physics-Objekt, der ab sofort dieses Objekt sein soll.
     *
     * @see Physics.Type
     */
    @API
    public void setBodyType(Physics.Type type) {
        this.physicsHandler.setType(type);

        int category = 0;
        switch (type) {
            case STATIC:
                category = WorldHandler.CATEGORY_PASSIVE;
                break;
            case DYNAMIC:
            case KINEMATIC:
                category = WorldHandler.CATEGORY_DYNAMIC_OR_KINEMATIC;
                break;
        }

        this.physicsHandler.getBody().m_fixtureList.m_filter.categoryBits = category;
    }

    /**
     * Gibt aus, was für ein Type Physics-Objekt dieses Objekt momentan ist.
     *
     * @return der Type Physics-Objekt, der das entsprechende <code>Actor</code>-Objekt momentan ist.
     *
     * @see Physics.Type
     */
    @API
    public Physics.Type getBodyType() {
        return physicsHandler.getType();
    }

    /**
     * Die Basiszeichenmethode.<br> Sie schließt eine Fallabfrage zur Sichtbarkeit ein. Diese Methode wird bei den
     * einzelnen Gliedern eines Knotens aufgerufen.
     *
     * @param g Das zeichnende Graphics-Objekt
     * @param r Das BoundingRechteck, dass die Kameraperspektive Repraesentiert.<br> Hierbei soll zunaechst getestet
     *          werden, ob das Objekt innerhalb der Kamera liegt, und erst dann gezeichnet werden.
     */
    @Internal
    public void renderBasic(Graphics2D g, BoundingRechteck r) {
        if (visible && this.isWithinBounds(r)) {

            //Hole Rotation und Position absolut auf der Zeichenebene.
            float rotation;
            Vector position;

            synchronized (physicsHandler.getBody()) {
                rotation = physicsHandler.getRotation();
                position = physicsHandler.getPosition();
            }

            // ____ Pre-Render ____

            AffineTransform transform = g.getTransform();

            g.rotate(-rotation, position.x, -position.y); // TODO ist das die korrekte Rotation, Ursprung als Zentrum?
            g.translate(position.x, -position.y);

            //Opacity Update
            if (opacity != 1) {
                composite = g.getComposite();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, opacity));
            } else {
                composite = null;
            }

            // ____ Render ____

            render(g);

            if (Game.isDebug()) {
                synchronized (physicsHandler) {
                    // Visualisiere die Shape
                    if (physicsHandler.getBody().m_fixtureList != null) {
                        g.setColor(Color.YELLOW);
                        g.fillRect(-2, -2, 4, 4);
                        g.setColor(Color.red);
                        renderShape(physicsHandler.getBody().m_fixtureList.m_shape, g);
                    }
                }
            }

            // ____ Post-Render ____

            // Opacity Update
            if (composite != null) {
                g.setComposite(composite);
            }

            // Transform zurücksetzen
            g.setTransform(transform);

            // System.out.println("R: " + getPosition + " - " + getRotation);
        }
    }

    /**
     * Rendert eine Shape von JBox2D vectorFromThisTo den gegebenen Voreinstellungen im Graphics-Objekt.
     *
     * @param shape         Die Shape, die zu rendern ist.
     * @param g             Das Graphics2D-Object, das die Shape rendern soll. Farbe &amp; Co. sollte im Vorfeld
     *                      eingestellt sein. Diese Methode übernimmt nur das direkte rendern.
     */
    @Internal
    public static void renderShape(Shape shape, Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        if (shape instanceof PolygonShape) {
            PolygonShape polygonShape = (PolygonShape) shape;
            Vec2[] vec2s = polygonShape.getVertices();
            int[] xs = new int[polygonShape.getVertexCount()], ys = new int[polygonShape.getVertexCount()];
            for (int i = 0; i < xs.length; i++) {
                xs[i] = (int) (vec2s[i].x);
                ys[i] = (-1) * (int) (vec2s[i].y);
            }
            g.drawPolygon(xs, ys, xs.length);
        } else if (shape instanceof CircleShape) {
            int diameter = (int) (((CircleShape) shape).m_radius * 2);
            g.drawOval(0, -diameter, diameter, diameter);
        } else {
            Logger.error("Debug/Render", "Konnte die Shape (" + shape + ") nicht rendern. Unerwartete Shape.");
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
    private boolean isWithinBounds(BoundingRechteck r) {
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
    public <E extends Actor> void addCollisionListener(CollisionListener<E> listener, E collider) {
        WorldHandler.spezifischesKollisionsReagierbarEingliedern(listener, this, collider);
    }

    /**
     * Meldet einen neuen {@link CollisionListener} an, der auf alle Kollisionen reagiert, die dieser Actor mit seiner
     * Umwelt erlebt.
     *
     * @param listener Der Listener, der bei Kollisionen informiert werden soll, die der  <b>ausführende Actor</b> mit
     *                 allen anderen Objekten der Scene erlebt.
     *
     * @see #addCollisionListener(CollisionListener, Actor)
     */
    @API
    public void addCollisionListener(CollisionListener<Actor> listener) {
        WorldHandler.allgemeinesKollisionsReagierbarEingliedern(listener, this);
    }

    /* _________________________ Kontrakt: Abstrakte Methoden/Funktionen eines Actor-Objekts _________________________ */

    /**
     * Rendert das Objekt am Ursprung. <ul> <li>Die Position ist (0|0).</li> <li>Die Roation ist 0.</li> </ul>
     *
     * @param g Das zeichnende Graphics-Objekt
     */
    @Internal
    public abstract void render(Graphics2D g);

    /**
     * Gibt die Scene aus, zu der dieser Actor gehört.
     *
     * @return Das Scene-Objekt, zu dem dieser Actor gehört.
     */
    public Scene getScene() {
        return scene;
    }
}