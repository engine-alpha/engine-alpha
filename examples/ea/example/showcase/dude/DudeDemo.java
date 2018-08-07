package ea.example.showcase.dude;

import ea.*;
import ea.actor.Image;
import ea.actor.TileContainer;
import ea.example.showcase.ShowcaseDemo;
import ea.example.showcase.Showcases;
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
public class DudeDemo extends ShowcaseDemo implements KeyListener {

    public static final int GAME_WIDTH = Showcases.WIDTH, GAME_HEIGHT = Showcases.HEIGHT;

    private PlayerCharacter character;
    private Collection<Coin> coins = new HashSet<>();

    public DudeDemo(Scene parent) {
        super(parent);
        super.setDebuggingEnabled(false);

        HUD hud = new HUD(this, character);
        addLayer(hud);

        character = new PlayerCharacter(this, hud);
        character.position.set(-20, 200);
        character.setBodyType(Physics.Type.DYNAMIC);
        character.physics.setRotationLocked(true);

        setGravity(new Vector(0, -13));
        getCamera().setFocus(character);
        getCamera().setOffset(new Vector(0, 200));
        getCamera().setBounds(new BoundingRechteck(-2000, 0, 20000, 20000));

        setupPlayground();
        setupCosmeticLayers();

        // addFrameUpdateListener(new PeriodicTask(16, () -> {
        //     Particle particle = new Particle(DudeDemo.this, Random.nextInteger(2) + 2, 3000);
        //     particle.position.set(Random.nextInteger(860) - 430, -110);
        //     particle.physics.applyImpulse(new Vector(.5f * ((float) Math.random() - .5f), 2f * ((float) Math.random())));
        //     particle.setColor(new Color(54, 255, 195));
        //     particle.setLayer(-1);
        //
        //     add(particle);
        // }));

        addKeyListener(this);
    }

    private void setupPlayground() {
        makePlatform(7, -450, -200);
        makePlatform(3, 200, 0);

        Coin coin = new Coin(this);
        coins.add(coin);
        add(coin);

        coin = new Coin(this);
        coin.position.set(100, 0);
        coins.add(coin);
        add(coin);
    }

    private void setupCosmeticLayers() {
        Layer middleBackground = new Layer(this);
        middleBackground.setParallaxPosition(0.1f, 0.1f);
        middleBackground.setLayerPosition(-200);
        Image backgroundImage = new Image(this, "game-assets/dude/background/snow.png");
        backgroundImage.position.set(-GAME_WIDTH / 2 - 100, -GAME_HEIGHT / 2);
        middleBackground.add(backgroundImage);

        Layer furtherBackground = new Layer(this);
        //furtherBackground.setLayerPosition(-300);
        furtherBackground.setParallaxPosition(0.05f, 0.05f);

        Image moon = new Image(this, "game-assets/dude/moon.png");
        furtherBackground.add(moon);
        moon.position.set(300, 300);

        addLayer(middleBackground);
        addLayer(furtherBackground);

        //CLOUDS
        addCloudLayer(10, "game-assets/dude/tiles/sky/clouds_MG_1.png", 300, 1.6f, 0.1f, -2000);
        addCloudLayer(10, "game-assets/dude/tiles/sky/clouds_MG_2.png", 200, 1.4f, 0.1f, -2000);
        addCloudLayer(10, "game-assets/dude/tiles/sky/clouds_MG_3.png", -60, 1.2f, 0.1f, -2000);
    }

    private final void addCloudLayer(final int NUM_TILES, String tilePath, int layerLevel, float xParallax, float yParallax, float xOffset) {
        Layer clouds = new Layer(this);
        clouds.setParallaxPosition(xParallax, yParallax);
        clouds.setLayerPosition(layerLevel);
        TileContainer cloudTiles = new TileContainer(this, NUM_TILES, 1, 384, 216);
        for (int i = 0; i < NUM_TILES; i++) {
            cloudTiles.setTileAt(i, 0, tilePath);
        }
        cloudTiles.position.set(xOffset, -GAME_HEIGHT / 2);
        clouds.add(cloudTiles);
        addLayer(clouds);
    }

    private void makePlatform(int length, float pX, float pY) {
        new Platform(this, length).position.set(pX, pY);
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
            case KeyEvent.VK_SPACE:
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
