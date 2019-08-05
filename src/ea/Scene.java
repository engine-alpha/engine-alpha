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
import ea.input.*;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.internal.physics.WorldHandler;
import ea.internal.util.Logger;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RopeJoint;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Phaser;

public class Scene {
    /**
     * Die Kamera des Spiels. Hiermit kann der sichtbare Ausschnitt der Zeichenebene bestimmt und manipuliert werden.
     */
    private final Camera camera;

    /**
     * Die Liste aller angemeldeten KeyListener.
     */
    private final Collection<KeyListener> keyListeners = new HashSet<>();
    private boolean keyIterating = false;

    /**
     * Die Liste aller angemeldeten MouseClickListener.
     */
    private final Collection<MouseClickListener> mouseClickListeners = new HashSet<>();
    private boolean mouseClickIterating = false;

    /**
     * Die Liste aller angemeldeten MouseWheelListener.
     */
    private final Collection<MouseWheelListener> mouseWheelListeners = new HashSet<>();
    private boolean mouseWheelIterating = false;

    /**
     * Die Liste aller angemeldeten FrameUpdateListener.
     */
    private final Collection<FrameUpdateListener> frameUpdateListeners = new HashSet<>();
    private boolean frameUpdateIterating = false;

    /**
     * Die Layer dieser Szene.
     */
    private final List<Layer> layers = new ArrayList<>();

    /**
     * Das Main-Layer (default-additions)
     */
    private final Layer mainLayer;

    /**
     * Phaser für den Off-Worldstep.
     */
    private final Phaser offStepPhaser = new Phaser(1);

    /**
     * Phaser für den On-Worldstep.
     */
    private final Phaser onStepPhaser = new Phaser(1);

    private int layerCountForCurrentRender;
    private final Queue<Future> layerFutures = new ConcurrentLinkedQueue<>();

    public Scene() {
        this.camera = new Camera();
        mainLayer = new Layer();
        mainLayer.setLayerPosition(0);
        addLayer(mainLayer);
        this.addFrameUpdateListener(this.camera);
    }

    /**
     * Führt an allen Layern <b>parallelisiert</b> den World-Step aus.
     *
     * @param deltaTime Die Echtzeit, die seit dem letzten World-Step vergangen ist.
     */
    @Internal
    void worldStep(float deltaTime) {
        synchronized (layers) {
            layerCountForCurrentRender = layers.size();

            for (Layer layer : layers) {
                Future future = Game.threadPoolExecutor.submit(() -> layer.step(deltaTime));

                layerFutures.add(future);
            }
        }
    }

    /**
     * Gibt den World-Step Off-Phaser
     */
    @Internal
    Phaser getWorldStepOffPhaser() {
        return offStepPhaser;
    }

    /**
     * Gibt den World-Step On-Phaser
     */
    @Internal
    Phaser getOnStepPhaser() {
        return onStepPhaser;
    }

    @Internal
    public void render(Graphics2D g, int width, int height, Phaser worldStepEndBarrier) {
        final AffineTransform base = g.getTransform();

        int current = 0;
        while (current < layerCountForCurrentRender) {
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

            if (current == layerCountForCurrentRender - 1) {
                worldStepEndBarrier.arrive();
            }

            layer.render(g, camera, width, height);
            g.setTransform(base);

            current++;
        }

        if (current < layerCountForCurrentRender) {
            worldStepEndBarrier.arrive();
        }

        if (Game.isDebug()) {
            renderJoints(g);
        }
    }

    /**
     * Wird aufgerufen, wann immer ein Layerzustand innerhalb dieser Scene geändert wurde.
     * Stellt sicher, dass die Layer-Liste korrekt sortiert ist und aller Layer in der richtigen Reihenfolge gerendert
     * werden.
     */
    @Internal
    void layersUpdated() {
        this.layers.sort(Comparator.comparingInt(Layer::getLayerPosition));
    }

    @API
    public void addLayer(Layer layer) {
        synchronized (this.layers) {
            this.layers.add(layer);
            layer.setParent(this);
            layersUpdated();
        }
    }

    @API
    public void removeLayer(Layer layer) {
        synchronized (this.layers) {
            if (!this.layers.remove(layer) && Game.isVerbose()) {
                Logger.warning("Ein Layer, das gar nicht an der Scene angehängt ist, sollte entfernt werden.", "layer");
            }
        }
    }

    @API
    public Camera getCamera() {
        return camera;
    }

