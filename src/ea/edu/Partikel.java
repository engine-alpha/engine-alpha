package ea.edu;

import ea.Scene;
import ea.actor.Actor;
import ea.actor.Particle;

/**
 * Implementierung eines Partikeleffekts für die EDU-Version.
 *
 * @author Michael Andonie
 */
public class Partikel extends Particle implements EduActor {

    public Partikel(Scene scene, float diameter, int life) {
        super(scene, diameter, life);
        eduSetup();
    }

    @Override
    public Actor getActor() {
        return this;
    }
}
