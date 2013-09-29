/* Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
 *
 * Copyright (C) 2011  Michael Andonie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ea;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * Diese Klasse ist ausschliesslich dazu da, um der EA eine 
 * runnable-Funktion zu geben. Hierbei wird ein kleines Fenster
 * geoeffnet, dass eine Information zur Engine angibt.
 * @author Andonie
 *
 */
@SuppressWarnings("serial")
public class EngineAlpha
extends Frame {
	// 1.0, 1.1, 1.2, 2.0, 3.0pre
	public static final int VERSION_CODE = 5;
	public static final String VERSION_STRING = "v3.0pre";

	public EngineAlpha() {
		super("Engine Alpha " + EngineAlpha.VERSION_STRING);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				dispose();
				
				System.exit(0);
			}
		});
		
		new EngineAlphaPromotion(this);
	}
	
	private class EngineAlphaPromotion
	extends Canvas
	implements Runnable {
		
		private BufferedImage about;
		
		public EngineAlphaPromotion(EngineAlpha parent) {
			try {
				URL url = EngineAlpha.class.getResource("/ea/about.png");
				
				if(url == null)
					System.exit(1);
				
				about = ImageIO.read(url);
			} catch (IOException e) {
				System.exit(1);
			}
			
			setSize(300, 200);
			setPreferredSize(getSize());
			parent.add(this);
			parent.pack();
			
			Dimension screen = getToolkit().getScreenSize();
			parent.setLocation((screen.width-parent.getWidth())/2, (screen.height-parent.getHeight())/2);
			
			parent.setVisible(true);
			new Thread(this).start();
		}
		
		public void run() {
			createBufferStrategy(2);
			BufferStrategy bs = getBufferStrategy();
			Graphics2D g = (Graphics2D) bs.getDrawGraphics();
			
			while(isVisible()) {
				render(g);
				bs.show();
				
				try {
					Thread.sleep(50);
				} catch(InterruptedException e) {
					// don't care!
				}
			}
		}
		
		public void render(Graphics2D g) {
			g.drawImage(about, 0, 0, null);
		}
	}
	
	public static void main(String[] args) {
		new EngineAlpha();
	}
}
