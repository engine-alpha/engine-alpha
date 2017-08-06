package ea.internal.ui;

import ea.*;
import ea.internal.gui.Fenster;
import ea.mouse.KlickReagierbar;
import ea.mouse.Maus;
import ea.mouse.MausLosgelassenReagierbar;
import ea.mouse.RechtsKlickReagierbar;

import java.util.ArrayList;

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
     * Der Button, der geklickt wurde.
     * @see Maus
     */
    private final int klickButton;

    /**
     * Die Anzahl an Klicks, die hitnereinander getätigt wurden (gehören alle zum selben Event).
     */
    private final int klickAnzahl;

    /**
     * Ist dieser Wert <code>true</code>, fand anstatt eines Klicks ein release statt.
     */
    private final boolean isRelease;

    /**
     * Konstruktor. Erstellt ein Event.
     * @param punkt Der Punkt auf der Zeichenebene, an dem es einen Mausklick gab.
     * @param klickButton
     * @param klickAnzahl
     * @param isRelease
     */
    public KlickEvent(Fenster fenster, Punkt punkt, int klickButton, int klickAnzahl, boolean isRelease) {
        super(fenster);
        this.punkt = punkt;
        this.klickButton = klickButton;
        this.klickAnzahl = klickAnzahl;
        this.isRelease = isRelease;
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
        //Hol die Maus.
        Maus maus = fenster.getMaus();

        if(isRelease) {
            ArrayList<MausLosgelassenReagierbar> list = maus.getMausLosgelassenListeners();
            for(MausLosgelassenReagierbar mlr : list) {
                mlr.mausLosgelassen(punkt, klickButton==Maus.LINKSKLICK);
            }
        } else if(klickButton == Maus.LINKSKLICK) {
            //Linksklick: KlickReagierbar
            ArrayList<KlickReagierbar> list = maus.getKlickListeners();
            for(KlickReagierbar kr : list) {
                kr.klickReagieren(punkt);
            }
        } else if (klickButton == Maus.RECHTSKLICK) {
            //Rechtsklick: RechtsKlickReagierbar
            ArrayList<RechtsKlickReagierbar> list = maus.getRechtsKlickListeners();
            for(RechtsKlickReagierbar rkr : list) {
                rkr.rechtsKlickReagieren(punkt);
            }
        }

        //TODO sophisticated interfaces
    }
}
