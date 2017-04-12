package ea.internal.frame;

public abstract class FrameSubthread extends Thread {
    /**
     * Gibt an, ob dieser Thread gerade für eine framespezifische Berechnung aktiv ist.
     */
    boolean frameActive;

    /**
     * Referenz auf den Master-Framethread, der diesen Subthread nutzt.
     */
    protected final FrameThread master;

    protected FrameSubthread(FrameThread master, String threadname) {
        super(threadname);
        super.setDaemon(true);
        this.master = master;
    }

    /**
     * Run-Methode: Implementiert die frameweise Synchronisation mit dem Master-Thread. Auf Signal
     * von Diesem wird die <code>dispatchFrame()</code>-Routine des Threads ausgeführt.
     */
    @Override
    public final void run() {
        while (!isInterrupted()) {
            // Warte auf Okay von Master
            try {
                synchronized (this) {
                    while (!frameActive) {
                        this.wait();
                    }
                }
            } catch (InterruptedException e) {
                interrupt();
            }

            // Soll der Thread aufhören?
            if (isInterrupted()) {
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
        frameActive = true;

        synchronized (this) {
            this.notifyAll();
        }
    }

    /**
     * Ein Semi-Join. Ein noromaler join endet, sobald der Thread beendet ist. Da dieser Thread
     * frameweise "fertig" ist, aber nicht terminieren soll (da er im folgenden Frame wieder
     * gebraucht wird), blockiert diese Methode - vergleichbar mit einem "frameweisen join" -
     * solange, bis der aktuelle Frame abgearbeitet ist (ist der Frame schon fertig, so terminiert
     * die Methode direkt).
     */
    public final void joinFrame() throws InterruptedException {
        try {
            synchronized (this) {
                while (frameActive) {
                    this.wait();
                }
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }

    /**
     * Gibt den Frame-Thread aus, zu dem dieser Sub-Thread gehört.
     *
     * @return der Frame-Thread, zu dem dieser Sub-Thread gehört.
     */
    public FrameThread getMaster() {
        return master;
    }

    /**
     * In dieser Methode wird die jeweilige (frameweise) Logik des Threads ausgeführt.
     */
    public abstract void dispatchFrame();
}