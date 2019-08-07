package ea.example.showcase.dude;

import ea.*;
import ea.actor.Image;
import ea.actor.TileContainer;
import ea.actor.TileMap;
import ea.example.showcase.ShowcaseDemo;
import ea.example.showcase.Showcases;
import ea.actor.BodyType;
import ea.event.KeyListener;

import java.awt.event.KeyEvent;

/**
 * Eine kleine Spieldemo.
 * <p>
 * Vielen Dank an <a href="https://rvros.itch.io/animated-pixel-hero">rvros</a>
 */
public class DudeDemo extends ShowcaseDemo implements KeyListener {

    public static final int GAME_WIDTH_PX = Showcases.WIDTH, GAME_HEIGHT_PX = Showcases.HEIGHT;

    private final GameData gameData;
    private final PlayerCharacter character;

    public DudeDemo(Scene parent) {
        super(parent);
        super.setDebuggingEnabled(false);

        gameData = new GameData();

        addLayer(new HUD(gameData));

        character = new PlayerCharacter(gameData);
        character.setPosition(0, 0);
        character.setRotationLocked(true);
        character.setBodyType(BodyType.DYNAMIC);

        add(character);

        setGravity(new Vector(0, -13));

        getCamera().setFocus(character);
        getCamera().setOffset(new Vector(0, 3));
        getCamera().setBounds(new Bounds(-2000, -3, 20000, 20000));
        getCamera().setZoom(30f);

        setupPlayground();
        setupCosmeticLayers();

        getMainLayer().setVisibleHeight(15);

        PauseLayer pauseLayer = new PauseLayer();
        pauseLayer.setVisible(false);
        addLayer(pauseLayer);
    }

    private void setupPlayground() {
        makePlatform(7, -450 / 60, -200 / 60);
        makePlatform(3, 200 / 60, 0);
        makePlatform(5, 800 / 60, -100 / 60);
        makeBoxes(0, 40, 5);

        for (int i = 0; i < 15; i++) {
            Coin coin = new Coin();
            coin.setPosition(6 + i, 6);
            coin.addCollisionListener(character, coin);

            add(coin);
        }

        for (int j = 0; j < 30; j++) {
            ManaPickup manaPickup = new ManaPickup();
            manaPickup.setPosition(-j, 1);
            manaPickup.addCollisionListener(character, manaPickup);

            add(manaPickup);
        }
    }

    private void setupCosmeticLayers() {
        Layer middleBackground = new Layer();
        middleBackground.setParallaxPosition(0.1f, 0.1f);
        middleBackground.setLayerPosition(-200);

        Image backgroundImage = new Image("game-assets/dude/background/snow.png", 25f);
        backgroundImage.setPosition(-getVisibleArea().width / 2, -getVisibleArea().height / 2);
        middleBackground.add(backgroundImage);

        Layer furtherBackground = new Layer();
        furtherBackground.setLayerPosition(-300);
        furtherBackground.setParallaxPosition(0.05f, 0.05f);

        Image moon = new Image("game-assets/dude/moon.png", 1, 1);
        furtherBackground.add(moon);
        moon.setPosition(300, 300);

        addLayer(middleBackground);
        addLayer(furtherBackground);

        // CLOUDS
        addCloudLayer(10, "game-assets/dude/tiles/sky/clouds_MG_1.png", 300, 1.6f, 1f, -100);
        addCloudLayer(10, "game-assets/dude/tiles/sky/clouds_MG_2.png", -50, 0.8f, 1f, -100);
        addCloudLayer(10, "game-assets/dude/tiles/sky/clouds_MG_3.png", -60, 0.7f, 1f, -100);
    }

    private void addCloudLayer(final int NUM_TILES, String tilePath, int layerLevel, float xParallax, float yParallax, float xOffset) {
        Layer clouds = new Layer();
        clouds.setParallaxPosition(xParallax, yParallax);
        clouds.setLayerPosition(layerLevel);

        final float SCALE = 0.08f;
        TileContainer cloudTiles = new TileContainer(NUM_TILES, 1, 384 * SCALE, 216 * SCALE);
        for (int i = 0; i < NUM_TILES; i++) {
            cloudTiles.setTile(i, 0, TileMap.createFromImage(tilePath));
        }
        cloudTiles.setPosition(xOffset, -getVisibleArea().height / 2 + 5);
        clouds.add(cloudTiles);
        addLayer(clouds);
    }

    private void makePlatform(int length, float pX, float pY) {
        Platform platform = new Platform(length);
        platform.setPosition(pX, pY);

        add(platform);
    }

    private void makeBoxes(float pX, float pY, int amount) {
        for (int i = 0; i < amount; i++) {
            Box box = new Box();
            box.setPosition(pX + i * 80, pY);

            add(box);
        }
    }

    @Override
    public void onKeyDown(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_M) {
            toggleDebug();
        }
    }
}
