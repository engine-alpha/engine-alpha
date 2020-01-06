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
import ea.internal.FixtureBuilder;

import java.awt.Color;
import java.awt.event.KeyEvent;

import static ea.Factory.vector;
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

    private CarBody carBody;
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

        delay(.2f, () -> blender.animateOpacity(.3f, 0));

        Layer background = new Layer();
        background.setLayerPosition(-1);
        background.setParallaxPosition(.5f, -.025f);

        for (int i = -200; i < 200; i += 10) {
            background.add(createBackgroundTile(i));
        }

        addLayer(background);

        createGround(-70, -49).setColor(new Color(200, 104, 73));

        Actor left = createGround(-50, -20);
        Actor middle = createGround(-10, 70);
        Actor right = createGround(85, 170);

        createGround(169, 200).setColor(new Color(200, 104, 73));

        createRope(-20, -10, left, middle);
        createRope(70, 85, middle, right);

        createHill(5, range(1, 2));
        createHill(25, range(1, 2));
        createHill(45, range(1, 2));

        carBody = new CarBody(0, -8f);

        wheelFront = new Wheel(1.36f, -8.75f, new Axle(1.36f, -8.6f, carBody));
        wheelBack = new Wheel(-1, -8.75f, new Axle(-1, -8.6f, carBody));

        // Wheels automatically add axes, and axes add the car body
        add(wheelFront, wheelBack);

        setGravity(vector(0, -9.81f));

        getCamera().setZoom(ZOOM);
        getCamera().setFocus(carBody);
        getCamera().setOffset(vector(0, 3));
    }

    private Actor createBackgroundTile(int x) {
        Image image = new Image("game-assets/car/background-color-grass.png", 10, 10);
        image.setPosition(x, -7);
        image.setGravityScale(0);

        return image;
    }

    private void createRope(int startX, int endX, Actor left, Actor right) {
        int length = (endX - startX);

        RopeSegment[] rope = new RopeSegment[length];
        for (int i = 0; i < length; i++) {
            rope[i] = new RopeSegment(.8f, 0.2f);
            rope[i].setPosition(startX + i + 0.1f, -10.2f);
            rope[i].setColor(new Color(119, 82, 54));
            rope[i].setBodyType(BodyType.DYNAMIC);
            rope[i].setDensity(150);
            rope[i].setFriction(GROUND_FRICTION);
            rope[i].setRestitution(GROUND_RESTITUTION);
            rope[i].setBorderRadius(.5f);

            if (i == 0) {
                rope[0].createRevoluteJoint(left, vector(-.1f, .2f)).setLimits(0, 0.1f);
            } else {
                if (i == length - 1) {
                    rope[length - 1].createRevoluteJoint(right, vector(.9f, .2f)).setLimits(0, 0.1f);
                }

                rope[i - 1].createRevoluteJoint(rope[i], vector(.9f, .2f)).setLimits(0, 0.1f);
            }
        }

        add(rope);
    }

    private Ground createGround(float startX, float endX) {
        Ground ground = new Ground(startX, endX);

        add(ground);

        return ground;
    }

    private void createHill(float x, float height) {
        float offset = 180;

        for (int j = 0; j < 40 - 1; j += 1) {
            Polygon ground = new HillSegment(vector(x + j / 2f, -10), vector(x + j / 2f + 1, -10), vector(x + (j + 1) / 2f, -10 + Math.cos(Math.toRadians(((j + 1) / 2f) * 18 + offset)) * height + height), vector(x + j / 2f, -10 + Math.cos(Math.toRadians(j / 2f * 18 + offset)) * height + height));
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

        if (carBody.getCenter().getX() < -65) {
            carBody.applyForce(vector(10000 * (-65 - carBody.getCenter().getX()), 0), carBody.getCenter());
        } else if (carBody.getCenter().getX() > 195) {
            carBody.applyForce(vector(10000 * (195 - carBody.getCenter().getX()), 0), carBody.getCenter());
        }

        if (carBody.getCenter().getY() < -20) {
            Game.transitionToScene(new CarDemo(null));
        }
    }

    private static Actor createParticle(float size, Vector center, Color initialColor, Vector impulse) {
        Circle particle = new Circle(size);
        particle.setBodyType(BodyType.PARTICLE);
        particle.setLayerPosition(2);
        particle.setColor(initialColor);
        particle.setCenter(center);
        particle.animateParticle(range(.1f, 3f));
        particle.animateColor(range(.3f, .6f), Color.BLACK);
        particle.applyImpulse(impulse);
        particle.setGravityScale(1);
        particle.setLinearDamping(range(18, 22));
        particle.setLayerPosition(-1);

        return particle;
    }

    private static Actor createSplitter(Vector center) {
        Polygon splitter = new Polygon(vector(0, 0), vector(0.15f, 0), vector(0.15f, 0.05f));
        splitter.setBodyType(BodyType.PARTICLE);
        splitter.rotateBy(range(0, 360));
        splitter.setLayerPosition(2);
        splitter.setColor(new Color(119, 82, 54));
        splitter.setCenter(center.add(range(-.2f, .2f), range(-.2f, .2f)));
        splitter.animateParticle(range(.1f, 3f));
        splitter.setGravityScale(1);
        splitter.setLinearDamping(range(18, 22));
        splitter.setLayerPosition(-1);

        return splitter;
    }

    private static Runnable createSplitterEmitter(Actor actor) {
        return () -> {
            for (CollisionEvent<Actor> collision : actor.getCollisions()) {
                if (collision.getColliding() instanceof Wood && actor.getVelocity().getLength() > 1) {
                    for (Vector point : collision.getPoints()) {
                        actor.getLayer().add(createSplitter(point));
                    }
                }
            }
        };
    }

    private static class Ground extends Rectangle implements Mud {
        public Ground(float startX, float endX) {
            super(endX - startX, 10);
            setPosition(startX, -20);
            setColor(GROUND_COLOR);
            setBodyType(BodyType.STATIC);
            setFriction(GROUND_FRICTION);
            setRestitution(GROUND_RESTITUTION);
            setDensity(150);
            setBorderRadius(.1f);
        }
    }

    private static class HillSegment extends Polygon implements Mud {
        public HillSegment(Vector... vectors) {
            super(vectors);
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

    private static class Wheel extends Image {
        private RevoluteJoint motor;

        public Wheel(float cx, float cy, Axle axle) {
            super("game-assets/car/wheel-back.png", 1.4f, 1.4f);

            setFixture(() -> FixtureBuilder.createCircleShape(.7f, .7f, .7f));
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

            repeat(.025f, () -> {
                for (CollisionEvent<Actor> collision : getCollisions()) {
                    if (collision.getColliding() instanceof Mud) {
                        float velocity = getVelocity().getLength();
                        float overtwist = abs(getAngularVelocity() * (float) Math.PI * 2 * 0.7f) / velocity;

                        boolean slowMoving = abs(getVelocity().getX()) < 0.5f && abs(getAngularVelocity()) < 0.3f;
                        if (overtwist > 0.95 && overtwist < 1.05 || slowMoving) {
                            continue;
                        }

                        Vector impulse = collision.getTangentNormal() //
                                .rotate(90) //
                                .multiply(min(max(-1, overtwist - 1), 1));

                        for (Vector point : collision.getPoints()) {
                            float size = range(0.05f, .15f);
                            Vector center = point.add(point.getDistance(getCenter()).multiply(size));
                            Color color = ((Mud) collision.getColliding()).getColor();
                            getLayer().add(createParticle(size, center, color, impulse.rotate(range(-15, 15))));
                        }
                    }
                }
            });

            repeat(.25f, createSplitterEmitter(this));
        }

        public void setMotorSpeed(int speed) {
            motor.setMotorSpeed(speed);
        }

        public void setMotorEnabled(boolean enabled) {
            motor.setMotorEnabled(enabled);
        }
    }

    private static class CarBody extends Image {
        public CarBody(float cx, float cy) {
            super("game-assets/car/truck-240px.png", 4, 1.2f);

            setCenter(cx, cy);
            setBodyType(BodyType.DYNAMIC);
            setDensity(100);
            setAngularDamping(0.3f);
            setFriction(0.5f);
            setShapes("R0,.45,2,.45&P2,1.2,2.6,1.15,3.8,0.8,3.95,0.45,2,0.45&R1,0,2,0.6");

            repeat(.05f, () -> {
                if (getVelocity().getLength() < 0.1f) {
                    return;
                }

                for (CollisionEvent<Actor> collision : getCollisions()) {
                    if (collision.getColliding() instanceof Mud) {
                        for (Vector point : collision.getPoints()) {
                            float size = range(0.05f, .15f);
                            Vector impulse = vector(range(-1f, 1f), range(-1f, 1f));
                            getLayer().add(createParticle(size, point, Color.YELLOW, impulse));
                        }
                    }
                }
            });

            repeat(.25f, createSplitterEmitter(this));
        }
    }

    private static class RopeSegment extends Rectangle implements Wood {
        public RopeSegment(float width, float height) {
            super(width, height);
        }
    }

    private interface Mud {
        Color getColor();
    }

    private interface Wood {
        // marker
    }
}
