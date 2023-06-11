package ea.edu;

import ea.actor.RopeJoint;
import ea.internal.annotations.Internal;

public class SeilVerbindung extends Verbindung<RopeJoint> {

    /**
     * Erstellt eine neue Seilverbindung.
     *
     * @param joint Internes Box2D-Objekt
     * @hidden
     */
    @Internal
    SeilVerbindung(RopeJoint joint) {
        super(joint);
    }
}
