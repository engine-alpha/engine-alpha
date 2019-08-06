package ea.handle;

import ea.Vector;
import ea.actor.Actor;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.internal.util.Logger;
import org.jbox2d.dynamics.joints.*;

/**
 * Jedes <code>Actor</code>-Objekt hat ein öffentlich erreichbares Objekt <code>physics</code> dieser Klasse.
 * Dieses Objekt bietet eine umfangreiches Set an <i>Methoden</i>, die die Physics des entsprechenden
 * <code>Actor</code>-Objekts betreffen.<br><br>
 * <p>
 * Alle Methoden, die keine "richtige" Rückgabe hätten (also <code>void</code>-Methoden), sind mit <b>Chaining</b>
 * versehen. Das bedeutet, dass statt bei jeder Methode, die eigentlich vom <code>void</code>-Type wäre,
 * der Rückgabetyp <code>Position</code> ist und die Rückgabe das Objekt, das die Methode ausgeführt hat. Das ermöglicht
 * übersichtlichere Codes:<br>
 * <code>
 * actor.physics.applyForce(new Vector(10,0)); //Wirkt 10N vectorFromThisTo rechts <br>
 * actor.physics.drehimpulsWirken(2);         //Wirke einen Drehimpuls von 2 kg*m*m/s <br>
 * </code>
 * <br>
 * <b> ... kann so verkürzt werden zu ... </b> <br> <br>
 *
 * <code>
 * actor.physics.applyForce(new Vector(10,0)).drehimpulsWirken(2); <br>
 * </code> <br> <br>
 * <p>
 * Oder als größeres Beispiel:
 * <br>
 * <code>
 * actor.physics.setMass(80).getFriction(0.3f).applyForce(new Vector(10, -30)).drehimpuls(5);
 * </code>
 */
@API
public class Physics {

    /**
     * Das Actor-Objekt, zu dem dieses <code>Physics</code>-Objekt gehört.
     */
    private final Actor actor;

    /**
     * Interner Konstruktor. Wird nicht von außerhalb der Engine genutzt. Ein <code>Physics</code>-Objekt wird von
     * seinem
     * <code>Actor</code>-Parent erzeugt.
     *
     * @param actor Das <code>Actor</code>-Objekt, zu dem dieses <code>Physics</code>-Objekt ab sofort gehört.
     */
    @Internal
    public Physics(Actor actor) {
        this.actor = actor;
    }


    /* _________________________ Einheiten / Passive Eigenschaften _________________________ */

    /**
     * Setzt, ob <i>im Rahmen der physikalischen Simulation</i> die Rotation dieses Objekts
     * blockiert werden soll. <br>
     * Das Objekt kann in jedem Fall weiterhin über einen direkten Methodenaufruf rotiert
     * werden. Der folgende Code ist immer wirksam, unabhängig davon, ob die Rotation
     * im Rahmen der physikalischen Simulation blockiert ist:<br>
     * <code>
     * actor.getPosition.rotate(4.31f);
     * </code>
     *
     * @param rotationLocked Ist dieser Wert <code>true</code>, rotiert sich dieses
     *                       Objekts innerhalb der physikalischen Simulation <b>nicht mehr</b>.
     *                       Ist dieser Wert <code>false</code>, rotiert sich dieses
     *                       Objekt innerhalb der physikalsichen Simulation.
     *
     * @return Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     * Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     *
     * @see #isRotationLocked()
     */
    @API
    public Physics setRotationLocked(boolean rotationLocked) {
        actor.getPhysicsHandler().setRotationLocked(rotationLocked);
        return this;
    }

    /**
     * Gibt an, ob die Rotation dieses Objekts derzeit innerhalb der physikalischen Simulation
     * blockiert ist.
     *
     * @return <code>true</code>, wenn die Rotation dieses Objekts derzeit innerhalb der
     * physikalischen Simulation blockiert ist.
     *
     * @see #setRotationLocked(boolean)
     */
    @API
    public boolean isRotationLocked() {
        return actor.getPhysicsHandler().isRotationLocked();
    }

    /**
     * Setzt die Masse des Objekts neu. Hat Einfluss auf das physikalische Verhalten des Objekts.
     *
     * @param massInKG Die neue Masse für das Objekt in <b>[kg]</b>.
     *
     * @return Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     * Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physics setMass(float massInKG) {
        actor.getPhysicsHandler().setMass(massInKG);
        return this;
    }

    /**
     * Gibt die aktuelle Masse des Ziel-Objekts aus. Die Form bleibt unverändert, daher ändert sich
     * die <b>Dichte</b> in der Regel.
     *
     * @return Die Masse des Ziel-Objekts in <b>[kg]</b>.
     */
    @API
    public float getMass() {
        return actor.getPhysicsHandler().getMass();
    }

