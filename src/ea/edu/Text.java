package ea.edu;

import ea.internal.annotations.API;

/**
 * Ein einfacher Text.
 * <p>
 * EDU-Variante von {@link ea.actor.Text}.
 */
@API
public class Text extends Geometrie<ea.actor.Text> {

    /**
     * Erstellt ein neues Text-Objekt.
     *
     * @param inhalt Anzuzeigender Text
     * @param hoehe  Höhe des Textes in Metern
     */
    @API
    public Text(String inhalt, double hoehe) {
        super(new ea.actor.Text(inhalt, (float) hoehe));
        setzeFarbe("weiss");
    }

    /**
     * Setzt den Inhalt neu.
     *
     * @param inhalt Neuer Inhalt
     */
    @API
    public void setzeInhalt(String inhalt) {
        getActor().setContent(inhalt);
    }

    /**
     * Setzt die Höhe neu.
     *
     * @param hoehe Neue Höhe
     */
    @API
    public void setzeHoehe(double hoehe) {
        getActor().setHeight((float) hoehe);
    }
}
