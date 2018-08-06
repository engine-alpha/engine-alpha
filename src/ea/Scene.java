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
import java.util.concurrent.CopyOnWriteArraySet;

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
     * Der Wurzel-ActorGroup. An ihm müssen direkt oder indirekt (über weitere ActorGroup) alle
     * <code>Actor</code>-Objekte angemeldet werden, die gezeichnet werden sollen.
     */
    private final ActorGroup root;

    /**
     * Die Physics der Szene.
     */
    private final WorldHandler worldHandler;

    public Scene() {
        this.worldHandler = new WorldHandler();
        this.camera = new Camera();
        this.root = new ActorGroup(this);
        this.addFrameUpdateListener(this.camera);
    }

    @NoExternalUse
    public void render(Graphics2D g, BoundingRechteck bounds) {
        this.root.renderBasic(g, bounds);

        if (Game.isDebug()) {
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
            g.drawOval((int) aOnZE.x - (CIRC_RAD / 2), (int) aOnZE.y - (CIRC_RAD / 2), CIRC_RAD, CIRC_RAD);
        } else if (j instanceof RopeJoint) {
            g.setColor(Color.cyan);
            g.drawRect((int) aOnZE.x - (CIRC_RAD / 2), (int) aOnZE.y - (CIRC_RAD / 2), RECT_SID, RECT_SID);
            g.drawRect((int) bOnZE.x - (CIRC_RAD / 2), (int) bOnZE.y - (CIRC_RAD / 2), RECT_SID, RECT_SID);
            g.drawLine((int) aOnZE.x, (int) aOnZE.y, (int) bOnZE.x, (int) bOnZE.y);
        } else if (j instanceof DistanceJoint) {
            g.setColor(Color.orange);
            g.drawRect((int) aOnZE.x - (CIRC_RAD / 2), (int) aOnZE.y - (CIRC_RAD / 2), RECT_SID, RECT_SID);
            g.drawRect((int) bOnZE.x - (CIRC_RAD / 2), (int) bOnZE.y - (CIRC_RAD / 2), RECT_SID, RECT_SID);
            g.drawLine((int) aOnZE.x, (int) aOnZE.y, (int) bOnZE.x, (int) bOnZE.y);
        }
    }

    public WorldHandler getWorldHandler() {
        return worldHandler;
    }

    @API
    final public void add(Actor... rooms) {
        for (Actor room : rooms) {
            this.root.add(room);
        }
    }

    @API
    final public void remove(Actor... rooms) {
        for (Actor room : rooms) {
            this.root.remove(room);
            room.destroy();
        }
    }

    // TODO : Dokumentation für alle ADD-Methoden

    @API
    final public void addMouseClickListener(MouseClickListener mouseClickListener) {
        this.mouseClickListeners.add(mouseClickListener);
    }

    @API
    final public void removeMouseClickListener(MouseClickListener mouseClickListener) {
        this.mouseClickListeners.remove(mouseClickListener);
    }

    @API
    final public void addMouseWheelListener(MouseWheelListener mouseWheelListener) {
        this.mouseWheelListeners.add(mouseWheelListener);
    }

    @API
    final public void removeMouseWheelListener(MouseWheelListener mouseWheelListener) {
        this.mouseWheelListeners.remove(mouseWheelListener);
    }

    @API
    final public void addKeyListener(KeyListener keyListener) {
        this.keyListeners.add(keyListener);
    }

    @API
    final public void removeKeyListener(KeyListener keyListener) {
        this.keyListeners.remove(keyListener);
    }

    @API
    final public void addFrameUpdateListener(FrameUpdateListener frameUpdateListener) {
        this.frameUpdateListeners.add(frameUpdateListener);
    }

    @API
    final public void removeFrameUpdateListener(FrameUpdateListener frameUpdateListener) {
        this.frameUpdateListeners.remove(frameUpdateListener);
    }

    @NoExternalUse
    final void onFrameUpdateInternal(int frameDuration) {
        for (FrameUpdateListener listener : this.frameUpdateListeners) {
            listener.onFrameUpdate(frameDuration);
        }
    }

    @NoExternalUse
    final void onKeyDownInternal(int key) {
        for (KeyListener listener : keyListeners) {
            listener.onKeyDown(key);
        }
    }

    @NoExternalUse
    final void onKeyUpInternal(int key) {
        for (KeyListener listener : keyListeners) {
            listener.onKeyUp(key);
        }
    }

    @NoExternalUse
    final void onMouseDownInternal(Vector position, MouseButton button) {
        for (MouseClickListener listener : mouseClickListeners) {
            listener.onMouseDown(position, button);
        }
    }

    @NoExternalUse
    final void onMouseUpInternal(Vector position, MouseButton button) {
        for (MouseClickListener listener : mouseClickListeners) {
            listener.onMouseUp(position, button);
        }
    }

    @NoExternalUse
    final void onMouseWheelMoveInternal(MouseWheelAction mouseWheelAction) {
        for (MouseWheelListener listener : mouseWheelListeners) {
            listener.onMouseWheelMove(mouseWheelAction);
        }
    }

    @API
    final public Vector getMousePosition() {
        return Game.convertMousePosition(this, Game.getMousePositionInFrame());
    }
}
