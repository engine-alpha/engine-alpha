package ea;

import ea.actor.Actor;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.internal.physics.WorldHandler;

import java.awt.*;
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
    private final Collection<Actor> actorList;

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

    private int zIndex = -2;

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
        worldHandler = new WorldHandler();
        actorList = new ConcurrentLinkedQueue<>();
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
     * @param sublayer Die neue Position dieses Layers. Je höher dieser Wert, desto weiter vorne ist das Layer.
     */
    @API
    public void setLayerPosition(int sublayer) {
        this.zIndex = sublayer;
        if (parent != null) {
            parent.layersUpdated();
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
        return zIndex;
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
        if (timeDistort <= 0) {
            throw new IllegalArgumentException("Zeitverzerrungsfaktor muss größer als 0 sein. War " + timeDistort);
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
        for (Actor room : actors) {
            this.actorList.add(room);
        }
    }

    @API
    final public void remove(Actor... actors) {
        for (Actor actor : actors) {
            this.actorList.remove(actor);
            actor.setScene(null);
        }
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

        float pixelPerMeter = 1 + (camera.getZoom() - 1) * parallaxZoom;

        g.rotate(rotation * parallaxRotation, 0, 0);
        g.translate((-position.x * parallaxX) * pixelPerMeter, (position.y * parallaxY) * pixelPerMeter);

        // TODO: Calculate optimal bounds
        int size = Math.max(width, height);

        for (Actor actor : actorList) {
            actor.renderBasic(g, new BoundingRechteck(position.x - size, position.y - size, size * 2, size * 2), pixelPerMeter);
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

    public void step(float deltaTime) {
        synchronized (worldHandler) {
            worldHandler.step(deltaTime * timeDistort);
        }
    }
}
