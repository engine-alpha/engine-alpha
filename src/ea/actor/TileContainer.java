package ea.actor;

import ea.internal.ShapeHelper;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 * Ein <code>TileContainer</code> ist eine schachbrettartige Anordnung quadratischer
 * <a href="https://de.wikipedia.org/wiki/Tiling_(Computer)">Tiles</a>.
 *
 * @author Michael Andonie
 */
public class TileContainer extends Actor implements TileMap {

    /**
     * Die IDs der aktuellen Tiles des Containers.
     */
    private final Tile[][] tiles;

    /**
     * Die Breite eines Tiles (original) in px.
     */
    private final float tileWidth;

    /**
     * Die Höhe eines Tiles (original) in px.
     */
    private final float tileHeight;

    /**
     * Erstellt einen <b>leeren</b> Tile-Container. Er ist erst "sichtbar", wenn Tiles gesetzt werden.
     *
     * @param numX       Die Anzahl an Tiles in X-Richtung.
     * @param numY       Die Anzahl an Tiles in Y-Richtung.
     * @param tileWidth  Die Breite eines Tiles in Pixel.
     * @param tileHeight Die Höhe eines Tiles in Pixel.
     *
     * @see #setTile(int, int, Tile)
     */
    @API
    public TileContainer(int numX, int numY, float tileWidth, float tileHeight) {
        super(() -> ShapeHelper.createRectangularShape(tileWidth * numX, tileHeight * numY));

        if (numX <= 0 || numY <= 0) {
            throw new IllegalArgumentException("numX und numY müssen jeweils > 0 sein.");
        }
        if (tileWidth <= 0 || tileHeight <= 0) {
            throw new IllegalArgumentException("Breite und Höhe der Tiles müssen jeweils > 0 sein.");
        }

        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tiles = new Tile[numX][numY];
    }

    /**
     * Erstellt einen <b>leeren</b> Tile-Container für quadratische Tiles. Er ist erst "sichtbar", wenn Tiles gesetzt
     * werden.
     *
     * @param numX     Die Anzahl an Tiles in X-Richtung.
     * @param numY     Die Anzahl an Tiles in Y-Richtung.
     * @param tileSize Die Höhe <b>und</b> Breite eines Tiles in Pixel.
     *
     * @see #setTile(int, int, Tile)
     */
    @API
    public TileContainer(int numX, int numY, float tileSize) {
        this(numX, numY, tileSize, tileSize);
    }

    /**
     * Setzt das Tile an einer festen Position durch eine klare Bilddatei.
     *
     * @param x    Der X-Index für das neu zu setzende Tile.
     * @param y    Der Y-Index für das neu zu setzende Tile.
     * @param tile Das neue Tile. Bei <code>null</code> wird das entsprechende Tile leer.
     */
    @API
    public void setTile(int x, int y, Tile tile) {
        tiles[x][y] = tile;
    }

    @Internal
    @Override
    public void render(Graphics2D g) {
        final AffineTransform before = g.getTransform();
        float offset = tiles[0].length * tileHeight;

        try {
            g.translate(0, -offset);

            for (int x = 0; x < tiles.length; x++) {
                for (int y = 0; y < tiles[0].length; y++) {
                    if (tiles[x][y] == null) {
                        continue;
                    }

                    float tx = tileWidth * x;
                    float ty = tileHeight * y;

                    g.translate(tx, ty);
                    tiles[x][y].render(g, tileWidth, tileHeight);
                    g.translate(-tx, -ty);
                }
            }
        } finally {
            g.setTransform(before);
        }
    }

    @Override
    public Tile getTile(int x, int y) {
        return tiles[x][y];
    }
}
