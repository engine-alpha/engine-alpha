/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2018 Michael Andonie and contributors.
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
import ea.event.EventListeners;
import ea.input.*;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.internal.physics.WorldHandler;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RopeJoint;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;

public class Scene {
    public static final Color REVOLUTE_JOINT_COLOR = Color.blue;
    public static final Color ROPE_JOINT_COLOR = Color.CYAN;
    public static final Color DISTANCE_JOINT_COLOR = Color.ORANGE;
    /**
     * Die Kamera des Spiels. Hiermit kann der sichtbare Ausschnitt der Zeichenebene bestimmt und manipuliert werden.
     */
    private final Camera camera;

    private final EventListeners<KeyListener> keyListeners = new EventListeners<>();
    private final EventListeners<MouseClickListener> mouseClickListeners = new EventListeners<>();
    private final EventListeners<MouseWheelListener> mouseWheelListeners = new EventListeners<>();
    private final EventListeners<FrameUpdateListener> frameUpdateListeners = new EventListeners<>();

    /**
     * Die Layer dieser Szene.
     */
    private final List<Layer> layers = new ArrayList<>();

    /**
     * Gibt das Main Layer dieser Scene aus.
     *
     * @return Das Main Layer der Scene.
     */
    @API
    public Layer getMainLayer() {
        return mainLayer;
    }

    /**
     * Das Main-Layer (default-additions)
     */
    private final Layer mainLayer;

    @SuppressWarnings ( "FieldAccessedSynchronizedAndUnsynchronized" )
    private transient int layerCountForCurrentRender;
    private transient final Queue<Future> layerFutures = new ConcurrentLinkedQueue<>();// (Basis-)Radius für die Visualisierung von Kreisen

    private static final int JOINT_CIRCLE_RADIUS = 10;// (Basis-)Breite für die Visualisierung von Rechtecken
    private static final int JOINT_RECTANGLE_SIDE = 12;

    public Scene() {
        this.camera = new Camera();
        mainLayer = new Layer();
        mainLayer.setLayerPosition(0);
        addLayer(mainLayer);
        autoRegisterListeners();
    }

    private void autoRegisterListeners() {
        if (this instanceof KeyListener) {
            getKeyListeners().add((KeyListener) this);
        }

        if (this instanceof MouseClickListener) {
            getMouseClickListeners().add((MouseClickListener) this);
        }

        if (this instanceof MouseWheelListener) {
            getMouseWheelListeners().add((MouseWheelListener) this);
        }

        if (this instanceof FrameUpdateListener) {
            getFrameUpdateListeners().add((FrameUpdateListener) this);
        }
    }

    /**
     * Führt an allen Layern <b>parallelisiert</b> den World-Step aus.
     *
     * @param deltaTime Die Echtzeit, die seit dem letzten World-Step vergangen ist.
     */
    @Internal
    final void worldStep(float deltaTime, Phaser worldStepEndBarrier) {
        camera.onFrameUpdate();

        synchronized (layers) {
            layerCountForCurrentRender = layers.size();
            AtomicInteger remainingSteps = new AtomicInteger(layerCountForCurrentRender);

            for (Layer layer : layers) {
                Future future = Game.threadPoolExecutor.submit(() -> {
                    layer.step(deltaTime);

                    if (remainingSteps.decrementAndGet() == 0) {
                        worldStepEndBarrier.arrive();
                    }
                });

                layerFutures.add(future);
            }
        }
    }

    @Internal
    public final void render(Graphics2D g, int width, int height) {
        final AffineTransform base = g.getTransform();

        int current = 0;
        int limit = this.layerCountForCurrentRender;

        while (current < limit) {
            Future future = layerFutures.poll();
            if (future == null) {
                break;
            }

            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            Layer layer;
            synchronized (layers) {
                layer = layers.get(current);
            }

            if (layer == null) {
                break;
            }

            layer.render(g, camera, width, height);
            g.setTransform(base);

            current++;
        }

        if (Game.isDebug()) {
            renderJoints(g, camera);
        }
    }

