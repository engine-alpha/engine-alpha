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
        //System.out.println("Position after: " + body.getPosition());
        //System.out.println();
    }

    @Override
    public Punkt mittelpunkt() {
        bodyGate();
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
    public boolean schneidet(Raum r) {
        bodyGate();
        return false;
    }

    @Override
    public boolean beinhaltet(Punkt p) {
        bodyGate();
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
        //bodyGate();
        if(body == null) {
            return predecessor.rotation();
        }
        return WorldHandler.radToDeg(body.getAngle());
    }

    @Override
    public void rotieren(float radians) {
        bodyGate();
        System.out.println("Rotiere um " + radians);
        body.setTransform(body.getPosition(), body.getAngle() + radians);
    }

    @Override
    public void dichteSetzen(float dichte) {
        bodyGate();
        //Fixture body.getFixtureList()
    }

    @Override
    public float dichte() {
        bodyGate();
        return body.getFixtureList().getDensity();
    }

    @Override
    public void reibungSetzen(float reibung) {
        bodyGate();
        //
    }

    @Override
    public float reibung() {
        bodyGate();
        return body.getFixtureList().getFriction();
    }

    @Override
    public void elastizitaetSetzen(float ela) {
        bodyGate();

    }

    @Override
    public float elastizitaet() {
        bodyGate();
        return 0;
    }

    @Override
    public void masseSetzen(float masse) {
        bodyGate();

    }

    @Override
    public float masse() {
        bodyGate();
        return body.getMass();
    }

    @Override
    public void kraftWirken(Vektor kraft) {
        bodyGate();
        //System.out.println("Kraft " + kraft);
        body.applyForceToCenter(new Vec2(kraft.x, kraft.y));
    }

    @Override
    public void drehMomentWirken(float drehmoment) {
        bodyGate();

    }

    @Override
    public void drehImpulsWirken(float drehimpuls) {
        bodyGate();

    }

    @Override
    public void schwerkraftSetzen(Vektor schwerkraftInN) {
        bodyGate();
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
        isSensor = true; //TODO Delete again.

        //System.out.println("I have a fixture: " + body.getFixtureList());

        body.setActive(typ != Physik.Typ.PASSIV || isSensor);
        fixture.setSensor(typ == Physik.Typ.PASSIV && isSensor);

        //System.out.println("Ph-Update: Sensor=" + body.getFixtureList().isSensor() + " - " + body.isActive());

        return this;
    }


    @Override
    public void kraftWirken(Vektor kraftInN, Punkt globalerOrt) {
        bodyGate();

    }

    @Override
    public void impulsWirken(Vektor impulsInNS, Punkt globalerOrt) {
        bodyGate();

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
     * Stellt sicher, dass der Body bereits existiert und
     * wartet ggf. auf dessen Erstellung. Wird vor
     * der Nutzung von Methoden verwendet, die den Body
     * zwingend voraussetzen (Impulse, Kräfte etc.)
     */
    @NoExternalUse
    private void bodyGate() {
        System.out.println("ENTER");
        while(body == null) {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    //
                }
            }
        }
        System.out.println("LEFT");
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

        synchronized (this) {
            this.notifyAll();
        }
    }
}
