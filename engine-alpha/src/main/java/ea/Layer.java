package ea;

import ea.actor.Actor;
import ea.event.*;
import ea.internal.Bounds;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.internal.physics.*;
import org.jbox2d.dynamics.Body;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Layer bieten die Möglichkeit, <code>Actors</code> vor und hinter der Zeichenebene mit zusätzlichen Eigenschaften
 * (wie Parallaxe) zu rendern.
 *
 * @author Michael Andonie
 */
public class Layer implements KeyListenerContainer, MouseClickListenerContainer, MouseWheelListenerContainer, FrameUpdateListenerContainer {
    private static final Comparator<? super Actor> ACTOR_COMPARATOR = Comparator.comparingInt(Actor::getLayerPosition);

    private <T> Supplier<T> createParentSupplier(Function<Scene, T> supplier) {
        return () -> {
            Scene scene = getParent();
            if (scene == null) {
                return null;
            }

            return supplier.apply(scene);
        };
    }

    private final List<Actor> actors;

    private float parallaxX = 1;
    private float parallaxY = 1;
    private float parallaxRotation = 1;
    private float parallaxZoom = 1;
    private float timeDistort = 1;

    /**
     * Bestimmt die Reihenfolge der Layer, kleinere Werte werden zuerst gerendert, sind also weiter "hinten"
     */
    private int layerPosition = -2;

    private boolean visible = true;

    private Scene parent;

    private final WorldHandler worldHandler;

    private final EventListeners<KeyListener> keyListeners = new EventListeners<>(createParentSupplier(Scene::getKeyListeners));
    private final EventListeners<MouseClickListener> mouseClickListeners = new EventListeners<>(createParentSupplier(Scene::getMouseClickListeners));
    private final EventListeners<MouseWheelListener> mouseWheelListeners = new EventListeners<>(createParentSupplier(Scene::getMouseWheelListeners));
    private final EventListeners<FrameUpdateListener> frameUpdateListeners = new EventListeners<>();

    /**
     * Erstellt ein neues Layer.
     */
    @API
    public Layer() {
        worldHandler = new WorldHandler(this);
        actors = new ArrayList<>();
        EventListenerHelper.autoRegisterListeners(this);
    }

    public Scene getParent() {
        return parent;
    }

    @Internal
    void setParent(Scene parent) {
        if (parent != null && this.parent != null) {
            throw new IllegalStateException("Das Layer wurde bereits an einer Scene angemeldet.");
        }

        if (parent != null) {
            keyListeners.invoke(parent::addKeyListener);
            mouseClickListeners.invoke(parent::addMouseClickListener);
            mouseWheelListeners.invoke(parent::addMouseWheelListener);
            frameUpdateListeners.invoke(parent::addFrameUpdateListener);
        } else {
            keyListeners.invoke(this.parent::removeKeyListener);
            mouseClickListeners.invoke(this.parent::removeMouseClickListener);
            mouseWheelListeners.invoke(this.parent::removeMouseWheelListener);
            frameUpdateListeners.invoke(this.parent::removeFrameUpdateListener);
        }

        this.parent = parent;
    }

    /**
     * Setzt die Position dieses Layers relativ zu anderen Layers.
     *
     * @param position Die neue Position dieses Layers. Je höher dieser Wert, desto weiter vorne ist das Layer.
     */
    @API
    public void setLayerPosition(int position) {
        this.layerPosition = position;

        if (parent != null) {
            parent.sortLayers();
        }
    }

    /**
     * Gibt die Position des Layers aus.
     *
     * @return Der Wert, der die Position dieses Layers repräsentiert.
     *
     * @see #setLayerPosition(int)
     */
    @API
    public int getLayerPosition() {
        return layerPosition;
    }

    /**
     * Setzt den Parallaxenwert der Bewegung für dieses Layer:
     * <ul>
     * <li><code>1</code> ist keine Parallaxe (Bewegung exakt mit der Kamera)</li>
     * <li>Werte zwischen <code>0</code> und <code>1</code> schaffen einen entfernten Effekt: Die Bewegung ist
     * weniger als die der Kamera</li>
     * <li><code>0</code> bedeutet, die Bewegung der Kamera hat gar keinen Einfluss auf das Layer.</li>
     * <li>Negative Werte sorgen für Bewegung entgegen der Kamera</li>
     * <li>Werte <code>&gt; 1</code> verstärken die Bewegung der Kamera (z.B. für Vordergrund). </li>
     * </ul>
     *
     * @param parallaxX Der X-Parallaxenwert.
     * @param parallaxY Der Y-Parallaxenwert.
     */
    @API
    public void setParallaxPosition(float parallaxX, float parallaxY) {
        this.parallaxX = parallaxX;
        this.parallaxY = parallaxY;
    }

