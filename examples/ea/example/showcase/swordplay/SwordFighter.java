package ea.example.showcase.swordplay;

import ea.Scene;
import ea.actor.Animation;
import ea.actor.StatefulAnimation;

public class SwordFighter
extends StatefulAnimation {

    public SwordFighter(Scene parent) {


        //Die Frame-Duration
        final int fd = 250;

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
        addQualifiedState("jump");

        parent.add(this);
    }

    private void addQualifiedState(String stateName) {
        //Alle Bilder teilen den Anfang des Pfades
        final String directory="game-assets\\sword";
        //Standard-Prefix
        final String sPrefix="adventurer-";
        addState(stateName, Animation.createFromImagesPrefix(75, directory, sPrefix+stateName));
    }

}
