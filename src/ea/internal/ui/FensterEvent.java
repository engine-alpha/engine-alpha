package ea.internal.ui;

import ea.internal.gui.Fenster;

/**
 * Ein Fenster-Event ist ein UI-Event, das innerhalb eines Fensters stattfindet.
 * Damit finden die Dispatches abhängig von den Listenern des jeweiligen Fensters statt.
 * Created by andonie on 15.02.15.
 */
public abstract class FensterEvent
extends UIEvent {

    protected final Fenster fenster;

    /**
     * Initialisiert das Fenster-Event
     * @param fenster   Das Fenster, in dem das Event (Typ abhängig von Implementierung) stattfindet.
     */
    protected FensterEvent(Fenster fenster) {
        this.fenster = fenster;
    }
}
