package ea.internal.physics;

import ea.actor.Actor;
import ea.handle.Physics;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Diese Klasse wrappt die wesentlichen physikalischen Eigenschaften eines <code>Actor</code>-Objekts.
 */
public class ProxyData {

    /* _________________________________________ CONSTANTS _________________________________________ */

    private static final float DENSITY_DEFAULT = 30f;
    private static final float FRICTION_DEFAULT = 0f;
    private static final float RESTITUTION_DEFAULT = 0.5f;
    private static final Physics.Type PHYSICS_DEFAULT = Physics.Type.PASSIVE;

    /* _________________________________________ FIELDS _________________________________________ */

    boolean isSensor = false;
    boolean isRotationLocked = false;

    /**
     * Position
     */
    float x = 0, y = 0;

    /**
     * Rotation
     */
    float rot = 0;

    float density = DENSITY_DEFAULT, friction = FRICTION_DEFAULT, restitution = RESTITUTION_DEFAULT;

    float torque = 0, angular_v = 0;
    Vec2 v = new Vec2(0, 0);

    Physics.Type type = PHYSICS_DEFAULT;

    /**
     * Eine Funktion, die alle Shapes des zugehörigen Objektes relativ zu seiner Position beschreibt.
     */
    Supplier<List<Shape>> shapes;

    /**
     * Default-Konstruktor erstellt ein Proxydatenset mit Standardwerten.
     *
     * @param baseShape Eine Funktion, die eine gut abschätzende Shape für das zugehörige Actor-Objekt berechnet.
     */
    public ProxyData(Supplier<Shape> baseShape) {
        shapes = () -> {
            ArrayList<Shape> ret = new ArrayList<>();
            ret.add(baseShape.get());
            return ret;
        };
    }

    /**
     * Erstellt ein Proxydatenset basierend auf einem JBox2D-Body
     *
     * @param body Der zu kopierende Körper.
     */
    public ProxyData(Body body, Supplier<List<Shape>> shapes, Physics.Type type) {
        isRotationLocked = body.isFixedRotation();
        isSensor = body.m_fixtureList.isSensor();

        density = body.m_fixtureList.m_density;
        friction = body.m_fixtureList.m_friction;
        restitution = body.m_fixtureList.m_restitution;

        x = body.getPosition().x;
        y = body.getPosition().y;
        rot = body.getAngle();

        torque = body.m_torque;
        v = body.m_linearVelocity;
        angular_v = body.m_angularVelocity;
        this.type = type;
        this.shapes = shapes;
    }

    /**
     * Erstellt Fixture-Definitions für alle Shapes des Actors.
     */
    public FixtureDef[] createFixtureDefs() {
        ArrayList<FixtureDef> fixtureDefs = new ArrayList<>();
        List<Shape> shapeList = this.shapes.get();
        for (Shape shape : shapeList) {
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.density = this.density;
            fixtureDef.friction = this.friction;
            fixtureDef.restitution = this.restitution;
            fixtureDef.shape = shape;
            fixtureDef.isSensor = this.type == Physics.Type.PASSIVE;
            fixtureDefs.add(fixtureDef);
        }
        return fixtureDefs.toArray(new FixtureDef[fixtureDefs.size()]);
    }

    /**
     * Erstellt eine Body-Definition für den Actor
     */
    public BodyDef createBodyDef() {
        BodyDef bodyDef = new BodyDef();

        bodyDef.angle = this.rot;
        bodyDef.position.set(new Vec2(x, y));
        bodyDef.fixedRotation = isRotationLocked;
        bodyDef.linearVelocity = v;
        bodyDef.angularVelocity = angular_v;

        bodyDef.type = type.convert();
        bodyDef.active = true;
        bodyDef.gravityScale = type == Physics.Type.PASSIVE ? 0 : 1;

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
        FixtureDef[] fixtureDefs = createFixtureDefs();
        for (FixtureDef fixtureDef : fixtureDefs) {
            Fixture fixture = body.createFixture(fixtureDef);
        }
        return body;
    }
}