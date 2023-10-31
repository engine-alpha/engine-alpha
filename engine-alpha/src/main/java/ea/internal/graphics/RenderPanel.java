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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;

/**
 * Dies ist das Panel, in dem die einzelnen Dinge gezeichnet werden.
 *
 * @author Michael Andonie
 * @author Niklas Keller
 */
public final class RenderPanel extends Canvas implements RenderTarget {
    /**
     * Konstruktor für Objekte der Klasse RenderPanel
     *
     * @param width  Die Größe des Einflussbereichs des Panels in Richtung <code>getX</code> in Pixel.
     * @param height Die Größe des Einflussbereichs des Panels in Richtung <code>getY</code> in Pixel.
     */
    public RenderPanel(int width, int height) {
        this.setSize(width, height);
        this.setPreferredSize(this.getSize());
        this.setBackground(Color.BLACK);
    }

    /**
     * Muss aufgerufen werden, nachdem das Fenster isVisible ist, um die BufferStrategy zu erzeugen.
     */
    public void allocateBuffers() {
        createBufferStrategy(2);
    }

    public void render(RenderSource source) {
        BufferStrategy bufferStrategy = getBufferStrategy();

        do {
            do {
                source.render((Graphics2D) bufferStrategy.getDrawGraphics(), getWidth(), getHeight());
            } while (bufferStrategy.contentsRestored() && !Thread.currentThread().isInterrupted());

            if (!bufferStrategy.contentsLost()) {
                bufferStrategy.show();

                Toolkit.getDefaultToolkit().sync();
            }
        } while (bufferStrategy.contentsLost() && !Thread.currentThread().isInterrupted());
    }
}
