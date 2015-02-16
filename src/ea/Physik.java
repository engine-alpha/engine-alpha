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


    /* _________________________ API-Methoden _________________________ */

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


    /**
     * Wirkt eine Kraft auf den <i>Schwerpunkt</i> des Objekts.
     * @param kraftInN  Ein Kraft-Vektor. Einheit ist <b>nicht [px], sonder [N]</b.
     * @return          Das ausführende Objekt (also sinngemäß <code>return this;</code>).
     *                  Für <b>Chaining</b> von Methoden (siehe Dokumentation der Klasse).
     */
    @API
    public Physik kraftWirken(Vektor kraftInN) {
        //TODO
        return this;
    }

    //public Physik impulsWirken(Vektor impulsIn)

}
