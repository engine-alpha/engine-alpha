package ea.internal.frame;

import ea.internal.util.Logger;

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
public class FrameLogic
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
     * Setzt die aktuelle FPS-Zahl neu
     * @param fps   Die Anzahl an Frames pro Sekunde, die berechnet werden sollen.
     * @see #setFPS(float)
     */
    public void setFPS(float fps) {
        if(fps > 100) {
            Logger.error("Die FPS Zahl darf nicht größer als 100 sein.");
            return;
        }
        maxmillis = (int) (1000 / fps);
    }

    /**
     * Gibt die <i>ungefähre</i> FPS-Zahl dieser Frame-Logik aus.
     * @return  Die <i>ungefähre</i> Framerate dieser Frame-Logik in Frames/Sekunde.
     */
    public float getFPS() {
        return 1000f / ((float) maxmillis);
    }

    private final WorldThread worldThread;

    /**
     * Konstruktor erstellt den Thread, aber <b>startet ihn nicht</b>.
     */
    public FrameLogic() {
        super("Frame Master Thread #" + threadcnt++); //<- eigener Name (f. Multi-Window)
        this.setDaemon(true); // Daemon setzen
    }

    /**
     * Innerhalb dieser Run-Methode läuft die Frame-Logik.
     */
    @Override
    public void run() {
        long deltaT = maxmillis; // Das tatsächliche DeltaT aus dem letzten Frame-Schritt (zu Beginn der Idealfall)
        while(!interrupted()) {
            long tStart = System.currentTimeMillis();

            //Eigentliche Arbeit: Möglichst hoch parallelisiert



            //ENDE der eigentlichen Arbeit

            long tEnd = System.currentTimeMillis();
            deltaT = tEnd - tStart;

            //ggf. warten:
            if (deltaT < maxmillis) {
                try {
                    Thread.sleep(maxmillis-deltaT);
                } catch (InterruptedException e) {}
            }
        }
    }

}
