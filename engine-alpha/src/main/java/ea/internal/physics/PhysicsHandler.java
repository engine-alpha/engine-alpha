package ea.internal.physics;

import ea.Vector;
import ea.actor.Actor;
import ea.actor.BodyType;
import ea.collision.CollisionEvent;
import ea.internal.annotations.Internal;
import org.jbox2d.dynamics.Body;

import java.util.List;
import java.util.function.Supplier;

/**
 * Beschreibt allgemein ein Objekt, dass die physikalischen Eigenschaften eines Actor-Objektes kontrollieren kann.
 * Dazu gehört:
 * <ul>
 * <li>Das <code>Actor</code>-Objekt <b>bewegen</b>.</li>
 * <li><b>Physikalische Eigenschaften</b> des Objektes verändern (wie Masse, Reibungskoeffizient etc.)</li>
 * <li><b>Einflüsse</b> auf das <code>Actor</code>-Objekt ausüben (wie anwenden von Impulsen / Kräften)</li>
 * </ul>
 * Created by andonie on 16.02.15.
 */
@Internal
public interface PhysicsHandler {
    /**
     * Verschiebt das Ziel-Objekt um einen spezifischen Wert auf der Zeichenebene. Die Ausführung hat <b>erst (ggf.) im
     * kommenden Frame</b> einfluss auf die Physics und <b>ändert keine physikalischen Eigenschaften</b> des
     * Ziel-Objekts
     * (außer dessen Ort).
     *
     * @param v Ein Vector, um den das Ziel-Objekt verschoben werden soll. Dies ändert seine Position, jedoch sonst
     *          keine weiteren Eigenschaften.
     */
    @Internal
    void moveBy(Vector v);

    /**
     * Gibt den <b>Gewichtsmittelpunkt</b> dieses <code>Actor</code>-Objekts aus.
     *
     * @return der aktuelle <b>Gewichtsmittelpunkt</b> des Ziel-Objekts als <i>Point auf der Zeichenebene</i>.
     */
    @Internal
    Vector getCenter();

    /**
     * Gibt an, ob ein bestimmter Point auf der Zeichenebene innerhalb des Ziel-Objekts liegt.
     *
     * @param p Ein Point auf der Zeichenebene.
     *
     * @return <code>true</code>, wenn der übergebene Point innerhalb des Ziel-Objekts liegt, sonst <code>false</code>.
     * Das Ergebnis kann (abhängig von der implementierenden Klasse) verschieden sicher richtige Ergebnisse
     * liefern.
     */
    @Internal
    boolean contains(Vector p);

    /**
     * Gibt die aktuelle Position des Ziel-Objekts an.
     *
     * @return Die aktuelle Position des Ziel-Objekts. Diese ist bei Erstellung des Objekts zunächst immer
     * <code>(0|0)</code> und wird mit Rotation und Verschiebung verändert.
     */
    @Internal
    Vector getPosition();

    /**
     * Gibt die aktuelle Rotation des Ziel-Objekts in <i>Grad</i> an. Bei Erstellung eines
     * <code>Actor</code>-Objekts ist seine Rotation stets 0.
     *
     * @return die aktuelle Rotation des Ziel-Objekts in <i>Grad</i>.
     */
    @Internal
    float getRotation();

    /**
     * Rotiert das Ziel-Objekt um einen festen Winkel.
     *
     * @param degree Der Winkel, um den das Ziel-Objekt gedreht werden soll (in <i>Grad</i>).
     *               <ul>
     *               <li>Werte &gt; 0 : Drehung gegen Uhrzeigersinn</li>
     *               <li>Werte &lt; 0 : Drehung im Uhrzeigersinn</li>
     *               </ul>
     */
    @Internal
    void rotateBy(float degree);

    @Internal
    void setRotation(float degree);

    @Internal
    void setDensity(float density);

    @Internal
    float getDensity();

    @Internal
    void setGravityScale(float factor);

    @Internal
    float getGravityScale();

    @Internal
    void setFriction(float friction);

    @Internal
    float getFriction();

    @Internal
    void setRestitution(float elasticity);

    @Internal
    float getRestitution();

    @Internal
    void setLinearDamping(float damping);

    @Internal
    float getLinearDamping();

    @Internal
    void setAngularDamping(float damping);

    @Internal
    float getAngularDamping();

    /**
     * Gibt die Masse des Ziel-Objekts aus.
     *
     * @return Die Masse des Ziel-Objekts in [kg].
     */
    @Internal
    float getMass();

