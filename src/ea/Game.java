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
import ea.internal.ano.NoExternalUse;
import ea.internal.frame.Dispatchable;
import ea.internal.frame.FrameSubthread;
import ea.internal.gra.RenderPanel;
import ea.internal.io.ImageLoader;
import ea.internal.util.Logger;
import ea.keyboard.Key;
import ea.keyboard.KeyAction;
import ea.mouse.MouseAction;
import ea.mouse.MouseButton;
import ea.mouse.MouseWheelAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
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
        System.setProperty("sun.java2d.d3d", "true"); // ok
        System.setProperty("sun.java2d.noddraw", "false"); // set false if possible, linux
        System.setProperty("sun.java2d.pmoffscreen", "true"); // set true if possible, linux
        System.setProperty("sun.java2d.ddoffscreen", "true"); // ok, windows
        System.setProperty("sun.java2d.ddscale", "true"); // ok, hardware accelerated image scaling on windows
    }

    /**
     * Breite des Fensters.
     */
    private static int width;

    /**
     * Höhe des Fensters.
     */
    private static int height;

    /**
     * Eigentliches Fenster des Spiels.
     */
    private static final Frame frame = new Frame("Engine Alpha");

    private static RenderPanel renderPanel;

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

    private static CyclicBarrier frameBarrierStart;

    private static CyclicBarrier frameBarrierEnd;

    private static FrameSubthread renderThread;

    private static Thread mainThread;

    /**
     * Queue aller Dispatchables, die im nächsten Frame ausgeführt werden.
     */
    private static volatile Queue<Dispatchable> dispatchableQueue = new ConcurrentLinkedQueue<>();

    /**
     * Speichert den Zustand von Tasten der Tastatur. Ist ein Wert <code>true</code>, so ist die entsprechende Taste
     * gedrückt, sonst ist der Wert <code>false</code>.
     */
    private static volatile boolean[] keys = new boolean[45];

    /**
     * Letzte Mausposition.
     */
    private static java.awt.Point mousePosition;

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
        if (renderPanel != null) {
            //Start wurde schon ausgeführt.
            throw new RuntimeException("Game.start wurde bereits ausgeführt.");
        }
        Game.width = width;
        Game.height = height;
        Game.scene = scene;

        renderPanel = new RenderPanel(width, height) {
            public void render(Graphics2D g) {
                // Absoluter Hintergrund
                g.setColor(Color.black);
                g.fillRect(0, 0, this.getWidth(), this.getHeight());

                AffineTransform transform = g.getTransform();
                Camera camera = Game.scene.getCamera();
                Point position = camera.getPosition();
                float rotation = -camera.getRotation();

                g.setClip(0, 0, width, height);
                g.translate(width / 2, height / 2);

                g.scale(camera.getZoom(), camera.getZoom());
                g.rotate(rotation, 0, 0);
                g.translate(-position.x, position.y);

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

        renderPanel.allocateBuffers();

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
                mousePosition = e.getPoint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mousePosition = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mousePosition = e.getPoint();
            }
        };

        renderPanel.addMouseMotionListener(mouseAdapter);
        renderPanel.addMouseListener(mouseAdapter);
        renderPanel.addMouseWheelListener(Game::enqueueMouseWheelEvent);

        try {
            frame.setIconImage(ImageLoader.load("assets/favicon.png"));
        } catch (Exception e) {
            Logger.warning("IO", "Standard-Icon konnte nicht geladen werden.");
        }

        frameBarrierStart = new CyclicBarrier(2);
        frameBarrierEnd = new CyclicBarrier(2);

        renderThread = new FrameSubthread("Rendering", frameBarrierStart, frameBarrierEnd) {
            @Override
            public void dispatchFrame() {
                try {
                    do {
                        BufferStrategy bufferStrategy = renderPanel.getBufferStrategy();

                        do {
                            Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

                            // have to be the same @ Game.screenshot!
                            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

                            renderPanel.render(g);

                            g.dispose();
                        } while (bufferStrategy.contentsRestored());

                        if (!bufferStrategy.contentsLost()) {
                            bufferStrategy.show();
                        }

                        Toolkit.getDefaultToolkit().sync();
                    } while (renderPanel.getBufferStrategy().contentsLost());
                } catch (IllegalStateException e) {
                    Logger.error("Rendering", e.getMessage());
                    Game.exit();
                }
            }
        };

        renderThread.setPriority(Thread.MAX_PRIORITY);

        mousePosition = new java.awt.Point(width / 2, height / 2);

        mainThread = new Thread(Game::run, "Main Game");
        mainThread.start();
        mainThread.setPriority(Thread.MAX_PRIORITY);
    }

    /**
     * Rendert Debug-Informationen auf dem Bildschirm.
     *
     * @param g Das Graphics-Objekt zum zeichnen.
     */
    @NoExternalUse
    private static void renderDebug(Graphics2D g) {
        AffineTransform transform = g.getTransform();
        Camera camera = Game.scene.getCamera();
        Point position = camera.getPosition();
        float rotation = -camera.getRotation();

        g.setClip(0, 0, width, height);
        g.translate(width / 2, height / 2);

        g.scale(camera.getZoom(), camera.getZoom());
        g.rotate(rotation, 0, 0);
        g.translate(-position.x, position.y);

        int gridSize = 100;
        int windowSize = Math.max(width, height);

        // TODO: Optimize to draw only the required grid cells on getRotation
        // Without getRotation: - width / 2, - height / 2
        int tx = (int) position.x - windowSize;
        int ty = (int) (-1 * position.y) - windowSize;

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
                g.drawString(x + " / " + (-y), x + 10, y + 20);
            }
        }

        g.setTransform(transform);

        renderInfo(g);
    }

    /**
     * Rendert zusätzliche Debug-Infos auf dem Bildschirm.
     *
     * @param g Das Graphics-Objekt zum zeichnen.
     */
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

            scene.getWorldHandler().step(frameDuration);

            try {
                frameBarrierStart.await();
            } catch (BrokenBarrierException | InterruptedException e) {
                break;
            }

            scene.onFrameUpdateInternal(frameDuration);

            while (!dispatchableQueue.isEmpty()) {
                dispatchableQueue.poll().dispatch();
            }

            try {
                frameBarrierEnd.await();
            } catch (BrokenBarrierException | InterruptedException e) {
                break;
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
                scene.onKeyDownInternal(z);
            } else {
                scene.onKeyUpInternal(z);
            }
        });
    }

    /**
     * Diese Methode wird immer dann ausgeführt, wenn ein simplifiedDirection Klick der Maus ausgeführt wird.
     *
     * @param e      Das MouseEvent.
     * @param action Drücken oder Loslassen?
     */
    private static void enqueueMouseEvent(MouseEvent e, MouseAction action) {
        Point sourcePosition = convertMousePosition(scene, e.getPoint());
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
                scene.onMouseDownInternal(sourcePosition, button);
            } else {
                scene.onMouseUpInternal(sourcePosition, button);
            }
        });
    }

    @NoExternalUse
    public static Point convertMousePosition(Scene scene, java.awt.Point mousePosition) {
        // Finde Klick auf Zeichenebene, die Position relativ zum Ursprung des RenderPanel-Canvas.
        // Mausklick-Position muss mit Zoom-Wert verrechnet werden
        float zoom = scene.getCamera().getZoom();
        float rotation = scene.getCamera().getRotation();
        Point position = scene.getCamera().getPosition();

        return new Point(
                position.x + (((float) Math.cos(rotation) * (mousePosition.x - width / 2f) + (float) Math.sin(rotation) * (mousePosition.y - height / 2f))) / zoom,
                (-1) * position.y + (((float) Math.sin(rotation) * (mousePosition.x - width / 2f) - (float) Math.cos(rotation) * (mousePosition.y - height / 2f))) / zoom
        );
    }

    /**
     * Diese Methode wird immer dann ausgeführt, wenn das Mausrad bewegt wurde und ein MouseWheelEvent registriert
     * wurde.
     *
     * @param mouseWheelEvent das Event.
     */
    private static void enqueueMouseWheelEvent(MouseWheelEvent mouseWheelEvent) {
        MouseWheelAction mouseWheelAction = new MouseWheelAction((float) mouseWheelEvent.getPreciseWheelRotation());
        enqueueDispatchable(() -> {
            scene.onMouseWheelMoveInternal(mouseWheelAction);
        });
    }

    @API
    public static void enqueueDispatchable(Dispatchable dispatchable) {
        dispatchableQueue.add(dispatchable);
    }

    /**
     * TODO : Dokumentation!
     */
    @API
    public static void transitionToScene(Scene scene) {
        enqueueDispatchable(() -> nextScene = scene);
    }

    /**
     * Gibt an, ob eine bestimmte Taste derzeit heruntergedrückt ist.
     *
     * @param key Die zu testende Taste als Key-Code (also z.B. <code>Key.W</code>).
     *
     * @return <code>true</code>, wenn die zu testende Taste gerade heruntergedrückt ist. Sonst <code>false</code>.
     *
     * @see ea.keyboard.Key
     */
    @API
    public static boolean isKeyPressed(int key) {
        return keys[key];
    }

    /**
     * Gibt an, ob gerade die Engine läuft. Die Engine läuft, sobald es ein sichtbares Fenster gibt. Dieses läuft,
     * sobald {@link #start(int, int, Scene)} ausgeführt wurde.
     *
     * @return <code>true</code>, wenn das Spiel läuft, sonst <code>false</code>.
     *
     * @see #start(int, int, Scene)
     */
    @API
    public static boolean isRunning() {
        return frame.isVisible();
    }

    /**
     * Setzt die Größe des Engine-Fensters.
     *
     * @param width  Die neue Breite des Engine-Fensters.
     * @param height Die neue Höhe des Engine-Fensters.
     */
    @API
    public static void setFrameSize(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new RuntimeException("Die Fenstergröße kann nicht kleiner/gleich 0 sein. "
                    + "Eingabe war: " + width + " - " + height + ".");
        }
        if (renderPanel == null) {
            throw new RuntimeException("Fenster-Resizing ist erst möglich, nachdem Game.start ausgeführt wurde.");
        }
        renderPanel.setSize(width, height);
        renderPanel.setPreferredSize(new Dimension(width, height));
        frame.pack();
    }

    /**
     * Diese Methode beendet das Spiel.<br /> Das heißt, dass das Fenster geschlossen, alle belegten Ressourcen
     * freigegeben und auch die virtuelle Maschine von Java beendet wird.
     */
    @API
    public static void exit() {
        if (mainThread == null) {
            System.exit(0);

            return;
        }

        mainThread.interrupt();
    }

    /**
     * Gibt einen Nachricht in einem modalen Dialogfenster aus.
     * Der Dialog ist über {@link javax.swing.JOptionPane} implementiert.
     * @param message   Der Inhalt der Botschaft im Dialogfenster.
     * @param title     Der Titel des Dialogfensters.
     */
    @API
    public static void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Öffnet ein modales Dialogfenster, in dem der Nutzer zur Eingabe von Text in einer
     * Zeile aufgerufen wird.
     * Der Dialog ist über {@link javax.swing.JOptionPane} implementiert.
     * @param message   Der Inhalt der Botschaft im Dialogfenster.
     * @param title     Der Titel des Dialogfensters.
     * @return          Die Eingabe des Nutzers. Ist <code>null</code>, wenn der Nutzer
     *                  den Dialog abgebrochen hat.
     */
    @API
    public static String requestStringInput(String message, String title) {
        return JOptionPane.showInputDialog(frame, message, title, JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Öffnet ein modales Dialogfenster mit Ja/Nein-Buttons.
     * Der Dialog ist über {@link javax.swing.JOptionPane} implementiert.
     * @param message   Der Inhalt der Botschaft im Dialogfenster.
     * @param title     Der Titel des Dialogfensters.
     * @return          Die Eingabe des Nutzers:
     *                  <ul>
     *                      <li>Ja -> <code>true</code></li>
     *                      <li>Nein -> <code>false</code></li>
     *                      <li>Abbruch (= Dialog manuell schließen) -> <code>false</code></li>
     *                  </ul>
     */
    @API
    public static boolean requestYesNo(String message, String title) {
        return JOptionPane.showConfirmDialog(frame, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /**
     * Öffnet ein modales Dialogfenster mit OK/Abbrechen-Buttons.
     * Der Dialog ist über {@link javax.swing.JOptionPane} implementiert.
     * @param message   Der Inhalt der Botschaft im Dialogfenster.
     * @param title     Der Titel des Dialogfensters.
     * @return          Die Eingabe des Nutzers:
     *                  <ul>
     *                      <li>OK -> <code>true</code></li>
     *                      <li>Abbrechen -> <code>false</code></li>
     *                      <li>Abbruch (= Dialog manuell schließen) -> <code>false</code></li>
     *                  </ul>
     */
    @API
    public static boolean requestOkCancel(String message, String title) {
        return JOptionPane.showConfirmDialog(frame, message, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }

    @NoExternalUse
    public static java.awt.Point getMousePositionInFrame() {
        return mousePosition;
    }
}
