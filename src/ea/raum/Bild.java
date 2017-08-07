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

package ea.raum;

import ea.internal.ano.API;
import ea.internal.io.ImageLoader;
import org.jbox2d.collision.shapes.Shape;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Ein Bild als grafische Repräsentation einer Bilddatei, die gezeichnet werden kann.
 *
 * @author Michael Andonie
 */
public class Bild extends Raum {
    /**
     * Das BufferedImage, das dieses Bild darstellt.
     */
    private final BufferedImage image;

    /**
     * Der Konstruktor lädt das Bild und erlaubt die Nutung von Spritesheets.
     *
     * @param filepath Der Verzeichnispfad des Bildes, das geladen werden soll.
     */
    @API
    public Bild(String filepath) {
        this.image = ImageLoader.load(filepath);
    }

    @API
    public Dimension getSize() {
        return new Dimension(this.image.getWidth(), this.image.getHeight());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Shape createShape(float pixelPerMeter) {
        return this.berechneBoxShape(pixelPerMeter, image.getWidth(), image.getHeight());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(Graphics2D g) {
        g.drawImage(this.image, 0, 0, null);
    }
}
