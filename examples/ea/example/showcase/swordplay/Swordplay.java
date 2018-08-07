package ea.example.showcase.swordplay;

import ea.Game;
import ea.Scene;
import ea.Vector;
import ea.actor.Actor;
import ea.actor.Image;
import ea.example.showcase.ShowcaseDemo;
import ea.handle.Physics;
import ea.input.KeyListener;

import java.awt.event.KeyEvent;
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
        character.setBodyType(Physics.Type.DYNAMIC);
        character.physics.setRotationLocked(true);
        character.physics.setGravity(new Vector(0, -13));

        new Platform(this, 7).position.set(-450, -200);

        addKeyListener(this);
        addFrameUpdateListener(character);

        getCamera().move(0, 200);

        Coin coin = new Coin(this);
        coins.add(coin);
        add(coin);

        coin = new Coin(this);
        coin.position.set(100, 0);
        coins.add(coin);
        add(coin);

        Image moon = new Image(this, "game-assets/sword/moon.png");
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
    public void onKeyDown(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A: //Move left
                if (character.getHorizontalMovement() != PlayerCharacter.HorizontalMovement.RIGHT) {
                    //Wir bewegen uns gerade NICHT schon nach rechts-> Dann auf nach links
                    character.setHorizontalMovement(PlayerCharacter.HorizontalMovement.LEFT);
                }
                break;
            case KeyEvent.VK_S:
                character.smash();
                break;
            case KeyEvent.VK_D://Move right
                if (character.getHorizontalMovement() != PlayerCharacter.HorizontalMovement.LEFT) {
                    //Wir bewegen uns gerade NICHT schon nach links -> Dann auf nach rechts
                    character.setHorizontalMovement(PlayerCharacter.HorizontalMovement.RIGHT);
                }
                break;
            case KeyEvent.VK_W: //Sprungbefehl
                character.tryJumping();
                break;
            case KeyEvent.VK_X:
                character.physics.applyImpulse(new Vector(500, 0));
                break;
            case KeyEvent.VK_P:
                toggleDebug();
                break;
            case KeyEvent.VK_T:
                character.physics.applyImpulse(new Vector(0, -2000));
                break;
            case KeyEvent.VK_C:
                //
                break;
        }
    }

    @Override
    public void onKeyUp(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A: //Links losgelassen
                if (character.getHorizontalMovement() == PlayerCharacter.HorizontalMovement.LEFT) {
                    //Wir haben uns bisher nach links bewegt und das soll jetzt aufhören
                    if (Game.isKeyPressed(KeyEvent.VK_D)) {
                        //D ist auch gedrückt, wir wollen Also ab jetzt nach Rechts
                        character.setHorizontalMovement(PlayerCharacter.HorizontalMovement.RIGHT);
                    } else {
                        character.setHorizontalMovement(PlayerCharacter.HorizontalMovement.IDLE);
                    }
                }
                break;
            case KeyEvent.VK_D: //Rechts losgelassen
                if (character.getHorizontalMovement() == PlayerCharacter.HorizontalMovement.RIGHT) {
                    //Wir haben uns bisher nach rechts bewegt und das soll jetzt aufhören
                    if (Game.isKeyPressed(KeyEvent.VK_A)) {
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
