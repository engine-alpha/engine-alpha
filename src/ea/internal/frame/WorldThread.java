package ea.internal.frame;

import ea.internal.phy.WorldHandler;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

/**
 * Dieser Thread kümmert sich um die frameweise Weiterentwicklung der physikalischen Game-World.
 * Created by andonie on 14.02.15.
 */
public class WorldThread
extends FrameSubthread {

    /**
     * globaler WT-Counter
     */
    private static int wtcnt = 1;

    /**
     * Die Physik-World, die alle (komplexen) physikalischen Berechnungen wrappt.
     */
    private final World world;

    /**
     * Das deltaT für die kommende Berechnung. Wird vom Parent-Thread upgedated.
     */
    private float deltaT;

    /**
     * Die Anzahl an Iterationen für die Neuberechnung der Geschwindigkeitsvektoren.
     * Empfohlen: 6.
     */
    private int velocityIterations = 6;

    /**
     * Die Anzahl an Iterationen für die Neuberechnung der Positionsvektoren.
     * Empfohlen: 3.
     */
    private int positionIterations = 3;

    /**
     * Setzt den DeltaT-Wert für die kommende Brechnung. Wird vom Parent-Thread aufgerufen,
     * bevor die nächste Berechnung gestartet wird.
     * @param millis    Die Zeit in Millisekunden, die voraus gerechnet werden soll.
     */
    public void setDT(long millis) {
        deltaT = ((float)millis) / 1000f;
    }

    /**
     * Ein Lock, der sicherstellt, dass nur ein Thread auf einmal die World beansprucht.
     */
    private Object worldLock = new Object();

    /**
     * Erstellt
     * @param world
     */
    public WorldThread(FrameThread master, WorldHandler worldHandler) {
        super(master, "Physics-Thread #" + wtcnt++);
        this.world = worldHandler.getWorld();
        worldHandler.setWorldThread(this);
        this.setPriority(Thread.MAX_PRIORITY);
    }

    public Body createBody(BodyDef bd) {
        synchronized (worldLock) {
            return world.createBody(bd);
        }
    }

    /**
     * Die Run-Methode; führt einen DeltaT-Schritt aus.
     */
    @Override
    public void frameLogic() {
        synchronized (worldLock) {
            world.step(deltaT, velocityIterations, positionIterations);
        }
    }
}
