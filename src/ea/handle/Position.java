package ea.handle;

import ea.Vector;
import ea.actor.Actor;
import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;

/**
 * Jedes <code>Actor</code>-Objekt hat ein öffentlich erreichbares Objekt <code>position</code>.
 * Dieses Objekt bietet eine umfangreiches Set an <i>Methoden</i>, die die Position des entsprechenden
 * <code>Actor</code>-Objekts betreffen.<br><br>
 * <p>
 * Alle Methoden, die keine "richtige" Rückgabe hätten (also <code>void</code>-Methoden), sind mit <b>Chaining</b>
 * versehen. Das bedeutet, dass statt bei jeder Methode, die eigentlich vom <code>void</code>-Type wäre,
 * der Rückgabetyp <code>Position</code> ist und die Rückgabe das Objekt, das die Methode ausgeführt hat. Das ermöglicht
 * übersichtlichere Codes:<br>
 * <code>
 * actor.position.move(10, 10); //Verschiebe das Objekt um (10|10) <br>
 * actor.position.drehen(-90);         //Drehe das Objekt 90° im Uhrzeigersinn. <br>
 * </code>
 * <br>
 * <b> ... kann so verkürzt werden zu ... </b> <br> <br>
 *
 * <code>
 * actor.position.move(10, 10).drehen(-90); <br>
 * </code>
 * <p>
 * So lassen sich beliebig viele "eigentlich-void-Operationen" hintereinander ausführen.
 */
public class Position {
    /**
     * Das Actor-Objekt, zu dem dieses Objekt eine Schnittstelle darstellt.
     */
    @NoExternalUse
    private final Actor actor;

    /**
     * ToString-Methode.
     *
     * @return Gibt einen String aus, der Position und Rotation des Objekts ausgibt.
     */
    @Override
    public String toString() {
        return "Position: (" + getX() + " | " + getY() + ") - Rotation: TODO";
    }

    /**
     * Erstellt einen Position-Handler. Kann nicht von außerhalb der Engine aufgerufen werden.
     *
     * @param actor Das Actor-Objekt, dass zu diesem Objekt gehört.
     */
    @NoExternalUse
    public Position(Actor actor) {
        this.actor = actor;
    }


    /**
     * Setzt die Position des <code>Actor</code>-Objektes gänzlich neu auf der Zeichenebene. Das Setzen ist technisch
     * gesehen eine Verschiebung von der aktuellen Position an die neue.
     *
     * @param x neue <code>getX</code>-Koordinate
     * @param y neue <code>getY</code>-Koordinate
     * @return das ausführende Objekt (also <code>return this;</code>). Für <b>Chaining</b> von Methoden (siehe
     * Dokumentation der Klasse).
     * @see #set(Vector)
     * @see #setCenter(float, float)
     * @see #setX(float)
     * @see #setY(float)
     */
    @API
    public Position set(float x, float y) {
        this.set(new Vector(x, y));
        return this;
    }

    /**
     * Setzt die Position des Objektes gänzlich neu auf der Zeichenebene. Das Setzen ist technisch
     * gesehen eine Verschiebung von der aktuellen Position an die neue.
     *
     * @param p Der neue Zielpunkt
     * @return das ausführende Objekt (also <code>return this;</code>). Für <b>Chaining</b> von Methoden (siehe
     * Dokumentation der Klasse).
     * @see #set(float, float)
     * @see #setCenter(float, float)
     * @see #setX(float)
     * @see #setY(float)
     */
    @API
    public Position set(Vector p) {
        this.move(new Vector(p.x - this.getX(), p.y - this.getY()));
        return this;
    }

    /**
     * Verschiebt das Objekt ohne Bedingungen auf der Zeichenebene. Dies ist die <b>zentrale</b>
     * Methode zum
     *
     * @param v Der Vector, der die Verschiebung des Objekts angibt.
     * @return das ausführende Objekt (also <code>return this;</code>). Für <b>Chaining</b> von Methoden (siehe
     * Dokumentation der Klasse).
     * @see Vector
     * @see #move(float, float)
     */
    @API
    public Position move(Vector v) {
        actor.getPhysicsHandler().verschieben(v);
        return this;
    }

