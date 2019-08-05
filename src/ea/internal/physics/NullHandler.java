package ea.internal.physics;

import ea.Vector;
import ea.actor.Actor;
import ea.handle.Physics;
import org.jbox2d.dynamics.Body;

/**
 * Default-Handler für Operationen an Actor-Objekten, die an keiner Scene angehängt sind.
 * Führt alle Operationen rein numerisch durch.
 * Gibt Fehler aus, wenn Operationen ausgeführt werden, die nur mit einer Verbindung zu einer
 * Physics World funktionieren können.
 */
public class NullHandler extends PhysicsHandler {

    private final ProxyData proxyData;

    public NullHandler(Actor actor, ProxyData proxyData) {
        super(actor);
        this.proxyData = proxyData;
    }

    @Override
    public void setSensor(boolean isSensor) {
        this.proxyData.isSensor = isSensor;
    }

    @Override
    public boolean isSensor(boolean isSensor) {
        return this.proxyData.isSensor;
    }

    @Override
    public void moveBy(Vector v) {
        this.proxyData.x += v.x;
        this.proxyData.y += v.y;
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
        return new Vector(this.proxyData.x, this.proxyData.y);
    }

    @Override
    public float getRotation() {
        return this.proxyData.rot;
    }

    @Override
    public void rotateBy(float radians) {
        this.proxyData.rot += radians;
    }

    @Override
    public void setDensity(float density) {
        if (density <= 0) {
            throw new IllegalArgumentException("Dichte kann nicht kleiner als 0 sein. Eingabe war " + density + ".");
        }
        this.proxyData.density = density;
    }

    @Override
    public float getDensity() {
        return this.proxyData.density;
    }

    @Override
    public void setFriction(float friction) {
        this.proxyData.friction = friction;
    }

    @Override
    public float getFriction() {
        return this.proxyData.friction;
    }

    @Override
    public void setRestitution(float elasticity) {
        this.proxyData.restitution = elasticity;
    }

    @Override
    public float getRestitution() {
        return this.proxyData.restitution;
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
    public PhysicsHandler setType(Physics.Type type) {
        this.proxyData.type = type;
        return this;
    }

    @Override
    public Physics.Type getType() {
        return proxyData.type;
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
    public void killBody() {
        // TODO sicher nichts zu tun?
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
        throw makeNullException("das Messen einer Geschwindigkeit");
    }

    @Override
    public void setRotationLocked(boolean block) {
        this.proxyData.isRotationLocked = block;
    }

    @Override
    public boolean isRotationLocked() {
        return this.proxyData.isRotationLocked;
    }

    @Override
    public boolean isGrounded() {
        throw makeNullException("das Prüfen auf Stehen");
    }

    @Override
    public float getTorque() {
        throw makeNullException("das Messen eines Drehmomentes");
    }

    @Override
    public void setTorque(float value) {
        throw makeNullException("das Setzen eines Drehmomentes");
    }

    @Override
    public ProxyData getProxyData() {
        return this.proxyData;
    }
}
