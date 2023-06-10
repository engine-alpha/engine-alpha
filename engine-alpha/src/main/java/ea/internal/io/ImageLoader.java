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

package ea.internal.io;

import ea.internal.annotations.API;
import ea.internal.util.Optimizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Lädt Bilder vom Dateisystem und optimiert diese direkt für die Anzeige.
 *
 * @author Niklas Keller
 */
final public class ImageLoader {
    /**
     * Cache, damit viele gleiche Bilder nicht jedes Mal neu geladen werden müssen.
     */
    private static final Map<String, BufferedImage> cache = new HashMap<>();

    private ImageLoader() {
        // keine Objekte erlaubt!
    }

    /**
     * Lädt ein Image und optimiert es für das aktuelle System.
     *
     * @param path Pfad des Bildes.
     *
     * @return geladenes Image
     */
    public static BufferedImage load(String path) {
        if (cache.containsKey(path)) {
            return cache.get(path);
        }

        try {
            BufferedImage img = Optimizer.toCompatibleImage(ImageIO.read(ResourceLoader.loadAsStream(path)));

            cache.put(path, img);

            return img;
        } catch (IOException e) {
            throw new RuntimeException("Das Image konnte nicht geladen werden: " + path);
        }
    }

    /**
     * Leert den Cache und lädt Bilder beim nächsten Laden erneut vom Dateisystem.
     */
    @API
    public static void clearCache() {
        cache.clear();
    }

    /**
     * Leert einen bestimmten Cache-Eintrag und lädt den Eintrag bei der nächsten Verwendung erneut
     * vom Dateisystem.
     *
     * @param path Pfad des Bildes.
     */
    @API
    public static void clearCache(String path) {
        cache.remove(path);
    }
}