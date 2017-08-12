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

import ea.internal.ano.API;
import ea.internal.frame.Dispatchable;
import ea.internal.frame.FrameSubthread;
import ea.internal.gra.RenderPanel;
import ea.internal.io.ImageLoader;
import ea.internal.util.Logger;
import ea.keyboard.Key;
import ea.keyboard.KeyAction;
import ea.mouse.MouseAction;
import ea.mouse.MouseButton;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;

/**
 * Diese Klasse gibt Zugriff auf das aktuelle Spiel.
 *
 * @author Michael Andonie
 * @author Niklas Keller
 */
public class Game {
    static {
        System.setProperty("sun.java2d.opengl", "true"); // ok
        System.setProperty("sun.java2d.d3d", "false"); // ok
        System.setProperty("sun.java2d.noddraw", "true"); // set false if possible, linux
        System.setProperty("sun.java2d.pmoffscreen", "false"); // set true if possible, linux
        System.setProperty("sun.java2d.ddoffscreen", "true"); // ok, windows
        System.setProperty("sun.java2d.ddscale", "true"); // ok, hardware accelerated image scaling on windows
    }

    /**
     * Breite der Zeichenebene.
     */
    private static int width;

    /**
     * Höhe der Zeichenebene.
     */
    private static int height;

    /**
     * Eigentliches Fenster des Spiels.
     */
    private static final Frame frame = new Frame("Engine Alpha");

    /**
     * Gibt an, ob bei Escape-Druck das Spiel beendet werden soll.
     */
    private static boolean exitOnEsc = true;

    /**
     * Aktuelle Szene des Spiels.
     */
    private static Scene scene;

    /**
     * Falls gesetzt, wird im nächsten Frame zu dieser Szene gewechselt.
     */
    private static Scene nextScene;

    private static CyclicBarrier frameBarrier;

    private static FrameSubthread renderThread;

    private static Thread mainThread;

    /**
     * Queue aller Dispatchables, die im nächsten Frame ausgeführt werden.
     */
    private static volatile Queue<Dispatchable> dispatchableQueue = new ConcurrentLinkedQueue<>();

    /**
     * Speichert den Zustand von Tasten der Tastatur. Ist ein Wert <code>true</code>, so ist die
     * entsprechende Taste gedrückt, sonst ist der Wert <code>false</code>.
     */
    private static volatile boolean[] keys = new boolean[45];

    /**
     * Letzte Mausposition.
     */
    private static Point mousePosition;

    private static int frameDuration;

    /**
     * Setzt den Titel des Spielfensters.
     *
     * @param title Titel des Spielfensters.
     */
    @API
    public static void setTitle(String title) {
        frame.setTitle(title);
    }

    /**
     * Setzt, ob beim Drücken von Escape das Spiel beendet werden soll.
     *
     * @param value <code>true</code>, falls ja, sonst <code>false</code>.
     */
    @API
    public static void setExitOnEsc(boolean value) {
        exitOnEsc = value;
    }

