package ea.internal.frame;

/**
 * Dieses Interface beschreibt ein <b>Event</b>, das vom Dispatcher-Thread als Auftrag ausgeführt
 * werden kann. Dispatchbare Aufträge sind z.B. <ul> <li>Einen Tick (zum passenden Zeitpunkt)
 * aufrufen</li> <li>Durch den Aufruf von <code>klickReagieren(x,y)</code> dem API-Nutzer die
 * Möglichkeit geben, auf einen User-Klick an der Stelle (x|y) zu geben.</li> <li>Durch den Aufruf
 * von <code>kollisionReagieren(code)</code> dem API-Nutzer die Möglichkeit geben, die Kollision mit
 * der ID <code>code</code> geben.</li> </ul>
 */
public interface Dispatchable {
    /**
     * Der (einmalige) Aufruf dieser Methode sorgt dafür, dass das ausführbare Event ausgeführt
     * wird. Es ist garantiert, das niemals zwei Dispatchable Events parallel ausgeführt werden.
     */
    void dispatch();
}