package ea.internal.ui;

import ea.mouse.MausBewegungReagierbar;
import ea.Vektor;
import ea.internal.gui.Fenster;

import java.util.List;

/**
 * Ein <code>MausBewegungEvent</code> beschreibt eine Bewegung, die die Maus
 * auf der Zeichenebene getätigt hat.
 * Created by andonie on 15.02.15.
 */
public class MausBewegungEvent
extends FensterEvent {

    /**
     * Die Bewegung, die die Maus zurückgelegt hat.
     */
    private final Vektor bewegung;

    /**
     * Erstellt ein Maus-Bewegungs-Event
     * @param fenster   Das Fenster, in dem sich die Maus bewegungSimulieren hat.
     * @param bewegung  Die Bewegung <b>auf der Zeichenebene</b>, die die Maus zurückgelegt hat.
     */
    public MausBewegungEvent(Fenster fenster, Vektor bewegung) {
        super(fenster);
        this.bewegung = bewegung;
    }

    /**
     * Dispatch-Logik. Gibt die Bewegung weiter an alle Listener
     */
    @Override
    public void dispatch() {
        List<MausBewegungReagierbar> list = fenster.getMaus().getMausBewegungListeners();
        for(MausBewegungReagierbar mbr : list) {
            mbr.mausBewegt(bewegung);
        }
    }
}