    @Internal
    private void renderJoints(Graphics2D g) {
        // Display Joints
        Joint j = mainLayer.getWorldHandler().getWorld().getJointList();

        while (j != null) {
            renderJoint(j, g);
            j = j.m_next;
        }
    }

    @Internal
    private static void renderJoint(Joint j, Graphics2D g) {
        final int CIRC_RAD = 10; // (Basis-)Radius für die Visualisierung von Kreisen
        final int RECT_SID = 12; // (Basis-)Breite für die Visualisierung von Rechtecken

        Vec2 anchorA = new Vec2(), anchorB = new Vec2();
        j.getAnchorA(anchorA);
        j.getAnchorB(anchorB);

        Vector aOnGrid = Vector.of(anchorA);
        Vector bOnGrid = Vector.of(anchorB);

        if (j instanceof RevoluteJoint) {
            g.setColor(Color.blue);
            g.drawOval((int) aOnGrid.x - (CIRC_RAD / 2), (int) aOnGrid.y - (CIRC_RAD / 2), CIRC_RAD, CIRC_RAD);
        } else if (j instanceof RopeJoint) {
            g.setColor(Color.cyan);
            g.drawRect((int) aOnGrid.x - (CIRC_RAD / 2), (int) aOnGrid.y - (CIRC_RAD / 2), RECT_SID, RECT_SID);
            g.drawRect((int) bOnGrid.x - (CIRC_RAD / 2), (int) bOnGrid.y - (CIRC_RAD / 2), RECT_SID, RECT_SID);
            g.drawLine((int) aOnGrid.x, (int) aOnGrid.y, (int) bOnGrid.x, (int) bOnGrid.y);
        } else if (j instanceof DistanceJoint) {
            g.setColor(Color.orange);
            g.drawRect((int) aOnGrid.x - (CIRC_RAD / 2), (int) aOnGrid.y - (CIRC_RAD / 2), RECT_SID, RECT_SID);
            g.drawRect((int) bOnGrid.x - (CIRC_RAD / 2), (int) bOnGrid.y - (CIRC_RAD / 2), RECT_SID, RECT_SID);
            g.drawLine((int) aOnGrid.x, (int) aOnGrid.y, (int) bOnGrid.x, (int) bOnGrid.y);
        }
    }

    /**
     * Gibt den Worldhandler des Main-Layers aus.
     *
     * @return der Worldhandler des Main-Layers.
     */
    @Internal
    public WorldHandler getWorldHandler() {
        return mainLayer.getWorldHandler();
    }

    /**
     * Setzt die Schwerkraft, die auf <b>alle Objekte innerhalb des Hauptlayers der Scene</b> wirkt.
     *
     * @param gravityInN Die neue Schwerkraft als Vector. Die Einheit ist <b>[N]</b>.
     */
    @API
    public void setGravity(Vector gravityInN) {
        mainLayer.getWorldHandler().getWorld().setGravity(new Vec2(gravityInN.x, gravityInN.y));
    }

