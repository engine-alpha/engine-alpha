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

package ea.example.showcase;

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
public class Particles extends ShowcaseDemo implements KeyListener {
    public static void main(String[] args) {
        Game.setDebug(true);
        Game.start(Showcases.WIDTH, Showcases.HEIGHT, new Particles(null));
    }

    /**
     * Startet ein Sandbox-Fenster.
     */
    public Particles(Scene parent) {
        super(parent);

        Game.setTitle("Marbles");

        Rectangle left = new Rectangle(this, 200, 10);
        left.position.set(-Showcases.WIDTH / 6 - 150, -50);
        add(left);

        left.setColor(Color.white);
        left.position.rotate((float) Math.toRadians(-21));

        Rectangle right = new Rectangle(this, 200, 10);
        right.position.set(+Showcases.WIDTH / 6, 0);
        add(right);

        right.setColor(Color.white);
        right.position.rotate((float) Math.toRadians(45));

        this.addKeyListener(this);

        addFrameUpdateListener(new PeriodicTask(1000, () -> createCircle(getMousePosition(), Color.YELLOW)));

        left.setBodyType(Physics.Type.STATIC);
        right.setBodyType(Physics.Type.STATIC);
        left.physics.setElasticity(.9f);
        right.physics.setElasticity(.9f);

        Rectangle r1 = new Rectangle(this, Showcases.WIDTH, 10);
        r1.position.set(-Showcases.WIDTH / 2, -Showcases.HEIGHT / 2);

        Rectangle r2 = new Rectangle(this, 10, Showcases.HEIGHT);
        r2.position.set(-Showcases.WIDTH / 2, -Showcases.HEIGHT / 2);

        Rectangle r3 = new Rectangle(this, Showcases.WIDTH, 10);
        r3.position.set(-Showcases.WIDTH / 2, Showcases.HEIGHT / 2 - 10);

        Rectangle r4 = new Rectangle(this, 10, Showcases.HEIGHT);
        r4.position.set(Showcases.WIDTH / 2 - 10, -Showcases.HEIGHT / 2);

        add(r1, r2, r3, r4);

        r1.setBodyType(Physics.Type.STATIC);
        r2.setBodyType(Physics.Type.STATIC);
        r3.setBodyType(Physics.Type.STATIC);
        r4.setBodyType(Physics.Type.STATIC);

        r1.setColor(Color.DARK_GRAY);
        r2.setColor(Color.DARK_GRAY);
        r3.setColor(Color.DARK_GRAY);
        r4.setColor(Color.DARK_GRAY);

        r1.addCollisionListener(event -> remove(event.getColliding()));

        setGravity(new Vector(0, -10));

        this.addFrameUpdateListener(new ValueAnimator<>(5000, left.position::setX, new ReverseEaseFloat(left.position.getX(), left.position.getX() + 200), ValueAnimator.Mode.REPEATED));
    }

    private void createCircle(Vector position, Color color) {
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

        k.position.set(position);
        k.setColor(color);
        add(k);

        k.setBodyType(Physics.Type.DYNAMIC);
    }

    @Override
    public void onKeyDown(KeyEvent e) {
        switch (e.getKeyCode()) {

            case KeyEvent.VK_1: // Zoom Out
                getCamera().setZoom(getCamera().getZoom() * .9f);
                break;

            case KeyEvent.VK_2: // Zoom In
                getCamera().setZoom(getCamera().getZoom() * 1.1f);
                break;

            case KeyEvent.VK_LESS:
                getCamera().rotate(0.1f);
                break;
        }
    }

    @Override
    public void onKeyUp(KeyEvent e) {
        // do nothing
    }
}