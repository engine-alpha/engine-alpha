package ea.internal.phy;

import ea.*;
import ea.internal.ano.NoExternalUse;
import ea.internal.util.Logger;
import org.jbox2d.collision.shapes.MassData;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * Beschreibt ein Objekt, dass die physikalischen Eigenschaften eines Raum-Objektes kontrollieren kann.
 * Dazu gehört:
 * <ul>
 *     <li>Das <code>Raum</code>-Objekt <b>bewegen</b>.</li>
 *     <li><b>Physikalische Eigenschaften</b> des Objektes verändern (wie Masse, Reibungskoeffizient etc.)</li>
 *     <li><b>Einflüsse</b> auf das <code>Raum</code>-Objekt ausüben (wie anwenden von Impulsen / Kräften)</li>
 * </ul>
 * Created by andonie on 16.02.15.
 */
public class PhysikHandler {


    /**
     * Die Eigenschaften, die ein Body standardmäßig in der EA hat:
     * <ul>
     *     <li>Der JB2D-Physik Typ (intern) ist <i>statisch</i>.</li>
     *     <li>Das Physik-Verhalten innerhalb der EA ist <i>inaktiv</i> (= <i>Sensor</i>
     *         in JB2D).</li>
     *     <li>Die interne <i>Gravity Scale</i> ist 1 (Gravity wirkt mit normal auf das Objekt)</li>
     *     <li>Das Objekt ist JB2D-intern <i>inactive</i>, wird also in Berechnungen
     *         (explizit Collision Detection) nicht einbezogen. Dies ändert sich, wenn:
     *         <ul>
     *             <li>Eine Kollisionsüberwachung mit diesem Objekt angemeldet wird.</li>
     *             <li>Das Objekt in der EA Physik angemeldet wird.</li>
     *         </ul>
     *     </li>
     *     <li>Die Position ist standardmäßig der Koordinatenursprung (0|0)</li>
     * </ul>
     */
    private static final BodyDef    standardBody;

    /**
     * Eigenschaften, die eine Fixture standardmäßig in der EA hat:
     * <ul>
     *     <li><i>Restitution</i> ist 0. Das Objekt ist unelastisch.</li>
     *     <li><i>Reibungswert</i> ist 0.2 .</li>
     *     <li>Der <i>Dichtewert</i> ist 0.</li>
     *     <li>Das Objekt ist standardmäßig <i>ein Sensor</i>, interagiert also nicht
     *         physikalisch auf irgendeine Weise, sondern ist nur für Kollisionschecks
     *         da. (dies wird für Physik-Objekte geändert)</li>
     * </ul>
     */
    private static final FixtureDef standardFixture;

    /**
     * Statischer Konstruktor erstellt die Standard-Definitionen für
     * Fixture und Body.
     */
    static {
        standardBody = new BodyDef();
        standardBody.active = false;

        standardFixture = new FixtureDef();
        standardFixture.isSensor = true;
    }


    /**
     * Das eine Raum-Objekt, das dieser Handler kontrolliert.
     */
    protected final Raum raum;

    /**
     * Der (JB2D) Body, der zu dem Raum-Objekt korrespondiert, zu dem dieser
     * Handler gehört.
     */
    private Body body = null;

    /**
     * Der Physik-Typ des korrespondierenden Raum-Objekts. Folgende Typen gibt es:
     * <ul>
     *     <li><b>Passiv<b>: Keine Interaktion mit der Physik. Kollisionstests sind möglich.</b></li>
     *     <li><b>Statisch</b>: "Wand": Feste Position, Impulse werden nicht auf das Objekt übertragen,
     *         Kräfte wirken nicht an diesem Objekt.</li>
     *     <li><b>Kinematisch</b>: "Plattform": Kann Geschwindigkeit haben, überträgt Impulse, selbst wirken
     *         keine Kräfte hierauf.</li>
     *     <li><b>Dynamisch</b>: Voll newton'sches Objekt, auf das Kräfte wirken.</li>
     * </ul>
     * Der Typ ist initial stets passiv.
     */
    private Physik.Typ physiktyp = Physik.Typ.PASSIV;

    /**
     * Referenz auf den Repräsentat der Physik-Umgebung, zu der das korrespondierende
     * Raum-Objekt gehört.
     */
    private WorldHandler worldHandler = null;

    /**
     * Initialisiert den Handler.
     * @param raum  Das eine Raum-Objekt, das dieser Handler kontrolliert.
     */
    public PhysikHandler(Raum raum) {
        this.raum = raum;
    }

