package ea.example.showcase.dude;

import ea.actor.Tile;
import ea.actor.TileContainer;
import ea.actor.TileMap;
import ea.actor.BodyType;

/**
 * Einfache Plattform. Herzlichen Dank an <a href="https://www.gameart2d.com/free-graveyard-platformer-tileset.html">Billard
 * Art 2D</a> für die kostenfreien Tiles.
 */
public class Platform extends TileContainer {
    private static final int SIZE = 2;
    private static final float FRICTION = 0.5f;

    public Platform(int tileCount) {
        super(tileCount, 1, SIZE);

        if (tileCount < 2) {
            throw new IllegalArgumentException("Number of tiles must be at least 2");
        }

        setShapes("R 0, " + (SIZE - 0.5) + "," + (SIZE * tileCount) + ", 0.5");

        String basePath = "game-assets/dude/tiles/";

        setLeftTile(TileMap.createFromImage(basePath + "platform_l.png"));
        setMiddleTiles(TileMap.createFromImage(basePath + "platform_m.png"));
        setRightTile(TileMap.createFromImage(basePath + "platform_r.png"));

        setBodyType(BodyType.STATIC);
        setFriction(FRICTION);
        setRestitution(0);
    }

    private void setLeftTile(Tile tile) {
        setTile(0, 0, tile);
    }

    private void setMiddleTiles(Tile tile) {
        for (int x = 1; x < getTileCountX() - 1; x++) {
            setTile(x, 0, tile);
        }
    }

    private void setRightTile(Tile tile) {
        setTile(getTileCountX() - 1, 0, tile);
    }
}
