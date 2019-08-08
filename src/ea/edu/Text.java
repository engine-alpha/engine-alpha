package ea.edu;

import ea.internal.annotations.API;

@API
public class Text extends EduActor<ea.actor.Text> {

    @API
    public Text(String inhalt, float hoehe) {
        super(new ea.actor.Text(inhalt, hoehe));
    }

    @API
    public void setzeInhalt(String inhalt) {
        getActor().setContent(inhalt);
    }

    @API
    public void setzeFarbe(String farbe) {
        getActor().setColor(Spiel.konvertiereVonFarbname(farbe));
    }
}