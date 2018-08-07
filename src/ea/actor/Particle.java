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

package ea.actor;

import ea.FrameUpdateListener;
import ea.Scene;
import ea.Vector;
import ea.handle.Physics;
import ea.internal.ano.API;
import ea.internal.phy.BodyHandler;
import ea.internal.phy.PhysikHandler;
import ea.internal.phy.WorldHandler;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;

import java.awt.*;

@API
public class Particle extends Circle implements FrameUpdateListener {
    private int life;
    private int age = 0;

    /**
     * Konstruktor.
     *
     * @param diameter Durchmesser des Kreises
     */
    public Particle(Scene scene, float diameter, int life) {
        super(scene, diameter);

        this.life = life;
    }

    @Override
    protected PhysikHandler createPhysicsHandler(Shape shape) {
        getScene().getWorldHandler().blockPPMChanges();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = Physics.Type.PASSIVE.convert();
        bodyDef.active = true;
        bodyDef.position.set(getScene().getWorldHandler().fromVektor(Vector.NULL));
        bodyDef.gravityScale = 0;

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0;
        fixtureDef.friction = 0;
        fixtureDef.restitution = 0.5f;
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = WorldHandler.CATEGORY_PARTICLE;
        fixtureDef.filter.maskBits = 0;

        return new BodyHandler(this, getScene().getWorldHandler(), bodyDef, fixtureDef, Physics.Type.PASSIVE, true);
    }

    @Override
    public void setBodyType(Physics.Type type) {
        super.setBodyType(type);

        getPhysicsHandler().getBody().m_fixtureList.m_filter.maskBits = WorldHandler.CATEGORY_PASSIVE;
        getPhysicsHandler().getBody().m_gravityScale = 0;
    }

    @Override
    public void onFrameUpdate(int frameDuration) {
        this.age += frameDuration;

        Color color = getColor();

        int alpha = (int) (255 * Math.max(0, 1 - (float) age / life));
        this.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));

        if (isDead()) {
            getScene().remove(this);
        }
    }

    @API
    public boolean isDead() {
        return this.age > this.life;
    }

    @API
    public int getRemainingLifetime() {
        return Math.max(0, this.life - this.age);
    }
}
