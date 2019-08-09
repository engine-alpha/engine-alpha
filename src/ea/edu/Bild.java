package ea.edu;

import ea.actor.Image;
import ea.internal.annotations.API;

@API
public class Bild extends EduActor<Image> {
    /**
     * Der Konstruktor lädt das Image und erlaubt die Nutung von Spritesheets.
     *
     * @param x        X-Koordinate (linke obere Ecke des Bildes)
     * @param y        Y-Koordinate (linke obere Ecke des Bildes)
     * @param filepath Der Verzeichnispfad des Bildes, das geladen werden soll.
     */
    @API
    public Bild(double x, double y, double breite, double hoehe, String filepath) {
        super(new Image(filepath, (float) breite, (float) hoehe));

        getActor().setPosition((float) x, (float) y);
    }
}
