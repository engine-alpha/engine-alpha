package ea.edu;

import ea.actor.Actor;
import ea.actor.Text;

public class EduText extends Text implements EduActor {

    public EduText(String content) {
        super(content);

        eduSetup();
    }

    public void setzeInhalt(String inhalt) {
        super.setContent(inhalt);
    }

    public void setzeFarbe(String farbe) {
        super.setColor(Spiel.stringToColor(farbe));
    }

    public void setzeGroesse(int groesse) {
        super.setSize(groesse);
    }

    @Override
    public Actor getActor() {
        return this;
    }
}
