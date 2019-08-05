package ea.edu;

import ea.actor.Actor;
import ea.actor.Rectangle;

public class Rechteck implements EduGeometrie {

    private final Rectangle rectangle;

    public Rechteck(float breite, float hoehe) {
        rectangle = new Rectangle(breite, hoehe);
        eduSetup();
    }

    @Override
    public Actor getActor() {
        return rectangle;
    }
}
