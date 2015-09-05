package ea;

import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;

/**
 * Jedes <code>Raum</code>-Objekt hat ein öffentlich erreichbares Objekt <code>position</code>.
 * Dieses Objekt bietet eine umfangreiches Set an <i>Methoden</i>, die die Position des entsprechenden
 * <code>Raum</code>-Objekts betreffen.<br /><br />
 *
 * Alle Methoden, die keine "richtige" Rückgabe hätten (also <code>void</code>-Methoden), sind mit <b>Chaining</b>
 * versehen. Das bedeutet, dass statt bei jeder Methode, die eigentlich vom <code>void</code>-Typ wäre,
 * der Rückgabetyp <code>Position</code> ist und die Rückgabe das Objekt, das die Methode ausgeführt hat. Das ermöglicht
 * übersichtlichere Codes:<br />
 * <code>
 *     raum.position.verschieben(10, 10); //Verschiebe das Objekt um (10|10) <br />
 *     raum.position.drehen(-90);         //Drehe das Objekt 90° im Uhrzeigersinn. <br />
 * </code>
 * <br />
 * <b> ... kann so verkürzt werden zu ... </b> <br /> <br />
 *
 * <code>
 *     raum.position.verschieben(10, 10).drehen(-90); <br />
 * </code>
 *
 * So lassen sich beliebig viele "eigentlich-void-Operationen" hintereinander ausführen.
 * Created by andonie on 16.02.15.
 */
public class Position {
    private static final float DEG_PRO_RADIAN = (float) (180f/Math.PI);

    /**
     * Das Raum-Objekt, zu dem dieses Objekt eine Schnittstelle darstellt.
     */
    @NoExternalUse
    private final Raum raum;

    /**
     * ToString-Methode.
     * @return  Gibt einen String aus, der Position und Rotation des Objekts ausgibt.
     */
    @Override
    public String toString() {
        return "Position: (" + x() + " | " + y() + ") - Rotation: TODO";
    }

    /**
     * Erstellt einen Position-Handler. Kann nicht von außerhalb der Engine aufgerufen werden.
     * @param raum  Das Raum-Objekt, dass zu diesem Objekt gehört.
     */
    @NoExternalUse
    Position(Raum raum) {
        this.raum = raum;
    }


    /**
     * Setzt die Position des <code>Raum</code>-Objektes gänzlich neu auf der Zeichenebene. Das Setzen ist technisch
     * gesehen eine Verschiebung von der aktuellen Position an die neue.
     *
     * @param x
     * 		neue <code>x</code>-Koordinate
     * @param y
     * 		neue <code>y</code>-Koordinate
     * @return
     *      das ausführende Objekt (also <code>return this;</code>). Für <b>Chaining</b> von Methoden (siehe
     *      Dokumentation der Klasse).
     *
     *
     * @see #set(Punkt)
     * @see #mittelpunktSetzen(float, float)
     * @see #x(float)
     * @see #y(float)
     */
    @API
    public Position set(float x, float y) {
        this.set(new Punkt(x, y));
        return this;
    }

    /**
     * Setzt die Position des Objektes gänzlich neu auf der Zeichenebene. Das Setzen ist technisch
     * gesehen eine Verschiebung von der aktuellen Position an die neue.
     * @param p
     * 		Der neue Zielpunkt
     * @return
     *      das ausführende Objekt (also <code>return this;</code>). Für <b>Chaining</b> von Methoden (siehe
     *      Dokumentation der Klasse).
     *
     * @see #set(float, float)
     * @see #mittelpunktSetzen(float, float)
     * @see #x(float)
     * @see #y(float)
     */
    @API
    public Position set(Punkt p) {
        this.verschieben(new Vektor(p.x - this.x(), p.y - this.y()));
        return this;
    }

    /**
     * Verschiebt das Objekt ohne Bedingungen auf der Zeichenebene. Dies ist die <b>zentrale</b>
     * Methode zum
     *
     * @param v
     * 		Der Vektor, der die Verschiebung des Objekts angibt.
     * @return
     *      das ausführende Objekt (also <code>return this;</code>). Für <b>Chaining</b> von Methoden (siehe
     *      Dokumentation der Klasse).
     *
     * @see Vektor
     * @see #verschieben(float, float)
     */
    @API
    public Position verschieben (Vektor v) {
        raum.getPhysikHandler().verschieben(v);
        return this;
    }

    /**
     * Verschiebt die Raum-Figur so, dass ihr Mittelpunkt die eingegebenen Koordinaten hat.
     * <p/>
     * Diese Methode arbeitet nach dem Mittelpunkt des das Objekt abdeckenden BoundingRechtecks
     * durch den Aufruf der Methode <code>zentrum()</code>. Daher ist diese Methode in der Anwendung
     * auf ein Knoten-Objekt nicht unbedingt sinnvoll.
     *
     * @param x
     * 		Die <code>x</code>-Koordinate des neuen Mittelpunktes des Objektes
     * @param y
     * 		Die <code>y</code>-Koordinate des neuen Mittelpunktes des Objektes
     * @return
     *      das ausführende Objekt (also <code>return this;</code>). Für <b>Chaining</b> von Methoden (siehe
     *      Dokumentation der Klasse).
     *
     *
     * @see #mittelpunktSetzen(Punkt)
     * @see #verschieben(Vektor)
     * @see #set(float, float)
     * @see #mittelPunkt()
     */
    @API
    public Position mittelpunktSetzen (float x, float y) {
        this.mittelpunktSetzen(new Punkt(x, y));
        return this;
    }

