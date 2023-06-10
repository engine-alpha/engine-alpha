package ea.animation;

import ea.Vector;
import ea.actor.Actor;
import ea.animation.interpolation.LinearFloat;
import ea.event.AggregateFrameUpdateListener;

/**
 * Eine Animation, die ein Actor-Objekt in einer Linie animiert.
 */
public class LineAnimation extends AggregateFrameUpdateListener {

    /**
     * Erstellt eine neue Linien-Animation.
     *
     * @param actor             Der Actor, der zwischen seinem aktuellen Mittelpunkt und einem Endpunkt bewegt werden
     *                          soll.
     * @param endPoint          Der Endpunkt. Die Bewegung des Aktors endet mit seinem Mittelpunkt auf dem
     *                          <code>endPoint</code>.
     * @param durationInSeconds Die Zeit in Sekunden, in der der Actor von seiner Ausgangsposition bis zum
     *                          Zielpunkt benötigt.
     * @param pingpong          <code>false</code>: Die Animation endet, wenn der Actor den Zielpunkt erreicht hat.
     *                          <code>true</code>: Der Actor bewegt sich zwischen seinem Ausgangspunkt und dem
     *                          Zielpunkt
     *                          hin und her. Jede Strecke in eine Richtung dauert <code>durationInMS</code>. Die
     *                          Animation
     *                          endet nicht von sich aus.
     */
    public LineAnimation(Actor actor, Vector endPoint, float durationInSeconds, boolean pingpong) {
        Vector center = actor.getCenter();

        addFrameUpdateListener(new ValueAnimator<>(durationInSeconds, x -> actor.setCenter(x, actor.getCenter().getY()), new LinearFloat(center.getX(), endPoint.getX()), pingpong ? AnimationMode.PINGPONG : AnimationMode.SINGLE, this));
        addFrameUpdateListener(new ValueAnimator<>(durationInSeconds, y -> actor.setCenter(actor.getCenter().getX(), y), new LinearFloat(center.getY(), endPoint.getY()), pingpong ? AnimationMode.PINGPONG : AnimationMode.SINGLE, this));
    }
}
