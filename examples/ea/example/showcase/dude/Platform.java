package ea.example.showcase.dude;

import ea.actor.TileContainer;
import ea.actor.TileMap;
import ea.collision.CollisionEvent;
import ea.collision.CollisionListener;
import ea.handle.BodyType;

/**
 * Einfache Plattform. Herzlichen Dank an <a href="https://www.gameart2d.com/free-graveyard-platformer-tileset.html">Billard
 * Art 2D</a> für die kostenfreien Tiles.
 */
public class Platform extends TileContainer implements CollisionListener<Box> {
    private static final int TILE_SIZE = 2;

    public Platform(int tileCount) {
        super(tileCount, 1, TILE_SIZE);

        if (tileCount < 2) {
            throw new IllegalArgumentException("Number of tiles must be at least 2");
        }

        setShapes("R 0, " + (TILE_SIZE - 0.5) + "," + (TILE_SIZE * tileCount) + ", 0.5");

        String basePath = "game-assets/dude/tiles/";

        // Tile für das linke Ende
        setTile(0, 0, TileMap.createFromImage(basePath + "platform_l.png"));

        for (int x = 1; x < tileCount - 1; x++) {
            // Tile für alle mittleren Stücke
            setTile(x, 0, TileMap.createFromImage(basePath + "platform_m.png"));
        }

        // Tile für das rechte Ende
        setTile(tileCount - 1, 0, TileMap.createFromImage(basePath + "platform_r.png"));

        setBodyType(BodyType.STATIC);

        physics.setFriction(0.5f);
        physics.setRestitution(0);
    }

    @Override
    public void onCollisionEnd(CollisionEvent<Box> collisionEvent) {

    }

    @Override
    public void onCollision(CollisionEvent<Box> collisionEvent) {

    }
}
