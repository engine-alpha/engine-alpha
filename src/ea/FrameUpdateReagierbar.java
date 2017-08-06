package ea;

import ea.handle.Anmelden;
import ea.internal.ano.API;

/**
 * Beschreibt ein Objekt, dass auf jedes Frame-Update reagieren kann.
 *
 * @see Anmelden
 *
 * Created by andonie on 06.09.15.
 */
@API
public interface FrameUpdateReagierbar {

    /**
     * Diese Methode wird bei einem (angemeldeten) Objekt jeden Frame
     * erneut aufgerufen.
     * @param ts    Die Zeit <b>in Sekunden</b>, die seit dem letzten
     *              Update vergangen ist.
     * @see   Anmelden#frameUpdateReagierbar(ea.FrameUpdateReagierbar)
     */
    @API
    public abstract void frameUpdate(float ts);

}
