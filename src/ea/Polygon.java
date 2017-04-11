package ea;

import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import ea.internal.util.Logger;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

import java.awt.*;

/**
 *
 *
 * Created by Michael on 12.04.2017.
 */
public class Polygon
extends Geometrie{

    /**
     * Die Punkte
     */
    private final Punkt[] points;

    /**
     * Die Punkte, die das Polygon beschreiben
     */
    private final int[] px, py;

    /**
     * Erstellt ein neues Polygon. Seine Position ist der <b>Ursprung</b>.
     * @param points    Der Streckenzug an Punkten, der das Polygon beschreibt. Alle
     */
    @API
    public Polygon(Punkt... points) {
        super(Punkt.ZENTRUM);
        this.points = points;
        if(points.length < 3) {
            Logger.error("Geometry", "Der Streckenzug muss mindestens aus 3 Punkten bestehen, um ein " +
                    "gültiges Polygon zu beschreiben.");
            px=py=null;
            return;
        }

        px=new int[points.length];
        py=new int[points.length];

        for(int i = 0; i < points.length; i++) {
            px[i] = (int)points[i].realX();
            py[i] = (int)points[i].realY();
        }
    }

    /**
     * {@inheritDoc}
     */
    @NoExternalUse
    @Override
    public void render(Graphics2D g) {
        g.setColor(getColor());
        g.drawPolygon(px, py, px.length);
    }

    /**
     * {@inheritDoc}
     */
    @NoExternalUse
    @Override
    public Shape createShape(float pixelProMeter) {
        Vec2[] vec2s = new Vec2[points.length];
        for(int i = 0; i < points.length; i++) {
            vec2s[i] = points[i].toVec2();
        }
        PolygonShape shape = new PolygonShape();
        shape.set(vec2s, points.length);
        return shape;
    }
}
