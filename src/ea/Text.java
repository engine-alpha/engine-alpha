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
import org.jbox2d.collision.shapes.Shape;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Zur Darstellung von Texten im Programmbildschirm.
 * <p/>
 * TODO: Review der ganzen Klasse (v.a. der Dokumentation)
 *
 * @author Michael Andonie
 */
public class Text extends Raum {
    /**
     * Alle möglichen Fontnamen des Systems, auf dem man sich gerade befindet.<br /> Hiernach werden
     * Überprüfungen gemacht, ob die gewünschte Schriftart auch auf dem hiesigen System vorhanden
     * ist.
     */
    public static final String[] fontNamen;

    private static final long serialVersionUID = -2145724725115670955L;

	/**
	 * Ein Feld aller existenten Fonts, die im Hauptprojektordner gespeichert sind.<br /> Macht das
	 * interne verwenden dieser Fonts moeglich, ohne das Vorhandensein der Fonts in den
	 * Computerressourcen selber zur Voraussetzung zu haben.
	 */
	private static Font[] eigene;

	/**
	 * static-Konstruktor.<br />
	 * hier werden die externen Fonts geladen.
	 */
	static {
		ArrayList<File> alleFonts = new ArrayList<>();
		fontsEinbauen(alleFonts, new File(System.getProperty("user.dir")));
		File[] unter = alleFonts.toArray(new File[alleFonts.size()]);
		eigene = new Font[unter.length];

		for (int i = 0; i < unter.length; i++) {
			try {
				FileInputStream s = new FileInputStream(unter[i]);
				eigene[i] = Font.createFont(Font.TRUETYPE_FONT, s);
				s.close();
			} catch (FileNotFoundException e) {
				Logger.error("Text/Fonts", "Interner Lesefehler. Dies hätte unter keinen Umständen passieren dürfen.");
			} catch (FontFormatException e) {
				Logger.error("Text/Fonts", "Das TrueType-Font-Format einer Datei (" + unter[i].getPath() + ") war nicht einlesbar!");
			} catch (IOException e) {
				Logger.error("Text/Fonts", "Lesefehler beim Laden der eigenen Fonts! Zugriffsrechte überprüfen.");
			}
		}
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        fontNamen = ge.getAvailableFontFamilyNames();

	}

	/**
	 * Die Schriftgröße des Textes
	 */
	protected int groesse;

	/**
	 * Die Schriftart (<b>fett, kursiv, oder fett & kursiv</b>).<br /> Dies wird dargestellt als
	 * int.Wert:<br /> 0: Normaler Text<br /> 1: Fett<br /> 2: Kursiv<br /> 3: Fett & Kursiv
	 */
	protected int schriftart;

	/**
	 * Der Wert des Textes.
	 */
	protected String inhalt;

	/**
	 * Der Font der Darstellung
	 */
	protected Font font;

	/**
	 * Die Farbe, in der der Text dargestellt wird.
	 */
	protected Color farbe;

	/**
	 * Referenz auf die Farbe, die vor dem leuchten da war (zum wiederherstellen)
	 */
	private Color alte;

	/**
	 * Gibt an, ob dieser Text gerade leuchtet
	 */
	private boolean leuchtet = false;

	/**
	 * Der Zaehler fuer die Leuchtanimation
	 */
	private int leuchtzaehler;

	/**
	 * Textanker: links, mittig oder rechts
	 */
	private Anker anker = Anker.LINKS;

    /**
     * Referenz auf die jüngsten Font-Metriken, die für die Berechnung der Textmaße verwendet wurden.
     */
    private FontMetrics fontMetrics;

    /**
	 * Ebenefalls ein vereinfachter Konstruktor. Hierbei ist die Farbe "Weiss" und der Text weder
	 * kursiv noch fett; weiterhin ist die Schriftgroesse automatisch 24.
	 *
	 * @param inhalt
	 * 		Die Zeichenkette, die dargestellt werden soll
	 * @param x
	 * 		Die X-Koordinate des Anfangs
	 * @param y
	 * 		Die Y-Koordinate des Anfangs
	 * @param fontName
	 * 		Der Name des zu verwendenden Fonts.<br /> Wird hierfuer ein Font verwendet, der in dem
	 * 		Projektordner vorhanden sein soll, <b>und dies ist immer und in jedem Fall zu
	 * 		empfehlen</b>, muss der Name der Schriftart hier ebenfalls einfach nur eingegeben werden,
	 * 		<b>nicht der Name der schriftart-Datei!!!!!!!!!!!!!!!!!!!!!!!!</b>
	 */
	public Text (String inhalt, float x, float y, String fontName) {
		this(inhalt, x, y, fontName, 24);
	}

