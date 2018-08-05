/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2017 Michael Andonie and contributors.
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
import ea.actor.ActorGroup;
import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import ea.internal.phy.WorldHandler;
import ea.keyboard.KeyListener;
import ea.mouse.MouseButton;
import ea.mouse.MouseClickListener;
import ea.mouse.MouseWheelAction;
import ea.mouse.MouseWheelListener;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RopeJoint;

import java.awt.*;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

public class Scene {
    /**
     * Die Kamera des Spiels. Hiermit kann der sichtbare Ausschnitt der Zeichenebene
     * bestimmt und manipuliert werden.
     */
    private final Camera camera;

    /**
     * Die Liste aller angemeldeten KeyListener.
     */
    private final Collection<KeyListener> keyListeners = new CopyOnWriteArraySet<>();

    /**
     * Die Liste aller angemeldeten MouseClickListener.
     */
    private final Collection<MouseClickListener> mouseClickListeners = new CopyOnWriteArraySet<>();

    /**
     * Die Liste aller angemeldeten MouseWheelListener.
     */
    private final Collection<MouseWheelListener> mouseWheelListeners = new CopyOnWriteArraySet<>();

    /**
     * Die Liste aller angemeldeten FrameUpdateListener.
     */
    private final Collection<FrameUpdateListener> frameUpdateListeners = new CopyOnWriteArraySet<>();

    /**
     * Die Liste aller angemeldeten Attachables.
     */
    private final Map<Attachable, AtomicInteger> attachables = new ConcurrentHashMap<>();

    /**
     * Der Wurzel-ActorGroup. An ihm müssen direkt oder indirekt (über weitere ActorGroup) alle
     * <code>Actor</code>-Objekte angemeldet werden, die gezeichnet werden sollen.
     */
    private final ActorGroup root;

    /**
     * Die Physics der Szene.
     */
    private final WorldHandler worldHandler;

    public Scene() {
        this.root = new ActorGroup();
        this.camera = new Camera();
        this.worldHandler = new WorldHandler();
        this.root.onAttach(this);
        this.addFrameUpdateListener(this.camera);
    }

    @NoExternalUse
    public void render(Graphics2D g, BoundingRechteck bounds) {
        this.root.renderBasic(g, bounds);

        if (EngineAlpha.isDebug()) {
            this.renderDebug(g);
        }
    }

    @API
    public Camera getCamera() {
        return this.camera;
    }

    @NoExternalUse
    private void renderDebug(Graphics2D g) {
        // Display Joints
        Joint j = root.getPhysicsHandler().worldHandler().getWorld().getJointList();

        while (j != null) {
            renderJoint(j, g);
            j = j.m_next;
        }
    }

    @NoExternalUse
    private void renderJoint(Joint j, Graphics2D g) {
        final int CIRC_RAD = 10; // (Basis-)Radius für die Visualisierung von Kreisen
        final int RECT_SID = 12; // (Basis-)Breite für die Visualisierung von Rechtecken

        Vec2 anchorA = new Vec2(), anchorB = new Vec2();
        j.getAnchorA(anchorA);
        j.getAnchorB(anchorB);

        Vector aOnZE = root.getPhysicsHandler().worldHandler().fromVec2(anchorA);
        Vector bOnZE = root.getPhysicsHandler().worldHandler().fromVec2(anchorB);

        if (j instanceof RevoluteJoint) {
            g.setColor(Color.blue);
            g.drawOval((int) aOnZE.getRealX() - (CIRC_RAD / 2), (int) aOnZE.getRealY() - (CIRC_RAD / 2), CIRC_RAD, CIRC_RAD);
        } else if (j instanceof RopeJoint) {
            g.setColor(Color.cyan);
            g.drawRect((int) aOnZE.getRealX() - (CIRC_RAD / 2), (int) aOnZE.getRealY() - (CIRC_RAD / 2), RECT_SID, RECT_SID);
            g.drawRect((int) bOnZE.getRealX() - (CIRC_RAD / 2), (int) bOnZE.getRealY() - (CIRC_RAD / 2), RECT_SID, RECT_SID);
            g.drawLine((int) aOnZE.getRealX(), (int) aOnZE.getRealY(), (int) bOnZE.getRealX(), (int) bOnZE.getRealY());
        } else if (j instanceof DistanceJoint) {
            g.setColor(Color.orange);
            g.drawRect((int) aOnZE.getRealX() - (CIRC_RAD / 2), (int) aOnZE.getRealY() - (CIRC_RAD / 2), RECT_SID, RECT_SID);
            g.drawRect((int) bOnZE.getRealX() - (CIRC_RAD / 2), (int) bOnZE.getRealY() - (CIRC_RAD / 2), RECT_SID, RECT_SID);
            g.drawLine((int) aOnZE.getRealX(), (int) aOnZE.getRealY(), (int) bOnZE.getRealX(), (int) bOnZE.getRealY());
        }
    }

