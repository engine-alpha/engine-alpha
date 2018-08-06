package ea.internal.phy;

import ea.Vector;
import ea.actor.Actor;
import ea.handle.Physics;
import ea.internal.ano.NoExternalUse;
import ea.internal.util.Logger;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.MassData;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

/**
 * Ein <code>Body-Handler</code> kümmert sich um die <i>physikalische Darstellung</i> eines
 * <code>Actor</code>-Objekts.<br /> Er übernimmt zwei wesentliche Aufgaben:
 * <ul>
 * <li>Die Kontrolle und Steuerung innerhalb der <b>Physics-Engine</b> aus Sicht des respektiven Actor Objekts.</li>
 * <li>Die Speicherung der <i>räumlichen Eigenschaften</i> (Position und Rotation) des respektiven Actor-Objekts.</li>
 * </ul>
 */
public class BodyHandler extends PhysikHandler {

    /**
     * Referenz auf den Handler der World, in der sich der Body befindet.
     */
    private final WorldHandler worldHandler;

    /**
     * Die fixture (list) des Bodies.
     */
    private final Fixture fixture;

    /**
     * Der Body als die physische Repräsentation des analogen Actor-Objekts in der Physics-Engine.
     */
    private final Body body;

    /**
     * Erstellt einen neuen Body-Handler
     */
    @NoExternalUse
    public BodyHandler(Actor actor, WorldHandler worldHandler, BodyDef bodyDef, FixtureDef fixtureDef, Physics.Type physikType, boolean isSensor) {
        super(actor, physikType, isSensor);

        this.worldHandler = worldHandler;

        body = worldHandler.createBody(bodyDef, actor);
        fixture = body.createFixture(fixtureDef);
    }

    public Body getBody() {
        return body;
    }

    @Override
    public void setSensor(boolean isSensor) {
        this.isSensor = isSensor;
        this.body.getFixtureList().setSensor(isSensor);
    }

    @Override
    public void verschieben(Vector v) {
        Vec2 phyVec = worldHandler.fromVektor(v);
        body.setTransform(phyVec.add(body.getPosition()), body.getAngle());

        // Wake Up Body -> Ensure In-Engine (JB2D) Adjustments will happen, e.g. Collision Readjustment
        body.setAwake(true);
    }

    @Override
    public Vector mittelpunkt() {
        if (physikType == Physics.Type.DYNAMIC) {
            Vec2 wc = body.getWorldCenter();
            return worldHandler.fromVec2(wc);
        } else {
            AABB bodyAABB = calculateBodyAABB();
            return worldHandler.fromVec2(bodyAABB.getCenter());
        }
    }

    @Override
    public boolean beinhaltet(Vector p) {
        Fixture toTest = fixture;
        while (toTest != null) {
            if (toTest.testPoint(worldHandler.fromVektor(p))) {
                return true;
            }
            toTest = toTest.m_next;
        }
        return false;
    }

    @Override
    public Vector position() {
        return worldHandler.fromVec2(body.getPosition());
    }

    @Override
    public float rotation() {
        return body.getAngle();
    }

    @Override
    public void rotieren(float radians) {
        body.setTransform(body.getPosition(), body.getAngle() + radians);
    }