    /**
     * Setzt den Parallaxenwert beim Zoom für dieses Layer:
     * <ul>
     * <li><code>1</code>: Normaler Zoom mit der Kamera</li>
     * <li><code>0</code>: Kamerazoom hat keinen Einfluss auf dieses Layer.</li>
     * <li><code>0 &lt; parallaxZoom &lt; 1</code>: Der Zoomeffekt tritt schwächer auf.</li>
     * <li><code>parallaxZoom &gt; 1</code>: Der Zoomeffekt tritt stärker auf. </li>
     * <li><code>parallaxZoom &lt; 0</code>: Der Zoomeffekt tritt betragsmäßig ähnlich und umgekehrt auf.</li>
     * </ul>
     */
    @API
    public void setParallaxZoom(float parallaxZoom) {
        this.parallaxZoom = parallaxZoom;
    }

    /**
     * Setzt die Parallaxe der Rotation. Dieses Layer wird um <code>[kamerarotation] * parallaxRotation</code> rotiert.
     *
     * @param parallaxRotation Die Rotationsparallaxe.
     */
    @API
    public void setParallaxRotation(float parallaxRotation) {
        this.parallaxRotation = parallaxRotation;
    }

    /**
     * Setzt einen Zeitverzerrungsfaktor. Die Zeit in der Physiksimulation vergeht standardmäßig in Echtzeit, kann
     * allerdings verzerrt werden.
     *
     * @param timeDistort <i>Zeit in der Simulation = Echtzeit * Verzerrungsfaktor</i> <br />
     *                    <ul>
     *                    <li>Werte <code>&gt;1</code> lassen die Zeit <b>schneller</b> vergehen</li>
     *                    <li>Werte <code>&lt;1</code> lassen die Zeit <b>langsamer</b> vergehen</li>
     *                    <li><code>1</code> lässt die Zeit in Echtzeit vergehen</li>
     *                    <li>Werte <code>&lt;=0</code> sind nicht erlaubt</li>
     *                    </ul>
     */
    @API
    public void setTimeDistort(float timeDistort) {
        if (timeDistort < 0) {
            throw new IllegalArgumentException("Zeitverzerrungsfaktor muss größer oder gleich 0 sein, war " + timeDistort);
        }

        this.timeDistort = timeDistort;
    }

    /**
     * Setzt die Schwerkraft, die auf <b>alle Objekte innerhalb des Hauptlayers der Scene</b> wirkt.
     *
     * @param gravityInNewton Die neue Schwerkraft als Vector. Die Einheit ist <b>[N]</b>.
     */
    @API
    public void setGravity(Vector gravityInNewton) {
        this.worldHandler.getWorld().setGravity(gravityInNewton.toVec2());
    }

    /**
     * Setzt, ob dieses Layer sichtbar sein soll.
     *
     * @param visible <code>true</code>: Das Layer ist sichtbar, wenn es an einer Szene angemeldet ist.
     *                <code>false</code>: Das Layer ist auch dann nicht sichtbar, wenn es an einer Szene angemeldet
     *                ist.
     *
     * @see #isVisible()
     */
    @API
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Gibt an, ob dieses Layer gerade sichtbar ist.
     *
     * @return <code>true</code>: Das Layer ist sichtbar.
     * <code>false</code>: Das Layer ist nicht sichtbar.
     *
     * @see #setVisible(boolean)
     */
    @API
    public boolean isVisible() {
        return this.visible;
    }

    @API
    public void add(Actor... actors) {
        defer(() -> {
            for (Actor actor : actors) {
                if (actor.isMounted()) {
                    if (actor.getLayer() != this) {
                        throw new IllegalArgumentException("Ein Actor kann nur an einem Layer gleichzeitig angemeldet sein");
                    } else {
                        return;
                    }
                }

                PhysicsHandler oldHandler = actor.getPhysicsHandler();
                PhysicsHandler newHandler = new BodyHandler(actor, oldHandler.getPhysicsData(), worldHandler);
                actor.setPhysicsHandler(newHandler);
                oldHandler.applyMountCallbacks(newHandler);

                this.actors.add(actor);
            }

            this.actors.sort(ACTOR_COMPARATOR);
        });
    }

    @API
    final public void remove(Actor... actors) {
        defer(() -> {
            for (Actor actor : actors) {
                this.actors.remove(actor);

                PhysicsData physicsData = actor.getPhysicsHandler().getPhysicsData();
                PhysicsHandler physicsHandler = actor.getPhysicsHandler();
                if (physicsHandler.getWorldHandler() == null) {
                    return;
                }

                Body body = physicsHandler.getBody();
                worldHandler.removeAllInternalReferences(body);
                worldHandler.getWorld().destroyBody(body);
                actor.setPhysicsHandler(new NullHandler(physicsData));
            }
        });
    }

    /**
     * Übersetzt einen Punkt auf diesem Layer zu der analogen, aktuellen Pixelkoordinate im zeichnenden Frame.
     *
     * @param worldPoint Ein Punkt auf dem Layer
     *
     * @return Ein Vektor <b>in Pixelkoordinaten</b> (nicht Meter, Y-Achse ist umgekehrt), der mit der
     * aktuellen Kameraeinstellung dem angegebenen <code>worldPoint</code> entspricht
     */
    @Internal
    public Vector translateWorldPointToFramePxCoordinates(Vector worldPoint) {
        float pixelPerMeter = calculatePixelPerMeter();
        Vector frameSize = Game.getFrameSizeInPixels();
        Vector cameraPositionInPx = new Vector(frameSize.getX() / 2, frameSize.getY() / 2);
        Vector fromCamToPointInWorld = parent.getCamera().getPosition().multiplyX(parallaxX).multiplyY(parallaxY).getDistance(worldPoint);
        return cameraPositionInPx.add(fromCamToPointInWorld.multiplyY(-1).multiply(pixelPerMeter * parallaxZoom));
    }

