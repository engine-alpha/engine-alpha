package ea.edu;

import ea.internal.annotations.API;

@API
public class Text extends EduGeometrie<ea.actor.Text> {

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

    @API
    public void setzeHoehe(float height) {
        getActor().setHeight(height);
    }
}