    /**
     * Wird aufgerufen, wann immer ein Layerzustand innerhalb dieser Scene geändert wurde.
     * Stellt sicher, dass die Layer-Liste korrekt sortiert ist und aller Layer in der richtigen Reihenfolge gerendert
     * werden.
     */
    @Internal
    final void sortLayers() {
        this.layers.sort(Comparator.comparingInt(Layer::getLayerPosition));
    }

    @API
    public final void addLayer(Layer layer) {
        synchronized (this.layers) {
            layer.setParent(this);
            this.layers.add(layer);
            sortLayers();
        }
    }

    @API
    public final void removeLayer(Layer layer) {
        synchronized (this.layers) {
            this.layers.remove(layer);
            layer.setParent(null);
        }
    }

    /**
     * Gibt die sichtbare Fläche auf dem <b>Hauptlayer</b> aus.
     *
     * @return Die sichtbare Fläche auf dem Hauptlayer
     */
    @API
    public Bounds getVisibleArea() {
        return mainLayer.visibleArea();
    }

    @API
    public final Camera getCamera() {
        return camera;
    }

    @Internal
    private void renderJoints(Graphics2D g, Camera camera) {
        // Display Joints

        for (Layer layer : layers) {
            Joint j = layer.getWorldHandler().getWorld().getJointList();

            while (j != null) {
                renderJoint(j, g, layer, camera);
                j = j.m_next;
            }
        }
    }

    @Internal
    private static void renderJoint(Joint j, Graphics2D g, Layer layer, Camera camera) {
        Vec2 anchorA = new Vec2(), anchorB = new Vec2();
        j.getAnchorA(anchorA);
        j.getAnchorB(anchorB);

        Vector aInWorld = Vector.of(anchorA);
        Vector bInWorld = Vector.of(anchorB);

        final float pixelPerMeter = layer.calculatePixelPerMeter(camera);

        if (j instanceof RevoluteJoint) {
            g.setColor(REVOLUTE_JOINT_COLOR);
            g.drawOval((int) aInWorld.x - (JOINT_CIRCLE_RADIUS / 2), (int) aInWorld.y - (JOINT_CIRCLE_RADIUS / 2), JOINT_CIRCLE_RADIUS, JOINT_CIRCLE_RADIUS);
        } else if (j instanceof RopeJoint) {
            renderJointRectangle(g, ROPE_JOINT_COLOR, aInWorld, bInWorld);
        } else if (j instanceof DistanceJoint) {
            renderJointRectangle(g, DISTANCE_JOINT_COLOR, aInWorld, bInWorld);
        }
    }

    @Internal
    private static void renderJointRectangle(Graphics2D g, Color color, Vector a, Vector b) {
        g.setColor(color);
        g.drawRect((int) a.x - (JOINT_CIRCLE_RADIUS / 2), (int) a.y - (JOINT_CIRCLE_RADIUS / 2), JOINT_RECTANGLE_SIDE, JOINT_RECTANGLE_SIDE);
        g.drawRect((int) b.x - (JOINT_CIRCLE_RADIUS / 2), (int) b.y - (JOINT_CIRCLE_RADIUS / 2), JOINT_RECTANGLE_SIDE, JOINT_RECTANGLE_SIDE);
        g.drawLine((int) a.x, (int) a.y, (int) b.x, (int) b.y);
    }

    /**
     * Gibt den Worldhandler des Main-Layers aus.
     *
     * @return der Worldhandler des Main-Layers.
     */
    @Internal
    public final WorldHandler getWorldHandler() {
        return mainLayer.getWorldHandler();
    }

    /**
     * Setzt die Schwerkraft, die auf <b>alle Objekte innerhalb des Hauptlayers der Scene</b> wirkt.
     *
     * @param gravityInNewton Die neue Schwerkraft als Vector. Die Einheit ist <b>[N]</b>.
     */
    @API
    public void setGravity(Vector gravityInNewton) {
        mainLayer.getWorldHandler().getWorld().setGravity(new Vec2(gravityInNewton.x, gravityInNewton.y));
    }

