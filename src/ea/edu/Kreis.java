package ea.edu;

import ea.actor.Circle;
import ea.internal.annotations.API;

/**
 * Ein einfacher Kreis.
 * <p>
 * EDU-Variante von {@link Circle}.
 *
 * @author Michael Andonie
 */
@API
public class Kreis extends Geometrie<Circle> {
    /**
     * Konstruktor erstellt einen neuen Kreis.
     *
     * @param radius Durchmesser des Kreises
     */
    @API
    public Kreis(double radius) {
        super(new Circle((float) radius * 2));
        setzeFarbe("gelb");
    }
}
