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

import ea.*;
import ea.actor.*;
import ea.collision.CollisionEvent;
import ea.example.showcase.ShowcaseDemo;
import ea.example.showcase.Showcases;
import ea.internal.ShapeBuilder;

import java.awt.Color;
import java.awt.event.KeyEvent;

public class CarDemo extends ShowcaseDemo implements FrameUpdateListener {

    public static final float GROUND_FRICTION = .95f;
    public static final float GROUND_RESTITUTION = .3f;

    public static final int MOTOR_SPEED = 80;

    public static void main(String[] args) {
        Game.setDebug(true);
        Game.start(Showcases.WIDTH, Showcases.HEIGHT, new CarDemo(null));
    }

    private Wheel wheelFront;
    private Wheel wheelBack;

    public CarDemo(Scene parent) {
        super(parent);

        Rectangle ground = new Rectangle(400, 1);
        ground.setPosition(-200, -11);
        ground.setColor(Color.YELLOW);
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
                g.setColor(Color.BLUE);
                g.setFriction(GROUND_FRICTION);
                g.setRestitution(GROUND_RESTITUTION);
                g.setDensity(50);
                add(g);
            }
        }

        CarBody carBody = new CarBody(0, -7.2f);

        wheelFront = new Wheel(1, -8, new Axle(1, -7.6f, carBody));
        wheelBack = new Wheel(-1, -8, new Axle(-1, -7.6f, carBody));

        // Wheels automatically add axes, and axes add the car body
        add(wheelFront, wheelBack);

        setGravity(new Vector(0, -9.81f));

        getCamera().setZoom(60);
        getCamera().setFocus(carBody);
    }

    @Override
    public void onFrameUpdate(float deltaSeconds) {
        boolean left = Game.isKeyPressed(KeyEvent.VK_J);
        boolean right = Game.isKeyPressed(KeyEvent.VK_L);

        // Antriebssteuerung
        if (left ^ right) {
            wheelFront.setMotorSpeed(right ? MOTOR_SPEED : -MOTOR_SPEED);
            wheelBack.setMotorSpeed(right ? MOTOR_SPEED : -MOTOR_SPEED);
        } else {
            wheelFront.setMotorEnabled(false);
            wheelBack.setMotorEnabled(false);
        }

        // Bremse
        if (Game.isKeyPressed(KeyEvent.VK_SPACE)) {
            wheelFront.setMotorSpeed(0);
            wheelBack.setMotorSpeed(0);
        }
    }

    private static class Axle extends Rectangle implements FrameUpdateListener {
        private PrismaticJoint spring;
        private CarBody carBody;

        public Axle(float cx, float cy, CarBody carBody) {
            super(.2f, 1);

            setCenter(cx, cy);
            setBodyType(BodyType.DYNAMIC);
            setColor(Color.GRAY);
            setBorderRadius(1);
            setDensity(50);

            this.carBody = carBody;

            spring = createPrismaticJoint(carBody, getCenterRelative(), 90);
            spring.setLimits(-.15f, .15f);

            addMountListener(() -> getLayer().add(carBody));
        }

        @Override
        public void onFrameUpdate(float deltaSeconds) {
            // Federeffekt für die Achsen
            float translation = spring.getTranslation();
            spring.setMotorSpeed((float) Math.sin(Math.min(Math.max(-0.15f, translation), 0.15f) / .15 * Math.PI / 2) * -.1f);
            spring.setMaximumMotorForce(Math.abs(5000));
        }

        public CarBody getCarBody() {
            return carBody;
        }
    }

    private static class Wheel extends Image implements FrameUpdateListener {
        private RevoluteJoint motor;

        public Wheel(float cx, float cy, Axle axle) {
            super("game-assets/car/wheel.png", 1, 1);

            setShape(() -> ShapeBuilder.createCircleShape(.5f, .5f, .5f));
            setCenter(cx, cy);
            setDensity(100);
            setBodyType(BodyType.DYNAMIC);
            setFriction(1);
            setRestitution(.2f);
            setDensity(150);

            motor = createRevoluteJoint(axle, getCenterRelative());
            motor.setMaximumMotorTorque(1000);

            addMountListener(() -> getLayer().add(axle));
            addCollisionListener(axle.getCarBody(), CollisionEvent::ignoreCollision);

            /*
            float overtwist = Math.abs(getAngularVelocity() * (float) Math.PI) / getVelocity().getLength();
                    if (true || overtwist > 1.01f || overtwist < 0.98f) {

                    }
             */
        }

        public void setMotorSpeed(int speed) {
            motor.setMotorSpeed(speed);
        }

        public void setMotorEnabled(boolean enabled) {
            motor.setMotorEnabled(enabled);
        }

        @Override
        public void onFrameUpdate(float deltaSeconds) {
            // Reibung der Radachse, damit Auto nicht endlos weiterfährt
            applyTorque(-10000 * getAngularVelocity() * deltaSeconds);

            for (CollisionEvent<Actor> collision : getCollisions()) {
                if (collision.isIgnored()) {
                    continue;
                }

                float velocity = getVelocity().getLength();
                float overtwist = Math.abs(getAngularVelocity() * (float) Math.PI) / velocity;

                if (overtwist > 0.97 && overtwist < 1.03) {
                    continue;
                }

                collision.getPoints().forEach((point) -> {
                    Circle particle = new Circle(0.1f);
                    particle.setCenter(point.add(point.getDistance(getCenter()).multiply(0.15f)));
                    particle.setColor(Color.GRAY);
                    particle.setLayerPosition(2);
                    particle.animateParticle(.5f);
                    particle.animateColor(.25f, Color.WHITE);
                    particle.applyImpulse(collision.getTangentNormal().rotate(90 * -Math.signum(1 - overtwist) + Random.getFloat(-5, 5)).multiply(-0.05f * Math.min(Math.max(-1, 1 - overtwist), 1)));

                    getLayer().add(particle);
                });
            }
        }
    }

    private static class CarBody extends Rectangle implements FrameUpdateListener {
        public CarBody(float cx, float cy) {
            super(4, 1.2f);

            setCenter(cx, cy);
            setBodyType(BodyType.DYNAMIC);
            setDensity(50);
            setBorderRadius(.5f);
        }

        @Override
        public void onFrameUpdate(float deltaSeconds) {
            // Stabilisierung der Rotation des Autos
            applyTorque(-40000 * deltaSeconds * getAngularVelocity());
        }
    }
}
