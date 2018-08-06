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

package ea.internal;

import java.util.concurrent.Phaser;

public abstract class FrameSubthread extends Thread {
    private Phaser frameBarrierStart;
    private Phaser frameBarrierEnd;

    protected FrameSubthread(String name, Phaser frameBarrierStart, Phaser frameBarrierEnd) {
        super(name);

        this.frameBarrierStart = frameBarrierStart;
        this.frameBarrierEnd = frameBarrierEnd;
        this.setDaemon(true);
    }

    /**
     * Implementiert die frameweise Synchronisation mit dem Master-Thread. Auf Signal von diesem
     * wird die {@link #dispatchFrame()}-Routine des Threads ausgeführt.
     */
    @Override
    public final void run() {
        while (!interrupted()) {
            frameBarrierStart.arriveAndAwaitAdvance();

            dispatchFrame();

            frameBarrierEnd.arriveAndAwaitAdvance();
        }
    }

    /**
     * In dieser Methode wird die jeweilige (frameweise) Logik des Threads ausgeführt.
     */
    public abstract void dispatchFrame();
}