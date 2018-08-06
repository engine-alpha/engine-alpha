package ea.edu;

import ea.Point;
import ea.Scene;
import ea.actor.Actor;
import ea.actor.Polygon;
import ea.internal.ano.API;

public class Dreieck extends Polygon implements EduGeometrie {

    @API
    public Dreieck(float ax, float ay, float bx, float by, float cx, float cy) {
        super(Spiel.getActiveScene(), new Point(ax, ay), new Point(bx, by), new Point(cx, cy));

        eduSetup();
    }

    @Override
    public Actor getActor() {
        return this;
    }
}
