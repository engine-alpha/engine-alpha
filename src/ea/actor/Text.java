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

import ea.Scene;
import ea.internal.ShapeHelper;
import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import ea.internal.io.FontLoader;

import java.awt.*;

/**
 * Zur Darstellung von Texten im Programmbildschirm.
 * <p/>
 * TODO: Review der ganzen Klasse (v.a. der Dokumentation)
 * <p>
 * TODO: Allow Custom Colors
 *
 * @author Michael Andonie
 * @author Niklas Keller
 */
public class Text extends Actor {
    private static final int DEFAULT_SIZE = 12;

    /**
     * Die Schriftgröße des Textes
     */
    private int size;

    /**
     * Die Schriftart (<b>fett, kursiv, oder fett & kursiv</b>).<br /> Dies wird dargestellt als int.Wert:<br /> 0:
     * Normaler Text<br /> 1: Fett<br /> 2: Kursiv<br /> 3: Fett & Kursiv
     */
    private int fontStyle;

    /**
     * Der Wert des Textes
     */
    private String content;

    /**
     * Der Font der Darstellung
     */
    private Font font;

    /**
     * Die Farbe, in der der Text dargestellt wird.
     */
    private Color color = Color.WHITE;

    /**
     * Textanker: links, mittig oder rechts
     */
    private Anchor anchor = Anchor.LEFT;

    /**
     * Konstruktor für Objekte der Klasse Text<br /> Möglich ist es auch, Fonts zu laden, die im Projektordner sind.
     * Diese werden zu Anfang einmalig geladen und stehen dauerhaft zur Verfügung.
     *
     * @param content  Die Zeichenkette, die dargestellt werden soll
     * @param fontName Der Name des zu verwendenden Fonts.<br /> Wird hierfuer ein Font verwendet, der in dem
     *                 Projektordner vorhanden sein soll, <b>und dies ist immer und in jedem Fall zu empfehlen</b>, muss
     *                 der Name der Schriftart hier ebenfalls einfach nur eingegeben werden, <b>nicht der Name der
     *                 schriftart-Datei!</b>
     * @param size     Die Groesse, in der die Schrift dargestellt werden soll
     * @param type     Die Schriftart dieses Textes. Folgende Werte entsprechen folgendem:<br /> 0: Normaler Text<br />
     *                 1: Fett<br /> 2: Kursiv<br /> 3: Fett & Kursiv <br /> <br /> Alles andere sorgt nur fuer einen
     *                 normalen Text.
     */
    @API
    public Text(Scene scene, String content, String fontName, int size, int type) {
        super(scene, () -> {
            Font font = FontLoader.loadByName(fontName).deriveFont(type, size);
            FontMetrics fontMetrics = ea.util.FontMetrics.get(font);

            return ShapeHelper.createRectangularShape(
                    fontMetrics.stringWidth(content) / scene.getWorldHandler().getPixelProMeter(),
                    fontMetrics.getHeight() / scene.getWorldHandler().getPixelProMeter()
            );
        });

        this.content = content;
        this.size = size;

        if (type >= 0 && type <= 3) {
            this.fontStyle = type;
        } else {
            this.fontStyle = 0;
        }

        setFont(fontName);
    }

    /**
     * Erstellt einen Text mit spezifischem Inhalt und Font. Der Text ist in Schriftgröße 12, nicht fett, nicht kursiv.
     *
     * @param content  Der Inhalt, der dargestellt wird
     * @param fontName Der Font, in dem der Text dargestellt werden soll.
     */
    @API
    public Text(Scene scene, String content, String fontName) {
        this(scene, content, fontName, DEFAULT_SIZE, 0);
    }

    /**
     * Erstellt einen Text mit spezifischem Inhalt und spezifischer Größe. Die Schriftart ist ein Standard-Font
     * (Serifenfrei), nicht fett, nicht kursiv.
     *
     * @param content Der Inhalt, der dargestellt wird
     * @param size    Die Schriftgröße
     */
    @API
    public Text(Scene scene, String content, int size) {
        this(scene, content, Font.SANS_SERIF, size, 0);
    }

    /**
     * Erstellt einen Text mit spezifischem Inhalt und spezifischer Größe. Die Schriftart ist ein Standard-Font
     * (Serifenfrei), Größe 12, nicht fett, nicht kursiv.
     *
     * @param content Der Inhalt, der dargestellt wird
     */
    @API
    public Text(Scene scene, String content) {
        this(scene, content, DEFAULT_SIZE);
    }

    /**
     * Setzt einen neuen Font für den Text.
     *
     * @param fontName Name des neuen Fonts für den Text
     */
    @API
    public void setFont(String fontName) {
        this.font = FontLoader.loadByName(fontName);
        aktualisieren();
    }

    /**
     * Setzt den Inhalt des Textes.
     *
     * @param content Der neue Inhalt des Textes
     */
    @API
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Setzt den Stil der Schriftart (Fett/Kursiv/Fett&Kursiv/Normal).
     *
     * @param style Die Repraesentation der Schriftart als Zahl:<br/> 0: Normaler Text<br /> 1: Fett<br /> 2: Kursiv<br
     *              /> 3: Fett & Kursiv<br /> <br /> Ist die Eingabe nicht eine dieser 4 Zahlen, so wird nichts
     *              geaendert.
     */
    public void setStyle(int style) {
        if (style >= 0 && style <= 3) {
            fontStyle = style;
            aktualisieren();
        }
    }

    /**
     * Klasseninterne Methode zum aktualisieren des Font-Objektes
     */
    @NoExternalUse
    private void aktualisieren() {
        this.font = this.font.deriveFont(fontStyle, size);
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
        if (size <= 0) {
            throw new RuntimeException("Die Schriftgröße muss größer als 0 sein. Sie war " + size + ".");
        }
        this.size = size;
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
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NoExternalUse
    public void render(Graphics2D g) {
        FontMetrics fontMetrics = g.getFontMetrics(font);

        int x = 0;

        if (anchor == Anchor.CENTER) {
            x = -fontMetrics.stringWidth(content) / 2;
        } else if (anchor == Anchor.RIGHT) {
            x = -fontMetrics.stringWidth(content);
        }

        g.setColor(color);
        g.setFont(font);
        g.drawString(content, x, -fontMetrics.getHeight() + size);
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
     * Setzt den Textanker. Dies beschreibt, wo sich der Text relativ zur getX-Koordinate befindet. Möglich sind:
     * <li>{@code Text.Anchor.LEFT},</li> <li>{@code Text.Anchor.CENTER},</li>
     * <li>{@code Text.Anchor.RIGHT}.</li> <br> <b>Hinweis</b>: {@code null} wird wie {@code
     * Anchor.LEFT} behandelt!
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

    /**
     * Ein Textanker beschreibt, wo sich der Text relativ zu seiner getX-Koordinate befindet. Möglich sind: <li>{@code
     * Anchor.LEFT},</li> <li>{@code Anchor.CENTER},</li> <li>{@code Anchor.RIGHT}.</li>
     *
     * @see #setAnchor(Anchor)
     * @see #getAnchor()
     */
    public enum Anchor {
        LEFT, CENTER, RIGHT
    }
}
