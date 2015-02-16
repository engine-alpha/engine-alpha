package ea.internal.phy;

import ea.Punkt;
import ea.Raum;
import ea.Vektor;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 *
 * Created by andonie on 16.02.15.
 */
public class NullHandler extends PhysikHandler {

    public NullHandler(Raum raum) {
        super(raum);
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;
    }



    /**
     * Speichert die Position des Objekts. Ist zu Beginn immer der Ursprung.
     */
    private Punkt position = Punkt.ZENTRUM;

    /**
     * Speichert die aktuelle Rotation in Radians. Ist zu Beginn stets 0.
     */
    private float rotation = 0;

    /**Standard-Physik-Parameter*/
    private float dichte = 0.5f, reibung = 0.3f, elastizitaet = 0.5f, masse = 50f;

    /**
     * Die Standard Body-Definition. Wird beim Übertrag eines Null-Handlers in einen
     * JB2D-Handler verwendet.
     */
    private final BodyDef bodyDef;

    @Override
    public void verschieben(Vektor v) {
        position = position.verschobeneInstanz(v);
    }

    @Override
    public Punkt mittelpunkt() {
        return null;
    }

    @Override
    public boolean schneidet(Raum r) {
        return false;
    }

    @Override
    public boolean beinhaltet(Punkt p) {
        return false;
    }

    @Override
    public Punkt position() {
        return position;
    }

    @Override
    public float rotation() {
        return rotation;
    }

    @Override
    public void rotieren(float radians) {
        rotation += radians;
    }

    @Override
    public void dichteSetzen(float dichte) {
        this.dichte = dichte;
    }

    @Override
    public float dichte() {
        return dichte;
    }

    @Override
    public void reibungSetzen(float reibung) {
        this.reibung = reibung;
    }

    @Override
    public float reibung() {
        return reibung;
    }

    @Override
    public void elastizitaetSetzen(float ela) {
        this.elastizitaet = ela;
    }

    @Override
    public float elastizitaet() {
        return elastizitaet;
    }

    @Override
    public void masseSetzen(float masse) {
        this.masse = masse;
    }

    @Override
    public float masse() {
        return masse;
    }

    @Override
    public void kraftWirken(Vektor kraft) {
        //
    }

    @Override
    public void drehMomentWirken(float drehmoment) {
        //
    }

    @Override
    public void drehImpulsWirken(float drehimpuls) {
        //
    }


    @Override
    public PhysikHandler update(WorldHandler worldHandler) throws IllegalStateException {
        if(worldHandler == null)
            return this;

        Shape jb2dShape = raum.berechneShape(worldHandler.getPixelProMeter());

        if(jb2dShape == null) {
            //Das Objekt hat keine Shape (ist Knoten)
            return this;
        }

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = jb2dShape;
        fixtureDef.density = dichte;
        fixtureDef.friction = reibung;
        fixtureDef.restitution = elastizitaet;

        bodyDef.position.set(worldHandler.fromVektor(position.alsVektor()));

        return new BodyHandler(raum, worldHandler, bodyDef, fixtureDef);
    }
}
