package ea.edu;

import ea.actor.Rectangle;
import ea.internal.annotations.API;

/**
 * Ein einfaches Rechteck.
 * <p>
 * EDU-Variante von {@link Rectangle}.
 */
@API
public class Rechteck extends Geometrie<Rectangle> {

    /**
     * Erstellt ein neues Rechteck.
     *
     * @param breite Breite in Metern
     * @param hoehe  Höhe in Metern
     */
    @API
    public Rechteck(double breite, double hoehe) {
        super(new Rectangle((float) breite, (float) hoehe));
        setzeFarbe("blau");
    }
}
