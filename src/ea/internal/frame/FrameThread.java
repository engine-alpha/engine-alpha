package ea.internal.frame;

import ea.FrameUpdateReagierbar;
import ea.Game;
import ea.Ticker;
import ea.internal.ano.NoExternalUse;
import ea.internal.gra.Zeichner;
import ea.internal.phy.WorldHandler;
import ea.internal.ui.UIEvent;
import ea.internal.util.Logger;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Ein Objekt der Klasse <code>FrameLogic</code> überwacht die frameweise "Arbeit" der Engine.
 * Innerhalb eines Frames passiert:
 * <ul>
 *     <li><b>Rendern</b> (grafische Darstellung aller sichtbaren Elemente in korrekter Weise)</li>
 *     <li><b>Listener-Cheks</b> aller angemeldeten Listener (s.u.)</li>
 *     <li><b>Listener-Dispatch</b> aller zu aktivierenden Listener der Engine (wie <code>Ticker</code> oder
 *     <code>Reagierbar</code>-Interfaces)</li>
 *     <li>Berechnungen der <b>internen Physik</b></li>
 * </ul>
 *
 * Jede <i>Frame-Logic</i> arbeitet intern mit einer festen <b>FPS</b>-Zahl (<b>F</b>rames <b>p</b>er <b>s</b>econd).
 * Diese Zahl gibt an, wie viele Frames <i>maximal</i> pro Sekunde ausgeführt werden sollen.
 *
 * Ein Frame ist ein "Schritt" innerhalb der Engine. Jede Bewegung und Berechnung lässt sich einem Frame zuordnen.
 * Created by andonie on 14.02.15.
 */
