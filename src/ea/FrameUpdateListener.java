package ea;

import ea.internal.annotations.API;

/**
 * Beschreibt ein Objekt, dass auf jedes Frame-Update reagieren kann.
 */
@API
public interface FrameUpdateListener {
    /**
     * Diese Methode wird bei einem (angemeldeten) Objekt jeden Frame erneut aufgerufen.
     *
     * @param frameDuration Die Zeit <b>in Millisekunden</b>, die seit dem letzten Update vergangen
     */
    @API
    void onFrameUpdate(float frameDuration);
}
