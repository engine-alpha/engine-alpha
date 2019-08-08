/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2019 Michael Andonie and contributors.
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

import ea.internal.ShapeBuilder;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.internal.io.FontLoader;
import org.jbox2d.collision.shapes.Shape;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 * Zur Darstellung von Texten im Programmbildschirm.
 *
 * @author Michael Andonie
 * @author Niklas Keller
 */
public class Text extends Geometry {
    private static final int SIZE = 92;

    @Internal
    private static Shape createShape(String content, float height, Font font) {
        FontMetrics fontMetrics = ea.internal.util.FontMetrics.get(font);

        int widthInPixels = fontMetrics.stringWidth(content);
        int heightInPixels = fontMetrics.getHeight();

        return ShapeBuilder.createSimpleRectangularShape(widthInPixels * height / heightInPixels, height);
    }

    /**
     * Höhe des Textes.
     */
    private float height;

    /**
     * Die Schriftart (<b>fett, kursiv, oder fett & kursiv</b>).<br> Dies wird dargestellt als int.Wert:<br> 0:
     * Normaler Text<br> 1: Fett<br> 2: Kursiv<br> 3: Fett & Kursiv
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
     * Textanker: links, mittig oder rechts
     */
    private Anchor anchor = Anchor.LEFT;

    /**
     * Konstruktor für Objekte der Klasse Text<br> Möglich ist es auch, Fonts zu laden, die im Projektordner sind.
     * Diese werden zu Anfang einmalig geladen und stehen dauerhaft zur Verfügung.
     *
     * @param content  Die Zeichenkette, die dargestellt werden soll
     * @param fontName Der Name des zu verwendenden Fonts.<br> Wird hierfuer ein Font verwendet, der in dem
     *                 Projektordner vorhanden sein soll, <b>und dies ist immer und in jedem Fall zu empfehlen</b>, muss
     *                 der Name der Schriftart hier ebenfalls einfach nur eingegeben werden, <b>nicht der Name der
     *                 schriftart-Datei!</b>
     * @param height   Die Breite
     * @param style    Die Schriftart dieses Textes. Folgende Werte entsprechen folgendem:<br> 0: Normaler Text<br>
     *                 1: Fett<br> 2: Kursiv<br> 3: Fett &amp; Kursiv <br> <br> Alles andere sorgt nur für einen
     *                 normalen Text.
     */
    @API
    public Text(String content, float height, String fontName, int style) {
        super(() -> createShape(content == null ? "" : content, height, FontLoader.loadByName(fontName).deriveFont(style, SIZE)));

        this.content = content == null ? "" : content;
        this.height = height;

        setStyle(style);
        setFont(fontName);
    }

    /**
     * Erstellt einen Text mit spezifischem Inhalt und Font. Der Text ist in Schriftgröße 12, nicht fett, nicht kursiv.
     *
     * @param content  Der Inhalt, der dargestellt wird
     * @param height   Die Höhe
     * @param fontName Der Font, in dem der Text dargestellt werden soll.
     */
    @API
    public Text(String content, float height, String fontName) {
        this(content, height, fontName, 0);
    }

    /**
     * Erstellt einen Text mit spezifischem Inhalt und spezifischer Größe. Die Schriftart ist ein Standard-Font
     * (Serifenfrei), nicht fett, nicht kursiv.
     *
     * @param content Der Inhalt, der dargestellt wird
     * @param height  Die Höhe
     */
    @API
    public Text(String content, float height) {
        this(content, height, Font.SANS_SERIF, 0);
    }

    /**
     * Setzt einen neuen Font für den Text.
     *
     * @param fontName Name des neuen Fonts für den Text
     */
    @API
    public void setFont(String fontName) {
        this.font = FontLoader.loadByName(fontName).deriveFont(fontStyle, SIZE);
        this.update();
    }

    /**
     * Setzt den Inhalt des Textes.
     *
     * @param content Der neue Inhalt des Textes
     */
    @API
    public void setContent(String content) {
        String normalizedContent = content;
        if (normalizedContent == null) {
            normalizedContent = "";
        }

        if (!this.content.equals(normalizedContent)) {
            this.content = normalizedContent;
            this.update();
        }
    }

    /**
     * Setzt den Stil der Schriftart (Fett/Kursiv/Fett&amp;Kursiv/Normal).
     *
     * @param style Die Repräsentation der Schriftart als Zahl:<br> 0: Normaler Text<br> 1: Fett<br> 2: Kursiv<br>
     *              3: Fett &amp; Kursiv<br> <br> Ist die Eingabe nicht eine dieser 4 Zahlen, so wird nichts
     *              geändert.
     */
    public void setStyle(int style) {
        if (style >= 0 && style <= 3 && style != this.fontStyle) {
            fontStyle = style;
            font = font.deriveFont(style, SIZE);
            this.update();
        }
    }

    public void setHeight(float height) {
        if (this.height != height) {
            this.height = height;
            this.update();
        }
    }

    @Internal
    private void update() {
        setShape(() -> createShape(content, height, font));
    }

    @Override
    @Internal
    public void render(Graphics2D g, float pixelPerMeter) {
        FontMetrics fontMetrics = g.getFontMetrics(font);

        int widthInPixels = fontMetrics.stringWidth(content);

        int x = 0;

        if (anchor == Anchor.CENTER) {
            x = -widthInPixels / 2;
        } else if (anchor == Anchor.RIGHT) {
            x = -widthInPixels;
        }

        AffineTransform pre = g.getTransform();
        Font preFont = g.getFont();

        float scaleFactor = height * pixelPerMeter / SIZE;

        g.setColor(getColor());
        g.scale(scaleFactor, scaleFactor);
        g.setFont(font);
        g.drawString(content, x, -fontMetrics.getDescent());

        g.setFont(preFont);
        g.setTransform(pre);
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
     * <ul>
     * <li>{@code Text.Anchor.LEFT},</li>
     * <li>{@code Text.Anchor.CENTER},</li>
     * <li>{@code Text.Anchor.RIGHT}.</li>
     * </ul><br><b>Hinweis</b>: {@code null} wird wie {@code
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
     * Ein Textanker beschreibt, wo sich der Text relativ zu seiner getX-Koordinate befindet. Möglich sind:
     * <ul>
     * <li>{@code Anchor.LEFT},</li>
     * <li>{@code Anchor.CENTER},</li>
     * <li>{@code Anchor.RIGHT}.</li>
     * </ul>
     *
     * @see #setAnchor(Anchor)
     * @see #getAnchor()
     */
    public enum Anchor {
        LEFT, CENTER, RIGHT
    }
}
