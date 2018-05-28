package ea.handle;

import ea.Point;
import ea.Vector;
import ea.actor.Actor;
import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import ea.internal.util.Logger;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.*;

/**
 * Jedes <code>Actor</code>-Objekt hat ein öffentlich erreichbares Objekt <code>physics</code> dieser Klasse.
 * Dieses Objekt bietet eine umfangreiches Set an <i>Methoden</i>, die die Physics des entsprechenden
 * <code>Actor</code>-Objekts betreffen.<br /><br />
 *
 * Alle Methoden, die keine "richtige" Rückgabe hätten (also <code>void</code>-Methoden), sind mit <b>Chaining</b>
 * versehen. Das bedeutet, dass statt bei jeder Methode, die eigentlich vom <code>void</code>-Type wäre,
 * der Rückgabetyp <code>Position</code> ist und die Rückgabe das Objekt, das die Methode ausgeführt hat. Das ermöglicht
 * übersichtlichere Codes:<br />
 * <code>
 *     actor.physics.applyForce(new Vector(10,0)); //Wirkt 10N vectorFromThisTo rechts <br />
 *     actor.physics.drehimpulsWirken(2);         //Wirke einen Drehimpuls von 2 kg*m*m/s <br />
 * </code>
 * <br />
 * <b> ... kann so verkürzt werden zu ... </b> <br /> <br />
 *
 * <code>
 *     actor.physics.applyForce(new Vector(10,0)).drehimpulsWirken(2); <br />
 * </code> <br /> <br />
 *
 * Oder als größeres Beispiel:
 * <br />
 * <code>
 *     actor.physics.setMass(80).getFriction(0.3f).applyForce(new Vector(10, -30)).drehimpuls(5);
 * </code>
 *
 * Created by andonie on 16.02.15.
 */
@API
public class Physics {

    /**
     * Das Actor-Objekt, zu dem dieses <code>Physics</code>-Objekt gehört.
     */
    private final Actor actor;


    /**
     * Interner Konstruktor. Wird nicht von außerhalb der Engine genutzt. Ein <code>Physics</code>-Objekt wird von seinem
     * <code>Actor</code>-Parent erzeugt.
     * @param actor  Das <code>Actor</code>-Objekt, zu dem dieses <code>Physics</code>-Objekt ab sofort gehört.
     */
    @NoExternalUse
    public Physics(Actor actor) {
        this.actor = actor;
    }


    /* _________________________ Einheiten / Passive Eigenschaften _________________________ */

    /**
     * Setzt, ob <i>im Rahmen der physikalischen Simulation</i> die Rotation dieses Objekts
     * blockiert werden soll. <br/>
     * Das Objekt kann in jedem Fall weiterhin über einen direkten Methodenaufruf rotiert
     * werden. Der folgende Code ist immer wirksam, unabhängig davon, ob die Rotation
     * im Rahmen der physikalischen Simulation blockiert ist:<br />
     * <code>
     *     actor.getPosition.rotate(4.31f);
     * </code>
     * @param rotationLocked
     *                  Ist dieser Wert <code>true</code>, rotiert sich dieses
     *                  Objekts innerhalb der physikalischen Simulation <b>nicht mehr</b>.
     *                  Ist dieser Wert <code>false</code>, rotiert sich dieses
     *                  Objekt innerhalb der physikalsichen Simulation.
     * @return          Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                  Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     * @see #isRotationLocked()
     */
    @API
    public Physics setRotationLocked(boolean rotationLocked) {
        actor.getPhysicsHandler().rotationBlockiertSetzen(rotationLocked);
        return this;
    }

    /**
     * Gibt an, ob die Rotation dieses Objekts derzeit innerhalb der physikalischen Simulation
     * blockiert ist.
     * @return          <code>true</code>, wenn die Rotation dieses Objekts derzeit innerhalb der
     *                  physikalischen Simulation blockiert ist.
     * @see #setRotationLocked(boolean)
     */
    @API
    public boolean isRotationLocked() {
        return actor.getPhysicsHandler().rotationBlockiert();
    }

