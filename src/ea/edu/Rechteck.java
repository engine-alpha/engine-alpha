package ea.edu;

import ea.Scene;
import ea.actor.Actor;
import ea.actor.Rectangle;

public class Rechteck extends Rectangle implements EduGeometrie {

    public Rechteck(Scene scene, float breite, float hoehe) {
        super(scene, breite, hoehe);
        eduSetup();
    }

    @Override
    public Actor getActor() {
        return this;
    }
}