    /**
     * Setzt die Dichte des Objekts neu. Die Form bleibt dabei unverändert, daher ändert sich die
     * <b>Masse</b> in der Regel.
     *
     * @param densityInKgProQM die neue Dichte des Objekts in <b>[kg/m^2]</b>
     *
     * @return Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     * Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physics setDensity(float densityInKgProQM) {
        actor.getPhysicsHandler().setDensity(densityInKgProQM);
        return this;
    }

    /**
     * Gibt die aktuelle Dichte des Objekts an.
     *
     * @return Die aktuelle Dichte des Objekts in <b>[kg/m^2]</b>.
     */
    @API
    public float getDensity() {
        return actor.getPhysicsHandler().getDensity();
    }

    /**
     * Setzt den Reibungskoeffizient für das Objekt. Hat Einfluss auf
     * die Bewegung des Objekts.
     *
     * @param coefficientOfElasticity Der Reibungskoeffizient. In der Regel im Bereich <b>[0; 1]</b>.
     *
     * @return Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     * Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physics setFriction(float coefficientOfElasticity) {
        actor.getPhysicsHandler().setFriction(coefficientOfElasticity);
        return this;
    }

    /**
     * Gibt den Reibungskoeffizienten für dieses Objekt aus.
     *
     * @return Der Reibungskoeffizient des Objekts. Ist in der Regel (in der Realität)
     * ein Wert im Bereich <b>[0; 1]</b>.
     */
    @API
    public float getFriction() {
        return actor.getPhysicsHandler().getFriction();
    }

    /**
     * Setzt die Geschwindigkeit "hart" für dieses Objekt. Damit wird die aktuelle
     * Bewegung (nicht aber die Rotation) des Objekts ignoriert und hart auf den
     * übergebenen Wert gesetzt.
     *
     * @param velocityInMPerS Die Geschwindigkeit, mit der sich dieses Objekt ab sofort
     *                        bewegen soll. In <b>[m / s]</b>
     *
     * @return Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     * Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physics setVelocity(Vector velocityInMPerS) {
        actor.getPhysicsHandler().setVelocity(velocityInMPerS);
        return this;
    }

    /**
     * Gibt die Geschwindigkeit aus, mit der sich dieses Objekt gerade (also in diesem Frame) bewegt.
     *
     * @return Die Geschwindigkeit, mit der sich dieses Objekt gerade (also in diesem Frame) bewegt.
     * In <b>[m / s]</b>
     */
    @API
    public Vector getVelocity() {
        return actor.getPhysicsHandler().getVelocity();
    }

    @API
    public Physics setRestitution(float elasticity) {
        actor.getPhysicsHandler().setRestitution(elasticity);
        return this;
    }

    @API
    public float getRestitution() {
        return actor.getPhysicsHandler().getRestitution();
    }


    /* _________________________ Doers : Direkter Effekt auf Simulation _________________________ */

