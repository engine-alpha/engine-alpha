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

import static ea.Random.range;
import static java.lang.Math.*;

public class CarDemo extends ShowcaseDemo implements FrameUpdateListener {

    public static final float GROUND_FRICTION = .6f;
    public static final float GROUND_RESTITUTION = .3f;

    public static final int MOTOR_SPEED = 80;
    public static final Color GROUND_COLOR = new Color(85, 86, 81);
    public static final int ZOOM = 60;

    public static void main(String[] args) {
        Game.start(Showcases.WIDTH, Showcases.HEIGHT, new CarDemo(null));
    }

    private Wheel wheelFront;
    private Wheel wheelBack;

    public CarDemo(Scene parent) {
        super(parent);

        setBackgroundColor(new Color(207, 239, 252));

        Layer blend = new Layer();
        Rectangle blender = new Rectangle((float) Showcases.WIDTH / ZOOM, (float) Showcases.HEIGHT / ZOOM);
        blender.setColor(Color.BLACK);
        blender.setCenter(0, 0);
        blend.add(blender);
        blend.setParallaxRotation(0);
        blend.setParallaxPosition(0, 0);
        blend.setLayerPosition(10);
        addLayer(blend);

        delay(.5f, () -> blender.animateOpacity(.5f, 0));

        Layer background = new Layer();
        background.setLayerPosition(-1);
        background.setParallaxPosition(.5f, -.05f);

        for (int i = -200; i < 200; i+= 10) {
            background.add(createBackgroundTile(i));
        }

        addLayer(background);

        Actor left = createGround(-200, -20);
        Actor middle = createGround(-10, 70);
        Actor right = createGround(85, 170);

        createRope(-20, -10, left, middle);
        createRope(70, 85, middle, right);

        createHill(5, range(1, 2));
        createHill(25, range(1, 2));
        createHill(45, range(1, 2));

        CarBody carBody = new CarBody(0, -8f);

        wheelFront = new Wheel(1.36f, -8.75f, new Axle(1.36f, -8.6f, carBody));
        wheelBack = new Wheel(-1, -8.75f, new Axle(-1, -8.6f, carBody));

        // Wheels automatically add axes, and axes add the car body
        add(wheelFront, wheelBack);

        setGravity(new Vector(0, -9.81f));

        getCamera().setZoom(ZOOM);
        getCamera().setFocus(carBody);
        getCamera().setOffset(new Vector(0, 3));
    }

    private Actor createBackgroundTile(int x) {
        Image image = new Image("game-assets/car/background-color-grass.png", 10, 10);
        image.setPosition(x, -7);
        image.setGravityScale(0);

        return image;
    }

    private void createRope(int startX, int endX, Actor left, Actor right) {
        int length = (endX - startX);

        Rectangle[] rope = new Rectangle[length];
        for (int i = 0; i < length; i++) {
            rope[i] = new Rectangle(.8f, 0.2f);
            rope[i].setPosition(startX + i + 0.1f, -10.2f);
            rope[i].setColor(new Color(119, 82, 54));
            rope[i].setBodyType(BodyType.DYNAMIC);
            rope[i].setDensity(150);
            rope[i].setFriction(GROUND_FRICTION);
            rope[i].setRestitution(GROUND_RESTITUTION);
            rope[i].setBorderRadius(.5f);

            if (i == 0) {
                rope[0].createRevoluteJoint(left, new Vector(-.1f, .2f)).setLimits(0, 0.1f);
            } else {
                if (i == length - 1) {
                    rope[length - 1].createRevoluteJoint(right, new Vector(.9f, .2f)).setLimits(0, 0.1f);
                }

                rope[i - 1].createRevoluteJoint(rope[i], new Vector(.9f, .2f)).setLimits(0, 0.1f);
            }
        }

        add(rope);
    }

    private Actor createGround(float startX, float endX) {
        Rectangle ground = new Rectangle(endX - startX, 10);
        ground.setPosition(startX, -20);
        ground.setColor(GROUND_COLOR);
        ground.setBodyType(BodyType.STATIC);
        ground.setFriction(GROUND_FRICTION);
        ground.setRestitution(GROUND_RESTITUTION);
        ground.setDensity(150);
        ground.setBorderRadius(.1f);

        add(ground);

        return ground;
    }

    private void createHill(float x, float height) {
        float offset = 180;

        for (int j = 0; j < 40 - 1; j += 1) {
            Polygon ground = new Polygon(new Vector(x + j / 2f, -10), new Vector(x + j / 2f + 1, -10), new Vector(x + (j + 1) / 2f, -10 + Math.cos(Math.toRadians(((j + 1) / 2f) * 18 + offset)) * height + height), new Vector(x + j / 2f, -10 + Math.cos(Math.toRadians(j / 2f * 18 + offset)) * height + height));
            ground.moveBy(0, -0.01f);
            ground.setBodyType(BodyType.STATIC);
            ground.setColor(GROUND_COLOR);
            ground.setFriction(GROUND_FRICTION);
            ground.setRestitution(GROUND_RESTITUTION);
            ground.setDensity(50);

            add(ground);
        }
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

                    if (overtwist > 0.95 && overtwist < 1.05 || abs(getVelocity().getX()) < 0.5f && abs(getAngularVelocity()) < 0.3f) {
                        continue;
                    }

                    Vector impulse = collision.getTangentNormal() //
                            .rotate(90) //
                            .multiply(-1f * min(max(-1, 1 - overtwist), 1));

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
            super("game-assets/car/truck-240px.png", 4, 1.2f);

            setCenter(cx, cy);
            setBodyType(BodyType.DYNAMIC);
            setDensity(100);
            setAngularDamping(0.3f);
        }
    }
}
