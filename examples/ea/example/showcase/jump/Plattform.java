package ea.example.showcase.jump;

import ea.actor.Image;

public class Plattform
extends Image {
    /**
     * Der Konstruktor lädt das Image und erlaubt die Nutung von Spritesheets.
     *
     * @param filepath Der Verzeichnispfad des Bildes, das geladen werden soll.
     */
    public Plattform(String filepath) {
        super(filepath);
    }
}
