package ea.edu;

import ea.actor.Rectangle;
import ea.internal.annotations.API;

@API
public class Rechteck extends Geometrie<Rectangle> {

    @API
    public Rechteck(double breite, double hoehe) {
        super(new Rectangle((float) breite, (float) hoehe));
    }
}
