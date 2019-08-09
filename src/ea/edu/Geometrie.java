package ea.edu;

import ea.actor.Geometry;
import ea.internal.annotations.API;

import java.awt.Color;

/**
 * Oberklasse aller Geometrie-Objekte ({@link Dreieck}, {@link Kreis}, {@link Rechteck},  {@link Text}).
 */
@API
public abstract class Geometrie<Actor extends Geometry> extends EduActor<Actor> {

    @API
    public Geometrie(Actor actor) {
        super(actor);
    }

    /**
     * Setzt die Farbe des Objekts.
     *
     * @param farbe Neue Farbe des Objekts. Siehe {@link Spiel#registriereFarbe(String, Color)}
     */
    @API
    public void setzeFarbe(String farbe) {
        getActor().setColor(Spiel.konvertiereVonFarbname(farbe));
    }

    @API
    public String nenneFarbe() {
        return Spiel.konvertiereZuFarbname(getActor().getColor());
    }

    @API
    public void animiereFarbe(double dauerInSekunden, String farbe) {
        getActor().animateColor((float) dauerInSekunden, Spiel.konvertiereVonFarbname(farbe));
    }
}
