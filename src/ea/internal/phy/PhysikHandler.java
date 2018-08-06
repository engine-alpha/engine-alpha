package ea.internal.phy;

import ea.Vector;
import ea.actor.Actor;
import ea.handle.Physics;
import ea.internal.ano.NoExternalUse;
import org.jbox2d.dynamics.Body;

/**
 * Beschreibt allgemein ein Objekt, dass die physikalischen Eigenschaften eines Actor-Objektes kontrollieren kann.
 * Dazu gehört:
 * <ul>
 *     <li>Das <code>Actor</code>-Objekt <b>bewegen</b>.</li>
 *     <li><b>Physikalische Eigenschaften</b> des Objektes verändern (wie Masse, Reibungskoeffizient etc.)</li>
 *     <li><b>Einflüsse</b> auf das <code>Actor</code>-Objekt ausüben (wie anwenden von Impulsen / Kräften)</li>
 * </ul>
 * Created by andonie on 16.02.15.
 */
public abstract class PhysikHandler {



    /**
     * Das eine Actor-Objekt, das dieser Handler kontrolliert.
     */
    protected final Actor actor;

    /**
     * Der physikalische Type, den der Klient gerade fährt.
     */
    protected Physics.Type physikType;

    /**
     * Diese Variable speichert die Sensor-Flag des Klienten.
     * Die Sensor-Flag is true, wenn ein passives Objekt (= keine Physics) trotzdem
     * an Kollisionstests teilnehmen soll.
     */
    protected boolean isSensor;

    /**
     * Initialisiert den Physics-Handler.
     * @param actor  Das eine Actor-Objekt, das dieser Handler kontrolliert.
     */
    protected PhysikHandler(Actor actor, Physics.Type physikType, boolean isSensor) {
        this.actor = actor;
        this.physikType = physikType;
        this.isSensor = isSensor;
    }

    @NoExternalUse
    public boolean isSensor() {
        return isSensor;
    }

    /* __________________________ Kontrakt: Abstrakte Methoden/Funktionen der Physics __________________________ */

    /**
     * Setzt, ob das Klient-Objekt Sensorstatus haben soll oder nicht.
     * @param isSensor  Ob das Klient-Objekt Sensorstatus haben soll oder nicht.
     */
    public abstract void setSensor(boolean isSensor);

    /**
     * Verschiebt das Ziel-Objekt um einen spezifischen Wert auf der Zeichenebene. Die Ausführung hat <b>erst (ggf.) im
     * kommenden Frame</b> einfluss auf die Physics und <b>ändert keine physikalischen Eigenschaften</b> des Ziel-Objekts
     * (außer dessen Ort).
     * @param v     Ein Vector, um den das Ziel-Objekt verschoben werden soll. Dies ändert seine Position, jedoch sonst
     *              keine weiteren Eigenschaften.
     */
    public abstract void verschieben(Vector v);

    /**
     * Gibt den <b>Gewichtsmittelpunkt</b> dieses <code>Actor</code>-Objekts aus.
     * @return  der aktuelle <b>Gewichtsmittelpunkt</b> des Ziel-Objekts als <i>Point auf der Zeichenebene</i>.
     */
    public abstract Vector mittelpunkt();

    /**
     * Gibt an, ob ein bestimmter Point auf der Zeichenebene innerhalb des Ziel-Objekts liegt.
     * @param p Ein Point auf der Zeichenebene.
     * @return  <code>true</code>, wenn der übergebene Point innerhalb des Ziel-Objekts liegt, sonst <code>false</code>.
     *          Das Ergebnis kann (abhängig von der implementierenden Klasse) verschieden sicher richtige Ergebnisse
     *          liefern.
     */
    public abstract boolean beinhaltet(Vector p);

    /**
     * Gibt die aktuelle Position des Ziel-Objekts an.
     * @return  Die aktuelle Position des Ziel-Objekts. Diese ist bei Erstellung des Objekts zunächst immer
     *          <code>(0|0)</code> und wird mit Rotation und Verschiebung verändert.
     */
    public abstract Vector position();

    /**
     * Gibt die aktuelle Rotation des Ziel-Objekts in <i>Radians</i> an. Bei Erstellung eines
     * <code>Actor</code>-Objekts ist seine Rotation stets 0.
     * @return  die aktuelle Rotation des Ziel-Objekts in <i>Radians</i>.
     */
    public abstract float rotation();

    /**
     * Rotiert das Ziel-Objekt um einen festen Winkel.
     * @param radians   Der Winkel, um den das Ziel-Objekt gedreht werden soll (in <i>Radians</i>).
     *                  <ul>
     *                      <li>Werte > 0 : Drehung gegen Uhrzeigersinn</li>
     *                      <li>Werte < 0 : Drehung im Uhrzeigersinn</li>
     *                  </ul>
     */
    public abstract void rotieren(float radians);

    public abstract void dichteSetzen(float dichte);

