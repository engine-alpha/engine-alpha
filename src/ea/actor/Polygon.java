package ea.actor;

import ea.Vector;
import ea.internal.ShapeBuilder;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

import java.awt.Graphics2D;

/**
 * Beschreibt eine beliebige polygonale geometrische Form.
 *
 * @author Michael Andonie
 */
@API
public class Polygon extends Geometry {
    /**
     * Die Punkte, die das Polygon beschreiben.
     * Pending Scaling
     */
    private int[] px, py;

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

        if (points.length < 3) {
            throw new RuntimeException("Der Streckenzug muss mindestens aus 3 Punkten bestehen, um ein gültiges Polygon zu beschreiben.");
        }

        this.px = new int[points.length];
        this.py = new int[points.length];

        for (int i = 0; i < points.length; i++) {
            px[i] = Math.round(points[i].x);
            py[i] = -1 * Math.round(points[i].y);
        }
    }

    /**
     * 
     * @param points
     */
    private final void resetPoints(Vector... points) {
        //
    }

    /**
     * {@inheritDoc}
     */
    @Internal
    @Override
    public void render(Graphics2D g) {
        g.setColor(getColor());
        g.fillPolygon(px, py, px.length);
    }
}
