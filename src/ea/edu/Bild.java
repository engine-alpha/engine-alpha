package ea.edu;

import ea.actor.Actor;
import ea.actor.Image;

public class Bild implements EduActor {

    /**
     * Gewrapptes Image Objekt
     */
    private final Image image;

    /**
     * Der Konstruktor lädt das Image und erlaubt die Nutung von Spritesheets.
     *
     * @param x        X-Koordinate (linke obere Ecke des Bildes)
     * @param y        Y-Koordinate (linke obere Ecke des Bildes)
     * @param filepath Der Verzeichnispfad des Bildes, das geladen werden soll.
     */
    public Bild(float x, float y, String filepath) {
        image = new Image(Spiel.getActiveScene(), filepath, 1, 1); // TODO width, height
        eduSetup();
        image.position.set(x, y);
    }

    @Override
    public Actor getActor() {
        return image;
    }
}
