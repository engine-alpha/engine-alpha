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

package ea.example.showcase.billard;

import ea.Game;
import ea.actor.Actor;
import ea.actor.Rectangle;
import ea.collision.CollisionListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Table {
    public static final float BORDER = 30;
    public static final float GAP = 20;
    public static final float DIAGONAL_GAP = (float) Math.sqrt(GAP * GAP / 2) * 2;

    private List<Actor> actors = new ArrayList<>();

    public Actor[] getActors() {
        return actors.toArray(new Actor[0]);
    }

    public Table() {
        Rectangle border = new Rectangle(2 * Edge.WIDTH + 2 * GAP + 2 * DIAGONAL_GAP + BORDER * 2, Edge.WIDTH + 2 * DIAGONAL_GAP + BORDER * 2);
        border.position.set(-Edge.WIDTH - GAP - DIAGONAL_GAP - BORDER, -Edge.WIDTH / 2 - DIAGONAL_GAP - BORDER);
        border.setColor(new Color(226, 228, 231));

        Rectangle background = new Rectangle(2 * Edge.WIDTH + 2 * GAP + 2 * DIAGONAL_GAP, Edge.WIDTH + 2 * DIAGONAL_GAP);
        background.position.set(-Edge.WIDTH - GAP - DIAGONAL_GAP, -Edge.WIDTH / 2 - DIAGONAL_GAP);
        background.setColor(new Color(68, 121, 43));

        actors.add(border);
        actors.add(background);

        createEdges();
        createHoles();
    }

    private void createHoles() {
        Hole hole;
        CollisionListener<Actor> collisionListener = (collisionEvent) -> {
            if (collisionEvent.getColliding() instanceof Ball) {
                Game.enqueue(() -> collisionEvent.getColliding().removeFromScene());
            }
        };

        // top left
        hole = new Hole(-Edge.WIDTH - GAP - DIAGONAL_GAP - Edge.HEIGHT / 2, Edge.WIDTH / 2 - Hole.RADIUS + DIAGONAL_GAP + Edge.HEIGHT / 2);
        hole.addCollisionListener(collisionListener);
        actors.add(hole);

        // top midle
        hole = new Hole(-Hole.RADIUS / 2, Edge.WIDTH / 2 - Hole.RADIUS + DIAGONAL_GAP + Hole.RADIUS / 2);
        hole.addCollisionListener(collisionListener);
        actors.add(hole);

        // top right
        hole = new Hole(+Edge.WIDTH + GAP + DIAGONAL_GAP - Hole.RADIUS + Edge.HEIGHT / 2, Edge.WIDTH / 2 - Hole.RADIUS + DIAGONAL_GAP + Edge.HEIGHT / 2);
        hole.addCollisionListener(collisionListener);
        actors.add(hole);

        // bottom left
        hole = new Hole(-Edge.WIDTH - GAP - DIAGONAL_GAP - Edge.HEIGHT / 2, -Edge.WIDTH / 2 - DIAGONAL_GAP - Edge.HEIGHT / 2);
        hole.addCollisionListener(collisionListener);
        actors.add(hole);

        // bottom midle
        hole = new Hole(-Hole.RADIUS / 2, -Edge.WIDTH / 2 - DIAGONAL_GAP - Hole.RADIUS / 2);
        hole.addCollisionListener(collisionListener);
        actors.add(hole);

        // bottom right
        hole = new Hole(+Edge.WIDTH + GAP + DIAGONAL_GAP - Hole.RADIUS + Edge.HEIGHT / 2, -Edge.WIDTH / 2 - DIAGONAL_GAP - Edge.HEIGHT / 2);
        hole.addCollisionListener(collisionListener);
        actors.add(hole);
    }

    private void createEdges() {
        Edge topLeft = new Edge(-GAP, +Edge.WIDTH / 2 + DIAGONAL_GAP);
        topLeft.position.rotate((float) Math.PI);
        actors.add(topLeft);

        Edge topRight = new Edge(Edge.WIDTH + GAP, +Edge.WIDTH / 2 + DIAGONAL_GAP);
        topRight.position.rotate((float) Math.PI);
        actors.add(topRight);

        Edge bottomLeft = new Edge(-GAP - Edge.WIDTH, -Edge.WIDTH / 2 - DIAGONAL_GAP);
        actors.add(bottomLeft);

        Edge bottomRight = new Edge(GAP, -Edge.WIDTH / 2 - DIAGONAL_GAP);
        actors.add(bottomRight);

        Edge left = new Edge(-Edge.WIDTH - GAP - DIAGONAL_GAP, +Edge.WIDTH / 2);
        left.position.rotate((float) -Math.PI / 2);
        actors.add(left);

        Edge right = new Edge(+Edge.WIDTH + GAP + DIAGONAL_GAP, -Edge.WIDTH / 2);
        right.position.rotate((float) Math.PI / 2);
        actors.add(right);
    }
}
