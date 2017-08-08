package ea.internal.frame;

public abstract class FrameSubthread extends Thread {
    /**
     * Gibt an, ob dieser Thread gerade für eine framespezifische Berechnung aktiv ist.
     */
    private boolean frameActive;

    protected FrameSubthread(String name) {
        super(name);

        this.setDaemon(true);
    }

    /**
     * Implementiert die frameweise Synchronisation mit dem Master-Thread. Auf Signal von diesem
     * wird die {@link #dispatchFrame()}-Routine des Threads ausgeführt.
     */
    @Override
    public final void run() {
        while (!interrupted()) {
            // Warte auf Signal von Master
            try {
                synchronized (this) {
                    while (!frameActive) {
                        this.wait();
                    }
                }
            } catch (InterruptedException e) {
                break;
            }

            dispatchFrame();

            synchronized (this) {
                frameActive = false;
                this.notifyAll();
            }
        }
    }

    /**
     * Ein Semi-Start. Ein normaler Start startet den Thread von Beginn an. Da dies nicht mehrfach
     * möglich ist, aber frameweise neu gestartet werden soll, ermöglicht der Semi-Start, die
     * nächste <code>dispatchFrame</code>-Routine starten.
     */
    public final void startFrame() {
        synchronized (this) {
            this.frameActive = true;
            this.notifyAll();
        }
    }

    /**
     * Ein Semi-Join. Ein noromaler join endet, sobald der Thread beendet ist. Da dieser Thread
     * frameweise "fertig" ist, aber nicht terminieren soll (da er im folgenden Frame wieder
     * gebraucht wird), blockiert diee Methode - vergleichbar mit einem "frameweisen join" -
     * solange, bis der aktuelle Frame abgearbeitet ist (ist der Frame schon fertig, so terminiert
     * die Methode direkt).
     */
    public final void joinFrame() throws InterruptedException {
        synchronized (this) {
            while (frameActive) {
                this.wait();
            }
        }
    }

    /**
     * In dieser Methode wird die jeweilige (frameweise) Logik des Threads ausgeführt.
     */
    public abstract void dispatchFrame();
}