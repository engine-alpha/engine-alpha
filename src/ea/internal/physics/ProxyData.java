package ea.internal.physics;

import ea.actor.Actor;
import ea.handle.BodyType;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Diese Klasse wrappt die wesentlichen physikalischen Eigenschaften eines <code>Actor</code>-Objekts.
 */
public class ProxyData {

    private static final float DENSITY_DEFAULT = 30f;
    private static final float FRICTION_DEFAULT = 0f;
    private static final float RESTITUTION_DEFAULT = 0.5f;
    private static final BodyType PHYSICS_DEFAULT = BodyType.PASSIVE;

    private boolean rotationLocked = false;

    private float x = 0;
    private float y = 0;

    private float rotation = 0;

    private float density = DENSITY_DEFAULT;
    private float friction = FRICTION_DEFAULT;
    private float restitution = RESTITUTION_DEFAULT;

    private float torque = 0;
    private float angularVelocity = 0;

    private Vec2 velocity = new Vec2(0, 0);

    private BodyType type = PHYSICS_DEFAULT;

    private Supplier<List<Shape>> shapes;

    /**
     * Default-Konstruktor erstellt ein Proxydatenset mit Standardwerten.
     *
     * @param baseShape Eine Funktion, die eine gut abschätzende Shape für das zugehörige Actor-Objekt berechnet.
     */
    public ProxyData(Supplier<Shape> baseShape) {
        setShapes(() -> Collections.singletonList(baseShape.get()));
    }

    /**
     * Erstellt ein Proxydatenset basierend auf einem JBox2D-Body
     *
     * @param body Der zu kopierende Körper.
     */
    public ProxyData(Body body, Supplier<List<Shape>> shapes, BodyType type) {
        setRotationLocked(body.isFixedRotation());

        setDensity(body.m_fixtureList.m_density);
        setFriction(body.m_fixtureList.m_friction);
        setRestitution(body.m_fixtureList.m_restitution);

        setX(body.getPosition().x);
        setY(body.getPosition().y);
        setRotation(body.getAngle());

        setTorque(body.m_torque);
        setVelocity(body.m_linearVelocity);
        setAngularVelocity(body.m_angularVelocity);
        setType(type);
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

        bodyDef.angle = this.getRotation();
        bodyDef.position.set(new Vec2(getX(), getY()));
        bodyDef.fixedRotation = isRotationLocked();
        bodyDef.linearVelocity = getVelocity();
        bodyDef.angularVelocity = getAngularVelocity();

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

    public Vec2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vec2 velocity) {
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