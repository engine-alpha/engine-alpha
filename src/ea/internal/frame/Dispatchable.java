package ea.internal.frame;

import ea.Vector;
import ea.collision.CollisionListener;
import ea.mouse.MouseButton;
import ea.mouse.MouseClickListener;

/**
 * Dieses Interface beschreibt ein <b>Event</b>, das vom Dispatcher-Thread als Auftrag ausgeführt
 * werden kann. Dispatchbare Aufträge sind z.B. <ul> <li>Einen Tick (zum passenden Zeitpunkt)
 * aufrufen</li> <li>Durch den Aufruf von {@link MouseClickListener#onMouseDown(Vector,
 * MouseButton)} dem API-Nutzer die Möglichkeit geben, auf einen User-Klick zu geben.</li>
 * <li>Durch den Aufruf von {@link CollisionListener} dem API-Nutzer die Möglichkeit
 * geben auf Kollisionen zu reagieren.</li> </ul>
 */
public interface Dispatchable {
    /**
     * Der (einmalige) Aufruf dieser Methode sorgt dafür, dass das ausführbare Event ausgeführt
     * wird. Es ist garantiert, das niemals zwei Dispatchable Events parallel ausgeführt werden.
     */
    void dispatch();
}