    /**
     * Übt eine Kraft auf das Ziel-Objekt (im Massenschwerpunkt) aus (sofern möglich).
     *
     * @param force Die Kraft, die auf den Massenschwerpunkt angewandt werden soll. <b>Nicht in [px]</b>, sondern in
     *              [N] = [m / s^2].
     */
    @Internal
    void applyForce(Vector force);

    /**
     * Wirkt einen Drehmoment auf das Ziel-Objekt.
     *
     * @param torque der Drehmoment, der auf das Ziel-Objekt wirken soll. In [N*m]
     */
    @Internal
    void applyTorque(float torque);

    /**
     * Wirkt einen Drehimpuls auf das Ziel-Objekt.
     *
     * @param rotationImpulse der Drehimpuls, der auf das Ziel-Objekt wirken soll. in [kg*m*m/s]
     */
    @Internal
    void applyRotationImpulse(float rotationImpulse);

    /**
     * Macht ein Type-Update für diesen Handler.
     *
     * @param type Der neue Type.
     */
    @Internal
    void setType(BodyType type);

    @Internal
    BodyType getType();

    @Internal
    void applyForce(Vector kraftInN, Vector globalLocation);

    /**
     * Wirkt einen Impuls auf einem Welt-Point.
     *
     * @param impulsInNS     Ein Impuls (in [Ns]).
     * @param globalLocation TODO
     */
    @Internal
    void applyImpulse(Vector impulsInNS, Vector globalLocation);

    /**
     * Gibt den WorldHandler aus, der die Welt handelt, in der sich der Klient
     * befindet.
     *
     * @return Der World-Handler, der zu diesem Physics-Handler gehört.
     */
    @Internal
    WorldHandler getWorldHandler();

    /**
     * Wird intern zum Debuggen benutzt. Gibt den korrespondierenden Body aus.
     *
     * @return Der korrespondierende Body.
     */
    @Internal
    Body getBody();

    /**
     * Setzt die Wirkung aller physikalischer Bewegungen (Geschwindigkeit und Drehung) zurück.
     * Hiernach ist das Objekt in Ruhe.
     */
    @Internal
    void resetMovement();

    /**
     * Setzt die Geschwindigkeit für das Handler-Objekt.
     *
     * @param metersPerSecond Setzt die Geschwindigkeit, mit der sich das Zielobjekt bewegen soll.
     */
    @Internal
    void setVelocity(Vector metersPerSecond);

    /**
     * Gibt die aktuelle Geschwindigkeit aus.
     *
     * @return Die aktuelle Geschwindigkeit.
     */
    @Internal
    Vector getVelocity();

    /**
     * Setzt die Drehgeschwindigkeit für das Handler-Objekt.
     *
     * @param rotationsPerSecond Setzt die Drehgeschwindigkeit, mit der sich das Zielobjekt bewegen soll.
     */
    @Internal
    void setAngularVelocity(float rotationsPerSecond);

    /**
     * Gibt die aktuelle Drehgeschwindigkeit aus.
     *
     * @return Die aktuelle Drehgeschwindigkeit.
     */
    @Internal
    float getAngularVelocity();

    /**
     * Setzt, ob die Rotation blockiert sein soll.
     */
    @Internal
    void setRotationLocked(boolean locked);

    /**
     * @return Ob die Rotation des Objekts blockiert ist.
     */
    @Internal
    boolean isRotationLocked();

    /**
     * Testet, ob das Objekt below sich festen Boden hat. Dies ist der Fall, wenn direkt below dem Objekt ein
     * passives Objekt liegt.<br>
     * Diese Methode geht bei <b>unten</b> explizit von "unterhalb der Y-Achse" aus. Ein Objekt hat also Boden sich,
     * wenn am "unteren" Ende seines Bodies (=höchster Y-Wert) in unmittelbarer Nähe (heuristisch getestet) ein passives
     * Objekt anliegt.
     *
     * @return <code>true</code>, wenn direkt below dem Objekt ein passives Objekt ist. Sonst <code>false</code>.
     */
    @Internal
    boolean isGrounded();

    /**
     * Entfernt alle Fixtures/Collider am Actor und setzt alle Fixturs für dieses Objekt neu.
     *
     * @param fixtures Die neuen Fixtures als Supplier, der die Liste der Fixtures ausgibt.
     */
    @Internal
    void setFixtures(Supplier<List<FixtureData>> fixtures);

    /**
     * Gibt die Proxy-Daten des Actors aus.
     *
     * @return der gegenwärtige physikalische Zustand des Raum-Objekts in Proxy-Daten.
     */
    @Internal
    PhysicsData getPhysicsData();

    void applyMountCallbacks(PhysicsHandler otherHandler);

    List<CollisionEvent<Actor>> getCollisions();
}
