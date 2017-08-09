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

package ea;

import ea.internal.frame.Dispatchable;

/**
 * Ein periodischer Task, der regelmäßig ausgeführt wird.
 *
 * @author Niklas Keller
 */
public abstract class PeriodicTask implements Dispatchable, FrameUpdateListener {
    /**
     * Intervall in Millisekunden.
     */
    private final int interval;

    /**
     * Aktuelle Zeit bis zur nächsten Ausführung.
     */
    private int countdown;

    /**
     * Konstruktor.
     *
     * @param interval Zeit zwischen den Ausführungen in Millisekunden.
     */
    public PeriodicTask(int interval) {
        this.interval = interval;
        this.countdown = interval;
    }

    /**
     * @param frameDuration Die Zeit in Millisekunden, die seit dem letzten Update vergangen
     */
    @Override
    public void onFrameUpdate(int frameDuration) {
        this.countdown -= frameDuration;

        while (this.countdown < 0) {
            this.countdown += this.interval;
            Game.enqueueDispatchable(this);
        }
    }
}
