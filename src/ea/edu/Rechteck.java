package ea.edu;

import ea.actor.Rectangle;
import ea.internal.annotations.API;

@API
public class Rechteck extends EduGeometrie<Rectangle> {

    @API
    public Rechteck(float breite, float hoehe) {
        super(new Rectangle(breite, hoehe));
    }
}
