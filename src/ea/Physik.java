package ea;

import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;

/**
 * Jedes <code>Raum</code>-Objekt hat ein öffentlich erreichbares Objekt <code>physik</code>.
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
    Physik(Raum raum) {
        this.raum = raum;
    }


    /* _________________________ Einheiten / Eigenschaften _________________________ */

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


    /* _________________________ World-Wrap _________________________ */

    /**
     * Setzt die Schwerkraft, die auf <b>alle Objekte innerhalb des Fensters</b> wirkt.
     * @param schwerkraftInN    Die neue Schwerkraft als Vektor. Die Einheit ist <b>[N]</b>.
     * @return                  Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                          Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
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
    public Physik impulsWirken(Vektor impulsInNS, Punkt globalerOrt) {
        raum.getPhysikHandler().impulsWirken(impulsInNS, globalerOrt);
        return this;
    }


    /* _________________________ Physik-Typ _________________________ */

    /**
     * Setzt, was für eine Typ physikalisches Objekt das Objekt sein soll. Erläuterung findet
     * sich im <code>enum Typ</code>.
     * @param typ   Der Typ Physik-Objekt, der ab sofort dieses Objekt sein soll.
     * @return      Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *              Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     * @see ea.Physik.Typ
     */
    @API
    public Physik typ(Typ typ) {
        raum.getPhysikHandler().typ(typ);
        return this;
    }

    /**
     * Gibt aus, was für ein Typ Physik-Objekt dieses Objekt momentan ist.
     * @return  der Typ Physik-Objekt, der das entsprechende <code>Raum</code>-Objekt momentan ist.
     * @see ea.Physik.Typ
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
     *             <li>Können eine Geschwindigkeit haben, aber reagieren nicht auf Kräfte.</li>
     *             <li>Kollidieren (im Sinne der Physik) nur mit dynamischen Objekten.</li>
     *         </ul>
     *         Doese Eigenschaft gehört zum Beispiel zu <i>beweglichen Plattformen</i>.
     *     </li>
     * </ul>
     * @see #typ(ea.Physik.Typ)
     * @see #typ()
     */
    @API
    public enum Typ {
        STATISCH, DYNAMISCH, KINEMATISCH
    }

}