    @Override
    public void dichteSetzen(float dichte) {
        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            fixture.setDensity(dichte);
            fixture = fixture.getNext();
        }
    }

    @Override
    public float dichte() {
        return body.getFixtureList().getDensity();
    }

    @Override
    public void reibungSetzen(float reibung) {
        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            fixture.setFriction(reibung);
            fixture = fixture.getNext();
        }
    }

    @Override
    public float reibung() {
        return body.getFixtureList().getFriction();
    }

    @Override
    public void elastizitaetSetzen(float ela) {
        Fixture fixture = body.getFixtureList();
        if (fixture != null) {
            fixture.setRestitution(ela);
        }
    }

    @Override
    public float elastizitaet() {
        return body.getFixtureList().getRestitution();
    }

    @Override
    public void masseSetzen(float masse) {
        MassData md = new MassData();
        body.getMassData(md);
        md.mass = masse;
        body.setMassData(md);
    }

    @Override
    public float masse() {
        return body.getMass();
    }

    @Override
    public void kraftWirken(Vector kraft) {
        body.applyForceToCenter(new Vec2(kraft.x, kraft.y));
    }

    @Override
    public void drehMomentWirken(float drehmoment) {
        body.applyTorque(drehmoment);
    }

    @Override
    public void drehImpulsWirken(float drehimpuls) {
        body.applyAngularImpulse(drehimpuls);
    }

    @Override
    public void schwerkraftSetzen(Vector schwerkraftInN) {
        worldHandler.getWorld().setGravity(new Vec2(schwerkraftInN.x, schwerkraftInN.y));
        body.setAwake(true);
    }

    @Override
    public PhysikHandler typ(Physics.Type type) {
        if (type == physikType) {
            return this; // kein Update nötig
        }

        this.physikType = type;

        BodyType newType = type.convert();
        body.setType(newType);
        // isSensor = true; // TODO Delete again.

        body.setActive(true);
        fixture.setSensor(type == Physics.Type.PASSIVE);// && isSensor);
        body.setGravityScale(type == Physics.Type.PASSIVE ? 0 : 1);

        return this;
    }

    @Override
    public void kraftWirken(Vector kraftInN, Vector globalerOrt) {
        body.applyForce(new Vec2(kraftInN.x, kraftInN.y), worldHandler.fromVektor(globalerOrt));
    }

    @Override
    public void impulsWirken(Vector impulsInNS, Vector globalerOrt) {
        body.applyLinearImpulse(new Vec2(impulsInNS.x, impulsInNS.y), worldHandler.fromVektor(globalerOrt));
    }

    @Override
    public void physicalReset() {
        body.setLinearVelocity(new Vec2());
        body.setAngularVelocity(0);
    }

    @Override
    public void geschwindigkeitSetzen(Vector geschwindigkeitInMProS) {
        body.setLinearVelocity(new Vec2(geschwindigkeitInMProS.x, geschwindigkeitInMProS.y));
    }

    @Override
    public Vector geschwindigkeit() {
        return worldHandler.fromVec2(body.getLinearVelocity());
    }

    @Override
    public void rotationBlockiertSetzen(boolean block) {
        body.setFixedRotation(block);
    }

    @Override
    public boolean rotationBlockiert() {
        return body.isFixedRotation();
    }

    private AABB calculateBodyAABB() {
        AABB bodyBounds = new AABB();
        bodyBounds.lowerBound.x = Float.MAX_VALUE;
        bodyBounds.lowerBound.y = Float.MAX_VALUE;
        bodyBounds.upperBound.x = -Float.MAX_VALUE;
        bodyBounds.upperBound.y = -Float.MAX_VALUE;

        Fixture nextFixture = body.m_fixtureList;
        while (nextFixture != null) {
            // TODO Include chain shapes (more than one child)
            bodyBounds.combine(bodyBounds, nextFixture.getAABB(0));
            nextFixture = nextFixture.m_next;
        }

        return bodyBounds;
    }

    @Override
    public boolean testIfGrounded() {
        if (this.typ() != Physics.Type.DYNAMIC) {
            throw new RuntimeException("Der Steh-Test ist nur für dynamische Objekte definiert");
        }

        AABB bodyBounds = calculateBodyAABB();

        // Test-AABB: Should be a minimal space centered right below the Body
        AABB testAABB = new AABB();
        final float epsilon = 0.0001f;
        testAABB.lowerBound.set((bodyBounds.lowerBound.x + bodyBounds.upperBound.x) / 2 - epsilon, bodyBounds.lowerBound.y);
        testAABB.upperBound.set((bodyBounds.lowerBound.x + bodyBounds.upperBound.x) / 2 + epsilon,
                bodyBounds.lowerBound.y + 2 * epsilon);

        Fixture[] groundCandidates = worldHandler.aabbQuery(testAABB);
        for (Fixture fixture : groundCandidates) {
            Actor corresponding = worldHandler.bodyLookup(fixture.m_body);
            if (corresponding != null && corresponding.physics.getType() == Physics.Type.STATIC) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void killBody() {
        Logger.verboseInfo("Physics", "Entferne Actor-Objekt aus Physics-Umgebung.");
        worldHandler.removeAllInternalReferences(body);
        worldHandler.getWorld().destroyBody(body);
    }

    @Override
    public WorldHandler getWorldHandler() {
        return worldHandler;
    }
}
