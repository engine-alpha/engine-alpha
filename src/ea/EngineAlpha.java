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

package ea;

import ea.internal.util.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Diese Klasse definiert Versions-Konstanten und sorgt für eine About-Box beim Ausführen der
 * .jar-Datei.
 * <p/>
 * TODO: Commit-ID in Jar packen und mit aktueller ID auf GitHub vergleichen
 *
 * @author Niklas Keller <me@kelunik.com>
 */
public class EngineAlpha extends Frame {

	/**
	 * Der Versionscode des aktuellen Release.<br />
	 * Rechnung:<br/>
	 * <code>
	 * 10000 * major + 100 * minor + 1 * bugfix
	 * </code>
	 */
	public static final int VERSION_CODE = 30200;

	/**
	 * Format: v(major).(minor).(bugfix)
	 * Beispiel: v3.1.2
	 */
	public static final String VERSION_STRING = "v3.2.0";

	/**
	 * Gibt an, ob dieser Release in .jar - Form vorliegt. Ist das der Fall,
	 * ist dieser Wert <code>true</code>, sonst ist er <code>false</code>.
	 */
	public static final boolean IS_JAR;

	/**
	 * Zeitpunkt, an dem diese Jar-Datei erzeugt wurde, falls als Jar-Datei ausgeführt, sonst die
	 * aktuelle Zeit in Sekunden seit dem 01.01.1970 (Unix Timestamp)
	 */
	public static final long BUILD_TIME;

	/**
	 * Statischer Konstruktor.
	 * Ermittelt <code>IS_JAR</code> und <code>BUILD_TIME</code>.
	 */
	static {
		IS_JAR = isJar();
		BUILD_TIME = IS_JAR ? getBuildTime() / 1000 : System.currentTimeMillis() / 1000;
	}

	/**
	 * Wird debug auf <code>true</code> gesetzt, so werden ausführliche Informationen zu Tickern im
	 * Logger ausgegeben.
	 */
	private static boolean debug;

	/**
	 * Panel, das den Fensterinhalt zeichnet.
	 */
	private EngineAlphaPromotion promo;

