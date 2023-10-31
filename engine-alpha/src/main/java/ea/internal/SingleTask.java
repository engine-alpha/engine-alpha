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

package ea.internal;

import ea.FrameUpdateListener;
import ea.event.FrameUpdateListenerContainer;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

/**
 * Ein einfacher Task, der einmalig mit Verzögerung ausgeführt wird.
 *
 * @author Niklas Keller
 * @see FrameUpdateListenerContainer#delay(float, Runnable)
 */
@Internal
public final class SingleTask implements FrameUpdateListener {
    /**
     * Verzögerung in Sekunden.
     */
    private final float delay;

    /**
     * Aktuelle Zeit bis zur nächsten Ausführung.
     */
    private float countdown;

    /**
     * Code, der nach X Sekunden ausgeführt wird.
     */
    private final Runnable runnable;

    /**
     * Sagt, ob der Task bereits vollständig ausgeführt wurde.
     */
    private boolean done;

    /**
     * Container, an dem der Task angemeldet wird, wo er sich auch selbst wieder abmeldet.
     */
    private FrameUpdateListenerContainer parent;

    /**
     * Konstruktor.
     *
     * @param delayInSeconds Zeit zwischen den Ausführungen in Millisekunden.
     */
    public SingleTask(float delayInSeconds, Runnable runnable, FrameUpdateListenerContainer parent) {
        this.delay = delayInSeconds;
        this.countdown = delayInSeconds;
        this.runnable = runnable;
        this.parent = parent;
    }

    /**
     * Gibt das aktuelle Intervall des periodischen Tasks aus.
     *
     * @return Das aktuelle Intervall. Zeit zwischen den Ausführungen in Sekunden.
     */
    @API
    public float getDelay() {
        return delay;
    }

    /**
     * @return Sagt, ob der Task bereits vollständig ausgeführt wurde.
     */
    @API
    public boolean isDone() {
        return done;
    }

    /**
     * @param deltaSeconds Die Zeit in Millisekunden, die seit dem letzten Update vergangen
     */
    @Override
    public void onFrameUpdate(float deltaSeconds) {
        countdown -= deltaSeconds;

        if (!done && this.countdown < 0) {
            runnable.run();
            parent.removeFrameUpdateListener(this);
            done = true;
        }
    }
}
