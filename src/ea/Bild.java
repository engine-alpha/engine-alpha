/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
 * 
 * Copyright (C) 2011 Michael Andonie
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
import ea.internal.util.Optimizer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Ein Bild als Grafische Repraesentation einer Bilddatei, die gezeichnet werden kann.
 * 
 * @author Michael Andonie
 */
@SuppressWarnings("serial")
public class Bild extends Raum {
	
	/**
	 * Die effektive Hoehe des Bildes auf der Zeichenebene
	 */
	private int hoehe;
	
	/**
	 * Die effektive Breite des Bildes auf der Zeichenebene
	 */
	private int breite;
	
	/**
	 * Gibt an, ob sich das Bild wiederholen soll.<br />
	 * In diesem Fall wird das Bild in der originalen Groesse ueber den Zugesprochenen Grund wiederholt.
	 */
	private boolean wiederholen;
	
	/**
	 * Das BufferedImage, das dieses Bild darstellt.
	 */
	private BufferedImage img;
	
	/**
	 * Die Breite der Quellbilddatei
	 */
	private final int urBreite;
	
	/**
	 * Die Hoehe der Quellbilddatei
	 */
	private final int urHoehe;
	
	/**
	 * Der minimale Basiskonstruktor fuer Objekte der Klasse Bild.<br />
	 * Der absolute Standartkonstruktor, der bei allen anderen ebenfalss aufgerufen wird. Dieser gleicht die Position an und laedt das Bild
	 * 
	 * @param x
	 *            Die X-Position
	 * @param y
	 *            Die Y-Position
	 * @param verzeichnis
	 *            Der Verzeichnispfad des Bildes, das geladen werden soll.
	 */
	public Bild(float x, float y, String verzeichnis) {
		super.position = new Punkt(x, y);
		this.wiederholen = false;
		img = imageModernLaden(verzeichnis);
		urHoehe = img.getHeight();
		urBreite = img.getWidth();
	}
	
	/**
	 * Erweiterter Konstruktor.<br />
	 * Hiebei wird ein Bild erstellt, wobei auch dessen Masse variabel angegeben werden koennen.
	 * 
	 * @param x
	 *            Die X-Position
	 * @param y
	 *            Die Y-Position
	 * @param breite
	 *            Die Breite, die das Bild haben soll
	 * @param hoehe
	 *            Die Hoehe, die das Bild haben soll.
	 * @param verzeichnis
	 *            Der Verzeichnispfad des Bildes, das geladen werden soll.
	 * @param wiederholen
	 *            Ob das Bild skaliert oder wiederholt werden soll. <br />
	 *            In diesem Fall wird das Bild in der originalen Groesse ueber den Zugesprochenen Grund wiederholt: die Parameter <code>breite</code> und <code>hoehe</code> beschreiben diesen Flaeche.
	 */
	public Bild(float x, float y, int breite, int hoehe, String verzeichnis, boolean wiederholen) {
		this(x, y, verzeichnis);
		this.wiederholen = wiederholen;
		this.breite = breite;
		this.hoehe = hoehe;
		this.wiederholen = wiederholen;
		
		if (!wiederholen)
			img = resize(img, breite, hoehe);
	}
	
	/**
	 * Erweiterter Konstruktor.<br />
	 * Hiebei wird ein Bild erstellt, wobei auch dessen Masse variabel angegeben werden koennen.
	 * 
	 * @param x
	 *            Die X-Position
	 * @param y
	 *            Die Y-Position
	 * @param breite
	 *            Die Breite, die das Bild haben soll
	 * @param hoehe
	 *            Die Hoehe, die das Bild haben soll.
	 * @param verzeichnis
	 *            Der Verzeichnispfad des Bildes, das geladen werden soll.
	 */
	public Bild(float x, float y, int breite, int hoehe, String verzeichnis) {
		this(x, y, breite, hoehe, verzeichnis, false);
	}
	
	/**
	 * Erstellt ein Bild, unter Eingabe dessen effektiver Groesse als prozentualer Anteil an der der Bilddatei.
	 * 
	 * @param x
	 *            Die X-Position
	 * @param y
	 *            Die Y-Position
	 * @param prozent
	 *            Der prozentuale Anteil, den das Bild im Vergleich zu der urspruenglichen Bilddatei haben soll
	 * @param verzeichnis
	 *            Der Verzeichnispfad des Bildes, das geladen werden soll.
	 */
	public Bild(float x, float y, int prozent, String verzeichnis) {
		this(x, y, verzeichnis);
		
		img = resize(img, img.getWidth() * prozent / 100, (img.getHeight() * prozent) / 100);
	}
	
	/**
	 * Direkter Konstruktor.<br />
	 * Dieser erwartet direkt die Bilddatei, die es anzuzeigen gilt.<br />
	 * Dieser Konstruktor wird innerhalb der Engine verwendet fuer die Maus.
	 */
	public Bild(float x, float y, BufferedImage img) {
		super.positionSetzen(new Punkt(x, y));
		
		this.img = img;
		
		urHoehe = img.getHeight();
		urBreite = img.getWidth();
		
		hoehe = urHoehe;
		breite = urBreite;
	}
	
