package ea.collision;

import ea.actor.Actor;
import ea.event.ListenerEvent;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import org.jbox2d.dynamics.contacts.Contact;

/**
 * Ein Objekt der Klasse <code>CollisionEvent</code> repräsentiert eine <b>Kollision zwischen zwei Actor-Objekten</b>.
 * Nur Actor-Objekte, mit denen ein CollisionListener verkmnüpft ist, generieren <code>CollisionEvent</code>s.
 * <p>
 * Das CollisionEvent wird verwendet als
 * <ul>
 *     <li>
 *         <b>Angabe des Kollisionspartners</b>: In der Engine ist eines der beiden Actor-Objekte des Aufpralls
 *         implizit bestimmt dadurch, dass der <code>CollisionListener</code> an dem entsprechenden Actor-Objekt
 *         <b>angemeldet</b> werden musste. Das hiermit kollidierende Objekt ist im Event angegeben.
 *     </li>
 *     <li>
 *         <b>Ausführliche Informationsquelle</b>: Hierüber sind Informationen zur Kollision erhältlich, z.B. über
 *         die Härte des Aufpralls.
 *     </li>
 *     <li>
 *         <b>Kontrolle der Kollisionsauflösung</b>: Der Nutzer kann entscheiden, ob die Kollision aufgelöst werden soll
 *         oder ignoriert werden soll. Hiermit lassen sich zum Beispiel einseitige Sperren/Wände umsetzen.
 *     </li>
 * </ul>
 *
 * @see CollisionListener
 */
public class CollisionEvent<E extends Actor> implements ListenerEvent {
    /**
     * Der JBox2D-Contact. Zur Manipulation der Kollision und zur Abfrage.
     */
    private final Contact contact;

    /**
     * Das kollidierende Actor-Objekt.
     */
    private final E colliding;

    private final Runnable removeListener;

    /**
     * Konstruktor. Erstellt ein Collision-Event.
     *
     * @param contact   Das JBox2D-Contact-Objekt zur direkten Manipulation der Kollisionsauflösung (und zur Abfrage von
     *                  Informationen).
     * @param colliding Das kollidierende Actor-Objekt. Das zweite Objekt der Kollision ist implizit durch die
     *                  Anmeldung am entsprechenden Actor gegeben.
     */
    @Internal
    public CollisionEvent(Contact contact, E colliding, Runnable removeListener) {
        this.contact = contact;
        this.colliding = colliding;
        this.removeListener = removeListener;
    }

    /**
     * Gibt das <code>Actor</code>-Objekt aus, dass mit dem <code>Actor</code> kollidiert,
     * an dem der Listener angemeldet wurde.
     *
     * @return Das kollidierende Actor-Objekt. Das zweite Objekt der Kollision ist implizit durch die
     * Anmeldung am entsprechenden Actor gegeben.
     */
    @API
    public E getColliding() {
        return colliding;
    }

    /**
     * Wenn diese Methode aufgerufen wird, wird diese Kollision <b>nicht von der Physics-Engine</b> aufgelöst, sondern
     * ignoriert. <br>
     * Dies lässt sich Nutzen zum Beispiel für:
     * <ul>
     *     <li>Feste Plattformen, durch die man von unten "durchspringen" kann, um so von unten auf sie drauf zu
     *     springen.</li>
     *     <li>Einbahn-Sperren, die nur auf einer Seite durchlässig sind.</li>
     *     <li>Gegner, die nicht miteinander kollidieren sollen, sondern nur mit dem Spielcharakter.</li>
     * </ul>
     */
    @API
    public void ignoreCollision() {
        contact.setEnabled(false);
        colliding.getPhysicsHandler().getWorldHandler().addContactToBlacklist(contact);
    }

    @Override
    public void removeListener() {
        removeListener.run();
    }
}
