package ea.example.showcase.dude;

import ea.Scene;
import ea.actor.TileContainer;
import ea.handle.Physics;

/**
 * Einfache Plattform. Herzlichen Dank an <a href="https://www.gameart2d.com/free-graveyard-platformer-tileset.html">Game
 * Art 2D</a> für die kostenfreien Tiles.
 */
public class Platform extends TileContainer {
    private static final int TILE_SIZE = 128;

    public Platform(Scene scene, int tileCount) {
        super(scene, tileCount, 1, TILE_SIZE);

        if (tileCount < 2) {
            throw new IllegalArgumentException("Number of tiles must be at least 2");
        }

        String basePath = "game-assets/dude/tiles/";

        // Tile für das linke Ende
        setTileAt(0, 0, basePath + "platform_l.png");

        for (int x = 1; x < tileCount - 1; x++) {
            // Tile für alle mittleren Stücke
            setTileAt(x, 0, basePath + "platform_m.png");
        }

        // Tile für das rechte Ende
        setTileAt(tileCount - 1, 0, basePath + "platform_r.png");

        scene.add(this);

        setBodyType(Physics.Type.STATIC);
        physics.setElasticity(0);
    }
}
