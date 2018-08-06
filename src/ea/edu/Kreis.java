package ea.edu;

import ea.Scene;
import ea.actor.Actor;
import ea.actor.Circle;

/**
 * EDU-Variante von {@link Circle}.
 * @author Michael Andonie
 */
public class Kreis extends Circle implements EduGeometrie {

    /**
     * Konstruktor. Erstellt einen Kreis.
     *
     * @param radius Durchmesser des Kreises
     */
    public Kreis(Scene scene, float radius) {
        super(scene, radius*2);
        eduSetup();
    }

    @Override
    public Actor getActor() {
        return this;
    }
}
