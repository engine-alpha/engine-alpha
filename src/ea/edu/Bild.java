package ea.edu;

import ea.Scene;
import ea.actor.Actor;
import ea.actor.Image;

public class Bild extends Image implements EduActor {

    /**
     * Der Konstruktor lädt das Image und erlaubt die Nutung von Spritesheets.
     *
     * @param x         X-Koordinate (linke obere Ecke des Bildes)
     * @param y         Y-Koordinate (linke obere Ecke des Bildes)
     * @param filepath Der Verzeichnispfad des Bildes, das geladen werden soll.
     */
    public Bild(float x, float y, String filepath) {
        super(Spiel.getActiveScene(), filepath);
        eduSetup();
        position.set(x,y);
    }

    @Override
    public Actor getActor() {
        return this;
    }
}