    /**
     * Setzt, ob die Engine-Physics für diese Szene pausiert sein soll.
     *
     * @param worldPaused <code>false</code>: Die Engine-Physik läuft normal.
     *                    <code>true</code>: Die Engine-Physik läuft <b>nicht</b>. Das bedeutet u.A. keine
     *                    Collision-Detection, keine Physik-Simulation etc., bis die Physik wieder mit
     *                    <code>setPhysicsPaused(true)</code> aktiviert wird.
     *
     * @see #isPhysicsPaused()
     */
    @API
    public void setPhysicsPaused(boolean worldPaused) {
        mainLayer.getWorldHandler().setWorldPaused(worldPaused);
    }

    /**
     * Gibt an, ob die Physik dieser Szene pausiert ist.
     *
     * @return <code>true</code>: Die Physik ist pausiert.
     * <code>false</code>: Die Physik ist nicht pausiert.
     *
     * @see #setPhysicsPaused(boolean)
     */
    @API
    public boolean isPhysicsPaused() {
        return mainLayer.getWorldHandler().isWorldPaused();
    }

    @API
    final public void add(Actor... actors) {
        for (Actor actor : actors) {
            mainLayer.add(actor);
        }
    }

    @API
    final public void remove(Actor... actors) {
        for (Actor actor : actors) {
            mainLayer.remove(actor);
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

    private final class OneTimeCallBackHandle implements Runnable {
        /**
         * Speichert invoked-state, um sicherzustellen, dass der One Time Callback nicht mehrfach ausgeführt wird.
         *
         * @see ea.PeriodicTask#onFrameUpdate(float)
         */
        private boolean invoked = false;
        private final Runnable callback;
        private PeriodicTask toRemove;

        public OneTimeCallBackHandle(Runnable runnable) {
            this.callback = runnable;
        }

        private void setToRemove(PeriodicTask toRemove) {
            this.toRemove = toRemove;
        }

        @Override
        public void run() {
            if (!invoked) {
                callback.run();
                invoked = true;
                Scene.this.getFrameUpdateListeners().remove(toRemove);
            }
        }
    }

    @API
    final public void addOneTimeCallback(int delayInMS, Runnable callback) {
        OneTimeCallBackHandle oneTimeCallBackHandle = new OneTimeCallBackHandle(callback);
        PeriodicTask periodicTask = new PeriodicTask(delayInMS, oneTimeCallBackHandle);
        oneTimeCallBackHandle.setToRemove(periodicTask);
        getFrameUpdateListeners().add(periodicTask);
    }

    @Internal
    final void onFrameUpdateInternal(int frameDuration) {
        frameUpdateListeners.invoke(frameUpdateListener -> frameUpdateListener.onFrameUpdate(frameDuration));
    }

    @Internal
    final void onKeyDownInternal(KeyEvent e) {
        keyListeners.invoke(keyListener -> keyListener.onKeyDown(e));
    }

    @Internal
    final void onKeyUpInternal(KeyEvent e) {
        keyListeners.invoke(keyListener -> keyListener.onKeyUp(e));
    }

    @Internal
    final void onMouseDownInternal(Vector position, MouseButton button) {
        mouseClickListeners.invoke(mouseClickListener -> mouseClickListener.onMouseDown(position, button));
    }

    @Internal
    final void onMouseUpInternal(Vector position, MouseButton button) {
        mouseClickListeners.invoke(mouseClickListener -> mouseClickListener.onMouseUp(position, button));
    }

    @Internal
    final void onMouseWheelMoveInternal(MouseWheelEvent mouseWheelEvent) {
        mouseWheelListeners.invoke(mouseWheelListener -> mouseWheelListener.onMouseWheelMove(mouseWheelEvent));
    }

    @API
    public final Vector getMousePosition() {
        return Game.convertMousePosition(this, Game.getMousePositionInFrame());
    }
}
