package ea.edu;

import ea.Game;
import ea.Vector;
import ea.actor.Actor;
import ea.animation.CircleAnimation;
import ea.animation.LineAnimation;
import ea.handle.Physics;
import ea.internal.annotations.Internal;

public interface EduActor {

    /**
     * Gibt das Engine-interne Actor-Objekt zurück.
     *
     * @return Das Core-Engine-Actor-Objekt
     */
    @Internal
    Actor getActor();

    /**
     * Standard-Ausführung im Konstruktor. Meldet das Objekt unmittelbar in der aktuell aktiven Szene an.
     */
    @Internal
    default void eduSetup() {
        Spiel.getActiveScene().add(getActor());

        //Default Physics Setup für EDU Objekte
        getActor().physics.setRotationLocked(true);
        getActor().physics.setElasticity(0);
    }

    default void entfernen() {
        Game.enqueue(() -> Spiel.getActiveScene().remove(getActor()));
    }

    default void verschieben(float dX, float dY) {
        Game.enqueue(() -> getActor().position.move(dX, dY));
    }

    default void drehen(float drehwinkelInWinkelgrad) {
        Game.enqueue(() -> getActor().position.rotate(drehwinkelInWinkelgrad));
    }

    default float nenneWinkel() {
        return getActor().position.getRotation();
    }

    default void setzeMittelpunkt(float mX, float mY) {
        Game.enqueue(() -> getActor().position.setCenter(mX, mY));
    }

    default void setzeSichtbar(boolean sichtbar) {
        Game.enqueue(() -> getActor().setVisible(sichtbar));
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
        return getActor().contains(new Vector(pX, pY));
    }

    default Vector mittelPunkt() {
        return getActor().position.getCenter();
    }

    default Vector zentrum() {
        return mittelPunkt();
    }

    default boolean schneidet(EduActor other) {
        return other.getActor().overlaps(getActor());
    }

    /* ~~~ PHYSICS ~~~ */

    default void setzeRotationBlockiert(boolean blockiert) {
        Game.enqueue(() -> getActor().physics.setRotationLocked(blockiert));
    }

    default void wirkeImpuls(float iX, float iY) {
        Game.enqueue(() -> getActor().physics.applyImpulse(new Vector(iX, iY)));
    }

    default void setzeReibung(float reibungsKoeffizient) {
        Game.enqueue(() -> getActor().physics.setFriction(reibungsKoeffizient));
    }

    default void setzeGeschwindigkeit(float vX, float vY) {
        Game.enqueue(() -> getActor().physics.setVelocity(new Vector(vX, vY)));
    }

    default void setzeElastizitaet(float elastizitaetsKoeffizient) {
        Game.enqueue(() -> getActor().physics.setElasticity(elastizitaetsKoeffizient));
    }

    default void setzeSchwerkraft(float schwerkraft) {
        Game.enqueue(() -> getActor().getScene().setGravity(new Vector(0, -schwerkraft)));
    }

    default void setzeMasse(float masse) {
        Game.enqueue(() -> getActor().physics.setMass(masse));
    }

    default float nenneVx() {
        return getActor().physics.getVelocity().x;
    }

    default float nenneVy() {
        return getActor().physics.getVelocity().y;
    }

    /* ~~~ JUMP N RUN WRAPPER ~~~ */

    default boolean steht() {
        return getActor().physics.testStanding();
    }

    default boolean stehtAuf(Actor actor) {
        return getActor().overlaps(actor) && getActor().physics.testStanding();
    }

    default void macheAktiv() {
        Game.enqueue(() -> getActor().setBodyType(Physics.Type.DYNAMIC));
    }

    default void machePassiv() {
        Game.enqueue(() -> getActor().setBodyType(Physics.Type.STATIC));
    }

    default void macheNeutral() {
        Game.enqueue(() -> getActor().setBodyType(Physics.Type.PASSIVE));
    }

    default void sprung(float staerke) {
        if (steht()) {
            getActor().physics.applyImpulse(new Vector(0, staerke * 1000));
        }
    }

    /**
     * Bewegt den Actor anhand einer Gerade.
     *
     * @param zX   X-Koordinate des Mittelpunkts des Actors nach <code>ms</code> Millisekunden
     * @param zY   Y-Koordinate des Mittelpunkts des Actors nach <code>ms</code> Millisekunden
     * @param ms   Zeit in Millisekunden, die der Actor von Beginn der Animation benötigt, bis er am angegebenen
     *             Endpunkt angekommen ist.
     * @param loop <code>true</code>: Der Actor "ping pongt" zwischen dem impliziten Startpunkt und dem angegebenen
     *             Endpunkt hin und her. Die Strecke in eine Richtung benötigt jeweils <code>ms</code> Millisekunden
     *             Zeit. <br>
     *             <code>false</code>: Die Animation endet automatisch, nachdem der Zielpunkt (das erste Mal) erreicht
     *             wurde.
     */
    default void geradenAnimation(float zX, float zY, int ms, boolean loop) {
        Game.enqueue(() -> Spiel.getActiveScene().addFrameUpdateListener(new LineAnimation(getActor(), new Vector(zX, zY), ms, loop)));
    }

    /**
     * Bewegt den Actor in einem Kreis um einen Angegebenen Mittelpunkt.
     *
     * @param mX            X-Koordinate des Mittelpunkts der Revolution.
     * @param mY            Y-Koordinate des Mittelpunkts der Revolution.
     * @param ms            Dauer in Millisekunden, die eine komplette Umdrehung benötigt.
     * @param uhrzeigersinn <code>true</code>= Drehung findet im Uhrzeigersinn statt. <code>false</code>: Gegen den
     *                      Uhrzeigersinn
     * @param rotation      <code>true</code>=Das Actor-Objekt wird auch entsprechend seiner Kreis-Position rotiert.
     *                      <code>false</code>=Das Actor-Objekt behält seine Rotation bei.
     */
    default void kreisAnimation(float mX, float mY, int ms, boolean uhrzeigersinn, boolean rotation) {
        Game.enqueue(() -> Spiel.getActiveScene().addFrameUpdateListener(new CircleAnimation(getActor(), new Vector(mX, mY), ms, uhrzeigersinn, rotation)));
    }
}
