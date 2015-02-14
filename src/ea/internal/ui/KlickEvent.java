package ea.internal.ui;

import ea.Punkt;
import ea.internal.frame.Dispatchable;
import ea.internal.gui.Fenster;

import java.util.Queue;

/**
 * Ein Mausklick <b>auf der Zeichenebene</b>
 * Created by andonie on 14.02.15.
 */
public class KlickEvent
extends FensterEvent {



    /**
     * Der Punkt auf der Zeichenebene, an dem geklickt wurde.
     */
    private final Punkt punkt;

    /**
     * Konstruktor. Erstellt ein Event.
     * @param punkt Der Punkt auf der Zeichenebene, an dem es einen Mausklick gab.
     */
    public KlickEvent(Fenster fenster, Punkt punkt) {
        super(fenster);
        this.punkt = punkt;
    }

    /**
     * Dispatch-Methode: Leitet den Klick an alle relevanten Listener weiter:
     * <ul>
     *     <li>Unmittelbarer Übertrag an die <code>KlickReagierbar</code>-Objekte</li>
     *     <li></li>
     * </ul>
     */
    @Override
    public void dispatch() {

    }
}
