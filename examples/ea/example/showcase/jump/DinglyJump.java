package ea.example.showcase.jump;

import ea.*;
import ea.actor.Circle;
import ea.actor.Rectangle;
import ea.animation.ValueAnimator;
import ea.animation.interpolation.ReverseEaseFloat;
import ea.example.showcase.ShowcaseDemo;
import ea.handle.Physics;
import ea.keyboard.Key;
import ea.keyboard.KeyListener;

import java.awt.*;

/**
 * Ein einfaches Demo-Spiel zur Demonstration der Engine 4.0: Ein <i>Doodle Jump</i>-Klon. <br /> Tausend Dank an <a
 * href="http://openpixelproject.com">Open Pixel Project</a> für die Bereitstellung kostenfreier Assets für diese Demo!
 */
public class DinglyJump extends ShowcaseDemo implements KeyListener, FrameUpdateListener {

    PlayerCharacter character;
    private boolean hasGravity = false;

    public DinglyJump(Scene parent) {
        super(parent);

        character = new PlayerCharacter(this);
        character.physics.setType(Physics.Type.DYNAMIC);
        character.physics.setRotationLocked(true);

        toggleGravity();

        Rectangle platform = new Rectangle(5000, 10);
        platform.setColor(Color.WHITE);
        platform.position.set(-2500, -300);
        add(platform);
        platform.physics.setType(Physics.Type.STATIC);
        platform.physics.setElasticity(0);
        platform.getPhysicsHandler().getBody().m_userData = "Ground";

        getCamera().setFocus(character);

        addKeyListener(this);
        addFrameUpdateListener(this);

        setDebuggingEnabled(false);

        Circle circleToAnimate = new Circle(250);
        add(circleToAnimate);
        circleToAnimate.setColor(Color.YELLOW);

        addFrameUpdateListener(new ValueAnimator<>(5000,
                circleToAnimate.position::setY,
                new ReverseEaseFloat(500, 1000), ValueAnimator.Mode.REPEATED));

        createPlatforms();
        createRandomParcour(50);
    }

    private void createPlatforms() {
        Platform p1 = new Platform(this, character, 100, 10);
        Platform p2 = new Platform(this, character, 200, 10);
        Platform p3 = new Platform(this, character, 200, 10);
        Platform p4 = new Platform(this, character, 100, 10);

        p1.position.set(100, -200);
        p2.position.set(350, -250);
        p3.position.set(-50, 100);
        p4.position.set(500, -350);
    }

    @Override
    public void onKeyDown(int key) {
        switch (key) {
            case Key.W:
                break;
            case Key.A: //Move left
                if (character.getMovementState() != PlayerCharacter.MovementState.RIGHT) {
                    //Wir bewegen uns gerade NICHT schon nach rechts-> Dann auf nach links
                    character.setMovementState(PlayerCharacter.MovementState.LEFT);
                }
                break;
            case Key.S:
                break;
            case Key.D://Move right
                if (character.getMovementState() != PlayerCharacter.MovementState.LEFT) {
                    //Wir bewegen uns gerade NICHT schon nach links -> Dann auf nach rechts
                    character.setMovementState(PlayerCharacter.MovementState.RIGHT);
                }
                break;
            case Key.LEERTASTE: //Sprungbefehl
                character.tryJumping();
                break;
            case Key.X:
                character.physics.applyImpulse(new Vector(500, 0));
                break;
            case Key.P:
                toggleDebug();
                break;
            case Key.R:
                toggleGravity();
                break;
            case Key.T:
                character.physics.applyImpulse(new Vector(0, -2000));
                break;
            case Key.F:
                spawnEnemy();
                break;
            case Key.C:
                //
                break;
        }
    }

    private void toggleGravity() {
        character.physics.setGravity(new Vector(0, hasGravity ? 0 : -13));
        hasGravity = !hasGravity;
    }

    private void createRandomParcour(int numPlatforms) {
        for (int i = 0; i < numPlatforms; i++) {
            Platform platform = new Platform(this, character, Random.getInteger(10) * 20 + 20, 10);
            platform.position.set(i * 250 + (Random.getInteger(10) * 5), - 300 + (i * 50));
        }
    }

    private void spawnEnemy() {
        Enemy enemy = new Enemy(this, character, new Vector(-5, 0));
        enemy.position.set(0, 200);
    }

    @Override
    public void onKeyUp(int code) {
        switch (code) {
            case Key.A: //Links losgelassen
                if (character.getMovementState() == PlayerCharacter.MovementState.LEFT) {
                    //Wir haben uns bisher nach links bewegt und das soll jetzt aufhören
                    if (Game.isKeyPressed(Key.D)) {
                        //D ist auch gedrückt, wir wollen Also ab jetzt nach Rechts
                        character.setMovementState(PlayerCharacter.MovementState.RIGHT);
                    } else {
                        character.setMovementState(PlayerCharacter.MovementState.IDLE);
                    }
                }
                break;
            case Key.D: //Rechts losgelassen
                if (character.getMovementState() == PlayerCharacter.MovementState.RIGHT) {
                    //Wir haben uns bisher nach rechts bewegt und das soll jetzt aufhören
                    if (Game.isKeyPressed(Key.A)) {
                        //A ist gedrückt, wir wollen also ab jetzt nach Links
                        character.setMovementState(PlayerCharacter.MovementState.LEFT);
                    } else {
                        character.setMovementState(PlayerCharacter.MovementState.IDLE);
                    }
                }
                break;
        }
    }

    @Override
    public void onFrameUpdate(int frameDuration) {
        character.framewiseUpdate(frameDuration);
    }
}
