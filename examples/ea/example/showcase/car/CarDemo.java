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

import ea.Game;
import ea.Scene;
import ea.Vector;
import ea.actor.BodyType;
import ea.actor.Circle;
import ea.actor.Rectangle;
import ea.example.showcase.ShowcaseDemo;
import ea.example.showcase.Showcases;

import java.awt.Color;

public class CarDemo extends ShowcaseDemo {
    public static void main(String[] args) {
        Game.setDebug(true);
        Game.start(Showcases.WIDTH, Showcases.HEIGHT, new CarDemo(null));
    }

    public CarDemo(Scene parent) {
        super(parent);

        Rectangle ground = new Rectangle(40, 1);
        ground.setPosition(-20, -11);
        ground.setColor(Color.YELLOW);
        ground.setBodyType(BodyType.STATIC);

        Rectangle carBody = new Rectangle(4, 1);
        carBody.setPosition(-2, -8);
        carBody.setBodyType(BodyType.DYNAMIC);

        Rectangle axleFront = new Rectangle(.4f, .8f);
        axleFront.setCenter(1.5f, -7.5f);
        axleFront.setBodyType(BodyType.DYNAMIC);
        axleFront.setColor(Color.PINK);
        axleFront.createPrismaticJoint(carBody, axleFront.getCenterRelative(), 90);

        Circle wheelFront = new Circle(1);
        wheelFront.setCenter(1.5f, -8);
        wheelFront.setBodyType(BodyType.DYNAMIC);
        wheelFront.setColor(Color.GRAY);
        wheelFront.createRevoluteJoint(axleFront, wheelFront.getCenterRelative());

        Rectangle axleBack = new Rectangle(.4f, .8f);
        axleBack.setCenter(-1.5f, -7.5f);
        axleBack.setBodyType(BodyType.DYNAMIC);
        axleBack.setColor(Color.PINK);
        axleBack.createPrismaticJoint(carBody, axleBack.getCenterRelative(), 90);

        Circle wheelBack = new Circle(1);
        wheelBack.setCenter(-1.5f, -8);
        wheelBack.setBodyType(BodyType.DYNAMIC);
        wheelBack.setColor(Color.GRAY);
        wheelBack.createRevoluteJoint(axleBack, wheelBack.getCenterRelative());

        add(ground);
        add(carBody);
        add(axleFront);
        add(axleBack);
        add(wheelFront);
        add(wheelBack);

        setGravity(new Vector(0, -9f));
    }
}
