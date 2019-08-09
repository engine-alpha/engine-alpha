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

package ea;

import ea.event.MouseButton;
import ea.event.MouseWheelEvent;
import ea.internal.Bounds;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.internal.graphics.RenderPanel;
import ea.internal.io.ImageLoader;
import ea.internal.physics.WorldHandler;

import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

/**
 * Diese Klasse gibt Zugriff auf das aktuelle Spiel.
 *
 * @author Michael Andonie
 * @author Niklas Keller
 */
@SuppressWarnings ( "StaticVariableOfConcreteClass" )
public final class Game {

    private static final float DESIRED_FRAME_DURATION = 0.016f;
    private static final int NANOSECONDS_PER_SECOND = 1000000000;
    private static final Color COLOR_FPS_BACKGROUND = new Color(255, 255, 255, 50);
    private static final Color COLOR_FPS_BORDER = new Color(0, 106, 214);
    private static final Color COLOR_BODY_COUNT_BORDER = new Color(0, 214, 84);
    private static final Color COLOR_BODY_COUNT_BACKGROUND = new Color(255, 255, 255, 50);
    private static final int DEBUG_INFO_HEIGHT = 20;
    private static final int DEBUG_INFO_LEFT = 10;
    private static final int DEBUG_INFO_TEXT_OFFSET = 16;
    private static final Color DEBUG_GRID_COLOR = new Color(255, 255, 255, 100);
    private static final Color DEBUG_GRID_COLOR_0 = new Color(255, 255, 255, 150);

    static {
        System.setProperty("sun.java2d.opengl", "true"); // ok
        System.setProperty("sun.java2d.d3d", "true"); // ok
        System.setProperty("sun.java2d.noddraw", "false"); // set false if possible, linux
        System.setProperty("sun.java2d.pmoffscreen", "true"); // set true if possible, linux
        System.setProperty("sun.java2d.ddoffscreen", "true"); // ok, windows
        System.setProperty("sun.java2d.ddscale", "true"); // ok, hardware accelerated image scaling on windows
    }

    /**
     * Wird debug auf <code>true</code> gesetzt, so werden ausführliche Informationen zu Tickern im Logger ausgegeben.
     */
    private static boolean debug;

    /**
     * Wird <code>verbose</code> auf <code>true</code> geesetzt, so werden äuerst ausführliche Log-Ausgaben gemacht.
     * Dies betrifft below anderem Informationen über das Verhalten der frameweise arbeitenden Threads. Hierfür wurde
     * diese Variable eingeführt.
     *
     * @author andonie
     * @version 11.04.2017
     */
    private static boolean verbose;

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

    private static Phaser frameBarrierStart = new Phaser(2);

    private static Phaser frameBarrierEnd = new Phaser(2);

    private static Phaser worldStepEndBarrier = new Phaser(2);

    private static Thread renderThread;

    private static Thread mainThread;

    /**
     * Ein Thread Pool Executor Service für Engine-interne tasks.
     */
    @Internal
    public static final ExecutorService threadPoolExecutor = Executors.newCachedThreadPool();//(ThreadPoolExecutor) Executors.newFixedThreadPool(6);

    /**
     * Queue aller Dispatchables, die im nächsten Frame ausgeführt werden.
     */
    private static volatile Queue<Runnable> dispatchableQueue = new ConcurrentLinkedQueue<>();

    /**
     * Speichert den Zustand von Tasten der Tastatur. Ist ein Wert <code>true</code>, so ist die entsprechende Taste
     * gedrückt, sonst ist der Wert <code>false</code>.
     */
    private static Collection<Integer> pressedKeys = new HashSet<>();

    /**
     * Letzte Mausposition.
     */
    private static java.awt.Point mousePosition;

    private static float frameDuration;

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

                Game.scene.render(g, Game.width, Game.height);

                g.setTransform(transform);

