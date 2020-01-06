/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2020 Michael Andonie and contributors.
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

package ea.example.showcase.car;

import ea.FrameUpdateListener;
import ea.Game;
import ea.Scene;
import ea.Vector;
import ea.actor.*;
import ea.collision.CollisionEvent;
import ea.example.showcase.ShowcaseDemo;
import ea.example.showcase.Showcases;
import ea.internal.ShapeBuilder;

import java.awt.Color;
import java.awt.event.KeyEvent;

import static ea.Random.range;
import static java.lang.Math.*;

public class CarDemo extends ShowcaseDemo implements FrameUpdateListener {

    public static final float GROUND_FRICTION = .6f;
    public static final float GROUND_RESTITUTION = .3f;

    public static final int MOTOR_SPEED = 80;
    public static final Color GROUND_COLOR = new Color(85, 86, 81);

    public static void main(String[] args) {
        Game.setDebug(true);
        Game.start(Showcases.WIDTH, Showcases.HEIGHT, new CarDemo(null));
    }

    private Wheel wheelFront;
    private Wheel wheelBack;

    public CarDemo(Scene parent) {
        super(parent);

        setBackgroundColor(new Color(112, 187, 250));

        Rectangle ground = new Rectangle(400, 10);
        ground.setPosition(-200, -20);
        ground.setColor(GROUND_COLOR);
        ground.setBodyType(BodyType.STATIC);
        ground.setFriction(GROUND_FRICTION);
        ground.setRestitution(GROUND_RESTITUTION);
        ground.setDensity(50);
        add(ground);

        for (int i = 1; i < 30; i++) {
            float offset = 180;
            float height = 1 + (float) Math.random() * 2;

            for (int j = 0; j < 20; j += 1) {
                Polygon g = new Polygon(new Vector(i * 20 + j, -10), new Vector(i * 20 + j + 1, -10), new Vector(i * 20 + j + 1, -10 + Math.cos(Math.toRadians((j + 1) * 18 + offset)) * height + height), new Vector(i * 20 + j, -10 + Math.cos(Math.toRadians(j * 18 + offset)) * height + height));

                g.setBodyType(BodyType.STATIC);
                g.setColor(GROUND_COLOR);
                g.setFriction(GROUND_FRICTION);
                g.setRestitution(GROUND_RESTITUTION);
                g.setDensity(50);
                add(g);
            }
        }

        CarBody carBody = new CarBody(0, -8f);

        wheelFront = new Wheel(1.36f, -8.75f, new Axle(1.36f, -8.6f, carBody));
        wheelBack = new Wheel(-1, -8.75f, new Axle(-1, -8.6f, carBody));

        // Wheels automatically add axes, and axes add the car body
        add(wheelFront, wheelBack);

        setGravity(new Vector(0, -9.81f));

        getCamera().setZoom(60);
        getCamera().setFocus(carBody);
        getCamera().setOffset(new Vector(0, 3));
    }

    @Override
    public void onFrameUpdate(float deltaSeconds) {
        boolean left = Game.isKeyPressed(KeyEvent.VK_J);
        boolean right = Game.isKeyPressed(KeyEvent.VK_L);

        // Antriebssteuerung
        if (left ^ right) {
            wheelFront.setMotorSpeed(right ? MOTOR_SPEED : -MOTOR_SPEED);
            wheelBack.setMotorSpeed(right ? MOTOR_SPEED : -MOTOR_SPEED);
        } else if (Game.isKeyPressed(KeyEvent.VK_SPACE)) {
            wheelFront.setMotorSpeed(0);
            wheelBack.setMotorSpeed(0);
        } else {
            wheelFront.setMotorEnabled(false);
            wheelBack.setMotorEnabled(false);
        }
    }

    private static class Axle extends Rectangle implements FrameUpdateListener {
        private PrismaticJoint spring;
        private CarBody carBody;

