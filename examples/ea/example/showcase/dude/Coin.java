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

package ea.example.showcase.dude;

import ea.actor.Animation;
import ea.collision.CollisionEvent;
import ea.collision.CollisionListener;

/**
 * Herzlichen Dank an <a href="https://opengameart.org/content/coin-animation">dontmind8.blogspot.com</a> für die
 * kostenfreien Grafiken.
 */
public class Coin extends Animation implements CollisionListener<PlayerCharacter> {
    public Coin() {
        super(Animation.createFromAnimatedGif("game-assets/dude/coin.gif", .2f, .2f));
    }

    @Override
    public void onCollision(CollisionEvent<PlayerCharacter> collisionEvent) {
        //Habe mich mit PlayerCharacter kollidiert!
        collisionEvent.getColliding().gotItem(Item.Coin);
        this.getScene().remove(this);
    }

    @Override
    public void onCollisionEnd(CollisionEvent<PlayerCharacter> collisionEvent) {

    }
}
