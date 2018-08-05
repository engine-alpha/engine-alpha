package ea.actor;

import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import ea.internal.io.ImageLoader;
import org.jbox2d.collision.shapes.Shape;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ein <code>TileContainer</code> ist eine schachbrettartige Anordnung quadratischer
 * <a href="https://de.wikipedia.org/wiki/Tiling_(Computer)">Tiles</a>.
 * @author Michael Andonie
 */
public class TileContainer
extends Actor{

    private static final ConcurrentHashMap<String, Tile> tileAtlas = new ConcurrentHashMap<>();

    /**
     * Die IDs der aktuellen Tiles des Containers.
     */
    private final Tile[][] tiles;

    /**
     * Die Größe eines Tiles in px
     */
    private final int tileSize;

    /**
     * Erstellt einen <b>leeren</b> Tile-Container. Er ist erst "sichtbar", wenn Tiles gesetzt werden.
     * @param numX  Die Anzahl an Tiles in X-Richtung.
     * @param numY  Die Anzahl an Tiles in Y-Richtung.
     * @param tileSize  Die Größe eines Tiles in Pixel.
     * @see #setTileAt(int, int, String)
     */
    @API
    public TileContainer(int numX, int numY, int tileSize) {
        if(numX <= 0 || numY <= 0) throw new IllegalArgumentException("numX und numY müssen jeweils > 0 sein.");
        this.tileSize=tileSize;
        this.tiles=new Tile[numX][numY];
    }

    /**
     * Setzt das Tile an einer festen Position durch eine klare Bilddatei.
     * @param x Der X-Index für das neu zu setzende Tile.
     * @param y Der Y-Index für das neu zu setzende Tile.
     * @param imagePath Der Pfad zur Bilddatei des neuen Tiles. Bei <code>null</code> wird das entsprechende Tile leer.
     */
    @API
    public void setTileAt(int x, int y, String imagePath) {
        assertXYIndices(x, y);

        if(imagePath==null) {
            tiles[x][y]=null;
            return;
        }

        Tile newTile=null;

        //Load in new Tile in Atlas (issues like non-existent files are thrown as RuntimeException)
        BufferedImage tileImage = ImageLoader.load(imagePath);
        if(tileImage.getWidth() != tileImage.getHeight() || tileImage.getWidth() != tileSize) {
            throw new RuntimeException("Das Bild hatte nicht die korrekten Maße (" + tileSize + "x" + tileSize
                    + "). Die Maße waren: " + tileImage.getWidth() + "x" + tileImage.getHeight());
        }

        tiles[x][y]=new BufferedImageTile(tileImage);
    }

    /**
     * Setzt das Tile an einer festen Position neu anhand eines Bildausschnitts.
     * @param x Der X-Index <b>in diesem Container</b> für das neu zu setzende Tile.
     * @param y Der Y-Index <b>in diesem Container</b> für das neu zu setzende Tile.
     * @param imagePath Der Pfad zur Bilddatei des neuen Tiles.
     * @param imageIndexX Der X-Index des Tiles <b>in der Bilddatei</b>.
     * @param imageIndexY Der Y-Index des Tiles <b>in der Bilddatei</b>.
     */
    @API
    public void setTileAt(int x, int y, String imagePath, int imageIndexX, int imageIndexY) {
        assertXYIndices(x, y);
        if(imagePath==null) throw new IllegalArgumentException("Der imagePath kann nicht null sein.");

        final String tileKey=imagePath+"|"+tileSize+"|"+imageIndexX+"|"+imageIndexY;

        Tile newTile;

        //Check if Tile exists in TileAtlas
        if(!tileAtlas.containsKey(tileKey)) {
            //Load in new Tile in Atlas (issues like non-existent files are thrown as RuntimeException)
            BufferedImage baseImage = ImageLoader.load(imagePath);
            BufferedImage tileImage = baseImage.getSubimage(
                    imageIndexX*tileSize, imageIndexY*tileSize, tileSize, tileSize);
            tileAtlas.put(tileKey, new BufferedImageTile(baseImage));
        }
        newTile = tileAtlas.get(tileKey);
        if(newTile.getSize() != tileSize) throw new RuntimeException("Das Bild hatte nicht die korrekten Maße (" +
                tileSize + "x" + tileSize + "). Die Maße waren: " + newTile.getSize() + "x" + newTile.getSize());

        tiles[x][y]=newTile;
    }

    /**
     * Stellt sicher, dass ein X/Y-Parameterpaar im Rahmen der möglichen Indizes für Tiles dieses Containers liegt.
     * @param x Ein X-Index.
     * @param y Ein Y-Index.
     * @throws IllegalArgumentException Wenn das Parameterpaar nicht im Rahmen der möglichen Indizes lag.
     */
    @NoExternalUse
    private void assertXYIndices(int x, int y) {
        if(x < 0 || x >= tiles.length)
            throw new IllegalArgumentException("X muss innerhalb der richtigen Größe sein (0-" + (tiles.length-1) + ")"
                    + ". War: " + x);
        if(y < 0 || y >= tiles[0].length)
            throw new IllegalArgumentException("Y muss innerhalb der richtigen Größe sein (0-" + (tiles[0].length-1)
                    + "). War: " + y);
    }

    @NoExternalUse
    @Override
    public void render(Graphics2D g) {
        for(int x = 0; x < tiles.length; x++) {
            for(int y = 0; y < tiles[0].length; y++) {
                if(tiles[x][y]==null) continue;
                tiles[x][y].render(g, tileSize*x, tileSize*y);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @NoExternalUse
    @Override
    public Shape createShape(float pixelProMeter) {
        return berechneBoxShape(pixelProMeter, tileSize*tiles.length, tileSize*tiles[0].length);
    }


    /**
     * Abstrakte Klasse beschreibt eine Tile-Instanz für den Tile-Atlas. <i>In jeder Engine-Instanz existiert
     * jedes Tile nur einmal im Atlas.</i>
     */
    @NoExternalUse
    private abstract class Tile {
        abstract void render(Graphics2D g, int dX, int dY);
        abstract int getSize();
    }

    @NoExternalUse
    private class BufferedImageTile
    extends Tile {
        private final BufferedImage bufferedImage;

        private BufferedImageTile(BufferedImage bufferedImage) {
            this.bufferedImage = bufferedImage;
        }

        @Override
        @NoExternalUse
        void render(Graphics2D g, int dX, int dY) {
            g.drawImage(bufferedImage, null, dX, dY);
        }

        @Override
        int getSize() {
            return bufferedImage.getWidth();
        }
    }
}
