package ea;

import ea.actor.Actor;
import ea.event.EventListeners;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.internal.physics.BodyHandler;
import ea.internal.physics.NullHandler;
import ea.internal.physics.ProxyData;
import ea.internal.physics.WorldHandler;
import org.jbox2d.dynamics.Body;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Layer bieten die Möglichkeit, <code>Actors</code> vor und hinter der Zeichenebene mit zusätzlichen Eigenschaften
 * (wie Parallaxe) zu rendern.
 *
 * @author Michael Andonie
 */
public class Layer {

    /**
     * Der Inhalt des Layers.
     */
    private final Collection<Actor> actors;

    /**
     * Die Liste aller angemeldeten FrameUpdateListener.
     */
    private final EventListeners<FrameUpdateListener> frameUpdateListeners = new EventListeners<>();

    /**
     * Parallaxen-X-Faktor
     */
    private float parallaxX = 1;

    /**
     * Parallaxen-Y-Faktor
     */
    private float parallaxY = 1;

    /**
     * Parallaxen-Rotations-Faktor
     */
    private float parallaxRotation = 1;

    /**
     * Der Parallaxen-Zoom-Faktor
     */
    private float parallaxZoom = 1;

    /**
     * Ein Zeitverzerrungsfaktor
     */
    private float timeDistort = 1;

    /**
     * Bestimmt die Reihenfolge der Layer, kleinere Werte werden zuerst gerendert, sind also weiter "hinten"
     */
    private int layerPosition = -2;

    /**
     * Ob dieses Layer gerade sichtbar ist (also gerendert wird).
     */
    private boolean visible = true;

    /**
     * Die Physics des Layers.
     */
    private final WorldHandler worldHandler;

    /**
     * Die Parent-Scene dieses Layers.
     */
    private Scene parent;

    /**
     * Erstellt ein neues Layer.
     */
    @API
    public Layer() {
        worldHandler = new WorldHandler(this);
        actors = new ConcurrentLinkedQueue<>();
    }

    public Scene getParent() {
        return parent;
    }

    @Internal
    void setParent(Scene parent) {
        if (this.parent != null) {
            throw new IllegalStateException("Das Layer wurde bereits an einer Scene angemeldet.");
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
     *                    <li>Werte <code>>1</code> lassen die Zeit <b>schneller</b> vergehen</li>
     *                    <li>Werte <code><1</code> lassen die Zeit <b>langsamer</b> vergehen</li>
     *                    <li><code>1</code> lässt die Zeit in Echtzeit vergehen</li>
     *                    <li>Werte <code><=0</code> sind nicht erlaubt</li>
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
        for (Actor actor : actors) {
            if (actor.getPhysicsHandler().getBody() != null) {
                throw new IllegalArgumentException("Ein Actor kann nur an einem Layer gleichzeitig angemeldet sein");
            }

            actor.setPhysicsHandler(new BodyHandler(actor, actor.getPhysicsHandler().getProxyData(), worldHandler));

            this.actors.add(actor);
        }
    }

    @API
    final public void remove(Actor... actors) {
        for (Actor actor : actors) {
            this.actors.remove(actor);

            ProxyData proxyData = actor.getPhysicsHandler().getProxyData();
            Body body = actor.getPhysicsHandler().getBody();
            worldHandler.removeAllInternalReferences(body);
            worldHandler.getWorld().destroyBody(body);
            actor.setPhysicsHandler(new NullHandler(actor, proxyData));
        }
    }

    /**
     * Gibt die derzeit sichtbare Fläche des Layers auf dem Bildschirm an.
     *
     * @return Die sichtbare Fläche als Bounds Objekt <b>mit Angaben in Meter</b>
     */
    @Internal
    public Bounds visibleArea() {
        Vector center = parent.getCamera().getPosition();
        float pixelPerMeter = calculatePixelPerMeter(parent.getCamera());
        Bounds frameBoundsPx = Game.getFrameSizeInPx();
        return new Bounds(0, 0, frameBoundsPx.width / pixelPerMeter, frameBoundsPx.height / pixelPerMeter).withCenterPoint(center);
    }

    /**
     * Setzt den Kamerazoom exakt, sodass die sichtbare Breite des sichtbaren Fensters einer bestimmten Länge
     * entspricht.
     *
     * @param widthInM Die Breite in Meter, auf die die Kamera im Fenster exakt zu setzen ist.
     *
     * @see #setVisibleHeight(float)
     */
    @API
    public void setVisibleWidth(float widthInM) {
        Bounds frameBoundsPx = Game.getFrameSizeInPx();

        float desiredPixelPerMeter = frameBoundsPx.width / widthInM;
        float desiredZoom = 1 + ((desiredPixelPerMeter - 1) / parallaxZoom);
        parent.getCamera().setZoom(desiredZoom);
    }

    /**
     * Setzt den Kamerazoom exakt, sodass die sichtbare HÖhe des sichtbaren Fensters einer bestimmten Länge
     * entspricht.
     *
     * @param heightInM Die Höhe in Meter, auf die die Kamera im Fenster exakt zu setzen ist.
     *
     * @see #setVisibleWidth(float)
     */
    @API
    public void setVisibleHeight(float heightInM) {
        Bounds frameBoundsPx = Game.getFrameSizeInPx();

        float desiredPixelPerMeter = frameBoundsPx.height / heightInM;
        float desiredZoom = 1 + ((desiredPixelPerMeter - 1) / parallaxZoom);
        parent.getCamera().setZoom(desiredZoom);
    }

    private float calculatePixelPerMeter(Camera camera) {
        return 1 + (camera.getZoom() - 1) * parallaxZoom;
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

        float pixelPerMeter = calculatePixelPerMeter(camera);

        g.rotate(rotation * parallaxRotation, 0, 0);
        g.translate((-position.x * parallaxX) * pixelPerMeter, (position.y * parallaxY) * pixelPerMeter);

        // TODO: Calculate optimal bounds
        int size = Math.max(width, height);

        for (Actor actor : actors) {
            actor.renderBasic(g, new Bounds(position.x - size, position.y - size, size * 2, size * 2), pixelPerMeter);
        }
    }

    @API
    final public void addFrameUpdateListener(FrameUpdateListener frameUpdateListener) {
        frameUpdateListeners.addListener(frameUpdateListener);
    }

    @API
    final public void removeFrameUpdateListener(FrameUpdateListener frameUpdateListener) {
        frameUpdateListeners.removeListener(frameUpdateListener);
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

    public void step(float deltaTime) {
        synchronized (worldHandler) {
            float timeToSimulate = deltaTime * timeDistort;

            worldHandler.step(timeToSimulate);

            frameUpdateListeners.invoke(frameUpdateListener -> frameUpdateListener.onFrameUpdate(timeToSimulate));
        }
    }
}
