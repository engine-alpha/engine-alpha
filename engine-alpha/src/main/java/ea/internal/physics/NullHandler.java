package ea.internal.physics;

import ea.Vector;
import ea.actor.Actor;
import ea.actor.BodyType;
import ea.collision.CollisionEvent;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Transform;
import org.jbox2d.dynamics.Body;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Default-Handler für Operationen an Actor-Objekten, die an keiner Scene angehängt sind.
 * Führt alle Operationen rein numerisch durch.
 * Gibt Fehler aus, wenn Operationen ausgeführt werden, die nur mit einer Verbindung zu einer
 * Physics World funktionieren können.
 */
public class NullHandler implements PhysicsHandler {

    private final PhysicsData physicsData;
    private final Collection<Consumer<PhysicsHandler>> mountCallbacks = new ArrayList<>();

    public NullHandler(PhysicsData physicsData) {
        this.physicsData = physicsData;
    }

    @Override
    public void moveBy(Vector v) {
        this.physicsData.setX(this.physicsData.getX() + v.getX());
        this.physicsData.setY(this.physicsData.getY() + v.getY());
    }

    @Override
    public Vector getCenter() {
        AABB bounds = null;
        AABB shapeBounds = new AABB();
        Transform transform = new Transform();

        for (FixtureData fixtureData : physicsData.getFixtures().get()) {
            transform.set(getPosition().toVec2(), (float) Math.toRadians(getRotation()));
            fixtureData.getShape().computeAABB(shapeBounds, transform, 0);

            if (bounds != null) {
                bounds.combine(shapeBounds);
            } else {
                bounds = new AABB();
                bounds.set(shapeBounds);
            }
        }

        return Vector.of(bounds.getCenter());
    }

    /**
     * Ein Objekt ohne Physik enthält keinen Punkt.
     *
     * @param p Ein Point auf der Zeichenebene.
     *
     * @return false
     */
    @Override
    public boolean contains(Vector p) {
        return false;
    }

    @Override
    public Vector getPosition() {
        return new Vector(this.physicsData.getX(), this.physicsData.getY());
    }

    @Override
    public float getRotation() {
        return this.physicsData.getRotation();
    }

    @Override
    public void rotateBy(float degree) {
        this.physicsData.setRotation(this.physicsData.getRotation() + degree);
    }

    @Override
    public void setRotation(float degree) {
        this.physicsData.setRotation(degree);
    }

    @Override
    public void setDensity(float density) {
        if (density <= 0) {
            throw new IllegalArgumentException("Dichte kann nicht kleiner als 0 sein. Eingabe war " + density + ".");
        }
        this.physicsData.setGlobalDensity(density);
    }

    @Override
    public float getDensity() {
        return this.physicsData.getGlobalDensity();
    }

    @Override
    public void setGravityScale(float factor) {
        this.physicsData.setGravityScale(factor);
    }

    @Override
    public float getGravityScale() {
        return this.physicsData.getGravityScale();
    }

    @Override
    public void setFriction(float friction) {
        this.physicsData.setGlobalFriction(friction);
    }

    @Override
    public float getFriction() {
        return this.physicsData.getGlobalFriction();
    }

    @Override
    public void setRestitution(float elasticity) {
        this.physicsData.setGlobalRestitution(elasticity);
    }

    @Override
    public float getRestitution() {
        return this.physicsData.getGlobalRestitution();
    }

    @Override
    public void setLinearDamping(float damping) {
        this.physicsData.setLinearDamping(damping);
    }

    @Override
    public float getLinearDamping() {
        return physicsData.getLinearDamping();
    }

    @Override
    public void setAngularDamping(float damping) {
        physicsData.setAngularDamping(damping);
    }

    @Override
    public float getAngularDamping() {
        return physicsData.getAngularDamping();
    }

    @Override
    public float getMass() {
        Float mass = physicsData.getMass();
        return mass == null ? 0 : mass;
    }

    @Override
    public void applyForce(Vector force) {
        mountCallbacks.add(physicsHandler -> physicsHandler.applyForce(force));
    }

    @Override
    public void applyTorque(float torque) {
        mountCallbacks.add(physicsHandler -> physicsHandler.applyTorque(torque));
    }

    @Override
    public void applyRotationImpulse(float rotationImpulse) {
        mountCallbacks.add(physicsHandler -> physicsHandler.applyRotationImpulse(rotationImpulse));
    }

    @Override
    public void setType(BodyType type) {
        this.physicsData.setType(type);
    }

    @Override
    public BodyType getType() {
        return physicsData.getType();
    }

    @Override
    public void applyForce(Vector force, Vector globalLocation) {
        mountCallbacks.add(physicsHandler -> physicsHandler.applyForce(force, globalLocation));
    }

    @Override
    public void applyImpulse(Vector impulse, Vector globalLocation) {
        mountCallbacks.add(physicsHandler -> physicsHandler.applyImpulse(impulse, globalLocation));
    }

    @Override
    public WorldHandler getWorldHandler() {
        return null;
    }

    @Override
    public Body getBody() {
        return null;
    }

    @Override
    public void resetMovement() {
        physicsData.setVelocity(Vector.NULL);
        physicsData.setAngularVelocity(0);
    }

    @Override
    public void setVelocity(Vector metersPerSecond) {
        physicsData.setVelocity(metersPerSecond);
    }

    @Override
    public Vector getVelocity() {
        return physicsData.getVelocity();
    }

    @Override
    public void setAngularVelocity(float rotationsPerSecond) {
        physicsData.setAngularVelocity((float) Math.toRadians(rotationsPerSecond * 360));
    }

    @Override
    public float getAngularVelocity() {
        return physicsData.getAngularVelocity();
    }

    @Override
    public void setRotationLocked(boolean locked) {
        this.physicsData.setRotationLocked(locked);
    }

    @Override
    public boolean isRotationLocked() {
        return this.physicsData.isRotationLocked();
    }

    @Override
    public boolean isGrounded() {
        return false;
    }

    @Override
    public void setFixtures(Supplier<List<FixtureData>> shapes) {
        physicsData.setFixtures(shapes);
    }

    @Override
    public PhysicsData getPhysicsData() {
        return this.physicsData;
    }

    @Override
    public void applyMountCallbacks(PhysicsHandler otherHandler) {
        for (Consumer<PhysicsHandler> mountCallback : mountCallbacks) {
            mountCallback.accept(otherHandler);
        }

        mountCallbacks.clear();
    }

    @Override
    public List<CollisionEvent<Actor>> getCollisions() {
        return Collections.emptyList();
    }
}
