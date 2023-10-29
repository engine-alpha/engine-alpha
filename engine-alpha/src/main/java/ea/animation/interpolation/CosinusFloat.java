package ea.animation.interpolation;

import ea.animation.Interpolator;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

/**
 * Interpoliert auf einer kompletten Cosinuskurve.
 *
 * @author Michael Andonie
 */
public class CosinusFloat
implements Interpolator<Float> {

    /**
     * Der Startwert der Cosinuskurve.
     */
    private final float start;

    /**
     * Die Amplitude der Cosinuskurve
     */
    private final float amplitude;

    /**
     * Erstellt einen neuen Cosinuskurven-Interpolator.
     * @param start     Der Startpunkt der Animation (also die Spitze der Cosinuskurve)
     * @param amplitude Die Amplitude der Cosinuskurve. Damit ist die Interpolation
     *                  <ul>
     *                      <li>Nach 0/4 der Zeit <code>start</code></li>
     *                      <li>Nach 1/4 der Zeit <code>start-amplitude</code></li>
     *                      <li>Nach 1/2 der Zeit <code>start- 2*amplitude</code></li>
     *                      <li>Nach 3/4 der Zeit <code>start-amplitude</code></li>
     *                      <li>Nach 4/4 der Zeit <code>start</code></li>
     *                  </ul>
     */
    @API
    public CosinusFloat(float start, float amplitude) {
        this.start = start;
        this.amplitude = amplitude;
    }

    @Internal
    @Override
    public Float interpolate(float progress) {
        return (float)Math.cos(Math.PI * progress * 2) * amplitude + start - amplitude;
    }
}
