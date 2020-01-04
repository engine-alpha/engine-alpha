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

import ea.Random;
import ea.actor.BodyType;
import ea.actor.Image;

public class Box extends Image {

    private static final float SIZE = 1;
    private static final float MASS_IN_KG = 30;
    private static final float RESTITUTION = .3f;

    public Box() {
        this(Random.nextInteger(9));
    }

    public Box(int type) {
        super(getBoxPath(type), SIZE, SIZE);

        setBodyType(BodyType.DYNAMIC);
        //setMass(MASS_IN_KG);
        setRestitution(RESTITUTION);
    }

    public static String getBoxPath(int type) {
        if (type < 0 || type > 9) {
            throw new RuntimeException("Box-Typ existiert nicht");
        }

        String prefix = type <= 4 ? "game-assets/dude/box/obj_box00" : "game-assets/dude/box/obj_crate00";

        return prefix + (type % 5 + 1) + ".png";
    }
}
