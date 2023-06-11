package ea.edu;

import ea.actor.DistanceJoint;

public class StabVerbindung extends Verbindung<DistanceJoint> {

    /**
     * Erzeugt eine neue Stabverbindung.
     *
     * @param joint Internes Box2D-Objekt
     * @hidden
     */
    StabVerbindung(DistanceJoint joint) {
        super(joint);
    }
}
