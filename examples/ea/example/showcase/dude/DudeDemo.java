package ea.example.showcase.dude;

import ea.BoundingRechteck;
import ea.Layer;
import ea.Scene;
import ea.Vector;
import ea.actor.Image;
import ea.actor.TileContainer;
import ea.actor.TileMap;
import ea.example.showcase.ShowcaseDemo;
import ea.example.showcase.Showcases;
import ea.handle.BodyType;
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
        character.position.set(0, 0);
        character.setBodyType(BodyType.DYNAMIC);
        character.physics.setRotationLocked(true);
        hud.setPlayerCharacter(character);

        setGravity(new Vector(0, -13));
        getCamera().setFocus(character);
        getCamera().setOffset(new Vector(0, -100));
        getCamera().setBounds(new BoundingRechteck(-2000, 0, 20000, 20000));
        getCamera().setZoom(30f);

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

        pauseLayer = new PauseLayer();
        isPaused = false;
        pauseLayer.setVisible(false);
        addLayer(pauseLayer);

        addKeyListener(this);

        //game_loop.loop();
    }

    private void setupPlayground() {
        makePlatform(7, -450 / 60, -200 / 60);
        makePlatform(3, 200 / 60, 0);

        makePlatform(5, 800 / 60, -100 / 60);

        //makeBoxes(-100, 500, 40);

        makeBoxes(0, 40, 5);

        for (int i = 0; i < 15; i++) {
            Coin coin = new Coin();
            coin.position.set(6 + i, 6);
            coin.addCollisionListener(coin, character);
            add(coin);
        }

        for (int j = 0; j < 10; j++) {
            ManaPickup manaPickup = new ManaPickup();
            manaPickup.position.set(-j, 1);
            manaPickup.addCollisionListener(manaPickup, character);
            add(manaPickup);
        }
    }

    private void setupCosmeticLayers() {
        Layer middleBackground = new Layer();
        middleBackground.setParallaxPosition(0.1f, 0.1f);
        middleBackground.setLayerPosition(-200);
        Image backgroundImage = new Image("game-assets/dude/background/snow.png", 1, 1);
        backgroundImage.position.set(-GAME_WIDTH / 2 - 100, -GAME_HEIGHT / 2);
        middleBackground.add(backgroundImage);

        Layer furtherBackground = new Layer();
        furtherBackground.setLayerPosition(-300);
        furtherBackground.setParallaxPosition(0.05f, 0.05f);

        Image moon = new Image("game-assets/dude/moon.png", 1, 1);
        furtherBackground.add(moon);
        moon.position.set(300, 300);

        addLayer(middleBackground);
        addLayer(furtherBackground);

        // CLOUDS
        // addCloudLayer(10, "game-assets/dude/tiles/sky/clouds_MG_1.png", 300, 1.6f, 0.1f, -2000);
        // addCloudLayer(10, "game-assets/dude/tiles/sky/clouds_MG_2.png", 200, 1.4f, 0.1f, -2000);
        // addCloudLayer(10, "game-assets/dude/tiles/sky/clouds_MG_3.png", -60, 1.2f, 0.1f, -2000);
    }

    private void addCloudLayer(final int NUM_TILES, String tilePath, int layerLevel, float xParallax, float yParallax, float xOffset) {
        Layer clouds = new Layer();
        clouds.setParallaxPosition(xParallax, yParallax);
        clouds.setLayerPosition(layerLevel);
        TileContainer cloudTiles = new TileContainer(NUM_TILES, 1, 384, 216);
        for (int i = 0; i < NUM_TILES; i++) {
            cloudTiles.setTile(i, 0, TileMap.createFromImage(tilePath));
        }
        cloudTiles.position.set(xOffset, -GAME_HEIGHT / 2);
        clouds.add(cloudTiles);
        addLayer(clouds);
    }

    private void makePlatform(int length, float pX, float pY) {
        Platform platform = new Platform(length);
        platform.position.set(pX, pY);

        add(platform);
    }

    private void makeBoxes(float pX, float pY, int amount) {
        for (int i = 0; i < amount; i++) {
            Box box = new Box();
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
