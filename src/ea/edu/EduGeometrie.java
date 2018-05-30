package ea.edu;

import ea.actor.Geometry;

public interface EduGeometrie
extends EduActor {

    default void setzeFarbe(String farbe) {
        ((Geometry)getActor()).setColor(Spiel.stringToColor(farbe));
    }

    default String nenneFarbe() {
        return Spiel.colorToString(((Geometry)getActor()).getColor());
    }
}
