package ea.example.showcase.swordplay;

import ea.Game;
import ea.Scene;
import ea.Vector;
import ea.example.showcase.ShowcaseDemo;
import ea.handle.Physics;
import ea.keyboard.Key;
import ea.keyboard.KeyListener;

/**
 * Eine kleine Spieldemo.
 * <p>
 * Vielen Dank an <a href="https://rvros.itch.io/animated-pixel-hero">rvros</a>
 */
public class Swordplay extends ShowcaseDemo implements KeyListener {

    private PlayerCharacter character;

    public Swordplay(Scene parent) {
        super(parent);
        super.setDebuggingEnabled(false);

        character = new PlayerCharacter(this);
        character.position.set(-20, 200);
        character.physics.setType(Physics.Type.DYNAMIC);
        character.physics.setRotationLocked(true);
        character.physics.setGravity(new Vector(0, -13));

        new Platform(this, 7).position.set(-450, -200);

        addKeyListener(this);
        addFrameUpdateListener(character);
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
                character.smash();
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
            case Key.T:
                character.physics.applyImpulse(new Vector(0, -2000));
                break;
            case Key.C:
                //
                break;
        }
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
}