    /**
     * Setzt die Masse des Objekts neu. Hat Einfluss auf das physikalische Verhalten des Objekts.
     * @param massInKG Die neue Masse für das Objekt in <b>[kg]</b>.
     * @return          Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                  Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physics setMass(float massInKG) {
        actor.getPhysicsHandler().masseSetzen(massInKG);
        return this;
    }

    /**
     * Gibt die aktuelle Masse des Ziel-Objekts aus. Die Form bleibt unverändert, daher ändert sich
     * die <b>Dichte</b> in der Regel.
     * @return  Die Masse des Ziel-Objekts in <b>[kg]</b>.
     */
    @API
    public float getMass() {
        return actor.getPhysicsHandler().masse();
    }

    /**
     * Setzt die Dichte des Objekts neu. Die Form bleibt dabei unverändert, daher ändert sich die
     * <b>Masse</b> in der Regel.
     * @param densityInKgProQM   die neue Dichte des Objekts in <b>[kg/m^2]</b>
     * @return                  Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                          Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physics setDensity(float densityInKgProQM) {
        actor.getPhysicsHandler().dichteSetzen(densityInKgProQM);
        return this;
    }

    /**
     * Gibt die aktuelle Dichte des Objekts an.
     * @return  Die aktuelle Dichte des Objekts in <b>[kg/m^2]</b>.
     */
    @API
    public float getDensity() {
        return actor.getPhysicsHandler().dichte();
    }

    /**
     * Setzt den Reibungskoeffizient für das Objekt. Hat Einfluss auf
     * die Bewegung des Objekts.
     * @param coefficientOfElasticity   Der Reibungskoeffizient. In der Regel im Bereich <b>[0; 1]</b>.
     * @return                      Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                              Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physics setFriction(float coefficientOfElasticity) {
        actor.getPhysicsHandler().reibungSetzen(coefficientOfElasticity);
        return this;
    }

    /**
     * Gibt den Reibungskoeffizienten für dieses Objekt aus.
     * @return  Der Reibungskoeffizient des Objekts. Ist in der Regel (in der Realität)
     *          ein Wert im Bereich <b>[0; 1]</b>.
     */
    @API
    public float getFriction() {
        return actor.getPhysicsHandler().reibung();
    }

    /**
     * Setzt die Geschwindigkeit "hart" für dieses Objekt. Damit wird die aktuelle
     * Bewegung (nicht aber die Rotation) des Objekts ignoriert und hart auf den
     * übergebenen Wert gesetzt.
     * @param velocityInMPerS    Die Geschwindigkeit, mit der sich dieses Objekt ab sofort
     *                                  bewegen soll. In <b>[m / s]</b>
     * @return                          Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                                  Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physics setVelocity(Vector velocityInMPerS) {
        actor.getPhysicsHandler().geschwindigkeitSetzen(velocityInMPerS);
        return this;
    }

    /**
     * Gibt die Geschwindigkeit aus, mit der sich dieses Objekt gerade (also in diesem Frame) bewegt.
     * @return  Die Geschwindigkeit, mit der sich dieses Objekt gerade (also in diesem Frame) bewegt.
     *          In <b>[m / s]</b>
     */
    @API
    public Vector getVelocity() {
        return actor.getPhysicsHandler().geschwindigkeit();
    }

    @API
    public Physics setElasticity(float elasticity) {
        actor.getPhysicsHandler().elastizitaetSetzen(elasticity);
        return this;
    }

    @API
    public float getElasticity() {
        return actor.getPhysicsHandler().elastizitaet();
    }

    /* _________________________ World-Wrap _________________________ */

    /**
     * Setzt die Schwerkraft, die auf <b>alle Objekte innerhalb der Scene</b> wirkt.
     * @param gravityInN    Die neue Schwerkraft als Vector. Die Einheit ist <b>[N]</b>.
     * @return                  Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                          Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physics setGravity(Vector gravityInN) {
        actor.getPhysicsHandler().schwerkraftSetzen(gravityInN);
        return this;
    }


    /* _________________________ Doers : Direkter Effekt auf Simulation _________________________ */

