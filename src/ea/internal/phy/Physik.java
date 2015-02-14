package ea.internal.phy;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

/**
 * Die Physik-Klasse ist die (nicht objektgebundene) Middleware zwischen der JBox2D Engine und der EA.
 * Sie ist verantwortlich für:
 * <ul>
 *     <li>Den globalen "World"-Parameter aus der JBox2D Engine.</li>
 *     <li>Translation zwischen JB2D-Vektoren (SI-Basiseinheiten) und denen der Engine (Zeichengrößen)</li>
 * </ul>
 * Created by andonie on 14.02.15.
 */
public class Physik {

    /**
     * Die World dieser Physik. Hierin laufen globale Einstellungen (z.B. Schwerkraft) ein.
     */
    private final World world;

    public Physik() {
        this.world = new World(new Vec2(0f, -9.81f)); //Erstelle standard-World mit Standard-Gravitation.
    }

    /**
     * Gibt den World-Parameter der Physik aus.
     * @return  Der JB2D-World-Parameter der Welt.
     */
    public World getWorld() {
        return world;
    }
}