    /**
     * Dies startet das Fenster und beginnt sämtliche internen Prozesse der Engine.
     *
     * @param width  Die Breite des Zeichenbereichs.
     * @param height Die Höhe des Zeichenbereichs.
     * @param scene  Szene, mit der das Spiel gestartet wird, z.B. das Menü.
     */
    @API
    public static void start(int width, int height, Scene scene) {
        Game.width = width;
        Game.height = height;
        Game.scene = scene;

        RenderPanel renderPanel = new RenderPanel(width, height) {
            public void render(Graphics2D g) {
                // Absoluter Hintergrund
                g.setColor(Color.black);
                g.fillRect(0, 0, this.getWidth(), this.getHeight());

                AffineTransform transform = g.getTransform();
                Camera camera = Game.scene.getCamera();
                Punkt position = camera.getPosition();
                float rotation = -camera.getRotation();

                g.setClip(0, 0, width, height);
                g.translate(width / 2, height / 2);

                g.scale(camera.getZoom(), camera.getZoom());
                g.rotate(rotation, 0, 0);
                g.translate(-position.x, -position.y);

                // TODO: Calculate optimal bounds
                int size = Math.max(width, height);
                Game.scene.render(g, new BoundingRechteck(position.x - size, position.y - size, size * 2, size * 2));

                g.setTransform(transform);

                if (EngineAlpha.isDebug()) {
                    renderDebug(g);
                }
            }
        };

        frame.setResizable(false);
        frame.add(renderPanel);
        frame.pack();

        // Center frame on screen - https://stackoverflow.com/a/144893/2373138
        frame.setLocationRelativeTo(null);

        renderPanel.initialize();

        // pack() already allows to create the buffer strategy for rendering
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Game.exit();
            }
        });

        KeyListener keyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                enqueueKeyEvent(e, KeyAction.DOWN);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                enqueueKeyEvent(e, KeyAction.UP);
            }
        };

        frame.addKeyListener(keyListener);
        renderPanel.addKeyListener(keyListener);
        renderPanel.setFocusable(true);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                enqueueMouseEvent(e, MouseAction.DOWN);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                enqueueMouseEvent(e, MouseAction.UP);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                mousePosition = new Point(e.getX() - width / 2, e.getY() - height / 2);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mousePosition = new Point(e.getX() - width / 2, e.getY() - height / 2);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mousePosition = new Point(e.getX() - width / 2, e.getY() - height / 2);
            }
        };

        renderPanel.addMouseMotionListener(mouseAdapter);
        renderPanel.addMouseListener(mouseAdapter);

        try {
            frame.setIconImage(ImageLoader.load("assets/favicon.png"));
        } catch (Exception e) {
            Logger.warning("IO", "Standard-Icon konnte nicht geladen werden.");
        }

        frameBarrier = new CyclicBarrier(2);

        renderThread = new FrameSubthread("Rendering", frameBarrier) {
            @Override
            public void dispatchFrame() {
                try {
                    do {
                        do {
                            Graphics2D g = (Graphics2D) renderPanel.getBufferStrategy().getDrawGraphics();

                            // have to be the same @ Game.screenshot!
                            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

                            renderPanel.render(g);

                            g.dispose();
                        } while (renderPanel.getBufferStrategy().contentsRestored());

                        renderPanel.getBufferStrategy().show();
                    } while (renderPanel.getBufferStrategy().contentsLost());
                } catch (IllegalStateException e) {
                    Logger.error("Rendering", e.getMessage());
                    Game.exit();
                }
            }
        };

        renderThread.setPriority(Thread.MAX_PRIORITY);

        mousePosition = new Point(width / 2, height / 2);

        mainThread = new Thread(Game::run);
        mainThread.start();
        mainThread.setPriority(Thread.MAX_PRIORITY);
    }

    private static void renderDebug(Graphics2D g) {
        AffineTransform transform = g.getTransform();
        Camera camera = Game.scene.getCamera();
        Punkt position = camera.getPosition();
        float rotation = -camera.getRotation();

        g.setClip(0, 0, width, height);
        g.translate(width / 2, height / 2);

        g.scale(camera.getZoom(), camera.getZoom());
        g.rotate(rotation, 0, 0);
        g.translate(-position.x, -position.y);

        int gridSize = 100;
        int windowSize = Math.max(width, height);

        // TODO: Optimize to draw only the required grid cells on rotation
        // Without rotation: - width / 2, - height / 2
        int tx = (int) position.x - windowSize;
        int ty = (int) position.y - windowSize;

        tx -= tx % gridSize;
        ty -= ty % gridSize;

        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        g.setColor(new Color(255, 255, 255, 100));

        for (int x = tx; x < tx + 2 * windowSize + gridSize; x += gridSize) {
            g.drawLine(x, ty - gridSize, x, ty + windowSize * 2 + gridSize);
        }

        for (int y = ty; y < ty + 2 * windowSize + gridSize; y += gridSize) {
            g.drawLine(tx - gridSize, y, tx + windowSize * 2 + gridSize, y);
        }

        for (int x = tx; x < tx + 2 * windowSize + gridSize; x += gridSize) {
            for (int y = ty; y < ty + 2 * windowSize + gridSize; y += gridSize) {
                g.drawString(x + " / " + y, x + 10, y + 20);
            }
        }

        g.setTransform(transform);

        renderInfo(g);
    }

    private static void renderInfo(Graphics2D g) {
        Font displayFont = new Font("Monospaced", Font.PLAIN, 12);
        FontMetrics fm = g.getFontMetrics(displayFont);
        Rectangle2D bounds;
        int y = 10;

        // Prevent java.lang.ArithmeticException: / by zero
        String fpsMessage = "FPS: " + (1000 / Math.max(frameDuration, 1));
        bounds = fm.getStringBounds(fpsMessage, g);

        g.setColor(new Color(0, 106, 214));
        g.fillRect(10, y, (int) bounds.getWidth() + 20, (int) bounds.getHeight() + 16);
        g.setColor(new Color(255, 255, 255, 50));
        g.drawRect(10, y, (int) bounds.getWidth() + 19, (int) bounds.getHeight() + 15);

        g.setColor(Color.white);
        g.setFont(displayFont);
        g.drawString(fpsMessage, 20, y + 8 + fm.getHeight() - fm.getDescent());

        y += fm.getHeight() + 20;

        String bodyMessage = "Bodies: " + scene.getWorldHandler().getWorld().getBodyCount();
        bounds = fm.getStringBounds(bodyMessage, g);

        g.setColor(new Color(0, 214, 84));
        g.fillRect(10, y, (int) bounds.getWidth() + 20, (int) bounds.getHeight() + 16);
        g.setColor(new Color(255, 255, 255, 50));
        g.drawRect(10, y, (int) bounds.getWidth() + 19, (int) bounds.getHeight() + 15);

        g.setColor(Color.white);
        g.setFont(displayFont);
        g.drawString(bodyMessage, 20, y + 8 + fm.getHeight() - fm.getDescent());
    }

    private static void run() {
        renderThread.start();

        frameDuration = 16; // TODO: Readd FPS setting (maxmillis)
        long frameStart = System.nanoTime();

        while (!Thread.interrupted()) {
            if (nextScene != null) {
                scene = nextScene;
                nextScene = null;
            }

            try {
                frameBarrier.await();
            } catch (BrokenBarrierException | InterruptedException e) {
                break;
            }

            scene.getWorldHandler().step(frameDuration);
            scene.onFrameUpdate(frameDuration);

            while (!dispatchableQueue.isEmpty()) {
                dispatchableQueue.poll().dispatch();
            }

            long frameEnd = System.nanoTime();
            int duration = (int) (frameEnd - frameStart) / 1000000;

            if (duration < 16) {
                try {
                    Thread.sleep(16 - duration);
                } catch (InterruptedException e) {
                    break;
                }
            }

            frameEnd = System.nanoTime();
            frameDuration = (int) ((frameEnd - frameStart) / 1000000);

            frameStart = frameEnd;
        }

        // Thread soll aufhören: Sauber machen!
        renderThread.interrupt();

        try {
            renderThread.join();
        } catch (InterruptedException e) {
            // Ignore here
        }

        frame.setVisible(false);
        frame.dispose();

        System.exit(0);
    }

    /**
     * Diese Methode wird immer dann ausgeführt, wenn eine Taste gedrückt oder losgelassen wurde.
     *
     * @param e      Das KeyEvent.
     * @param action Drücken oder Loslassen?
     */
    private static void enqueueKeyEvent(KeyEvent e, KeyAction action) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE && exitOnEsc) {
            Game.exit();
        }

        int z = Key.vonJava(e.getKeyCode());

        if (z == -1) {
            return;
        }

        if (action == KeyAction.DOWN) {
            if (keys[z]) {
                return; // Ignore duplicate presses, because they're system dependent
            }

            keys[z] = true;
        } else {
            keys[z] = false;
        }

        enqueueDispatchable(() -> {
            if (action == KeyAction.DOWN) {
                scene.onKeyDown(z);
            } else {
                scene.onKeyUp(z);
            }
        });
    }

    /**
     * Diese Methode wird immer dann ausgeführt, wenn ein einfacher Klick der Maus ausgeführt wird.
     *
     * @param e      Das MouseEvent.
     * @param action Drücken oder Loslassen?
     */
    private static void enqueueMouseEvent(MouseEvent e, MouseAction action) {
        // Finde Klick auf Zeichenebene, die Position relativ zum Ursprung des RenderPanel-Canvas.
        Point sourceClick = e.getPoint();

        // Mausklick-Position muss mit Zoom-Wert verrechnet werden
        float zoom = scene.getCamera().getZoom();
        float rotation = scene.getCamera().getRotation();
        Punkt position = scene.getCamera().getPosition();

        Punkt sourcePosition = new Punkt(
                position.x + (((float) Math.cos(rotation) * (sourceClick.x - width / 2) - (float) Math.sin(rotation) * (sourceClick.y - height / 2))) / zoom,
                position.y + (((float) Math.sin(rotation) * (sourceClick.x - width / 2) + (float) Math.cos(rotation) * (sourceClick.y - height / 2))) / zoom
        );

        MouseButton button;

        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                button = MouseButton.LEFT;
                break;

            case MouseEvent.BUTTON3:
                button = MouseButton.RIGHT;
                break;

            default:
                // Ignore event
                return;
        }

        enqueueDispatchable(() -> {
            if (action == MouseAction.DOWN) {
                scene.onMouseDown(sourcePosition, button);
            } else {
                scene.onMouseUp(sourcePosition, button);
            }
        });
    }

    @API
    public static void enqueueDispatchable(Dispatchable dispatchable) {
        dispatchableQueue.add(dispatchable);
    }

    @API
    public static void transitionToScene(Scene scene) {
        enqueueDispatchable(() -> nextScene = scene);
    }

    /**
     * Diese Methode beendet das Spiel.<br /> Das heißt, dass das Fenster geschlossen, alle belegten
     * Ressourcen freigegeben und auch die virtuelle Maschine von Java beendet wird.
     */
    @API
    public static void exit() {
        if (mainThread == null) {
            System.exit(0);

            return;
        }

        mainThread.interrupt();
    }

    public static Point getMousePosition() {
        return mousePosition;
    }
}
