/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2020 Michael Andonie and contributors.
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

package ea.edu.turtle;

import ea.Game;
import ea.Scene;
import ea.Vector;
import ea.actor.BodyType;
import ea.actor.Rectangle;
import ea.animation.ValueAnimator;
import ea.animation.interpolation.LinearFloat;
import ea.edu.Spiel;

import java.awt.Color;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Turtle {
    public static void main(String[] args) {
        new Turtle();
    }

    private final Scene scene;
    private final Rectangle turtle;

    private boolean drawLine;
    private Color lineColor = Color.BLACK;
    private float speed = 100;

    public Turtle() {
        scene = new Scene();
        scene.setBackgroundColor(new Color(240, 240, 240));

        turtle = new Rectangle(1, 1);
        turtle.setCenter(0, 0);
        turtle.setColor(Color.RED);

        scene.add(turtle);

        if (!Game.isRunning()) {
            Game.start(800, 600, scene);
        } else {
            Game.transitionToScene(scene);
        }

        scene.getCamera().setFocus(turtle);

        schnee(10, 5);
    }

    private void schnee(double length, int d) {
        absetzen();
        rotiere(180);
        laufe(5);
        rotiere(-90);
        laufe(3);
        rotiere(-90);
        ansetzen();

        for (int i = 0; i < 3; i++) {
            curve(length, d);
            rotiere(-120);
        }

        absetzen();
        rotiere(180);
        laufe(-5);
        rotiere(-90);
        laufe(-3);
        rotiere(-90);
        ansetzen();
    }

    private void curve(double length, int d) {
        if (d == 0) {
            laufe(length);
        } else {
            curve(length / 3, d - 1);
            rotiere(60);
            curve(length / 3, d - 1);
            rotiere(-120);
            curve(length / 3, d - 1);
            rotiere(60);
            curve(length / 3, d - 1);
        }
    }

    protected final void warte(double sekunden) {
        try {
            Thread.sleep((long) (1000 * sekunden));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected final void setzeFarbe(String farbe) {
        lineColor = Spiel.konvertiereVonFarbname(farbe);
    }

    protected final void ansetzen() {
        drawLine = true;
    }

    protected final void absetzen() {
        drawLine = false;
    }

    protected final void laufe(double meter) {
        Vector move = Vector.ofAngle(turtle.getRotation()).multiply((float) meter);
        Vector initial = turtle.getCenter();

        float duration = (float) meter / speed;

        AtomicReference<Rectangle> line = new AtomicReference<>();

        animate(duration, progress -> {
            turtle.setCenter(initial.add(move.multiply(progress)));

            if (drawLine) {
                if (line.get() != null) {
                    line.get().remove();
                }

                line.set(new Rectangle((float) meter * progress, 0.1f));
                line.get().setRotation(turtle.getRotation());
                line.get().setCenter(turtle.getCenter().subtract(move.multiply(progress * 0.5f)));
                line.get().setColor(lineColor);
                line.get().setBorderRadius(1);
                line.get().setBodyType(BodyType.PARTICLE);

                scene.add(line.get());
            }
        });
    }

    protected final void rotiere(double grad) {
        Vector center = turtle.getCenter();

        float start = turtle.getRotation();
        float duration = (float) grad / 360 / speed;

        animate(duration, progress -> {
            turtle.setRotation(start + progress * (float) grad);
            turtle.setCenter(center);
        });
    }

    private void animate(float duration, Consumer<Float> setter) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        ValueAnimator<Float> animator = new ValueAnimator<>(duration, setter, new LinearFloat(0, 1), turtle);

        animator.addCompletionListener(value -> {
            setter.accept(value);
            future.complete(null);
        });

        turtle.addFrameUpdateListener(animator);

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
