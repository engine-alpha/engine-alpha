package ea.example.showcase.dude;

import ea.FrameUpdateListener;
import ea.Game;
import ea.Random;
import ea.Vector;
import ea.actor.*;
import ea.animation.Interpolator;
import ea.animation.ValueAnimator;
import ea.animation.interpolation.LinearInteger;
import ea.animation.interpolation.SinusFloat;
import ea.collision.CollisionEvent;
import ea.collision.CollisionListener;
import ea.example.showcase.jump.Enemy;
import ea.handle.BodyType;
import ea.input.KeyListener;
import ea.sound.Sound;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashSet;

public class PlayerCharacter extends StatefulAnimation implements CollisionListener<Actor>, FrameUpdateListener, KeyListener {

    private static final float MAX_SPEED = 100;
    private static final float THRESHOLD_SPEED = 0.5f;
    public static final int JUMP_FORCE = +300;
    public static final int SMASH_FORCE = -1500;
    public static final int BOTTOM_OUT = -500 / 30;
    private static final int DOUBLE_JUMP_COST = 3;
    private static final int MANA_PICKUP_BONUS = 50;
    private static final int ROCKETCOST_PER_FRAME = 5;

    private final boolean GODMODE = false;

    private final DudeDemo master;

    private final Sound walk = new Sound("game-assets/dude/audio/footstep.wav");
    private final Sound jump = new Sound("game-assets/dude/audio/footstep.wav");
    private final Sound pickup_gold = new Sound("game-assets/dude/audio/pickup_gold.wav");

    /**
     * Guthaben.
     */
    private int money = 0;

    /**
     * Ability-Points
     */
    private int mana = 0;

    private final int MAX_MANA = 500;

    private boolean didDoubleJump = false;

    private boolean rocketMode = false;

    private final HUD hud;

    private final Collection<Platform> ignoredPlatformForCollision = new HashSet<>();

    /**
     * Beschreibt die drei Zustände, die ein Character bezüglich seiner horizontalen Bewegung haben kann.
     */
    public enum HorizontalMovement {
        LEFT, RIGHT, IDLE;

        public float getTargetXVelocity() {
            switch (this) {
                case LEFT:
                    return -MAX_SPEED;
                case RIGHT:
                    return MAX_SPEED;
                case IDLE:
                    return 0;
                default:
                    throw new IllegalStateException("Illegal enum state");
            }
        }

        public boolean opposite(float xVel) {
            if (this == LEFT) {
                return xVel < -THRESHOLD_SPEED;
            } else if (this == RIGHT) {
                return xVel > THRESHOLD_SPEED;
            } else {
                return false;
            }
        }
    }

    private HorizontalMovement horizontalMovement = HorizontalMovement.IDLE;
    private Vector smashForce = Vector.NULL;

    public PlayerCharacter(DudeDemo scene, HUD hud) {
        super(1.5f, 1.5f);
        this.hud = hud;
        this.master = scene;

        // Alle einzuladenden Dateien teilen den Großteil des Paths (Ordner sowie gemeinsame Dateipräfixe)
        String basePath = "game-assets/dude/char/spr_m_traveler_";

        addState("idle", Animation.createFromAnimatedGif(basePath + "idle_anim.gif", 1, 1));
        addState("walking", Animation.createFromAnimatedGif(basePath + "walk_anim.gif", 1, 1));
        addState("running", Animation.createFromAnimatedGif(basePath + "run_anim.gif", 1, 1));
        addState("jumpingUp", Animation.createFromAnimatedGif(basePath + "jump_1up_anim.gif", 1, 1));
        addState("midair", Animation.createFromAnimatedGif(basePath + "jump_2midair_anim.gif", 1, 1));
        addState("falling", Animation.createFromAnimatedGif(basePath + "jump_3down_anim.gif", 1, 1));
        addState("landing", Animation.createFromAnimatedGif(basePath + "jump_4land_anim.gif", 1, 1));
        addState("smashing", Animation.createFromAnimatedGif(basePath + "jump_4land_anim.gif", 1, 1));

        setStateTransition("midair", "falling");
        setStateTransition("landing", "idle");

        physics.setFriction(0.5f);
        physics.setRestitution(0);

        setMana(0);

        setShapes("C0.5,0.3,0.3&C0.5,0.6,0.3");
        /*setShapes(() -> {
            List<Shape> shapeList = new ArrayList<>(2);
            shapeList.add(ShapeBuilder.createAxisParallelRectangularShape(0.2f, 0, 0.6f, 1f));
            shapeList.add(ShapeBuilder.createCircleShape(.3f, .3f, 0.3f));
            return shapeList;
        });*/

        scene.add(this);
        physics.setMass(650000000);
        scene.addKeyListener(this);
        scene.addFrameUpdateListener(this);
        physics.setMass(65);
        addCollisionListener(this);

        Polygon polygon = new Polygon(new Vector(1, 1), new Vector(10, 10), new Vector(10, 0));
        polygon.setColor(Color.white);
        polygon.position.set(4, 4);
        polygon.setBodyType(BodyType.STATIC);
        scene.add(polygon);
    }

