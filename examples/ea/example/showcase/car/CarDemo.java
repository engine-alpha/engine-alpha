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
import ea.example.showcase.ShowcaseDemo;
import ea.example.showcase.Showcases;

import java.awt.Color;
import java.awt.event.KeyEvent;

public class CarDemo extends ShowcaseDemo implements FrameUpdateListener {
    public static void main(String[] args) {
        Game.setDebug(true);
        Game.start(Showcases.WIDTH, Showcases.HEIGHT, new CarDemo(null));
    }

    private Actor carBody;
    private PrismaticJoint springFront;
    private PrismaticJoint springBack;
    private RevoluteJoint motorFront;
    private RevoluteJoint motorBack;

    public CarDemo(Scene parent) {
        super(parent);

        Rectangle ground = new Rectangle(40, 1);
        ground.setPosition(-20, -11);
        ground.setColor(Color.YELLOW);
        ground.setBodyType(BodyType.STATIC);
        ground.setFriction(.5f);

        carBody = new Rectangle(4, 1);
        carBody.setPosition(-2, -8);
        carBody.setBodyType(BodyType.DYNAMIC);

        Rectangle axleFront = new Rectangle(.4f, .8f);
        axleFront.setCenter(1.5f, -7.5f);
        axleFront.setBodyType(BodyType.DYNAMIC);
        axleFront.setColor(Color.PINK);
        springFront = axleFront.createPrismaticJoint(carBody, axleFront.getCenterRelative(), 90);
        springFront.setLimits(-.5f, .5f);

        Circle wheelFront = new Circle(1);
        wheelFront.setCenter(1.5f, -8);
        wheelFront.setBodyType(BodyType.DYNAMIC);
        wheelFront.setColor(Color.GRAY);
        wheelFront.setFriction(.5f);
        motorFront = wheelFront.createRevoluteJoint(axleFront, wheelFront.getCenterRelative());
        motorFront.setMotorEnabled(true);
        motorFront.setMaxMotorForce(100);

        Rectangle axleBack = new Rectangle(.4f, .8f);
        axleBack.setCenter(-1.5f, -7.5f);
        axleBack.setBodyType(BodyType.DYNAMIC);
        axleBack.setColor(Color.PINK);
        springBack = axleBack.createPrismaticJoint(carBody, axleBack.getCenterRelative(), 90);
        springBack.setLimits(-.5f, .5f);

        Circle wheelBack = new Circle(1);
        wheelBack.setCenter(-1.5f, -8);
        wheelBack.setBodyType(BodyType.DYNAMIC);
        wheelBack.setColor(Color.GRAY);
        wheelBack.setFriction(.5f);
        motorBack = wheelBack.createRevoluteJoint(axleBack, wheelBack.getCenterRelative());
        motorBack.setMotorEnabled(true);
        motorBack.setMaxMotorForce(100);

        add(ground);
        add(carBody);
        add(axleFront);
        add(axleBack);
        add(wheelFront);
        add(wheelBack);

        setGravity(new Vector(0, -9f));

        add(new Text("Hello", 2));
    }

    @Override
    public void onFrameUpdate(float deltaSeconds) {
        boolean left = Game.isKeyPressed(KeyEvent.VK_J);
        boolean right = Game.isKeyPressed(KeyEvent.VK_L);

        if (left ^ right) {
            motorFront.setMotorSpeed(right ? 2 : -2);
            motorBack.setMotorSpeed(right ? 2 : -2);
        } else {
            motorFront.setMotorSpeed(0);
            motorBack.setMotorSpeed(0);
        }
    }
}
