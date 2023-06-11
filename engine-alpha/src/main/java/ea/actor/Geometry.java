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

import ea.animation.AnimationMode;
import ea.animation.ValueAnimator;
import ea.animation.interpolation.LinearFloat;
import ea.internal.annotations.API;
import ea.internal.physics.FixtureData;

import java.awt.*;
import java.util.function.Supplier;

/**
 * Ein Objekt, das aus n primitiven geometrischen Formen - <b>Dreiecken</b> - besteht.
 *
 * @author Michael Andonie
 */
@API
public abstract class Geometry extends Actor {
    /**
     * Die Farbe dieses Geometry-Objekts.
     */
    private Color color = Color.WHITE;

    /**
     * Konstruktor.
     */
    @API
    public Geometry(Supplier<FixtureData> fixtureSupplier) {
        super(fixtureSupplier);
    }

    /**
     * Setzt ganzheitlich die Farbe aller Formen auf eine bestimmte Farbe.<br>Dadurch färbt
     * sich im Endeffekt das ganze Objekt neu ein.
     *
     * @param color Die neue Farbe.
     */
    @API
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Gibt die Farbe aus.
     *
     * @return Die Farbe des Objekts.
     */
    @API
    public Color getColor() {
        return color;
    }

    /**
     * Animiert die Farbe des aktuellen Objekts.
     *
     * @param duration Dauer der Animation in Sekunden
     * @param color    Neue Farbe des Objekts
     * @return Animations-Objekt, das die weitere Steuerung der Animation erlaubt
     */
    @API
    public ValueAnimator<Float> animateColor(float duration, Color color) {
        Color originalColor = getColor();

        ValueAnimator<Float> animator = new ValueAnimator<>(duration, progress -> setColor(calculateIntermediateColor(originalColor, color, progress)), new LinearFloat(0, 1), AnimationMode.SINGLE, this);
        addFrameUpdateListener(animator);

        return animator;
    }

    private Color calculateIntermediateColor(Color original, Color target, float progress) {
        int r = original.getRed() - (int) ((original.getRed() - target.getRed()) * progress);
        int g = original.getGreen() - (int) ((original.getGreen() - target.getGreen()) * progress);
        int b = original.getBlue() - (int) ((original.getBlue() - target.getBlue()) * progress);

        return new Color(r, g, b);
    }
}
