package ea.internal.frame;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Ein Producer-Thread ist ein Thread, der <code>Dispatchable</code> Events erzeugt und in eine
 * entsprechende Queue zur Abarbeitung / Ausführung legt. Welche Art von Events das letztlich ist, hängt von der
 * Created by andonie on 14.02.15.
 */
public class EventThread<E extends Dispatchable>
        extends ProducerThread {

    private static int etcnt = 1;

    /**
     * Eine LIFO - Liste mit allen UI-Events, die seit dem letzten Frame-Update geschehen sind.
     */
    private final Queue<E> lastFrameDispatchables = new LinkedList<E>();

    protected EventThread(FrameThread master, String type, Queue<Dispatchable> queue) {
        super(master, "Event Thread #" + etcnt++ + " (" + type + ")", queue);

    }

    /**
     * Informiert den Producer von einem neuen Dispatchable Event. Diese Methode wird (asynchron)
     * von der Quelle des Ereignisses aufgerufen, sobald der zugehörige Auslöser aktiviert wird.
     * @param disp  Ein dispatchable-Objekt, das in der folgenden Frame-Logik ausgeführt werden soll.
     */
    public void enqueueDispatchableForNextFrame(E disp) {
        if(isFrameActive()) {
            //Gerade arbeitet dieser Thread aktiv an der Abarbeitung der Queue. Warten, bis die Queue abgearbeitet ist.
            synchronized (lastFrameDispatchables) {
                try {
                    System.out.println("PreWait (enq)");
                    lastFrameDispatchables.wait();
                    System.out.println("PostWait (enq)");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }
        synchronized (lastFrameDispatchables) {
            lastFrameDispatchables.add(disp);
        }
    }

    /**
     * Die Run-Methode: Holt sukzessive alle noch ausstehenden Dispatcher (Implementierung in Child-Class) und fügt
     * diese synchronized in die Queue ein.
     */
    @Override
    public final void frameLogic() {
        while(!lastFrameDispatchables.isEmpty()) {
            synchronized (dispatcherQueue) {
                dispatcherQueue.add(lastFrameDispatchables.poll());
            }
        }

        //Fertig mit der Übertragung: Die Queue darf wieder gefüllt werden.
        synchronized (dispatcherQueue) {
            dispatcherQueue.notifyAll();
        }
        synchronized (lastFrameDispatchables) {
            lastFrameDispatchables.notifyAll();
        }
    }

}
