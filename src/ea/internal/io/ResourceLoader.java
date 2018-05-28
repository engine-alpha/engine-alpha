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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

final public class ResourceLoader {
    private ResourceLoader() {
        // keine Objekte erlaubt!
    }

    public static byte[] load(String filename) throws IOException {
        Path path = Paths.get(filename);

        if (ResourceLoader.class.getResource("/" + filename) != null) {
            try {
                path = Paths.get(ResourceLoader.class.getResource("/" + filename).toURI());
            } catch (URISyntaxException e) {
                throw new IOException("Could not convert URL to URI", e);
            }
        }

        return Files.readAllBytes(path);
    }

    public static InputStream loadAsStream(String filename) throws IOException {
        if (ResourceLoader.class.getResource("/" + filename) != null) {
            return ResourceLoader.class.getResourceAsStream("/" + filename);
        }

        return new FileInputStream(filename);
    }
}