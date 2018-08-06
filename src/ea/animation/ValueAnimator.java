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

import ea.Attachable;
import ea.FrameUpdateListener;
import ea.Scene;
import ea.internal.ano.API;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class ValueAnimator<V> implements FrameUpdateListener, Attachable {
    private Scene scene;
    private Consumer<V> consumer;
    private Interpolator<V> interpolator;
    private Mode mode;
    private int currentTime = 0;
    private int duration;
    private boolean complete = false;

    /**
     * Hilfsvariable für PINGPONG-Mode.
     * @author Michael Andonie
     */
    private boolean goingBackwards = false;

    private Collection<Consumer<V>> completionListeners = new ConcurrentLinkedQueue<>();

    public ValueAnimator(int duration, Consumer<V> consumer, Interpolator<V> interpolator, Mode mode) {
        this.duration = duration;
        this.consumer = consumer;
        this.interpolator = interpolator;
        this.mode = mode;
    }

    public ValueAnimator(int duration, Consumer<V> consumer, Interpolator<V> interpolator) {
        this(duration, consumer, interpolator, Mode.SINGLE);
    }

    /**
     * Setzt den aktuellen Fortschritt des Animators händisch.
     * @param progress  Der Fortschritt, zu dem der Animator gesetzt werden soll. <code>0</code> ist <b>Anfang der
     *                  Animation</b>, <code>1</code> ist <b>Ende der Animation</b>. Werte kleiner 0 bzw. größer als 1
     *                  sind nicht erlaubt.
     */
    @API
    public void setProgress(float progress) {
        if(progress < 0 || progress > 1) {
            throw new IllegalArgumentException("Der eingegebene Progess muss zwischen 0 und 1 liegen. War " + progress);
        }
        this.currentTime = (int) (duration*progress);
        goingBackwards = false;
        this.interpolator.interpolate(progress);
    }

    @Override
    public void onFrameUpdate(int frameDuration) {
        float progress;
        if(!goingBackwards) {
            this.currentTime += frameDuration;
            if (this.currentTime > this.duration) {

                switch(this.mode) {
                    case REPEATED:
                        this.currentTime %= this.duration;
                        progress = (float) this.currentTime / this.duration;
                        break;
                    case SINGLE:
                        this.currentTime = this.duration;
                        this.scene.removeFrameUpdateListener(this);

                        for (Consumer<V> listener : completionListeners) {
                            listener.accept(this.interpolator.interpolate(1));
                        }
                        progress = 1;
                        complete = true;
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
                progress = (float) this.currentTime / this.duration;
            }
        } else {
            //Ping-Pong-Backwards Strategy
            this.currentTime -= frameDuration;
            if(this.currentTime < 0) {
                //PINGPONG backwards ist fertig -> Jetzt wieder vorwärts
                goingBackwards = false;
                progress = 0;
            } else {
                progress = (float) this.currentTime / this.duration;
            }
        }


        this.consumer.accept(interpolator.interpolate(progress));
    }

    @Override
    public void onAttach(Scene scene) {
        this.scene = scene;
    }

    public ValueAnimator<V> addCompletionListener(Consumer<V> listener) {
        if (this.complete) {
            listener.accept(this.interpolator.interpolate(1));
        } else {
            this.completionListeners.add(listener);
        }

        return this;
    }

    public enum Mode {
        SINGLE, REPEATED, PINGPONG
    }
}
