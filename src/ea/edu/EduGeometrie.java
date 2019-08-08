package ea.edu;

import ea.actor.Geometry;

public interface EduGeometrie
extends EduActor {

    default void setzeFarbe(String farbe) {
        ((Geometry)getActor()).setColor(Spiel.konvertiereVonFarbname(farbe));
    }

    default String nenneFarbe() {
        return Spiel.konvertiereZuFarbname(((Geometry)getActor()).getColor());
    }
}
