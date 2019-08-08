package ea.edu;

import ea.actor.Geometry;
import ea.internal.annotations.API;

@API
public abstract class EduGeometrie<T extends Geometry> extends EduActor<T> {

    @API
    public EduGeometrie(T actor) {
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
    public void animiereFarbe(float dauerInSekunden, String farbe) {
        getActor().animateColor(dauerInSekunden, Spiel.konvertiereVonFarbname(farbe));
    }
}
