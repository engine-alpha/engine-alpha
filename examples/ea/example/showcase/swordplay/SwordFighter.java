package ea.example.showcase.swordplay;

import ea.FrameUpdateListener;
import ea.Game;
import ea.Scene;
import ea.Vector;
import ea.actor.Actor;
import ea.actor.Animation;
import ea.actor.StatefulAnimation;
import ea.collision.CollisionEvent;
import ea.collision.CollisionListener;
import ea.handle.Physics;
import ea.keyboard.Key;
import ea.keyboard.KeyListener;

public class SwordFighter
extends StatefulAnimation
implements KeyListener, FrameUpdateListener, CollisionListener<Actor> {

    private final Vector MOVE_MID = new Vector(0.005f,0);

    private int horizontalMoveLevel = 0;
    private final int MAX_MOVE_LEVEL = 500;

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
        setStateTransition("jump", "fall");

        parent.add(this);

        physics.setGravity(new Vector(0, 9.81f));

        physics.setType(Physics.Type.DYNAMIC);
        physics.setMass(75);
        physics.setElasticity(0);


    }

    /**
     * Interne Utility-Methode. Durch die einheitliche Naming-Convention im Ordner lassen sich die Statenamen als
     * Präfix in den Dateien wiederfinden. So lässt sich das Einladen vom Code her einfach umsetzen.
     * @param stateName Der State-Name. Korrespondiert mit dem Naming in den Dateien.
     */
    private void addQualifiedState(String stateName) {
        //Alle Bilder teilen den Anfang des Pfades
        final String directory="game-assets\\sword\\char";
        //Standard-Prefix
        final String sPrefix="adventurer-";
        addState(stateName, Animation.createFromImagesPrefix(75, directory, sPrefix+stateName));
    }

    private void attack1() {
        setState("attack1");
    }

    private void attack2() {
        setState("attack2");
    }

    private void attack3() {
        setState("attack3");
    }

    private void jump() {
        if(physics.testStanding()) {
            physics.applyImpulse(new Vector(0, -500));
            setState("jump");
        }
    }

    private boolean isAttacking() {
        return getCurrentState().startsWith("attack1");
    }

    @Override
    public void onKeyDown(int key) {
        switch(key) {
            case Key.SPACE: //Sprung
                jump();
                break;
            case Key.J: //Attack 1
                attack1();
                break;
            case Key.K: //Attack 2
                attack2();
                break;
            case Key.L: //Attack 3
                attack3();
                break;
        }
    }

    @Override
    public void onKeyUp(int code) {

    }



    @Override
    public void onFrameUpdate(int frameDuration) {
        //Richtung setzen
        if(physics.getVelocity().getRealX()<0)
            this.setFlipHorizontal(true);
        else
            this.setFlipHorizontal(false);

        //Bewegung?
        if(Game.isKeyPressed(Key.A)) {
            //Movement left
            physics.setVelocity(MOVE_MID.getNegatedVektor());
            if(horizontalMoveLevel>=-MAX_MOVE_LEVEL)
                horizontalMoveLevel-=frameDuration;
        } else if(Game.isKeyPressed(Key.D)) {
            //Movement right
            physics.setVelocity(MOVE_MID);
            if(horizontalMoveLevel<=MAX_MOVE_LEVEL)
                horizontalMoveLevel+=frameDuration;
        } else {
            if(Math.abs(horizontalMoveLevel) < frameDuration/2) horizontalMoveLevel=0;
            else horizontalMoveLevel=horizontalMoveLevel + ((frameDuration/2)* (isFlipHorizontal() ? -1 : 1));
        }
        //physics.setVelocity(new Vector(MOVE_MID.multiply(horizontalMoveLevel).x, physics.getVelocity().y));

        //Standing-> Walking?
        if(Math.abs(physics.getVelocity().getRealX())>0.05f && getCurrentState().equals("idle")) setState("run");
    }

    @Override
    public void onCollision(CollisionEvent<Actor> collisionEvent) {
        //Collision -> Bin ich gelandet?
        if(getCurrentState().equals("fall") && physics.testStanding()) {
            //Ich bin gelandet
            setState("stand");
        }
    }
}
