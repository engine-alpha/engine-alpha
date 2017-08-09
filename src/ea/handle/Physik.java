package ea.handle;

import ea.Punkt;
import ea.Vektor;
import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import ea.internal.util.Logger;
import ea.raum.Raum;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.*;

/**
 * Jedes <code>Raum</code>-Objekt hat ein öffentlich erreichbares Objekt <code>physik</code> dieser Klasse.
 * Dieses Objekt bietet eine umfangreiches Set an <i>Methoden</i>, die die Physik des entsprechenden
 * <code>Raum</code>-Objekts betreffen.<br /><br />
 *
 * Alle Methoden, die keine "richtige" Rückgabe hätten (also <code>void</code>-Methoden), sind mit <b>Chaining</b>
 * versehen. Das bedeutet, dass statt bei jeder Methode, die eigentlich vom <code>void</code>-Typ wäre,
 * der Rückgabetyp <code>Position</code> ist und die Rückgabe das Objekt, das die Methode ausgeführt hat. Das ermöglicht
 * übersichtlichere Codes:<br />
 * <code>
 *     raum.physik.kraftWirken(new Vektor(10,0)); //Wirkt 10N nach rechts <br />
 *     raum.physik.drehimpulsWirken(2);         //Wirke einen Drehimpuls von 2 kg*m*m/s <br />
 * </code>
 * <br />
 * <b> ... kann so verkürzt werden zu ... </b> <br /> <br />
 *
 * <code>
 *     raum.physik.kraftWirken(new Vektor(10,0)).drehimpulsWirken(2); <br />
 * </code> <br /> <br />
 *
 * Oder als größeres Beispiel:
 * <br />
 * <code>
 *     raum.physik.masse(80).reibung(0.3f).kraftWirken(new Vektor(10, -30)).drehimpuls(5);
 * </code>
 *
 * Created by andonie on 16.02.15.
 */
@API
public class Physik {

    /**
     * Das Raum-Objekt, zu dem dieses <code>Physik</code>-Objekt gehört.
     */
    private final Raum raum;


    /**
     * Interner Konstruktor. Wird nicht von außerhalb der Engine genutzt. Ein <code>Physik</code>-Objekt wird von seinem
     * <code>Raum</code>-Parent erzeugt.
     * @param raum  Das <code>Raum</code>-Objekt, zu dem dieses <code>Physik</code>-Objekt ab sofort gehört.
     */
    @NoExternalUse
    public Physik(Raum raum) {
        this.raum = raum;
    }


    /* _________________________ Einheiten / Passive Eigenschaften _________________________ */

    /**
     * Setzt, ob <i>im Rahmen der physikalischen Simulation</i> die Rotation dieses Objekts
     * blockiert werden soll. <br/>
     * Das Objekt kann in jedem Fall weiterhin über einen direkten Methodenaufruf rotiert
     * werden. Der folgende Code ist immer wirksam, unabhängig davon, ob die Rotation
     * im Rahmen der physikalischen Simulation blockiert ist:<br />
     * <code>
     *     raum.getPosition.rotieren(4.31f);
     * </code>
     * @param rotationBlockiert
     *                  Ist dieser Wert <code>true</code>, rotiert sich dieses
     *                  Objekts innerhalb der physikalischen Simulation <b>nicht mehr</b>.
     *                  Ist dieser Wert <code>false</code>, rotiert sich dieses
     *                  Objekt innerhalb der physikalsichen Simulation.
     * @return          Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                  Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     * @see #rotationBlockiert()
     */
    @API
    public Physik rotationBlockiertSetzen(boolean rotationBlockiert) {
        raum.getPhysikHandler().rotationBlockiertSetzen(rotationBlockiert);
        return this;
    }

    /**
     * Gibt an, ob die Rotation dieses Objekts derzeit innerhalb der physikalischen Simulation
     * blockiert ist.
     * @return          <code>true</code>, wenn die Rotation dieses Objekts derzeit innerhalb der
     *                  physikalischen Simulation blockiert ist.
     * @see #rotationBlockiertSetzen(boolean)
     */
    @API
    public boolean rotationBlockiert() {
        return raum.getPhysikHandler().rotationBlockiert();
    }

    /**
     * Setzt die Masse des Objekts neu. Hat Einfluss auf das physikalische Verhalten des Objekts.
     * @param masseInKG Die neue Masse für das Objekt in <b>[kg]</b>.
     * @return          Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                  Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physik masse(float masseInKG) {
        raum.getPhysikHandler().masseSetzen(masseInKG);
        return this;
    }

    /**
     * Gibt die aktuelle Masse des Ziel-Objekts aus. Die Form bleibt unverändert, daher ändert sich
     * die <b>Dichte</b> in der Regel.
     * @return  Die Masse des Ziel-Objekts in <b>[kg]</b>.
     */
    @API
    public float masse() {
        return raum.getPhysikHandler().masse();
    }

