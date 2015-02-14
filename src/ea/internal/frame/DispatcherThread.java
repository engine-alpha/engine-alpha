package ea.internal.frame;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Der <code>DispatcherThread</code> führt frameweise alle (in diesem Frame zu aktivierenden) Listener aus.
 * Created by andonie on 14.02.15.
 */
public class DispatcherThread
extends Thread {
    /**
     * Der Hilfs-Counter für die Anzahl an Dispatcher-Threads
     */
    private static int dtcnt = 1;

    /**
     * Dieser Wert gibt an, ob der Thread für den jeweils aktiven Frame bereits abgeschlossen ist.
     */
    private boolean done;

    /**
     * Die Queue, aus der die Dispatchable Events aus ausgeführt werden.
     */
    private final Queue<Dispatchable> dispatchableQueue;

    /**
     * Erstellt einen neuen Dispatcher-Thread, der die Abarbeitung der Dispatches übernimmt.
     * @param dispatchableQueue Die Warteschlange, aus der die abzuarbeitenden Dispatchable-Events genommen werden.
     */
    public DispatcherThread(Queue<Dispatchable> dispatchableQueue) {
        super("EA Event Dispatcher #" + dtcnt++);
        this.setDaemon(true);

        this.dispatchableQueue = dispatchableQueue;
    }

    /**
     * Der Aufruf dieser Methode sorgt dafür, dass der Thread nicht mehr auf weitere Dispatchable Events wartet,
     * sobald die Queue als nächstes mal leer ist.
     */
    public void frameAbschliessen() {
        done = true;
        this.interrupt();
    }

    /**
     * Baut für die Dauer des Frames alle Dispatchable Events ab.
     */
    @Override
    public void run() {
        done = false;
        while (!done || !dispatchableQueue.isEmpty()) {
            if(dispatchableQueue.isEmpty()) {
                //Warten
                synchronized (dispatchableQueue) {
                    //Warten, Unterbrechung erwünscht
                    try {
                        dispatchableQueue.wait();
                    } catch (InterruptedException e) { }
                }
            } else {
                Dispatchable next = dispatchableQueue.poll();
                next.dispatch();
            }
        }
    }

}
