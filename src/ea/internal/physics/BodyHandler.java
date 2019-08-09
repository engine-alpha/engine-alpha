package ea.internal.physics;

import ea.Vector;
import ea.actor.Actor;
import ea.actor.BodyType;
import ea.internal.annotations.Internal;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.MassData;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Ein <code>Body-Handler</code> kümmert sich um die <i>physikalische Darstellung</i> eines
 * <code>Actor</code>-Objekts.<br> Er übernimmt zwei wesentliche Aufgaben:
 * <ul>
 * <li>Die Kontrolle und Steuerung innerhalb der <b>Physics-Engine</b> aus Sicht des respektiven Actor Objekts.</li>
 * <li>Die Speicherung der <i>räumlichen Eigenschaften</i> (Position und Rotation) des respektiven Actor-Objekts.</li>
 * </ul>
 */
public class BodyHandler implements PhysicsHandler {
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

    private BodyType type;

    /**
     * Erstellt einen neuen Body-Handler
     */
    @Internal
    public BodyHandler(Actor actor, PhysicsData physicsData, WorldHandler worldHandler) {
        this.worldHandler = worldHandler;
        this.body = physicsData.createBody(worldHandler, actor);

        setType(physicsData.getType());
    }

    public Body getBody() {
        return body;
    }

    @Override
    public void moveBy(Vector meters) {
        synchronized (worldHandler) {
            worldHandler.assertNoWorldStep();

            Vec2 vector = meters.toVec2();
            body.setTransform(vector.addLocal(body.getPosition()), body.getAngle());

            // Wake up body, ensures in-engine (JB2D) adjustments will happen, e.g. collision rejustment
            body.setAwake(true);
        }
    }

    @Override
    public Vector getCenter() {
        if (type == BodyType.DYNAMIC || type == BodyType.PARTICLE) {
            return Vector.of(body.getWorldCenter());
        }

        return Vector.of(calculateBodyAABB().getCenter());
    }

