package ea;

/**
 * Jedes <code>Raum</code>-Objekt hat ein öffentlich erreichbares Objekt <code>get</code>.
 * Dieses Objekt bietet eine umfangreiches Set an <i>Methoden</i>, die die Position des entsprechenden
 * <code>Raum</code>-Objekts betreffen.<br /><br />
 *
 * Alle Methoden, die keine "richtige" Rückgabe hätten (also <code>void</code>-Methoden), sind mit <b>Chaining</b>
 * versehen. Das bedeutet, dass statt bei jeder Methode, die eigentlich vom <code>void</code>-Typ wäre,
 * der Rückgabetyp <code>Position</code> ist und die Rückgabe das Objekt, das die Methode ausgeführt hat. Das ermöglicht
 * übersichtlichere Codes:<br />
 * <code>
 *     raum.get.verschieben(10, 10); //Verschiebe das Objekt um (10|10) <br />
 *     raum.get.drehen(-90);         //Drehe das Objekt 90° im Uhrzeigersinn. <br />
 * </code>
 * <br />
 * <b> ... kann so verkürzt werden zu ... </b> <br /> <br />
 *
 * <code>
 *     raum.get.verschieben(10, 10).drehen(-90); <br />
 * </code>
 * Created by andonie on 16.02.15.
 */
public class Physik {
}
