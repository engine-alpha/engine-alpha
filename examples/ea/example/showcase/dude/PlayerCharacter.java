package ea.example.showcase.dude;

import ea.FrameUpdateListener;
import ea.Game;
import ea.Random;
import ea.Vector;
import ea.actor.Actor;
import ea.actor.Animation;
import ea.actor.Particle;
import ea.actor.StatefulAnimation;
import ea.animation.Interpolator;
import ea.animation.ValueAnimator;
import ea.animation.interpolation.LinearInteger;
import ea.animation.interpolation.SinusFloat;
import ea.collision.CollisionEvent;
import ea.collision.CollisionListener;
import ea.input.KeyListener;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashSet;

public class PlayerCharacter extends StatefulAnimation<PlayerState> implements CollisionListener<Actor>, FrameUpdateListener, KeyListener {

    private static final float MAX_SPEED = 100;
    public static final int JUMP_FORCE = +150;
    public static final int SMASH_FORCE = -1500;
    public static final int BOTTOM_OUT = -500 / 30;
    private static final int DOUBLE_JUMP_COST = 3;
    private static final int MANA_PICKUP_BONUS = 50;
    private static final int ROCKETCOST_PER_FRAME = 5;
    private static final boolean GOD_MODE = true;
    private static final float FRICTION = 0.5f;
    private static final float RESTITUTION = 0;
    private static final int MASS = 65;

    /* private final Sound walk = new Sound("game-assets/dude/audio/footstep.wav");
    private final Sound jump = new Sound("game-assets/dude/audio/footstep.wav");
    private final Sound pickup_gold = new Sound("game-assets/dude/audio/pickup_gold.wav"); */

    private boolean didDoubleJump = false;

    private boolean rocketMode = false;

    private final GameData gameData;

    private final Collection<Platform> ignoredPlatformForCollision = new HashSet<>();

    /**
     * Beschreibt die drei Zustände, die ein Character bezüglich seiner horizontalen Bewegung haben kann.
     */
    private enum HorizontalMovement {
        LEFT(-MAX_SPEED), RIGHT(MAX_SPEED), IDLE(0);

        private float targetVelocityX;

        HorizontalMovement(float targetVelocityX) {
            this.targetVelocityX = targetVelocityX;
        }

        public float getTargetXVelocity() {
            return targetVelocityX;
        }
    }

    private HorizontalMovement horizontalMovement = HorizontalMovement.IDLE;
    private Vector smashForce = Vector.NULL;

    public PlayerCharacter(GameData gameData) {
        super(1, 1);

        this.gameData = gameData;

        // Alle einzuladenden Dateien teilen den Großteil des Paths (Ordner sowie gemeinsame Dateipräfixe)
        String basePath = "game-assets/dude/char/spr_m_traveler_";

        addState(PlayerState.Idle, Animation.createFromAnimatedGif(basePath + "idle_anim.gif", 1, 1));
        addState(PlayerState.Walking, Animation.createFromAnimatedGif(basePath + "walk_anim.gif", 1, 1));
        addState(PlayerState.Running, Animation.createFromAnimatedGif(basePath + "run_anim.gif", 1, 1));
        addState(PlayerState.JumpingUp, Animation.createFromAnimatedGif(basePath + "jump_1up_anim.gif", 1, 1));
        addState(PlayerState.Midair, Animation.createFromAnimatedGif(basePath + "jump_2midair_anim.gif", 1, 1));
        addState(PlayerState.Falling, Animation.createFromAnimatedGif(basePath + "jump_3down_anim.gif", 1, 1));
        addState(PlayerState.Landing, Animation.createFromAnimatedGif(basePath + "jump_4land_anim.gif", 1, 1));
        addState(PlayerState.Smashing, Animation.createFromAnimatedGif(basePath + "jump_4land_anim.gif", 1, 1));

        setStateTransition(PlayerState.Midair, PlayerState.Falling);
        setStateTransition(PlayerState.Landing, PlayerState.Idle);

        physics.setFriction(FRICTION);
        physics.setRestitution(RESTITUTION);

        setShapes("C0.5,0.3,0.3&C0.5,0.6,0.3");

        /*setShapes(() -> {
            List<Shape> shapeList = new ArrayList<>(2);
            shapeList.add(ShapeBuilder.createAxisParallelRectangularShape(0.2f, 0, 0.6f, 1f));
            shapeList.add(ShapeBuilder.createCircleShape(.3f, .3f, 0.3f));
            return shapeList;
        });*/

        physics.setMass(MASS);

        addCollisionListener(this);
    }

