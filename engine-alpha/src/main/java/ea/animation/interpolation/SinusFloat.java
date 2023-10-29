package ea.animation.interpolation;

import ea.animation.Interpolator;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

/**
 * Interpoliert auf einer kompletten Sinuskurve.
 *
 * @author Michael Andonie
 */
public class SinusFloat implements Interpolator<Float> {

    /**
     * Der Startwert (und Endwert)
     */
    private final float start;

    /**
     * Die Amplitude der Sinuskurve
     */
    private final float amplitude;

    /**
     * Erstellt einen <code>SinusFloat</code>-Interpolator.
     * @param start     Der Startpunkt der Sinuskurve, die dieser Interpolator interpoliert. Dieser Punkt wird also
     *                  erreicht zu Beginn, bei Ablauf der halben Zeit sowie zum Ende der Interpolation.
     * @param amplitude Die Amplitude der Sinuskurve. Bei 1/4 der Zeit ist der Wert der Interpolation also
     *                  <code>start+amplitude</code> und bei 3/4 ist der Wert <code>start-amplitude</code>.<br>
     *                  Negative Werte sind natürlich auch möglich.
     */
    @API
    public SinusFloat(float start, float amplitude) {
        this.start = start;
        this.amplitude = amplitude;
    }

    @Internal
    @Override
    public Float interpolate(float progress) {
        return (float)Math.sin(Math.PI * progress * 2) * amplitude + start;
    }
}