    /**
     * Wirkt eine Kraft auf den <i>Schwerpunkt</i> des Objekts.
     *
     * @param force Ein Kraft-Vector. Einheit ist <b>nicht [px], sonder [N]</b>.
     *
     * @return Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     * Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physics applyForce(Vector force) {
        actor.getPhysicsHandler().applyForce(force);
        return this;
    }

    /**
     * Wirkt eine Kraft auf einem bestimmten <i>Point in der Welt</i>.
     *
     * @param kraftInN    Eine Kraft. Einheit ist <b>[N]</b>
     * @param globalPoint Der Ort auf der <i>Zeichenebene</i>, an dem die Kraft wirken soll.
     *
     * @return Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     * Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physics applyForce(Vector kraftInN, Vector globalPoint) {
        actor.getPhysicsHandler().applyForce(kraftInN, globalPoint);
        return this;
    }

    /**
     * Wirkt einen Impuls auf den <i>Schwerpunkt</i> des Objekts.
     *
     * @param impulseInNS Der Impuls, der auf den Schwerpunkt wirken soll. Einheit ist <b>[Ns]</b>
     *
     * @return Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     * Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physics applyImpulse(Vector impulseInNS) {
        actor.getPhysicsHandler().applyImpluse(impulseInNS, actor.getPhysicsHandler().getCenter());
        return this;
    }

    /**
     * Wirkt einen Impuls an einem bestimmten <i>Point in der Welt</i>.
     *
     * @param impulseInNS Ein Impuls. Einheit ist <b>[Ns]</b>
     * @param globalPoint Der Ort auf der <i>Zeichenebene</i>, an dem der Impuls wirken soll.
     *
     * @return Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     * Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physics applyImpulse(Vector impulseInNS, Vector globalPoint) {
        actor.getPhysicsHandler().applyImpluse(impulseInNS, globalPoint);
        return this;
    }

    /**
     * Versetzt das Objekt - unabhängig von aktuellen Kräften und Geschwindigkeiten -
     * <i>in Ruhe</i>. Damit werden alle (physikalischen) Bewegungen des Objektes zurückgesetzt.
     * Sollte eine konstante <i>Schwerkraft</i> (oder etwas Vergleichbares) exisitieren, wo
     * wird dieses Objekt jedoch möglicherweise aus der Ruhelage wieder in Bewegung versetzt.
     *
     * @return Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     * Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physics cancelAll() {
        actor.getPhysicsHandler().resetMovement();
        return this;
    }

    /**
     * Testet, ob das Objekt "steht". Diese Funktion ist below anderem hilfreich für die Entwicklung von Platformern
     * (z.B. wenn der Spieler nur springen können soll, wenn er auf dem Boden steht).<br>
     * Diese Funktion ist eine <b>Heuristik</b>, sprich sie ist eine Annäherung. In einer Physik-Simulation ist die
     * Definition von "stehen" nicht unbedingt einfach. Hier bedeutet es folgendes:<br>
     * <i>Ein Objekt steht genau dann, wenn alle Eigenschaften erfüllt sind:</i>
     * <ul>
     * <li>Es ist ein <b>dynamisches Objekt</b>.</li>
     * <li>Direkt below der Mitte der minimalen
     * <a href="https://en.wikipedia.org/wiki/Minimum_bounding_box#Axis-aligned_minimum_bounding_box">AABB</a>,
     * die das gesamte Objekt umspannt, befindet sich ein <b>statisches Objekt</b>.</li>
     * </ul>
     */
    @API
    public boolean isGrounded() {
        return actor.getPhysicsHandler().isGrounded();
    }

    /* _________________________ JOINTS _________________________ */

    /**
     * Prüft ob das zugehörige <code>Actor</code>-Objekt in der selben JB2D World liegt wie das übergebene Objekt.
     * Diese Logik ist ausgelagert, um den Code etwas schöner zu machen.
     *
     * @param other ein zweites <code>Actor</code>-Objekt zum testen.
     *
     * @return true = beide Objekte liegen in der selben World. Sonst false.
     */
    @Internal
    private boolean assertSameWorld(Actor other) {
        if (other.getPhysicsHandler().getWorldHandler() != actor.getPhysicsHandler().getWorldHandler()) {
            Logger.error("Physics", "Die Actor-Objekte sind nicht an der selben Wurzel angemeldet. Sie können " + "deshalb (noch) nicht physikalisch verbunden werden.");
            return false;
        }
        return true;
    }

    /**
     * Erstellt einen Revolute-Joint zwischen dem zugehörigen <code>Actor</code>-Objekt und einem weiteren.
     *
     * <h3>Definition Revolute-Joint</h3>
     * <p>Verbindet zwei <code>Actor</code>-Objekte <b>untrennbar an einem Anchor-Point</b>. Die Objekte können sich
     * ab sofort nur noch <b>relativ zueinander drehen</b>.</p>
     *
     * @param other  Das zweite <code>Actor</code>-Objekt, das ab sofort mit dem zugehörigen <code>Actor</code>-Objekt
     *               über einen <code>RevoluteJoint</code> verbunden sein soll.
     * @param anchor Der Ankerpunkt <b>auf der Zeichenebene</b>. Es wird davon
     *               ausgegangen, dass beide Objekte bereits korrekt positioniert sind.
     *
     * @return Ein <code>RevoluteJoint</code>-Objekt, mit dem der Joint weiter gesteuert werden kann.
     *
     * @see org.jbox2d.dynamics.joints.RevoluteJoint
     */
    @API
    public RevoluteJoint createRevoluteJoint(Actor other, Vector anchor) {
        if (!assertSameWorld(other)) {
            return null;
        }

        // Definiere den Joint
        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef.initialize(actor.getPhysicsHandler().getBody(), other.getPhysicsHandler().getBody(),
                //actor.physicsHandler.getWorldHandler().fromVektor(actor.getPosition.get().asVector().add(anchor)));
                anchor.toVec2());
        revoluteJointDef.collideConnected = false;

        return (RevoluteJoint) actor.getPhysicsHandler().getWorldHandler().getWorld().createJoint(revoluteJointDef);
    }

