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

package ea.example.showcase.dude;

public class GameData {
    public static final int MAX_MANA = 500;

    private int money;
    private int mana;
    private float playerVelocity;

    public void setPlayerVelocity(float playerVelocity) {
        this.playerVelocity = playerVelocity;
    }

    public float getPlayerVelocity() {
        return playerVelocity;
    }

    public void addMoney(int money) {
        this.money += money;
    }

    public void addMana(int mana) {
        this.mana += mana;

        if (this.mana > MAX_MANA) {
            this.mana = MAX_MANA;
        }
    }

    public void consumeMana(int mana) {
        this.mana -= mana;

        if (this.mana < 0) {
            this.mana = 0;
        }
    }

    public int getMana() {
        return mana;
    }

    public int getMoney() {
        return money;
    }
}
