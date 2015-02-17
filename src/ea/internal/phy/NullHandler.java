package ea.internal.phy;

import ea.Physik;
import ea.Physik.Typ;
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

    /**
     * Die Fixture Definition für das Client-Objekt.
     */
    private final FixtureDef fixtureDef;

    /**
     * Masse als hilfeweise gespeicherte Variable.
     */
    private float masse;

    public NullHandler(Raum raum) {
        super(raum);
        bodyDef = new BodyDef();

        fixtureDef = new FixtureDef();
        fixtureDef.density = 30f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.5f;

    }

    /**
     * Speichert die Position des Objekts. Ist zu Beginn immer der Ursprung.
     */
    private Punkt position = Punkt.ZENTRUM;

    /**
     * Speichert die aktuelle Rotation in Radians. Ist zu Beginn stets 0.
     */
    private float rotation = 0;

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
        System.out.println("Nullhandler Mittelpunkt");
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
        fixtureDef.density = dichte;
    }

    @Override
    public float dichte() {

        return fixtureDef.density;
    }

    @Override
    public void reibungSetzen(float reibung) {
        fixtureDef.friction = reibung;
    }

    @Override
    public float reibung() {
        return fixtureDef.friction;
    }

    @Override
    public void elastizitaetSetzen(float ela) {
        fixtureDef.restitution = ela;
    }

    @Override
    public float elastizitaet() {
        return fixtureDef.restitution;
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
        throw new IllegalStateException("Das Objekt hat noch keine physikalischen Eigenschaften zugewiesen bekommen.");
    }

    @Override
    public void drehMomentWirken(float drehmoment) {
        throw new IllegalStateException("Das Objekt hat noch keine physikalischen Eigenschaften zugewiesen bekommen.");
    }

    @Override
    public void drehImpulsWirken(float drehimpuls) {
        throw new IllegalStateException("Das Objekt hat noch keine physikalischen Eigenschaften zugewiesen bekommen.");
    }

    @Override
    public void schwerkraftSetzen(Vektor schwerkraftInN) {
        throw new IllegalStateException("Das Objekt hat noch keine physikalischen Eigenschaften zugewiesen bekommen.");
    }

    @Override
    public void kraftWirken(Vektor kraftInN, Punkt globalerOrt) {
        throw new IllegalStateException("Das Objekt hat noch keine physikalischen Eigenschaften zugewiesen bekommen.");
    }

    @Override
    public void impulsWirken(Vektor impulsInNS, Punkt globalerOrt) {
        throw new IllegalStateException("Das Objekt hat noch keine physikalischen Eigenschaften zugewiesen bekommen.");
    }

    @Override
    public void typ(Typ typ) {
        switch (typ) {
            case STATISCH:
                bodyDef.type = BodyType.STATIC;
                break;
            case DYNAMISCH:
                bodyDef.type = BodyType.DYNAMIC;
                break;
            case KINEMATISCH:
                bodyDef.type = BodyType.KINEMATIC;
                break;
        }
        //TODO Typ <-> Update : Generierung vertauschen.



    }

    @Override
    public Typ typ() {
        return null;
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

        worldHandler.blockPPMChanges();

        bodyDef.position.set(worldHandler.fromVektor(position.alsVektor()));

        return new BodyHandler(raum, worldHandler, bodyDef, fixtureDef);
    }
}
