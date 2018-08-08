package ea.example.showcase.dude;

import ea.BoundingRechteck;
import ea.Layer;
import ea.Scene;
import ea.Vector;
import ea.actor.Image;
import ea.actor.TileContainer;
import ea.example.showcase.ShowcaseDemo;
import ea.example.showcase.Showcases;
import ea.handle.Physics;
import ea.input.KeyListener;
import ea.sound.Music;

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

    private final PauseLayer pauseLayer;

    private boolean isPaused = false;

    private Music game_loop = new Music("game-assets/dude/audio/background_game.wav");

    public DudeDemo(Scene parent) {
        super(parent);
        super.setDebuggingEnabled(false);

        HUD hud = new HUD(this);
        addLayer(hud);

        character = new PlayerCharacter(this, hud);
        character.position.set(-20, 200);
        character.setBodyType(Physics.Type.DYNAMIC);
        character.physics.setRotationLocked(true);
        hud.setPlayerCharacter(character);

        setGravity(new Vector(0, -13));
        getCamera().setFocus(character);
        getCamera().setOffset(new Vector(0, -100));
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

        pauseLayer = new PauseLayer(this);
        isPaused = false;
        pauseLayer.setVisible(false);
        addLayer(pauseLayer);

        addKeyListener(this);

        //game_loop.loop();
    }

    private void setupPlayground() {
        makePlatform(7, -450, -200);
        makePlatform(3, 200, 0);

        makePlatform(5, 800, -100);

        makeBoxes(0, 40, 5);

        for (int i = 0; i < 15; i++) {
            Coin coin = new Coin(this);
            coin.position.set(200 + 30 * i, 200);
            coin.addCollisionListener(coin, character);
            add(coin);
        }
        for (int j = 0; j < 10; j++) {
            ManaPickup manaPickup = new ManaPickup(this);
            manaPickup.position.set(0 - 30 * j, 0);
            manaPickup.addCollisionListener(manaPickup, character);
            add(manaPickup);
        }
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

    private void makeBoxes(float pX, float pY, int amount) {
        for (int i = 0; i < amount; i++) {
            Box box = new Box(this);
            box.position.set(pX + i * 80, pY);
            //box.position.set(0, 0);
            add(box);
        }
    }

    @Override
    public void onKeyDown(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_P:
                togglePause();
                break;
            case KeyEvent.VK_M:
                toggleDebug();
                break;
        }
    }

    @Override
    public void onKeyUp(KeyEvent e) {
        //Ignore
    }

    private void togglePause() {
        isPaused = !isPaused;
        pauseLayer.setVisible(isPaused);
        setPhysicsPaused(isPaused);
    }

    public boolean isPaused() {
        return isPaused;
    }
}
