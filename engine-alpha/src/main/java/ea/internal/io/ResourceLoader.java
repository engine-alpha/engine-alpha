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

package ea.internal.io;

import ea.internal.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Lädt Dateien aus der JAR oder dem aktuellen Arbeitsverzeichnis.
 *
 * @author Niklas Keller
 */
final public class ResourceLoader {
    private ResourceLoader() {
        // keine Objekte erlaubt!
    }

    public static byte[] load(String filename) throws IOException {
        String normalizedFilename = normalizePath(filename);
        Path path = Paths.get(normalizedFilename);

        URL url = ResourceLoader.class.getResource("/" + normalizedFilename);
        if (url != null) {
            try {
                path = Paths.get(url.toURI());
            } catch (URISyntaxException e) {
                throw new IOException("Could not convert URL to URI", e);
            }
        }

        return Files.readAllBytes(path);
    }

    public static InputStream loadAsStream(String filename) throws IOException {
        String normalizedFilename = normalizePath(filename);

        if (ResourceLoader.class.getResource("/" + normalizedFilename) != null) {
            return ResourceLoader.class.getResourceAsStream("/" + normalizedFilename);
        }

        return new FileInputStream(normalizePath(normalizedFilename));
    }

    public static File loadAsFile(String filename) throws IOException {
        String normalizedFilename = normalizePath(filename);

        URL url = ResourceLoader.class.getResource("/" + normalizedFilename);
        if (url != null) {
            try {
                return new File(url.toURI());
            } catch (URISyntaxException e) {
                Logger.error("IO", e.getMessage());
            }
        }

        return new File(normalizePath(normalizedFilename));
    }

    static String normalizePath(String path) {
        return path.replace("\\", File.separator).replace("/", File.separator);
    }
}