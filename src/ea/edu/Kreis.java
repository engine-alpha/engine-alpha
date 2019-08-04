package ea.edu;

import ea.actor.Actor;
import ea.actor.Circle;

/**
 * EDU-Variante von {@link Circle}.
 *
 * @author Michael Andonie
 */
public class Kreis implements EduGeometrie {

    private final Circle circle;

    /**
     * Konstruktor. Erstellt einen Kreis.
     *
     * @param radius Durchmesser des Kreises
     */
    public Kreis(float radius) {
        circle = new Circle(Spiel.getActiveScene(), radius * 2);
        eduSetup();
    }

    @Override
    public Actor getActor() {
        return circle;
    }
}
