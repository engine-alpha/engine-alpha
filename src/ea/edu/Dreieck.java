package ea.edu;

import ea.Vector;
import ea.actor.Polygon;
import ea.internal.annotations.API;

@API
public class Dreieck extends EduGeometrie<Polygon> {
    @API
    public Dreieck(float ax, float ay, float bx, float by, float cx, float cy) {
        super(new Polygon(new Vector(ax, ay), new Vector(bx, by), new Vector(cx, cy)));
    }
}
