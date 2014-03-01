package ea;

import ea.internal.ani.Animierer;

/**
 * AnimationsEndeReagierbar kann auf das Ende einer Animation reagieren und entsprechend der Lage 
 * etwas tun.
 * 
 * @author Michael Andonie
 */

public interface AnimationsEndeReagierbar {
    /**
     * Diese Methode wird einmal dann aufgerufen, wenn die Animation zu Ende ist.<br />
     * Dadurch kann das Ende der Animation in Programmiercode gefestigt und speziell genommen werden.
     * @param   animierer   Der Animierer, der sich gerade beendet hat.
     */
    public abstract void endeReagieren(Animierer animierer);
}