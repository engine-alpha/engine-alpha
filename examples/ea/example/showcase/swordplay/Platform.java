package ea.example.showcase.swordplay;

import ea.Scene;
import ea.actor.TileContainer;
import ea.handle.Physics;

/**
 * Einfache Plattform.
 * Herzlichen Dank an <a href="https://www.gameart2d.com/free-graveyard-platformer-tileset.html">Game Art 2D</a> für
 * die kostenfreien Tiles.
 */
public class Platform
extends TileContainer {

    public Platform(Scene parent, int numX) {
        super(numX, 1, 128);
        if(numX < 2) throw new IllegalArgumentException("Platform zu klein!");

        String pathToTiles="game-assets\\sword\\tiles\\";

        //Tile für das linke Ende
        setTileAt(0,0, pathToTiles+"platform_l.png");
        //Tile für alle mittleren Stücke
        for(int x = 1; x < numX-1; x++)
            setTileAt(x, 0, pathToTiles+"platform_m.png");
        //Tile für das rechte Ende
        setTileAt(numX-1,0,pathToTiles+"platform_r.png");

        parent.add(this);

        physics.setType(Physics.Type.STATIC);
        physics.setElasticity(0);
    }
}
