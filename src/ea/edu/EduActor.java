package ea.edu;

import ea.Point;
import ea.Vector;
import ea.actor.Actor;
import ea.animation.ValueAnimator;
import ea.animation.interpolation.CosinusFloat;
import ea.animation.interpolation.LinearFloat;
import ea.animation.interpolation.SinusFloat;
import ea.handle.Physics;
import ea.internal.ano.NoExternalUse;

public interface EduActor {

    /**
     * Gibt den Actor aus. Standardimplementierung: return this;
     * @return  Das Core-Engine-Actor-Objekt
     */
    @NoExternalUse
    Actor getActor();

    /**
     * Standard-Ausführung im Konstruktor. Meldet das Objekt unmittelbar in der aktuell aktiven Szene an.
     */
    @NoExternalUse
    default void eduSetup() {
        Spiel.getActiveScene().add(getActor());

        //Default Physics Setup für EDU Objekte
        getActor().physics.setRotationLocked(true);
        getActor().physics.setElasticity(0);
        getActor().physics.setGravity(new Vector(0, -9.81f));
    }

    default void entfernen() {
        Spiel.getActiveScene().remove(getActor());
    }

    default void verschieben(float dX, float dY) {
        getActor().position.move(dX, dY);
    }

    default void drehen(float drehwinkelInWinkelgrad) {
        getActor().position.rotate(drehwinkelInWinkelgrad);
    }

    default float nenneWinkel() {
        return getActor().position.getRotation();
    }

    default void setzeMittelpunkt(float mX, float mY) {
        getActor().position.setCenter(mX, mY);
    }

    default void setzeSichtbar(boolean sichtbar) {
        getActor().setVisible(sichtbar);
    }

    default boolean nenneSichtbar() {
        return getActor().isVisible();
    }

    default float nenneMx() {
        return getActor().position.getCenter().x;
    }

    default float nenneMy() {
        return getActor().position.getCenter().y;
    }

    default boolean beinhaltetPunkt(float pX, float pY) {
        return getActor().contains(new Point(pX, pY));
    }

    default Point mittelPunkt() {
        return getActor().position.getCenter();
    }

    default Point zentrum() {
        return mittelPunkt();
    }

    default boolean schneidet(Actor actor) {
        return getActor().overlaps(actor);
    }

    /* ~~~ PHYSICS ~~~ */

    default void setzeRotationBlockiert(boolean blockiert) {
        getActor().physics.setRotationLocked(blockiert);
    }

    default void wirkeImpuls(float iX, float iY) {
        getActor().physics.applyImpulse(new Vector(iX, iY));
    }

    default void setzeReibung(float reibungsKoeffizient) {
        getActor().physics.setFriction(reibungsKoeffizient);
    }

    default void setzeElastizitaet(float elastizitaetsKoeffizient) {
        getActor().physics.setElasticity(elastizitaetsKoeffizient);
    }

    default void setzeSchwerkraft(float schwerkraft) {
        getActor().physics.setGravity(new Vector(0, -schwerkraft));
    }

    default void setzeMasse(float masse) {
        getActor().physics.setMass(masse);
    }

    default float nenneVx() {
        return getActor().physics.getVelocity().getRealX();
    }

    default float nenneVy() {
        return getActor().physics.getVelocity().getRealY();
    }

    /* ~~~ JUMP N RUN WRAPPER ~~~ */

    default boolean steht() {
        return getActor().physics.testStanding();
    }

    default boolean stehtAuf(Actor actor) {
        return getActor().overlaps(actor) && getActor().physics.testStanding();
    }

    default void macheAktiv() {
        getActor().physics.setType(Physics.Type.DYNAMIC);
    }

    default void machePassiv() {
        getActor().physics.setType(Physics.Type.STATIC);
    }

    default void macheNeutral() {
        getActor().physics.setType(Physics.Type.PASSIVE);
    }

    default void sprung(float staerke) {
        if(steht())
            getActor().physics.applyImpulse(new Vector(0, staerke*1000));
    }

