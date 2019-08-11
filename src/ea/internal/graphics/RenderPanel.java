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

package ea.internal.graphics;

import ea.Camera;
import ea.Scene;
import ea.Vector;
import ea.internal.DebugInfo;
import ea.internal.annotations.Internal;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Dies ist das Panel, in dem die einzelnen Dinge gezeichnet werden.
 *
 * @author Michael Andonie
 * @author Niklas Keller
 */
public final class RenderPanel extends Canvas {
    private static final Color COLOR_FPS_BACKGROUND = new Color(255, 255, 255, 50);
    private static final Color COLOR_FPS_BORDER = new Color(0, 106, 214);
    private static final Color COLOR_BODY_COUNT_BORDER = new Color(0, 214, 84);
    private static final Color COLOR_BODY_COUNT_BACKGROUND = new Color(255, 255, 255, 50);
    private static final int DEBUG_INFO_HEIGHT = 20;
    private static final int DEBUG_INFO_LEFT = 10;
    private static final int DEBUG_INFO_TEXT_OFFSET = 16;
    private static final Color DEBUG_GRID_COLOR = new Color(255, 255, 255, 100);
    public static final int GRID_SIZE_IN_PIXELS = 150;
    public static final int GRID_SIZE_METER_LIMIT = 100000;
    public static final int DEBUG_TEXT_SIZE = 12;

    /**
     * Konstruktor für Objekte der Klasse RenderPanel
     *
     * @param width  Die Größe des Einflussbereichs des Panels in Richtung <code>getX</code>.
     * @param height Die Größe des Einflussbereichs des Panels in Richtung <code>getY</code>.
     */
    public RenderPanel(int width, int height) {
        this.setSize(width, height);
        this.setPreferredSize(this.getSize());
    }

    /**
     * Muss aufgerufen werden, nachdem das Fenster isVisible ist, um die BufferStrategy zu erzeugen.
     */
    final public void allocateBuffers() {
        createBufferStrategy(2);
    }

    /**
     * Führt die gesamte Zeichenroutine aus.
     *
     * @param g Zeichenobjekt.
     */
    @Internal
    public void render(Graphics2D g, Scene scene) {
        // Absoluter Hintergrund
        g.setColor(Color.black);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        AffineTransform transform = g.getTransform();

        scene.render(g, getWidth(), getHeight());

        g.setTransform(transform);
    }

    /**
     * Rendert Debug-Informationen auf dem Bildschirm.
     *
     * @param g Das Graphics-Objekt zum zeichnen.
     */
    @Internal
    public void renderGrid(Graphics2D g, Scene scene) {
        AffineTransform pre = g.getTransform();

        Camera camera = scene.getCamera();
        Vector position = camera.getPosition();
        float rotation = -camera.getRotation();

        int width = getWidth();
        int height = getHeight();

        g.setClip(0, 0, width, height);
        g.translate(width / 2, height / 2);

        float pixelPerMeter = camera.getZoom();

        g.rotate(rotation, 0, 0);
        g.translate(-position.getX() * pixelPerMeter, position.getY() * pixelPerMeter);

        int gridSizeInMeters = Math.round(GRID_SIZE_IN_PIXELS / pixelPerMeter);
        float gridSizeInPixels = gridSizeInMeters * pixelPerMeter;
        float gridSizeFactor = gridSizeInPixels / gridSizeInMeters;

        if (gridSizeInMeters > 0 && gridSizeInMeters < GRID_SIZE_METER_LIMIT) {
            int windowSizeInPixels = (int) Math.ceil(Math.max(width, height));

            int startX = (int) (position.getX() - windowSizeInPixels / 2 / pixelPerMeter);
            int startY = (int) ((-1 * position.getY()) - windowSizeInPixels / 2 / pixelPerMeter);

            startX -= (startX % gridSizeInMeters) + gridSizeInMeters;
            startY -= (startY % gridSizeInMeters) + gridSizeInMeters;

            startX -= gridSizeInMeters;

            int stopX = (int) (startX + windowSizeInPixels / pixelPerMeter + gridSizeInMeters * 2);
            int stopY = (int) (startY + windowSizeInPixels / pixelPerMeter + gridSizeInMeters * 2);

            g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, DEBUG_TEXT_SIZE));
            g.setColor(DEBUG_GRID_COLOR);

