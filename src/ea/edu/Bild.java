package ea.edu;

import ea.Vector;
import ea.actor.Image;
import ea.internal.annotations.API;

/**
 * Ein einfaches Bild.
 * <p>
 * Für Animationen kann {@link Figur} verwendet werden.
 */
@API
public class Bild extends EduActor<Image> {
    /**
     * Der Konstruktor lädt das Bild.
     *
     * @param x        X-Koordinate (Mitte des Bildes)
     * @param y        Y-Koordinate (Mitte des Bildes)
     * @param filepath Der Verzeichnispfad des Bildes, das geladen werden soll.
     */
    @API
    public Bild(double x, double y, double breite, double hoehe, String filepath) {
        super(new Image(filepath, (float) breite, (float) hoehe));

        getActor().setCenter(new Vector(x, y));
    }
}
