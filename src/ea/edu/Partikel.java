package ea.edu;

import ea.actor.Actor;
import ea.actor.Particle;

/**
 * Implementierung eines Partikeleffekts für die EDU-Version.
 *
 * @author Michael Andonie
 */
public class Partikel implements EduActor {

    private final Particle particle;

    public Partikel(float diameter, int life) {
        particle = new Particle(Spiel.getActiveScene(), diameter, life);
        eduSetup();
    }

    @Override
    public Actor getActor() {
        return particle;
    }
}
