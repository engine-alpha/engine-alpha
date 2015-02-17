package ea.internal.phy;

import ea.Physik;
import ea.Punkt;
import ea.Raum;
import ea.Vektor;
import ea.internal.ano.NoExternalUse;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

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
    public BodyHandler(Raum raum, WorldHandler worldHandler, BodyDef bd, FixtureDef fixtureDef) {
        super(raum);
        this.worldHandler = worldHandler;

        //create the body and add fixture to it
        body =  worldHandler.createBody(bd);
        body.createFixture(this.fixtureDef = fixtureDef);
    }

    @Override
    public PhysikHandler update(WorldHandler worldHandler) throws IllegalStateException {
        if(worldHandler != this.worldHandler) {
            throw new IllegalStateException("Ein Raum-Objekt darf nicht zwischen Wurzeln wechseln.");
        }
        return this;
    }

    @Override
    public void verschieben(Vektor v) {
        Vec2 phyVec = worldHandler.fromVektor(v);
        body.setTransform(phyVec.add(body.getPosition()), body.getAngle());
    }

    @Override
    public Punkt mittelpunkt() {
        Vec2 wc = body.getWorldCenter();
        return worldHandler.fromVec2(wc).alsPunkt();
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
    public void typ(Physik.Typ typ) {

    }

    @Override
    public Physik.Typ typ() {
        return null;
    }
}
