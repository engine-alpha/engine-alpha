package ea.input;

import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

/**
 * Beschreibt eine Bewegung des Mausrads. Wird vom {@link MouseWheelListener} genutzt.
 *
 * @see ea.input.MouseWheelListener
 *
 * @author Michael Andonie
 */
@API
public class MouseWheelAction {

    /**
     * Die Rotation des Mausrades. Bei Mäusen mit Präzession auch in Bruchteilen eines "Clicks"
     */
    private final float wheelRotation;


    @Internal
    public MouseWheelAction(float wheelRotation) {
        this.wheelRotation = wheelRotation;
    }

    /**
     * Gibt die Anzahl an "Clicks" aus, die das Mausrad bewegt wurde.
     * @return  Die Anzahl an "Clicks", die das Mausrad bewegt wurde.<br>
     *          <b>Negative Werte:</b> Das Rad wurde "rauf" gedreht (weg vom Benutzer).
     *          <b>Positive Werte:</b> Das Rad wurde "runter" gedreht (hin zum Benutzer).
     * @see #getPreciseWheelRotation()
     */
    @API
    public int getWheelRotation() {
        return (int)wheelRotation;
    }

    /**
     * Gibt die Anzahl an "Clicks" aus, die das Mausrad bewegt wurde. Wenn die benutzte Maus auch Zwischenschritte
     * erlaubt, werden auch "Click-Bruchteile" mit eingerechnet.
     * @return  Die Anzahl an "Clicks", die das Mausrad bewegt wurde.<br>
     *          <b>Negative Werte:</b> Das Rad wurde "rauf" gedreht (weg vom Benutzer).
     *          <b>Positive Werte:</b> Das Rad wurde "runter" gedreht (hin zum Benutzer).
     * @see #getWheelRotation()
     */
    @API
    public float getPreciseWheelRotation() {
        return (float)wheelRotation;
    }

}
