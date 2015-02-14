package ea.internal.frame;

import java.util.Queue;

/**
 * Ein Producer-Thread ist ein Thread, der <code>Dispatchable</code> Events erzeugt und in eine
 * entsprechende Queue zur Abarbeitung / Ausführung legt. Welche Art von Events das letztlich ist, hängt von der
 * Created by andonie on 14.02.15.
 */
public abstract class ProducerThread
extends FrameSubthread {

    private static int ptcnt = 1;

    /**
     * Die Queue, in der die Dispatchables eingeräumt werden sollen.
     */
    private final Queue<Dispatchable> queue;

    /**
     * Dieser Wert ist dann <code>true</code>, wenn für den aktuellen Frame
     * von diesem Producer keine weiteren Dispatchable Events kommen werden.
     */
    private boolean done;

    protected ProducerThread(Queue<Dispatchable> queue, String type) {
        super("Producer Thread #" + ptcnt++ + " (" + type + ")");

        this.queue = queue;
    }

    /**
     * Berechnet und erstellt - sofern möglich - das nächste Dispatchable Event für diesen Producer Thread.
     * @return  Ein neues <code>Dispatchable</code> Event, dass noch in diesem Frame ausgeführt werden soll.
     *          Gibt es für diesen Frame kein weiteres <code>Dispatchable</code>-Event, so ist die
     *          Rückgabe <code>null</code>.
     */
    public abstract Dispatchable nextEvent();

    /**
     * Die Run-Methode: Holt sukzessive alle noch ausstehenden Dispatcher (Implementierung in Child-Class) und fügt
     * diese synchronized in die Queue ein.
     */
    @Override
    public final void frameLogic() {
        while(!done) {
            Dispatchable next = nextEvent();
            if(next == null) {
                done = true;
            } else {
                synchronized (queue) {
                    queue.add(next);
                }
            }
        }
    }

}
