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

package ea.internal.util;

import ea.Vector;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

public final class FontMetrics {
    private static final ThreadLocal<Canvas> canvas = ThreadLocal.withInitial(Canvas::new);

    public static int getDescent(Font font) {
        return canvas.get().getFontMetrics(font).getDescent();
    }

    public static Vector getSize(String content, Font font) {
        Canvas canvas = FontMetrics.canvas.get();
        Rectangle2D bounds = canvas.getFontMetrics(font).getStringBounds(content, canvas.getGraphics());

        return new Vector(bounds.getWidth(), bounds.getHeight());
    }
}