    /**
     * Setzt die Dichte des Objekts neu. Die Form bleibt dabei unverändert, daher ändert sich die
     * <b>Masse</b> in der Regel.
     * @param dichteInKgProQM   die neue Dichte des Objekts in <b>[kg/m^2]</b>
     * @return                  Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                          Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physik dichte(float dichteInKgProQM) {
        raum.getPhysikHandler().dichteSetzen(dichteInKgProQM);
        return this;
    }

    /**
     * Gibt die aktuelle Dichte des Objekts an.
     * @return  Die aktuelle Dichte des Objekts in <b>[kg/m^2]</b>.
     */
    @API
    public float dichte() {
        return raum.getPhysikHandler().dichte();
    }

    /**
     * Setzt den Reibungskoeffizient für das Objekt. Hat Einfluss auf
     * die Bewegung des Objekts.
     * @param reibungskoeffizient   Der Reibungskoeffizient. In der Regel im Bereich <b>[0; 1]</b>.
     * @return                      Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                              Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physik reibung(float reibungskoeffizient) {
        raum.getPhysikHandler().reibungSetzen(reibungskoeffizient);
        return this;
    }

    /**
     * Gibt den Reibungskoeffizienten für dieses Objekt aus.
     * @return  Der Reibungskoeffizient des Objekts. Ist in der Regel (in der Realität)
     *          ein Wert im Bereich <b>[0; 1]</b>.
     */
    @API
    public float reibung() {
        return raum.getPhysikHandler().reibung();
    }

    /**
     * Setzt die Geschwindigkeit "hart" für dieses Objekt. Damit wird die aktuelle
     * Bewegung (nicht aber die Rotation) des Objekts ignoriert und hart auf den
     * übergebenen Wert gesetzt.
     * @param geschwindigkeitInMProS    Die Geschwindigkeit, mit der sich dieses Objekt ab sofort
     *                                  bewegen soll. In <b>[m / s]</b>
     * @return                          Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                                  Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physik geschwindigkeit(Vektor geschwindigkeitInMProS) {
        raum.getPhysikHandler().geschwindigkeitSetzen(geschwindigkeitInMProS);
        return this;
    }

    /**
     * Gibt die Geschwindigkeit aus, mit der sich dieses Objekt gerade (also in diesem Frame) bewegt.
     * @return  Die Geschwindigkeit, mit der sich dieses Objekt gerade (also in diesem Frame) bewegt.
     *          In <b>[m / s]</b>
     */
    @API
    public Vektor geschwindigkeit() {
        return raum.getPhysikHandler().geschwindigkeit();
    }

    public Physik elastizitaet(float elastizitaet) {
        raum.getPhysikHandler().elastizitaetSetzen(elastizitaet);
        return this;
    }

    @API
    public float elastizitaet() {
        return raum.getPhysikHandler().elastizitaet();
    }

    /* _________________________ World-Wrap _________________________ */

    /**
     * Setzt die Schwerkraft, die auf <b>alle Objekte innerhalb des Fensters</b> wirkt.
     * @param schwerkraftInN    Die neue Schwerkraft als Vektor. Die Einheit ist <b>[N]</b>.
     * @return                  Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                          Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physik schwerkraft(Vektor schwerkraftInN) {
        raum.getPhysikHandler().schwerkraftSetzen(schwerkraftInN);
        return this;
    }


    /* _________________________ Doers : Direkter Effekt auf Simulation _________________________ */

    /**
     * Wirkt eine Kraft auf den <i>Schwerpunkt</i> des Objekts.
     * @param kraftInN  Ein Kraft-Vektor. Einheit ist <b>nicht [px], sonder [N]</b.
     * @return          Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                  Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physik kraftWirken(Vektor kraftInN) {
        raum.getPhysikHandler().kraftWirken(kraftInN);
        return this;
    }

    /**
     * Wirkt eine Kraft auf einem bestimmten <i>Punkt in der Welt</i>.
     * @param kraftInN              Eine Kraft. Einheit ist <b>[N]</b>
     * @param globalerPunkt         Der Ort auf der <i>Zeichenebene</i>, an dem die Kraft wirken soll.
     * @return                      Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                              Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physik kraftWirken(Vektor kraftInN, Punkt globalerPunkt) {
        raum.getPhysikHandler().kraftWirken(kraftInN, globalerPunkt);
        return this;
    }

    /**
     * Wirkt einen Impuls auf den <i>Schwerpunkt</i> des Objekts.
     * @param impulsInNS    Der Impuls, der auf den Schwerpunkt wirken soll. Einheit ist <b>[Ns]</b>
     * @return              Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                      Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physik impulsWirken(Vektor impulsInNS) {
        raum.getPhysikHandler().impulsWirken(impulsInNS, raum.getPhysikHandler().mittelpunkt());
        return this;
    }

    /**
     * Wirkt einen Impuls an einem bestimmten <i>Punkt in der Welt</i>.
     * @param impulsInNS    Ein Impuls. Einheit ist <b>[Ns]</b>
     * @param globalerOrt   Der Ort auf der <i>Zeichenebene</i>, an dem der Impuls wirken soll.
     * @return              Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                      Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physik impulsWirken(Vektor impulsInNS, Punkt globalerOrt) {
        raum.getPhysikHandler().impulsWirken(impulsInNS, globalerOrt);
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
    public Physik inRuheVersetzen() {
        raum.getPhysikHandler().physicalReset();
        return this;
    }

    /* _________________________ JOINTS _________________________ */

