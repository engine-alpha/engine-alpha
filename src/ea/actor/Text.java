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

package ea.actor;

import ea.Point;
import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
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
 * <p>
 * TODO: Allow Custom Colors
 *
 * @author Michael Andonie
 */
public class Text extends Actor {
    /**
     * Alle möglichen Fontnamen des Systems, auf dem man sich gerade befindet.<br /> Hiernach werden
     * Überprüfungen gemacht, ob die gewünschte Schriftart auch auf dem hiesigen System vorhanden
     * ist.
     */
    public static final String[] fontNamen;

    /**
     * Ein Feld aller existenten Fonts, die im Hauptprojektordner gespeichert sind.<br /> Macht das
     * interne verwenden dieser Fonts moeglich, ohne das Vorhandensein der Fonts in den
     * Computerressourcen selber zur Voraussetzung zu haben.
     */
    private static Font[] eigene;

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
    protected Color color = Color.WHITE;

    /**
     * Textanker: links, mittig oder rechts
     */
    private Anchor anchor = Anchor.LINKS;

    /**
     * Referenz auf die jüngsten Font-Metriken, die für die Berechnung der Textmaße verwendet
     * wurden.
     */
    private FontMetrics fontMetrics;

    /**
     * Konstruktor für Objekte der Klasse Text<br /> Möglich ist es auch, Fonts zu laden, die im
     * Projektordner sind. Diese werden zu Anfang einmalig geladen und stehen dauerhaft zur
     * Verfügung.
     *
     * @param content         Die Zeichenkette, die dargestellt werden soll
     * @param fontName       Der Name des zu verwendenden Fonts.<br /> Wird hierfuer ein Font
     *                       verwendet, der in dem Projektordner vorhanden sein soll, <b>und dies
     *                       ist immer und in jedem Fall zu empfehlen</b>, muss der Name der
     *                       Schriftart hier ebenfalls einfach nur eingegeben werden, <b>nicht der
     *                       Name der schriftart-Datei!</b>
     * @param size Die Groesse, in der die Schrift dargestellt werden soll
     * @param type     Die Schriftart dieses Textes. Folgende Werte entsprechen folgendem:<br
     *                       /> 0: Normaler Text<br /> 1: Fett<br /> 2: Kursiv<br /> 3: Fett &
     *                       Kursiv <br /> <br /> Alles andere sorgt nur fuer einen normalen Text.
     */
    @API
    public Text(String content, String fontName, int size, int type) {
        this.inhalt = content;
        this.groesse = size;

        if (type >= 0 && type <= 3) {
            this.schriftart = type;
        } else {
            this.schriftart = 0;
        }

        //TODO auskommentieren rückgängig machen
        setFont(fontName);
    }

    /**
     * Erstellt einen Text mit spezifischem Inhalt und Font.
     * Der Text ist in Schriftgröße 12, nicht fett, nicht kursiv.
     * @param content       Der Inhalt, der dargestellt wird
     * @param fontName      Der Font, in dem der Text dargestellt werden soll.
     */
    @API
    public Text(String content, String fontName) {
        this(content, fontName, 12, 0);
    }

    /**
     * Erstellt einen Text mit spezifischem Inhalt und spezifischer Größe.
     * Die Schriftart ist ein Standard-Font (Serifenfrei), nicht fett, nicht kursiv.
     * @param content       Der Inhalt, der dargestellt wird
     * @param size          Die Schriftgröße
     */
    @API
    public Text(String content, int size) {
        this(content, "SansSerif", size, 0);
    }

    /**
     * Erstellt einen Text mit spezifischem Inhalt und spezifischer Größe.
     * Die Schriftart ist ein Standard-Font (Serifenfrei), Größe 12, nicht fett, nicht kursiv.
     * @param content       Der Inhalt, der dargestellt wird
     */
    @API
    public Text(String content) {
        this(content, 12);
    }