    /**
     * Gibt die derzeit auf dem Bildschirm sichtbare Fläche des Layers an.
     *
     * @return Die sichtbare Fläche als Bounds Objekt <b>mit Angaben in Meter</b>
     *
     * @see Game#getFrameSizeInPixels()
     */
    @API
    public Bounds getVisibleArea(Vector gameSizeInPixels) {
        Vector center = parent.getCamera().getPosition();
        float pixelPerMeter = calculatePixelPerMeter();

        return new Bounds(0, 0, gameSizeInPixels.getX() / pixelPerMeter, gameSizeInPixels.getY() / pixelPerMeter) //
                .withCenterPoint(center);
    }

    /**
     * Setzt den Kamerazoom exakt, sodass die sichtbare Breite des sichtbaren Fensters einer bestimmten Länge
     * entspricht.
     *
     * @param width Die Breite in Meter, auf die die Kamera im Fenster exakt zu setzen ist.
     *
     * @see #setVisibleHeight(float, Vector)
     * @see Game#getFrameSizeInPixels()
     */
    @API
    public void setVisibleWidth(float width, Vector gameSizeInPixels) {
        float desiredPixelPerMeter = gameSizeInPixels.getX() / width;
        float desiredZoom = 1 + ((desiredPixelPerMeter - 1) / parallaxZoom);

        parent.getCamera().setZoom(desiredZoom);
    }

    /**
     * Setzt den Kamerazoom exakt, sodass die sichtbare Höhe des sichtbaren Fensters einer bestimmten Länge
     * entspricht.
     *
     * @param height Die Höhe in Meter, auf die die Kamera im Fenster exakt zu setzen ist.
     *
     * @see #setVisibleWidth(float, Vector)
     * @see Game#getFrameSizeInPixels()
     */
    @API
    public void setVisibleHeight(float height, Vector gameSizeInPixels) {
        float desiredPixelPerMeter = gameSizeInPixels.getY() / height;
        float desiredZoom = 1 + ((desiredPixelPerMeter - 1) / parallaxZoom);

        parent.getCamera().setZoom(desiredZoom);
    }

    @API
    public float calculatePixelPerMeter() {
        return 1 + (parent.getCamera().getZoom() - 1) * parallaxZoom;
    }

    @Internal
    public void render(Graphics2D g, Camera camera, int width, int height) {
        if (!visible) {
            return;
        }

        Vector position = camera.getPosition();
        float rotation = -camera.getRotation();
        g.setClip(0, 0, width, height);
        g.translate(width / 2, height / 2);

        float pixelPerMeter = calculatePixelPerMeter();

        g.rotate(Math.toRadians(rotation) * parallaxRotation, 0, 0);
        g.translate((-position.getX() * parallaxX) * pixelPerMeter, (position.getY() * parallaxY) * pixelPerMeter);

        // TODO: Calculate optimal bounds
        int size = Math.max(width, height);

        boolean needsSort = false;
        int previousPosition = Integer.MIN_VALUE;

        for (Actor actor : actors) {
            actor.renderBasic(g, new Bounds(position.getX() - size, position.getY() - size, size * 2, size * 2), pixelPerMeter);

            if (!needsSort) {
                int actorPosition = actor.getLayerPosition();
                if (actorPosition < previousPosition) {
                    needsSort = true;
                }

                previousPosition = actorPosition;
            }
        }

        if (needsSort) {
            this.actors.sort(ACTOR_COMPARATOR);
        }
    }

    /**
     * Gibt den Worldhandler dieses Layers aus.
     *
     * @return Der Worldhandler dieser Ebene.
     */
    @Internal
    public WorldHandler getWorldHandler() {
        return worldHandler;
    }

    @Internal
    public void step(float deltaSeconds) {
        synchronized (worldHandler) {
            worldHandler.step(deltaSeconds * timeDistort);
        }
    }

    @API
    public EventListeners<KeyListener> getKeyListeners() {
        return keyListeners;
    }

    @API
    public EventListeners<MouseClickListener> getMouseClickListeners() {
        return mouseClickListeners;
    }

    @API
    public EventListeners<MouseWheelListener> getMouseWheelListeners() {
        return mouseWheelListeners;
    }

    @API
    public EventListeners<FrameUpdateListener> getFrameUpdateListeners() {
        return frameUpdateListeners;
    }

    @Internal
    void invokeFrameUpdateListeners(float deltaSeconds) {
        float scaledSeconds = deltaSeconds * timeDistort;
        frameUpdateListeners.invoke(frameUpdateListener -> frameUpdateListener.onFrameUpdate(scaledSeconds));
    }
}
