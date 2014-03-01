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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * Diese Klasse definiert Versions-Konstanten und sorgt für eine About-Box
 * beim Ausführen der .jar-Datei.
 * 
 * @author Niklas Keller
 */
@SuppressWarnings("serial")
public class EngineAlpha extends Frame {
	// 1 => 1.0
	// 2 => 1.1
	// 3 => 1.2
	// 4 => 2.0
	// 5 => 3.0pre
	// 6 => 3.0
	
	public static final int VERSION_CODE = 6;
	public static final String VERSION_STRING = "v3.0";
	
	private BufferedImage favicon;
	
	public EngineAlpha() {
		super("Engine Alpha " + VERSION_STRING);
		
		try {
			favicon = ImageIO.read(getClass().getResourceAsStream("/ea/assets/favicon.png"));
			
			this.setIconImage(favicon);
		} catch(IOException e) {
			System.exit(1); // should actually never happen
		}
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				dispose();
				
				System.exit(0);
			}
		});
		
		new EngineAlphaPromotion(this);
	}
	
	private class EngineAlphaPromotion extends Canvas implements Runnable {
		private BufferedImage logo;
		private double alpha = 0;
		private boolean loading = true;
		private int availableVersion = -1;
		
		public EngineAlphaPromotion(EngineAlpha parent) {
			try {
				logo = ImageIO.read(getClass().getResource("/ea/assets/logo.png"));
			} catch (IOException e) {
				System.exit(1); // should actually never happen
			}
			
			setSize(300, 200);
			setPreferredSize(getSize());
			parent.add(this);
			parent.pack();
			
			Dimension screen = getToolkit().getScreenSize();
			parent.setLocation((screen.width-parent.getWidth())/2, (screen.height-parent.getHeight())/2);
			
			parent.setVisible(true);
			
			new Thread(this) {
				{
					setDaemon(true);
				}
			}.start();
			
			new Thread() {
				{
					setDaemon(true);
				}
				
				public void run() {
					BufferedInputStream bis = null;
					URL url = null;
					
					try {
						url = new URL("http://engine-alpha.org/api/v1/version");
						bis = new BufferedInputStream(url.openStream());
						
						StringBuilder builder = new StringBuilder();
						byte[] data = new byte[1024];
						int read = 0;
						
						while((read = bis.read(data)) != -1) {
							builder.append(new String(data, 0, read));
						}
						
						try {
							availableVersion = Integer.parseInt(builder.toString().trim());
						} catch(NumberFormatException e) {
							
						}
					} catch (IOException e) {
						/////
					} finally {
						if(bis != null) {
							try {
								bis.close();
							} catch (IOException e) {
								
							}
						}
					}
					
					loading = false;
				}
			}.start();
		}
		
		public void run() {
			createBufferStrategy(2);
			BufferStrategy bs = getBufferStrategy();
			Graphics2D g = (Graphics2D) bs.getDrawGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			long lastTime = System.currentTimeMillis();
			long currTime = System.currentTimeMillis();
			
			while(isVisible()) {
				lastTime = currTime;
				currTime = System.currentTimeMillis();
				
				update(currTime - lastTime);
				
				try {
					render(g);
					bs.show();
				} catch(Exception e) {
					// just to be sure on shutdown
				}
				
				try {
					Thread.sleep(50);
				} catch(InterruptedException e) {
					// don't care!
				}
			}
		}
		
		private void update(long passedTime) {
			alpha += passedTime * .01;
			alpha %= 360;
		}

		public void render(Graphics2D g) {
			g.setColor(new Color(250, 250, 250));
			g.fillRect(0, 0, getWidth(), getHeight());
			
			g.drawImage(logo, (getWidth() - logo.getWidth()) / 2, 22, null);
			
			if(loading) {
				g.setColor(new Color(0,0,0,150));
				g.fillOval((int) (getWidth()/2+8*Math.cos(alpha))-2, (int) (getHeight()-25+8*Math.sin(alpha))-2, 4, 4);
				g.fillOval((int) (getWidth()/2+8*Math.cos(180+alpha))-2, (int) (getHeight()-25+8*Math.sin(180+alpha))-2, 4, 4);
				g.drawLine((int) (getWidth()/2+8*Math.cos(alpha)), (int) (getHeight()-25+8*Math.sin(alpha)),
						(int) (getWidth()/2+8*Math.cos(180+alpha)), (int) (getHeight()-25+8*Math.sin(180+alpha)));
			} else {
				String message = "";
				Color color = new Color(30,30,30);
				
				if(availableVersion == -1) {
					message = "Server für Versionsabgleich nicht erreichbar.";
				}
				
				else if(availableVersion == VERSION_CODE) {
					message = "Diese Version ist aktuell.";
				}
				
				else if(availableVersion > VERSION_CODE) {
					message = "Es ist eine neue Version verfügbar.";
					color = new Color(200,50,0);
				}
				
				else if(availableVersion < VERSION_CODE) {
					message = "Du arbeitest bereits mit einer Preview.";
					color = new Color(0,100,150);
				}
				
				g.setColor(color);
				g.setFont(new Font("SansSerif", Font.ITALIC, 14));
				FontMetrics fm = g.getFontMetrics();
				g.drawString(message, (getWidth() - fm.stringWidth(message)) / 2, getHeight() - 22);
			}
		}
	}
	
	public static void main(String[] args) {
		new EngineAlpha();
	}
}
