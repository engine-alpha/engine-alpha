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
import ea.animation.ValueAnimator;
import ea.animation.interpolation.ReverseEaseFloat;
import ea.handle.Physics;
import ea.input.KeyListener;
import ea.particle.ParticleEmitter;

import java.awt.*;
import java.awt.event.KeyEvent;

@SuppressWarnings ( "MagicNumber" )
public class Particles extends ea.Scene implements KeyListener {
    public static final int WIDTH = 800, HEIGHT = 600;

    public static void main(String[] args) {
        EngineAlpha.setVerbose(true);
        Game.setDebug(true);
        Game.start(WIDTH, HEIGHT, new Particles());
    }

    /**
     * Startet ein Sandbox-Fenster.
     */
    private Particles() {
        Game.setTitle("Marbles");

        Rectangle left = new Rectangle(this, 200, 10);
        left.position.set(-WIDTH / 6 - 50, 50);
        add(left);

        left.setColor(Color.white);
        left.position.rotate((float) Math.toRadians(21));

        Rectangle right = new Rectangle(this, 200, 10);
        right.position.set(+WIDTH / 6, 0);
        add(right);

        right.setColor(Color.white);
        right.position.rotate((float) Math.toRadians(-45));

        this.addKeyListener(this);

        addFrameUpdateListener(new PeriodicTask(1000) {
            @Override
            public void run() {
                Circle k = new Circle(Particles.this, 6) {
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

                k.position.set(getMousePosition());
                k.setColor(Color.white);
                add(k);

                k.physics.setType(Physics.Type.DYNAMIC);
                k.physics.setGravity(new Vector(0, 10));
            }
        });

        left.physics.setType(Physics.Type.STATIC);
        right.physics.setType(Physics.Type.STATIC);
        left.physics.setElasticity(.9f);
        right.physics.setElasticity(.9f);

        Rectangle r1 = new Rectangle(this, WIDTH, 10);
        r1.position.set(-WIDTH / 2, -HEIGHT / 2);

        Rectangle r2 = new Rectangle(this, 10, HEIGHT);
        r2.position.set(-WIDTH / 2, -HEIGHT / 2);

        Rectangle r3 = new Rectangle(this, WIDTH, 10);
        r3.position.set(-WIDTH / 2, HEIGHT / 2 - 10);

        Rectangle r4 = new Rectangle(this, 10, HEIGHT);
        r4.position.set(WIDTH / 2 - 10, -HEIGHT / 2);

        add(r1, r2, r3, r4);

        r1.physics.setType(Physics.Type.STATIC);
        r2.physics.setType(Physics.Type.STATIC);
        r3.physics.setType(Physics.Type.STATIC);
        r4.physics.setType(Physics.Type.STATIC);

        r1.setColor(Color.yellow);
        r2.setColor(Color.yellow);
        r3.setColor(Color.yellow);
        r4.setColor(Color.yellow);

        r3.addCollisionListener((event) -> this.remove(event.getColliding()));

        this.animateCamera();
        this.addFrameUpdateListener(new ValueAnimator<>(5000, left.position::setX, new ReverseEaseFloat(left.position.getX(), left.position.getX() + 200), ValueAnimator.Mode.REPEATED));
    }

    private void animateCamera() {
        this.addFrameUpdateListener(new ValueAnimator<>(400, getCamera()::rotateTo, new ReverseEaseFloat(0, .002f + (float) Math.random() * 0.002f)).addCompletionListener(
                (value) -> this.addFrameUpdateListener(new ValueAnimator<>(350, getCamera()::rotateTo, new ReverseEaseFloat(0, -.002f + (float) Math.random() * -0.002f)).addCompletionListener((dummy) -> this.animateCamera()))
        ));
    }

    @Override
    public void onKeyDown(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                getCamera().move(-30, 0);
                break;

            case KeyEvent.VK_RIGHT:
                getCamera().move(30, 0);
                break;

            case KeyEvent.VK_UP:
                getCamera().move(0, -30);
                break;

            case KeyEvent.VK_DOWN:
                getCamera().move(0, 30);
                break;

            case KeyEvent.VK_R: // RESET
                Game.transitionToScene(new Particles());
                break;

            case KeyEvent.VK_D: // Toggle Debug
                Game.setDebug(!Game.isDebug());
                break;

            case KeyEvent.VK_1: // Zoom Out
                getCamera().setZoom(getCamera().getZoom() * .9f);
                break;

            case KeyEvent.VK_2: // Zoom In
                getCamera().setZoom(getCamera().getZoom() * 1.1f);
                break;
        }
    }

    @Override
    public void onKeyUp(KeyEvent e) {
        // do nothing
    }
}