	/**
	 * Konstruktor ohne Farb- und sonderartseingabezwang. In diesem Fall ist die Farbe "Weiss" und
	 * der Text weder kursiv noch fett.
	 *
	 * @param inhalt
	 * 		Die Zeichenkette, die dargestellt werden soll
	 * @param x
	 * 		Die X-Koordinate des Anfangs
	 * @param y
	 * 		Die Y-Koordinate des Anfangs
	 * @param fontName
	 * 		Der Name des zu verwendenden Fonts.<br /> Wird hierfuer ein Font verwendet, der in dem
	 * 		Projektordner vorhanden sein soll, <b>und dies ist immer und in jedem Fall zu
	 * 		empfehlen</b>, muss der Name der Schriftart hier ebenfalls einfach nur eingegeben werden.
	 * @param schriftGroesse
	 * 		Die Groesse, in der die Schrift dargestellt werden soll
	 */
	public Text (String inhalt, float x, float y, String fontName, int schriftGroesse) {
		this(inhalt, x, y, fontName, schriftGroesse, 0, "Weiss");
	}

	/**
	 * Konstruktor für Objekte der Klasse Text<br /> Möglich ist es auch, Fonts zu laden, die im
	 * Projektordner sind. Diese werden zu Anfang einmalig geladen und stehen dauerhaft zur
	 * Verfügung.
	 *
	 * @param inhalt
	 * 		Die Zeichenkette, die dargestellt werden soll
	 * @param x
	 * 		Die X-Koordinate des Anfangs
	 * @param y
	 * 		Die Y-Koordinate des Anfangs
	 * @param fontName
	 * 		Der Name des zu verwendenden Fonts.<br /> Wird hierfuer ein Font verwendet, der in dem
	 * 		Projektordner vorhanden sein soll, <b>und dies ist immer und in jedem Fall zu
	 * 		empfehlen</b>, muss der Name der Schriftart hier ebenfalls einfach nur eingegeben werden,
	 * 		<b>nicht der Name der schriftart-Datei!</b>
	 * @param schriftGroesse
	 * 		Die Groesse, in der die Schrift dargestellt werden soll
	 * @param schriftart
	 * 		Die Schriftart dieses Textes. Folgende Werte entsprechen folgendem:<br /> 0: Normaler
	 * 		Text<br /> 1: Fett<br /> 2: Kursiv<br /> 3: Fett & Kursiv <br /> <br /> Alles andere sorgt
	 * 		nur fuer einen normalen Text.
	 * @param farbe
	 * 		Die Farbe, die für den Text benutzt werden soll.
	 */
	public Text (String inhalt, float x, float y, String fontName, int schriftGroesse, int schriftart, String farbe) {
		this.inhalt = inhalt;
		this.position.set(new Punkt(x, y));
		this.groesse = schriftGroesse;
		this.farbe = Farbe.zuFarbeKonvertieren(farbe);

		if (schriftart >= 0 && schriftart <= 3) {
			this.schriftart = schriftart;
		} else {
			this.schriftart = 0;
		}

		setzeFont(fontName);
	}

    /**
     * Prüft, ob ein Font auf diesem Computer existiert.
     *
     * @param name
     * 		Der Name des zu ueberpruefenden Fonts
     *
     * @return <code>true</code>, falls der Font auf dem System existiert, sonst <code>false</code>
     */
    public static boolean fontExistiert (String name) {
        for (String s : fontNamen) {
            if (s.equals(name)) {
                return true;
            }
        }

        return false;
    }

