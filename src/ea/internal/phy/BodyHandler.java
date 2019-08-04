package ea.internal.phy;

import ea.Vector;
import ea.actor.Actor;
import ea.handle.Physics;
import ea.internal.annotations.Internal;
import ea.internal.util.Logger;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.MassData;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

/**
 * Ein <code>Body-Handler</code> kümmert sich um die <i>physikalische Darstellung</i> eines
 * <code>Actor</code>-Objekts.<br> Er übernimmt zwei wesentliche Aufgaben:
 * <ul>
 * <li>Die Kontrolle und Steuerung innerhalb der <b>Physics-Engine</b> aus Sicht des respektiven Actor Objekts.</li>
 * <li>Die Speicherung der <i>räumlichen Eigenschaften</i> (Position und Rotation) des respektiven Actor-Objekts.</li>
 * </ul>
 */
public class BodyHandler extends PhysicsHandler {
    private static final Vec2 NULL_VECTOR = new Vec2();

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
    @Internal
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

        Fixture current = fixture;
        while (current != null) {
            current.setSensor(isSensor);
            current = current.m_next;
        }
    }

    @Override
    public void moveBy(Vector meters) {
        WorldHandler.assertNoWorldStep();

        Vec2 vector = meters.toVec2();
        body.setTransform(vector.addLocal(body.getPosition()), body.getAngle());

        // Wake up body, ensures in-engine (JB2D) adjustments will happen, e.g. collision rejustment
        body.setAwake(true);
    }

    @Override
    public Vector getCenter() {
        if (type == Physics.Type.DYNAMIC) {
            return Vector.of(body.getWorldCenter());
        }

        return Vector.of(calculateBodyAABB().getCenter());
    }

    @Override
    public boolean contains(Vector vector) {
        Vec2 point = vector.toVec2();

        Fixture current = fixture;
        while (current != null) {
            if (current.testPoint(point)) {
                return true;
            }

            current = current.m_next;
        }

        return false;
    }

    @Override
    public Vector getPosition() {
        return Vector.of(body.getPosition());
    }

    @Override
    public float getRotation() {
        return body.getAngle();
    }

    @Override
    public void rotateBy(float radians) {
        WorldHandler.assertNoWorldStep();

        body.setTransform(body.getPosition(), body.getAngle() + radians);
    }

    @Override
    public void setDensity(float density) {
        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            fixture.setDensity(density);
            fixture = fixture.getNext();
        }
    }

    @Override
    public float getDensity() {
        return fixture.getDensity();
    }

    @Override
    public void setFriction(float friction) {
        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            fixture.setFriction(friction);
            fixture = fixture.getNext();
        }
    }

    @Override
    public float getFriction() {
        return fixture.getFriction();
    }

    @Override
    public void setRestitution(float elasticity) {
        Fixture current = fixture;
        while (current != null) {
            current.setRestitution(elasticity);
            current = current.m_next;
        }
    }

    @Override
    public float getRestitution() {
        return fixture.getRestitution();
    }

    @Override
    public void setMass(float mass) {
        WorldHandler.assertNoWorldStep();

        MassData massData = new MassData();
        body.getMassData(massData);
        massData.mass = mass;
        body.setMassData(massData);
    }

    @Override
    public float getMass() {
        return body.getMass();
    }

    @Override
    public void applyForce(Vector force) {
        body.applyForceToCenter(force.toVec2());
    }

    @Override
    public void applyRotationMomentum(float rotationMomentum) {
        body.applyTorque(rotationMomentum);
    }

    @Override
    public void applyRotationImpulse(float rotationImpulse) {
        body.applyAngularImpulse(rotationImpulse);
    }

    @Override
    public PhysicsHandler setType(Physics.Type type) {
        WorldHandler.assertNoWorldStep();

        if (type == this.type) {
            return this; // kein Update nötig
        }

        this.type = type;

        BodyType newType = type.convert();
        body.setType(newType);
        // isSensor = true; // TODO Delete again.

        body.setActive(true);
        fixture.setSensor(type == Physics.Type.PASSIVE);// && isSensor);
        body.setGravityScale(type == Physics.Type.PASSIVE ? 0 : 1);

        return this;
    }

    @Override
    public void applyForce(Vector forceInN, Vector globalLocation) {
        body.applyForce(forceInN.toVec2(), globalLocation.toVec2());
    }

    @Override
    public void applyImpluse(Vector impluseInNs, Vector globalLocation) {
        body.applyLinearImpulse(impluseInNs.toVec2(), globalLocation.toVec2());
    }

    @Override
    public void resetMovement() {
        body.setLinearVelocity(NULL_VECTOR);
        body.setAngularVelocity(0);
    }

    @Override
    public void setVelocity(Vector metersPerSecond) {
        body.setLinearVelocity(metersPerSecond.toVec2());
    }

    @Override
    public Vector getVelocity() {
        return Vector.of(body.getLinearVelocity());
    }

    @Override
    public void setRotationLocked(boolean block) {
        body.setFixedRotation(block);
    }

    @Override
    public boolean isRotationLocked() {
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
    public boolean isGrounded() {
        if (this.getType() != Physics.Type.DYNAMIC) {
            throw new RuntimeException("Der Steh-Test ist nur für dynamische Objekte definiert");
        }

        AABB bodyBounds = calculateBodyAABB();

        // Test-AABB: Should be a minimal space centered right below the Body
        AABB testAABB = new AABB();
        final float epsilon = 0.0001f;
        testAABB.lowerBound.set((bodyBounds.lowerBound.x + bodyBounds.upperBound.x) / 2 - epsilon, bodyBounds.lowerBound.y);
        testAABB.upperBound.set((bodyBounds.lowerBound.x + bodyBounds.upperBound.x) / 2 + epsilon, bodyBounds.lowerBound.y + 2 * epsilon);

        Fixture[] groundCandidates = worldHandler.aabbQuery(testAABB);
        for (Fixture fixture : groundCandidates) {
            Actor corresponding = worldHandler.lookupActor(fixture.m_body);
            if (corresponding != null && corresponding.getBodyType() == Physics.Type.STATIC) {
                return true;
            }
        }

        return false;
    }

    @Override
    public float getTorque() {
        return body.m_torque;
    }

    @Override
    public void setTorque(float value) {
        body.m_torque = value;
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
