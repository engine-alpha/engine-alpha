package ea.edu;

import ea.Vector;
import ea.actor.Actor;
import ea.actor.Polygon;
import ea.internal.ano.API;

public class Dreieck extends Polygon implements EduGeometrie {

    @API
    public Dreieck(float ax, float ay, float bx, float by, float cx, float cy) {
        super(Spiel.getActiveScene(), new Vector(ax, ay), new Vector(bx, by), new Vector(cx, cy));

        eduSetup();
    }

    @Override
    public Actor getActor() {
        return this;
    }
}
