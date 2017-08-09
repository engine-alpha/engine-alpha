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

import ea.collision.CollisionListener;
import ea.internal.ano.API;
import ea.internal.phy.WorldHandler;
import ea.keyboard.KeyListener;
import ea.mouse.MouseButton;
import ea.mouse.MouseClickListener;
import ea.raum.Knoten;
import ea.raum.Raum;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RopeJoint;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Scene implements FrameUpdateListener, MouseClickListener, KeyListener {
    /**
     * Die Kamera des Spiels. Hiermit kann der sichtbare Ausschnitt der Zeichenebene
     * bestimmt und manipuliert werden.
     */
    private final Camera camera;

    /**
     * Die Liste aller angemeldeten KeyListener.
     */
    private final Set<KeyListener> keyListeners = new HashSet<>();

    /**
     * Die Liste aller angemeldeten MouseClickListener.
     */
    private final Set<MouseClickListener> mouseClickListeners = new HashSet<>();

    /**
     * Die Liste aller angemeldeten FrameUpdateListener.
     */
    private Set<FrameUpdateListener> frameUpdateListeners = new HashSet<>();

    /**
     * Die Liste aller angemeldeten PeriodicTask.
     */
    private final Set<PeriodicTask> tickers = new HashSet<>();

    /**
     * Der Wurzel-Knoten. An ihm müssen direkt oder indirekt (über weitere Knoten) alle
     * <code>Raum</code>-Objekte angemeldet werden, die gezeichnet werden sollen.
     */
    private final Knoten root;

    /**
     * Die Physik der Szene.
     */
    private final WorldHandler worldHandler;

    public Scene() {
        this.root = new Knoten();
        this.camera = new Camera();
        this.worldHandler = new WorldHandler();
        this.root.onAttach(this);
        this.addFrameUpdateListener(this.camera);
    }

    public void render(Graphics2D g, BoundingRechteck bounds) {
        this.root.renderBasic(g, bounds);

        if (EngineAlpha.isDebug()) {
            this.renderDebug(g);
        }
    }

    private void renderDebug(Graphics2D g) {
        this.renderDebugGrid(g);
    }

    public Camera getCamera() {
        return this.camera;
    }

    private void renderDebugGrid(Graphics2D g) {
        int tx = (int) camera.getPosition().x;
        int ty = (int) camera.getPosition().y;
        int gridSize = 50;

        g.translate(-tx, -ty);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        g.setColor(new Color(255, 255, 255, 100));

        // TODO: Remove 500px values and replace with correct solution that uses the Window size.

        for (int x = tx / gridSize * gridSize; x < tx + 500; x += gridSize) {
            g.drawLine(x, ty, x, ty + (int) 500);
            g.drawString("" + x, x + 10, ty + 20);
        }

        for (int y = ty / gridSize * gridSize; y < ty + 500; y += gridSize) {
            g.drawLine(tx, y, tx + (int) 500, y);
            g.drawString("" + y, tx + 10, y + 20);
        }

        // Display Joints
        Joint j = root.getPhysikHandler().worldHandler().getWorld().getJointList();

        while (j != null) {
            renderJoint(j, g);
            j = j.m_next;
        }
    }

    private void renderJoint(Joint j, Graphics2D g) {
        final int CIRC_RAD = 10; // (Basis-)Radius für die Visualisierung von Kreisen
        final int RECT_SID = 12; // (Basis-)Breite für die Visualisierung von Rechtecken

        Vec2 anchorA = new Vec2(), anchorB = new Vec2();
        j.getAnchorA(anchorA);
        j.getAnchorB(anchorB);

        Vektor aOnZE = root.getPhysikHandler().worldHandler().fromVec2(anchorA);
        Vektor bOnZE = root.getPhysikHandler().worldHandler().fromVec2(anchorB);

        if (j instanceof RevoluteJoint) {
            g.setColor(Color.blue);
            g.drawOval((int) aOnZE.realX() - (CIRC_RAD / 2), (int) aOnZE.realY() - (CIRC_RAD / 2), CIRC_RAD, CIRC_RAD);
        } else if (j instanceof RopeJoint) {
            g.setColor(Color.cyan);
            g.drawRect((int) aOnZE.realX() - (CIRC_RAD / 2), (int) aOnZE.realY() - (CIRC_RAD / 2), RECT_SID, RECT_SID);
            g.drawRect((int) bOnZE.realX() - (CIRC_RAD / 2), (int) bOnZE.realY() - (CIRC_RAD / 2), RECT_SID, RECT_SID);
            g.drawLine((int) aOnZE.realX(), (int) aOnZE.realY(), (int) bOnZE.realX(), (int) bOnZE.realY());
        } else if (j instanceof DistanceJoint) {
            g.setColor(Color.orange);
            g.drawRect((int) aOnZE.realX() - (CIRC_RAD / 2), (int) aOnZE.realY() - (CIRC_RAD / 2), RECT_SID, RECT_SID);
            g.drawRect((int) bOnZE.realX() - (CIRC_RAD / 2), (int) bOnZE.realY() - (CIRC_RAD / 2), RECT_SID, RECT_SID);
            g.drawLine((int) aOnZE.realX(), (int) aOnZE.realY(), (int) bOnZE.realX(), (int) bOnZE.realY());
        }
    }

    public WorldHandler getWorldHandler() {
        return worldHandler;
    }

    @Override
    public void onFrameUpdate(int frameDuration) {
        for (FrameUpdateListener listener : this.frameUpdateListeners) {
            listener.onFrameUpdate(frameDuration);
        }
    }

    public void add(Raum... rooms) {
        for (Raum room : rooms) {
            this.root.add(room);
            room.onAttach(this);
        }
    }

    public void addMouseClickListener(MouseClickListener mouseClickListener) {
        this.mouseClickListeners.add(mouseClickListener);
    }

    public void addKeyListener(KeyListener keyListener) {
        this.keyListeners.add(keyListener);
    }

    public void addFrameUpdateListener(FrameUpdateListener frameUpdateListener) {
        this.frameUpdateListeners.add(frameUpdateListener);
    }

    @API
    public <E extends Raum> void addCollisionListener(CollisionListener<E> listener, Raum actor, E collider) {
        WorldHandler.spezifischesKollisionsReagierbarEingliedern(listener, actor, collider);
    }

    @API
    public void addCollisionListener(CollisionListener<Raum> listener, Raum actor) {
        WorldHandler.allgemeinesKollisionsReagierbarEingliedern(listener, actor);
    }

    @Override
    public void onKeyDown(int key) {
        for (KeyListener listener : keyListeners) {
            listener.onKeyDown(key);
        }
    }

    @Override
    public void onKeyUp(int key) {
        for (KeyListener listener : keyListeners) {
            listener.onKeyUp(key);
        }
    }

    @Override
    public void onMouseDown(Punkt position, MouseButton button) {
        for (MouseClickListener listener : mouseClickListeners) {
            listener.onMouseDown(position, button);
        }
    }

    @Override
    public void onMouseUp(Punkt position, MouseButton button) {
        for (MouseClickListener listener : mouseClickListeners) {
            listener.onMouseUp(position, button);
        }
    }

    @API
    public Punkt getMousePosition() {
        Point mouse = Game.getMousePosition();
        Punkt position = camera.getPosition();

        float rotation = camera.getRotation();

        float mx = mouse.x;
        float my = mouse.y;

        return new Punkt(
                position.x + (((float) Math.cos(rotation) * mx - (float) Math.sin(rotation) * my)) / camera.getZoom(),
                position.y + (((float) Math.sin(rotation) * mx + (float) Math.cos(rotation) * my)) / camera.getZoom()
        );
    }
}
