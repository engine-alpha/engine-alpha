/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2014 Michael Andonie and contributors.
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

import ea.Scene;
import ea.internal.ShapeHelper;
import ea.internal.ano.API;
import ea.internal.io.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Ein Image als grafische Repräsentation einer Bilddatei, die gezeichnet werden kann.
 *
 * @author Michael Andonie
 */
public class Image extends Actor {
    /**
     * Das BufferedImage, das dieses Image darstellt.
     */
    private final BufferedImage image;

    /**
     * Der Konstruktor lädt das Image und erlaubt die Nutung von Spritesheets.
     *
     * @param filepath Der Verzeichnispfad des Bildes, das geladen werden soll.
     */
    @API
    public Image(Scene scene, String filepath) {
        super(scene, () -> {
            BufferedImage image = ImageLoader.load(filepath);

            return ShapeHelper.createRectangularShape(
                    image.getWidth() / scene.getWorldHandler().getPixelProMeter(),
                    image.getHeight() / scene.getWorldHandler().getPixelProMeter()
            );
        });

        this.image = ImageLoader.load(filepath);
    }

    @API
    public Dimension getSize() {
        return new Dimension(this.image.getWidth(), this.image.getHeight());
    }

    @API
    public BufferedImage getImage() {
        return this.image;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(Graphics2D g) {
        g.drawImage(this.image, 0, -this.image.getHeight(), null);
    }
}