    /**
	 * Setzt einen neuen Font fuer den Text
	 *
	 * @param fontName
	 * 		Der Name des neuen Fonts fuer den Text
	 */
	public void setzeFont (String fontName) {
		Font base = null;
		for (int i = 0; i < eigene.length; i++) {
			if (eigene[i].getName().equals(fontName)) {
				base = eigene[i];
				break;
			}
		}
		if (base != null) {
			this.font = base.deriveFont(schriftart, groesse);
		} else {
			if (!fontExistiert(fontName)) {
				fontName = "SansSerif";
				Logger.error("Text/Fonts", "Achtung! Die gewuenschte Schriftart existiert nicht im Font-Verzeichnis dieses PC! " + "Wurde der Name falsch geschrieben? Oder existiert der Font nicht?");
			}
			this.font = new Font(fontName, schriftart, groesse);
		}
	}

	/**
	 * Einfacherer Konstruktor.<br /> Hierbei wird automatisch die Schriftart auf eine
	 * Standartmaessige gesetzt
	 *
	 * @param inhalt
	 * 		Die Zeichenkette, die dargestellt werden soll
	 * @param x
	 * 		Die X-Koordinate des Anfangs
	 * @param y
	 * 		Die Y-Koordinate des Anfangs
	 * @param schriftGroesse
	 * 		Die Groesse, in der die Schrift dargestellt werden soll
	 */
	public Text (String inhalt, float x, float y, int schriftGroesse) {
		this(inhalt, x, y, "SansSerif", schriftGroesse, 0, "Weiss");
	}

	/**
	 * Ein vereinfachter Konstruktor.<br /> Hierbei wird eine Standartschriftart, die Farbe weiss
	 * und eine Groesse von 24 gewaehlt.
	 *
	 * @param inhalt
	 * 		Der Inhalt des Textes
	 * @param x
	 * 		X-Koordinate
	 * @param y
	 * 		Y-Koordinate
	 */
	public Text (String inhalt, float x, float y) {
		this(inhalt, x, y, "SansSerif", 24, 0, "Weiss");
	}

	/**
	 * Ein vereinfachter parallerer Konstruktor.<br /> Diesen gibt es inhaltlich genauso bereits,
	 * jedoch sind hier die Argumente vertauscht; dies dient der Praevention undgewollter falscher
	 * Konstruktorenaufrufe. Hierbei wird eine Standartschriftart, die Farbe weiss und eine Groesse
	 * von 24 gewaehlt.
	 *
	 * @param inhalt
	 * 		Der Inhalt des Textes
	 * @param x
	 * 		X-Koordinate
	 * @param y
	 * 		Y-Koordinate
	 */
	public Text (float x, float y, String inhalt) {
		this(inhalt, x, y, "SansSerif", 24, 0, "Weiss");
	}

	/**
	 * Ein vereinfachter parallerer Konstruktor.<br /> Diesen gibt es inhaltlich genauso bereits,
	 * jedoch sind hier die Argumente vertauscht; dies dient der Praevention undgewollter falscher
	 * Konstruktorenaufrufe. Hierbei wird eine Standartschriftart und die Farbe weiss gewaehlt.
	 *
	 * @param inhalt
	 * 		Der Inhalt des Textes
	 * @param x
	 * 		X-Koordinate
	 * @param y
	 * 		Y-Koordinate
	 * @param schriftGroesse
	 * 		Die Schriftgroesse, die der Text haben soll
	 */
	public Text (int x, int y, int schriftGroesse, String inhalt) {
		this(inhalt, x, y, "SansSerif", schriftGroesse, 0, "Weiss");
	} // TODO: Mehr Verwirrung als Hilfe?

