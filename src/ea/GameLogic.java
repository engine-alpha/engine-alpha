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

package ea;

import ea.internal.DebugInfo;
import ea.internal.RenderThread;
import ea.internal.annotations.Internal;
import ea.internal.graphics.RenderPanel;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.function.Supplier;

public final class GameLogic {
    private static final float DESIRED_FRAME_DURATION = 0.016f;

    private static final int NANOSECONDS_PER_SECOND = 1000000000;

    private final ExecutorService threadPoolExecutor = Executors.newCachedThreadPool();

    private final RenderThread renderThread;

    private final Phaser frameStartBarrier = new Phaser(2);
    private final Phaser frameEndBarrier = new Phaser(2);

    private final Supplier<Scene> currentScene;

    /**
     * Queue aller Dispatchables, die im nächsten Frame ausgeführt werden.
     */
    private Queue<Runnable> dispatchableQueue = new ConcurrentLinkedQueue<>();

    private float frameDuration;

    public GameLogic(RenderPanel renderPanel, Supplier<Scene> currentScene, Supplier<Boolean> isDebug) {
        this.renderThread = new RenderThread(frameStartBarrier, frameEndBarrier, renderPanel, currentScene, () -> {
            if (isDebug.get()) {
                return new DebugInfo(frameDuration, currentScene.get().getWorldHandler().getWorld().getBodyCount());
            }

            return null;
        });

        this.currentScene = currentScene;
    }

    public void enqueue(Runnable runnable) {
        dispatchableQueue.add(runnable);
    }

    public void run() {
        this.renderThread.start();

        this.frameDuration = DESIRED_FRAME_DURATION;

        long frameStart = System.nanoTime();
        long frameEnd;

        while (!Thread.interrupted()) {
            Scene scene = this.currentScene.get();

            try {
                float deltaSeconds = Math.min(2 * DESIRED_FRAME_DURATION, frameDuration);

                scene.step(deltaSeconds, threadPoolExecutor::submit);
                scene.getCamera().onFrameUpdate();

                frameStartBarrier.arriveAndAwaitAdvance();

                scene.invokeFrameUpdateListeners(deltaSeconds);

                Runnable runnable = dispatchableQueue.poll();
                while (runnable != null) {
                    runnable.run();
                    runnable = dispatchableQueue.poll();
                }

                frameEndBarrier.arriveAndAwaitAdvance();

                frameEnd = System.nanoTime();
                float duration = (float) (frameEnd - frameStart) / NANOSECONDS_PER_SECOND;

                if (duration < DESIRED_FRAME_DURATION) {
                    try {
                        Thread.sleep((int) (1000 * (DESIRED_FRAME_DURATION - duration)));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }

                frameEnd = System.nanoTime();
                frameDuration = ((float) (frameEnd - frameStart) / NANOSECONDS_PER_SECOND);

                frameStart = frameEnd;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        while (renderThread.isAlive()) {
            // Thread soll aufhören: Sauber machen!
            renderThread.interrupt();

            try {
                renderThread.join();
            } catch (InterruptedException e) {
                // Try again
            }
        }

        threadPoolExecutor.shutdown();

        try {
            threadPoolExecutor.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // noinspection UnnecessaryReturnStatement
            return; // if interrupted again, don't wait
        }
    }

    @Internal
    RenderThread getRenderThread() {
        return this.renderThread;
    }
}