    private void setMana(int mana) {
        this.mana = mana;
        if (this.mana < 0) {
            this.mana = 0;
        }
        if (this.mana > MAX_MANA) {
            this.mana = MAX_MANA;
        }
        hud.setManaValue((float) mana / (float) MAX_MANA);
    }

    /**
     * Wird ausgeführt, wenn ein Sprungbefehl (W) angekommen ist.
     */
    public void tryJumping() {
        if (physics.isGrounded()) {
            physics.applyImpulse(new Vector(0, JUMP_FORCE));
            setState("jumpingUp");
        } else if (!didDoubleJump && mana >= DOUBLE_JUMP_COST && !getCurrentState().equals("smashing")) {
            //Double Jump!
            didDoubleJump = true;
            setMana(mana - DOUBLE_JUMP_COST);
            physics.setVelocity(new Vector(physics.getVelocity().x, 0));
            physics.applyImpulse(new Vector(0, JUMP_FORCE * 0.8f));
            setState("jumpingUp");
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
                money++;
                hud.setMoneyValue(money);
                pickup_gold.play();
                break;
            case ManaPickup:
                setMana(mana + MANA_PICKUP_BONUS);
                break;
        }
    }

    public void smash() {
        if (getCurrentState().equals("falling") || getCurrentState().equals("jumpingUp") || getCurrentState().equals("midair")) {
            setState("smashing");
            smashForce = new Vector(0, SMASH_FORCE);
        }
    }

    /**
     * Wird frameweise aufgerufen: Checkt den aktuellen state des Characters und macht ggf. Änderungen
     */
    @Override
    public void onFrameUpdate(int frameDuration) {
        Vector velocity = physics.getVelocity();

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

        if (rocketMode && (mana > 0 || GODMODE)) {
            setMana(mana - ROCKETCOST_PER_FRAME);
            physics.applyImpulse(new Vector(0, 10));

            Particle particle = new Particle(0.1f, 500);
            particle.position.set(position.getCenter().subtract(new Vector((float) Math.random() * 0.1f, .45f)));
            particle.setColor(Color.RED);
            particle.setLayer(-1);

            ValueAnimator<Integer> animator = new ValueAnimator<>(250, yellow -> particle.setColor(new Color(255, yellow, 0)), new LinearInteger(0, 255));
            animator.addCompletionListener((value) -> {
                getScene().removeFrameUpdateListener(animator);
                particle.removeFromScene();
            });

            getScene().addFrameUpdateListener(animator);
            getScene().add(particle);

            particle.addCollisionListener(e -> {
                if (e.getColliding() instanceof Platform) {
                    Platform platform = (Platform) e.getColliding();
                    if (ignoredPlatformForCollision.contains(platform)) {
                        e.ignoreCollision();
                    }
                }
            });

            Game.enqueue(() -> particle.physics.applyImpulse(new Vector(0.005f * -impulse + ((float) Math.random() - 0.5f), -2 * ((float) Math.random()))));
        }

        switch (getCurrentState()) {
            case "jumpingUp":
                if (velocity.y < 0) {
                    setState("midair");
                }
                break;
            case "idle":
            case "running":
            case "walking":
                //if(standing) {
                didDoubleJump = false;
                if (velocity.y > 0.1f) {
                    setState("midair");
                } else if (Math.abs(velocity.x) > 5.5f) {
                    changeState("running");
                } else if (Math.abs(velocity.x) > .1f) {
                    changeState("walking");
                } else {
                    changeState("idle");
                }
                //}
                break;
        }

        physics.applyForce(smashForce);

        if (position.getY() < BOTTOM_OUT) {
            position.set(0, 0);
            physics.setVelocity(Vector.NULL);
            setState("falling");
        }
    }

