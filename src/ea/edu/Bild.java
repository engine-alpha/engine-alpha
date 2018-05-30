package ea.edu;

import ea.actor.Actor;
import ea.actor.Image;

public class Bild
extends Image
implements EduActor {

    /**
     * Der Konstruktor lädt das Image und erlaubt die Nutung von Spritesheets.
     *
     * @param filepath Der Verzeichnispfad des Bildes, das geladen werden soll.
     */
    public Bild(String filepath) {
        super(filepath);
        eduSetup();
    }

    @Override
    public Actor getActor() {
        return this;
    }
}
