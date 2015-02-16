package ea.internal.phy;

import ea.Raum;
import ea.internal.ano.NoExternalUse;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
     * Die Physik des Handlers. Ist zu Beginn undefiniert. Kann nur einmal gesetzt werden.
     */
    private final Physik physik;

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
    public BodyHandler(Raum raum, Physik physik, BodyDef bd, FixtureDef fixtureDef) {
        this.raum = raum;
        this.physik = physik;

        //create the body and add fixture to it
        body =  physik.getWorld().createBody(bd);
        body.createFixture(this.fixtureDef = fixtureDef);
    }
}
