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

import ea.Scene;
import ea.internal.annotations.Internal;
import ea.internal.graphics.RenderPanel;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.concurrent.Phaser;
import java.util.function.Supplier;

@Internal
public final class RenderThread extends Thread {
    private final Phaser frameStartBarrier;
    private final Phaser frameEndBarrier;
    private final RenderPanel renderPanel;
    private final Supplier<Scene> currentScene;
    private final Supplier<DebugInfo> debugInfo;

    @Internal
    public RenderThread(Phaser frameStartBarrier, Phaser frameEndBarrier, RenderPanel renderPanel, Supplier<Scene> currentScene, Supplier<DebugInfo> debugInfo) {
        super("ea.rendering");

        this.frameStartBarrier = frameStartBarrier;
        this.frameEndBarrier = frameEndBarrier;
        this.renderPanel = renderPanel;
        this.currentScene = currentScene;
        this.debugInfo = debugInfo;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                frameStartBarrier.awaitAdvanceInterruptibly(frameStartBarrier.arrive());

                try {
                    do {
                        BufferStrategy bufferStrategy = renderPanel.getBufferStrategy();

                        do {
                            Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

                            // have to be the same @ Game.screenshot!
                            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

                            Scene scene = currentScene.get();
                            DebugInfo debugInfo = this.debugInfo.get();

                            renderPanel.render(g, scene);

                            if (debugInfo != null) {
                                renderPanel.renderDebug(g, scene);
                                renderPanel.renderInfo(g, debugInfo);
                            }

                            g.dispose();
                        } while (bufferStrategy.contentsRestored() && !isInterrupted());

                        if (!bufferStrategy.contentsLost()) {
                            bufferStrategy.show();

                            Toolkit.getDefaultToolkit().sync();
                        }
                    } while (renderPanel.getBufferStrategy().contentsLost() && !isInterrupted());
                } catch (IllegalStateException e) {
                    throw new RuntimeException(e);
                }

                frameEndBarrier.awaitAdvanceInterruptibly(frameEndBarrier.arrive());
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}