    @Override
    public boolean contains(Vector vector) {
        Vec2 point = vector.toVec2();

        for (Fixture fixture = body.m_fixtureList; fixture != null; fixture = fixture.m_next) {
            if (fixture.testPoint(point)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Vector getPosition() {
        return Vector.of(body.getPosition());
    }

    @Override
    public float getRotation() {
        return (float) Math.toDegrees(body.getAngle());
    }

    @Override
    public void rotateBy(float degree) {
        synchronized (worldHandler) {
            worldHandler.assertNoWorldStep();

            body.setTransform(body.getPosition(), body.getAngle() + (float) Math.toRadians(degree));
        }
    }

    @Override
    public void setRotation(float degree) {
        synchronized (worldHandler) {
            worldHandler.assertNoWorldStep();

            body.setTransform(body.getPosition(), (float) Math.toRadians((double) degree));
        }
    }

    @Override
    public void setDensity(float density) {
        synchronized (worldHandler) {
            for (Fixture fixture = body.m_fixtureList; fixture != null; fixture = fixture.m_next) {
                fixture.setDensity(density);
            }
        }
    }

    @Override
    public float getDensity() {
        return body.m_fixtureList.getDensity();
    }

    @Override
    public void setFriction(float friction) {
        synchronized (worldHandler) {
            for (Fixture fixture = body.m_fixtureList; fixture != null; fixture = fixture.m_next) {
                fixture.setFriction(friction);
            }
        }
    }

    @Override
    public float getFriction() {
        return body.m_fixtureList.getFriction();
    }

    @Override
    public void setRestitution(float elasticity) {
        synchronized (worldHandler) {
            for (Fixture fixture = body.m_fixtureList; fixture != null; fixture = fixture.m_next) {
                fixture.setRestitution(elasticity);
            }
        }
    }

    @Override
    public float getRestitution() {
        return body.m_fixtureList.getRestitution();
    }

    @Override
    public void setMass(float mass) {
        synchronized (worldHandler) {
            worldHandler.assertNoWorldStep();

            MassData massData = new MassData();
            body.getMassData(massData);
            massData.mass = mass;
            body.setMassData(massData);
        }
    }

    @Override
    public float getMass() {
        return body.getMass();
    }

    @Override
    public void applyForce(Vector force) {
        synchronized (worldHandler) {
            body.applyForceToCenter(force.toVec2());
        }
    }

    @Override
    public void applyTorque(float torque) {
        synchronized (worldHandler) {
            body.applyTorque(torque);
        }
    }

    @Override
    public void applyRotationImpulse(float rotationImpulse) {
        synchronized (worldHandler) {
            body.applyAngularImpulse(rotationImpulse);
        }
    }

    @Override
    public void setType(BodyType type) {
        synchronized (worldHandler) {
            worldHandler.assertNoWorldStep();

            if (type == this.type) {
                return;
            }

            this.type = type;

            body.setType(type.toBox2D());
            body.setActive(true);
            body.setAwake(true);
            body.setGravityScale(type.getDefaultGravityScale());

            for (Fixture fixture = body.m_fixtureList; fixture != null; fixture = fixture.m_next) {
                fixture.m_isSensor = type.isSensorType();

                switch (type) {
                    case SENSOR:
                        fixture.m_filter.categoryBits = WorldHandler.CATEGORY_PASSIVE;
                        fixture.m_filter.maskBits = DEFAULT_MASK_BITS & ~WorldHandler.CATEGORY_PARTICLE;
                        break;
                    case STATIC:
                        fixture.m_filter.categoryBits = WorldHandler.CATEGORY_STATIC;
                        fixture.m_filter.maskBits = DEFAULT_MASK_BITS;
                        break;
                    case DYNAMIC:
                    case KINEMATIC:
                        fixture.m_filter.categoryBits = WorldHandler.CATEGORY_DYNAMIC_OR_KINEMATIC;
                        fixture.m_filter.maskBits = DEFAULT_MASK_BITS & ~WorldHandler.CATEGORY_PARTICLE;
                        break;
                    case PARTICLE:
                        fixture.m_filter.categoryBits = WorldHandler.CATEGORY_PARTICLE;
                        fixture.m_filter.maskBits = WorldHandler.CATEGORY_STATIC;
                        break;
                    default:
                        throw new RuntimeException("Unknown body type: " + type);
                }
            }
        }
    }

    @Override
    public BodyType getType() {
        return type;
    }

    @Override
    public void applyForce(Vector forceInN, Vector globalLocation) {
        synchronized (worldHandler) {
            body.applyForce(forceInN.toVec2(), globalLocation.toVec2());
        }
    }

    @Override
    public void applyImpluse(Vector impluseInNs, Vector globalLocation) {
        synchronized (worldHandler) {
            body.applyLinearImpulse(impluseInNs.toVec2(), globalLocation.toVec2(), true);
        }
    }

    @Override
    public void resetMovement() {
        synchronized (worldHandler) {
            body.setLinearVelocity(NULL_VECTOR);
            body.setAngularVelocity(0);
        }
    }

    @Override
    public void setVelocity(Vector metersPerSecond) {
        synchronized (worldHandler) {
            body.setLinearVelocity(metersPerSecond.toVec2());
        }
    }

    @Override
    public Vector getVelocity() {
        return Vector.of(body.getLinearVelocity());
    }

    @Override
    public void setAngularVelocity(float rotationsPerSecond) {
        synchronized (worldHandler) {
            body.setAngularVelocity((float) Math.toRadians(rotationsPerSecond * 360));
        }
    }

    @Override
    public float getAngularVelocity() {
        return (float) Math.toDegrees(body.getAngularVelocity()) / 360;
    }

    @Override
    public void setRotationLocked(boolean locked) {
        synchronized (worldHandler) {
            body.setFixedRotation(locked);
        }
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

        for (Fixture fixture = body.m_fixtureList; fixture != null; fixture = fixture.m_next) {
            // TODO Include chain shapes (more than one child)
            bodyBounds.combine(bodyBounds, fixture.getAABB(0));
        }

        return bodyBounds;
    }

    @Override
    public boolean isGrounded() {
        if (this.getType() != BodyType.DYNAMIC) {
            throw new RuntimeException("Der Steh-Test ist nur für dynamische Objekte definiert");
        }

        AABB bodyBounds = calculateBodyAABB();

        // Test-AABB: Should be a minimal space centered right below the Body
        AABB testAABB = new AABB();
        final float epsilon = 0.0001f;
        testAABB.lowerBound.set((bodyBounds.lowerBound.x + bodyBounds.upperBound.x) / 2 - epsilon, bodyBounds.lowerBound.y);
        testAABB.upperBound.set((bodyBounds.lowerBound.x + bodyBounds.upperBound.x) / 2 + epsilon, bodyBounds.lowerBound.y + 2 * epsilon);

        Fixture[] groundCandidates = worldHandler.queryAABB(testAABB);
        for (Fixture fixture : groundCandidates) {
            Actor corresponding = worldHandler.lookupActor(fixture.m_body);
            if (corresponding != null && corresponding.getBodyType() == BodyType.STATIC) {
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
        synchronized (worldHandler) {
            body.m_torque = value;
        }
    }

    @Override
    public void setShapes(Supplier<List<Shape>> shapes) {
        synchronized (worldHandler) {
            PhysicsData physicsData = this.getPhysicsData();

            for (Fixture fixture = body.m_fixtureList; fixture != null; fixture = fixture.m_next) {
                body.destroyFixture(fixture);
            }

            FixtureDef fixtureDef = physicsData.createPlainFixtureDef();
            for (Shape shape : shapes.get()) {
                fixtureDef.shape = shape;
                body.createFixture(fixtureDef);
            }
        }
    }

    @Override
    @Internal
    public PhysicsData getPhysicsData() {
        final List<Shape> shapeList = new ArrayList<>();

        for (Fixture fixture = body.m_fixtureList; fixture != null; fixture = fixture.m_next) {
            shapeList.add(fixture.m_shape);
        }

        return PhysicsData.fromBody(body, () -> shapeList, getType());
    }

    @Override
    public void applyMountCallbacks(PhysicsHandler otherHandler) {
        // nothing to do
    }

    @Override
    public WorldHandler getWorldHandler() {
        return worldHandler;
    }
}
