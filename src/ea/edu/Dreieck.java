package ea.edu;

import ea.Vector;
import ea.actor.Actor;
import ea.actor.Polygon;
import ea.internal.ano.API;

public class Dreieck implements EduGeometrie {

    private final Polygon polygon;

    @API
    public Dreieck(float ax, float ay, float bx, float by, float cx, float cy) {
        polygon = new Polygon(Spiel.getActiveScene(), new Vector(ax, ay), new Vector(bx, by), new Vector(cx, cy));

        eduSetup();
    }

    @Override
    public Actor getActor() {
        return polygon;
    }
}
