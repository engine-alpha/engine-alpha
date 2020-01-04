/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2019 Michael Andonie and contributors.
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

public class CarDemo extends ShowcaseDemo implements FrameUpdateListener {

    public static final float WHEEL_FRICTION = .8f;
    public static final float WHEEL_RESTITUTION = .6f;
    public static final Color WHEEL_COLOR = Color.GRAY;

    public static final int MOTOR_LIMIT = 500;

    public static final float GROUND_RESTITUTION = .6f;
    public static final float GROUND_FRICTION = .8f;

    public static void main(String[] args) {
        Game.setDebug(true);
        Game.start(Showcases.WIDTH, Showcases.HEIGHT, new CarDemo(null));
    }

    private Rectangle carBody;
    private Actor wheelFront;
    private Actor wheelBack;
    private PrismaticJoint springFront;
    private PrismaticJoint springBack;
    private RevoluteJoint motorFront;
    private RevoluteJoint motorBack;

    public CarDemo(Scene parent) {
        super(parent);

        Rectangle ground = new Rectangle(400, 1);
        ground.setPosition(-200, -11);
        ground.setColor(Color.YELLOW);
        ground.setBodyType(BodyType.STATIC);
        ground.setFriction(GROUND_FRICTION);
        ground.setRestitution(GROUND_RESTITUTION);

        for (int i = 1; i < 10; i++) {
            float offset = 180;
            float height = (float) Math.random() * 2;

            for (int j = 0; j < 20; j += 1) {
                Polygon g = new Polygon(new Vector(i * 20 + j, -10), new Vector(i * 20 + j + 1, -10), new Vector(i * 20 + j + 1, -10 + Math.cos(Math.toRadians((j + 1) * 18 + offset)) * height + height), new Vector(i * 20 + j, -10 + Math.cos(Math.toRadians(j * 18 + offset)) * height + height));

                g.setFriction(.5f);
                g.setBodyType(BodyType.STATIC);
                g.setColor(Color.BLUE);
                g.setFriction(GROUND_FRICTION);
                g.setRestitution(GROUND_RESTITUTION);
                add(g);
            }
        }

        carBody = new Rectangle(4, 1.2f);
        carBody.setPosition(-2, -7.5f);
        carBody.setBodyType(BodyType.DYNAMIC);
        carBody.setLayerPosition(2);
        carBody.setDensity(50);
        carBody.setBorderRadius(.5f);

        Rectangle axleFront = new Rectangle(.2f, 1);
        axleFront.setCenter(1.3f, -7.6f);
        axleFront.setBodyType(BodyType.DYNAMIC);
        axleFront.setColor(Color.GRAY);
        axleFront.setBorderRadius(.5f);
        springFront = axleFront.createPrismaticJoint(carBody, axleFront.getCenterRelative().add(0, axleFront.getHeight() / 2), 90);
        springFront.setLimits(-.5f, .25f);

        wheelFront = new Image("game-assets/car/wheel.png", 1, 1);
        wheelFront.setShape(() -> ShapeBuilder.createCircleShape(.5f, .5f, .5f));
        wheelFront.setCenter(1.3f, -8);
        wheelFront.setDensity(60);
        wheelFront.setBodyType(BodyType.DYNAMIC);
        wheelFront.setFriction(WHEEL_FRICTION);
        wheelFront.setRestitution(WHEEL_RESTITUTION);
        motorFront = wheelFront.createRevoluteJoint(axleFront, wheelFront.getCenterRelative());
        motorFront.setMaximumMotorTorque(MOTOR_LIMIT);

        Rectangle axleBack = new Rectangle(.2f, 1);
        axleBack.setCenter(-1.3f, -7.6f);
        axleBack.setBodyType(BodyType.DYNAMIC);
        axleBack.setColor(Color.GRAY);
        axleBack.setBorderRadius(.5f);
        springBack = axleBack.createPrismaticJoint(carBody, axleBack.getCenterRelative().add(0, axleBack.getHeight() / 2), 90);
        springBack.setLimits(-.5f, .25f);

        wheelBack = new Image("game-assets/car/wheel.png", 1, 1);
        wheelBack.setShape(() -> ShapeBuilder.createCircleShape(.5f, .5f, .5f));
        wheelBack.setCenter(-1.3f, -8);
        wheelBack.setDensity(60);
        wheelBack.setBodyType(BodyType.DYNAMIC);
        wheelBack.setFriction(WHEEL_FRICTION);
        wheelBack.setRestitution(WHEEL_RESTITUTION);
        motorBack = wheelBack.createRevoluteJoint(axleBack, wheelBack.getCenterRelative());
        motorBack.setMaximumMotorTorque(MOTOR_LIMIT);

        add(ground);
        add(carBody);
        add(axleFront);
        add(axleBack);
        add(wheelFront);
        add(wheelBack);

        carBody.addCollisionListener(wheelFront, CollisionEvent::ignoreCollision);
        carBody.addCollisionListener(wheelBack, CollisionEvent::ignoreCollision);

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
            motorFront.setMotorSpeed(right ? 10 : -10);
            motorBack.setMotorSpeed(right ? 10 : -10);
        } else {
            motorFront.setMotorEnabled(false);
            motorBack.setMotorEnabled(false);
        }

        // Reibung der Radachse, damit Auto nicht endlos weiterfährt
        wheelFront.applyTorque(-150 * wheelFront.getAngularVelocity());
        wheelBack.applyTorque(-150 * wheelBack.getAngularVelocity());

        // TODO Remove in real game
        if (Game.isKeyPressed(KeyEvent.VK_SPACE)) {
            carBody.applyImpulse(new Vector(0, 100));
        }

        // Federeffekt für die Achsen
        float springFrontTranslation = springFront.getTranslation();
        springFront.setMotorSpeed((springFront.getMotorSpeed() - 2 * springFrontTranslation) * .5f);
        springFront.setMaximumMotorForce(Math.abs(5000 * springFrontTranslation));

        float springBackTranslation = springBack.getTranslation();
        springBack.setMotorSpeed((springBack.getMotorSpeed() - 2 * springBackTranslation) * .5f);
        springBack.setMaximumMotorForce(Math.abs(5000 * springBackTranslation));

        // Stabilisierung der Rotation des Autos
        carBody.applyTorque(-4000 * carBody.getAngularVelocity());
    }
}