    public WorldHandler getWorldHandler() {
        return worldHandler;
    }

    @API
    public void add(Actor... rooms) {
        for (Actor room : rooms) {
            this.root.add(room);
        }
    }

    @API
    public void remove(Actor... rooms) {
        for (Actor room : rooms) {
            this.root.remove(room);
        }
    }

    // TODO : Dokumentation für alle ADD-Methoden

    public void attach(Object object) {
        if (object instanceof Attachable) {
            if (!attachables.containsKey(object)) {
                Attachable attachable = (Attachable) object;
                attachables.put(attachable, new AtomicInteger(1));
                attachable.onAttach(this);
            } else {
                attachables.get(object).incrementAndGet();
            }
        }
    }

    public void detach(Object object) {
        if (!(object instanceof Attachable) || !attachables.containsKey(object)) {
            return;
        }

        int count = attachables.get(object).decrementAndGet();

        if (count == 0) {
            attachables.remove(object);
            ((Attachable) object).onDetach(this);
        }
    }

    @API
    public void addMouseClickListener(MouseClickListener mouseClickListener) {
        if (this.mouseClickListeners.add(mouseClickListener)) {
            this.attach(mouseClickListener);
        }
    }

    @API
    public void removeMouseClickListener(MouseClickListener mouseClickListener) {
        if (this.mouseClickListeners.remove(mouseClickListener)) {
            this.detach(mouseClickListener);
        }
    }

    @API
    public void addMouseWheelListener(MouseWheelListener mouseWheelListener) {
        if (this.mouseWheelListeners.add(mouseWheelListener)) {
            this.attach(mouseWheelListener);
        }
    }

    @API
    public void removeMouseWheelListener(MouseWheelListener mouseWheelListener) {
        if (this.mouseWheelListeners.remove(mouseWheelListener)) {
            this.detach(mouseWheelListener);
        }
    }

    @API
    public void addKeyListener(KeyListener keyListener) {
        if (this.keyListeners.add(keyListener)) {
            this.attach(keyListener);
        }
    }

    @API
    public void removeKeyListener(KeyListener keyListener) {
        if (this.keyListeners.remove(keyListener)) {
            this.detach(keyListener);
        }
    }

    @API
    public void addFrameUpdateListener(FrameUpdateListener frameUpdateListener) {
        if (this.frameUpdateListeners.add(frameUpdateListener)) {
            this.attach(frameUpdateListener);
        }
    }

    @API
    public void removeFrameUpdateListener(FrameUpdateListener frameUpdateListener) {
        if (this.frameUpdateListeners.remove(frameUpdateListener)) {
            this.detach(frameUpdateListener);
        }
    }



    @NoExternalUse
    public final void onFrameUpdateInternal(int frameDuration) {
        for (FrameUpdateListener listener : this.frameUpdateListeners) {
            listener.onFrameUpdate(frameDuration);
        }
    }

    @NoExternalUse
    public void onKeyDownInternal(int key) {
        for (KeyListener listener : keyListeners) {
            listener.onKeyDown(key);
        }
    }

    @NoExternalUse
    public void onKeyUpInternal(int key) {
        for (KeyListener listener : keyListeners) {
            listener.onKeyUp(key);
        }
    }

    @NoExternalUse
    public void onMouseDownInternal(Point position, MouseButton button) {
        for (MouseClickListener listener : mouseClickListeners) {
            listener.onMouseDown(position, button);
        }
    }

    @NoExternalUse
    public void onMouseUpInternal(Point position, MouseButton button) {
        for (MouseClickListener listener : mouseClickListeners) {
            listener.onMouseUp(position, button);
        }
    }

    @NoExternalUse
    public void onMouseWheelMoveInternal(MouseWheelAction mouseWheelAction) {
        for (MouseWheelListener listener : mouseWheelListeners) {
            listener.onMouseWheelMove(mouseWheelAction);
        }
    }

    @API
    public Point getMousePosition() {
        return Game.convertMousePosition(this, Game.getMousePositionInFrame());
    }
}
