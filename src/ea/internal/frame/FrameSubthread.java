package ea.internal.frame;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Dieser
 * Created by andonie on 14.02.15.
 */
public abstract class FrameSubthread
extends Thread {

    /**
     * Interne Klasse zum Loggen eines Strings
     */
    public static class StringLogger {
        StringWriter writer;

        public StringLogger() {
            writer = new StringWriter();
        }

        synchronized void log(String s) {
            writer.write(s + "\n");
        }

        public synchronized String getString() {
            writer.flush();
            return writer.toString();
        }
    }

    public static final StringLogger logger = new StringLogger();

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

    /**
     * Referenz auf den Master-Framethread, der diesen Subthread nutzt.
     */
    protected final FrameThread master;

    protected FrameSubthread(FrameThread master, String threadname) {
        super(threadname);
        this.master = master;
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
                    while(!frameActive)
                        this.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //System.out.println("~~~~~~~~~~~~~~Frame Start: " + getName());

            frameLogic();
            frameActive = false;
            //System.out.println("~~~~~~~~~~~~~~~~~~~~~~END: " + getName());


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
        frameActive = true;
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
        synchronized (masterLock) {
            while(frameActive) {
                //System.out.println("Enter Wait");
                masterLock.wait();
                //System.out.println("Left Wait");
            }
        }
    }

    /**
     * Gibt den Frame-Thread aus, zu dem dieser Sub-Thread gehört.
     * @return  der Frame-Thread, zu dem dieser Sub-Thread gehört.
     */
    public FrameThread getMaster() {
        return master;
    }

    /**
     * In dieser Methode wird die jeweilige (frameweise) Logik des Threads ausgeführt.
     */
    public abstract void frameLogic();

}
