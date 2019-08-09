package ea.edu;

import ea.actor.Geometry;
import ea.internal.annotations.API;

@API
public abstract class Geometrie<T extends Geometry> extends EduActor<T> {

    @API
    public Geometrie(T actor) {
        super(actor);
    }

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
