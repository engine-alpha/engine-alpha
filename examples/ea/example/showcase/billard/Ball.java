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

import ea.FrameUpdateListener;
import ea.Vector;
import ea.actor.Circle;
import ea.handle.Physics;

import java.awt.*;

public class Ball extends Circle implements FrameUpdateListener {
    public static final float DIAMETER = 24;

    public Ball() {
        super(DIAMETER);

        setColor(Color.YELLOW);
        setBodyType(Physics.Type.DYNAMIC);

        physics.setFriction(.5f);
        physics.setElasticity(0);
    }

    @Override
    public void onFrameUpdate(int frameDuration) {
        if (physics.getVelocity().getLength() < 0.2f) {
            physics.setVelocity(Vector.NULL);
        } else {
            physics.applyForce(physics.getVelocity().negate().multiply(5));
            physics.setTorque(physics.getTorque() * 0.5f);
        }
    }
}
