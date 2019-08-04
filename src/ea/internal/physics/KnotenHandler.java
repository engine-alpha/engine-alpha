package ea.internal.physics;

import ea.Vector;
import ea.actor.ActorGroup;
import ea.handle.Physics;
import ea.internal.util.Logger;
import org.jbox2d.dynamics.Body;

/**
 * Spezieller Physics-Handler für ActorGroup.
 *
 * @author Michael Andonie
 */
public class KnotenHandler extends PhysicsHandler {
    private final ActorGroup actorGroup;

    /**
     * Symbolische Position des Knotens.
     */
    private Vector position = Vector.NULL;

    /**
     * Symbolische Rotation des Knotens
     */
    private float rotation = 0f;

    /**
     * Initialisiert den Physics-Handler.
     *
     * @param raum Der ActorGroup, um den sich dieser Handler kümmert.
     */
    public KnotenHandler(ActorGroup raum) {
        super(raum, Physics.Type.PASSIVE, false);
        actorGroup = raum;
    }

    @Override
    public void setSensor(boolean isSensor) {
        //
    }

    @Override
    public void moveBy(Vector v) {
        position = position.add(v);
        actorGroup.forEach(r -> r.position.move(v));
    }

    @Override
    public Vector getCenter() {
        return null;
    }

    @Override
    public boolean contains(Vector p) {
        return false;
    }

    @Override
    public Vector getPosition() {
        return position;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public void rotateBy(float radians) {
        rotation += radians;
        actorGroup.forEach(r -> r.position.setRotation(radians));
    }

    @Override
    public void setDensity(float density) {
        Logger.error("Physics", "Ein ActorGroup hat keine Dichte");
    }

    @Override
    public float getDensity() {
        Logger.error("Physics", "Ein ActorGroup hat keine Dichte");
        return 0;
    }

    @Override
    public void setFriction(float friction) {
        Logger.error("Physics", "Ein ActorGroup hat keine Reibung");
    }

    @Override
    public float getFriction() {
        Logger.error("Physics", "Ein ActorGroup hat keine Reibung");
        return 0;
    }

    @Override
    public void setRestitution(float elasticity) {
        Logger.error("Physics", "Ein ActorGroup hat keine Elastizität");
    }

    @Override
    public float getRestitution() {
        Logger.error("Physics", "Ein ActorGroup hat keine Elastizität");
        return 0;
    }

    @Override
    public void setMass(float mass) {
        Logger.error("Physics", "Ein ActorGroup hat keine Masse");
    }

    @Override
    public float getMass() {
        Logger.error("Physics", "Ein ActorGroup hat keine Masse");
        return 0;
    }

    @Override
    public void applyForce(Vector force) {

    }

    @Override
    public void applyRotationMomentum(float rotationMomentum) {

    }

    @Override
    public void applyRotationImpulse(float rotationImpulse) {

    }

    @Override
    public PhysicsHandler setType(Physics.Type type) {
        return null;
    }

    @Override
    public void applyForce(Vector kraftInN, Vector globalerOrt) {

    }

    @Override
    public void applyImpluse(Vector impulsInNS, Vector globalerOrt) {

    }

    @Override
    public void killBody() {

    }

    @Override
    public WorldHandler getWorldHandler() {
        return actorGroup.getScene().getWorldHandler();
    }

    @Override
    public Body getBody() {
        return null;
    }

    @Override
    public void resetMovement() {
        actorGroup.forEach(r -> r.physics.cancelAll());
    }

    @Override
    public void setVelocity(Vector geschwindigkeitInMProS) {

    }

    @Override
    public Vector getVelocity() {
        return null;
    }

    @Override
    public void setRotationLocked(boolean block) {

    }

    @Override
    public boolean isRotationLocked() {
        return false;
    }

    @Override
    public boolean isGrounded() {
        return false;
    }

    @Override
    public float getTorque() {
        return 0;
    }

    @Override
    public void setTorque(float value) {

    }
}
