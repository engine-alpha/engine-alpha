package ea.edu;

import ea.Vector;
import ea.actor.Polygon;
import ea.internal.annotations.API;

@API
public class Dreieck extends Geometrie<Polygon> {
    @API
    public Dreieck(double ax, double ay, double bx, double by, double cx, double cy) {
        super(new Polygon(new Vector(ax, ay), new Vector(bx, by), new Vector(cx, cy)));
    }
}
