package ea.internal.phy;

import ea.Physik;
import ea.Punkt;
import ea.Raum;
import ea.Vektor;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

/**
 * Beschreibt allgemein ein Objekt, dass die physikalischen Eigenschaften eines Raum-Objektes kontrollieren kann.
 * Dazu gehört:
 * <ul>
 *     <li>Das <code>Raum</code>-Objekt <b>bewegen</b>.</li>
 *     <li><b>Physikalische Eigenschaften</b> des Objektes verändern (wie Masse, Reibungskoeffizient etc.)</li>
 *     <li><b>Einflüsse</b> auf das <code>Raum</code>-Objekt ausüben (wie anwenden von Impulsen / Kräften)</li>
 * </ul>
 * Created by andonie on 16.02.15.
 */
public abstract class PhysikHandler {



    /**
     * Das eine Raum-Objekt, das dieser Handler kontrolliert.
     */
    protected final Raum raum;

    /**
     * Der physikalische Typ, den der Klient gerade fährt.
     */
    protected Physik.Typ physikTyp;

    /**
     * Diese Variable speichert die Sensor-Flag des Klienten.
     * Die Sensor-Flag is true, wenn ein passives Objekt (= keine Physik) trotzdem
     * an Kollisionstests teilnehmen soll.
     */
    protected boolean isSensor;

    /**
     * Initialisiert den Physik-Handler.
     * @param raum  Das eine Raum-Objekt, das dieser Handler kontrolliert.
     */
    protected PhysikHandler(Raum raum, Physik.Typ physikTyp, boolean isSensor) {
        this.raum = raum;
        this.physikTyp = physikTyp;
        this.isSensor = isSensor;
    }

    /* __________________________ Kontrakt: Abstrakte Methoden/Funktionen der Physik __________________________ */

    /**
     * Informiert diesen Handler, wenn es ein Update in der Baumstruktur um das Raum-Objekt gab. Die neue Physik (falls vorhanden)
     * wird so an alle Raum-Objekte weitergegeben.
     * @param worldHandler      Die neue Physik-Umgebung.
     * @throws java.lang.IllegalStateException  Falls ein Raum-Objekt nach dem anmelden an einer Wurzel auf die Wurzel eines anderen Fensters
     *                                          verschoben wird.
     */
    public abstract void update(WorldHandler worldHandler) throws IllegalStateException;

    /**
     * Verschiebt das Ziel-Objekt um einen spezifischen Wert auf der Zeichenebene. Die Ausführung hat <b>erst (ggf.) im
     * kommenden Frame</b> einfluss auf die Physik und <b>ändert keine physikalischen Eigenschaften</b> des Ziel-Objekts
     * (außer dessen Ort).
     * @param v     Ein Vektor, um den das Ziel-Objekt verschoben werden soll. Dies ändert seine Position, jedoch sonst
     *              keine weiteren Eigenschaften.
     */
    public abstract void verschieben(Vektor v);

    /**
     * Gibt den <b>Gewichtsmittelpunkt</b> dieses <code>Raum</code>-Objekts aus.
     * @return  der aktuelle <b>Gewichtsmittelpunkt</b> des Ziel-Objekts als <i>Punkt auf der Zeichenebene</i>.
     */
    public abstract Punkt mittelpunkt();

    /**
     * Gibt an, ob ein <code>Raum</code>-Objekt ein anderes schneidet. Wie genau diese Methode arbeitet, hängt von der
     * implementierenden Klasse an. Sie kann eine überschaubare Heuristik sein oder ein komplexe Berechnung.
     * @param r Ein anderes <code>Raum</code>-Objekt.
     * @return  <code>true</code>, wenn sich das Ziel-Objekt und das übergebene Objekt (nach ggf. heuristischer
     *          Berechnung) schneiden, sonst <code>false</code>.
     */
    public abstract boolean schneidet(Raum r);

    /**
     * Gibt an, ob ein bestimmter Punkt auf der Zeichenebene innerhalb des Ziel-Objekts liegt.
     * @param p Ein Punkt auf der Zeichenebene.
     * @return  <code>true</code>, wenn der übergebene Punkt innerhalb des Ziel-Objekts liegt, sonst <code>false</code>.
     *          Das Ergebnis kann (abhängig von der implementierenden Klasse) verschieden sicher richtige Ergebnisse
     *          liefern.
     */
    public abstract boolean beinhaltet(Punkt p);

    /**
     * Gibt die aktuelle Position des Ziel-Objekts an.
     * @return  Die aktuelle Position des Ziel-Objekts. Diese ist bei Erstellung des Objekts zunächst immer
     *          <code>(0|0)</code> und wird mit Rotation und Verschiebung verändert.
     */
    public abstract Punkt position();

    /**
     * Gibt die aktuelle Rotation des Ziel-Objekts in <i>Radians</i> an. Bei Erstellung eines
     * <code>Raum</code>-Objekts ist seine Rotation stets 0.
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
    public abstract void kraftWirken(Vektor kraft);

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
     * Setzt global für die Physik-Umgebung, in der sich das Zielobjekt befindet, die Schwerkraft neu.
     * @param schwerkraftInN    die neue Schwerkraft als Vektor. in [N].
     */
    public abstract void schwerkraftSetzen(Vektor schwerkraftInN);

    /**
     * Macht ein Typ-Update für diesen Handler.
     * @param typ   Der neue Typ.
     * @return      Ein Handler, der diesen Typ behandelt (ggf. this).
     */
    public abstract PhysikHandler typ(Physik.Typ typ);

    public Physik.Typ typ() {
        return physikTyp;
    }

    public abstract void kraftWirken(Vektor kraftInN, Punkt globalerOrt);

    /**
     * Wirkt einen Impuls auf einem Welt-Punkt.
     * @param impulsInNS        Ein Impuls (in [Ns]).
     * @param globalerOrt       Der
     */
    public abstract void impulsWirken(Vektor impulsInNS, Punkt globalerOrt);

    /**
     * Entfernt den Körper von diesem Handler.
     * Danach ist das Objekt physikalisch nicht mehr existent.
     */
    public abstract void killBody();

    /**
     * Gibt den WorldHandler aus, der die Welt handelt, in der sich der Klient
     * befindet.
     * @return  Der World-Handler, der zu diesem Physik-Handler gehört.
     */
    public abstract WorldHandler worldHandler();
}
