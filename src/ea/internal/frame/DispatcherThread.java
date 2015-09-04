package ea.internal.frame;

import java.util.Queue;

/**
 * Der <code>DispatcherThread</code> führt frameweise alle (in diesem Frame zu aktivierenden) Listener aus.
 * Created by andonie on 14.02.15.
 */
public class DispatcherThread
extends FrameSubthread {
    /**
     * Der Hilfs-Counter für die Anzahl an Dispatcher-Threads
     */
    private static int dtcnt = 1;

    /**
     * Dieser Wert gibt an, ob noch neue Inputs für die Queue in diesem Frame erwartet werden können.
     */
    private boolean nomoreNewStuff;

    /**
     * Die Queue, aus der die Dispatchable Events aus ausgeführt werden.
     */
    private final Queue<Dispatchable> dispatchableQueue;

    /**
     * Erstellt einen neuen Dispatcher-Thread, der die Abarbeitung der Dispatches übernimmt.
     * @param dispatchableQueue Die Warteschlange, aus der die abzuarbeitenden Dispatchable-Events genommen werden.
     */
    public DispatcherThread(FrameThread master, Queue<Dispatchable> dispatchableQueue) {
        super(master, "EA Event Dispatcher #" + dtcnt++);
        this.setDaemon(true);

        this.dispatchableQueue = dispatchableQueue;
    }

    /**
     * Initialisiert den Dispatcher für den kommenden Frame.
     */
    public void frameInit() {
        nomoreNewStuff = false;
    }

    /**
     * Der Aufruf dieser Methode sorgt dafür, dass der Thread nicht mehr auf weitere Dispatchable Events wartet,
     * sobald die Queue als nächstes mal leer ist.
     */
    public void frameAbschliessen() {
        nomoreNewStuff = true;
    }

    /**
     * Baut für die Dauer des Frames alle Dispatchable Events ab.
     */
    @Override
    public void frameLogic() {
        System.out.println("3");
        while (!nomoreNewStuff || !dispatchableQueue.isEmpty()) {
            if(dispatchableQueue.isEmpty()) {
                //Warten
                synchronized (dispatchableQueue) {
                    //Warten, Unterbrechung erwünscht
                    try {
                        dispatchableQueue.wait(1);
                    } catch (InterruptedException e) { }
                }
            } else {
                synchronized (dispatchableQueue) {
                    Dispatchable next = dispatchableQueue.remove();
                    if(next == null) continue;
                    next.dispatch();
                }

            }
        }
        System.out.println("4");
    }

}
