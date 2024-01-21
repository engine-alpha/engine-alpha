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

package ea.internal.io;

import ea.internal.util.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Diese IO Klasse schreibt Bilder.
 */
public class ImageWriter {

    public static void writeImage(BufferedImage image, String path) {
        String pathlowercase = path.toLowerCase();
        String formatname = null;
        if (pathlowercase.endsWith(".png")) {
            formatname = "png";
        } else if (pathlowercase.endsWith(".gif")) {
            formatname = "gif";
        } else if (pathlowercase.endsWith(".jpg")) {
            formatname = "jpg";
        } else {
            Logger.error("IO", "Nicht unterstütztes Format. Nur png, jpg, gif ist unterstützt");
            return;
        }
        try {
            ImageIO.write(image, formatname, new File(ResourceLoader.normalizePath(path)));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Schreiben des Bildes");
        }
    }
}
