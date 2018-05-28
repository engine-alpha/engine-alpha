package ea;

import ea.internal.ano.API;

/**
 * Beschreibt ein Objekt, dass auf jedes Frame-Update reagieren kann.
 */
@API
public interface FrameUpdateListener {
    /**
     * Diese Methode wird bei einem (angemeldeten) Objekt jeden Frame erneut aufgerufen.
     *
     * @param frameDuration Die Zeit <b>in Millisekunden</b>, die seit dem letzten Update vergangen
     *                      ist.
     */
    @API
    void onFrameUpdate(int frameDuration);

    /**
     * Diese Methode wird aufgerufen, wenn ein Objekt angemeldet wird.
     *
     * @param scene Szene, an der das Objekt angemeldet wird.
     */
    @API
    default void onAttach(Scene scene) {
        // override if special behavior is needed
    }

    /**
     * Diese Methode wird aufgerufen, wenn ein Objekt abgemeldet wird.
     *
     * @param scene Szene, an der das Objekt abgemeldet wird.
     */
    @API
    default void onDetach(Scene scene) {
        // override if special behavior is needed
    }
}
