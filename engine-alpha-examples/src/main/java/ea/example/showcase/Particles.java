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

import ea.FrameUpdateListener;
import ea.Game;
import ea.Scene;
import ea.Vector;
import ea.actor.BodyType;
import ea.actor.Circle;
import ea.actor.Rectangle;
import ea.animation.AnimationMode;
import ea.animation.ValueAnimator;
import ea.animation.interpolation.ReverseEaseFloat;
import ea.event.KeyListener;

import java.awt.Color;
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

        Rectangle left = new Rectangle(200, 10);
        left.setPosition(-Showcases.WIDTH / 6 - 150, -50);
        left.rotateBy(-21);
        left.setBodyType(BodyType.STATIC);
        left.setColor(Color.white);
        left.setRestitution(15f);
        add(left);

        Rectangle right = new Rectangle(200, 10);
        right.setPosition(+Showcases.WIDTH / 6, 0);
        right.rotateBy(45);
        right.setBodyType(BodyType.STATIC);
        right.setColor(Color.white);
        right.setRestitution(15f);
        add(right);

        addKeyListener(this);
        repeat(1, () -> createCircle(getMousePosition(), Color.YELLOW));

        Rectangle r1 = new Rectangle(Showcases.WIDTH, 10);
        r1.setPosition(-Showcases.WIDTH / 2, -Showcases.HEIGHT / 2);

        Rectangle r2 = new Rectangle(10, Showcases.HEIGHT);
        r2.setPosition(-Showcases.WIDTH / 2, -Showcases.HEIGHT / 2);

        Rectangle r3 = new Rectangle(Showcases.WIDTH, 10);
        r3.setPosition(-Showcases.WIDTH / 2, Showcases.HEIGHT / 2 - 10);

        Rectangle r4 = new Rectangle(10, Showcases.HEIGHT);
        r4.setPosition(Showcases.WIDTH / 2 - 10, -Showcases.HEIGHT / 2);

        add(r1, r2, r3, r4);

        r1.setBodyType(BodyType.STATIC);
        r2.setBodyType(BodyType.STATIC);
        r3.setBodyType(BodyType.STATIC);
        r4.setBodyType(BodyType.STATIC);

        r1.setColor(Color.DARK_GRAY);
        r2.setColor(Color.DARK_GRAY);
        r3.setColor(Color.DARK_GRAY);
        r4.setColor(Color.DARK_GRAY);

        r1.addCollisionListener((event) -> remove(event.getColliding()));

        setGravity(new Vector(0, -600));
        getCamera().setZoom(1);

        left.animateColor(5, Color.YELLOW);

        this.addFrameUpdateListener(new ValueAnimator<>(5, left::setX, new ReverseEaseFloat(left.getX(), left.getX() + 200), AnimationMode.REPEATED, this));
    }

    private void createCircle(Vector position, Color color) {
        Circle circle = new Circle(6);

        FrameUpdateListener emitter = repeat(0.01f, () -> {
            Circle particle = new Circle(3);
            particle.setPosition(circle.getCenter().subtract(new Vector(1, 1)));
            particle.setColor(Color.RED);
            particle.setLayerPosition(-1);
            particle.animateParticle(.5f);
            particle.animateColor(.25f, Color.YELLOW);
            particle.applyImpulse(new Vector(6000 * ((float) Math.random() - .5f), 6000 * ((float) Math.random() - .5f)));

            add(particle);
        });

        circle.setPosition(position);
        circle.setBodyType(BodyType.DYNAMIC);
        circle.setColor(color);
        circle.addFrameUpdateListener(emitter);

        add(circle);
    }

    @Override
    public void onKeyDown(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LESS) {
            getCamera().rotateBy(0.1f);
        }
    }
}