package ea.actor;

import ea.internal.ShapeHelper;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.internal.io.ImageLoader;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ein <code>TileContainer</code> ist eine schachbrettartige Anordnung quadratischer
 * <a href="https://de.wikipedia.org/wiki/Tiling_(Computer)">Tiles</a>.
 *
 * @author Michael Andonie
 */
public class TileContainer extends Actor {

    private static final ConcurrentHashMap<String, Tile> tileAtlas = new ConcurrentHashMap<>();

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
     * @see #setTileAt(int, int, String)
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
     * @see #setTileAt(int, int, String)
     */
    @API
    public TileContainer(int numX, int numY, float tileSize) {
        this(numX, numY, tileSize, tileSize);
    }

    /**
     * Setzt das Tile an einer festen Position durch eine klare Bilddatei.
     *
     * @param x         Der X-Index für das neu zu setzende Tile.
     * @param y         Der Y-Index für das neu zu setzende Tile.
     * @param imagePath Der Pfad zur Bilddatei des neuen Tiles. Bei <code>null</code> wird das entsprechende Tile leer.
     */
    @API
    public void setTileAt(int x, int y, String imagePath) {
        assertXYIndices(x, y);

        if (imagePath == null) {
            tiles[x][y] = null;
            return;
        }

        // Load in new Tile in Atlas (issues like non-existent files are thrown as RuntimeException)
        tiles[x][y] = new BufferedImageTile(ImageLoader.load(imagePath), tileWidth, tileHeight);
    }

    /**
     * Setzt das Tile an einer festen Position neu anhand eines Bildausschnitts.
     *
     * @param x           Der X-Index <b>in diesem Container</b> für das neu zu setzende Tile.
     * @param y           Der Y-Index <b>in diesem Container</b> für das neu zu setzende Tile.
     * @param imagePath   Der Pfad zur Bilddatei des neuen Tiles.
     * @param imageIndexX Der X-Index des Tiles <b>in der Bilddatei</b>.
     * @param imageIndexY Der Y-Index des Tiles <b>in der Bilddatei</b>.
     */
    @API
    public void setTileAt(int x, int y, String imagePath, int imageIndexX, int imageIndexY) {
        assertXYIndices(x, y);
        if (imagePath == null) {
            throw new IllegalArgumentException("Der imagePath kann nicht null sein.");
        }

        BufferedImage image = ImageLoader.load(imagePath);
        int tileWidth = image.getWidth() / tiles.length;
        int tileHeight = image.getHeight() / tiles[0].length;

        String tileKey = imagePath + "|" + tileWidth + "|" + tileHeight + "|" + imageIndexX + "|" + imageIndexY;

        Tile newTile;

        // Check if Tile exists in TileAtlas
        if (!tileAtlas.containsKey(tileKey)) {
            // Load in new Tile in Atlas (issues like non-existent files are thrown as RuntimeException)
            BufferedImage tileImage = image.getSubimage(imageIndexX * tileWidth, imageIndexY * tileHeight, tileWidth, tileHeight);
            tileAtlas.put(tileKey, new BufferedImageTile(tileImage, tileWidth, tileHeight));
        }
        newTile = tileAtlas.get(tileKey);
        if (newTile.getWidth() != tileWidth || newTile.getHeight() != tileHeight) {
            throw new RuntimeException("Das Bild hatte nicht die korrekten Maße (" + tileWidth + "x" + tileHeight + "). Die Maße waren: " + newTile.getWidth() + "x" + newTile.getHeight());
        }

        tiles[x][y] = newTile;
    }

    /**
     * Stellt sicher, dass ein X/Y-Parameterpaar im Rahmen der möglichen Indizes für Tiles dieses Containers liegt.
     *
     * @param x Ein X-Index.
     * @param y Ein Y-Index.
     *
     * @throws IllegalArgumentException Wenn das Parameterpaar nicht im Rahmen der möglichen Indizes lag.
     */
    @Internal
    private void assertXYIndices(int x, int y) {
        if (x < 0 || x >= tiles.length) {
            throw new IllegalArgumentException("X muss innerhalb der richtigen Größe sein (0-" + (tiles.length - 1) + ")" + ". War: " + x);
        }
        if (y < 0 || y >= tiles[0].length) {
            throw new IllegalArgumentException("Y muss innerhalb der richtigen Größe sein (0-" + (tiles[0].length - 1) + "). War: " + y);
        }
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

                    float tx = tiles[x][y].getWidth() * x;
                    float ty = tiles[x][y].getHeight() * y;

                    g.translate(tx, ty);
                    tiles[x][y].render(g);
                    g.translate(-tx, -ty);
                }
            }
        } finally {
            g.setTransform(before);
        }
    }

    /**
     * Abstrakte Klasse beschreibt eine Tile-Instanz für den Tile-Atlas. <i>In jeder Engine-Instanz existiert jedes Tile
     * nur einmal im Atlas.</i>
     */
    @Internal
    private abstract static class Tile {
        abstract void render(Graphics2D g);
        abstract float getWidth();
        abstract float getHeight();
    }

    @Internal
    private static class BufferedImageTile extends Tile {
        private final BufferedImage bufferedImage;
        private final float width;
        private final float height;

        private BufferedImageTile(BufferedImage bufferedImage, float width, float height) {
            this.bufferedImage = bufferedImage;
            this.width = width;
            this.height = height;
        }

        @Override
        @Internal
        void render(Graphics2D g) {
            AffineTransform pre = g.getTransform();
            g.scale(width / bufferedImage.getWidth(), height / bufferedImage.getHeight());
            g.drawImage(this.bufferedImage, null, 0, 0);
            g.setTransform(pre);
        }

        @Override
        float getWidth() {
            return width;
        }

        @Override
        float getHeight() {
            return height;
        }
    }
}
