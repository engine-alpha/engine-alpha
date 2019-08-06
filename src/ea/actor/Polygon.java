package ea.actor;

import ea.Vector;
import ea.internal.ShapeBuilder;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.function.Supplier;

/**
 * Beschreibt eine beliebige polygonale geometrische Form.
 *
 * @author Michael Andonie
 */
@API
public class Polygon extends Geometry {
    /**
     * Die Punkte, die das Polygon beschreiben.
     */
    private float[] px, py;

    private int[] scaledPx, scaledPy;

    /**
     * Skalierungsfaktor Breite
     */
    private float scaleX;

    /**
     * Skalierungsfaktor Höhe.
     */
    private float scaleY;

    /**
     * Erstellt ein neues Polygon. Seine Position ist der <b>Ursprung</b>.
     *
     * @param points Der Streckenzug an Punkten, der das Polygon beschreibt. Alle
     */
    @API
    public Polygon(Vector... points) {
        super(() -> ShapeBuilder.createPolygonShape(points));
        resetPoints(points);
    }

    /**
     * Setzt den Streckenzug neu, der dieses Polygon beschreibt. <b>Ändert die physikalischen Eigenschaften</b> des
     * Polygons.
     * <i>Konkanve Streckenzüge werden durch die kleinste konvexe Körperform beschrieben, die den Streckenzug
     * umspannt.</i>
     * Komplexere Formen können über {@code setShapes(Supplier)} physikalisch präzise umgesetzt werden.
     *
     * @param points Neuer Streckenzug.
     *
     * @see ea.actor.Actor#setShapes(Supplier)
     */
    @API
    public void resetPoints(Vector... points) {
        if (points.length < 3) {
            throw new RuntimeException("Der Streckenzug muss mindestens aus 3 Punkten bestehen, um ein gültiges Polygon zu beschreiben.");
        }

        this.px = new float[points.length];
        this.py = new float[points.length];
        this.scaledPx = new int[points.length];
        this.scaledPy = new int[points.length];

        for (int i = 0; i < points.length; i++) {
            px[i] = points[i].x;
            py[i] = points[i].y;
        }

        this.setShape(() -> ShapeBuilder.createPolygonShape(points));
    }

    /**
     * {@inheritDoc}
     */
    @Internal
    @Override
    public void render(Graphics2D g, float pixelPerMeter) {
        for (int i = 0; i < scaledPx.length; i++) {
            scaledPx[i] = (int) (px[i] * pixelPerMeter);
            scaledPy[i] = (int) (py[i] * pixelPerMeter);
        }

        AffineTransform at = g.getTransform();
        g.scale(1, -1);
        g.setColor(getColor());
        g.fillPolygon(scaledPx, scaledPy, scaledPx.length);
        g.setTransform(at);
    }
}
