package ea.example.showcase.swordplay;

import ea.Game;
import ea.Scene;
import ea.Vector;
import ea.actor.Actor;
import ea.actor.Image;
import ea.example.showcase.ShowcaseDemo;
import ea.handle.Physics;
import ea.keyboard.Key;
import ea.keyboard.KeyListener;

import java.util.Collection;
import java.util.HashSet;

/**
 * Eine kleine Spieldemo.
 * <p>
 * Vielen Dank an <a href="https://rvros.itch.io/animated-pixel-hero">rvros</a>
 */
public class Swordplay extends ShowcaseDemo implements KeyListener {

    private PlayerCharacter character;
    private Collection<Coin> coins = new HashSet<>();

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

        getCamera().move(0, 200);

        Coin coin = new Coin();
        coins.add(coin);
        add(coin);

        coin = new Coin();
        coin.position.set(100, 0);
        coins.add(coin);
        add(coin);

        Image moon = new Image("game-assets/sword/moon.png");
        add(moon);
        moon.position.set(300, 300);

        character.addCollisionListener(e -> {
            Actor actor = e.getColliding();
            if (actor instanceof Coin) {
                coins.remove(actor);
                remove(actor);
            }
        });
    }

    @Override
    public void onKeyDown(int key) {
        switch (key) {
            case Key.A: //Move left
                if (character.getHorizontalMovement() != PlayerCharacter.HorizontalMovement.RIGHT) {
                    //Wir bewegen uns gerade NICHT schon nach rechts-> Dann auf nach links
                    character.setHorizontalMovement(PlayerCharacter.HorizontalMovement.LEFT);
                }
                break;
            case Key.S:
                character.smash();
                break;
            case Key.D://Move right
                if (character.getHorizontalMovement() != PlayerCharacter.HorizontalMovement.LEFT) {
                    //Wir bewegen uns gerade NICHT schon nach links -> Dann auf nach rechts
                    character.setHorizontalMovement(PlayerCharacter.HorizontalMovement.RIGHT);
                }
                break;
            case Key.W: //Sprungbefehl
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
                if (character.getHorizontalMovement() == PlayerCharacter.HorizontalMovement.LEFT) {
                    //Wir haben uns bisher nach links bewegt und das soll jetzt aufhören
                    if (Game.isKeyPressed(Key.D)) {
                        //D ist auch gedrückt, wir wollen Also ab jetzt nach Rechts
                        character.setHorizontalMovement(PlayerCharacter.HorizontalMovement.RIGHT);
                    } else {
                        character.setHorizontalMovement(PlayerCharacter.HorizontalMovement.IDLE);
                    }
                }
                break;
            case Key.D: //Rechts losgelassen
                if (character.getHorizontalMovement() == PlayerCharacter.HorizontalMovement.RIGHT) {
                    //Wir haben uns bisher nach rechts bewegt und das soll jetzt aufhören
                    if (Game.isKeyPressed(Key.A)) {
                        //A ist gedrückt, wir wollen also ab jetzt nach Links
                        character.setHorizontalMovement(PlayerCharacter.HorizontalMovement.LEFT);
                    } else {
                        character.setHorizontalMovement(PlayerCharacter.HorizontalMovement.IDLE);
                    }
                }
                break;
        }
    }
}