    /**
     * Informiert diesen Handler, wenn es ein Update in der Baumstruktur um das Raum-Objekt gab. Die neue Physik (falls vorhanden)
     * wird so an alle Raum-Objekte weitergegeben.
     * @param worldHandler      Die neue Physik-Umgebung.
     * @throws java.lang.IllegalStateException  Falls ein Raum-Objekt nach dem anmelden an einer Wurzel auf die Wurzel eines anderen Fensters
     *                                          verschoben wird.
     */
    public void update(WorldHandler worldHandler) throws IllegalStateException {

        if(worldHandler == null)
            return;

        if(this.worldHandler != null && this.worldHandler != worldHandler) {
            Logger.error("Wurzel", "Ein Raum-Objekt wurde an einer zweiten Wurzel angemeldet. Dies ist nicht " +
                    "möglich.");
            return;
        }

        this.worldHandler = worldHandler;


        if(! (this.raum instanceof Knoten)) worldHandler.blockPPMChanges();
    }


    @NoExternalUse
    public Body getBody() {
        if(body == null) {
            if(worldHandler == null) {
                //ERROR: Kann keinen Body ohne World erstellen
                Logger.error("Zeichenumgebung", "Kann das Objekt nicht in Zeichenebene einordnen, da es nicht mit " +
                        "einer Wurzel verbunden ist. Vorher an Wurzel anmelden.");
                return null;
            }
            //CREATE BODY

            //Bringe neuen Body in die Welt
            this.body = worldHandler.createBody(standardBody);

            //Gib Body die nötige Fixture
            standardFixture.shape = raum.berechneShape(worldHandler.getPixelProMeter());
            body.createFixture(standardFixture);
            standardFixture.shape = null;

            return body;
        } else {
            //Body wurde schon erschaffen, zurückgeben
            return this.body;
        }
    }

    /**
     * Prüfroutine, die checkt, ob bereits ein Body für das korrespondierende Raum-Objekt erstellt wurde.
     * @return  <code>true</code>, wenn es bereits einen Body für das Raum-Objekt gibt. Sonst
     *          <code>false</code>.
     */
    private boolean checkBody() {
        Body body = getBody();
        return body != null;
    }

    /**
     * Verschiebt das Ziel-Objekt um einen spezifischen Wert auf der Zeichenebene. Die Ausführung hat <b>erst (ggf.) im
     * kommenden Frame</b> einfluss auf die Physik und <b>ändert keine physikalischen Eigenschaften</b> des Ziel-Objekts
     * (außer dessen Position).
     * @param v     Ein Vektor, um den das Ziel-Objekt verschoben werden soll. Dies ändert seine Position, jedoch sonst
     *              keine weiteren Eigenschaften.
     */
    public void verschieben(Vektor v) {
        if(!checkBody()) return;

        Vec2 newposition = worldHandler.fromVektor(worldHandler.fromVec2(body.getPosition()).summe(v));

        body.setTransform(newposition, body.getAngle());
    }

    /**
     * Gibt den <b>Gewichtsmittelpunkt</b> dieses <code>Raum</code>-Objekts aus.
     * @return  der aktuelle <b>Gewichtsmittelpunkt</b> des Ziel-Objekts als <i>Punkt auf der Zeichenebene</i>.
     */
    public Punkt mittelpunkt() {
        if(!checkBody()) return null;

        Vec2 wc = body.getWorldCenter();
        return worldHandler.fromVec2(wc).alsPunkt();
    }

    /**
     * Gibt die aktuelle Position des Ziel-Objekts an.
     * @return  Die aktuelle Position des Ziel-Objekts. Diese ist bei Erstellung des Objekts zunächst immer
     *          <code>(0|0)</code> und wird mit Rotation und Verschiebung verändert.
     */
    public Punkt position() {
        if(!checkBody()) return null;

        return worldHandler.fromVec2(body.getPosition()).alsPunkt();
    }

    /**
     * Gibt die aktuelle Rotation des Ziel-Objekts in <i>Radians</i> an. Bei Erstellung eines
     * <code>Raum</code>-Objekts ist seine Rotation stets 0.
     * @return  die aktuelle Rotation des Ziel-Objekts in <i>Radians</i>.
     */
    public float rotation() {
        if(!checkBody()) return -1;

        return body.getAngle();
    }

    public void rotationFixiertSetzen(boolean fixiert) {
        if(!checkBody()) return;

        body.setFixedRotation(fixiert);
    }

    public boolean rotationFixiert() {
        if(!checkBody()) return false;

        return body.isFixedRotation();
    }

