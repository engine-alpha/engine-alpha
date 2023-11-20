package ea.animation.interpolation;

import ea.animation.Interpolator;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

/**
 * Ein Interpolator, der eine konstante Funktion darstellt.
 *
 * @param <Value> Ein beliebiger Typ zum Interpolieren
 */
public class ConstantInterpolator<Value> implements Interpolator<Value> {

    private final Value value;

    /**
     * Erstellt einen konstanten Interpolator
     *
     * @param value Der stets auszugebende Wert
     */
    @API
    public ConstantInterpolator(Value value) {
        this.value = value;
    }

    @Internal
    @Override
    public Value interpolate(float progress) {
        return value;
    }
}