    /**
     * Prüft ob das zugehörige <code>Raum</code>-Objekt in der selben JB2D World liegt wie das übergebene Objekt.
     * Diese Logik ist ausgelagert, um den Code etwas schöner zu machen.
     * @param other ein zweites <code>Raum</code>-Objekt zum testen.
     * @return true = beide Objekte liegen in der selben World. Sonst false.
     */
    @NoExternalUse
    private boolean assertSameWorld(Raum other) {
        if(other.getPhysikHandler().worldHandler() != raum.getPhysikHandler().worldHandler()) {
            Logger.error("Physik", "Die Raum-Objekte sind nicht an der selben Wurzel angemeldet. Sie können " +
                    "deshalb (noch) nicht physikalisch verbunden werden.");
            return false;
        }
        return true;
    }

    /**
     * Erstellt einen Revolute-Joint zwischen dem zugehörigen <code>Raum</code>-Objekt und einem weiteren.
     *
     * <h3>Definition Revolute-Joint</h3>
     * <p>Verbindet zwei <code>Raum</code>-Objekte <b>untrennbar an einem Anker-Punkt</b>. Die Objekte können sich
     * ab sofort nur noch <b>relativ zueinander drehen</b>.</p>
     * @param other     Das zweite <code>Raum</code>-Objekt, das ab sofort mit dem zugehörigen <code>Raum</code>-Objekt
     *                  über einen <code>RevoluteJoint</code> verbunden sein soll.
     * @param anchor    Der Ankerpunkt <b>auf der Zeichenebene</b>. Es wird davon
     *                  ausgegangen, dass beide Objekte bereits korrekt positioniert sind.
     * @return          Ein <code>RevoluteJoint</code>-Objekt, mit dem der Joint weiter gesteuert werden kann.
     * @see org.jbox2d.dynamics.joints.RevoluteJoint
     */
    @API
    public RevoluteJoint createRevoluteJoint(Raum other, Vektor anchor) {
        if(!assertSameWorld(other)) return null;

        //Definiere den Joint
        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef.initialize(raum.getPhysikHandler().getBody(), other.getPhysikHandler().getBody(),
                //raum.physikHandler.worldHandler().fromVektor(raum.getPosition.get().alsVektor().summe(anchor)));
                raum.getPhysikHandler().worldHandler().fromVektor(anchor));
        revoluteJointDef.collideConnected = false;

        return (RevoluteJoint) raum.getPhysikHandler().worldHandler().getWorld().createJoint(revoluteJointDef);
    }

    /**
     * Erstellt einen Rope-Joint zwischen diesem und einem weiteren <code>Raum</code>-Objekt.
     * @param other     Das zweite <code>Raum</code>-Objekt, das ab sofort mit dem zugehörigen <code>Raum</code>-Objekt
     *                  über einen <code>RopeJoint</code> verbunden sein soll.
     * @param anchorA   Der Ankerpunkt für das zugehörige <code>Raum</code>-Objekt. Der erste Befestigungspunkt
     *                  des Lassos. Angabe relativ zur Position vom zugehörigen Objekt.
     * @param anchorB   Der Ankerpunkt für das zweite <code>Raum</code>-Objekt, also <code>other</code>.
     *                  Der zweite Befestigungspunkt des Lassos. Angabe relativ zur Position vom zugehörigen Objekt.
     * @param ropeLength    Die Länge des Lassos. Dies ist ab sofort die maximale Länge, die die beiden Ankerpunkte
     *                      der Objekte voneinader entfernt sein können.
     * @return  Ein <code>RopeJoint</code>-Objekt, mit dem der Joint weiter gesteuert werden kann.
     * @see org.jbox2d.dynamics.joints.RopeJoint
     */
    @API
    public RopeJoint createRopeJoint(Raum other, Vektor anchorA, Vektor anchorB, float ropeLength) {
        if(!assertSameWorld(other)) return null;

        RopeJointDef ropeJointDef = new RopeJointDef();
        ropeJointDef.bodyA = raum .getPhysikHandler().getBody();
        ropeJointDef.bodyB = other.getPhysikHandler().getBody();

        ropeJointDef.localAnchorA.set(raum.getPhysikHandler().worldHandler().fromVektor(anchorA));
        ropeJointDef.localAnchorB.set(raum.getPhysikHandler().worldHandler().fromVektor(anchorB));
        ropeJointDef.maxLength = ropeLength;

        return (RopeJoint) raum.getPhysikHandler().worldHandler().getWorld().createJoint(ropeJointDef);

    }