        public Axle(float cx, float cy, CarBody carBody) {
            super(.2f, .9f);

            setCenter(cx, cy);
            setBodyType(BodyType.DYNAMIC);
            setColor(new Color(255, 255, 255, 0));
            setDensity(50);

            this.carBody = carBody;

            spring = createPrismaticJoint(carBody, getCenterRelative().add(0, getHeight() / 2), 90);
            spring.setLimits(-.15f, .15f);

            addMountListener(() -> getLayer().add(carBody));
        }

        @Override
        public void onFrameUpdate(float deltaSeconds) {
            // Federeffekt für die Achsen
            float translation = spring.getTranslation();
            spring.setMotorSpeed((float) Math.sin(min(max(-0.15f, translation), 0.15f) / .15 * Math.PI / 2) * -.3f);
            spring.setMaximumMotorForce(5000);
        }

        public CarBody getCarBody() {
            return carBody;
        }
    }

    private static class Wheel extends Image implements FrameUpdateListener {
        private RevoluteJoint motor;
        private float particleTimeRemaining;

        public Wheel(float cx, float cy, Axle axle) {
            super("game-assets/car/wheel-back.png", 1.4f, 1.4f);

            setShape(() -> ShapeBuilder.createCircleShape(.7f, .7f, .7f));
            setCenter(cx, cy);
            setDensity(100);
            setBodyType(BodyType.DYNAMIC);
            setFriction(.5f);
            setRestitution(.2f);
            setDensity(150);
            setAngularDamping(1);
            setLayerPosition(2);

            motor = createRevoluteJoint(axle, getCenterRelative());
            motor.setMaximumMotorTorque(5000);

            addMountListener(() -> getLayer().add(axle));
            addCollisionListener(axle.getCarBody(), CollisionEvent::ignoreCollision);
        }

        public void setMotorSpeed(int speed) {
            motor.setMotorSpeed(speed);
        }

        public void setMotorEnabled(boolean enabled) {
            motor.setMotorEnabled(enabled);
        }

        @Override
        public void onFrameUpdate(float deltaSeconds) {
            particleTimeRemaining -= deltaSeconds;
            if (particleTimeRemaining > 0) {
                return;
            }

            while (particleTimeRemaining < 0) {
                particleTimeRemaining += .025f;

                for (CollisionEvent<Actor> collision : getCollisions()) {
                    if (collision.isIgnored()) {
                        continue;
                    }

                    float velocity = getVelocity().getLength();
                    float overtwist = abs(getAngularVelocity() * (float) Math.PI * 2 * 0.7f) / velocity;

                    if (overtwist > 0.95 && overtwist < 1.05 || abs(getVelocity().getX()) < 0.001f && getAngularVelocity() < 0.01f) {
                        continue;
                    }

                    Vector impulse = collision.getTangentNormal() //
                            .rotate(90) //
                            .multiply(-0.3f * min(max(-1, 1 - overtwist), 1));

                    collision.getPoints().forEach((point) -> {
                        float size = range(0.05f, .15f);

                        Circle particle = new Circle(size);
                        particle.setCenter(point.add(point.getDistance(getCenter()).multiply(size)));
                        particle.setBodyType(BodyType.PARTICLE);
                        particle.setColor(GROUND_COLOR);
                        particle.setLayerPosition(2);
                        particle.animateParticle(range(.1f, 3f));
                        particle.animateColor(range(.3f, .6f), Color.BLACK);
                        particle.applyImpulse(impulse.rotate(range(-15, 15)));
                        particle.setGravityScale(1);
                        particle.setLinearDamping(range(18, 22));
                        particle.setLayerPosition(-1);

                        getLayer().add(particle);
                    });
                }
            }
        }
    }

    private static class CarBody extends Image {
        public CarBody(float cx, float cy) {
            super("game-assets/car/truck.png", 4, 1.2f);

            setCenter(cx, cy);
            setBodyType(BodyType.DYNAMIC);
            setDensity(100);
            setAngularDamping(0.3f);
        }
    }
}
