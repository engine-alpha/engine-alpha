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

package ea.example.showcase.billard;

import ea.Game;
import ea.Random;
import ea.Scene;
import ea.Vector;
import ea.example.showcase.ShowcaseDemo;
import ea.example.showcase.Showcases;
import ea.input.KeyListener;

import java.awt.Color;
import java.awt.event.KeyEvent;

public class Billard extends ShowcaseDemo implements KeyListener {
    public static void main(String[] args) {
        Game.start(Showcases.WIDTH, Showcases.HEIGHT, new Billard(null));
    }

    private Ball whiteBall;

    public Billard(Scene parent) {
        super(parent);

        add(new Table().getActors());

        for (int i = 0; i < 10; i++) {
            Ball ball = new Ball();
            ball.position.set(calculatePosition(i));
            add(ball);
        }

        whiteBall = new Ball();
        whiteBall.setColor(Color.WHITE);
        whiteBall.position.set(-200, 0);

        add(whiteBall);

        getKeyListeners().add(this);
    }

    private Vector calculatePosition(int i) {
        switch (i) {
            case 0:
                return new Vector(0, 0);
            case 1:
                return new Vector(Ball.DIAMETER, +Ball.DIAMETER / 2);
            case 2:
                return new Vector(Ball.DIAMETER, -Ball.DIAMETER / 2);
            case 3:
                return new Vector(Ball.DIAMETER * 2, +Ball.DIAMETER);
            case 4:
                return new Vector(Ball.DIAMETER * 2, 0);
            case 5:
                return new Vector(Ball.DIAMETER * 2, -Ball.DIAMETER);
            case 6:
                return new Vector(Ball.DIAMETER * 3, +Ball.DIAMETER);
            case 7:
                return new Vector(Ball.DIAMETER * 3, +Ball.DIAMETER / 2);
            case 8:
                return new Vector(Ball.DIAMETER * 3, -Ball.DIAMETER / 2);
            case 9:
                return new Vector(Ball.DIAMETER * 3, -Ball.DIAMETER);
            default:
                throw new IllegalArgumentException("Invalid index");
        }
    }

    @Override
    public void onKeyDown(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            whiteBall.physics.applyImpulse(new Vector(1000, (Random.nextFloat() - .5f) * 100));
        }
    }
}
