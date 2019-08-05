package ea.internal.physics;

import ea.Vector;
import ea.actor.Actor;
import ea.handle.Physics;
import ea.internal.annotations.Internal;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.MassData;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

import java.util.ArrayList;
import java.util.List;

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
    private static final int DEFAULT_MASK_BITS = 0xFFFF;

    /**
     * Referenz auf den Handler der World, in der sich der Body befindet.
     */
    private final WorldHandler worldHandler;

    /**
     * Der Body als die physische Repräsentation des analogen Actor-Objekts in der Physics-Engine.
     */
    private final Body body;

    private Physics.Type type;

    /**
     * Erstellt einen neuen Body-Handler
     */
    @Internal
    public BodyHandler(Actor actor, ProxyData proxyData, WorldHandler worldHandler) {
        super(actor);

        this.worldHandler = worldHandler;
        this.body = proxyData.createBody(worldHandler, actor);

        setType(proxyData.type);
    }

    public Body getBody() {
        return body;
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
        if (type == Physics.Type.DYNAMIC || type == Physics.Type.PARTICLE) {
            return Vector.of(body.getWorldCenter());
        }

        return Vector.of(calculateBodyAABB().getCenter());
    }

    @Override
    public boolean contains(Vector vector) {
        Vec2 point = vector.toVec2();

        Fixture current = body.m_fixtureList;
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
        return body.m_fixtureList.getDensity();
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
        return body.m_fixtureList.getFriction();
    }

    @Override
    public void setRestitution(float elasticity) {
        Fixture current = body.m_fixtureList;
        while (current != null) {
            current.setRestitution(elasticity);
            current = current.m_next;
        }
    }

    @Override
    public float getRestitution() {
        return body.m_fixtureList.getRestitution();
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
    public void applyTorque(float rotationMomentum) {
        body.applyTorque(rotationMomentum);
    }

    @Override
    public void applyRotationImpulse(float rotationImpulse) {
        body.applyAngularImpulse(rotationImpulse);
    }

    @Override
    public void setType(Physics.Type type) {
        WorldHandler.assertNoWorldStep();

        if (type == this.type) {
            return;
        }

        this.type = type;

        body.setType(type.convert());
        body.setActive(true);
        body.setGravityScale(type == Physics.Type.PASSIVE || type == Physics.Type.PARTICLE ? 0 : 1);

        Fixture current = this.body.m_fixtureList;
        while (current != null) {
            switch (type) {
                case PASSIVE:
                    current.m_filter.categoryBits = WorldHandler.CATEGORY_PASSIVE;
                    current.m_filter.maskBits = DEFAULT_MASK_BITS & ~WorldHandler.CATEGORY_PARTICLE;
                    current.m_isSensor = true;
                case STATIC:
                    current.m_filter.categoryBits = WorldHandler.CATEGORY_STATIC;
                    current.m_filter.maskBits = DEFAULT_MASK_BITS;
                    current.m_isSensor = false;
                    break;
                case DYNAMIC:
                case KINEMATIC:
                    current.m_filter.categoryBits = WorldHandler.CATEGORY_DYNAMIC_OR_KINEMATIC;
                    current.m_filter.maskBits = DEFAULT_MASK_BITS & ~WorldHandler.CATEGORY_PARTICLE;
                    current.m_isSensor = false;
                    break;
                case PARTICLE:
                    current.m_filter.categoryBits = WorldHandler.CATEGORY_PARTICLE;
                    current.m_filter.maskBits = WorldHandler.CATEGORY_STATIC;
                    current.m_isSensor = false;
                    break;
                default:
                    throw new RuntimeException("Unknown body type: " + type);
            }

            current = current.m_next;
        }
    }

    @Override
    public Physics.Type getType() {
        return type;
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

        Fixture current = body.m_fixtureList;
        while (current != null) {
            // TODO Include chain shapes (more than one child)
            bodyBounds.combine(bodyBounds, current.getAABB(0));
            current = current.m_next;
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
    @Internal
    public ProxyData getProxyData() {
        final List<Shape> shapeList = new ArrayList<>();
        Fixture fixture = body.m_fixtureList;
        while (fixture != null) {
            shapeList.add(fixture.m_shape);
            fixture = fixture.m_next;
        }
        return new ProxyData(body, () -> shapeList, getType());
    }

    @Override
    public WorldHandler getWorldHandler() {
        return worldHandler;
    }
}
