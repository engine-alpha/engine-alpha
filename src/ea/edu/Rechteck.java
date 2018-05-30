package ea.edu;

import ea.actor.Actor;
import ea.actor.Rectangle;

public class Rechteck
extends Rectangle
implements EduGeometrie {

    public Rechteck(float breite, float hoehe) {
        super(breite, hoehe);
        eduSetup();
    }

    @Override
    public Actor getActor() {
        return this;
    }
}
