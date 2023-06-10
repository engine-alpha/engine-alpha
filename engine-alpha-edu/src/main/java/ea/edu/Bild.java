package ea.edu;

import ea.actor.Image;
import ea.internal.annotations.API;

/**
 * Ein einfaches Bild.
 * <p>
 * Für Animationen kann {@link Figur} verwendet werden.
 * <p>
 * EDU-Variante von {@link Image}.
 */
@API
public class Bild extends EduActor<Image> {
    /**
     * Der Konstruktor lädt das Bild.
     *
     * @param filepath Der Verzeichnispfad des Bildes, das geladen werden soll.
     */
    @API
    public Bild(double breite, double hoehe, String filepath) {
        super(new Image(filepath, (float) breite, (float) hoehe));
    }
}
