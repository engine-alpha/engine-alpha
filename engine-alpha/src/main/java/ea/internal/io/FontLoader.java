/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2018 Michael Andonie and contributors.
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

package ea.internal.io;

import ea.internal.annotations.API;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@API
final public class FontLoader {
    private static final int DEFAULT_SIZE = 12;

    /**
     * Alle möglichen Schriftnamen des Systems, auf dem man sich gerade befindet.<br>
     * Hiernach werden Überprüfungen gemacht, ob die gewünschte Schriftart auf dem System vorhanden
     * ist.
     */
    public static final String[] systemFonts;

    /**
     * Alle geladenen Fonts, die manuell eingebunden sind.<br />
     * Macht das Verwenden dieser Schriften möglich, ohne dass die Schriftart auf dem System
     * vorhanden ist.
     */
    private static final Map<String, Font> userFonts = new ConcurrentHashMap<>();

    static {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        systemFonts = ge.getAvailableFontFamilyNames();
    }

    /**
     * Prüft, ob ein Font auf diesem System vorhanden ist.
     *
     * @param fontName Der Name des zu überprüfenden Fonts.
     *
     * @return <code>true</code>, falls der Font auf dem System existiert, sonst <code>false</code>.
     */
    @API
    public static boolean isSystemFont(String fontName) {
        for (String s : systemFonts) {
            if (s.equals(fontName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gibt eine Liste der Namen der Systemschriftarten zurück.
     *
     * @return Liste mit Systemschriftarten.
     */
    @API
    public static String[] getSystemFonts() {
        return systemFonts.clone();
    }

    /**
     * Lädt eine Systemschriftart basierend auf dem Namen.
     *
     * @param fontName Name des Fonts.
     *
     * @return Geladener Font.
     */
    @API
    public static Font loadByName(String fontName) {
        return new Font(fontName, Font.PLAIN, DEFAULT_SIZE);
    }

    /**
     * Lädt eine Schriftart basierend auf dem Dateinamen.
     *
     * @param filename Dateiname des Fonts.
     *
     * @return Geladener Font.
     */
    @API
    public static Font loadFromFile(String filename) {
        if (userFonts.containsKey(filename)) {
            return userFonts.get(filename);
        }

        try (InputStream stream = ResourceLoader.loadAsStream(filename)) {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(Font.PLAIN);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);

            userFonts.put(filename, customFont);

            return customFont;
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException("Die angegebene Schriftart konnte nicht geladen werden: " + filename);
        }
    }

    private FontLoader() {
        // keine Objekte erlaubt!
    }
}
