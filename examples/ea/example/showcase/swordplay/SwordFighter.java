package ea.example.showcase.swordplay;

import ea.Scene;
import ea.actor.Animation;
import ea.actor.StatefulAnimation;

public class SwordFighter
extends StatefulAnimation {

    public SwordFighter(Scene parent) {

        //Alle Bilder teilen den Anfang des Pfades
        final String path="game-assets\\sword\\adventurer-";
        //Die Frame-Duration
        final int fd = 250;

        addState("attack1", Animation.createFromImages(fd, path+"attack1"));
    }

}
