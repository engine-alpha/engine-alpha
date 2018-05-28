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

package ea.example.particles;

import ea.*;
import ea.actor.Circle;
import ea.actor.Rectangle;
import ea.handle.Physics;
import ea.keyboard.Key;
import ea.particle.ParticleEmitter;

import java.awt.*;

public class Particles extends ea.Scene {
    public static final int WIDTH = 800, HEIGHT = 600;

    public static void main(String[] args) {
        EngineAlpha.setVerbose(true);
        Game.start(WIDTH, HEIGHT, new Particles());
    }

    /**
     * Startet ein Sandbox-Fenster.
     */
    private Particles() {
        Game.setTitle("Marbles");

        Rectangle left = new Rectangle(-WIDTH / 6 - 50, 50, 200, 10);
        add(left);

        left.setColor(Color.white);
        left.position.setRotation((float) Math.toRadians(21));

        Rectangle right = new Rectangle(+WIDTH / 6, 0, 200, 10);
        add(right);

        right.setColor(Color.white);
        right.position.setRotation((float) Math.toRadians(-45));

        addFrameUpdateListener(new PeriodicTask(1000) {
            @Override
            public void dispatch() {
                Circle k = new Circle(getMousePosition().x, getMousePosition().y, 6) {
                    private ParticleEmitter particles = new ParticleEmitter();
                    private boolean registered = false;

                    @Override
                    public void renderBasic(Graphics2D g, BoundingRechteck r) {
                        if (!registered) {
                            addFrameUpdateListener(particles);
                            registered = true;
                        }

                        particles.render(g);
                        particles.emit(
                                position.getX() + 3,
                                position.getY() + 3,
                                20 * ((float) Math.random() - .5f),
                                20 * ((float) Math.random() - .5f),
                                2,
                                300,
                                Color.red
                        );

                        super.renderBasic(g, r);
                    }
                };

                k.setColor(Color.white);
                add(k);

                k.physics.setType(Physics.Type.DYNAMISCH);
                k.physics.setGravity(new Vector(0, 10));
            }
        });

        left.physics.setType(Physics.Type.STATISCH);
        right.physics.setType(Physics.Type.STATISCH);
        left.physics.setElasticity(.9f);
        right.physics.setElasticity(.9f);

        Rectangle r1 = new Rectangle(-WIDTH / 2, -HEIGHT / 2, WIDTH, 10);
        Rectangle r2 = new Rectangle(-WIDTH / 2, -HEIGHT / 2, 10, HEIGHT);
        Rectangle r3 = new Rectangle(-WIDTH / 2, HEIGHT / 2 - 10, WIDTH, 10);
        Rectangle r4 = new Rectangle(WIDTH / 2 - 10, -HEIGHT / 2, 10, HEIGHT);
        add(r1, r2, r3, r4);

        r1.setColor(Color.yellow);
        r2.setColor(Color.yellow);
        r3.setColor(Color.yellow);
        r4.setColor(Color.yellow);

        r1.physics.setType(Physics.Type.STATISCH);
        r2.physics.setType(Physics.Type.STATISCH);
        r3.physics.setType(Physics.Type.STATISCH);
        r4.physics.setType(Physics.Type.STATISCH);

        addCollisionListener(this::remove, r3);
    }

    /**
     * Wird bei jedem Tastendruck aufgerufen.
     *
     * @param code Der Code der gedrückten Key.
     */
    @Override
    public void onKeyDown(int code) {
        super.onKeyDown(code);

        switch (code) {
            case Key.LINKS:
                getCamera().move(-30, 0);
                break;

            case Key.RECHTS:
                getCamera().move(30, 0);
                break;

            case Key.OBEN:
                getCamera().move(0, -30);
                break;

            case Key.UNTEN:
                getCamera().move(0, 30);
                break;

            case Key.R: // RESET
                Game.transitionToScene(new Particles());
                break;

            case Key.D: // Toggle Debug
                EngineAlpha.setDebug(!EngineAlpha.isDebug());
                break;

            case Key._1: // Zoom Out
                getCamera().setZoom(getCamera().getZoom() - 0.1f);
                break;

            case Key._2: // Zoom In
                getCamera().setZoom(getCamera().getZoom() + 0.1f);
                break;
        }
    }
}