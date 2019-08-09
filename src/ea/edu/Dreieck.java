package ea.edu;

import ea.Vector;
import ea.actor.Polygon;
import ea.internal.annotations.API;

/**
 * Ein einfaches Dreieck.
 */
@API
public class Dreieck extends Geometrie<Polygon> {
    /**
     * Der Konstruktor erstellt ein neues Dreieck.
     *
     * @param x1 X-Koordinate des ersten Eckpunkts
     * @param y1 Y-Koordinate des ersten Eckpunkts
     * @param x2 X-Koordinate des zweiten Eckpunkts
     * @param y2 Y-Koordinate des zweiten Eckpunkts
     * @param x3 X-Koordinate des dritten Eckpunkts
     * @param y3 Y-Koordinate des dritten Eckpunkts
     */
    @API
    public Dreieck(double x1, double y1, double x2, double y2, double x3, double y3) {
        super(new Polygon(new Vector(x1, y1), new Vector(x2, y2), new Vector(x3, y3)));
    }
}
