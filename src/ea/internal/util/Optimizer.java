/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
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

package ea.internal.util;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

/**
 * Diese Klasse optimiert Resourcen für das System des Anwenders.
 * 
 * @author Niklas Keller <me@kelunik.com>
 */
public class Optimizer {
	private Optimizer() {
		
	}
	
	/**
	 * Optimiert ein Bild für das Rendering, abhänig vom Bildschirm des Anwenders.
	 * 
	 * @param img
	 *            Bild, das optimiert werden soll
	 * @return optimiertes Bild
	 */
	public static BufferedImage toCompatibleImage(BufferedImage img) {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsConfiguration gc = env.getDefaultScreenDevice().getDefaultConfiguration();
		
		if (img.getColorModel().equals(gc.getColorModel())) {
			return img;
		}
		
		BufferedImage compat = gc.createCompatibleImage(img.getWidth(), img.getHeight(), img.getTransparency());
		
		Graphics2D g = (Graphics2D) compat.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		
		return compat;
	}
}
