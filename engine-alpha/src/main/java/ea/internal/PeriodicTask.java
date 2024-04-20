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
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

/**
 * Ein periodischer Task, der regelmäßig ausgeführt wird.
 *
 * @author Niklas Keller
 */
public final class PeriodicTask implements FrameUpdateListener {
    /**
     * Intervall in Sekunden.
     */
    private float interval;

    /**
     * Aktuelle Zeit bis zur nächsten Ausführung.
     */
    private float countdown;

    /**
     * Code, der alle X Sekunden ausgeführt wird.
     */
    private Runnable runnable;

    /**
     * Konstruktor.
     *
     * @param intervalInSeconds Zeit zwischen den Ausführungen in Sekunden.
     */
    public PeriodicTask(float intervalInSeconds, Runnable runnable) {
        setInterval(intervalInSeconds);

        this.countdown = intervalInSeconds;
        this.runnable = runnable;
    }

    /**
     * Setzt das Intervall dieses periodischen Tasks neu.
     *
     * @param interval Das neue Intervall. Zeit zwischen den Ausführungen in Sekunden.
     *                 Muss größer als 0 sein.
     */
    @API
    public void setInterval(float interval) {
        if (interval <= 0) {
            throw new RuntimeException("Das Interval eines periodischen Tasks muss größer als 0 sein, war " + interval);
        }

        this.interval = interval;
    }

    /**
     * Gibt das aktuelle Intervall des periodischen Tasks aus.
     *
     * @return Das aktuelle Intervall. Zeit zwischen den Ausführungen in Sekunden.
     */
    @API
    public float getInterval() {
        return interval;
    }

    /**
     * @param deltaSeconds Die Zeit in Millisekunden, die seit dem letzten Update vergangen
     */
    @Override
    @Internal
    public void onFrameUpdate(float deltaSeconds) {
        countdown -= deltaSeconds;

        while (this.countdown < 0) {
            countdown += interval;
            runnable.run();
        }
    }
}
