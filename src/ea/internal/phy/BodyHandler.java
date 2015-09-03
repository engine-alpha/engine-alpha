package ea.internal.phy;

import ea.Physik;
import ea.Punkt;
import ea.Raum;
import ea.Vektor;
import ea.internal.ano.NoExternalUse;
import ea.internal.util.Logger;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

/**
 * Ein <code>Body-Handler</code> kümmert sich um die <i>physikalische Darstellung</i> eines <code>Raum</code>-Objekts.<br />
 * Er übernimmt zwei wesentliche Aufgaben:
 * <ul>
 *     <li>Die Kontrolle und Steuerung innerhalb der <b>Physik-Engine</b> aus Sicht des respektiven Raum Objekts.</li>
 *     <li>Die Speicherung der <i>räumlichen Eigenschaften</i> (Position und Rotation) des respektiven Raum-Objekts.</li>
 * </ul>
 * Created by andonie on 15.02.15.
 */
public class BodyHandler
extends PhysikHandler {

    /**
     * Referenz auf den Handler der World, in der sich der Body befindet.
     */
    private final WorldHandler worldHandler;



    /**
     * Die Fixture Definition des Objekts.
     */
    private final FixtureDef fixtureDef;

    /**
     * Der Body als die physische Repräsentation des analogen Raum-Objekts in der Physics-Engine.
     */
    private Body body;

    public Body getBody() {
        return body;
    }

    /**
     * Erstellt einen neuen Body-Handler
     * @param raum
     */
    @NoExternalUse
    public BodyHandler(Raum raum, WorldHandler worldHandler, BodyDef bd, FixtureDef fixtureDef, Physik.Typ physikTyp, boolean isSensor) {
        super(raum, physikTyp, isSensor);
        this.worldHandler = worldHandler;

        //create the body and add fixture to it
        body =  worldHandler.createBody(bd);

        body.createFixture(this.fixtureDef = fixtureDef);
    }

    @Override
    public void update(WorldHandler worldHandler) throws IllegalStateException {
        if(worldHandler != this.worldHandler) {
            throw new IllegalStateException("Ein Raum-Objekt darf nicht zwischen Wurzeln wechseln.");
        }
    }

    @Override
    public void verschieben(Vektor v) {
        //System.out.println("Position before: " + body.getPosition());
        //System.out.println();

        Vec2 phyVec = worldHandler.fromVektor(v);
        body.setTransform(phyVec.add(body.getPosition()), body.getAngle());
        //System.out.println("Position after: " + body.getPosition());
        //System.out.println();
    }

    @Override
    public Punkt mittelpunkt() {
        if(physikTyp== Physik.Typ.DYNAMISCH) {
            Vec2 wc = body.getWorldCenter();
            return worldHandler.fromVec2(wc).alsPunkt();
        } else {
            throw new RuntimeException("WAR");
            //Logger.error("Physik", "Mittelpunkt ist nur für Dynamische Objekte implementiert.");
            //return worldHandler.fromVec2(body.getPosition()).alsPunkt();
        }

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
        return worldHandler.fromVec2(body.getPosition()).alsPunkt();
    }

    @Override
    public float rotation() {
        return WorldHandler.radToDeg(body.getAngle());
    }

    @Override
    public void rotieren(float radians) {
        System.out.println("Rotiere um " + radians);
        body.setTransform(body.getPosition(), body.getAngle() + radians);
    }

    @Override
    public void dichteSetzen(float dichte) {
        //Fixture body.getFixtureList()
    }

    @Override
    public float dichte() {
        return body.getFixtureList().getDensity();
    }

    @Override
    public void reibungSetzen(float reibung) {
        //
    }

    @Override
    public float reibung() {
        return body.getFixtureList().getFriction();
    }

    @Override
    public void elastizitaetSetzen(float ela) {

    }

    @Override
    public float elastizitaet() {
        return 0;
    }

    @Override
    public void masseSetzen(float masse) {

    }

    @Override
    public float masse() {
        return body.getMass();
    }

    @Override
    public void kraftWirken(Vektor kraft) {
        //System.out.println("Kraft " + kraft);
        body.applyForceToCenter(new Vec2(kraft.x, kraft.y));
    }

    @Override
    public void drehMomentWirken(float drehmoment) {

    }

    @Override
    public void drehImpulsWirken(float drehimpuls) {

    }

    @Override
    public void schwerkraftSetzen(Vektor schwerkraftInN) {
        worldHandler.getWorld().setGravity(new Vec2(schwerkraftInN.x, schwerkraftInN.y));
    }

    @Override
    public PhysikHandler typ(Physik.Typ typ) {
        if(typ == physikTyp) {
            return this; //kein Update nötig.
        }
        BodyType newType = typ.convert();
        body.setType(newType);
        isSensor = true; //TODO Delete again.

        System.out.println("I have a fixture: " + body.getFixtureList());

        body.setActive(typ != Physik.Typ.PASSIV || isSensor);
        body.getFixtureList().setSensor(typ == Physik.Typ.PASSIV && isSensor);

        System.out.println("Ph-Update: Sensor=" + body.getFixtureList().isSensor() + " - " + body.isActive());

        return this;
    }


    @Override
    public void kraftWirken(Vektor kraftInN, Punkt globalerOrt) {

    }

    @Override
    public void impulsWirken(Vektor impulsInNS, Punkt globalerOrt) {

    }

    @Override
    public void killBody() {
        worldHandler.getWorld().destroyBody(body);
    }

    @Override
    public WorldHandler worldHandler() {
        return worldHandler;
    }
}