    /**
     * Setzt, ob die Engine-Physics für diese Szene pausiert sein soll.
     *
     * @param worldPaused <code>false</code>: Die Engine-Physik läuft normal.
     *                    <code>true</code>: Die Engine-Physik läuft <b>nicht</b>. Das bedeutet u.A. keine
     *                    Collision-Detection, keine Physik-Simulation etc., bis die Physik wieder mit
     *                    <code>setPhysicsPaused(true)</code> aktiviert wird.
     *
     * @see #getPhysicsPaused()
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
    public boolean getPhysicsPaused() {
        return mainLayer.getWorldHandler().isWorldPaused();
    }

    @API
    final public void add(Actor... actors) {
        Game.afterWorldStep(() -> {
            for (Actor actor : actors) {
                if (actor.getScene() != null) {
                    throw new IllegalArgumentException("Ein Actor, der an einer Scene angemeldet ist, kann nicht " + "hinzugefügt werden, bevor er abgemeldet wurde.");
                }
                mainLayer.add(actor);
                actor.setScene(this);
            }
        });
    }

    @API
    final public void remove(Actor... actors) {
        Game.afterWorldStep(() -> {
            for (Actor actor : actors) {
                mainLayer.remove(actor);
            }
        });
    }

    @API
    final public void addMouseClickListener(MouseClickListener mouseClickListener) {
        synchronized (mouseClickListeners) {
            if (mouseClickIterating) {
                Game.enqueue(() -> mouseClickListeners.add(mouseClickListener));
            } else {
                mouseClickListeners.add(mouseClickListener);
            }
        }
    }

    @API
    final public void removeMouseClickListener(MouseClickListener mouseClickListener) {
        synchronized (mouseClickListeners) {
            if (mouseClickIterating) {
                Game.enqueue(() -> mouseClickListeners.remove(mouseClickListener));
            } else {
                mouseClickListeners.remove(mouseClickListener);
            }
        }
    }

    @API
    final public void addMouseWheelListener(MouseWheelListener mouseWheelListener) {
        synchronized (mouseWheelListeners) {
            if (mouseWheelIterating) {
                Game.enqueue(() -> mouseWheelListeners.add(mouseWheelListener));
            } else {
                mouseWheelListeners.add(mouseWheelListener);
            }
        }
    }

    @API
    final public void removeMouseWheelListener(MouseWheelListener mouseWheelListener) {
        synchronized (mouseWheelListeners) {
            if (mouseWheelIterating) {
                Game.enqueue(() -> mouseWheelListeners.remove(mouseWheelListener));
            } else {
                mouseWheelListeners.remove(mouseWheelListener);
            }
        }
    }

    @API
    final public void addKeyListener(KeyListener keyListener) {
        synchronized (keyListeners) {
            if (keyIterating) {
                Game.enqueue(() -> keyListeners.add(keyListener));
            } else {
                keyListeners.add(keyListener);
            }
        }
    }

    @API
    final public void removeKeyListener(KeyListener keyListener) {
        synchronized (keyListeners) {
            if (keyIterating) {
                Game.enqueue(() -> keyListeners.remove(keyListener));
            } else {
                keyListeners.remove(keyListener);
            }
        }
    }

    @API
    final public void addFrameUpdateListener(FrameUpdateListener frameUpdateListener) {
        synchronized (frameUpdateListeners) {
            if (frameUpdateIterating) {
                Game.enqueue(() -> frameUpdateListeners.add(frameUpdateListener));
            } else {
                frameUpdateListeners.add(frameUpdateListener);
            }
        }
    }

    @API
    final public void removeFrameUpdateListener(FrameUpdateListener frameUpdateListener) {
        synchronized (frameUpdateListeners) {
            if (frameUpdateIterating) {
                Game.enqueue(() -> frameUpdateListeners.remove(frameUpdateListener));
            } else {
                frameUpdateListeners.remove(frameUpdateListener);
            }
        }
    }

    @Internal
    final void onFrameUpdateInternal(int frameDuration) {
        synchronized (frameUpdateListeners) {
            try {
                frameUpdateIterating = true;

                for (FrameUpdateListener listener : frameUpdateListeners) {
                    listener.onFrameUpdate(frameDuration);
                }
            } finally {
                frameUpdateIterating = false;
            }
        }
    }

    @Internal
    final void onKeyDownInternal(KeyEvent e) {
        synchronized (keyListeners) {
            try {
                keyIterating = true;

                for (KeyListener listener : keyListeners) {
                    listener.onKeyDown(e);
                }
            } finally {
                keyIterating = false;
            }
        }
    }

    @Internal
    final void onKeyUpInternal(KeyEvent e) {
        synchronized (keyListeners) {
            try {
                keyIterating = true;

                for (KeyListener listener : keyListeners) {
                    listener.onKeyUp(e);
                }
            } finally {
                keyIterating = false;
            }
        }
    }

    @Internal
    final void onMouseDownInternal(Vector position, MouseButton button) {
        synchronized (mouseClickListeners) {
            try {
                mouseClickIterating = true;

                for (MouseClickListener listener : mouseClickListeners) {
                    listener.onMouseDown(position, button);
                }
            } finally {
                mouseClickIterating = false;
            }
        }
    }

    @Internal
    final void onMouseUpInternal(Vector position, MouseButton button) {
        synchronized (mouseClickListeners) {
            try {
                mouseClickIterating = true;

                for (MouseClickListener listener : mouseClickListeners) {
                    listener.onMouseUp(position, button);
                }
            } finally {
                mouseClickIterating = false;
            }
        }
    }

    @Internal
    final void onMouseWheelMoveInternal(MouseWheelAction mouseWheelAction) {
        synchronized (mouseWheelListeners) {
            try {
                mouseWheelIterating = true;

                for (MouseWheelListener listener : mouseWheelListeners) {
                    listener.onMouseWheelMove(mouseWheelAction);
                }
            } finally {
                mouseWheelIterating = false;
            }
        }
    }

    @API
    public final Vector getMousePosition() {
        return Game.convertMousePosition(this, Game.getMousePositionInFrame());
    }
}
