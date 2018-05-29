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

    @Override
    public void onFrameUpdate(int frameDuration) {
        this.currentTime += frameDuration;

        if (this.currentTime > this.duration) {

            if (this.mode == Mode.REPEATED) {
                this.currentTime %= this.duration;
            } else {
                this.currentTime = this.duration;
                this.scene.removeFrameUpdateListener(this);

                for (Consumer<V> listener : completionListeners) {
                    listener.accept(this.interpolator.interpolate(1));
                }
            }
        }

        this.consumer.accept(interpolator.interpolate((float) this.currentTime / this.duration));
    }

    @Override
    public void onAttach(Scene scene) {
        this.scene = scene;
    }

    public ValueAnimator<V> onComplete(Consumer<V> listener) {
        if (this.complete) {
            listener.accept(this.interpolator.interpolate(1));
        } else {
            this.completionListeners.add(listener);
        }

        return this;
    }

    public enum Mode {
        SINGLE, REPEATED
    }
}