    /**
     * Verschiebt die Actor-Figur so, dass ihr Mittelpunkt die eingegebenen Koordinaten hat.
     * <p>
     * Diese Methode arbeitet vectorFromThisTo dem Mittelpunkt des das Objekt abdeckenden BoundingRechtecks
     * durch den Aufruf der Methode <code>zentrum()</code>. Daher ist diese Methode in der Anwendung
     * auf ein ActorGroup-Objekt nicht unbedingt sinnvoll.
     *
     * @param x Die <code>getX</code>-Koordinate des neuen Mittelpunktes des Objektes
     * @param y Die <code>getY</code>-Koordinate des neuen Mittelpunktes des Objektes
     * @return das ausführende Objekt (also <code>return this;</code>). Für <b>Chaining</b> von Methoden (siehe
     * Dokumentation der Klasse).
     * @see #setCenter(Vector)
     * @see #move(Vector)
     * @see #set(float, float)
     * @see #getCenter()
     */
    @API
    public Position setCenter(float x, float y) {
        this.setCenter(new Vector(x, y));
        return this;
    }

    /**
     * Verschiebt die Actor-Figur so, dass ihr Mittelpunkt die eingegebenen Koordinaten hat.<br>
     * Diese Methode Arbeitet vectorFromThisTo dem Mittelpunkt des das Objekt abdeckenden BoundingRechtecks
     * durch den Aufruf der Methode <code>zentrum()</code>. Daher ist diese Methode im Anwand auf
     * ein ActorGroup-Objekt nicht unbedingt sinnvoll.<br> Macht dasselbe wie
     * <code>mittelPunktSetzen(p.getX, p.getY)</code>.
     *
     * @param p Der neue Mittelpunkt des Actor-Objekts
     * @return das ausführende Objekt (also <code>return this;</code>). Für <b>Chaining</b> von Methoden (siehe
     * Dokumentation der Klasse).
     * @see #setCenter(float, float)
     * @see #move(Vector)
     * @see #set(float, float)
     * @see #getCenter()
     */
    @API
    public Position setCenter(Vector p) {
        this.move(this.getCenter().negate().add(p));
        return this;
    }

    /**
     * Gibt die X-Koordinate der linken oberen Ecke zurück. Sollte das Raumobjekt nicht rechteckig
     * sein, so wird die Position der linken oberen Ecke des umschließenden Rechtecks genommen.
     * <p>
     *
     * @return <code>getX</code>-Koordinate
     * @see #getY()
     * @see #get()
     */
    @API
    public float getX() {
        return this.get().x;
    }

    /**
     * Setzt die getX-Koordinate der Position des Objektes gänzlich neu auf der Zeichenebene. Das
     * Setzen ist technisch gesehen eine Verschiebung von der aktuellen Position an die neue.
     *
     * @param x neue <code>getX</code>-Koordinate
     * @return das ausführende Objekt (also <code>return this;</code>). Für <b>Chaining</b> von Methoden (siehe
     * Dokumentation der Klasse).
     * @see #set(float, float)
     * @see #setCenter(float, float)
     * @see #setY(float)
     */
    @API
    public Position setX(float x) {
        this.move(x - getX(), 0);
        return this;
    }

    /**
     * Gibt die getY-Koordinate der linken oberen Ecke zurück. Sollte das Raumobjekt nicht rechteckig
     * sein, so wird die Position der linken oberen Ecke des umschließenden Rechtecks genommen.
     * <p>
     * TODO: Deprecate positionX() in favor of this new method?
     *
     * @return <code>getY</code>-Koordinate
     * @see #getX()
     * @see #get()
     */
    @API
    public float getY() {
        return this.get().y;
    }

