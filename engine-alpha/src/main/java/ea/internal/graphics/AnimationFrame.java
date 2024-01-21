/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2023 Michael Andonie and contributors.
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

package ea.internal.graphics;

//import com.sun.corba.se.impl.orbutil.graph.Graph;

import ea.internal.annotations.Internal;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Beschreibt einen Frame einer {@link ea.actor.Animation}.
 * @author Niklas Keller
 */
@Internal
public final class AnimationFrame {
    /**
     * Das Bild, das zu diesem Frame gehört.
     */
    private final BufferedImage image;
    /**
     * Die Dauer in Sekunden, die dieser Frame aktiv bleibt.
     */
    private float duration;

    /**
     * Erstellt einen Frame.
     * @param image     Das Bild für den Frame.
     * @param duration  Die Dauer, die dieser Frame aktiv bleibt.
     */
    @Internal
    public AnimationFrame(BufferedImage image, float duration) {
        this.image = image;
        this.duration = duration;
    }

    @Internal
    public void setDuration(float duration) {
        this.duration = duration;
    }

    @Internal
    public BufferedImage getImage() {
        return image;
    }

    @Internal
    public float getDuration() {
        return duration;
    }

    /**
     * Rendert den Frame (an der entsprechenden Position des Graphics Objekts)
     * @param g   Das Graphics Objekt
     */
    @Internal
    public void render(Graphics2D g, float width, float height, boolean flipHorizontal, boolean flipVertical) {
        AffineTransform pre = g.getTransform();
        g.scale(width / this.image.getWidth(), height / this.image.getHeight());
        g.drawImage(image,
                flipHorizontal ? image.getWidth() : 0,
                -image.getHeight() + (flipVertical ? image.getHeight() : 0),
                (flipHorizontal ? -1 : 1)*image.getWidth(),
                (flipVertical ? -1 : 1)*image.getHeight(),
                null);
        g.setTransform(pre);
    }
}
