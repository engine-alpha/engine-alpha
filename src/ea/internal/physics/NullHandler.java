package ea.internal.physics;

import ea.Vector;
import ea.actor.Actor;
import ea.actor.BodyType;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.Body;

import java.util.List;
import java.util.function.Supplier;

/**
 * Default-Handler für Operationen an Actor-Objekten, die an keiner Scene angehängt sind.
 * Führt alle Operationen rein numerisch durch.
 * Gibt Fehler aus, wenn Operationen ausgeführt werden, die nur mit einer Verbindung zu einer
 * Physics World funktionieren können.
 */
public class NullHandler extends PhysicsHandler {

    private final PhysicsData physicsData;

    public NullHandler(Actor actor, PhysicsData physicsData) {
        super(actor);
        this.physicsData = physicsData;
    }

    @Override
    public void moveBy(Vector v) {
        this.physicsData.setX(this.physicsData.getX() + v.x);
        this.physicsData.setY(this.physicsData.getY() + v.y);
    }

    /**
     * Zentrum eines Objekts ohne Physik ist seine Positon
     */
    @Override
    public Vector getCenter() {
        return getPosition();
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
    public void setDensity(float density) {
        if (density <= 0) {
            throw new IllegalArgumentException("Dichte kann nicht kleiner als 0 sein. Eingabe war " + density + ".");
        }
        this.physicsData.setDensity(density);
    }

    @Override
    public float getDensity() {
        return this.physicsData.getDensity();
    }

    @Override
    public void setFriction(float friction) {
        this.physicsData.setFriction(friction);
    }

    @Override
    public float getFriction() {
        return this.physicsData.getFriction();
    }

    @Override
    public void setRestitution(float elasticity) {
        this.physicsData.setRestitution(elasticity);
    }

    @Override
    public float getRestitution() {
        return this.physicsData.getRestitution();
    }

    @Override
    public void setMass(float mass) {
        // TODO What to do here?
    }

    @Override
    public float getMass() {
        return 0;
    }

    @Override
    public void applyForce(Vector force) {
        throw makeNullException("das Wirken einer Kraft");
    }

    /**
     * Gibt eine Exception aus, die alle Fehlverhalten aufgrund Nichtanmeldung an einer Scene betrifft.
     *
     * @param ex Beispiel für den Fehlertext
     *
     * @return Die Exception, ready to throw.
     */
    private IllegalStateException makeNullException(String ex) {
        return new IllegalStateException("Physikalische Manipulation (wie zum Beispiel " + ex + ") " + "können nur an Objekten ausgeführt werden, die in einer Scene aktiv sind.");
    }

    @Override
    public void applyTorque(float rotationMomentum) {
        throw makeNullException("das Wirken eines Drehmoments");
    }

    @Override
    public void applyRotationImpulse(float rotationImpulse) {
        throw makeNullException("das Wirken eines Drehimpulses");
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
    public void applyForce(Vector kraftInN, Vector globalerOrt) {
        throw makeNullException("das Wirken einer Kraft");
    }

    @Override
    public void applyImpluse(Vector impulsInNS, Vector globalerOrt) {
        throw makeNullException("das Wirken eines Impulses");
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
        //nichts zu tun
    }

    @Override
    public void setVelocity(Vector geschwindigkeitInMProS) {
        throw makeNullException("das Setzen einer Geschwindigkeit");
    }

    @Override
    public Vector getVelocity() {
        return Vector.NULL;
    }

    @Override
    public void setRotationLocked(boolean block) {
        this.physicsData.setRotationLocked(block);
    }

    @Override
    public boolean isRotationLocked() {
        return this.physicsData.isRotationLocked();
    }

    @Override
    public boolean isGrounded() {
        throw makeNullException("das Prüfen auf Stehen");
    }

    @Override
    public float getTorque() {
        return 0;
    }

    @Override
    public void setTorque(float value) {
        throw makeNullException("das Setzen eines Drehmomentes");
    }

    @Override
    public void setShapes(Supplier<List<Shape>> shapes) {
        physicsData.setShapes(shapes);
    }

    @Override
    public PhysicsData getPhysicsData() {
        return this.physicsData;
    }
}
