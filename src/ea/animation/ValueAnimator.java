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

package ea.animation;

import ea.FrameUpdateListener;
import ea.event.EventListeners;
import ea.internal.annotations.API;

import java.util.function.Consumer;

public class ValueAnimator<Value> implements FrameUpdateListener {
    private Consumer<Value> consumer;
    private Interpolator<Value> interpolator;
    private AnimationMode mode;
    private float currentTime = 0;
    private float duration;
    private boolean complete = false;

    /**
     * Hilfsvariable für PINGPONG-Mode.
     */
    private boolean goingBackwards = false;

    private EventListeners<Consumer<Value>> completionListeners = new EventListeners<>();

    public ValueAnimator(float duration, Consumer<Value> consumer, Interpolator<Value> interpolator, AnimationMode mode) {
        this.duration = duration;
        this.consumer = consumer;
        this.interpolator = interpolator;
        this.mode = mode;
    }

    public ValueAnimator(float duration, Consumer<Value> consumer, Interpolator<Value> interpolator) {
        this(duration, consumer, interpolator, AnimationMode.SINGLE);
    }

    /**
     * Setzt den aktuellen Fortschritt des Animators händisch.
     *
     * @param progress Der Fortschritt, zu dem der Animator gesetzt werden soll. <code>0</code> ist <b>Anfang der
     *                 Animation</b>, <code>1</code> ist <b>Ende der Animation</b>. Werte kleiner 0 bzw. größer als 1
     *                 sind nicht erlaubt.
     */
    @API
    public void setProgress(float progress) {
        if (progress < 0 || progress > 1) {
            throw new IllegalArgumentException("Der eingegebene Progess muss zwischen 0 und 1 liegen. War " + progress);
        }

        this.goingBackwards = false;
        this.currentTime = duration * progress;
        this.interpolator.interpolate(progress);
    }

    @Override
    public void onFrameUpdate(float deltaSeconds) {
        float progress;

        if (!goingBackwards) {
            this.currentTime += deltaSeconds;

            if (this.currentTime > this.duration) {
                switch (this.mode) {
                    case REPEATED:
                        this.currentTime %= this.duration;
                        progress = this.currentTime / this.duration;
                        break;
                    case SINGLE:
                        this.currentTime = this.duration;
                        progress = 1;
                        complete = true;

                        Value finalValue = this.interpolator.interpolate(1);
                        completionListeners.invoke(listener -> listener.accept(finalValue));

                        break;
                    case PINGPONG:
                        //Ging bisher vorwärts -> Jetzt Rückwärts
                        goingBackwards = true;
                        progress = 1;
                        break;
                    default:
                        progress = -1;
                        break;
                }
            } else {
                progress = this.currentTime / this.duration;
            }
        } else {
            //Ping-Pong-Backwards Strategy
            this.currentTime -= deltaSeconds;
            if (this.currentTime < 0) {
                //PINGPONG backwards ist fertig -> Jetzt wieder vorwärts
                goingBackwards = false;
                progress = 0;
            } else {
                progress = this.currentTime / this.duration;
            }
        }

        this.consumer.accept(interpolator.interpolate(progress));
    }

    public ValueAnimator<Value> addCompletionListener(Consumer<Value> listener) {
        if (this.complete) {
            listener.accept(this.interpolator.interpolate(1));
        } else {
            this.completionListeners.add(listener);
        }

        return this;
    }
}
