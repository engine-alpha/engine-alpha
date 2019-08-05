package ea.internal.physics;

import ea.Vector;
import ea.actor.Actor;
import ea.handle.BodyType;
import ea.internal.annotations.Internal;
import org.jbox2d.dynamics.Body;

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
public abstract class PhysicsHandler {

    /**
     * Das eine Actor-Objekt, das dieser Handler kontrolliert.
     */
    protected final Actor actor;

    /**
     * Initialisiert den Physics-Handler.
     *
     * @param actor Das eine Actor-Objekt, das dieser Handler kontrolliert.
     */
    protected PhysicsHandler(Actor actor) {
        this.actor = actor;
    }

    /* __________________________ Kontrakt: Abstrakte Methoden/Funktionen der Physics __________________________ */

    /**
     * Verschiebt das Ziel-Objekt um einen spezifischen Wert auf der Zeichenebene. Die Ausführung hat <b>erst (ggf.) im
     * kommenden Frame</b> einfluss auf die Physics und <b>ändert keine physikalischen Eigenschaften</b> des
     * Ziel-Objekts
     * (außer dessen Ort).
     *
     * @param v Ein Vector, um den das Ziel-Objekt verschoben werden soll. Dies ändert seine Position, jedoch sonst
     *          keine weiteren Eigenschaften.
     */
    public abstract void moveBy(Vector v);

    /**
     * Gibt den <b>Gewichtsmittelpunkt</b> dieses <code>Actor</code>-Objekts aus.
     *
     * @return der aktuelle <b>Gewichtsmittelpunkt</b> des Ziel-Objekts als <i>Point auf der Zeichenebene</i>.
     */
    public abstract Vector getCenter();

    /**
     * Gibt an, ob ein bestimmter Point auf der Zeichenebene innerhalb des Ziel-Objekts liegt.
     *
     * @param p Ein Point auf der Zeichenebene.
     *
     * @return <code>true</code>, wenn der übergebene Point innerhalb des Ziel-Objekts liegt, sonst <code>false</code>.
     * Das Ergebnis kann (abhängig von der implementierenden Klasse) verschieden sicher richtige Ergebnisse
     * liefern.
     */
    public abstract boolean contains(Vector p);

    /**
     * Gibt die aktuelle Position des Ziel-Objekts an.
     *
     * @return Die aktuelle Position des Ziel-Objekts. Diese ist bei Erstellung des Objekts zunächst immer
     * <code>(0|0)</code> und wird mit Rotation und Verschiebung verändert.
     */
    public abstract Vector getPosition();

    /**
     * Gibt die aktuelle Rotation des Ziel-Objekts in <i>Radians</i> an. Bei Erstellung eines
     * <code>Actor</code>-Objekts ist seine Rotation stets 0.
     *
     * @return die aktuelle Rotation des Ziel-Objekts in <i>Radians</i>.
     */
    public abstract float getRotation();

    /**
     * Rotiert das Ziel-Objekt um einen festen Winkel.
     *
     * @param radians Der Winkel, um den das Ziel-Objekt gedreht werden soll (in <i>Radians</i>).
     *                <ul>
     *                <li>Werte &gt; 0 : Drehung gegen Uhrzeigersinn</li>
     *                <li>Werte &lt; 0 : Drehung im Uhrzeigersinn</li>
     *                </ul>
     */
    public abstract void rotateBy(float radians);

    public abstract void setDensity(float density);

    public abstract float getDensity();

    public abstract void setFriction(float friction);

    public abstract float getFriction();

    public abstract void setRestitution(float elasticity);

    public abstract float getRestitution();

    /**
     * Setzt die Masse für das Ziel-Objekt.
     *
     * @param mass Die Masse, die das Ziel-Objekt einnehmen soll. In [kg]
     */
    public abstract void setMass(float mass);

    /**
     * Gibt die Masse des Ziel-Objekts aus.
     *
     * @return Die Masse des Ziel-Objekts in [kg].
     */
    public abstract float getMass();

