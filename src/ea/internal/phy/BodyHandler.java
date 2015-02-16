package ea.internal.phy;

import ea.Raum;
import ea.internal.ano.NoExternalUse;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;

/**
 * Ein <code>Body-Handler</code> kümmert sich um die <i>physikalische Darstellung</i> eines <code>Raum</code>-Objekts.<br />
 * Er übernimmt zwei wesentliche Aufgaben:
 * <ul>
 *     <li>Die Kontrolle und Steuerung innerhalb der <b>Physik-Engine</b> aus Sicht des respektiven Raum Objekts.</li>
 *     <li>Die Speicherung der <i>räumlichen Eigenschaften</i> (Position und Rotation) des respektiven Raum-Objekts.</li>
 * </ul>
 * Created by andonie on 15.02.15.
 */
public class BodyHandler {

    /**
     * Das Raum-Objekt, um das sich dieses <code>BodyHandler</code>-Objekt kümmert.
     */
    private final Raum raum;

    /**
     * Referenz auf den Handler der World, in der sich der Body befindet.
     */
    private final WorldHandler worldHandler;

    /**
     * Die Fixture Definition des Objekts.
     */
    private final FixtureDef fixtureDef;

    /**
     * Der Body als die physische Repräsentation des analogen Raum-Objekts in der Physics-Engine.
     */
    private Body body;

    /**
     * Erstellt einen neuen Body-Handler
     * @param raum
     */
    @NoExternalUse
    public BodyHandler(Raum raum, WorldHandler worldHandler, BodyDef bd, FixtureDef fixtureDef) {
        this.raum = raum;
        this.worldHandler = worldHandler;

        //create the body and add fixture to it
        body =  worldHandler.getWorld().createBody(bd);
        body.createFixture(this.fixtureDef = fixtureDef);
    }
}