	private static void fontsEinbauen (final ArrayList<File> liste, File akt) {
		File[] files = akt.listFiles();

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].equals(akt)) {
					Logger.error("Text/Fonts", "Das Sub-Directory war das Directory selbst. Das darf nicht passieren!");
					continue;
				}
				if (files[i].isDirectory()) {
					fontsEinbauen(liste, files[i]);
				}
				if (files[i].getName().toLowerCase().endsWith(".ttf")) {
					liste.add(files[i]);
				}
			}
		}
	}

	/**
	 * TODO: Dokumentation
	 */
	public static Font holeFont (String fontName) {
		Font base = null;
		for (int i = 0; i < eigene.length; i++) {
			if (eigene[i].getName().equals(fontName)) {
				base = eigene[i];
				break;
			}
		}
		if (base != null) {
			return base;
		} else {
			if (!fontExistiert(fontName)) {
				fontName = "SansSerif";
				Logger.error("Text/Fonts", "Achtung! Die gewuenschte Schriftart existiert weder als geladene Sonderdatei" +
                        " noch im Font-Verzeichnis dieses PC! Wurde der Name falsch geschrieben? Oder existiert der Font nicht?");
			}
			return new Font(fontName, 0, 12);
		}
	}

	/**
	 * Sehr wichtige Methode!<br /> Diese Methode liefert als Protokoll an die Konsole alle Namen,
	 * mit denen die aus dem Projektordner geladenen ".ttf"-Fontdateien gewaehlt werden koennen.<br
	 * /> Diese Namen werden als <code>String</code>-Argument erwartet, wenn die eigens eingebauten
	 * Fontarten verwendet werden sollen.<br /> Der Aufruf dieser Methode wird <b>UMGEHEND</b>
	 * empfohlen, nach dem alle zu verwendenden Arten im Projektordner liegen, denn nur unter dem an
	 * die Konsole projezierten Namen <b>koennen diese ueberhaupt verwendet werden</b>!!<br /> Daher
	 * dient diese Methode der Praevention von Verwirrung, wegen "nicht darstellbarer" Fonts.
	 */
	public static void geladeneSchriftartenAusgeben () {
		Logger.info("Text/Fonts", "Protokoll aller aus dem Projektordner geladener Fontdateien");

		if (eigene.length == 0) {
			Logger.info("Text/Fonts", "Es wurden keine \".ttf\"-Dateien im Projektordner gefunden");
		} else {
			Logger.info("Text/Fonts", "Es wurden " + eigene.length + " \".ttf\"-Dateien im Projektordner gefunden.");
			Logger.info("Text/Fonts", "Diese sind unter folgenden Namen abrufbar:");

			for (Font font : eigene) {
				Logger.info("Text/Fonts", font.getName());
			}
		}
	}

	/**
	 * Setzt den Inhalt des Textes.<br /> Parallele Methode zu <code>setzeInhalt()</code>
	 *
	 * @param inhalt
	 * 		Der neue Inhalt des Textes
	 *
	 * @see #setzeInhalt(String)
	 */
	public void inhaltSetzen (String inhalt) {
		setzeInhalt(inhalt);
	}

	/**
	 * Setzt den Inhalt des Textes.
	 *
	 * @param inhalt
	 * 		Der neue Inhalt des Textes
	 */
	public void setzeInhalt (String inhalt) {
		this.inhalt = inhalt;
	}

	/**
	 * Setzt die Schriftart.
	 *
	 * @param art
	 * 		Die Repraesentation der Schriftart als Zahl:<br/> 0: Normaler Text<br /> 1: Fett<br /> 2:
	 * 		Kursiv<br /> 3: Fett & Kursiv<br /> <br /> Ist die Eingabe nicht eine dieser 4 Zahlen, so
	 * 		wird nichts geaendert.<br /> Parallele Methode zu <code>setzeSchriftart()</code>
	 *
	 * @see #setzeSchriftart(int)
	 */
	public void schriftartSetzen (int art) {
		setzeSchriftart(art);
	}

	/**
	 * Setzt die Schriftart.
	 *
	 * @param art
	 * 		Die Repraesentation der Schriftart als Zahl:<br/> 0: Normaler Text<br /> 1: Fett<br /> 2:
	 * 		Kursiv<br /> 3: Fett & Kursiv<br /> <br /> Ist die Eingabe nicht eine dieser 4 Zahlen, so
	 * 		wird nichts geaendert.
	 */
	public void setzeSchriftart (int art) {
		if (art >= 0 && art <= 3) {
			schriftart = art;
			aktualisieren();
		}
	}

	/**
	 * Klasseninterne Methode zum aktualisieren des Font-Objektes
	 */
	private void aktualisieren () {
		this.font = this.font.deriveFont(schriftart, groesse);
	}

	/**
	 * Setzt die Fuellfarbe<br /> Parallele Methode zu <code>setzeFarbe()</code>
	 *
	 * @param farbe
	 * 		Der Name der neuen Fuellfarbe
	 *
	 * @see #setzeFarbe(String)
	 * @see #farbeSetzen(Farbe)
	 */
	public void farbeSetzen (String farbe) {
		setzeFarbe(farbe);
	}

	/**
	 * Setzt die Fuellfarbe
	 *
	 * @param farbe
	 * 		Der Name der neuen Fuellfarbe
	 */
	public void setzeFarbe (String farbe) {
		this.setzeFarbe(Farbe.zuFarbeKonvertieren(farbe));
	}

	/**
	 * Setzt die Fuellfarbe
	 *
	 * @param c
	 * 		Die neue Fuellfarbe
	 */
	public void setzeFarbe (Color c) {
		farbe = c;
		aktualisieren();
	}

	/**
	 * Setzt die Fuellfarbe
	 *
	 * @param f
	 * 		Das Farbe-Objekt, das die neue Fuellfarbe beschreibt
	 *
	 * @see #farbeSetzen(String)
	 */
	public void farbeSetzen (Farbe f) {
		setzeFarbe(f.wert());
	}

	/**
	 * Setzt die Schriftgroesse.<br /> Wrappt hierbei die Methode <code>setzeGroesse</code>.
	 *
	 * @param groesse
	 * 		Die neue Schriftgroesse
	 *
	 * @see #setzeGroesse(int)
	 */
	public void groesseSetzen (int groesse) {
		setzeGroesse(groesse);
	}

	/**
	 * Setzt die Schriftgroesse
	 *
	 * @param groesse
	 * 		Die neue Schriftgroesse
	 */
	public void setzeGroesse (int groesse) {
		this.groesse = groesse;
		aktualisieren();
	}

	/**
	 * Diese Methode gibt die aktuelle Groesse des Textes aus
	 *
	 * @return Die aktuelle Schriftgroesse des Textes
	 *
	 * @see #groesseSetzen(int)
	 */
	public int groesse () {
		return groesse;
	}

	/**
	 * Setzt einen neuen Font fuer den Text.<br /> Parallele Methode zu <code>setzeFont()</code>
	 *
	 * @param name
	 * 		Der Name des neuen Fonts fuer den Text
	 *
	 * @see #setzeFont(String)
	 */
	public void fontSetzen (String name) {
		setzeFont(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(Graphics2D g) {


		fontMetrics = g.getFontMetrics(font);

		int x = 0;

		if (anker == Anker.MITTE) {
			x = - fontMetrics.stringWidth(inhalt) / 2;
		} else if (anker == Anker.RECHTS) {
			x = - fontMetrics.stringWidth(inhalt);
		}

		g.setColor(farbe);
		g.setFont(font);
		g.drawString(inhalt, (int) (x), (int)groesse);
	}

	/**
	 * Gibt den aktuellen Anker zurück.
	 *
	 * @return aktueller Anker
	 *
	 * @see ea.Text.Anker
	 * @see #setAnker(ea.Text.Anker)
	 */
	public Anker getAnker () {
		return anker;
	}

	/**
	 * Setzt den Textanker. Dies beschreibt, wo sich der Text relativ zur x-Koordinate befindet.
	 * Möglich sind: <li>{@code Text.Anker.LINKS},</li> <li>{@code Text.Anker.MITTE},</li>
	 * <li>{@code Text.Anker.RECHTS}.</li> <br> <b>Hinweis</b>: {@code null} wird wie {@code
	 * Anker.LINKS} behandelt!
	 *
	 * @param anker
	 * 		neuer Anker
	 *
	 * @see ea.Text.Anker
	 * @see #getAnker()
	 */
	public void setAnker (Anker anker) {
		this.anker = anker;
	}

    @Override
    public Shape berechneShape(float pixelProMeter) {
        if(fontMetrics == null) {
            fontMetrics = super.getPhysikHandler().worldHandler().getWorldThread().getMaster().getGame().real_fenster.getFontMetrics(font);
            //throw new IllegalStateException("Text wurde nach Shape gefragt, bevor die Metrik bekannt war.");
        }
        return berechneBoxShape(pixelProMeter, fontMetrics.stringWidth(inhalt), fontMetrics.getHeight());
    }

    /**
	 * Ein Textanker beschreibt, wo sich der Text relativ zu seiner x-Koordinate befindet. Möglich
	 * sind: <li>{@code Anker.LINKS},</li> <li>{@code Anker.MITTE},</li> <li>{@code
	 * Anker.RECHTS}.</li>
	 *
	 * @see #setAnker(ea.Text.Anker)
	 * @see #getAnker()
	 */
	public enum Anker {
		LINKS, MITTE, RECHTS
	}
}