    /**
     * Wirkt eine Kraft auf den <i>Schwerpunkt</i> des Objekts.
     * @param force  Ein Kraft-Vector. Einheit ist <b>nicht [px], sonder [N]</b.
     * @return          Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                  Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physics applyForce(Vector force) {
        actor.getPhysicsHandler().kraftWirken(force);
        return this;
    }

    /**
     * Wirkt eine Kraft auf einem bestimmten <i>Point in der Welt</i>.
     * @param kraftInN              Eine Kraft. Einheit ist <b>[N]</b>
     * @param globalPoint         Der Ort auf der <i>Zeichenebene</i>, an dem die Kraft wirken soll.
     * @return                      Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                              Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physics applyForce(Vector kraftInN, Point globalPoint) {
        actor.getPhysicsHandler().kraftWirken(kraftInN, globalPoint);
        return this;
    }

    /**
     * Wirkt einen Impuls auf den <i>Schwerpunkt</i> des Objekts.
     * @param impulseInNS    Der Impuls, der auf den Schwerpunkt wirken soll. Einheit ist <b>[Ns]</b>
     * @return              Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                      Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physics applyImpulse(Vector impulseInNS) {
        actor.getPhysicsHandler().impulsWirken(impulseInNS, actor.getPhysicsHandler().mittelpunkt());
        return this;
    }

    /**
     * Wirkt einen Impuls an einem bestimmten <i>Point in der Welt</i>.
     * @param impulseInNS    Ein Impuls. Einheit ist <b>[Ns]</b>
     * @param globalPoint   Der Ort auf der <i>Zeichenebene</i>, an dem der Impuls wirken soll.
     * @return              Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                      Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physics applyImpulse(Vector impulseInNS, Point globalPoint) {
        actor.getPhysicsHandler().impulsWirken(impulseInNS, globalPoint);
        return this;
    }

    /**
     * Versetzt das Objekt - unabhängig von aktuellen Kräften und Geschwindigkeiten -
     * <i>in Ruhe</i>. Damit werden alle (physikalischen) Bewegungen des Objektes zurückgesetzt.
     * Sollte eine konstante <i>Schwerkraft</i> (oder etwas Vergleichbares) exisitieren, wo
     * wird dieses Objekt jedoch möglicherweise aus der Ruhelage wieder in Bewegung versetzt.
     * @return              Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                      Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physics cancelAll() {
        actor.getPhysicsHandler().physicalReset();
        return this;
    }

    /* _________________________ JOINTS _________________________ */

