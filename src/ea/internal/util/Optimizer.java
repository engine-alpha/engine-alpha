package ea.internal.util;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

public class Optimizer {
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
