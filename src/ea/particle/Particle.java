/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2017 Michael Andonie and contributors.
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

package ea.particle;

import ea.FrameUpdateListener;

import java.awt.*;

public class Particle implements FrameUpdateListener {
    private float x;
    private float y;
    private float vx;
    private float vy;
    private int size;
    private int life;
    private int age;
    private Color color;

    public Particle(float x, float y, float vx, float vy, int size, int life, Color color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.size = size;
        this.life = life;
        this.color = color;
        this.age = 0;
    }

    @Override
    public void onFrameUpdate(int frameDuration) {
        this.x += vx * frameDuration / 1000;
        this.y += vy * frameDuration / 1000;
        this.age += frameDuration;
    }

    public void render(Graphics2D g) {
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * Math.max(0, 1 - (float) age / life))));
        g.fillOval((int) x - size, (int) y - size, 2 * size, 2 * size);
    }

    public boolean isDead() {
        return this.age > this.life;
    }

    public int getRemainingLifetime() {
        return Math.max(0, this.life - this.age);
    }
}