public class FrameThread
extends Thread {

    private static int threadcnt = 1;

    /**
     * Gibt an, wie lange ein Frame bei aktueller FPS-Einstellung maximal dauern darf.
     * Wird verwendet, um ggf. (also bei schnellerer Arbeit als nötig) den Timeout zu bestimmen.
     * Standard ~=~ 60 FPS
     * @see #setFPS(float)
     */
    private int maxmillis = 16;

    /**
     * Referenz auf das Spiel, das diesen Thread erstellt hat (und das damit zu diesem
     * Thread gehört).
     */
    private Game game;

    /**
     * Gibt die Game Referenz aus
     * @return  Die Referenz auf das Game Objekt, das zu diesem Frame Thread gehört.
     */
    @NoExternalUse
    public Game getGame() {
        return game;
    }

    /**
     * Gibt an, ob das Spiel bereits initiiert wurde, also ob innerhalb des
     * Frame-Threads bereits die Initiierungs-Methode des zugehörigen Game-Objekts aufgerufen
     * wurde.
     */
    private boolean gameInitiated = false;

    /**
     * Ist solange true, wie der Thread laufen soll.
     */
    private boolean sollLaufen = true;

    /**
     * Setzt die aktuelle FPS-Zahl neu
     * @param fps   Die Anzahl an Frames pro Sekunde, die berechnet werden sollen.
     * @see #setFPS(float)
     */
    public void setFPS(float fps) {
        if(fps > 100) {
            Logger.error("Frame-Logik", "Die FPS Zahl darf nicht größer als 100 sein.");
            return;
        }
        maxmillis = (int) (1000 / fps);
    }

    /**
     * Gibt die <i>tatsächliche</i> Dauer des letzten Frames an. (in ms)
     */
    private int lastFrameTime = maxmillis;

    /**
     * Gibt die tatsächliche Dauer des letzten Frames aus.
     * @return die tatsächliche Dauer des letzten Frames in Millisekunden.
     */
    public int getLastFrameTime() {
        return lastFrameTime;
    }

    /**
     * Gibt die <i>ungefähre</i> FPS-Zahl dieser Frame-Logik aus.
     * @return  Die <i>ungefähre</i> Framerate dieser Frame-Logik in Frames/Sekunde.
     */
    public float getFPS() {
        return 1000f / ((float) maxmillis);
    }

    /**
     * Der World-Thread. Übernimmt die Physik-relevanten Änderungen.
     */
    private final WorldThread worldThread;

    /**
     * Der Render-Thread. Übernimmt die frameweise Visualisierung.
     */
    private final RenderThread renderThread;

    /**
     * Der Dispatcher-Thread. Übernimmt die Ausführung von Listener-Events aus der API.
     */
    private final DispatcherThread dispatcherThread;

    private final ProducerThread[] producerThreads;

    private final EventThread<UIEvent> uiEventThread;
    private final EventThread<Dispatchable> internalJokerProducer;
    private final TickerThread tickerThread;
    private final FrameUpdateThread frameUpdateThread;


    /**
     * Konstruktor erstellt den Thread, aber <b>startet ihn nicht</b>.
     */
    public FrameThread(Zeichner zeichner, WorldHandler worldHandler) {
        super("Frame Master Thread #" + threadcnt++); //<- eigener Name (f. Multi-Window)
        this.setDaemon(true); // Daemon setzen

        //Die Dispatchable-Queue
        Queue<Dispatchable> queue = new LinkedList<Dispatchable>();

        //Die Childs initiieren
        worldThread = new WorldThread(this, worldHandler);
        renderThread = new RenderThread(this, zeichner);
        dispatcherThread = new DispatcherThread(this, queue);
        producerThreads = new ProducerThread[] {
                uiEventThread=new EventThread<UIEvent>(this, "UI", queue),
                internalJokerProducer = new EventThread<Dispatchable>(this, "Network", queue),
                tickerThread=new TickerThread(this, queue),
                frameUpdateThread=new FrameUpdateThread(this, queue)
        };

        //Startet die Threads. Sie verharren vorerst in Wartehaltung, bis die Run-Methode dieses Threads
        //Sie aus dem Wartezustand holt.
        worldThread.start();
        renderThread.start();
        dispatcherThread.start();
        for (ProducerThread pt : producerThreads) {
            pt.start();
        }
    }

    /**
     * Fuegt zum naechsten Frame ein UIEvent hinzu.
     */
    public void addUIEvent(UIEvent uiEvent) {
        if(uiEvent == null) {
            throw new IllegalArgumentException("UIEvent to be added was null!");
        }
        uiEventThread.enqueueDispatchableForNextFrame(uiEvent);
    }

    /**
     * Fuegt ein internes Event für die
     * Abarbeitung des kommenden Threads zu. Dies kann z.B. sein:
     * <ul>
     *     <li>Netzwerk-Event (empfangene Informationen vom Kommunikationspartner)</li>
     *     <li>Interne Kollision</li>
     * </ul>
     * @param d Ein Netzwerk-Event, das im kommenden Frame aufgelöst werden soll.
     */
    public void addInternalEvent(Dispatchable d) {
        internalJokerProducer.enqueueDispatchableForNextFrame(d);
    }

    public void tickerAnmelden(Ticker ticker, int intervall) {
        tickerThread.addTicker(ticker, intervall);
    }

    public void tickerAbmelden(Ticker ticker) {
        tickerThread.removeTicker(ticker);
    }

    public void frameUpdateReagierbarAnmelden(FrameUpdateReagierbar fur) {
        frameUpdateThread.addFrameUpdateReagierbar(fur);
    }

    public void frameUpdateReagierbarAbmelden(FrameUpdateReagierbar fur) {
        frameUpdateThread.removeFrameUpdateReagierbar(fur);
    }

    /**
     * Innerhalb dieser Run-Methode läuft die Frame-Logik.
     */
    @Override
    public void run() {
        long deltaT = maxmillis; // Das tatsächliche DeltaT aus dem letzten Frame-Schritt (zu Beginn der Idealfall)
        lastFrameTime = maxmillis;
        while(sollLaufen && !interrupted()) {

            Logger.verboseInfo("Frame-System", "Frame-Start");

            long tStart = System.currentTimeMillis();

            //Eigentliche Arbeit: Möglichst hoch parallelisiert

            //Render-Thread (läuft vollkommen parallel)
            renderThread.semi_start();

            //Physics (WorldThread)
            worldThread.setDT(maxmillis);
            worldThread.semi_start();

            //Start Producers
            for (ProducerThread pt : producerThreads) {
                pt.semi_start();
            }


            //System.out.println("Join World");
            //Join: WorldThread
            try {
                worldThread.semi_join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println("Joined World");

            //Start Dispatcher
            dispatcherThread.frameInit();
            dispatcherThread.semi_start();


            //System.out.println("Join Producers");
            //Join: Producers
            for (ProducerThread pt : producerThreads) {
                try {
                    pt.semi_join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println("Joined Producers");

            //-> Beende Wartehaltung d. Dispatchers
            dispatcherThread.frameAbschliessen();


            //System.out.println("Join Dispatcher");
            //Join: Dispatcher
            try {
                dispatcherThread.semi_join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println("Joined Dispatcher");

            //Frame Update für die World
            //if(gameInitiated) {
            //    game.frameUpdate(lastFrameTime/1000f);
            //}

            //System.out.println("Join Render");
            //Join: RenderThread
            try {
                renderThread.semi_join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //System.out.println("Joined Render");

            //System.out.println("__________________________");
            //FrameSubthread.logger.log("_______________");

            //ENDE der eigentlichen Arbeit

            long tEnd = System.currentTimeMillis();
            deltaT = tEnd - tStart;




            //ggf. warten:
            if (deltaT < maxmillis) {
                try {
                    lastFrameTime = maxmillis;
                    Thread.sleep(maxmillis-deltaT);
                } catch (InterruptedException e) {}
            } else {
                lastFrameTime = (int)deltaT;
            }
        }

        //Thread soll aufhören: Sauber machen!
        worldThread.anhalten();
        renderThread.anhalten();
        dispatcherThread.anhalten();
        for(ProducerThread pt : producerThreads) {
            pt.anhalten();
        }

        Logger.verboseInfo("Frame-System", "Frame-Ende");

    }

    /**
     * Meldet ein (frisch gestartetes) Spiel beim Frame-Thread an.
     * Zum Initiieren innerhalb der Frame-Logik und für die kommenden Frame Updates.
     * Im nächsten Frame wird die Intiierungsmethode der Game-Klasse dispatcht.
     * @param game Das Spiel, das initiert werden soll.
     */
    @NoExternalUse
    public void gameHandshake(final Game game) {
        //Add Dispatchable Event to execute Game Init.
        //Arbitrarily in the network thread.
        this.game =game;
        this.internalJokerProducer.enqueueDispatchableForNextFrame(new Dispatchable() {
            @Override
            public void dispatch() {
                game.initialisieren();
                gameInitiated = true;
                frameUpdateReagierbarAnmelden(game);
            }
        });
    }

    /**
     * Stoppt den Thread sicher. Kann nicht rückgängig gemacht werden.
     */
    @NoExternalUse
    public void anhalten() {
        sollLaufen = false;
    }
}
