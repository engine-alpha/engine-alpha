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
import ea.actor.ActorGroup;
import ea.input.*;
import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import ea.internal.phy.WorldHandler;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RopeJoint;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashSet;

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
        root.renderBasic(g, bounds);

        if (Game.isDebug()) {
            renderJoints(g);
        }
    }

    @API
    public Camera getCamera() {
        return camera;
    }

    @NoExternalUse
    private void renderJoints(Graphics2D g) {
        // Display Joints
        Joint j = root.getPhysicsHandler().getWorldHandler().getWorld().getJointList();

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

        Vector aOnGrid = worldHandler.fromVec2(anchorA);
        Vector bOnGrid = worldHandler.fromVec2(anchorB);

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

    @NoExternalUse
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

    @NoExternalUse
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

    @NoExternalUse
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

    @NoExternalUse
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

    @NoExternalUse
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

    @NoExternalUse
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
