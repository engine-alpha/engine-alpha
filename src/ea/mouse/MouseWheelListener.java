package ea.mouse;

import ea.internal.ano.API;

/**
 * Implementierende Klassen können auf das Drehen des Mausrades reagieren.
 * @see ea.mouse.MouseWheelAction
 * @author Michael Andonie
 */
@API
public interface MouseWheelListener {

    /**
     * Diese Methode wird immer dann aufgerufen, wenn das <b>Mausrad gedreht</b> wurde.
     * @param mouseWheelAction  Das MouseWheelAction-Objekt beschreibt, wie das Mausrad gedreht wurde.
     * @see ea.mouse.MouseWheelAction
     */
    void onMouseWheelMove(MouseWheelAction mouseWheelAction);
}
