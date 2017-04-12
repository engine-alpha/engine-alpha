package ea.internal.frame;

import ea.internal.phy.WorldHandler;
import ea.internal.util.Logger;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

/**
 * Dieser Thread kümmert sich um die frameweise Weiterentwicklung der physikalischen Game-World.
 */
public class WorldThread extends FrameSubthread {
    /**
     * globaler WT-Counter
     */
    private static int counter = 1;

    /**
     * Die Physik-World, die alle (komplexen) physikalischen Berechnungen wrappt.
     */
    private final World world;

    /**
     * Referenz auf den internen World-Handler.
     */
    private WorldHandler worldHandler;

    /**
     * Das deltaT für die kommende Berechnung. Wird vom Parent-Thread upgedated.
     */
    private float deltaT;

    /**
     * Die Anzahl an Iterationen für die Neuberechnung der Geschwindigkeitsvektoren.
     * Empfohlen: 6.
     */
    private final int velocityIterations = 6;

    /**
     * Die Anzahl an Iterationen für die Neuberechnung der Positionsvektoren.
     * Empfohlen: 3.
     */
    private final int positionIterations = 3;

    /**
     * Setzt den DeltaT-Wert für die kommende Brechnung. Wird vom Parent-Thread aufgerufen,
     * bevor die nächste Berechnung gestartet wird.
     * @param millis    Die Zeit in Millisekunden, die voraus gerechnet werden soll.
     */
    public void setDT(long millis) {
        if(millis<=0) {
            Logger.error("Physik", "Interner Fehler: Angegebenes Zeitintervall war negativ.");
        }
        deltaT = ((float)millis) / 1000f;
    }

    /**
     * Erstellt
     */
    public WorldThread(FrameThread master, WorldHandler worldHandler) {
        super(master, "Physics-Thread #" + counter++);
        this.world = worldHandler.getWorld();
        worldHandler.setWorldThread(this);
        this.setPriority(Thread.MAX_PRIORITY);
        this.worldHandler = worldHandler;
    }

    public Body createBody(BodyDef bd) {
        synchronized (this) {
            return world.createBody(bd);
        }
    }

    /**
     * Die Run-Methode; führt einen DeltaT-Schritt aus.
     */
    @Override
    public void dispatchFrame() {
        synchronized (this) {
            world.step(deltaT, velocityIterations, positionIterations);
        }
    }
}
