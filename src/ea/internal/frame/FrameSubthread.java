package ea.internal.frame;

/**
 * Dieser
 * Created by andonie on 14.02.15.
 */
public abstract class FrameSubthread
extends Thread {

    /**
     * Gibt an, ob dieser Thread gerade für eine framespezifische Berechnung aktiv ist.
     */
    private boolean frameActive;

    /**
     * Gibt an, ob dieser Thread gerade für eine framespezifische Berechnung aktiv ist.
     */
    protected boolean isFrameActive() {
        return frameActive;
    }

    /**
     * Das Objekt, das ggf. zum Locken des Master-Threads verwendet wird, falls dieser einen Semi-Join auf
     * diesem Subthread macht.
     */
    private final Object masterLock = new Object();

    protected FrameSubthread(String threadname) {
        super(threadname);
        super.setDaemon(true);
    }


    /**
     * Run-Methode: Implementiert die frameweise Synchronisation mit dem Master-Thread. Auf Signal von Diesem wird
     * die <code>frameLogic()</code>-Routine des Threads ausgeführt.
     */
    @Override
    public final void run() {
        while(!interrupted()) {
            //Warte auf Okay von Master
            try {
                synchronized (this) {
                    this.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            frameActive = true;
            frameLogic();
            frameActive = false;

            synchronized (masterLock) {
                masterLock.notifyAll();
            }
        }
    }

    /**
     * Ein Semi-Start. Ein normaler Start startet den Thread von Beginn an. Da dies nicht mehrfach möglich ist,
     * aber frameweise neu gestartet werden soll, ermöglicht der Semi-Start, die nächste <code>frameLogic</code>-Routine
     * starten.
     */
    public final void semi_start() {
        synchronized (this) {
            this.notifyAll();
        }
    }

    /**
     * Ein Semi-Join. Ein noromaler join endet, sobald der Thread beendet ist.
     * Da dieser Thread frameweise "fertig" ist, aber nicht terminieren soll (da er im folgenden Frame wieder gebraucht
     * wird), blockiert diese Methode - vergleichbar mit einem "frameweisen join" - solange, bis der aktuelle Frame
     * abgearbeitet ist (ist der Frame schon fertig, so terminiert die Methode direkt).
     * @throws InterruptedException
     */
    public final void semi_join() throws InterruptedException {
        if(frameActive) {
            synchronized (masterLock) { masterLock.wait(); }
        }
    }

    /**
     * In dieser Methode wird die jeweilige (frameweise) Logik des Threads ausgeführt.
     */
    public abstract void frameLogic();

}
