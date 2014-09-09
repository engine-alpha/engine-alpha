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

package ea.internal.io;

import ea.internal.util.Logger;
import ea.internal.util.Optimizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageLoader {
	private ImageLoader () {
		// keine Objekte erlaubt!
	}

	/**
	 * Lädt ein Bild, das außerhalb der ausführbaren Datei liegt.
	 *
	 * @param path
	 * 		Verzeichnis des Bildes.
	 *
	 * @return geladenes Bild oder <code>null</code> im Fehlerfall.
	 */
	public static BufferedImage loadExternalImage (String path) {
		BufferedImage img = null;

		try {
			img = ImageIO.read(new FileInputStream(new File(path)));
			img = Optimizer.toCompatibleImage(img);
		} catch (IOException e) {
			Logger.error("Das Bild konnte nicht geladen werden: " + path);
		}

		return img;
	}

	/**
	 * Lädt ein Bild, das innerhalb der ausführbaren Datei liegt.
	 *
	 * @param path
	 * 		Verzeichnis des Bildes, der Pfad muss absolut sein!
	 *
	 * @return geladenes Bild oder <code>null</code> im Fehlerfall.
	 */
	public static BufferedImage loadInternalImage (String path) {
		BufferedImage img = null;

		try {
			img = ImageIO.read(ImageLoader.class.getResource(path));
			img = Optimizer.toCompatibleImage(img);
		} catch (IOException e) {
			Logger.error("Das Bild konnte nicht geladen werden: " + path);
		}

		return img;
	}
}
