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
import ea.actor.Particle;
import ea.actor.Rectangle;
import ea.animation.ValueAnimator;
import ea.animation.interpolation.LinearInteger;
import ea.animation.interpolation.ReverseEaseFloat;
import ea.handle.Physics;
import ea.input.KeyListener;

import java.awt.*;
import java.awt.event.KeyEvent;

@SuppressWarnings ( "MagicNumber" )
public class Particles extends ea.Scene implements KeyListener {
    public static final int WIDTH = 800, HEIGHT = 600;

    public static void main(String[] args) {
        Game.start(WIDTH, HEIGHT, new Particles());
    }

    /**
     * Startet ein Sandbox-Fenster.
     */
    private Particles() {
        Game.setTitle("Marbles");

        Rectangle left = new Rectangle(this, 200, 10);
        left.position.set(-WIDTH / 6 - 150, -50);
        add(left);

        left.setColor(Color.white);
        left.position.rotate((float) Math.toRadians(-21));

        Rectangle right = new Rectangle(this, 200, 10);
        right.position.set(+WIDTH / 6, 0);
        add(right);

        right.setColor(Color.white);
        right.position.rotate((float) Math.toRadians(45));

        this.addKeyListener(this);

        addFrameUpdateListener(new PeriodicTask(1000, this::createCircle));

        left.setBodyType(Physics.Type.STATIC);
        right.setBodyType(Physics.Type.STATIC);
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

        r1.setBodyType(Physics.Type.STATIC);
        r2.setBodyType(Physics.Type.STATIC);
        r3.setBodyType(Physics.Type.STATIC);
        r4.setBodyType(Physics.Type.STATIC);

        r1.setColor(Color.yellow);
        r2.setColor(Color.yellow);
        r3.setColor(Color.yellow);
        r4.setColor(Color.yellow);

        r1.addCollisionListener(event -> remove(event.getColliding()));

        setGravity(new Vector(0, -10));

        addFrameUpdateListener(new PeriodicTask(1000, () -> {
            Particle particle = new Particle(Particles.this, Random.nextInteger(2) + 6, 3000);
            particle.position.set(Random.nextInteger(WIDTH) - WIDTH / 2, Random.nextInteger(HEIGHT) - HEIGHT / 2);
            particle.physics.applyImpulse(new Vector(.5f * ((float) Math.random() - .5f), .5f * ((float) Math.random() - .5f)));
            particle.setColor(Color.RED);
            particle.setBodyType(Physics.Type.DYNAMIC);
            particle.setLayer(-1);

            ValueAnimator<Integer> animator = new ValueAnimator<>(1500, yellow -> particle.setColor(new Color(255, yellow, 0)), new LinearInteger(0, 255));
            animator.addCompletionListener((value) -> removeFrameUpdateListener(animator));
            addFrameUpdateListener(animator);

            add(particle);
        }));

        this.addFrameUpdateListener(new ValueAnimator<>(5000, left.position::setX, new ReverseEaseFloat(left.position.getX(), left.position.getX() + 200), ValueAnimator.Mode.REPEATED));
    }

    private void createCircle() {
        Circle k = new Circle(Particles.this, 6);

        FrameUpdateListener emitter = new PeriodicTask(10, () -> {
            Particle particle = new Particle(Particles.this, 3, 500);
            particle.position.set(k.position.getCenter().subtract(new Vector(1, 1)));
            particle.physics.applyImpulse(new Vector(2 * ((float) Math.random() - .5f), 2 * ((float) Math.random() - .5f)));
            particle.setColor(Color.RED);
            particle.setBodyType(Physics.Type.DYNAMIC);
            particle.setLayer(-1);

            ValueAnimator<Integer> animator = new ValueAnimator<>(250, yellow -> particle.setColor(new Color(255, yellow, 0)), new LinearInteger(0, 255));
            animator.addCompletionListener((value) -> removeFrameUpdateListener(animator));
            addFrameUpdateListener(animator);

            add(particle);
        });

        addFrameUpdateListener(emitter);
        k.addDestructionListener(() -> removeFrameUpdateListener(emitter));

        k.position.set(getMousePosition());
        k.setColor(Color.white);
        add(k);

        k.setBodyType(Physics.Type.DYNAMIC);
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

            case KeyEvent.VK_LESS:
                getCamera().rotate(0.1f);
        }
    }

    @Override
    public void onKeyUp(KeyEvent e) {
        // do nothing
    }
}