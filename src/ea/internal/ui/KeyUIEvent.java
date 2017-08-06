package ea.internal.ui;

import ea.keyboard.TastenLosgelassenReagierbar;
import ea.keyboard.TastenReagierbar;
import ea.internal.gui.Fenster;

import java.util.List;

/**
 * Ein Key-UI-Event beschreibt das Drücken bzw. Loslassen einer Taste.
 * Created by andonie on 15.02.15.
 */
public class KeyUIEvent
extends FensterEvent{

    /**
     * Der (engine-interne) Keycode der betroffenen Taste.
     */
    private final int keycode;

    /**
     * Ist dieser Wert <code>true</code>, so handelt es sich um einen Key Release.
     */
    private final boolean release;

    /**
     * Erstellt ein neues UI-KeyEvent.
     * @param keycode   Der Code der gedrückten Taste (in <b>Engine-Format</b>).
     * @param release   Gibt an, ob es sich um ein Taste-Loslassen oder ein Taste-Drücken handelt.
     */
    public KeyUIEvent(Fenster fenster, int keycode, boolean release) {
        super(fenster);
        this.keycode = keycode;
        this.release = release;
    }

    /**
     * Dispatch: Verteile das Event an die jeweiligen Listeners.
     */
    @Override
    public void dispatch() {
        if(release) {
            //Losgelassen: TastenLosgelassenReagierbar
            List<TastenLosgelassenReagierbar> list = fenster.getLosListener();
            for(TastenLosgelassenReagierbar tlr : list) {
                tlr.tasteLosgelassen(keycode);
            }
        } else {
            //Runter gedrückt: TastenReagierbar
            List<TastenReagierbar> list = fenster.getListener();
            for(TastenReagierbar tr : list) {
                tr.reagieren(keycode);
            }
        }
    }
}
