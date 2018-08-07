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

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadSyncHelper {
    static final BackgroundThread BACKGROUND = new BackgroundThread();

    static {
        BACKGROUND.start();
    }

    private static ThreadLocal<AtomicBoolean> isEnqueued = ThreadLocal.withInitial(() -> new AtomicBoolean(false));
    private static ThreadLocal<Boolean> isSynced = ThreadLocal.withInitial(() -> false);
    private static ThreadLocal<Phaser> enqueueStart = ThreadLocal.withInitial(() -> new Phaser(2));
    private static ThreadLocal<Phaser> enqueueEnd = ThreadLocal.withInitial(() -> new Phaser(2));

    public static Phaser getStart() {
        return enqueueStart.get();
    }

    public static Phaser getEnd() {
        return enqueueEnd.get();
    }

    public static void setSynced(boolean value) {
        isSynced.set(value);
    }

    public static boolean isSynced() {
        return isSynced.get();
    }

    public static AtomicBoolean getEnqueueState() {
        return isEnqueued.get();
    }

    public static void delayAwait(AtomicBoolean enqueueState, Phaser end) {
        BACKGROUND.enqueue(() -> {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                return;
            }

            synchronized (BACKGROUND) {
                if (isSynced()) {
                    return;
                }

                if (enqueueState.compareAndSet(true, false)) {
                    end.arriveAndAwaitAdvance();
                }
            }
        });
    }

    private static class BackgroundThread extends Thread {
        private Collection<Runnable> dispatchables = new ArrayList<>();

        public BackgroundThread() {
            super("ThreadSync Background");
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Runnable[] runnables;

                    synchronized (this) {
                        while (dispatchables.isEmpty()) {
                            wait();
                        }

                        runnables = dispatchables.toArray(new Runnable[0]);
                        dispatchables.clear();
                    }

                    for (Runnable runnable : runnables) {
                        runnable.run();
                    }
                }
            } catch (InterruptedException e) {
                // ignore
            }
        }

        public synchronized void enqueue(Runnable runnable) {
            dispatchables.add(runnable);
            notifyAll();
        }
    }
}