    /**
     * Prüft, ob ein Font auf diesem Computer existiert.
     *
     * @param fontName Der Name des zu ueberpruefenden Fonts
     *
     * @return <code>true</code>, falls der Font auf dem System existiert, sonst <code>false</code>
     */
    @API
    public static boolean fontExists(String fontName) {
        for (String s : fontNamen) {
            if (s.equals(fontName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Setzt einen neuen Font fuer den Text
     *
     * @param fontName Der Name des neuen Fonts fuer den Text
     */
    @API
    public void setFont(String fontName) {
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
            if (!fontExists(fontName)) {
                fontName = "SansSerif";
                Logger.error("Text/Fonts", "Achtung! Die gewuenschte Schriftart existiert nicht im Font-Verzeichnis dieses PC! " + "Wurde der Name falsch geschrieben? Oder existiert der Font nicht?");
            }
            this.font = new Font(fontName, schriftart, groesse);
        }
    }

    private static void fontsEinbauen(final ArrayList<File> liste, File akt) {
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
    public static Font holeFont(String fontName) {
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
            if (!fontExists(fontName)) {
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
     * empfohlen, vectorFromThisTo dem alle zu verwendenden Arten im Projektordner liegen, denn nur unter dem an
     * die Konsole projezierten Namen <b>koennen diese ueberhaupt verwendet werden</b>!!<br /> Daher
     * dient diese Methode der Praevention von Verwirrung, wegen "nicht darstellbarer" Fonts.
     */
    public static void geladeneSchriftartenAusgeben() {
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
     * Setzt den Inhalt des Textes.
     *
     * @param content Der neue Inhalt des Textes
     */
    @API
    public void setContent(String content) {
        this.inhalt = content;
    }

    /**
     * Setzt den Stil der Schriftart (Fett/Kursiv/Fett&Kursiv/Normal).
     *
     * @param style Die Repraesentation der Schriftart als Zahl:<br/> 0: Normaler Text<br /> 1:
     *            Fett<br /> 2: Kursiv<br /> 3: Fett & Kursiv<br /> <br /> Ist die Eingabe nicht
     *            eine dieser 4 Zahlen, so wird nichts geaendert.
     */
    public void setStyle(int style) {
        if (style >= 0 && style <= 3) {
            schriftart = style;
            aktualisieren();
        }
    }

    /**
     * Klasseninterne Methode zum aktualisieren des Font-Objektes
     */
    @NoExternalUse
    private void aktualisieren() {
        this.font = this.font.deriveFont(schriftart, groesse);
    }

    /**
     * Setzt die Füllfarbe des Textes.
     *
     * @param color Die Farbe, in der der Text dargestellt werden soll.
     */
    @API
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Setzt die Schriftgroesse, in der der Text dargestellt werden soll.
     *
     * @param size Die neue Schriftgroesse
     */
    @API
    public void setSize(int size) {
        this.groesse = size;
        aktualisieren();
    }

    /**
     * Diese Methode gibt die aktuelle Groesse des Textes aus
     *
     * @return Die aktuelle Schriftgroesse des Textes
     *
     * @see #setSize(int)
     */
    @API
    public int getSize() {
        return groesse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NoExternalUse
    public void render(Graphics2D g) {

        fontMetrics = g.getFontMetrics(font);

        int x = 0;

        if (anchor == Anchor.MITTE) {
            x = -fontMetrics.stringWidth(inhalt) / 2;
        } else if (anchor == Anchor.RECHTS) {
            x = -fontMetrics.stringWidth(inhalt);
        }

        g.setColor(color);
        g.setFont(font);
        g.drawString(inhalt, (int) (x), (int) groesse);
    }

    /**
     * Gibt den aktuellen Anchor zurück.
     *
     * @return aktueller Anchor
     *
     * @see Anchor
     * @see #setAnchor(Anchor)
     */
    @API
    public Anchor getAnchor() {
        return anchor;
    }

    /**
     * Setzt den Textanker. Dies beschreibt, wo sich der Text relativ zur getX-Koordinate befindet.
     * Möglich sind: <li>{@code Text.Anchor.LINKS},</li> <li>{@code Text.Anchor.MITTE},</li>
     * <li>{@code Text.Anchor.RECHTS}.</li> <br> <b>Hinweis</b>: {@code null} wird wie {@code
     * Anchor.LINKS} behandelt!
     *
     * @param anchor neuer Anchor
     *
     * @see Anchor
     * @see #getAnchor()
     */
    @API
    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
    }

    @Override
    @NoExternalUse
    public Shape createShape(float pixelProMeter) {
        if (fontMetrics == null) {
            fontMetrics = ea.util.FontMetrics.get(font);
        }

        return berechneBoxShape(pixelProMeter, fontMetrics.stringWidth(inhalt), fontMetrics.getHeight());
    }

    /**
     * Ein Textanker beschreibt, wo sich der Text relativ zu seiner getX-Koordinate befindet. Möglich
     * sind: <li>{@code Anchor.LINKS},</li> <li>{@code Anchor.MITTE},</li> <li>{@code
     * Anchor.RECHTS}.</li>
     *
     * @see #setAnchor(Anchor)
     * @see #getAnchor()
     */
    public enum Anchor {
        LINKS, MITTE, RECHTS
    }
}
