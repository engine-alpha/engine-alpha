package ea.animation;

import ea.Vector;
import ea.actor.Actor;
import ea.animation.interpolation.CosinusFloat;
import ea.animation.interpolation.LinearFloat;
import ea.animation.interpolation.SinusFloat;
import ea.event.AggregateFrameUpdateListener;
import ea.internal.annotations.API;

/**
 * Animiert einen Actor in einem Kreis.
 *
 * @author Michael Andonie
 */
public class CircleAnimation extends AggregateFrameUpdateListener {
    /**
     * Erstellt eine Circle-Animation. Animiert ein Actor-Objekt anhand seines Mittelpunkts
     * um einen Drehungsmittelpunkt.
     *
     * @param actor             Der zu animierende Actor.
     * @param rotationCenter    Das Zentrum der Drehung.
     * @param durationInSeconds Die Dauer einer ganzen Umdrehung in Millisekunden.
     * @param circleClockwise   <code>true</code>: Drehung im Uhrzeigersinn. <code>false</code>: Drehung entgegen
     *                          des Uhrzeigersinns.
     * @param rotateActor       <code>true</code>: Der Actor rotiert auch.
     *                          <code>false</code>: Die Rotation des Actors bleibt fix. Nur seine Position verändert
     *                          sich durch die Animation.
     */
    @API
    public CircleAnimation(Actor actor, Vector rotationCenter, float durationInSeconds, boolean circleClockwise, boolean rotateActor) {
        Vector currentActorCenter = actor.getCenter();
        float radius = new Vector(rotationCenter, currentActorCenter).getLength();
        Vector rightPoint = rotationCenter.add(new Vector(radius, 0));

        ValueAnimator<Float> aX = new ValueAnimator<>(durationInSeconds, x -> actor.setCenter(x, actor.getCenter().getY()), new CosinusFloat(rightPoint.getX(), radius), AnimationMode.REPEATED, this);
        ValueAnimator<Float> aY = new ValueAnimator<>(durationInSeconds, y -> actor.setCenter(actor.getCenter().getX(), y), new SinusFloat(rotationCenter.getY(), circleClockwise ? -radius : radius), AnimationMode.REPEATED, this);

        // Winkel zwischen gewünschtem Startpunkt und aktueller Actor-Position (immer in [0;PI])
        float angle = rotationCenter.negate().add(rightPoint).getAngle(rotationCenter.negate().add(currentActorCenter));

        if (circleClockwise && currentActorCenter.getY() > rotationCenter.getY() || !circleClockwise && currentActorCenter.getY() < rotationCenter.getY()) {
            // Gedrehter Winkel ist bereits über die Hälfte
            angle = 360 - angle;
        }

        float actualProgress = angle / 360;
        aX.setProgress(actualProgress);
        aY.setProgress(actualProgress);

        addFrameUpdateListener(aX);
        addFrameUpdateListener(aY);

        if (rotateActor) {
            float rotationAngle = circleClockwise ? angle : -angle;
            ValueAnimator<Float> aR = new ValueAnimator<>(durationInSeconds, actor::setRotation, new LinearFloat(-rotationAngle, -rotationAngle + 360 * (circleClockwise ? -1 : 1)), AnimationMode.REPEATED, actor);
            aR.setProgress(actualProgress);
            addFrameUpdateListener(aR);
        }
    }
}