    /**
     * Bewegt den Actor anhand einer Gerade.
     * @param zX    X-Koordinate des Mittelpunkts des Actors nach <code>ms</code> Millisekunden
     * @param zY    Y-Koordinate des Mittelpunkts des Actors nach <code>ms</code> Millisekunden
     * @param ms    Zeit in Millisekunden, die der Actor von Beginn der Animation benötigt, bis er am angegebenen
     *              Endpunkt angekommen ist.
     * @param loop  <code>true</code>: Der Actor "ping pongt" zwischen dem impliziten Startpunkt und dem angegebenen
     *              Endpunkt hin und her. Die Strecke in eine Richtung benötigt jeweils <code>ms</code> Millisekunden
     *              Zeit. <br/>
     *              <code>false</code>: Die Animation endet automatisch, nachdem der Zielpunkt (das erste Mal) erreicht
     *              wurde.
     */
    default void geradenAnimation(float zX, float zY, int ms, boolean loop) {
        Point center = getActor().position.getCenter();
        ValueAnimator<Float> aX = new ValueAnimator<>(ms,
                x->getActor().position.setCenter(x, getActor().position.getCenter().getRealY()),
                new LinearFloat(center.getRealX(), zX),
                loop ? ValueAnimator.Mode.PINGPONG : ValueAnimator.Mode.SINGLE);
        ValueAnimator<Float> aY = new ValueAnimator<>(ms,
                y->getActor().position.setCenter(getActor().position.getCenter().getRealX(), y),
                new LinearFloat(center.getRealY(), zY),
                loop ? ValueAnimator.Mode.PINGPONG : ValueAnimator.Mode.SINGLE);
        Spiel.getActiveScene().addFrameUpdateListener(aX);
        Spiel.getActiveScene().addFrameUpdateListener(aY);
    }

    /**
     * Bewegt den Actor in einem Kreis um einen Angegebenen Mittelpunkt.
     * @param mX    X-Koordinate des Mittelpunkts der Revolution.
     * @param mY    Y-Koordinate des Mittelpunkts der Revolution.
     * @param ms    Dauer in Millisekunden, die eine komplette Umdrehung benötigt.
     * @param uhrzeigersinn  <code>true</code>= Drehung findet im Uhrzeigersinn statt. <code>false</code>: Gegen den
     *                      Uhrzeigersinn
     * @param rotation      <code>true</code>=Das Actor-Objekt wird auch entsprechend seiner Kreis-Position rotiert.
     *                      <code>false</code>=Das Actor-Objekt behält seine Rotation bei.
     * @param loop          <code>true</code>=Die Umdrehung wird wiederholt. <code>false</code>: Die Umdrehung endet
     *                      nach der ersten kompletten Drehung.
     */
    default void kreisAnimation(float mX, float mY, int ms, boolean uhrzeigersinn, boolean rotation, boolean loop) {
        Point currentActorCenter = getActor().position.getCenter();
        Point rotationCenter = new Point(mX, mY);
        float radius = rotationCenter.distanceTo(currentActorCenter);
        Point rightPoint = rotationCenter.verschobeneInstanz(
                new Vector(radius, 0));

        ValueAnimator<Float> aX = new ValueAnimator<>(ms,
                x->getActor().position.setCenter(x, getActor().position.getCenter().getRealY()),
                new CosinusFloat(rightPoint.getRealX(), radius),
                loop ? ValueAnimator.Mode.REPEATED : ValueAnimator.Mode.SINGLE);
        ValueAnimator<Float> aY = new ValueAnimator<>(ms,
                y->getActor().position.setCenter(getActor().position.getCenter().getRealX(), y),
                new SinusFloat(rotationCenter.getRealY(), uhrzeigersinn ? -radius : radius),
                loop ? ValueAnimator.Mode.REPEATED : ValueAnimator.Mode.SINGLE);


        //Winkel zwischen gewünschtem Startpunkt und aktueller Actor-Position (immer in [0;PI])
        float angle = rotationCenter.vectorFromThisTo(rightPoint).getAngle(
                rotationCenter.vectorFromThisTo(currentActorCenter));

        if(uhrzeigersinn && currentActorCenter.getRealY() > rotationCenter.getRealY()
            || !uhrzeigersinn && currentActorCenter.getRealY() < rotationCenter.getRealY()) {
            //Gedrehter Winkel ist bereits über die Hälfte
            angle += Math.PI;
        }

        float actualProgress = (float) (angle/(Math.PI*2));
        aX.setProgress(actualProgress);
        aY.setProgress(actualProgress);

        Spiel.getActiveScene().addFrameUpdateListener(aX);
        Spiel.getActiveScene().addFrameUpdateListener(aY);

        if(rotation) {
            float rotationAngle = uhrzeigersinn ? angle : -angle;
            ValueAnimator<Float> aR = new ValueAnimator<>(ms,
                    getActor().position::setRotation,
                    new LinearFloat(-angle, -angle + ((float)(Math.PI*2) * (uhrzeigersinn ? -1 : 1))),
                    loop ? ValueAnimator.Mode.REPEATED : ValueAnimator.Mode.SINGLE);
            aR.setProgress(actualProgress);
            Spiel.getActiveScene().addFrameUpdateListener(aR);
        }
    }
}
