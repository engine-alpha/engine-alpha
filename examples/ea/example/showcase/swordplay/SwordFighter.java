package ea.example.showcase.swordplay;

import ea.Scene;
import ea.actor.Animation;
import ea.actor.StatefulAnimation;
import ea.keyboard.Key;
import ea.keyboard.KeyListener;

public class SwordFighter
extends StatefulAnimation
implements KeyListener {

    public SwordFighter(Scene parent) {

        //Erstellen aller States
        addQualifiedState("attack1");
        addQualifiedState("attack2");
        addQualifiedState("attack3");
        addQualifiedState("idle");
        addQualifiedState("hurt");
        addQualifiedState("jump");
        addQualifiedState("ladder-climb");
        addQualifiedState("run");
        addQualifiedState("slide");
        addQualifiedState("smrslt");
        addQualifiedState("stand");
        addQualifiedState("swim");
        addQualifiedState("fall");
        addQualifiedState("die");

        //Erstellen der automatischen State-Transitions
        setStateTransition("attack1", "idle");
        setStateTransition("attack2", "idle");
        setStateTransition("attack3", "idle");

        parent.add(this);
    }

    /**
     * Interne Utility-Methode. Durch die einheitliche Naming-Convention im Ordner lassen sich die Statenamen als
     * Präfix in den Dateien wiederfinden. So lässt sich das Einladen vom Code her einfach umsetzen.
     * @param stateName Der State-Name. Korrespondiert mit dem Naming in den Dateien.
     */
    private void addQualifiedState(String stateName) {
        //Alle Bilder teilen den Anfang des Pfades
        final String directory="game-assets\\sword";
        //Standard-Prefix
        final String sPrefix="adventurer-";
        addState(stateName, Animation.createFromImagesPrefix(75, directory, sPrefix+stateName));
    }

    private void attack() {

    }


    @Override
    public void onKeyDown(int key) {
        switch(key) {
            case Key.SPACE: //Sprung
                //
                break;
            case Key.J: //Attack 1
                attack();
                break;
        }
    }

    @Override
    public void onKeyUp(int code) {

    }
}
