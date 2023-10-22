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

package ea.actor;

import ea.internal.annotations.API;
import ea.internal.io.ImageLoader;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

@API
public interface TileMap {
    @API
    static Tile createFromImage(String path) {
        return createFromImage(ImageLoader.load(path));
    }

    static Tile createFromImage(BufferedImage image) {
        return (g, width, height) -> {
            AffineTransform pre = g.getTransform();
            g.scale(width / image.getWidth(), height / image.getHeight());
            g.drawImage(image, null, 0, 0);
            g.setTransform(pre);
        };
    }

    static TileMap createFromImage(String path, int sizeX, int sizeY) {
        BufferedImage image = ImageLoader.load(path);

        if (image.getWidth() % sizeX != 0) {
          throw new IllegalArgumentException(String.format(
              "Kann die Kacheln mit der Breite von %spx nicht aus der Bilddatei (%s) ausschneiden, da die Bildbreite (%spx) ein Vielfaches der Kachelbreite sein muss.",
              sizeX, path, image.getWidth()));
        }

        if (image.getHeight() % sizeY != 0) {
          throw new IllegalArgumentException(String.format(
              "Kann die Kacheln mit der Höhe von %spx nicht aus der Bilddatei (%s) ausschneiden, da die Bildhöhe (%spx) ein Vielfaches der Kachelhöhe sein muss.",
              sizeY, path, image.getHeight()));
        }

        Tile[][] tiles = new Tile[image.getWidth() / sizeX][image.getHeight() / sizeY];

        for (int posX = 0; posX < image.getWidth(); posX += sizeX) {
            for (int posY = 0; posY < image.getHeight(); posY += sizeY) {
                tiles[posX / sizeX][posY / sizeY] = createFromImage(image.getSubimage(posX, posY, sizeX, sizeY));
            }
        }

        return (x, y) -> tiles[x][y];
    }

    Tile getTile(int x, int y);
}