    /**
     * Wird ausgeführt, wenn ein Sprungbefehl (W) angekommen ist.
     */
    public void tryJumping() {
        if (physics.isGrounded()) {
            physics.applyImpulse(new Vector(0, JUMP_FORCE));
            setState(PlayerState.JumpingUp);
        } else if (!didDoubleJump && gameData.getMana() >= DOUBLE_JUMP_COST && !getCurrentState().equals("smashing")) {
            // Double Jump!
            didDoubleJump = true;
            gameData.consumeMana(DOUBLE_JUMP_COST);
            physics.setVelocity(new Vector(physics.getVelocity().x, 0));
            physics.applyImpulse(new Vector(0, JUMP_FORCE * 0.8f));
            setState(PlayerState.JumpingUp);
        }
    }

    public void setHorizontalMovement(HorizontalMovement state) {
        switch (state) {
            case LEFT:
                setFlipHorizontal(true);
                break;
            case RIGHT:
                setFlipHorizontal(false);
                break;
        }

        this.horizontalMovement = state;
    }

    public HorizontalMovement getHorizontalMovement() {
        return this.horizontalMovement;
    }

    /**
     * Diese Methode wird aufgerufen, wenn der Character ein Item berührt hat.
     */
    public void gotItem(Item item) {
        switch (item) {
            case Coin:
                gameData.addMoney(1);
                break;
            case ManaPickup:
                gameData.addMana(MANA_PICKUP_BONUS);
                break;
        }
    }

    public void smash() {
        PlayerState currentState = getCurrentState();
        if (currentState == PlayerState.Falling || currentState == PlayerState.JumpingUp || currentState == PlayerState.Midair) {
            setState(PlayerState.Smashing);
            smashForce = new Vector(0, SMASH_FORCE);
        }
    }

    @Override
    public void onFrameUpdate(float deltaSeconds) {
        Vector velocity = physics.getVelocity();
        gameData.setPlayerVelocity(velocity.getLength());

        // kümmere dich um die horizontale Bewegung
        float desiredVelocity = horizontalMovement.getTargetXVelocity();
        float impulse;

        if (desiredVelocity == 0) {
            impulse = 0;
            physics.setVelocity(new Vector(velocity.x * 0.95f, velocity.y));
        } else {
            impulse = (desiredVelocity - velocity.x) * 4;
            physics.applyForce(new Vector(impulse, 0));
        }

        if (rocketMode && (gameData.getMana() > 0 || GOD_MODE)) {
            gameData.consumeMana(ROCKETCOST_PER_FRAME);
            physics.applyImpulse(new Vector(0, 5));

            Particle particle = new Particle(0.1f, .5f);
            particle.position.set(position.getCenter().subtract(new Vector((float) Math.random() * 0.1f, .45f)));
            particle.setColor(Color.RED);
            particle.setLayerPosition(-1);
            particle.getFrameUpdateListeners().add(new ValueAnimator<>(.25f, value -> particle.setColor(new Color(255, value, 0)), new LinearInteger(0, 255)));
            particle.addMountListener(e -> particle.physics.applyImpulse(new Vector(0.005f * -impulse + ((float) Math.random() - 0.5f), -2 * ((float) Math.random()))));
            particle.addCollisionListener((e) -> {
                if (e.getColliding() instanceof Platform) {
                    Platform platform = (Platform) e.getColliding();
                    if (ignoredPlatformForCollision.contains(platform)) {
                        e.ignoreCollision();
                    }
                }
            });

            getLayer().add(particle);
        }

        switch (getCurrentState()) {
            case JumpingUp:
                if (velocity.y < 0) {
                    setState(PlayerState.Midair);
                }
                break;
            case Idle:
            case Running:
            case Walking:
                //if(standing) {
                didDoubleJump = false;
                if (velocity.y > 0.1f) {
                    setState(PlayerState.Midair);
                } else if (Math.abs(velocity.x) > 5.5f) {
                    changeState(PlayerState.Running);
                } else if (Math.abs(velocity.x) > .1f) {
                    changeState(PlayerState.Walking);
                } else {
                    changeState(PlayerState.Idle);
                }
                //}
                break;
        }

        physics.applyForce(smashForce);

        if (position.getY() < BOTTOM_OUT) {
            position.set(0, 0);
            physics.setVelocity(Vector.NULL);
            setState(PlayerState.Falling);
        }
    }