    /**
     * Erstellt einen Rope-Joint zwischen diesem und einem weiteren <code>Actor</code>-Objekt.
     *
     * @param other      Das zweite <code>Actor</code>-Objekt, das ab sofort mit dem zugehörigen
     *                   <code>Actor</code>-Objekt
     *                   über einen <code>RopeJoint</code> verbunden sein soll.
     * @param anchorA    Der Ankerpunkt für das zugehörige <code>Actor</code>-Objekt. Der erste Befestigungspunkt
     *                   des Lassos. Angabe relativ zur Position vom zugehörigen Objekt.
     * @param anchorB    Der Ankerpunkt für das zweite <code>Actor</code>-Objekt, also <code>other</code>.
     *                   Der zweite Befestigungspunkt des Lassos. Angabe relativ zur Position vom zugehörigen Objekt.
     * @param ropeLength Die Länge des Lassos. Dies ist ab sofort die maximale Länge, die die beiden Ankerpunkte
     *                   der Objekte voneinader entfernt sein können.
     *
     * @return Ein <code>RopeJoint</code>-Objekt, mit dem der Joint weiter gesteuert werden kann.
     *
     * @see org.jbox2d.dynamics.joints.RopeJoint
     */
    @API
    public RopeJoint createRopeJoint(Actor other, Vector anchorA, Vector anchorB, float ropeLength) {
        if (!assertSameWorld(other)) {
            return null;
        }

        RopeJointDef ropeJointDef = new RopeJointDef();
        ropeJointDef.bodyA = actor.getPhysicsHandler().getBody();
        ropeJointDef.bodyB = other.getPhysicsHandler().getBody();

        ropeJointDef.localAnchorA.set(anchorA.toVec2());
        ropeJointDef.localAnchorB.set(anchorB.toVec2());
        ropeJointDef.maxLength = ropeLength;

        return (RopeJoint) actor.getPhysicsHandler().getWorldHandler().getWorld().createJoint(ropeJointDef);
    }

    /**
     * Erstellt einen Distance-Joint zwischen diesem und einem weiteren <code>Actor</code>-Objekt.
     *
     * @param other             Das zweite <code>Actor</code>-Objekt, das ab sofort mit dem zugehörigen
     *                          <code>Actor</code>-Objekt
     *                          über einen <code>DistanceJoint</code> verbunden sein soll.
     * @param anchorAAsWorldPos Der Ankerpunkt für das zugehörige <code>Actor</code>-Objekt. Der erste Befestigungspunkt
     *                          des Joints. Angabe als <b>Position auf der Zeichenebene</b>, also absolut.
     * @param anchorBAsWorldPos Der Ankerpunkt für das zweite <code>Actor</code>-Objekt, also <code>other</code>.
     *                          Der zweite Befestigungspunkt des Joints.
     *                          Angabe als <b>Position auf der Zeichenebene</b>, also absolut.
     *
     * @return Ein <code>DistanceJoint</code>-Objekt, mit dem der Joint weiter gesteuert werden kann.
     *
     * @see org.jbox2d.dynamics.joints.DistanceJoint
     */
    @API
    public DistanceJoint createDistanceJoint(Actor other, Vector anchorAAsWorldPos, Vector anchorBAsWorldPos) {
        if (!assertSameWorld(other)) {
            return null;
        }

        DistanceJointDef distanceJointDef = new DistanceJointDef();
        distanceJointDef.initialize(actor.getPhysicsHandler().getBody(), other.getPhysicsHandler().getBody(), anchorAAsWorldPos.toVec2(), anchorBAsWorldPos.toVec2());

        return (DistanceJoint) actor.getPhysicsHandler().getWorldHandler().getWorld().createJoint(distanceJointDef);
    }

    public float getTorque() {
        return actor.getPhysicsHandler().getTorque();
    }

    public void setTorque(float value) {
        actor.getPhysicsHandler().setTorque(value);
    }

    /* _________________________ Physics-Type _________________________ */
}
