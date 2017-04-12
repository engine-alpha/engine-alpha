package ea.internal.frame;

import java.util.Queue;

/**
 * Der <code>DispatcherThread</code> führt frameweise alle (in diesem Frame zu aktivierenden)
 * Listener aus. Created by andonie on 14.02.15.
 */
public class DispatcherThread extends FrameSubthread {
    /**
     * Der Hilfs-Counter für die Anzahl an Dispatcher-Threads
     */
    private static int counter = 1;

    /**
     * Die Queue, aus der die Dispatchable Events aus ausgeführt werden.
     */
    private final Queue<Dispatchable> dispatchableQueue;

    private boolean producerActive = false;

    /**
     * Erstellt einen neuen Dispatcher-Thread, der die Abarbeitung der Dispatches übernimmt.
     *
     * @param dispatchableQueue Die Warteschlange, aus der die abzuarbeitenden Dispatchable-Events
     *                          genommen werden.
     */
    public DispatcherThread(FrameThread master, Queue<Dispatchable> dispatchableQueue) {
        super(master, "EA Event Dispatcher #" + counter++);
        this.setDaemon(true);

        this.dispatchableQueue = dispatchableQueue;
    }

    /**
     * Baut für die Dauer des Frames alle Dispatchable Events ab.
     */
    @Override
    public void dispatchFrame() {
        while (true) {
            Dispatchable next = dispatchableQueue.poll();

            if (next == null) {
                try {
                    synchronized (dispatchableQueue) {
                        if (dispatchableQueue.isEmpty() && !producerActive) {
                            return;
                        } else if (!dispatchableQueue.isEmpty()) {
                            continue;
                        }

                        dispatchableQueue.wait();
                    }
                } catch (InterruptedException e) {
                    interrupt();
                }

                continue;
            }

            next.dispatch();
        }
    }

    public void setProducerActive(boolean producerActive) {
        synchronized (dispatchableQueue) {
            this.producerActive = producerActive;
            dispatchableQueue.notifyAll();
        }
    }
}