    public abstract float dichte();

    public abstract void reibungSetzen(float reibung);

    public abstract float reibung();

    public abstract void elastizitaetSetzen(float ela);

    public abstract float elastizitaet();

    /**
     * Setzt die Masse für das Ziel-Objekt.
     * @param masse Die Masse, die das Ziel-Objekt einnehmen soll. In [kg]
     */
    public abstract void masseSetzen(float masse);

    /**
     * Gibt die Masse des Ziel-Objekts aus.
     * @return  Die Masse des Ziel-Objekts in [kg].
     */
    public abstract float masse();

    /**
     * Uebt eine Kraft auf das Ziel-Objekt (im Massenschwerpunkt) aus (sofern möglich).
     * @param kraft Die Kraft, die auf den Massenschwerpunkt angewandt werden soll. <b>Nicht in [px]</b>, sondern in
     *              [N] = [m / s^2].
     */
    public abstract void kraftWirken(Vector kraft);

    /**
     * Wirkt einen Drehmoment auf das Ziel-Objekt.
     * @param drehmoment    der Drehmoment, der auf das Ziel-Objekt wirken soll. In [N*m]
     *
     */
    public abstract void drehMomentWirken(float drehmoment);

    /**
     * Wirkt einen Drehimpuls auf das Ziel-Objekt.
     * @param drehimpuls    der Drehimpuls, der auf das Ziel-Objekt wirken soll. in [kg*m*m/s]
     */
    public abstract void drehImpulsWirken(float drehimpuls);

    /**
     * Setzt global für die Physics-Umgebung, in der sich das Zielobjekt befindet, die Schwerkraft neu.
     * @param schwerkraftInN    die neue Schwerkraft als Vector. in [N].
     */
    public abstract void schwerkraftSetzen(Vector schwerkraftInN);

    /**
     * Macht ein Type-Update für diesen Handler.
     * @param type   Der neue Type.
     * @return      Ein Handler, der diesen Type behandelt (ggf. this).
     */
    public abstract PhysikHandler typ(Physics.Type type);

    public Physics.Type typ() {
        return physikType;
    }

    public abstract void kraftWirken(Vector kraftInN, Vector globalerOrt);

    /**
     * Wirkt einen Impuls auf einem Welt-Point.
     * @param impulsInNS        Ein Impuls (in [Ns]).
     * @param globalerOrt       Der
     */
    public abstract void impulsWirken(Vector impulsInNS, Vector globalerOrt);

    /**
     * Entfernt den Körper von diesem Handler.
     * Danach ist das Objekt physikalisch nicht mehr existent.
     */
    public abstract void killBody();

    /**
     * Gibt den WorldHandler aus, der die Welt handelt, in der sich der Klient
     * befindet.
     * @return  Der World-Handler, der zu diesem Physics-Handler gehört.
     */
    public abstract WorldHandler worldHandler();

    /**
     * Wird intern zum Debuggen benutzt. Gibt den korrespondierenden Body aus.
     * @return  Der korrespondierende Body.
     */
    @NoExternalUse
    public abstract Body getBody();

    /**
     * Setzt die Wirkung aller physikalischer Bewegungen (Geschwindigkeit und Drehung) zurück.
     * Hiernach ist das Objekt in Ruhe.
     */
    @NoExternalUse
    public abstract void physicalReset();

    /**
     * Setzt die Geschwindigkeit für das Handler-Objekt.
     * @param geschwindigkeitInMProS    Setzt die Geschwindigkeit, mit der sich das Zielobjekt bewegen soll.
     */
    @NoExternalUse
    public abstract void geschwindigkeitSetzen(Vector geschwindigkeitInMProS);

    /**
     * Gibt die aktuelle Geschwindigkeit aus.
     * @return  Die aktuelle Geschwindigkeit.
     */
    @NoExternalUse
    public abstract Vector geschwindigkeit();

    /**
     * Setzt, ob die Rotation blockiert sein soll.
     */
    @NoExternalUse
    public abstract void rotationBlockiertSetzen(boolean block);

    /**
     * @return ob die Rotation des Objekts blockiert ist.
     */
    @NoExternalUse
    public abstract boolean rotationBlockiert();

    /**
     * Testet, ob das Objekt unter sich festen Boden hat. Dies ist der Fall, wenn direkt unter dem Objekt ein
     * passives Objekt liegt.<br />
     * Diese Methode geht bei <b>unten</b> explizit von "unterhalb der Y-Achse" aus. Ein Objekt hat also Boden sich,
     * wenn am "unteren" Ende seines Bodies (=höchster Y-Wert) in unmittelbarer Nähe (heuristisch getestet) ein passives
     * Objekt anliegt.
     * @return  <code>true</code>, wenn direkt unter dem Objekt ein passives Objekt ist. Sonst <code>false</code>.
     */
    @NoExternalUse
    public abstract boolean testIfGrounded();
}