    @Override
    public void onKeyDown(KeyEvent e) {
        /* if (getLayer().getParent().isPhysicsPaused()) {
            //Pause --> Ignoriere Key Input
            return;
        } */ // TODO Pause handling

        switch (e.getKeyCode()) {
            case KeyEvent.VK_A: // Move left
                if (horizontalMovement != PlayerCharacter.HorizontalMovement.RIGHT) {
                    //Wir bewegen uns gerade NICHT schon nach rechts-> Dann auf nach links
                    setHorizontalMovement(PlayerCharacter.HorizontalMovement.LEFT);
                }
                break;
            case KeyEvent.VK_S:
                smash();
                break;
            case KeyEvent.VK_D:// Move right
                if (getHorizontalMovement() != PlayerCharacter.HorizontalMovement.LEFT) {
                    //Wir bewegen uns gerade NICHT schon nach links -> Dann auf nach rechts
                    setHorizontalMovement(PlayerCharacter.HorizontalMovement.RIGHT);
                }
                break;
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_W: // Sprungbefehl
                tryJumping();
                break;
            case KeyEvent.VK_C:
                physics.setVelocity(physics.getVelocity());
                break;
            case KeyEvent.VK_SHIFT:
                rocketMode = true;
                break;
        }
    }

    @Override
    public void onKeyUp(KeyEvent e) {
        // TODO Add pause handling

        switch (e.getKeyCode()) {
            case KeyEvent.VK_A: // Links losgelassen
                if (horizontalMovement == PlayerCharacter.HorizontalMovement.LEFT) {
                    // Wir haben uns bisher nach links bewegt und das soll jetzt aufhören
                    if (Game.isKeyPressed(KeyEvent.VK_D)) {
                        // D ist auch gedrückt, wir wollen Also ab jetzt nach Rechts
                        setHorizontalMovement(PlayerCharacter.HorizontalMovement.RIGHT);
                    } else {
                        setHorizontalMovement(PlayerCharacter.HorizontalMovement.IDLE);
                    }
                }
                break;
            case KeyEvent.VK_D: // Rechts losgelassen
                if (getHorizontalMovement() == PlayerCharacter.HorizontalMovement.RIGHT) {
                    // Wir haben uns bisher nach rechts bewegt und das soll jetzt aufhören
                    if (Game.isKeyPressed(KeyEvent.VK_A)) {
                        // A ist gedrückt, wir wollen also ab jetzt nach Links
                        setHorizontalMovement(PlayerCharacter.HorizontalMovement.LEFT);
                    } else {
                        setHorizontalMovement(PlayerCharacter.HorizontalMovement.IDLE);
                    }
                }
                break;
            case KeyEvent.VK_SHIFT:
                rocketMode = false;
                break;
        }
    }

    @Override
    public void onCollision(CollisionEvent<Actor> collisionEvent) {
        if (collisionEvent.getColliding() instanceof Platform) {
            Platform platform = (Platform) collisionEvent.getColliding();
            if (physics.getVelocity().y > 0 || ignoredPlatformForCollision.contains(platform)) {
                ignoredPlatformForCollision.add(platform);
                collisionEvent.ignoreCollision();
            }
        }

        boolean falling = getCurrentState() == PlayerState.Falling;
        boolean smashing = getCurrentState() == PlayerState.Smashing;

        if ((falling || smashing) && physics.isGrounded()) {
            setState(PlayerState.Landing);
            smashForce = Vector.NULL;

            if (smashing) {
                Vector originalOffset = getPhysicsHandler().getWorldHandler().getLayer().getParent().getCamera().getOffset();
                Interpolator<Float> interpolator = new SinusFloat(0, -0.0004f * physics.getVelocity().y);
                ValueAnimator<Float> valueAnimator = new ValueAnimator<>(.1f, y -> getLayer().getParent().getCamera().setOffset(originalOffset.add(new Vector(0, y))), interpolator);
                getLayer().getFrameUpdateListeners().add(valueAnimator);
                valueAnimator.addCompletionListener(value -> getLayer().getFrameUpdateListeners().remove(valueAnimator));
            }

            Vector speed = getPhysicsHandler().getVelocity();
            Vector transformedSpeed = Math.abs(speed.x) < .1f ? speed.add(100f * ((float) Math.random() - .5f), 0) : speed;

            Game.enqueue(() -> {
                for (int i = 0; i < 100; i++) {
                    Particle particle = new Particle(Random.nextFloat() * .02f + .02f, .5f);
                    particle.position.set(position.getCenter().add(0, -32));
                    particle.addMountListener(e -> particle.physics.applyImpulse(transformedSpeed.negate().multiply((float) Math.random() * 0.1f).multiplyY((float) Math.random() * 0.1f)));
                    particle.setColor(Color.GRAY);
                    particle.setLayerPosition(-1);

                    getLayer().add(particle);
                }
            });
        }
    }

    @Override
    public void onCollisionEnd(CollisionEvent<Actor> collisionEvent) {
        if (collisionEvent.getColliding() instanceof Platform) {
            Platform platform = (Platform) collisionEvent.getColliding();
            ignoredPlatformForCollision.remove(platform);
        }
    }
}