    /**
     * Erstellt einen Distance-Joint zwischen diesem und einem weiteren <code>Raum</code>-Objekt.
     * @param other     Das zweite <code>Raum</code>-Objekt, das ab sofort mit dem zugehörigen <code>Raum</code>-Objekt
     *                  über einen <code>DistanceJoint</code> verbunden sein soll.
     * @param anchorAAsWorldPos Der Ankerpunkt für das zugehörige <code>Raum</code>-Objekt. Der erste Befestigungspunkt
     *                  des Joints. Angabe als <b>Position auf der Zeichenebene</b>, also absolut.
     * @param anchorBAsWorldPos Der Ankerpunkt für das zweite <code>Raum</code>-Objekt, also <code>other</code>.
     *                  Der zweite Befestigungspunkt des Joints.
     *                  Angabe als <b>Position auf der Zeichenebene</b>, also absolut.
     * @return          Ein <code>DistanceJoint</code>-Objekt, mit dem der Joint weiter gesteuert werden kann.
     * @see org.jbox2d.dynamics.joints.DistanceJoint
     */
    @API
    public DistanceJoint createDistanceJoint(Raum other, Vektor anchorAAsWorldPos, Vektor anchorBAsWorldPos) {
        if(!assertSameWorld(other)) return null;

        DistanceJointDef distanceJointDef = new DistanceJointDef();
        distanceJointDef.initialize(
                raum.getPhysikHandler().getBody(),
                other.getPhysikHandler().getBody(),
                raum.getPhysikHandler().worldHandler().fromVektor(anchorAAsWorldPos),
                raum.getPhysikHandler().worldHandler().fromVektor(anchorBAsWorldPos));

        return (DistanceJoint) raum.getPhysikHandler().worldHandler().getWorld().createJoint(distanceJointDef);
    }

    /* _________________________ Physik-Typ _________________________ */

    /**
     * Setzt, was für eine Typ physikalisches Objekt das Objekt sein soll. Erläuterung findet
     * sich im <code>enum Typ</code>.
     * @param typ   Der Typ Physik-Objekt, der ab sofort dieses Objekt sein soll.
     * @return      Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *              Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     * @see Physik.Typ
     */
    @API
    public Physik typ(Typ typ) {
        raum.bodyTypeSetzen(typ);
        return this;
    }

    /**
     * Gibt aus, was für ein Typ Physik-Objekt dieses Objekt momentan ist.
     * @return  der Typ Physik-Objekt, der das entsprechende <code>Raum</code>-Objekt momentan ist.
     * @see Physik.Typ
     */
    @API
    public Typ typ() {
        return raum.getPhysikHandler().typ();
    }

    /**
     * Aufzählung der drei verschiedenen Typen von Objekten innerhalb der Physik der EA.
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
     *             <li>Kollidieren (im Sinne der Physik) nur mit dynamischen Objekten.</li>
     *         </ul>
     *         Doese Eigenschaft gehört zum Beispiel zu <i>beweglichen Plattformen</i>.
     *     </li>
     *     <li>
     *         <b>Passive</b> Objekte:
     *         <ul>
     *              <li>Nehmen nicht an der Physik teil. Sie werden von der Physik so behandelt,
     *              <i>als wären sie nicht da</i>.</li>
     *              <li>Dies ist die <b>Standardeinstellung</b> für Objekte.</li>
     *         </ul>
     *     </li>
     * </ul>
     * @see #typ(Physik.Typ)
     * @see #typ()
     */
    @API
    public enum Typ {
        STATISCH, DYNAMISCH, KINEMATISCH, PASSIV;

        /**
         * Konvertierungsmethode zwischen Engine-Physiktyp und JB2D-Physiktyp.
         * @return      Der zugehörige JB2D-Phy-Typ zu diesem Engine-Phy-Typ.
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
