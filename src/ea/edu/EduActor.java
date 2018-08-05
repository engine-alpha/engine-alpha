package ea.edu;

import ea.Point;
import ea.Vector;
import ea.actor.Actor;
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
        getActor().physics.setGravity(new Vector(0, -1));
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
        getActor().physics.applyImpulse(new Vector(0, staerke*1000));
    }
}