	public EngineAlpha () {
		super("Engine Alpha " + VERSION_STRING);

		try {
			setIconImage(ImageIO.read(getClass().getResourceAsStream("/assets/favicon.png")));
		} catch (IOException e) {
			Logger.error(e.getLocalizedMessage());
		}

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				promo.shutdown();

				setVisible(false);
				dispose();

				System.exit(0);
			}
		});

		promo = new EngineAlphaPromotion();
	}

	/**
	 * Main-Methode der Engine Alpha. Diese öffnet ein Fenster, das einen Versionsabgleich der
	 * aktuellen Version mit der aktuell verfügbaren Version macht.
	 */
	public static void main (String[] args) {
		new EngineAlpha();
	}

	/**
	 * Gibt an, ob das Programm gerade aus einer Jar heraus gestartet wurde.
	 * @return <code>true</code>, falls ja, sonst <code>false</code>.
	 */
	public static boolean isJar () {
		String className = EngineAlpha.class.getName().replace('.', '/');
		String classJar = EngineAlpha.class.getResource("/" + className + ".class").toString();

		return classJar.startsWith("jar:");
	}

	/**
	 * Gibt den Namen der Jar-Datei zurück, die gerade ausgeführt wird.
	 *
	 * @return Dateiname der Jar-Datei oder <code>null</code>, falls das Programm nicht über eine
	 * Jar-Datei ausgeführt wird.
	 */
	@SuppressWarnings ( "unused" )
	public static String getJarName () {
		String className = EngineAlpha.class.getName().replace('.', '/');
		String classJar = EngineAlpha.class.getResource("/" + className + ".class").toString();

		if (classJar.startsWith("jar:")) {
			String vals[] = classJar.split("/");

			for (String val : vals) {
				if (val.contains("!")) {
					try {
						return java.net.URLDecoder.decode(val.substring(0, val.length() - 1), "UTF-8");
					} catch (Exception e) {
						return null;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Gibt an, wann die Jar-Datei erzeugt wurde.
	 *
	 * @return Erzeugungsdatum der Jar-Datei in Sekunden seit dem 01.01.1970 (Unix Timestamp) oder
	 * den aktuellen Timestamp, falls nicht von einer Jar-Datei ausgeführt.
	 */
	public static long getBuildTime () {
		try {
			String uri = EngineAlpha.class.getName().replace('.', '/') + ".class";
			JarURLConnection j = (JarURLConnection) ClassLoader.getSystemResource(uri).openConnection();

			long time = j.getJarFile().getEntry("META-INF/MANIFEST.MF").getTime();
			return time > 0 ? time : System.currentTimeMillis() / 1000;
		} catch (Exception e) {
			return System.currentTimeMillis() / 1000;
		}
	}

	/**
	 * Holt den gesamten Inhalt einer einzelnen Webseite und gibt diesen zurück.
	 *
	 * @param uri
	 * 		URL, welche geholt werden soll
	 *
	 * @return Response-Body
	 */
	private static String getUrlBody (String uri) {
		// workaround, make sure this is set to false
		// see http://stackoverflow.com/a/14884941/2373138
		System.setProperty("jsse.enableSNIExtension", "false");

		BufferedInputStream bis = null;
		URL url;

		try {
			url = new URL(uri);
			bis = new BufferedInputStream(url.openStream());

			StringBuilder builder = new StringBuilder();
			byte[] data = new byte[1024];
			int read;

			while ((read = bis.read(data)) != -1) {
				builder.append(new String(data, 0, read));
			}

			return builder.toString();
		} catch (Exception e) {
			// client may have no internet connection
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	/**
	 * Gibt an, ob sich die Engine im Debug-Modus befindet.
	 *
	 * @return {@code true} falls die Engine im Debug-Modus läuft, sonst {@code false}
	 */
	public static boolean isDebug () {
		return debug;
	}

	/**
	 * Ändert den Debug-Status der Engine.
	 *
	 * @param value {@code true}, falls die Engine im Debug-Modus arbeiten soll, sonst {@code false}.
	 */
	public static void setDebug (boolean value) {
		debug = value;
	}

	/**
	 * Zeichenebene für den Versionschecker
	 */
	private class EngineAlphaPromotion extends Canvas implements Runnable {
		private Thread thread;

		private BufferedImage logo;

		private double alpha = 0;

		private boolean loading = true;

		private boolean alive = true;

		private int version_stable = -1;

		public EngineAlphaPromotion () {
			EngineAlpha parent = EngineAlpha.this;

			try {
				logo = ImageIO.read(getClass().getResource("/assets/logo.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			setSize(400, 300);
			setPreferredSize(getSize());
			parent.add(this);
			parent.pack();

			Dimension screen = getToolkit().getScreenSize();
			parent.setLocation((screen.width - parent.getWidth()) / 2, (screen.height - parent.getHeight()) / 2);

			parent.setVisible(true);

			thread = new Thread(this) {{
				setDaemon(true);
			}};
			thread.start();

			new Thread() {
				{
					setDaemon(true);
				}

				public void run () {
					try {
						String body = getUrlBody("https://raw.githubusercontent.com/engine-alpha/engine-alpha/master/VERSION_STABLE").trim();
						version_stable = Integer.parseInt(body);
					} catch (Exception e) {
						version_stable = -1;
					}

					loading = false;
				}
			}.start();
		}

		public void run () {
			createBufferStrategy(2);
			BufferStrategy bs = getBufferStrategy();
			Graphics2D g = (Graphics2D) bs.getDrawGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			long lastTime, currTime = System.currentTimeMillis();

			while (alive) {
				lastTime = currTime;
				currTime = System.currentTimeMillis();

				update(currTime - lastTime);

				render(g);
				bs.show();

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private void update (long passedTime) {
			alpha += passedTime * .01;
			alpha %= 360;
		}

		public void render (Graphics2D g) {
			g.setFont(new Font("SansSerif", Font.ITALIC, 14));
			FontMetrics fm = g.getFontMetrics();

			g.setColor(new Color(250, 250, 250));
			g.fillRect(0, 0, getWidth(), getHeight());

			g.drawImage(logo, (getWidth() - logo.getWidth()) / 2, 45, null);

			if (loading) {
				g.setColor(new Color(0, 0, 0, 150));
				g.fillOval((int) (getWidth() / 2 + 8 * Math.cos(alpha)) - 2, (int) (getHeight() - 80 + 8 * Math.sin(alpha)) - 2, 4, 4);
				g.fillOval((int) (getWidth() / 2 + 8 * Math.cos(180 + alpha)) - 2, (int) (getHeight() - 80 + 8 * Math.sin(180 + alpha)) - 2, 4, 4);
				g.drawLine((int) (getWidth() / 2 + 8 * Math.cos(alpha)), (int) (getHeight() - 80 + 8 * Math.sin(alpha)), (int) (getWidth() / 2 + 8 * Math.cos(180 + alpha)), (int) (getHeight() - 80 + 8 * Math.sin(180 + alpha)));
			} else {
				String message = "";
				Color color = new Color(30, 30, 30);

				if (version_stable == -1) {
					message = "Server für Versionsabgleich nicht erreichbar.";
				} else if (version_stable == VERSION_CODE) {
					message = "Dies ist die aktuelle Stable-Version.";
					color = new Color(50, 200, 25);
				} else if (VERSION_CODE < version_stable) {
					message = "Es ist eine neue Stable-Version verfügbar!";
					color = new Color(200, 50, 0);
				}

				g.setColor(color);
				g.drawString(message, (getWidth() - fm.stringWidth(message)) / 2, getHeight() - 70);
			}

			Date date = new Date(BUILD_TIME * 1000);
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			g.setColor(new Color(100, 100, 100));
			String str = "Build #" + VERSION_CODE + "   " + sdf.format(date);
			g.drawString(str, (getWidth() - fm.stringWidth(str)) / 2, getHeight() - 40);
		}

		public void shutdown () {
			this.alive = false;

			try {
				thread.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
