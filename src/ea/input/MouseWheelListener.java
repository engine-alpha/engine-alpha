package ea.input;

import ea.internal.annotations.API;

/**
 * Implementierende Klassen können auf das Drehen des Mausrades reagieren.
 * @see MouseWheelEvent
 * @author Michael Andonie
 */
@API
public interface MouseWheelListener {

    /**
     * Diese Methode wird immer dann aufgerufen, wenn das <b>Mausrad gedreht</b> wurde.
     * @param mouseWheelEvent  Das MouseWheelAction-Objekt beschreibt, wie das Mausrad gedreht wurde.
     * @see MouseWheelEvent
     */
    void onMouseWheelMove(MouseWheelEvent mouseWheelEvent);
}