    /**
     * Verschiebt die Raum-Figur so, dass ihr Mittelpunkt die eingegebenen Koordinaten hat.<br />
     * Diese Methode Arbeitet nach dem Mittelpunkt des das Objekt abdeckenden BoundingRechtecks
     * durch den Aufruf der Methode <code>zentrum()</code>. Daher ist diese Methode im Anwand auf
     * ein Knoten-Objekt nicht unbedingt sinnvoll.<br /> Macht dasselbe wie
     * <code>mittelPunktSetzen(p.x, p.y)</code>.
     *
     * @param p
     * 		Der neue Mittelpunkt des Raum-Objekts
     * @return
     *      das ausführende Objekt (also <code>return this;</code>). Für <b>Chaining</b> von Methoden (siehe
     *      Dokumentation der Klasse).
     *
     * @see #mittelpunktSetzen(float, float)
     * @see #verschieben(Vektor)
     * @see #set(float, float)
     * @see #mittelPunkt()
     */
    @API
    public Position mittelpunktSetzen (Punkt p) {
        this.verschieben(this.mittelPunkt().nach(p));
        return this;
    }

    /**
     * Gibt die x-Koordinate der linken oberen Ecke zurück. Sollte das Raumobjekt nicht rechteckig
     * sein, so wird die Position der linken oberen Ecke des umschließenden Rechtecks genommen.
     * <p/>
     * TODO: Deprecate positionX() in favor of this new method?
     *
     * @return <code>x</code>-Koordinate
     *
     * @see #y()
     * @see #get()
     */
    @API
    public float x() {
        return this.get().x;
    }

    /**
     * Setzt die x-Koordinate der Position des Objektes gänzlich neu auf der Zeichenebene. Das
     * Setzen ist technisch gesehen eine Verschiebung von der aktuellen Position an die neue.
     *
     * @param x
     * 		neue <code>x</code>-Koordinate
     * @return
     *      das ausführende Objekt (also <code>return this;</code>). Für <b>Chaining</b> von Methoden (siehe
     *      Dokumentation der Klasse).
     *
     * @see #set(float, float)
     * @see #mittelpunktSetzen(float, float)
     * @see #y(float)
     */
    @API
    public Position x(float x) {
        this.verschieben(x - x(), 0);
        return this;
    }

    /**
     * Gibt die y-Koordinate der linken oberen Ecke zurück. Sollte das Raumobjekt nicht rechteckig
     * sein, so wird die Position der linken oberen Ecke des umschließenden Rechtecks genommen.
     * <p/>
     * TODO: Deprecate positionX() in favor of this new method?
     *
     * @return <code>y</code>-Koordinate
     *
     * @see #x()
     * @see #get()
     */
    @API
    public float y() {
        return this.get().y;
    }

    /**
     * Setzt die y-Koordinate der Position des Objektes gänzlich neu auf der Zeichenebene. Das
     * Setzen ist technisch gesehen eine Verschiebung von der aktuellen Position an die neue. <br
     * /><br /> <b>Achtung!</b><br /> Bei <b>allen</b> Objekten ist die eingegebene Position die
     * linke, obere Ecke des Rechtecks, das die Figur optimal umfasst. Das heißt, dass dies bei
     * Kreisen z.B. <b>nicht</b> der Mittelpunkt ist! Hierfür gibt es die Sondermethode
     * <code>mittelpunktSetzen(int x, int y)</code>.
     *
     * @param y
     * 		neue <code>y</code>-Koordinate
     * @return
     *      das ausführende Objekt (also <code>return this;</code>). Für <b>Chaining</b> von Methoden (siehe
     *      Dokumentation der Klasse).
     *
     * @see #set(float, float)
     * @see #mittelpunktSetzen(float, float)
     * @see #x(float)
     */
    @API
    public Position y(float y) {
        this.verschieben(0, y - y());
        return this;
    }

    /**
     * Methode zum schnellen Herausfinden des Mittelpunktes des Raum-Objektes.
     *
     * @return Die Koordinaten des Mittelpunktes des Objektes
     *
     * @see #get()
     */
    @API
    public Punkt mittelPunkt () {
        return raum.getPhysikHandler().mittelpunkt();
    }

    /**
     * Verschiebt das Objekt.<br /> Hierbei wird nichts anderes gemacht, als <code>verschieben(new
     * Vektor(dX, dY))</code> auszufuehren. Insofern ist diese Methode dafuer gut, sich nicht mit
     * der Klasse Vektor auseinandersetzen zu muessen.
     *
     * @param dX
     * 		Die Verschiebung in Richtung X
     * @param dY
     * 		Die Verschiebung in Richtung Y
     * @return
     *      das ausführende Objekt (also <code>return this;</code>). Für <b>Chaining</b> von Methoden (siehe
     *      Dokumentation der Klasse).
     *
     * @see #verschieben(Vektor)
     */
    @API
    public Position verschieben (float dX, float dY) {
        this.verschieben(new Vektor(dX, dY));
        return this;
    }

    /**
     * Gibt die Position dieses Raum-Objekts aus.
     * @return die aktuelle Position dieses <code>Raum</code>-Objekts.
     */
    @API
    public Punkt get() {
        return raum.getPhysikHandler().position();
    }




    /* __________________________ Rotation __________________________ */


    public Position rotieren(float radians) {
        raum.getPhysikHandler().rotieren(radians);
        return this;
    }

    public float rotation() {
        return raum.getPhysikHandler().rotation();
    }

    public Position rotation(float winkelInRad) {
        raum.getPhysikHandler().rotieren(winkelInRad - rotation());
        return this;
    }
}
