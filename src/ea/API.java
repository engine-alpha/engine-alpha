package ea;

import java.lang.annotation.Documented;

/**
 * Markiert Methoden, die Schüler verwenden sollen.
 * Methoden ohne @API sollen nicht verwendet werden!
 *
 * Bisher müssen diese Methoden auch mit @SuppressWarnings("unused")
 * zusätzlich markiert werden.
 *
 * @author Niklas Keller <me@kelunik.com>
 */
@SuppressWarnings("unused")
@Documented
public @interface API { }