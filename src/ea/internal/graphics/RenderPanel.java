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

import java.awt.*;
import java.util.concurrent.Phaser;

/**
 * Dies ist das Panel, in dem die einzelnen Dinge gezeichnet werden.
 *
 * @author Michael Andonie
 * @author Niklas Keller
 */
public abstract class RenderPanel extends Canvas {
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
    public abstract void render(Graphics2D g, Phaser worldStepEndBarrier);
}