    @Override
    public void onKeyDown(KeyEvent e) {
        if (master.isPaused()) {
            //Pause --> Ignoriere Key Input
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A: //Move left
                if (horizontalMovement != PlayerCharacter.HorizontalMovement.RIGHT) {
                    //Wir bewegen uns gerade NICHT schon nach rechts-> Dann auf nach links
                    setHorizontalMovement(PlayerCharacter.HorizontalMovement.LEFT);
                }
                break;
            case KeyEvent.VK_S:
                smash();
                break;
            case KeyEvent.VK_D://Move right
                if (getHorizontalMovement() != PlayerCharacter.HorizontalMovement.LEFT) {
                    //Wir bewegen uns gerade NICHT schon nach links -> Dann auf nach rechts
                    setHorizontalMovement(PlayerCharacter.HorizontalMovement.RIGHT);
                }
                break;
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_W: //Sprungbefehl
                tryJumping();
                break;
            case KeyEvent.VK_X:
                //physics.applyImpulse(new Vector(500, 0));
                break;
            case KeyEvent.VK_T:
                //physics.applyImpulse(new Vector(0, -2000));
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
        if (master.isPaused()) {
            //Pause --> Ignoriere Key Input
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A: //Links losgelassen
                if (horizontalMovement == PlayerCharacter.HorizontalMovement.LEFT) {
                    //Wir haben uns bisher nach links bewegt und das soll jetzt aufhören
                    if (Game.isKeyPressed(KeyEvent.VK_D)) {
                        //D ist auch gedrückt, wir wollen Also ab jetzt nach Rechts
                        setHorizontalMovement(PlayerCharacter.HorizontalMovement.RIGHT);
                    } else {
                        setHorizontalMovement(PlayerCharacter.HorizontalMovement.IDLE);
                    }
                }
                break;
            case KeyEvent.VK_D: //Rechts losgelassen
                if (getHorizontalMovement() == PlayerCharacter.HorizontalMovement.RIGHT) {
                    //Wir haben uns bisher nach rechts bewegt und das soll jetzt aufhören
                    if (Game.isKeyPressed(KeyEvent.VK_A)) {
                        //A ist gedrückt, wir wollen also ab jetzt nach Links
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
        if (collisionEvent.getColliding() instanceof Enemy) {
            return;
        }

        if (collisionEvent.getColliding() instanceof Platform) {
            Platform platform = (Platform) collisionEvent.getColliding();
            if (physics.getVelocity().y > 0 || ignoredPlatformForCollision.contains(platform)) {
                ignoredPlatformForCollision.add(platform);
                collisionEvent.ignoreCollision();
            }
        }

        boolean falling = getCurrentState().equals("falling");
        boolean smashing = getCurrentState().equals("smashing");

        if ((falling || smashing) && physics.isGrounded()) {
            setState("landing");
            smashForce = Vector.NULL;

            if (smashing) {
                Vector originalOffset = getScene().getCamera().getOffset();
                Interpolator<Float> interpolator = new SinusFloat(0, -0.0004f * physics.getVelocity().y);
                ValueAnimator<Float> valueAnimator = new ValueAnimator<>(100, y -> getScene().getCamera().setOffset(originalOffset.add(new Vector(0, y))), interpolator);
                getScene().addFrameUpdateListener(valueAnimator);
                valueAnimator.addCompletionListener(value -> getScene().removeFrameUpdateListener(valueAnimator));
            }

            Vector speed = getPhysicsHandler().getVelocity();
            Vector transformedSpeed = Math.abs(speed.x) < .1f ? speed.add(100f * ((float) Math.random() - .5f), 0) : speed;

            Game.enqueue(() -> {
                for (int i = 0; i < 100; i++) {
                    Particle particle = new Particle(Random.nextFloat() * .02f + .02f, 500);
                    getScene().add(particle);
                    getScene().addFrameUpdateListener(particle);
                    particle.position.set(position.getCenter().add(0, -32));
                    particle.physics.applyImpulse(transformedSpeed.negate().multiply((float) Math.random() * 0.1f).multiplyY((float) Math.random() * 0.1f));
                    particle.setColor(Color.GRAY);
                    particle.setLayer(-1);
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