    /**
     * Prüft ob das zugehörige <code>Actor</code>-Objekt in der selben JB2D World liegt wie das übergebene Objekt.
     * Diese Logik ist ausgelagert, um den Code etwas schöner zu machen.
     * @param other ein zweites <code>Actor</code>-Objekt zum testen.
     * @return true = beide Objekte liegen in der selben World. Sonst false.
     */
    @NoExternalUse
    private boolean assertSameWorld(Actor other) {
        if(other.getPhysicsHandler().worldHandler() != actor.getPhysicsHandler().worldHandler()) {
            Logger.error("Physics", "Die Actor-Objekte sind nicht an der selben Wurzel angemeldet. Sie können " +
                    "deshalb (noch) nicht physikalisch verbunden werden.");
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
     * @param other     Das zweite <code>Actor</code>-Objekt, das ab sofort mit dem zugehörigen <code>Actor</code>-Objekt
     *                  über einen <code>RevoluteJoint</code> verbunden sein soll.
     * @param anchor    Der Ankerpunkt <b>auf der Zeichenebene</b>. Es wird davon
     *                  ausgegangen, dass beide Objekte bereits korrekt positioniert sind.
     * @return          Ein <code>RevoluteJoint</code>-Objekt, mit dem der Joint weiter gesteuert werden kann.
     * @see org.jbox2d.dynamics.joints.RevoluteJoint
     */
    @API
    public RevoluteJoint createRevoluteJoint(Actor other, Vector anchor) {
        if(!assertSameWorld(other)) return null;

        //Definiere den Joint
        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef.initialize(actor.getPhysicsHandler().getBody(), other.getPhysicsHandler().getBody(),
                //actor.physicsHandler.worldHandler().fromVektor(actor.getPosition.get().asVector().add(anchor)));
                actor.getPhysicsHandler().worldHandler().fromVektor(anchor));
        revoluteJointDef.collideConnected = false;

        return (RevoluteJoint) actor.getPhysicsHandler().worldHandler().getWorld().createJoint(revoluteJointDef);
    }

    /**
     * Erstellt einen Rope-Joint zwischen diesem und einem weiteren <code>Actor</code>-Objekt.
     * @param other     Das zweite <code>Actor</code>-Objekt, das ab sofort mit dem zugehörigen <code>Actor</code>-Objekt
     *                  über einen <code>RopeJoint</code> verbunden sein soll.
     * @param anchorA   Der Ankerpunkt für das zugehörige <code>Actor</code>-Objekt. Der erste Befestigungspunkt
     *                  des Lassos. Angabe relativ zur Position vom zugehörigen Objekt.
     * @param anchorB   Der Ankerpunkt für das zweite <code>Actor</code>-Objekt, also <code>other</code>.
     *                  Der zweite Befestigungspunkt des Lassos. Angabe relativ zur Position vom zugehörigen Objekt.
     * @param ropeLength    Die Länge des Lassos. Dies ist ab sofort die maximale Länge, die die beiden Ankerpunkte
     *                      der Objekte voneinader entfernt sein können.
     * @return  Ein <code>RopeJoint</code>-Objekt, mit dem der Joint weiter gesteuert werden kann.
     * @see org.jbox2d.dynamics.joints.RopeJoint
     */
    @API
    public RopeJoint createRopeJoint(Actor other, Vector anchorA, Vector anchorB, float ropeLength) {
        if(!assertSameWorld(other)) return null;

        RopeJointDef ropeJointDef = new RopeJointDef();
        ropeJointDef.bodyA = actor.getPhysicsHandler().getBody();
        ropeJointDef.bodyB = other.getPhysicsHandler().getBody();

        ropeJointDef.localAnchorA.set(actor.getPhysicsHandler().worldHandler().fromVektor(anchorA));
        ropeJointDef.localAnchorB.set(actor.getPhysicsHandler().worldHandler().fromVektor(anchorB));
        ropeJointDef.maxLength = ropeLength;

        return (RopeJoint) actor.getPhysicsHandler().worldHandler().getWorld().createJoint(ropeJointDef);

    }

    /**
     * Erstellt einen Distance-Joint zwischen diesem und einem weiteren <code>Actor</code>-Objekt.
     * @param other     Das zweite <code>Actor</code>-Objekt, das ab sofort mit dem zugehörigen <code>Actor</code>-Objekt
     *                  über einen <code>DistanceJoint</code> verbunden sein soll.
     * @param anchorAAsWorldPos Der Ankerpunkt für das zugehörige <code>Actor</code>-Objekt. Der erste Befestigungspunkt
     *                  des Joints. Angabe als <b>Position auf der Zeichenebene</b>, also absolut.
     * @param anchorBAsWorldPos Der Ankerpunkt für das zweite <code>Actor</code>-Objekt, also <code>other</code>.
     *                  Der zweite Befestigungspunkt des Joints.
     *                  Angabe als <b>Position auf der Zeichenebene</b>, also absolut.
     * @return          Ein <code>DistanceJoint</code>-Objekt, mit dem der Joint weiter gesteuert werden kann.
     * @see org.jbox2d.dynamics.joints.DistanceJoint
     */
    @API
    public DistanceJoint createDistanceJoint(Actor other, Vector anchorAAsWorldPos, Vector anchorBAsWorldPos) {
        if(!assertSameWorld(other)) return null;

        DistanceJointDef distanceJointDef = new DistanceJointDef();
        distanceJointDef.initialize(
                actor.getPhysicsHandler().getBody(),
                other.getPhysicsHandler().getBody(),
                actor.getPhysicsHandler().worldHandler().fromVektor(anchorAAsWorldPos),
                actor.getPhysicsHandler().worldHandler().fromVektor(anchorBAsWorldPos));

        return (DistanceJoint) actor.getPhysicsHandler().worldHandler().getWorld().createJoint(distanceJointDef);
    }

    /* _________________________ Physics-Type _________________________ */

    /**
     * Setzt, was für eine Type physikalisches Objekt das Objekt sein soll. Erläuterung findet
     * sich im <code>enum Type</code>.
     * @param type   Der Type Physics-Objekt, der ab sofort dieses Objekt sein soll.
     * @return      Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *              Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     * @see Type
     */
    @API
    public Physics setType(Type type) {
        actor.setBodyType(type);
        return this;
    }

    /**
     * Gibt aus, was für ein Type Physics-Objekt dieses Objekt momentan ist.
     * @return  der Type Physics-Objekt, der das entsprechende <code>Actor</code>-Objekt momentan ist.
     * @see Type
     */
    @API
    public Type getType() {
        return actor.getPhysicsHandler().typ();
    }

    /**
     * Aufzählung der drei verschiedenen Typen von Objekten innerhalb der Physics der EA.
     * <ul>
     *     <li>
     *         <b>Statische</b> Objekte:
     *         <ul>
     *             <li>Haben keine Geschwindigkeit</li>
     *             <li>Bewegen sich nicht in der Simulation, Kräfte haben keinen Einfluss auf sie.</li>
     *         </ul>
     *         Diese Eigenschaft gehört zum Beispiel zu <i>Wänden, Böden und Decken</i>.
     *     </li>
     *     <li>
     *         <b>Dynamische</b> Objekte:
     *         <ul>
     *             <li>Verhalten sich wie Objekte der newton'schen Mechanik.</li>
     *             <li>Können Kräfte auf sich wirken lassen und miteinander interagieren.</li>
     *         </ul>
     *         Diese Eigenschaft gehört zum Beispiel zu <i>Billiardkugeln, Spielfiguren und Wurfgeschossen</i>.
     *     </li>
     *     <li>
     *         <b>Kinematische</b> Objekte:
     *         <ul>
     *             <li>Können eine Geschwindigkeit haben, aber onKeyDown nicht auf Kräfte.</li>
     *             <li>Kollidieren (im Sinne der Physics) nur mit dynamischen Objekten.</li>
     *         </ul>
     *         Doese Eigenschaft gehört zum Beispiel zu <i>beweglichen Plattformen</i>.
     *     </li>
     *     <li>
     *         <b>Passive</b> Objekte:
     *         <ul>
     *              <li>Nehmen nicht an der Physics teil. Sie werden von der Physics so behandelt,
     *              <i>als wären sie nicht da</i>.</li>
     *              <li>Dies ist die <b>Standardeinstellung</b> für Objekte.</li>
     *         </ul>
     *     </li>
     * </ul>
     * @see #setType(Type)
     * @see #getType()
     */
    @API
    public enum Type {
        STATISCH, DYNAMISCH, KINEMATISCH, PASSIV;

        /**
         * Konvertierungsmethode zwischen Engine-Physiktyp und JB2D-Physiktyp.
         * @return      Der zugehörige JB2D-Phy-Type zu diesem Engine-Phy-Type.
         */
        @NoExternalUse
        public BodyType convert() {
            switch (this) {
                case STATISCH:
                    return BodyType.STATIC;
                case DYNAMISCH:
                case PASSIV:
                    return BodyType.DYNAMIC;
                case KINEMATISCH:
                    return BodyType.KINEMATIC;
            }
            return null;
        }
    }

}