            for (int x = startX; x < stopX; x += gridSizeInMeters) {
                g.fillRect((int) (x * gridSizeFactor) - 1, (int) ((startY - 1) * gridSizeFactor), 2, (int) (windowSizeInPixels + 2 * gridSizeInPixels));
            }

            for (int y = startY; y < stopY; y += gridSizeInMeters) {
                g.fillRect((int) ((startX - 1) * gridSizeFactor), (int) (y * gridSizeFactor - 1), (int) (windowSizeInPixels + 2 * gridSizeInPixels), 2);
            }

            for (int x = startX; x < stopX; x += gridSizeInMeters) {
                for (int y = startY; y < stopY; y += gridSizeInMeters) {
                    g.drawString(x + " / " + -y, x * gridSizeFactor + 5, y * gridSizeFactor - 5);
                }
            }
        }

        g.setTransform(pre);
    }

    /**
     * Rendert zusätzliche Debug-Infos auf dem Bildschirm.
     *
     * @param g Das Graphics-Objekt zum zeichnen.
     */
    @Internal
    public void renderInfo(Graphics2D g, DebugInfo debugInfo) {
        float frameDuration = debugInfo.getFrameDuration();
        int bodyCount = debugInfo.getBodyCount();

        Font displayFont = new Font("Monospaced", Font.PLAIN, DEBUG_TEXT_SIZE);
        FontMetrics fm = g.getFontMetrics(displayFont);
        Rectangle2D bounds;
        int y = 10;

        String fpsMessage = "FPS: " + (frameDuration == 0 ? "∞" : Math.round(1 / frameDuration));
        bounds = fm.getStringBounds(fpsMessage, g);

        g.setColor(COLOR_FPS_BORDER);
        g.fillRect(DEBUG_INFO_LEFT, y, (int) bounds.getWidth() + DEBUG_INFO_HEIGHT, (int) bounds.getHeight() + DEBUG_INFO_TEXT_OFFSET);
        g.setColor(COLOR_FPS_BACKGROUND);
        g.drawRect(DEBUG_INFO_LEFT, y, (int) bounds.getWidth() + DEBUG_INFO_HEIGHT - 1, (int) bounds.getHeight() + DEBUG_INFO_TEXT_OFFSET - 1);

        g.setColor(Color.WHITE);
        g.setFont(displayFont);
        g.drawString(fpsMessage, DEBUG_INFO_LEFT + 10, y + 8 + fm.getHeight() - fm.getDescent());

        y += fm.getHeight() + DEBUG_INFO_HEIGHT;

        String bodyMessage = "Bodies: " + bodyCount;
        bounds = fm.getStringBounds(bodyMessage, g);

        g.setColor(COLOR_BODY_COUNT_BORDER);
        g.fillRect(DEBUG_INFO_LEFT, y, (int) bounds.getWidth() + DEBUG_INFO_HEIGHT, (int) bounds.getHeight() + DEBUG_INFO_TEXT_OFFSET);
        g.setColor(COLOR_BODY_COUNT_BACKGROUND);
        g.drawRect(DEBUG_INFO_LEFT, y, (int) bounds.getWidth() + DEBUG_INFO_HEIGHT - 1, (int) bounds.getHeight() + DEBUG_INFO_TEXT_OFFSET - 1);

        g.setColor(Color.WHITE);
        g.setFont(displayFont);
        g.drawString(bodyMessage, DEBUG_INFO_LEFT + 10, y + 8 + fm.getHeight() - fm.getDescent());
    }
}