    /**
     * Uebt eine Kraft auf das Ziel-Objekt (im Massenschwerpunkt) aus (sofern möglich).
     *
     * @param force Die Kraft, die auf den Massenschwerpunkt angewandt werden soll. <b>Nicht in [px]</b>, sondern in
     *              [N] = [m / s^2].
     */
    public abstract void applyForce(Vector force);

    /**
     * Wirkt einen Drehmoment auf das Ziel-Objekt.
     *
     * @param rotationMomentum der Drehmoment, der auf das Ziel-Objekt wirken soll. In [N*m]
     */
    public abstract void applyTorque(float rotationMomentum);

    /**
     * Wirkt einen Drehimpuls auf das Ziel-Objekt.
     *
     * @param rotationImpulse der Drehimpuls, der auf das Ziel-Objekt wirken soll. in [kg*m*m/s]
     */
    public abstract void applyRotationImpulse(float rotationImpulse);

    /**
     * Macht ein Type-Update für diesen Handler.
     *
     * @param type Der neue Type.
     */
    public abstract void setType(BodyType type);

    public abstract BodyType getType();

    public abstract void applyForce(Vector kraftInN, Vector globalerOrt);

    /**
     * Wirkt einen Impuls auf einem Welt-Point.
     *
     * @param impulsInNS  Ein Impuls (in [Ns]).
     * @param globalerOrt Der
     */
    public abstract void applyImpluse(Vector impulsInNS, Vector globalerOrt);

    /**
     * Gibt den WorldHandler aus, der die Welt handelt, in der sich der Klient
     * befindet.
     *
     * @return Der World-Handler, der zu diesem Physics-Handler gehört.
     */
    public abstract WorldHandler getWorldHandler();

    /**
     * Wird intern zum Debuggen benutzt. Gibt den korrespondierenden Body aus.
     *
     * @return Der korrespondierende Body.
     */
    @Internal
    public abstract Body getBody();

    /**
     * Setzt die Wirkung aller physikalischer Bewegungen (Geschwindigkeit und Drehung) zurück.
     * Hiernach ist das Objekt in Ruhe.
     */
    @Internal
    public abstract void resetMovement();

    /**
     * Setzt die Geschwindigkeit für das Handler-Objekt.
     *
     * @param geschwindigkeitInMProS Setzt die Geschwindigkeit, mit der sich das Zielobjekt bewegen soll.
     */
    @Internal
    public abstract void setVelocity(Vector geschwindigkeitInMProS);

    /**
     * Gibt die aktuelle Geschwindigkeit aus.
     *
     * @return Die aktuelle Geschwindigkeit.
     */
    @Internal
    public abstract Vector getVelocity();

    /**
     * Setzt, ob die Rotation blockiert sein soll.
     */
    @Internal
    public abstract void setRotationLocked(boolean block);

    /**
     * @return ob die Rotation des Objekts blockiert ist.
     */
    @Internal
    public abstract boolean isRotationLocked();

    /**
     * Testet, ob das Objekt unter sich festen Boden hat. Dies ist der Fall, wenn direkt unter dem Objekt ein
     * passives Objekt liegt.<br>
     * Diese Methode geht bei <b>unten</b> explizit von "unterhalb der Y-Achse" aus. Ein Objekt hat also Boden sich,
     * wenn am "unteren" Ende seines Bodies (=höchster Y-Wert) in unmittelbarer Nähe (heuristisch getestet) ein passives
     * Objekt anliegt.
     *
     * @return <code>true</code>, wenn direkt unter dem Objekt ein passives Objekt ist. Sonst <code>false</code>.
     */
    @Internal
    public abstract boolean isGrounded();

    public abstract float getTorque();

    public abstract void setTorque(float value);

    /**
     * Gibt die Proxy-Daten des Actors aus.
     *
     * @return der gegenwärtige physikalische Zustand des Raum-Objekts in Proxy-Daten.
     */
    @Internal
    public abstract ProxyData getProxyData();
}
