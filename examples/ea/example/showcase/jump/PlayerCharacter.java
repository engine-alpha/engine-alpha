package ea.example.showcase.jump;

import ea.Vector;
import ea.actor.Animation;
import ea.actor.StatefulAnimation;

public class PlayerCharacter
extends StatefulAnimation {

    public PlayerCharacter() {
        //Load all Animations in

        //Alle einzuladenden Dateien teilen den Großteil des Paths (Ordner sowie gemeinsame Dateipräfixe)
        final String pathbase = "game-assets\\jump\\spr_m_traveler_";


        Animation idle = Animation.createFromAnimatedGif(pathbase+"idle_anim.gif");
        addState(idle, "idle");

        addState(Animation.createFromAnimatedGif(pathbase+"walk_anim.gif"), "walking");
        addState(Animation.createFromAnimatedGif(pathbase+"run_anim.gif"), "running");
        addState(Animation.createFromAnimatedGif(pathbase+"jump_1up_anim.gif"), "jumpingUp");
        addState(Animation.createFromAnimatedGif(pathbase+"jump_2midair_anim.gif"), "midair");
        addState(Animation.createFromAnimatedGif(pathbase+"jump_3down_anim.gif"), "falling");

        setStateTransition("midair", "falling");

        physics.setFriction(0);
        physics.setElasticity(0);
    }

    /**
     * Wird ausgeführt, wenn ein Sprungbefehl (Leertaste) angekommen ist.
     */
    public void tryJumping() {
        if(physics.testStanding()) {
            //Figur steht -> Jump
            physics.applyImpulse(new Vector(0, -1500));
        }
    }

    /**
     * Wird frameweise aufgerufen: Checkt den aktuellen state des Characters und macht ggf. Änderungen
     */
    public void framewiseUpdate(int frameDuration) {

    }
}
