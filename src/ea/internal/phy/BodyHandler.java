package ea.internal.phy;

import ea.Physik;
import ea.Punkt;
import ea.Raum;
import ea.Vektor;
import ea.internal.ano.NoExternalUse;
import ea.internal.util.Logger;
import org.jbox2d.collision.shapes.MassData;
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
     * Beschreibt eine Aktion, die an diesem Body ausgeführt werden soll.
     */
    public interface BodyJob {
        public void doJob();
    }


    /**
     * Die Fixture Definition des Objekts.
     */
    private FixtureDef fixtureDef;
    private BodyDef bodyDef;

    /**
     * Referenz auf den Vorgänger. Wird bewahrt für den Fall,
     * dass vor der Safe Body Creation Positionsinformationen
     * verlangt werden.
     */
    private NullHandler predecessor;

    /**
     * Die fixture (list) des Bodies.
     */
    private Fixture fixture;

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
    public BodyHandler(Raum raum, WorldHandler worldHandler, BodyDef bd, FixtureDef fixtureDef, Physik.Typ physikTyp, boolean isSensor, NullHandler predecessor) {
        super(raum, physikTyp, isSensor);
        this.worldHandler = worldHandler;
        this.fixtureDef = fixtureDef;
        this.bodyDef = bd;
        this.predecessor = predecessor;

        //Enqueue for safe creation of body / fixture
        //worldHandler.enqueueNewBodyHandler(this);
        createBodyAndFixture();
    }

    @Override
    public void setSensor(boolean isSensor) {
        this.isSensor = isSensor;
        this.body.getFixtureList().setSensor(isSensor);
    }

    @Override
    public void update(WorldHandler worldHandler) throws IllegalStateException {
        if(worldHandler != this.worldHandler) {
            throw new IllegalStateException("Ein Raum-Objekt darf nicht zwischen Wurzeln wechseln.");
        }
    }

    @Override
    public void verschieben(Vektor v) {
        if(body == null) {
            predecessor.verschieben(v);
            return;
        }

        //System.out.println("Position before: " + body.getPosition());
        //System.out.println();

        Vec2 phyVec = worldHandler.fromVektor(v);
        body.setTransform(phyVec.add(body.getPosition()), body.getAngle());

        //Wake Up Body -> Ensure In-Engine (JB2D) Adjustments will happen, e.g. Collision Readjustment
        body.setAwake(true);
        //System.out.println("Position after: " + body.getPosition());
        //System.out.println();
    }

    @Override
    public Punkt mittelpunkt() {
        if(physikTyp== Physik.Typ.DYNAMISCH) {
            Vec2 wc = body.getWorldCenter();
            return worldHandler.fromVec2(wc).alsPunkt();
        } else {
            throw new RuntimeException("Mittelpunktabfrage von nichtdynamischem Körper.");
            //Logger.error("Physik", "Mittelpunkt ist nur für Dynamische Objekte implementiert.");
            //return worldHandler.fromVec2(body.getPosition()).alsPunkt();
        }

    }

    @Override
    public boolean beinhaltet(Punkt p) {
        return false;
    }

    @Override
    public Punkt position() {
        if(body == null) {
            return predecessor.position();
        }
        return worldHandler.fromVec2(body.getPosition()).alsPunkt();
    }

    @Override
    public float rotation() {
        if(body == null) {
            return predecessor.rotation();
        }
        return WorldHandler.radToDeg(body.getAngle());
    }

    @Override
    public void rotieren(float radians) {
        //System.out.println("Rotiere um " + radians);
        body.setTransform(body.getPosition(), body.getAngle() + radians);
    }

    @Override
    public void dichteSetzen(float dichte) {
        //Fixture body.getFixtureList()
        if(physikBodyCheck()) {
            Fixture fixture = body.getFixtureList();
            while(fixture != null) {
                fixture.setDensity(dichte);
                fixture = fixture.getNext();
            }
        }
    }

    @Override
    public float dichte() {
        if(physikBodyCheck()) {
            return body.getFixtureList().getDensity();
        } return -1;
    }

    @Override
    public void reibungSetzen(float reibung) {
        if(physikBodyCheck()) {
            Fixture fixture = body.getFixtureList();
            while(fixture != null) {
                fixture.setFriction(reibung);
                fixture = fixture.getNext();
            }
        }
    }

    @Override
    public float reibung() {
        if(physikBodyCheck()) {
            return body.getFixtureList().getFriction();
        } return -1;
    }

    @Override
    public void elastizitaetSetzen(float ela) {
        if(physikBodyCheck()) {
            Fixture fixture = body.getFixtureList();
            while(fixture != null) {
                fixture.setRestitution(ela);
            }
        }
    }

    @Override
    public float elastizitaet() {
        if(physikBodyCheck()) {
            return body.getFixtureList().getRestitution();
        } return -1;
    }

    @Override
    public void masseSetzen(float masse) {
        if(physikBodyCheck()) {
            MassData md = new MassData();
            body.getMassData(md);
            md.mass = masse;
            body.setMassData(md);
        }
    }

    @Override
    public float masse() {
        if(physikBodyCheck()) {
            return body.getMass();
        } return -1;
    }

    @Override
    public void kraftWirken(Vektor kraft) {
        if(physikBodyCheck())
            body.applyForceToCenter(new Vec2(kraft.x, kraft.y));
    }

    @Override
    public void drehMomentWirken(float drehmoment) {
        if(physikBodyCheck()) {
            body.applyTorque(drehmoment);
        }
    }

    @Override
    public void drehImpulsWirken(float drehimpuls) {
        if(physikBodyCheck()) {
            body.applyAngularImpulse(drehimpuls);
        }
    }

    @Override
    public void schwerkraftSetzen(Vektor schwerkraftInN) {
        worldHandler.getWorld().setGravity(new Vec2(schwerkraftInN.x, schwerkraftInN.y));
    }

    @Override
    public PhysikHandler typ(Physik.Typ typ) {
        //bodyGate();
        if(typ == physikTyp) {
            return this; //kein Update nötig.
        }
        BodyType newType = typ.convert();
        body.setType(newType);
        //isSensor = true; //TODO Delete again.

        //System.out.println("I have a fixture: " + body.getFixtureList());

        //body.setActive(typ != Physik.Typ.PASSIV);
        //System.out.println("Set active!");
        body.setActive(true);
        fixture.setSensor(typ == Physik.Typ.PASSIV);// && isSensor);

        //System.out.println("Ph-Update: Sensor=" + body.getFixtureList().isSensor() + " - " + body.isActive());

        return this;
    }


    @Override
    public void kraftWirken(Vektor kraftInN, Punkt globalerOrt) {
        if(physikBodyCheck()) {
            body.applyForce(new Vec2(kraftInN.x, kraftInN.y), new Vec2(globalerOrt.x, globalerOrt.y));
        }
    }

    @Override
    public void impulsWirken(Vektor impulsInNS, Punkt globalerOrt) {
        if(physikBodyCheck()) {
            body.applyLinearImpulse(new Vec2(impulsInNS.x, impulsInNS.y), new Vec2(globalerOrt.x, globalerOrt.y));
        }
    }

    @Override
    public void physicalReset() {
        if(physikBodyCheck()) {
            body.setLinearVelocity(new Vec2());
            body.setAngularVelocity(0);
        }
    }

    @Override
    public void killBody() {
        worldHandler.getWorld().destroyBody(body);
    }

    @Override
    public WorldHandler worldHandler() {
        return worldHandler;
    }

    /**
     * Wird aufgerufen, sobald
     */
    @NoExternalUse
    public void createBodyAndFixture() {

        bodyDef.position.set(worldHandler.fromVektor(predecessor.position().alsVektor()));


        body =  worldHandler.createBody(bodyDef);
        fixture = body.createFixture(fixtureDef);

        bodyDef = null;
        fixtureDef = null;
        predecessor = null;
    }

    /**
     * Interner Check, ob der Body bereits erstellt wurde. Falls dies nicht der Fall ist,
     * wird eine Fehlermeldung ausgegeben.
     * @return  <code>true</code>, wenn der Body bereits existiert (ungleich <code>null</code> ist),
     *          sonst <code>false</code>.
     */
    private boolean physikBodyCheck() {
        if(body == null) {
            Logger.error("Physik", "Bevor das Raum-Objekt an einer Physik-Umgebung (~Wurzel) angemeldet war, " +
                    "wurde versucht, eine physikalische Operation daran auszuführen.");
            return false;
        } else return true;
    }
}