    /**
     * Rotiert das Ziel-Objekt um einen festen Winkel.
     * @param radians   Der Winkel, um den das Ziel-Objekt gedreht werden soll (in <i>Radians</i>).
     *                  <ul>
     *                      <li>Werte > 0 : Drehung gegen Uhrzeigersinn</li>
     *                      <li>Werte < 0 : Drehung im Uhrzeigersinn</li>
     *                  </ul>
     */
    public void rotieren(float radians) {
        if(!checkBody()) return;

        body.setTransform(body.getPosition(), body.getAngle() + radians);
    }

    public void dichteSetzen(float dichte) {
        if(!checkBody()) return;

        body.getFixtureList().setDensity(dichte);
    }

    public float dichte() {
        if(!checkBody()) return -1;

        return body.getFixtureList().getDensity();
    }

    public void reibungSetzen(float reibung) {
        if(!checkBody()) return;

        body.getFixtureList().setFriction(reibung);
    }

    public float reibung() {
        if(!checkBody()) return -1;

        return body.getFixtureList().getFriction();
    }

    public void elastizitaetSetzen(float ela) {
        if(!checkBody()) return;

        if(ela < 0 || ela > 1) {
            Logger.error("Physik", "Die Elastizität muss zwischen 0 (unelastisch) und 1 (perfekt elastisch) " +
                    "liegen. War: " + ela + " .");
            return;
        }

        body.getFixtureList().setRestitution(ela);
    }

    public float elastizitaet() {
        if(!checkBody()) return -1;

        return body.getFixtureList().getRestitution();
    }

    /**
     * Setzt die Masse für das Ziel-Objekt.
     * @param masse Die Masse, die das Ziel-Objekt einnehmen soll. In [kg]
     */
    public void masseSetzen(float masse) {
        if(!checkBody()) return;

        if(masse <= 0) {
            Logger.error("Physik", "Masse muss größer als 0 sein. War: " + masse + " .");
            return;
        }

        MassData data = new MassData();
        body.getMassData(data);
        data.mass = masse;
        body.setMassData(data);
    }

    /**
     * Gibt die Masse des Ziel-Objekts aus.
     * @return  Die Masse des Ziel-Objekts in [kg].
     */
    public float masse() {
        if(!checkBody()) return -1;

        return body.getMass();
    }

    /**
     * Uebt eine Kraft auf das Ziel-Objekt (im Massenschwerpunkt) aus (sofern möglich).
     * @param kraft Die Kraft, die auf den Massenschwerpunkt angewandt werden soll. <b>Nicht in [px]</b>, sondern in
     *              [N] = [m / s^2].
     */
    public void kraftWirken(Vektor kraft) {
        if(!checkBody()) return;

        body.applyForceToCenter(new Vec2(kraft.x, kraft.y));
    }

    /**
     * Wirkt einen Drehmoment auf das Ziel-Objekt.
     * @param drehmoment    der Drehmoment, der auf das Ziel-Objekt wirken soll. In [N*m]
     *
     */
    public void drehMomentWirken(float drehmoment) {
        if(!checkBody()) return;


    }

    /**
     * Wirkt einen Drehimpuls auf das Ziel-Objekt.
     * @param drehimpuls    der Drehimpuls, der auf das Ziel-Objekt wirken soll. in [kg*m*m/s]
     */
    public void drehImpulsWirken(float drehimpuls) {
        if(!checkBody()) return;


    }

    /**
     * Setzt global für die Physik-Umgebung, in der sich das Zielobjekt befindet, die Schwerkraft neu.
     * @param schwerkraftInN    die neue Schwerkraft als Vektor. in [N].
     */
    public void schwerkraftSetzen(Vektor schwerkraftInN) {
        if(!checkBody()) return;

        worldHandler.getWorld().setGravity(new Vec2(schwerkraftInN.x, schwerkraftInN.y));
    }

    /**
     * Macht ein Typ-Update für diesen Handler.
     * @param typ   Der neue Typ.
     * @return      Ein Handler, der diesen Typ behandelt (ggf. this).
     */
    public void typ(Physik.Typ typ) {
        if(!checkBody()) return;

        BodyType newType = typ.convert();
        if(newType == this.body.getType()) {
            return;
        }

        switch(typ) {
            case PASSIV:
                body.getFixtureList().setSensor(true);
                break;
            default:
                body.getFixtureList().setSensor(false);
                body.setActive(true);
                break;
        }

        body.setType(newType);
    }

    public Physik.Typ typ() {
        return physiktyp;
    }

    public void kraftWirken(Vektor kraftInN, Punkt globalerOrt) {
        if(!checkBody()) return;


    }

    /**
     * Wirkt einen Impuls auf einem Welt-Punkt.
     * @param impulsInNS        Ein Impuls (in [Ns]).
     * @param globalerOrt       Der
     */
    public void impulsWirken(Vektor impulsInNS, Punkt globalerOrt) {
        if(!checkBody()) return;


    }
}
