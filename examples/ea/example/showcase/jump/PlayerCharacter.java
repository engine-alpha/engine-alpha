package ea.example.showcase.jump;

import ea.FrameUpdateListener;
import ea.Scene;
import ea.Vector;
import ea.actor.Actor;
import ea.actor.Animation;
import ea.actor.StatefulAnimation;
import ea.collision.CollisionEvent;
import ea.collision.CollisionListener;

public class PlayerCharacter extends StatefulAnimation implements CollisionListener<Actor>, FrameUpdateListener {

    private static final float MAX_SPEED = 5000;

    /**
     * Beschreibt die drei Zustände, die ein Character bezüglich seiner horizontalen Bewegung haben kann.
     */
    public enum MovementState {
        LEFT, RIGHT, IDLE;

        public float getTargetXVelocity() {
            switch (this) {
                case LEFT:
                    return -MAX_SPEED;
                case RIGHT:
                    return MAX_SPEED;
                case IDLE:
                    return 0;
            }

            return Float.NaN;
        }
    }

    private MovementState movementState = MovementState.IDLE;

    public PlayerCharacter(Scene scene) {
        super(64, 64);

        //Load all Animations in

        //Alle einzuladenden Dateien teilen den Großteil des Paths (Ordner sowie gemeinsame Dateipräfixe)
        final String pathbase = "game-assets/dude/char/spr_m_traveler_";

        Animation idle = Animation.createFromAnimatedGif(pathbase + "idle_anim.gif", 1, 1);
        addState("idle", idle);

        addState("walking", Animation.createFromAnimatedGif(pathbase + "walk_anim.gif", 1, 1));
        addState("running", Animation.createFromAnimatedGif(pathbase + "run_anim.gif", 1, 1));
        addState("jumpingUp", Animation.createFromAnimatedGif(pathbase + "jump_1up_anim.gif", 1, 1));
        addState("midair", Animation.createFromAnimatedGif(pathbase + "jump_2midair_anim.gif", 1, 1));
        addState("falling", Animation.createFromAnimatedGif(pathbase + "jump_3down_anim.gif", 1, 1));
        addState("landing", Animation.createFromAnimatedGif(pathbase + "jump_4land_anim.gif", 1, 1));

        setStateTransition("midair", "falling");
        setStateTransition("landing", "idle");

        physics.setFriction(0);
        physics.setRestitution(0);

        scene.add(this);
        physics.setMass(65);
        addCollisionListener(this);
    }

    /**
     * Wird ausgeführt, wenn ein Sprungbefehl (Leertaste) angekommen ist.
     */
    public void tryJumping() {
        if (physics.isGrounded()) {
            // Figur steht -> Jump
            physics.applyImpulse(new Vector(0, +2000));
            setState("jumpingUp");
        }
    }

    public void setMovementState(MovementState state) {
        this.movementState = state;
    }

    public MovementState getMovementState() {
        return this.movementState;
    }

    /**
     * Wird frameweise aufgerufen: Checkt den aktuellen state des Characters und macht ggf. Änderungen
     */
    @Override
    public void onFrameUpdate(float frameDuration) {
        Vector velocity = physics.getVelocity();
        //boolean standing = physics.testStanding();

        //kümmere dich um die Horizontale Bewegung
        float desiredVelocity = movementState.getTargetXVelocity();
        float impulse = desiredVelocity - velocity.x;
        physics.applyForce(new Vector(impulse, 0));

        switch (getCurrentState()) {
            case "jumpingUp":
                if (velocity.y > 0) setState("midair");
                break;
            case "idle":
            case "running":
            case "walking":
                //if(standing) {
                if (velocity.y > 0.1f) {
                    setState("midair");
                } else if (Math.abs(velocity.x) > 550f) {
                    changeState("running");
                } else if (Math.abs(velocity.x) > 10f) {
                    changeState("walking");
                } else {
                    changeState("idle");
                }
                //}
                break;
        }

        if (velocity.x > 0) {
            setFlipHorizontal(false);
            //if(standing && !getCurrentState().equals("running")) setState("running");
        } else if (velocity.x < 0) {
            setFlipHorizontal(true);
            //if(standing && !getCurrentState().equals("running")) setState("running");
        }
    }

    @Override
    public void onCollision(CollisionEvent<Actor> collisionEvent) {
        if (collisionEvent.getColliding() instanceof Enemy) return;
        if (getCurrentState().equals("falling") && physics.isGrounded()) {
            setState("landing");
        }
    }
}
