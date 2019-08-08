package ea.edu;

import ea.internal.annotations.API;

@API
public class Text extends EduActor<ea.actor.Text> {

    @API
    public Text(String content) {
        super(new ea.actor.Text(content));
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
    public void setzeGroesse(int groesse) {
        getActor().setSize(groesse);
    }
}
