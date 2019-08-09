package ea.internal.physics;

import ea.Vector;
import ea.actor.Actor;
import ea.actor.BodyType;
import ea.internal.annotations.Internal;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Diese Klasse wrappt die wesentlichen physikalischen Eigenschaften eines <code>Actor</code>-Objekts.
 */
@Internal
public class PhysicsData {

    private static final float DEFAULT_DENSITY = 30f;
    private static final float DEFAULT_FRICTION = 0f;
    private static final float DEFAULT_RESTITUTION = 0.5f;
    private static final BodyType DEFAULT_BODY_TYPE = BodyType.SENSOR;

    private boolean rotationLocked = false;

    private float x = 0;
    private float y = 0;

    private float rotation = 0;

    private float density = DEFAULT_DENSITY;
    private float friction = DEFAULT_FRICTION;
    private float restitution = DEFAULT_RESTITUTION;
    private float torque = 0;
    private float angularVelocity = 0;

    private Float mass;

    private Vector velocity = Vector.NULL;

    private BodyType type = DEFAULT_BODY_TYPE;

    private Supplier<List<Shape>> shapes;

    /**
     * Erstellt ein Proxydatenset basierend auf einem JBox2D-Body
     *
     * @param body Der zu kopierende Körper.
     */
    public static PhysicsData fromBody(Body body, Supplier<List<Shape>> shapes, BodyType type) {
        PhysicsData data = new PhysicsData(shapes);
        data.setRotationLocked(body.isFixedRotation());
        data.setDensity(body.m_fixtureList.m_density);
        data.setFriction(body.m_fixtureList.m_friction);
        data.setRestitution(body.m_fixtureList.m_restitution);
        data.setX(body.getPosition().x);
        data.setY(body.getPosition().y);
        data.setRotation((float) Math.toDegrees(body.getAngle()));
        data.setTorque(body.m_torque);
        data.setVelocity(Vector.of(body.m_linearVelocity));
        data.setAngularVelocity((float) Math.toDegrees(body.m_angularVelocity) / 360);
        data.setType(type);
        data.setShapes(shapes);

        return data;
    }

    /**
     * Default-Konstruktor erstellt ein Proxydatenset mit Standardwerten.
     *
     * @param shapes Eine Funktion, die eine gut abschätzende Shape für das zugehörige Actor-Objekt berechnet.
     */
    public PhysicsData(Supplier<List<Shape>> shapes) {
        setShapes(shapes);
    }

    /**
     * Erstellt Fixture-Definitions für alle Shapes des Actors.
     */
    public FixtureDef[] createFixtureDefs() {
        List<FixtureDef> fixtureDefs = new ArrayList<>();
        List<Shape> shapeList = this.getShapes().get();

        for (Shape shape : shapeList) {
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.density = this.getDensity();
            fixtureDef.friction = this.getFriction();
            fixtureDef.restitution = this.getRestitution();
            fixtureDef.shape = shape;
            fixtureDef.isSensor = this.getType().isSensorType();
            fixtureDefs.add(fixtureDef);
        }

        return fixtureDefs.toArray(new FixtureDef[0]);
    }

    /**
     * Erstellt eine FixtureDef OHNE SHAPE
     */
    public FixtureDef createPlainFixtureDef() {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = this.getDensity();
        fixtureDef.friction = this.getFriction();
        fixtureDef.restitution = this.getRestitution();
        fixtureDef.isSensor = this.getType().isSensorType();

        return fixtureDef;
    }

    /**
     * Erstellt eine Body-Definition für den Actor
     */
    public BodyDef createBodyDef() {
        BodyDef bodyDef = new BodyDef();

        bodyDef.angle = (float) Math.toRadians(this.getRotation());
        bodyDef.position.set(new Vec2(getX(), getY()));
        bodyDef.fixedRotation = isRotationLocked();
        bodyDef.linearVelocity = getVelocity().toVec2();
        bodyDef.angularVelocity = (float) Math.toRadians(getAngularVelocity() * 360);
        bodyDef.type = getType().toBox2D();
        bodyDef.active = true;
        bodyDef.gravityScale = getType().getDefaultGravityScale();

        return bodyDef;
    }

    /**
     * Erstellt einen vollständigen Body basierend auf allen ProxyDaten.
     * <b>Macht keine Prüfungen, ob ein entsprechender Body bereits gebaut wurde.</b>
     *
     * @param world Der World-Handler, in dessen World der Body erstellt werden soll.
     *
     * @return Der frisch erstellte Body nach allen Spezifikationen der Proxy-Daten.
     */
    Body createBody(WorldHandler world, Actor actor) {
        Body body = world.createBody(createBodyDef(), actor);

        for (FixtureDef fixtureDef : createFixtureDefs()) {
            body.createFixture(fixtureDef);
        }

        return body;
    }

    public void setMass(Float mass) {
        this.mass = mass;
    }

    public Float getMass() {
        return mass;
    }

    public boolean isRotationLocked() {
        return rotationLocked;
    }

    public void setRotationLocked(boolean rotationLocked) {
        this.rotationLocked = rotationLocked;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public float getRestitution() {
        return restitution;
    }

    public void setRestitution(float restitution) {
        this.restitution = restitution;
    }

    public float getTorque() {
        return torque;
    }

    public void setTorque(float torque) {
        this.torque = torque;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public BodyType getType() {
        return type;
    }

    public void setType(BodyType type) {
        this.type = type;
    }

    public Supplier<List<Shape>> getShapes() {
        return shapes;
    }

    public void setShapes(Supplier<List<Shape>> shapes) {
        this.shapes = shapes;
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }
}