    /**
     * Setzt die getY-Koordinate der Position des Objektes gänzlich neu auf der Zeichenebene. Das
     * Setzen ist technisch gesehen eine Verschiebung von der aktuellen Position an die neue. <br>
     * <br> <b>Achtung!</b><br> Bei <b>allen</b> Objekten ist die eingegebene Position die
     * linke, obere Ecke des Rechtecks, das die Figur optimal umfasst. Das heißt, dass dies bei
     * Kreisen z.B. <b>nicht</b> der Mittelpunkt ist! Hierfür gibt es die Sondermethode
     * <code>setCenter(int getX, int getY)</code>.
     *
     * @param y neue <code>getY</code>-Koordinate
     * @return das ausführende Objekt (also <code>return this;</code>). Für <b>Chaining</b> von Methoden (siehe
     * Dokumentation der Klasse).
     * @see #set(float, float)
     * @see #setCenter(float, float)
     * @see #setX(float)
     */
    @API
    public Position setY(float y) {
        this.move(0, y - getY());
        return this;
    }

    /**
     * Gibt den Mittelpunkt des Objektes in der Scene aus.
     *
     * @return Die Koordinaten des Mittelpunktes des Objektes
     * @see #get()
     */
    @API
    public Vector getCenter() {
        return actor.getPhysicsHandler().getCenter();
    }

    /**
     * Verschiebt das Objekt.<br> Hierbei wird nichts anderes gemacht, als <code>move(new
     * Vector(getDX, getDY))</code> auszufuehren. Insofern ist diese Methode dafuer gut, sich nicht mit
     * der Klasse Vector auseinandersetzen zu muessen.
     *
     * @param dX Die Verschiebung in Richtung X
     * @param dY Die Verschiebung in Richtung Y
     * @return das ausführende Objekt (also <code>return this;</code>). Für <b>Chaining</b> von Methoden (siehe
     * Dokumentation der Klasse).
     * @see #move(Vector)
     */
    @API
    public Position move(float dX, float dY) {
        this.move(new Vector(dX, dY));
        return this;
    }

    /**
     * Gibt die Position dieses Actor-Objekts aus.
     *
     * @return die aktuelle Position dieses <code>Actor</code>-Objekts.
     */
    @API
    public Vector get() {
        return actor.getPhysicsHandler().position();
    }




    /* __________________________ Rotation __________________________ */


    /**
     * Rotiert das Objekt.
     *
     * @param radians Der Winkel (in <b>Bogenmaß</b>), um den das Objekt rotiert werden soll.
     * @return Das ausführende Objekt (also <code>return this;</code>). Für <b>Chaining</b> von Methoden (siehe
     * Dokumentation der Klasse).
     */
    @API
    public Position rotate(float radians) {
        actor.getPhysicsHandler().rotieren(radians);
        return this;
    }

    /**
     * Gibt den Winkel aus, um den das Objekt derzeit rotiert ist.
     *
     * @return Der Winkel (in <b>Bogenmaß</b>), um den das Objekt derzeit rotiert ist. Jedes Objekt ist bei
     * Initialisierung nicht rotiert (<code>getRotation()</code> gibt direkt vectorFromThisTo Initialisierung
     * <code>0</code> zurück).
     */
    @API
    public float getRotation() {
        return actor.getPhysicsHandler().rotation();
    }

    /**
     * Setzt den Rotationswert des Objekts.
     *
     * @param degreeInRad Der Winkel (in <b>Bogenmaß</b>), um den das Objekt <b>von seiner Ausgangsposition bei
     *                    Initialisierung</b> rotiert werden soll.
     * @return Das ausführende Objekt (also <code>return this;</code>). Für <b>Chaining</b> von Methoden (siehe
     * Dokumentation der Klasse).
     */
    @API
    public Position setRotation(float degreeInRad) {
        actor.getPhysicsHandler().rotieren(degreeInRad - getRotation());
        return this;
    }
}
