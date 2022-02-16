package ea.animation.interpolation;

import ea.animation.Interpolator;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

public class EaseInOutFloat implements Interpolator<Float> {

    /**
     * Startpunkt. Interpolationswert bei t=0
     */
    private final float start;

    /**
     * Endpunkt. Interpolationswert bei t=1
     */
    private final float end;

    /**
     * Erstellt einen EaseInOut-Interpolator.
     * Interpoliert "smooth" zwischen den beiden Werten, beginnt also langsam (erste Ableitung = 0) und endet langsam
     * (erste Ableitung = 0). Dazwischen wächst und schrumpft die Änderungsrate dynamisch.
     *
     * @param start Der Startpunkt der Interpolation.
     * @param end   Der Endpunkt der Interpolation.
     */
    @API
    public EaseInOutFloat(float start, float end) {
        this.start = start;
        this.end = end;
    }

    @Internal
    @Override
    public Float interpolate(float progress) {
        return (float) ((Math.sin((double) progress * Math.PI - Math.PI / 2) + 1) / 2) * (this.end - this.start) + this.start;
    }
}