                if (isDebug()) {
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

        java.awt.event.KeyListener keyListener = new KeyListener();

        frame.addKeyListener(keyListener);
        renderPanel.addKeyListener(keyListener);
        renderPanel.setFocusable(true);

        MouseAdapter mouseListener = new MouseListener();

        renderPanel.addMouseMotionListener(mouseListener);
        renderPanel.addMouseListener(mouseListener);
        renderPanel.addMouseWheelListener(Game::enqueueMouseWheelEvent);

        try {
            frame.setIconImage(ImageLoader.load("assets/favicon.png"));
        } catch (Exception e) {
            // FIXME: Doesn't work in JAR in BlueJ
            // Logger.warning("IO", "Standard-Icon konnte nicht geladen werden.");
        }

        renderThread = new RenderThread();
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
    @Internal
    private static void renderDebug(Graphics2D g) {
        AffineTransform transform = g.getTransform();
        Camera camera = Game.scene.getCamera();
        Vector position = camera.getPosition();
        float rotation = -camera.getRotation();

        g.setClip(0, 0, width, height);
        g.translate(width / 2, height / 2);

        float pixelPerMeter = camera.getZoom();

        g.rotate(rotation, 0, 0);
        g.translate(-position.getX() * pixelPerMeter, position.getY() * pixelPerMeter);

        int gridSizeInMeters = Math.round(150 / pixelPerMeter);
        float gridSizeInPixels = gridSizeInMeters * pixelPerMeter;
        float gridSizeFactor = gridSizeInPixels / gridSizeInMeters;

        if (gridSizeInMeters > 0 && gridSizeInMeters < 100000) {
            int windowSizeInPixels = (int) Math.ceil(Math.max(width, height));

            int startX = (int) (position.getX() - windowSizeInPixels / 2 / pixelPerMeter);
            int startY = (int) ((-1 * position.getY()) - windowSizeInPixels / 2 / pixelPerMeter);

            startX -= (startX % gridSizeInMeters);
            startY -= (startY % gridSizeInMeters);

            startX -= gridSizeInMeters;

            int stopX = (int) (startX + windowSizeInPixels / pixelPerMeter + gridSizeInMeters);
            int stopY = (int) (startY + windowSizeInPixels / pixelPerMeter + gridSizeInMeters);

            g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            g.setColor(DEBUG_GRID_COLOR);

            for (int x = startX; x < stopX; x += gridSizeInMeters) {
                g.fillRect((int) (x * gridSizeFactor) - 1, (int) ((startY - 1) * gridSizeFactor), 2, (int) (windowSizeInPixels + 2 * gridSizeInPixels));
            }

            for (int y = startY; y < stopY; y += gridSizeInMeters) {
                g.fillRect((int) ((startX - 1) * gridSizeFactor), (int) (y * gridSizeFactor - 1), (int) (windowSizeInPixels + 2 * gridSizeInPixels), 2);
            }

            for (int x = startX; x < stopX; x += gridSizeInMeters) {
                for (int y = startY; y < stopY; y += gridSizeInMeters) {
                    g.drawString(x + " / " + -y, x * gridSizeFactor + 5, y * gridSizeFactor - 5);
                }
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

        String fpsMessage = "FPS: " + (frameDuration == 0 ? "∞" : Math.round(1 / frameDuration));
        bounds = fm.getStringBounds(fpsMessage, g);

        g.setColor(COLOR_FPS_BORDER);
        g.fillRect(DEBUG_INFO_LEFT, y, (int) bounds.getWidth() + DEBUG_INFO_HEIGHT, (int) bounds.getHeight() + DEBUG_INFO_TEXT_OFFSET);
        g.setColor(COLOR_FPS_BACKGROUND);
        g.drawRect(DEBUG_INFO_LEFT, y, (int) bounds.getWidth() + DEBUG_INFO_HEIGHT - 1, (int) bounds.getHeight() + DEBUG_INFO_TEXT_OFFSET - 1);

        g.setColor(Color.WHITE);
        g.setFont(displayFont);
        g.drawString(fpsMessage, DEBUG_INFO_LEFT + 10, y + 8 + fm.getHeight() - fm.getDescent());

        y += fm.getHeight() + DEBUG_INFO_HEIGHT;

        String bodyMessage = "Bodies: " + scene.getWorldHandler().getWorld().getBodyCount();
        bounds = fm.getStringBounds(bodyMessage, g);

        g.setColor(COLOR_BODY_COUNT_BORDER);
        g.fillRect(DEBUG_INFO_LEFT, y, (int) bounds.getWidth() + DEBUG_INFO_HEIGHT, (int) bounds.getHeight() + DEBUG_INFO_TEXT_OFFSET);
        g.setColor(COLOR_BODY_COUNT_BACKGROUND);
        g.drawRect(DEBUG_INFO_LEFT, y, (int) bounds.getWidth() + DEBUG_INFO_HEIGHT - 1, (int) bounds.getHeight() + DEBUG_INFO_TEXT_OFFSET - 1);

        g.setColor(Color.WHITE);
        g.setFont(displayFont);
        g.drawString(bodyMessage, DEBUG_INFO_LEFT + 10, y + 8 + fm.getHeight() - fm.getDescent());
    }

    private static void run() {
        renderThread.start();

        frameDuration = DESIRED_FRAME_DURATION;

        long frameStart = System.nanoTime();
        long frameEnd;

        while (!Thread.interrupted()) {
            if (nextScene != null) {
                scene = nextScene;
                nextScene = null;
            }

            try {
                float deltaSeconds = Math.min(2 * DESIRED_FRAME_DURATION, frameDuration);

                scene.worldStep(deltaSeconds, worldStepEndBarrier);

                frameBarrierStart.arriveAndAwaitAdvance();
                worldStepEndBarrier.arriveAndAwaitAdvance();

                scene.onFrameUpdateInternal(deltaSeconds);

                Runnable runnable = dispatchableQueue.poll();
                while (runnable != null) {
                    runnable.run();
                    runnable = dispatchableQueue.poll();
                }

                frameBarrierEnd.arriveAndAwaitAdvance();

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
                Game.exit();

                // noinspection CallToPrintStackTrace
                e.printStackTrace();

                break; // comment out to keep window open
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

        frame.setVisible(false);
        frame.dispose();

        System.exit(0);
    }

    @Internal
    public static Vector convertMousePosition(Scene scene, java.awt.Point mousePosition) {
        // Finde Klick auf Zeichenebene, die Position relativ zum Ursprung des RenderPanel-Canvas.
        // Mausklick-Position muss mit Zoom-Wert verrechnet werden
        float zoom = scene.getCamera().getZoom();
        float rotation = scene.getCamera().getRotation();
        Vector position = scene.getCamera().getPosition();

        return new Vector(position.getX() + (((float) Math.cos(Math.toRadians(rotation)) * (mousePosition.x - width / 2f) + (float) Math.sin(Math.toRadians(rotation)) * (mousePosition.y - height / 2f))) / zoom, position.getY() + (((float) Math.sin(rotation) * (mousePosition.x - width / 2f) - (float) Math.cos(rotation) * (mousePosition.y - height / 2f))) / zoom);
    }

    /**
     * Diese Methode wird immer dann ausgeführt, wenn das Mausrad bewegt wurde und ein MouseWheelEvent registriert
     * wurde.
     *
     * @param mouseWheelEvent das Event.
     */
    private static void enqueueMouseWheelEvent(java.awt.event.MouseWheelEvent mouseWheelEvent) {
        MouseWheelEvent mouseWheelAction = new MouseWheelEvent((float) mouseWheelEvent.getPreciseWheelRotation());
        enqueue(() -> scene.onMouseWheelMoveInternal(mouseWheelAction));
    }

    @API
    public static void enqueue(Runnable runnable) {
        dispatchableQueue.add(runnable);
    }

    /**
     * Führt eine Operation aus und stellt dabei sicher, dass diese nicht ausgeführt wird, während der WorldStep
     * der Phyiscs-Engine läuft.
     *
     * @param runnable Die auszuführende Operation. Wird entweder sofort ausgeführt (falls der WorldStep nicht läuft)
     *                 oder (falls der WorldStep läuft) enqueued und später ausgeführt, nachdem der WorldStep
     *                 terminiert ist.
     */
    @API
    public static void afterWorldStep(Runnable runnable) {
        Scene currentScene = Game.scene;
        WorldHandler worldHandler = currentScene == null ? null : currentScene.getWorldHandler();
        if (worldHandler == null) {
            Game.enqueue(runnable);
            return;
        }

        synchronized (worldHandler) {
            if (worldHandler.getWorld().isLocked()) {
                enqueue(runnable);
            } else {
                runnable.run();
            }
        }
    }

    /**
     * TODO : Dokumentation!
     */
    @API
    public static void transitionToScene(Scene scene) {
        enqueue(() -> nextScene = scene);
    }

    /**
     * Gibt die gerade aktive Szene an.
     *
     * @return Die gerade aktive Szene. Wurde das Spiel noch nicht gestartet, ist die Rückgabe <code>null</code>.
     *
     * @see ea.Scene
     */
    @API
    public static Scene getActiveScene() {
        return scene;
    }

    /**
     * Gibt an, ob eine bestimmte Taste derzeit heruntergedrückt ist.
     *
     * @param keyCode Die zu testende Taste als Key-Code (also z.B. <code>KeyEvent.VK_D</code>).
     *
     * @return <code>true</code>, wenn die zu testende Taste gerade heruntergedrückt ist. Sonst <code>false</code>.
     *
     * @see KeyEvent#getKeyCode()
     * @see java.awt.event.KeyEvent
     */
    @API
    public static boolean isKeyPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
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
     * @param width  Die neue Breite des Engine-Fensters in px.
     * @param height Die neue Höhe des Engine-Fensters in px.
     */
    @API
    public static void setFrameSize(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new RuntimeException("Die Fenstergröße kann nicht kleiner/gleich 0 sein. " + "Eingabe war: " + width + " - " + height + ".");
        }
        if (renderPanel == null) {
            throw new RuntimeException("Fenster-Resizing ist erst möglich, nachdem Game.start ausgeführt wurde.");
        }
        Game.width = width;
        Game.height = height;
        renderPanel.setSize(width, height);
        renderPanel.setPreferredSize(new Dimension(width, height));
        frame.pack();
    }

    /**
     * Gibt die Fenstergröße <b>in Pixel</b> aus.
     *
     * @return Ein Bounds-Objekt, dessen Höhe und Breite mit Fensterhöhe & -breite übereinstimmt.
     */
    @API
    public static Bounds getFrameSizeInPixels() {
        return new Bounds(0, 0, Game.width, Game.height);
    }

    /**
     * Diese Methode beendet das Spiel.<br> Das heißt, dass das Fenster geschlossen, alle belegten Ressourcen
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
     * Gibt einen Nachricht in einem modalen Dialogfenster aus. Der Dialog ist über {@link javax.swing.JOptionPane}
     * implementiert.
     *
     * @param message Der Inhalt der Botschaft im Dialogfenster.
     * @param title   Der Titel des Dialogfensters.
     */
    @API
    public static void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Öffnet ein modales Dialogfenster, in dem der Nutzer zur Eingabe von Text in einer Zeile aufgerufen wird. Der
     * Dialog ist über {@link javax.swing.JOptionPane} implementiert.
     *
     * @param message Der Inhalt der Botschaft im Dialogfenster.
     * @param title   Der Titel des Dialogfensters.
     *
     * @return Die Eingabe des Nutzers. Ist <code>null</code>, wenn der Nutzer den Dialog abgebrochen hat.
     */
    @API
    public static String requestStringInput(String message, String title) {
        return JOptionPane.showInputDialog(frame, message, title, JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Öffnet ein modales Dialogfenster mit Ja/Nein-Buttons. Der Dialog ist über {@link javax.swing.JOptionPane}
     * implementiert.
     *
     * @param message Der Inhalt der Botschaft im Dialogfenster.
     * @param title   Der Titel des Dialogfensters.
     *
     * @return Die Eingabe des Nutzers:
     * <ul>
     * <li>Ja → <code>true</code></li>
     * <li>Nein → <code>false</code></li>
     * <li>Abbruch (= Dialog manuell schließen) → <code>false</code></li>
     * </ul>
     */
    @API
    public static boolean requestYesNo(String message, String title) {
        return JOptionPane.showConfirmDialog(frame, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /**
     * Öffnet ein modales Dialogfenster mit OK/Abbrechen-Buttons. Der Dialog ist über {@link javax.swing.JOptionPane}
     * implementiert.
     *
     * @param message Der Inhalt der Botschaft im Dialogfenster.
     * @param title   Der Titel des Dialogfensters.
     *
     * @return Die Eingabe des Nutzers:
     * <ul>
     * <li>OK → <code>true</code></li>
     * <li>Abbrechen → <code>false</code></li>
     * <li>Abbruch (= Dialog manuell schließen) → <code>false</code></li>
     * </ul>
     */
    @API
    public static boolean requestOkCancel(String message, String title) {
        return JOptionPane.showConfirmDialog(frame, message, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }

    @Internal
    public static java.awt.Point getMousePositionInFrame() {
        return mousePosition;
    }

    /**
     * Gibt an, ob die Engine gerade im Debug-Modus ausgeführt wird.
     *
     * @return ist dieser Wert <code>true</code>, wird die Engine gerade im Debug-Modus ausgeführt. Sonst ist der Wert
     * <code>false</code>.
     *
     * @see #setDebug(boolean)
     */
    @API
    public static boolean isDebug() {
        return debug;
    }

    /**
     * Gibt an, ob die laufende Instanz der Engine gerade verbose Output gibt.
     *
     * @return ist dieser Wert <code>true</code>, werden extrem ausführliche Logging-Informationen gespeichert.
     * Sonst ist der Wert <code>false</code>.
     *
     * @see #setVerbose(boolean)
     */
    @API
    public static boolean isVerbose() {
        return verbose;
    }

    /**
     * Setzt, ob die aktuell laufende Instanz der Engine verbose Output geben soll.
     *
     * @param value ist dieser Wert <code>true</code>, so wird ein äußerst ausführlicher Log über die Funktionalität
     *              der Engine geführt. Dies ist hauptsächlich für das Debugging an der Engine selbst notwendig.
     *
     * @see #isVerbose()
     * @see #setDebug(boolean)
     */
    @API
    public static void setVerbose(boolean value) {
        verbose = value;
    }

    /**
     * Setzt, ob die Engine im Debug-Modus ausgeführt werden soll.
     *
     * @param value ist dieser Wert <code>true</code>, wird die Engine ab sofort im Debug-Modus ausgeführt. Hierdurch
     *              werden mehr Informationen beim Ausführen der Engine angegeben, zum Beispiel ein Grafisches Raster
     *              und mehr Logging-Informationen. Dies ist hilfreich für Debugging am eigenen Spiel.
     *
     * @see #isDebug()
     */
    @API
    public static void setDebug(boolean value) {
        debug = value;
    }

    @SuppressWarnings ( "AssignmentToStaticFieldFromInstanceMethod" )
    private static class MouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            enqueueMouseEvent(e, true);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            enqueueMouseEvent(e, false);
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

        private void enqueueMouseEvent(MouseEvent e, boolean down) {
            Vector sourcePosition = convertMousePosition(scene, e.getPoint());
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

            enqueue(() -> {
                if (down) {
                    scene.onMouseDownInternal(sourcePosition, button);
                } else {
                    scene.onMouseUpInternal(sourcePosition, button);
                }
            });
        }
    }

    private static class KeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            enqueueKeyEvent(e, true);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            enqueueKeyEvent(e, false);
        }

        private void enqueueKeyEvent(KeyEvent e, boolean down) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE && exitOnEsc) {
                Game.exit();
            }

            enqueue(() -> {
                boolean pressed = pressedKeys.contains(e.getKeyCode());

                if (down) {
                    if (pressed) {
                        return; // Ignore duplicate presses, because they're system dependent
                    }

                    pressedKeys.add(e.getKeyCode());
                } else {
                    pressedKeys.remove(e.getKeyCode());
                }

                if (down) {
                    scene.onKeyDownInternal(e);
                } else {
                    scene.onKeyUpInternal(e);
                }
            });
        }
    }

    private static class RenderThread extends Thread {
        public RenderThread() {
            super("Rendering");
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    frameBarrierStart.awaitAdvanceInterruptibly(frameBarrierStart.arrive());

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
                            } while (bufferStrategy.contentsRestored() && !isInterrupted());

                            if (!bufferStrategy.contentsLost()) {
                                bufferStrategy.show();
                            }

                            Toolkit.getDefaultToolkit().sync();
                        } while (renderPanel.getBufferStrategy().contentsLost() && !isInterrupted());
                    } catch (IllegalStateException e) {
                        //Logger.error(getName(), e.getMessage());
                        e.printStackTrace();
                        Game.exit();
                    }

                    frameBarrierEnd.awaitAdvanceInterruptibly(frameBarrierEnd.arrive());
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}