	/**
	 * Laedt ein Bild auf die 2.0-Art. Alles auff Basis der ausfuehrbaren Datei.
	 * 
	 * @param verzeichnis
	 *            Das Verzeichnis des Bildes. Nach dem Materialordner-Prinzip
	 * @return Das geladene Bild
	 */
	private BufferedImage imageModernLaden(String verzeichnis) {
		BufferedImage img = null;
		
		try {
			img = ImageIO.read(new FileInputStream(new File(verzeichnis)));
			img = Optimizer.toCompatibleImage(img);
		} catch (IOException e) {
			Logger.error("Das Bild konnte nicht geladen werden: " + verzeichnis + "   Existiert dieses Verzeichnis tatsaechlich?");
			e.printStackTrace();
		}
		
		return img;
	}
	
	/**
	 * Aendert ein BufferedImage von seinen Massen her.<br />
	 * Wird intern benutzt, im Konstruktor.
	 * 
	 * @param img
	 *            Das zu beschraenkende Bild
	 * @param newW
	 *            Die neue Breite des Bildes
	 * @param newH
	 *            Die neue Hoehe des Bildes
	 */
	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		int w = img.getWidth();
		int h = img.getHeight();
		
		BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
		Graphics2D g = dimg.createGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
		g.dispose();
		
		return dimg;
	}
	
	/**
	 * Rotiert ein BufferedImage und gibt das neue, rotierte Bild aus.<br />
	 * Es wird immer nur um die eigene Mitte gedreht!!
	 * 
	 * @param img
	 *            Das zu rotierende Bild
	 * @param angle
	 *            Der Winkel im Bogenmass, um den gedreht werden soll.
	 */
	public static BufferedImage rotieren(BufferedImage img, double angle) {
		int w = img.getWidth();
		int h = img.getHeight();
		
		BufferedImage dimg = new BufferedImage((int) (w * Math.sin(angle) + w * Math.cos(angle)), (int) (h * Math.sin(angle) + h * Math.cos(angle)), img.getType());
		Graphics2D g = dimg.createGraphics();
		
		g.rotate(angle, w / 2, h / 2);
		g.drawImage(img, null, (int) (Math.cos(angle) * h / 2 * Math.sin(angle)), (int) (Math.cos(angle) * h / 2 * Math.sin(angle)));
		g.dispose();
		
		return dimg;
	}
	
	/**
	 * Rotiert das Objekt um eine bereits definierte Rotation.
	 * 
	 * @param rot
	 *            Das Rotationsobjekt, das die Rotation beschreibt
	 * @see Rotation
	 */
	public void rotieren(Rotation rot) {
		img = rotieren(img, rot.winkelBogen());
	}
	
	/**
	 * Test, ob ein anderes Raum-Objekt von diesem geschnitten wird.
	 * 
	 * @param r
	 *            Das Objekt, das auf Kollision mit diesem getestet werden soll.
	 * @return TRUE, wenn sich beide Objekte schneiden.
	 */
	public boolean schneidet(Raum r) {
		if(r == null) {
			return false;
		}

		return r.dimension().schneidetBasic(this.dimension());
	}
	
	/**
	 * @return Ein BoundingRechteck mit dem minimal noetigen Umfang, um das Objekt <b>voll einzuschliessen</b>.
	 */
	public BoundingRechteck dimension() {
		if (!wiederholen) {
			return new BoundingRechteck(position.realX(), position.realY(), img.getWidth(), img.getHeight());
		} else {
			return new BoundingRechteck(position.realX(), position.realY(), breite, hoehe);
		}
	}
	
	/**
	 * Zeichnet das Objekt.
	 * 
	 * @param g
	 *            Das zeichnende Graphics-Objekt
	 * @param r
	 *            Das BoundingRechteck, dass die Kameraperspektive Repraesentiert.<br />
	 *            Hierbei soll zunaechst getestet werden, ob das Objekt innerhalb der Kamera liegt, und erst dann gezeichnet werden.
	 */
	public void zeichnen(Graphics2D g, BoundingRechteck r) {
		super.beforeRender(g);
		
		if (r.schneidetBasic(this.dimension())) {
			if (!wiederholen) {
				g.drawImage(img, (int) (position.realX() - r.x), (int) (position.realY() - r.y), null);
			} else {
				// Texturfarbe erstellen, Anchor-Rechteck hat genau die Bildmasse
				TexturePaint tp = new TexturePaint(img, new Rectangle2D.Double(-r.x + position.realX(), -r.y + position.realY(), img.getWidth(), img.getHeight()));
				// Texturfarbe setzen
				g.setPaint(tp);
				// Rechteck fuellen
				g.fill(new Rectangle2D.Double(position.realX() - r.x, position.realY() - r.y, breite, hoehe));
			}
		}
		
		super.afterRender(g);
	}
	
	/**
	 * Gibt die Breite der Bilddatei, aus der dieses Bild besteht, in Pixeln zurueck.
	 * 
	 * @return Die Breite der urspruenglichen Bilddatei in Pixeln
	 */
	public int normaleBreite() {
		return urBreite;
	}
	
	/**
	 * Gibt die Hoehe der Bilddatei, aus der dieses Bild besteht, in Pixeln zurueck.
	 * 
	 * @return Die Hoehe der urspruenglichen Bilddatei in Pixeln
	 */
	public int normaleHoehe() {
		return urHoehe;
	}
	
	/**
	 * Gibt das Bild als <code>BufferedImage</code> zurueck.<br />
	 * Dies ist eine JAVA-Standartklasse.
	 * 
	 * @return Das Bild als <code>BufferedImage</code>.
	 */
	public BufferedImage bild() {
		return this.img;
	}
	
	public Bild clone() {
		return new Bild(positionX(), positionY(), img);
	}
}
